package br.com.passella.httpserver

class DefaultNotFoundHandler : HttpHandler {
    override fun handle(
        request: HttpRequest,
        response: HttpResponse,
    ) {
        response
            .status(404)
            .body("Rota não encontrada: ${request.method} ${request.path}")
            .send()
    }
}
