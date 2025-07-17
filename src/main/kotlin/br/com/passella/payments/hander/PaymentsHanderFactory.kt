package br.com.passella.payments.hander

import br.com.passella.httpserver.core.HttpHandler

object PaymentsHanderFactory {
    fun create(): HttpHandler = PaymentsHandler()
}
