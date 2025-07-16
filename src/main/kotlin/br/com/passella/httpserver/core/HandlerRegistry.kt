package br.com.passella.httpserver.core


interface HandlerRegistry {

    fun register(method: String, path: String, handler: Handler): HandlerRegistry
    

    fun getHandler(method: String, path: String): Handler?
}