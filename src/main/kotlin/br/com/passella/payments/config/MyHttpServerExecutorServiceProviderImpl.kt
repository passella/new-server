package br.com.passella.payments.config

import br.com.passella.httpserver.MyHttpServerExecutorServiceProvider
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MyHttpServerExecutorServiceProviderImpl : MyHttpServerExecutorServiceProvider {
    override fun getExecutorService(): ExecutorService = Executors.newVirtualThreadPerTaskExecutor()
}
