package br.com.passella.httpserver.adapter.input

import br.com.passella.httpserver.core.HttpHandler
import br.com.passella.httpserver.core.RequestPathHandler
import br.com.passella.httpserver.core.model.HttpRequest
import br.com.passella.httpserver.core.model.HttpResponse
import br.com.passella.httpserver.handler.DefaultErrorHandler
import br.com.passella.httpserver.handler.DefaultNotFoundHandler

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