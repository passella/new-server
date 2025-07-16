package br.com.passella.httpserver

import br.com.passella.fastlogger.FastLogger
import br.com.passella.httpserver.config.HttpServerConfiguration
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

// Interface que define o contrato para handlers HTTP
interface HttpHandler {
    fun handle(request: HttpRequest, response: HttpResponse)
}

// Classe que representa uma requisição HTTP
class HttpRequest(
    val method: String,
    val path: String,
    val headers: Map<String, String>,
    val body: String
)

// Classe que representa uma resposta HTTP
class HttpResponse(private val output: PrintWriter) {
    private var statusCode: Int = 200
    private var statusText: String = "OK"
    private val headers: MutableMap<String, String> = mutableMapOf("Content-Type" to "text/plain")
    private var body: String = ""

    fun status(code: Int, text: String = getStatusText(code)): HttpResponse {
        statusCode = code
        statusText = text
        return this
    }

    fun header(name: String, value: String): HttpResponse {
        headers[name] = value
        return this
    }

    fun body(content: String): HttpResponse {
        body = content
        return this
    }

    fun send() {
        headers["Content-Length"] = body.length.toString()

        val responseText = buildString {
            append("HTTP/1.1 $statusCode $statusText\r\n")
            headers.forEach { (name, value) ->
                append("$name: $value\r\n")
            }
            append("\r\n")
            append(body)
        }

        output.print(responseText)
        output.flush()
    }

    private fun getStatusText(statusCode: Int): String {
        return when (statusCode) {
            200 -> "OK"
            201 -> "Created"
            204 -> "No Content"
            400 -> "Bad Request"
            401 -> "Unauthorized"
            403 -> "Forbidden"
            404 -> "Not Found"
            500 -> "Internal Server Error"
            else -> "Unknown"
        }
    }
}

// Classe principal do servidor HTTP com API fluente
class MyHttpServer(private val configuration: HttpServerConfiguration) {
    private val executor: ExecutorService = Executors.newVirtualThreadPerTaskExecutor()
    private val routes: MutableMap<Pair<String, String>, HttpHandler> = mutableMapOf()
    private val pathPatternRoutes: MutableList<Triple<String, Regex, HttpHandler>> = mutableListOf()
    private var notFoundHandler: HttpHandler = DefaultNotFoundHandler()
    private var errorHandler: HttpHandler = DefaultErrorHandler()

    // API fluente para registrar handlers com caminhos exatos
    fun handler(method: String, path: String, handler: HttpHandler): MyHttpServer {
        routes[method.uppercase() to path] = handler
        return this
    }

    // API fluente para registrar handlers com padrões de caminho (usando regex)
    fun patternHandler(method: String, pathPattern: String, handler: HttpHandler): MyHttpServer {
        val regex = pathPattern.replace("*", ".*").toRegex()
        pathPatternRoutes.add(Triple(method.uppercase(), regex, handler))
        return this
    }

    // API fluente para definir handler de erro 404
    fun notFoundHandler(handler: HttpHandler): MyHttpServer {
        notFoundHandler = handler
        return this
    }

    // API fluente para definir handler de erro 500
    fun errorHandler(handler: HttpHandler): MyHttpServer {
        errorHandler = handler
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
                    executor.submit {
                        handleConnection(socket)
                    }
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

                // Parse da requisição HTTP
                val request = parseRequest(input)
                val response = HttpResponse(output)

                try {
                    // Encontra e executa o handler apropriado
                    val handler = findHandler(request.method, request.path)
                    handler.handle(request, response)
                } catch (e: Exception) {
                    logger.error(e) { "Erro ao processar requisição: ${request.method} ${request.path}" }
                    errorHandler.handle(request, response)
                }
            }
        } catch (e: Exception) {
            logger.error(e) { "Erro ao processar conexão" }
        }
    }

    private fun parseRequest(input: BufferedReader): HttpRequest {
        // Lê a primeira linha (método, path, versão)
        val requestLine = input.readLine() ?: ""
        val parts = requestLine.split(" ")

        val method = if (parts.size > 0) parts[0] else "GET"
        val path = if (parts.size > 1) parts[1] else "/"

        // Lê os headers
        val headers = mutableMapOf<String, String>()
        var line: String?
        while (input.readLine().also { line = it } != null && line!!.isNotEmpty()) {
            val headerParts = line!!.split(":", limit = 2)
            if (headerParts.size == 2) {
                headers[headerParts[0].trim()] = headerParts[1].trim()
            }
        }

        // Lê o corpo se houver
        val contentLength = headers["Content-Length"]?.toIntOrNull() ?: 0
        val body = if (contentLength > 0) {
            val buffer = CharArray(contentLength)
            input.read(buffer, 0, contentLength)
            String(buffer)
        } else {
            ""
        }

        logger.debug { "Requisição recebida: $method $path" }
        logger.debug { "Headers: $headers" }
        logger.debug { "Body: $body" }

        return HttpRequest(method, path, headers, body)
    }

    private fun findHandler(method: String, path: String): HttpHandler {
        // Primeiro tenta encontrar uma correspondência exata
        val exactMatch = routes[method.uppercase() to path]
        if (exactMatch != null) {
            return exactMatch
        }

        // Se não encontrar, tenta padrões de caminho
        for ((handlerMethod, pattern, handler) in pathPatternRoutes) {
            if (handlerMethod == method.uppercase() && pattern.matches(path)) {
                return handler
            }
        }

        // Se não encontrar nenhum handler, retorna o handler de 404
        return notFoundHandler
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

// Handler padrão para rotas não encontradas
class DefaultNotFoundHandler : HttpHandler {
    override fun handle(request: HttpRequest, response: HttpResponse) {
        response
            .status(404)
            .body("Rota não encontrada: ${request.method} ${request.path}")
            .send()
    }

    companion object {
        private val logger = FastLogger.getLogger(DefaultNotFoundHandler::class.java)
    }
}

// Handler padrão para erros internos
class DefaultErrorHandler : HttpHandler {
    override fun handle(request: HttpRequest, response: HttpResponse) {
        response
            .status(500)
            .body("Erro interno do servidor")
            .send()
    }

    companion object {
        private val logger = FastLogger.getLogger(DefaultErrorHandler::class.java)
    }
}

// Handler para a rota raiz
class RootHandler : HttpHandler {
    override fun handle(request: HttpRequest, response: HttpResponse) {
        response
            .status(200)
            .body("Servidor HTTP funcionando!")
            .send()
    }

    companion object {
        private val logger = FastLogger.getLogger(RootHandler::class.java)
    }
}

// Handler para a rota de pagamentos
class PaymentsHandler : HttpHandler {
    override fun handle(request: HttpRequest, response: HttpResponse) {
        when (request.method.uppercase()) {
            "POST" -> handlePostPayment(request, response)
            "GET" -> handleGetPayment(request, response)
            else -> {
                response
                    .status(405)
                    .body("Método não permitido")
                    .send()
            }
        }
    }

    private fun handlePostPayment(request: HttpRequest, response: HttpResponse) {
        // Aqui você implementaria a lógica real de processamento do pagamento
        logger.info { "Processando pagamento: ${request.body}" }

        response
            .status(200)
            .header("Content-Type", "application/json")
            .body("""{"status":"ACCEPTED","id":"payment-123"}""")
            .send()
    }

    private fun handleGetPayment(request: HttpRequest, response: HttpResponse) {
        response
            .status(200)
            .header("Content-Type", "application/json")
            .body("""{"payments":[]}""")
            .send()
    }

    companion object {
        private val logger = FastLogger.getLogger(PaymentsHandler::class.java)
    }
}

// Handler para a rota de eco
class EchoHandler : HttpHandler {
    override fun handle(request: HttpRequest, response: HttpResponse) {
        response
            .status(200)
            .header("Content-Type", "application/json")
            .body("""{"method":"${request.method}","path":"${request.path}","body":"${request.body}"}""")
            .send()
    }

    companion object {
        private val logger = FastLogger.getLogger(EchoHandler::class.java)
    }
}