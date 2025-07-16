package br.com.passella.httpserver.adapter.output

import br.com.passella.fastlogger.FastLogger
import br.com.passella.httpserver.core.HttpResponseWriter
import br.com.passella.httpserver.core.HttpStatusCode
import java.io.BufferedOutputStream
import java.io.IOException
import java.net.Socket
import java.nio.BufferOverflowException
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentHashMap

class HttpResponseWriterImpl : HttpResponseWriter {
    private val statusLineCache = ConcurrentHashMap<Int, ByteArray>()
    private val bufferPool = BufferPool()

    private companion object {
        private val logger = FastLogger.getLogger(HttpResponseWriterImpl::class.java)
        private val CRLF = "\r\n".toByteArray(StandardCharsets.UTF_8)
        private val SERVER_HEADER = "Server: MyHttpServer/1.0".toByteArray(StandardCharsets.UTF_8)
        private val CONNECTION_HEADER = "Connection: close".toByteArray(StandardCharsets.UTF_8)
        private val CONTENT_TYPE_PREFIX = "Content-Type: ".toByteArray(StandardCharsets.UTF_8)
        private val CHARSET_SUFFIX = "; charset=utf-8".toByteArray(StandardCharsets.UTF_8)
        private val CONTENT_LENGTH_PREFIX = "Content-Length: ".toByteArray(StandardCharsets.UTF_8)
        private val DATE_PREFIX = "Date: ".toByteArray(StandardCharsets.UTF_8)
        private val GMT_ZONE = ZoneId.of("GMT")
        private val DATE_FORMATTER = DateTimeFormatter.RFC_1123_DATE_TIME
        private const val BUFFER_OVERHEAD_BYTES = 512
    }

    override fun writeResponse(
        socket: Socket,
        content: String,
        statusCode: Int,
        contentType: String,
        headers: Map<String, String>
    ) {
        var buffer: ByteBuffer? = null

        try {
            val contentBytes = content.toByteArray(StandardCharsets.UTF_8)
            val estimatedSize = contentBytes.size + BUFFER_OVERHEAD_BYTES

            buffer = bufferPool.acquire(estimatedSize)
            buildAndSendHttpResponse(buffer, statusCode, contentType, contentBytes, headers, socket)
            logger.debug { "Resposta enviada: ${contentBytes.size} bytes, status $statusCode" }
        } catch (e: IOException) {
            logger.error(e) { "Erro ao enviar resposta HTTP" }
        } catch (e: BufferOverflowException) {
            logger.error(e) {
                "Buffer insuficiente para a resposta HTTP (tamanho necessário: " +
                        "${content.length + BUFFER_OVERHEAD_BYTES} bytes)"
            }

            if (buffer != null) {
                try {
                    val newSize = content.length + BUFFER_OVERHEAD_BYTES * 2
                    logger.info { "Tentando novamente com buffer maior: $newSize bytes" }
                    writeResponseWithLargerBuffer(socket, content, statusCode, contentType, headers, newSize)
                } catch (e2: Exception) {
                    logger.error(e2) { "Falha na segunda tentativa de enviar resposta" }
                }
            }
        } catch (e: IllegalArgumentException) {
            logger.error(e) { "Argumento inválido ao construir resposta HTTP" }
        } catch (e: SecurityException) {
            logger.error(e) { "Erro de segurança ao enviar resposta HTTP" }
        } catch (e: Exception) {
            logger.error(e) { "Erro inesperado ao enviar resposta HTTP" }
        } finally {
            if (buffer != null) {
                bufferPool.release(buffer)
            }
        }
    }

    @SuppressWarnings("LongParameterList")
    private fun writeResponseWithLargerBuffer(
        socket: Socket,
        content: String,
        statusCode: Int,
        contentType: String,
        headers: Map<String, String>,
        bufferSize: Int
    ) {
        val buffer = ByteBuffer.allocate(bufferSize)
        val contentBytes = content.toByteArray(StandardCharsets.UTF_8)
        buildAndSendHttpResponse(buffer, statusCode, contentType, contentBytes, headers, socket)
    }

    @SuppressWarnings("LongParameterList")
    private fun buildAndSendHttpResponse(
        buffer: ByteBuffer,
        statusCode: Int,
        contentType: String,
        contentBytes: ByteArray,
        headers: Map<String, String>,
        socket: Socket
    ) {
        buffer.put(getStatusLineBytes(statusCode))
        buffer.put(CRLF)

        appendContentTypeHeader(buffer, contentType)
        appendContentLengthHeader(buffer, contentBytes.size)
        appendDateHeader(buffer)
        buffer.put(SERVER_HEADER)
        buffer.put(CRLF)
        buffer.put(CONNECTION_HEADER)
        buffer.put(CRLF)

        headers.forEach { (key, value) ->
            buffer.put("$key: $value".toByteArray(StandardCharsets.UTF_8))
            buffer.put(CRLF)
        }

        buffer.put(CRLF)
        buffer.put(contentBytes)

        val outputStream = BufferedOutputStream(socket.getOutputStream())
        outputStream.write(buffer.array(), 0, buffer.position())
        outputStream.flush()
    }

    private fun getStatusLineBytes(statusCode: Int): ByteArray {
        return statusLineCache.computeIfAbsent(statusCode) {
            val statusText = HttpStatusCode.getStatusText(statusCode)
            "HTTP/1.1 $statusCode $statusText".toByteArray(StandardCharsets.UTF_8)
        }
    }

    private fun appendContentTypeHeader(buffer: ByteBuffer, contentType: String) {
        buffer.put(CONTENT_TYPE_PREFIX)
        buffer.put(contentType.toByteArray(StandardCharsets.UTF_8))
        buffer.put(CHARSET_SUFFIX)
        buffer.put(CRLF)
    }

    private fun appendContentLengthHeader(buffer: ByteBuffer, length: Int) {
        buffer.put(CONTENT_LENGTH_PREFIX)
        buffer.put(length.toString().toByteArray(StandardCharsets.UTF_8))
        buffer.put(CRLF)
    }

    private fun appendDateHeader(buffer: ByteBuffer) {
        buffer.put(DATE_PREFIX)
        val now = ZonedDateTime.now(GMT_ZONE).format(DATE_FORMATTER)
        buffer.put(now.toByteArray(StandardCharsets.UTF_8))
        buffer.put(CRLF)
    }
}