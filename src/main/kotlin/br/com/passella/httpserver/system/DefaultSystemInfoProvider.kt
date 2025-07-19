package br.com.passella.httpserver.system

import java.lang.management.ManagementFactory

class DefaultSystemInfoProvider : SystemInfoProvider {
    companion object {
        private const val BYTES_IN_MEGABYTE = 1024 * 1024
    }

    override fun getSystemInfo(serverPort: Int): SystemInfo {
        val runtime = Runtime.getRuntime()

        val maxMemory = runtime.maxMemory() / BYTES_IN_MEGABYTE
        val totalMemory = runtime.totalMemory() / BYTES_IN_MEGABYTE
        val freeMemory = runtime.freeMemory() / BYTES_IN_MEGABYTE
        val availableMemory = maxMemory - (totalMemory - freeMemory)

        val processors = runtime.availableProcessors()
        val osName = System.getProperty("os.name")
        val osVersion = System.getProperty("os.version")
        val osArch = System.getProperty("os.arch")
        val javaVersion = System.getProperty("java.version")

        val memoryBean = ManagementFactory.getMemoryMXBean()
        val heapMemoryUsage = memoryBean.heapMemoryUsage
        val nonHeapMemoryUsage = memoryBean.nonHeapMemoryUsage

        return SystemInfo(
            javaVersion = javaVersion,
            osName = osName,
            osVersion = osVersion,
            osArch = osArch,
            processors = processors,
            maxMemoryMb = maxMemory,
            totalMemoryMb = totalMemory,
            freeMemoryMb = freeMemory,
            availableMemoryMb = availableMemory,
            heapMemoryUsage =
                MemoryUsageInfo(
                    usedMb = heapMemoryUsage.used / BYTES_IN_MEGABYTE,
                    committedMb = heapMemoryUsage.committed / BYTES_IN_MEGABYTE,
                    maxMb = (heapMemoryUsage.max / BYTES_IN_MEGABYTE).toString(),
                ),
            nonHeapMemoryUsage =
                MemoryUsageInfo(
                    usedMb = nonHeapMemoryUsage.used / BYTES_IN_MEGABYTE,
                    committedMb = nonHeapMemoryUsage.committed / BYTES_IN_MEGABYTE,
                    maxMb =
                        if (nonHeapMemoryUsage.max < 0) {
                            "N/A"
                        } else {
                            (nonHeapMemoryUsage.max / BYTES_IN_MEGABYTE).toString()
                        },
                ),
            serverPort = serverPort,
        )
    }
}
