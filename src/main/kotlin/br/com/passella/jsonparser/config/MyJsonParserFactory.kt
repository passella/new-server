package br.com.passella.jsonparser.config

import br.com.passella.jsonparser.MyJsonParser

object MyJsonParserFactory {
    fun create(): MyJsonParser {
        return MyJsonParser()
    }
}