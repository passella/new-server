package br.com.passella.httpserver.config

import br.com.passella.httpserver.adapter.output.HttpResponseWriterImpl
import br.com.passella.httpserver.core.HttpResponseWriter

object HttpResponseWriterFactory {
    fun create(): HttpResponseWriter {
        return HttpResponseWriterImpl()
    }
}