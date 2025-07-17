package br.com.passella.httpserver

class RequestPathHandlerImpl : RequestPathHandler {
    private val routes: MutableMap<Pair<String, String>, HttpHandler> = mutableMapOf()
    private var notFoundHandler: HttpHandler = DefaultNotFoundHandler()
    private var errorHandler: HttpHandler = DefaultErrorHandler()

    override fun handler(
        method: String,
        path: String,
        handler: HttpHandler,
    ) {
        routes[method.uppercase() to path] = handler
    }

    override fun findHandler(
        method: String,
        path: String,
    ): HttpHandler {
        val exactMatch = routes[method.uppercase() to path]
        if (exactMatch != null) {
            return exactMatch
        }

        return notFoundHandler
    }

    override fun notFoundHandler(handler: HttpHandler) {
        this.notFoundHandler = handler
    }

    override fun errorHandler(handler: HttpHandler) {
        this.errorHandler = handler
    }

    override fun handleError(
        request: HttpRequest,
        response: HttpResponse,
    ) {
        errorHandler.handle(request, response)
    }
}
