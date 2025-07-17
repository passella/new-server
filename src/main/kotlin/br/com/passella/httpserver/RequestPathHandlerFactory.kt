package br.com.passella.httpserver

object RequestPathHandlerFactory {
    fun create(): RequestPathHandler = RequestPathHandlerImpl()
}
