package br.com.passella.httpserver.core

import br.com.passella.httpserver.core.model.HttpRequest
import br.com.passella.httpserver.core.model.HttpResponse

interface HttpHandler {
    fun handle(
        request: HttpRequest,
        response: HttpResponse,
    )
}