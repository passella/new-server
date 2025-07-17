package br.com.passella.httpserver

interface HttpHandler {
    fun handle(
        request: HttpRequest,
        response: HttpResponse,
    )
}
