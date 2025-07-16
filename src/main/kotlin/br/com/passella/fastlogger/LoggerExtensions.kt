package br.com.passella.fastlogger

inline fun <reified T : Any> T.logger(): FastLogger.Logger {
    return FastLogger.getLogger(T::class.java)
}

