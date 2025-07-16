package br.com.passella.httpserver.config

import br.com.passella.httpserver.MyHttpServer
import br.com.passella.httpserver.PaymentsHandler
import br.com.passella.httpserver.core.HandlerRegistry

object HttpServerFactory {

    fun createServer(
        handlerRegistry: HandlerRegistry,
        serverConfiguration: HttpServerConfiguration
    ): MyHttpServer {
        val responseWriter = HttpResponseWriterFactory.create()
        return MyHttpServer(serverConfiguration)
            .handler("POST", "/payments", PaymentsHandler())
    }
}