package br.com.passella.httpserver.system

import java.lang.management.ManagementFactory

class DefaultSystemInfoProvider : SystemInfoProvider {
    override fun getSystemInfo(serverPort: Int): SystemInfo {
        val mb = 1024 * 1024
        val runtime = Runtime.getRuntime()
        
        val maxMemory = runtime.maxMemory() / mb
        val totalMemory = runtime.totalMemory() / mb
        val freeMemory = runtime.freeMemory() / mb
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
            heapMemoryUsage = MemoryUsageInfo(
                usedMb = heapMemoryUsage.used / mb,
                committedMb = heapMemoryUsage.committed / mb,
                maxMb = (heapMemoryUsage.max / mb).toString()
            ),
            nonHeapMemoryUsage = MemoryUsageInfo(
                usedMb = nonHeapMemoryUsage.used / mb,
                committedMb = nonHeapMemoryUsage.committed / mb,
                maxMb = if (nonHeapMemoryUsage.max < 0) "N/A" else (nonHeapMemoryUsage.max / mb).toString()
            ),
            serverPort = serverPort
        )
    }
}