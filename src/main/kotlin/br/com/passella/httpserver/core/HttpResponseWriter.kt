package br.com.passella.httpserver.core

import java.net.Socket

interface HttpResponseWriter {

    fun writeResponse(
        socket: Socket,
        content: String,
        statusCode: Int = HttpStatusCode.OK.code,
        contentType: String = "text/plain",
        headers: Map<String, String> = emptyMap()
    )
    
    fun writeResponse(
        socket: Socket,
        content: String,
        statusCode: HttpStatusCode,
        contentType: String = "text/plain",
        headers: Map<String, String> = emptyMap()
    ) {
        writeResponse(socket, content, statusCode.code, contentType, headers)
    }
}