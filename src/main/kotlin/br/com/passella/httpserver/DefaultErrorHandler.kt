package br.com.passella.httpserver

class DefaultErrorHandler : HttpHandler {
    override fun handle(
        request: HttpRequest,
        response: HttpResponse,
    ) {
        response
            .status(500)
            .body("Erro interno do servidor")
            .send()
    }
}
