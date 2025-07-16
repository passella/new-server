package br.com.passella.httpserver.parser

import br.com.passella.fastlogger.FastLogger
import br.com.passella.httpserver.core.model.HttpRequest
import br.com.passella.httpserver.exception.EmptyRequestException
import br.com.passella.httpserver.exception.HeaderProcessingException
import br.com.passella.httpserver.exception.MalformedRequestException
import br.com.passella.httpserver.exception.RequestBodyProcessingException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.Socket
import java.nio.charset.StandardCharsets

class HttpRequestParser {

    fun parseRequest(socket: Socket): HttpRequest {
        return try {
            val inputStream = socket.getInputStream()
            parseRequestFromInputStream(inputStream, socket)
        } catch (e: IOException) {
            throw RequestBodyProcessingException("Erro de I/O ao ler requisição", e)
        }
    }

    private fun parseRequestFromInputStream(inputStream: InputStream, socket: Socket): HttpRequest {
        val reader = BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8))
        val firstLine = reader.readLine() ?: throw EmptyRequestException()

        val parts = firstLine.split(" ")
        if (parts.size < 2) {
            throw MalformedRequestException("Formato inválido da linha de requisição: $firstLine")
        }

        val method = parts[0]
        val fullPath = parts[1]

        val pathParts = fullPath.split("?", limit = 2)
        val path = pathParts[0]
        val queryString = if (pathParts.size > 1) pathParts[1] else ""

        val headers = parseHeaders(reader)

        val contentLength = headers["Content-Length"]?.toIntOrNull() ?: 0
        val body = if (contentLength > 0) {
            // Usar o próprio BufferedReader para ler o corpo
            readBodyFromReader(reader, contentLength)
        } else ""

        logger.debug { "Requisição parseada: $method $path (Content-Length: $contentLength)" }

        return HttpRequest(method, path, queryString, headers, body, socket)
    }

    private fun parseHeaders(reader: BufferedReader): Map<String, String> {
        val headers = HashMap<String, String>(16)
        var line: String?

        while (true) {
            line = reader.readLine()

            if (line.isNullOrEmpty()) {
                break
            }

            val colonIndex = line.indexOf(':')
            if (colonIndex > 0) {
                val key = line.substring(0, colonIndex).trim()
                val value = line.substring(colonIndex + 1).trim()
                headers[key] = value
            } else {
                throw HeaderProcessingException("Formato de cabeçalho inválido: $line")
            }
        }

        return headers
    }

    /**
     * Lê o corpo da requisição diretamente do BufferedReader, garantindo que
     * lemos exatamente o número de caracteres especificado pelo Content-Length.
     */
    private fun readBodyFromReader(reader: BufferedReader, contentLength: Int): String {
        if (contentLength <= 0) {
            return ""
        }

        // Usar um StringBuilder pré-alocado para evitar realocações
        val bodyBuilder = StringBuilder(contentLength)

        // Ler caracteres diretamente do reader
        val buffer = CharArray(8192)
        var totalCharsRead = 0
        var charsRead: Int

        try {
            while (totalCharsRead < contentLength) {
                val remaining = contentLength - totalCharsRead
                val toRead = minOf(buffer.size, remaining)

                charsRead = reader.read(buffer, 0, toRead)
                if (charsRead == -1) {
                    break // EOF
                }

                bodyBuilder.append(buffer, 0, charsRead)
                totalCharsRead += charsRead
            }
        } catch (e: IOException) {
            logger.error(e) { "Erro ao ler corpo da requisição" }
            throw RequestBodyProcessingException("Erro ao ler corpo da requisição", e)
        }

        return bodyBuilder.toString()
    }

    companion object {
        private val logger = FastLogger.getLogger(HttpRequestParser::class.java)
    }
}