package br.com.passella.httpserver.handler

import br.com.passella.httpserver.core.HttpHandler
import br.com.passella.httpserver.core.model.HttpRequest
import br.com.passella.httpserver.core.model.HttpResponse

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