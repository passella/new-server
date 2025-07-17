package br.com.passella.fastlogger

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentLinkedQueue
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

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
    private val logQueue = ConcurrentLinkedQueue<LogEntry>()
    val lastTimestamp = AtomicLong(System.currentTimeMillis())
    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

    init {
        val envLevel = System.getenv("APP_LOG_LEVEL")?.uppercase()
        val configLevel = envLevel ?: System.getProperty("app.log.level")?.uppercase()

        if (configLevel != null) {
            try {
                globalLevel = Level.valueOf(configLevel)
            } catch (_: IllegalArgumentException) {
                if (enabled.get()) {
                    System.err.println(
                        "[FastLogger] Nível de log inválido configurado: $configLevel. Mantendo padrão INFO.",
                    )
                }
            }
        }

        val envEnabled = System.getenv("APP_LOG_ENABLED")
        val configEnabled = envEnabled ?: System.getProperty("app.log.enabled")

        if (configEnabled != null) {
            enabled.set(configEnabled.equals("true", ignoreCase = true))
        }

        startLogProcessor()
    }

    private fun startLogProcessor() {
        val executor = Executors.newSingleThreadScheduledExecutor { Thread(it, "LogProcessor") }
        executor.scheduleAtFixedRate({
            processLogQueue()
        }, 0, 100, TimeUnit.MILLISECONDS)

        // Atualiza timestamp a cada segundo
        executor.scheduleAtFixedRate({
            lastTimestamp.set(System.currentTimeMillis())
        }, 0, 1, TimeUnit.SECONDS)
    }

    private fun processLogQueue() {
        while (logQueue.isNotEmpty()) {
            val entry = logQueue.poll() ?: break
            val timestamp = formatTimestamp(entry.timestamp)
            val message = "[$timestamp] [${entry.level.name}] [${entry.loggerName}] ${entry.message}"
            System.out.println(message)
        }
    }

    private fun formatTimestamp(timestamp: Long): String {
        val instant = Instant.ofEpochMilli(timestamp)
        val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        return dateFormatter.format(dateTime)
    }

    fun getLogger(clazz: Class<*>): Logger = Logger(clazz.name)

    data class LogEntry(
        val timestamp: Long,
        val level: Level,
        val loggerName: String,
        val message: String,
    )

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
            enqueueLog(lastTimestamp.get(), level, name, message())
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
                val errorMessage = "${message()}\n${throwable.stackTraceToString().take(1000)}"
                log(Level.ERROR) { errorMessage }
            }
        }

        fun enqueueLog(
            timestamp: Long,
            level: Level,
            loggerName: String,
            message: String,
        ) {
            logQueue.offer(LogEntry(timestamp, level, loggerName, message))
        }
    }
}
