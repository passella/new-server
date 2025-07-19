package br.com.passella.config

import br.com.passella.fastlogger.FastLogger

object PropertyProvider {
    val logger: FastLogger.Logger
        get() = FastLogger.getLogger(PropertyProvider::class.java)

    private val TRUE_VALUES = setOf("true", "yes", "y", "1")
    private val FALSE_VALUES = setOf("false", "no", "n", "0")

    fun getProperty(
        key: String,
        defaultValue: String,
    ): String {
        val envValue = System.getenv(key)
        val sysKey = key.lowercase().replace('_', '.')
        val sysValue = System.getProperty(sysKey)

        val result = envValue ?: sysValue ?: defaultValue

        if (envValue != null) {
            logger.trace { "Propriedade '$key' obtida de variável de ambiente: '$result'" }
        } else if (sysValue != null) {
            logger.trace { "Propriedade '$key' obtida de propriedade do sistema ($sysKey): '$result'" }
        } else {
            logger.trace { "Propriedade '$key' não encontrada, usando valor padrão: '$defaultValue'" }
        }

        return result
    }

    fun getIntProperty(
        key: String,
        defaultValue: Int,
    ): Int {
        val stringValue = getProperty(key, defaultValue.toString())

        return try {
            stringValue.toInt()
        } catch (e: NumberFormatException) {
            logger.warn { "Valor inválido para propriedade '$key': '$stringValue'. Usando valor padrão: $defaultValue" }
            defaultValue
        }
    }

    fun getBooleanProperty(
        key: String,
        defaultValue: Boolean,
    ): Boolean {
        val stringValue = getProperty(key, defaultValue.toString())
        val lowercaseValue = stringValue.lowercase()

        return when {
            TRUE_VALUES.contains(lowercaseValue) -> true
            FALSE_VALUES.contains(lowercaseValue) -> false
            else -> {
                logger.warn {
                    "Valor inválido para propriedade booleana '$key': '$stringValue'. " +
                        "Usando valor padrão: $defaultValue"
                }
                defaultValue
            }
        }
    }

    inline fun <reified T : Enum<T>> getEnumProperty(
        key: String,
        defaultValue: T,
    ): T {
        val stringValue = getProperty(key, defaultValue.name)

        return try {
            java.lang.Enum.valueOf(T::class.java, stringValue.uppercase())
        } catch (_: IllegalArgumentException) {
            logger.warn {
                "Valor inválido para propriedade enum '$key': '$stringValue'. " +
                    "Usando valor padrão: ${defaultValue.name}"
            }
            defaultValue
        }
    }
}
