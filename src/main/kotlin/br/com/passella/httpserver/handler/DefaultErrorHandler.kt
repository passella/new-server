package br.com.passella.httpserver.handler

import br.com.passella.httpserver.core.HttpHandler
import br.com.passella.httpserver.core.model.HttpRequest
import br.com.passella.httpserver.core.model.HttpResponse

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