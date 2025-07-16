package br.com.passella.httpserver.core

import br.com.passella.httpserver.core.model.HttpRequest

interface Handler {
    fun handle(request: HttpRequest)
}