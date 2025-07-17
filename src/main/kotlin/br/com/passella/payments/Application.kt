package br.com.passella.payments

import br.com.passella.fastlogger.FastLogger
import br.com.passella.payments.config.HttpServerFactory
import kotlin.system.exitProcess

fun main() {
    val logger = FastLogger.getLogger(Application::class.java)
    logger.info { "Iniciando aplicação..." }

    try {
        HttpServerFactory.createServer().start()
    } catch (e: Exception) {
        logger.error(e) { "Erro ao iniciar a aplicação" }
        exitProcess(1)
    }
}

class Application
