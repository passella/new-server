package br.com.passella.payments.config

import br.com.passella.config.PropertyProvider
import br.com.passella.fastlogger.FastLogger
import br.com.passella.httpserver.config.HttpServerConfiguration

object HttpServerConfigurationFactory {
    private val logger = FastLogger.getLogger(HttpServerConfigurationFactory::class.java)

    private const val DEFAULT_PORT = 8080
    private const val MIN_PORT_VALUE = 0
    private const val MAX_PORT_VALUE = 65535

    fun createConfiguration(): HttpServerConfiguration {
        val port = getServerPort()
        logger.debug { "Criando configuração do servidor HTTP com porta=$port" }
        return HttpServerConfiguration(port)
    }

    private fun getServerPort(): Int {
        val port = PropertyProvider.getIntProperty("APP_PORT", DEFAULT_PORT)

        if (port <= MIN_PORT_VALUE || port > MAX_PORT_VALUE) {
            logger.warn { "Porta inválida configurada: $port. Usando porta padrão: $DEFAULT_PORT!" }
            return DEFAULT_PORT
        }

        return port
    }
}
