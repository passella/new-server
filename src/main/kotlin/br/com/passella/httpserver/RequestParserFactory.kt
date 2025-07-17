package br.com.passella.httpserver

object RequestParserFactory {
    fun createRequestParser(): RequestParser = RequestParserImpl()
}
