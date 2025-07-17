package br.com.passella.httpserver

import br.com.passella.fastlogger.FastLogger
import br.com.passella.httpserver.config.HttpServerConfiguration
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit

class MyHttpServer(
    private val configuration: HttpServerConfiguration,
    myHttpServerExecutorServiceProvider: MyHttpServerExecutorServiceProvider,
    private val requestPathHandler: RequestPathHandler,
    private val requestParser: RequestParser,
) {
    private val executor: ExecutorService = myHttpServerExecutorServiceProvider.getExecutorService()

    fun handler(
        method: String,
        path: String,
        handler: HttpHandler,
    ): MyHttpServer {
        requestPathHandler.handler(method, path, handler)
        return this
    }

    fun notFoundHandler(handler: HttpHandler): MyHttpServer {
        requestPathHandler.notFoundHandler(handler)
        return this
    }

    fun errorHandler(handler: HttpHandler): MyHttpServer {
        requestPathHandler.errorHandler(handler)
        return this
    }

    fun start() {
        val port = configuration.port
        logger.info { "Iniciando servidor na porta $port com Java ${System.getProperty("java.version")}" }

        try {
            ServerSocket(port).use { serverSocket ->
                logger.info { "Servidor iniciado e aguardando conexões" }
                while (true) {
                    val socket = serverSocket.accept()
                    executor.submit { handleConnection(socket) }
                }
            }
        } finally {
            shutdownExecutor()
        }
    }

    private fun handleConnection(socket: Socket) {
        try {
            socket.use { clientSocket ->
                val input = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
                val output = PrintWriter(clientSocket.getOutputStream(), true)

                val request: HttpRequest = requestParser.parseRequest(input)
                val response = HttpResponse(output)

                try {
                    val handler = requestPathHandler.findHandler(request.method, request.path)
                    handler.handle(request, response)
                } catch (e: Exception) {
                    logger.error(e) { "Erro ao processar requisição: ${request.method} ${request.path}" }
                    requestPathHandler.handleError(request, response)
                }
            }
        } catch (e: Exception) {
            logger.error(e) { "Erro ao processar conexão" }
        }
    }

    private fun shutdownExecutor() {
        executor.shutdown()
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow()
            }
        } catch (_: InterruptedException) {
            executor.shutdownNow()
            Thread.currentThread().interrupt()
        }
    }

    companion object {
        private val logger = FastLogger.getLogger(MyHttpServer::class.java)
    }
}
