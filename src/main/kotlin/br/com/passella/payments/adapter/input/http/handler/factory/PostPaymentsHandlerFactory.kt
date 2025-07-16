package br.com.passella.payments.adapter.input.http.handler.factory

import br.com.passella.httpserver.config.HttpResponseWriterFactory
import br.com.passella.httpserver.core.Handler
import br.com.passella.jsonparser.config.MyJsonParserFactory
import br.com.passella.payments.adapter.input.http.handler.PostPaymentsHandler

object PostPaymentsHandlerFactory {

    fun create(): Handler {
        val responseWriter = HttpResponseWriterFactory.create()
        val jsonParser = MyJsonParserFactory.create()
        return PostPaymentsHandler(responseWriter, jsonParser)
    }
}