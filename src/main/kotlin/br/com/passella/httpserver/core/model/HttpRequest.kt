package br.com.passella.httpserver.core.model

data class HttpRequest(
    val method: String,
    val path: String,
    val queryString: String = "",
    val headers: Map<String, String> = emptyMap(),
    val body: String = "",
)
