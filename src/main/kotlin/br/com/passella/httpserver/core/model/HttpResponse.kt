package br.com.passella.httpserver.core.model

import java.io.BufferedOutputStream
import java.nio.charset.StandardCharsets

class HttpResponse(
    private val output: BufferedOutputStream,
) {
    private var statusCode: Int = 200
    private var statusText: String = "OK"
    private val headers: MutableMap<String, String> = mutableMapOf("Content-Type" to "text/plain")
    private var body: String = ""

    fun status(
        code: Int,
        text: String = getStatusText(code),
    ): HttpResponse {
        statusCode = code
        statusText = text
        return this
    }

    fun header(
        name: String,
        value: String,
    ): HttpResponse {
        headers[name] = value
        return this
    }

    fun body(content: String): HttpResponse {
        body = content
        return this
    }

    fun send() {
        headers["Content-Length"] = body.length.toString()

        val responseText =
            buildString {
                append("HTTP/1.1 $statusCode $statusText\r\n")
                headers.forEach { (name, value) ->
                    append("$name: $value\r\n")
                }
                append("\r\n")
                append(body)
            }

        val responseBytes = responseText.toByteArray(StandardCharsets.UTF_8)
        output.write(responseBytes)
    }

    fun flush() {
        output.flush()
    }

    private fun getStatusText(statusCode: Int): String =
        when (statusCode) {
            200 -> "OK"
            201 -> "Created"
            204 -> "No Content"
            400 -> "Bad Request"
            401 -> "Unauthorized"
            403 -> "Forbidden"
            404 -> "Not Found"
            500 -> "Internal Server Error"
            else -> "Unknown"
        }
}
