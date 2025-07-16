package br.com.passella.httpserver.adapter.input

import br.com.passella.fastlogger.FastLogger
import br.com.passella.httpserver.core.Handler
import br.com.passella.httpserver.core.HandlerRegistry

/**
 * Implementação padrão do registro de handlers.
 * Armazena handlers pré-configurados para reutilização em todas as requisições.
 */
class DefaultHandlerRegistry : HandlerRegistry {
    private val handlers = mutableMapOf<Route, Handler>()

    override fun register(method: String, path: String, handler: Handler): HandlerRegistry {
        val route = Route(method, path)
        handlers[route] = handler
        logger.debug { "Handler registrado para $method $path" }
        return this
    }

    override fun getHandler(method: String, path: String): Handler? {
        val route = Route(method, path)
        return handlers[route]
    }

    data class Route(val method: String, val path: String)

    companion object {
        private val logger = FastLogger.getLogger(DefaultHandlerRegistry::class.java)
    }
}