package br.com.passella.fastlogger

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

object FastLogger {
    enum class Level(
        val value: Int,
    ) {
        TRACE(0),
        DEBUG(1),
        INFO(2),
        WARN(3),
        ERROR(4),
        NONE(5),
    }

    private val enabled = AtomicBoolean(true)
    private var globalLevel = Level.INFO

    private val levelOverrides = ConcurrentHashMap<String, Level>()

    val dateFormatter: DateTimeFormatter
        get() = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

    val threadLocalBuilder: ThreadLocal<StringBuilder>
        get() = ThreadLocal.withInitial { StringBuilder(256) }

    init {
        val envLevel = System.getenv("APP_LOG_LEVEL")?.uppercase()
        val configLevel = envLevel ?: System.getProperty("app.log.level")?.uppercase()

        if (configLevel != null) {
            try {
                globalLevel = Level.valueOf(configLevel)
            } catch (_: IllegalArgumentException) {
            }
        }

        val envEnabled = System.getenv("APP_LOG_ENABLED")
        val configEnabled = envEnabled ?: System.getProperty("app.log.enabled")

        if (configEnabled != null) {
            enabled.set(configEnabled.equals("true", ignoreCase = true))
        }
    }

    fun getLogger(clazz: Class<*>): Logger = Logger(clazz.name)

    class Logger(
        val name: String,
    ) {
        fun isEnabled(level: Level): Boolean {
            if (!enabled.get()) return false

            val effectiveLevel = levelOverrides[name] ?: globalLevel
            return level.value >= effectiveLevel.value
        }

        inline fun log(
            level: Level,
            message: () -> String,
        ) {
            if (!isEnabled(level)) return

            val sb = threadLocalBuilder.get()
            sb.setLength(0)

            sb
                .append('[')
                .append(dateFormatter.format(LocalDateTime.now()))
                .append("] [")
                .append(level.name)
                .append("] [")
                .append(name)
                .append("] ")
                .append(message())

            System.out.println(sb.toString())
        }

        inline fun trace(message: () -> String) {
            log(Level.TRACE, message)
        }

        inline fun debug(message: () -> String) {
            log(Level.DEBUG, message)
        }

        inline fun info(message: () -> String) {
            log(Level.INFO, message)
        }

        inline fun warn(message: () -> String) {
            log(Level.WARN, message)
        }

        inline fun error(message: () -> String) {
            log(Level.ERROR, message)
        }

        inline fun error(
            throwable: Throwable,
            message: () -> String,
        ) {
            if (isEnabled(Level.ERROR)) {
                val sb = StringBuilder()
                sb
                    .append(message())
                    .append("\n")
                    .append(throwable.stackTraceToString())

                log(Level.ERROR) { sb.toString() }
            }
        }
    }
}
