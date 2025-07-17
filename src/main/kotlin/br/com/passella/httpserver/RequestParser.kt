package br.com.passella.httpserver

import java.io.BufferedReader

interface RequestParser {
    fun parseRequest(input: BufferedReader): HttpRequest
}
