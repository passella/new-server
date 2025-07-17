package br.com.passella.httpserver.core

import java.util.concurrent.ExecutorService

interface MyHttpServerExecutorServiceProvider {
    fun getExecutorService(): ExecutorService
}