package br.com.passella.httpserver.handler

import br.com.passella.fastlogger.FastLogger
import br.com.passella.httpserver.core.Handler
import br.com.passella.httpserver.core.HttpResponseWriter
import br.com.passella.httpserver.core.HttpStatusCode
import br.com.passella.httpserver.core.model.HttpRequest

class NotFoundHandler(private val responseWriter: HttpResponseWriter) : Handler {
    
    override fun handle(request: HttpRequest) {
        logger.debug { "Rota não encontrada" }
        responseWriter.writeResponse(request.socket, "Not Found", HttpStatusCode.NOT_FOUND)
    }
    
    companion object {
        private val logger = FastLogger.getLogger(NotFoundHandler::class.java)
    }
}