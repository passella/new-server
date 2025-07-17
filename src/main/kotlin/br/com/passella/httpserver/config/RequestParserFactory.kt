package br.com.passella.httpserver.config

import br.com.passella.httpserver.core.RequestParser
import br.com.passella.httpserver.parser.RequestParserImpl

object RequestParserFactory {
    fun createRequestParser(): RequestParser = RequestParserImpl()
}