package br.com.passella.payments.config

import br.com.passella.httpserver.MyHttpServerExecutorServiceProvider

object ExecutorServiceProviderFactory {
    fun getExecutorServiceProvider(): MyHttpServerExecutorServiceProvider = MyHttpServerExecutorServiceProviderImpl()
}
