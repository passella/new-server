package br.com.passella.httpserver

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
