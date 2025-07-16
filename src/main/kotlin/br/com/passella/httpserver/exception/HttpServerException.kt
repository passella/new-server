package br.com.passella.httpserver.exception

sealed class HttpServerException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

class ServerInitializationException(message: String, cause: Throwable? = null) :
    HttpServerException("Erro na inicialização do servidor: $message", cause)

class ConnectionHandlingException(message: String, cause: Throwable? = null) :
    HttpServerException("Erro ao processar conexão: $message", cause)

open class MalformedRequestException(message: String, cause: Throwable? = null) :
    HttpServerException("Requisição HTTP inválida: $message", cause)

class EmptyRequestException(cause: Throwable? = null) :
    MalformedRequestException("Requisição vazia recebida", cause)

class RequestBodyProcessingException(message: String, cause: Throwable? = null) :
    HttpServerException("Erro ao processar corpo da requisição: $message", cause)

class HeaderProcessingException(message: String, cause: Throwable? = null) :
    HttpServerException("Erro ao processar cabeçalhos: $message", cause)