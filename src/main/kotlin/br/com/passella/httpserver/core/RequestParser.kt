package br.com.passella.httpserver.core

import br.com.passella.httpserver.core.model.HttpRequest
import java.io.BufferedReader

interface RequestParser {
    fun parseRequest(input: BufferedReader): HttpRequest
}