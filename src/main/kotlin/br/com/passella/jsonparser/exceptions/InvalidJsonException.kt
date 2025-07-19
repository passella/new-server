package br.com.passella.jsonparser.exceptions

class InvalidJsonException(
    override val message: String?,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
