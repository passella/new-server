package br.com.passella.httpserver.core

import br.com.passella.httpserver.core.model.HttpRequest
import br.com.passella.httpserver.core.model.HttpResponse

interface RequestPathHandler {
    fun handler(
        method: String,
        path: String,
        handler: HttpHandler,
    )

    fun findHandler(
        method: String,
        path: String,
    ): HttpHandler

    fun notFoundHandler(handler: HttpHandler)

    fun errorHandler(handler: HttpHandler)

    fun handleError(
        request: HttpRequest,
        response: HttpResponse,
    )
}