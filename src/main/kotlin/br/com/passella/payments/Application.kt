package br.com.passella.payments

import br.com.passella.fastlogger.FastLogger
import br.com.passella.payments.config.HttpServerFactory
import java.util.Properties
import kotlin.system.exitProcess

fun main() {
    val logger = FastLogger.getLogger(Application::class.java)
    val version = getApplicationVersion()
    
    logger.info { "Iniciando aplicação versão $version" }

    try {
        HttpServerFactory.createServer().start()
    } catch (e: Exception) {
        logger.error(e) { "Erro ao iniciar a aplicação" }
        exitProcess(1)
    }
}

private fun getApplicationVersion(): String {
    return try {
        val properties = Properties()
        val inputStream = Application::class.java.classLoader.getResourceAsStream("version.properties")
        if (inputStream != null) {
            properties.load(inputStream)
            inputStream.close()
            properties.getProperty("version", "unknown")
        } else {
            val manifestVersion = Application::class.java.`package`.implementationVersion
            manifestVersion ?: "unknown"
        }
    } catch (e: Exception) {
        "unknown"
    }
}

class Application