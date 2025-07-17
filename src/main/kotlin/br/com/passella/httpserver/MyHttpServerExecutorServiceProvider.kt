package br.com.passella.httpserver

import java.util.concurrent.ExecutorService

interface MyHttpServerExecutorServiceProvider {
    fun getExecutorService(): ExecutorService
}
