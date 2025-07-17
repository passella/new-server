package br.com.passella.payments.config

import br.com.passella.httpserver.core.MyHttpServerExecutorServiceProvider
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MyHttpServerExecutorServiceProviderImpl : MyHttpServerExecutorServiceProvider {
    private val executor: ExecutorService by lazy { Executors.newVirtualThreadPerTaskExecutor() }

    override fun getExecutorService(): ExecutorService = executor
}
