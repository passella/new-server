package br.com.passella.httpserver.handler

import br.com.passella.fastlogger.FastLogger
import br.com.passella.httpserver.core.HandlerRegistry
import br.com.passella.httpserver.core.HttpResponseWriter
import java.io.IOException
import java.net.ServerSocket
import java.net.SocketException
import java.util.concurrent.Executor

class ConnectionHandler(
    private val executor: Executor,
    responseWriter: HttpResponseWriter,
    handlerRegistry: HandlerRegistry
) {
    private val notFoundHandler = NotFoundHandlerFactory.create(responseWriter)
    private val requestHandler = RequestHandler(responseWriter, handlerRegistry, notFoundHandler)


    fun handleConnections(serverSocket: ServerSocket) {
        while (!serverSocket.isClosed) {
            try {
                val clientSocket = serverSocket.accept()
                logger.debug { "Nova conexão aceita: ${clientSocket.inetAddress}" }
                executor.execute { requestHandler.handleRequest(clientSocket) }
            } catch (e: SocketException) {
                if (!serverSocket.isClosed) {
                    logger.warn { "Conexão interrompida: ${e.message}" }
                }
            } catch (e: IOException) {
                if (!serverSocket.isClosed) {
                    logger.error(e) { "Erro de I/O ao aceitar conexão" }
                }
            } catch (e: Exception) {
                if (!serverSocket.isClosed) {
                    logger.error(e) { "Erro inesperado ao processar conexão" }
                }
            }
        }
    }

    companion object {
        private val logger = FastLogger.getLogger(ConnectionHandler::class.java)
    }
}