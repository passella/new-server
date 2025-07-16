package br.com.passella.payments.config

import br.com.passella.httpserver.adapter.input.DefaultHandlerRegistry
import br.com.passella.httpserver.core.HandlerRegistry
import br.com.passella.payments.adapter.input.http.handler.factory.PostPaymentsHandlerFactory

object HandlerRegistryConfiguration {
    fun createHandlerRegistry(): HandlerRegistry {
        return DefaultHandlerRegistry().apply {
            register("POST", "/payments", PostPaymentsHandlerFactory.create())
        }
    }
}