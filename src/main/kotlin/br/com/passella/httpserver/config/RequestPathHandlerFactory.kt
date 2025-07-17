package br.com.passella.httpserver.config

import br.com.passella.httpserver.adapter.input.RequestPathHandlerImpl
import br.com.passella.httpserver.core.RequestPathHandler

object RequestPathHandlerFactory {
    fun create(): RequestPathHandler = RequestPathHandlerImpl()
}