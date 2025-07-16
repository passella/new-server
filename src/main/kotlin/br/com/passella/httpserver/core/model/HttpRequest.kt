package br.com.passella.httpserver.core.model

import java.net.Socket


data class HttpRequest(
    val method: String,
    val path: String,
    val queryString: String = "",
    val headers: Map<String, String> = emptyMap(),
    val body: String = "",
    val socket: Socket
)