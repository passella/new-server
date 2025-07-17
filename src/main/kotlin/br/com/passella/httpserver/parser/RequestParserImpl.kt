package br.com.passella.httpserver.parser

import br.com.passella.fastlogger.FastLogger
import br.com.passella.httpserver.core.RequestParser
import br.com.passella.httpserver.core.model.HttpRequest
import java.io.BufferedReader
import java.net.Socket

class RequestParserImpl : RequestParser {
    companion object {
        private val logger = FastLogger.getLogger(RequestParserImpl::class.java)
    }

    override fun parseRequest(input: BufferedReader): HttpRequest {
        val requestLine = input.readLine() ?: ""
        val parts = requestLine.split(" ")

        val method = if (parts.isNotEmpty()) parts[0] else "GET"
        val path = if (parts.size > 1) parts[1] else "/"

        val headers = mutableMapOf<String, String>()
        var line: String?
        while (input.readLine().also { line = it } != null && line!!.isNotEmpty()) {
            val headerParts = line!!.split(":", limit = 2)
            if (headerParts.size == 2) {
                headers[headerParts[0].trim()] = headerParts[1].trim()
            }
        }

        val contentLength = headers["Content-Length"]?.toIntOrNull() ?: 0
        val body =
            if (contentLength > 0) {
                val buffer = CharArray(contentLength)
                input.read(buffer, 0, contentLength)
                String(buffer)
            } else {
                ""
            }

        logger.debug { "Requisição recebida: $method $path" }
        logger.debug { "Headers: $headers" }
        logger.debug { "Body: $body" }

        // Note: This is a temporary solution as we need a Socket instance
        // In a real implementation, the Socket should be passed to this method
        return HttpRequest(method, path, "", headers, body, Socket())
    }
}