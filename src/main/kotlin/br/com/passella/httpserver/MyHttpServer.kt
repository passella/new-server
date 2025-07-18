package br.com.passella.httpserver

import br.com.passella.fastlogger.FastLogger
import br.com.passella.httpserver.config.HttpServerConfiguration
import br.com.passella.httpserver.core.HttpHandler
import br.com.passella.httpserver.core.MyHttpServerExecutorServiceProvider
import br.com.passella.httpserver.core.RequestParser
import br.com.passella.httpserver.core.RequestPathHandler
import br.com.passella.httpserver.core.model.HttpResponse
import br.com.passella.httpserver.system.SystemInfoProvider
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.ExecutorService
import java.util.concurrent.TimeUnit

class MyHttpServer(
    private val configuration: HttpServerConfiguration,
    myHttpServerExecutorServiceProvider: MyHttpServerExecutorServiceProvider,
    private val requestPathHandler: RequestPathHandler,
    private val requestParser: RequestParser,
    private val systemInfoProvider: SystemInfoProvider,
) {
    private val executor: ExecutorService = myHttpServerExecutorServiceProvider.getExecutorService()
    private var serverSocket: ServerSocket? = null

    fun handler(
        method: String,
        path: String,
        handler: HttpHandler,
    ): MyHttpServer {
        requestPathHandler.handler(method, path, handler)
        return this
    }

    fun start() {
        val port = configuration.port
        logSystemInfo(port)

        try {
            serverSocket =
                ServerSocket(port, configuration.backlog).apply {
                    soTimeout = configuration.socketTimeoutMs
                    reuseAddress = configuration.reuseAddress
                    setReceiveBufferSize(configuration.receiveBufferSize)
                }
            logger.info { "Servidor iniciado e aguardando conexões" }
            while (true) {
                val socket = serverSocket?.accept() ?: break
                executor.submit { handleConnection(socket) }
            }
        } finally {
            serverSocket?.close()
            shutdownExecutor()
        }
    }

    private fun logSystemInfo(port: Int) {
        val systemInfo = systemInfoProvider.getSystemInfo(port)
        systemInfo.toString().lines().forEach { line ->
            if (line.isNotEmpty()) {
                logger.info { line }
            }
        }
    }

    private fun handleConnection(socket: Socket) {
        try {
            socket.use { clientSocket ->
                clientSocket.soTimeout = configuration.socketTimeoutMs
                clientSocket.setReceiveBufferSize(configuration.receiveBufferSize)
                val input = BufferedReader(InputStreamReader(clientSocket.getInputStream()))
                val output = BufferedOutputStream(clientSocket.getOutputStream(), 8192)

                val request = requestParser.parseRequest(input)
                val response = HttpResponse(output)

                try {
                    val handler = requestPathHandler.findHandler(request.method, request.path)
                    handler.handle(request, response)
                } catch (e: Exception) {
                    logger.error(e) { "Erro ao processar requisição: ${request.method} ${request.path}" }
                    requestPathHandler.handleError(request, response)
                } finally {
                    response.flush()
                }
            }
        } catch (e: IOException) {
            logger.error(e) { "Erro de I/O ao processar conexão" }
        } catch (e: Exception) {
            logger.error(e) { "Erro inesperado ao processar conexão" }
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