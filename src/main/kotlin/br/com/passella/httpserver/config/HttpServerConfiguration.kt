package br.com.passella.httpserver.config

data class HttpServerConfiguration(
    val port: Int,
    val socketTimeoutMs: Int = 0,
    val receiveBufferSize: Int = 65536,
    val backlog: Int = 500,
    val reuseAddress: Boolean = true
)