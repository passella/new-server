package br.com.passella.payments.config

import br.com.passella.httpserver.MyHttpServer
import br.com.passella.httpserver.config.RequestParserFactory
import br.com.passella.httpserver.config.RequestPathHandlerFactory
import br.com.passella.payments.hander.PaymentsHanderFactory

object HttpServerFactory {
    fun createServer(): MyHttpServer {
        val serverConfiguration = HttpServerConfigurationFactory.createConfiguration()
        val executorServiceProvider = ExecutorServiceProviderFactory.getExecutorServiceProvider()
        val pathHandler = RequestPathHandlerFactory.create()
        val requestParser = RequestParserFactory.createRequestParser()
        return MyHttpServer(serverConfiguration, executorServiceProvider, pathHandler, requestParser)
            .handler("POST", "/payments", PaymentsHanderFactory.create())
    }
}