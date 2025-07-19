package br.com.passella.payments.hander

import br.com.passella.httpserver.core.HttpHandler
import br.com.passella.jsonparser.config.MyJsonParserFactory

object PaymentsHanderFactory {
    fun create(): HttpHandler {
        val jsonParser = MyJsonParserFactory.create()
        return PaymentsHandler(jsonParser)
    }
}
