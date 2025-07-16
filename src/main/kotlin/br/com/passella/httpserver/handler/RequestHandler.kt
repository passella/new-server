package br.com.passella.httpserver.handler

import br.com.passella.fastlogger.FastLogger
import br.com.passella.httpserver.core.Handler
import br.com.passella.httpserver.core.HandlerRegistry
import br.com.passella.httpserver.core.HttpResponseWriter
import br.com.passella.httpserver.exception.HttpServerException
import br.com.passella.httpserver.parser.HttpRequestParser
import java.io.IOException
import java.net.Socket

class RequestHandler(
    private val responseWriter: HttpResponseWriter,
    private val handlerRegistry: HandlerRegistry,
    private val notFoundHandler: Handler
) {
    private val httpRequestParser = HttpRequestParser()

    fun handleRequest(socket: Socket) {
        socket.use { s ->
            try {
                val request = httpRequestParser.parseRequest(s)
                logger.debug { "Requisição recebida: ${request.method} ${request.path}" }

                val handler = handlerRegistry.getHandler(request.method, request.path) ?: notFoundHandler
                handler.handle(request)
            } catch (e: HttpServerException) {
                logger.error { "Erro ao processar requisição: ${e.message}" }
                responseWriter.writeResponse(socket, "Erro interno: ${e.message}", INTERNAL_ERROR_STATUS_CODE)
            } catch (e: IOException) {
                logger.error(e) { "Erro de I/O ao processar requisição" }
                responseWriter.writeResponse(socket, "Erro de I/O", INTERNAL_ERROR_STATUS_CODE)
            } catch (e: SecurityException) {
                logger.error(e) { "Erro de segurança ao processar requisição" }
                responseWriter.writeResponse(socket, "Erro de segurança", INTERNAL_ERROR_STATUS_CODE)
            } catch (e: Throwable) {
                logger.error(e) { "Erro inesperado ao processar requisição" }
                responseWriter.writeResponse(
                    socket,
                    "Erro interno do servidor! {${e.message}}", INTERNAL_ERROR_STATUS_CODE
                )
            }
        }
    }

    companion object {
        private val logger = FastLogger.getLogger(RequestHandler::class.java)
        private const val INTERNAL_ERROR_STATUS_CODE = 500
    }
}