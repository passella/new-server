package br.com.passella.httpserver.system

import java.lang.management.MemoryUsage

data class SystemInfo(
    val javaVersion: String,
    val osName: String,
    val osVersion: String,
    val osArch: String,
    val processors: Int,
    val maxMemoryMb: Long,
    val totalMemoryMb: Long,
    val freeMemoryMb: Long,
    val availableMemoryMb: Long,
    val heapMemoryUsage: MemoryUsageInfo,
    val nonHeapMemoryUsage: MemoryUsageInfo,
    val serverPort: Int
) {
    override fun toString(): String {
        return buildString {
            appendLine("Iniciando servidor na porta $serverPort com Java $javaVersion")
            appendLine("Sistema Operacional: $osName $osVersion ($osArch)")
            appendLine("Processadores disponíveis: $processors")
            appendLine("Memória máxima: $maxMemoryMb MB")
            appendLine("Memória total alocada: $totalMemoryMb MB")
            appendLine("Memória livre: $freeMemoryMb MB")
            appendLine("Memória disponível: $availableMemoryMb MB")
            appendLine("Heap: $heapMemoryUsage")
            appendLine("Non-Heap: $nonHeapMemoryUsage")
        }
    }
}

data class MemoryUsageInfo(
    val usedMb: Long,
    val committedMb: Long,
    val maxMb: String
) {
    override fun toString(): String {
        return "usado=${usedMb}MB, comprometido=${committedMb}MB, max=${maxMb}MB"
    }
}