package br.com.passella.httpserver.system

object SystemInfoProviderFactory {
    fun createSystemInfoProvider(): SystemInfoProvider = DefaultSystemInfoProvider()
}
