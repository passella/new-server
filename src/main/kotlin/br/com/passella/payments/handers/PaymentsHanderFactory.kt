package br.com.passella.payments.handers

import br.com.passella.httpserver.HttpHandler

object PaymentsHanderFactory {
    fun create(): HttpHandler = PaymentsHandler()
}
