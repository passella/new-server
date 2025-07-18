package br.com.passella.httpserver.system

interface SystemInfoProvider {
    fun getSystemInfo(serverPort: Int): SystemInfo
}