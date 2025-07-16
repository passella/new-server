package br.com.passella.payments

import br.com.passella.fastlogger.FastLogger
import br.com.passella.httpserver.config.HttpServerFactory
import br.com.passella.payments.config.HandlerRegistryConfiguration
import br.com.passella.payments.config.HttpServerConfigurationFactory
import kotlin.system.exitProcess

fun main() {
    val logger = FastLogger.getLogger(Application::class.java)
    logger.info { "Iniciando aplicação..." }

    try {
        val handlerRegistry = HandlerRegistryConfiguration.createHandlerRegistry()
        val serverConfiguration = HttpServerConfigurationFactory.createConfiguration()
        HttpServerFactory
            .createServer(handlerRegistry, serverConfiguration)
            .start()

    } catch (e: Exception) {
        logger.error(e) { "Erro ao iniciar a aplicação" }
        exitProcess(1)
    }
}

class Application