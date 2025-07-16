package br.com.passella.httpserver.handler

import br.com.passella.httpserver.core.Handler
import br.com.passella.httpserver.core.HttpResponseWriter

object NotFoundHandlerFactory {
    
    fun create(responseWriter: HttpResponseWriter): Handler {
        return NotFoundHandler(responseWriter)
    }
}