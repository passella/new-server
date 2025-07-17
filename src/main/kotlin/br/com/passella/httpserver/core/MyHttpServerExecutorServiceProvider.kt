package br.com.passella.httpserver.core

import java.util.concurrent.ExecutorService

fun interface MyHttpServerExecutorServiceProvider {
    fun getExecutorService(): ExecutorService
}
