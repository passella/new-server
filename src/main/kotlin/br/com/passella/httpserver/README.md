# MyHttpServer

Um servidor HTTP leve, de alta performance e altamente configurável para aplicações Kotlin/JVM, projetado seguindo os princípios da arquitetura hexagonal e SOLID.

## Características

- **Alta Performance**: Utiliza um pool de threads configurável para processar múltiplas conexões simultaneamente
- **API Fluente**: Interface encadeável para configuração e registro de handlers
- **Baixo Overhead**: Implementação leve com mínima alocação de objetos por requisição
- **Configurável**: Amplas opções de configuração para otimizar de acordo com necessidades específicas
- **Arquitetura Hexagonal**: Clara separação entre domínio e infraestrutura
- **Extensível**: Fácil adição de novos handlers e funcionalidades

## Uso Básico

```kotlin
// Criar e configurar o servidor
val server = MyHttpServer(
    configuration = HttpServerConfiguration(port = 8080),
    myHttpServerExecutorServiceProvider = MyHttpServerExecutorServiceProviderImpl(),
    requestPathHandler = RequestPathHandlerImpl(DefaultNotFoundHandler(), DefaultErrorHandler()),
    requestParser = RequestParserImpl()
)

// Registrar handlers usando API fluente
server
    .handler("GET", "/api/hello", HelloHandler())
    .handler("POST", "/api/users", CreateUserHandler())
    .handler("GET", "/api/users", GetUsersHandler())

// Iniciar o servidor
server.start()
```

## Configuração

O servidor pode ser configurado através da classe `HttpServerConfiguration`:

```kotlin
val config = HttpServerConfiguration(
    port = 8080,                    // Porta em que o servidor irá escutar
    socketTimeoutMs = 30000,        // Timeout para sockets em milissegundos
    receiveBufferSize = 65536,      // Tamanho do buffer de recebimento
    backlog = 500,                  // Tamanho da fila de conexões pendentes
    reuseAddress = true             // Permite reutilização de endereços
)
```

## Criando Handlers

Handlers são componentes que processam requisições HTTP. Eles implementam a interface `HttpHandler`:

```kotlin
class HelloHandler : HttpHandler {
    override fun handle(request: HttpRequest, response: HttpResponse) {
        response
            .header("Content-Type", "application/json")
            .body("""{"message": "Hello, World!"}""")
            .send()
    }
}
```

## Arquitetura

MyHttpServer segue a arquitetura hexagonal (ports and adapters):

- **Core (Portas)**: Interfaces como `HttpHandler`, `RequestParser`, `RequestPathHandler`
- **Adapters**: Implementações como `RequestParserImpl`, `RequestPathHandlerImpl`
- **Configuração**: Classes de configuração como `HttpServerConfiguration`

## Componentes Principais

- **MyHttpServer**: Classe principal que gerencia o ciclo de vida do servidor
- **HttpHandler**: Interface para processamento de requisições
- **RequestPathHandler**: Gerencia o roteamento de requisições para handlers apropriados
- **RequestParser**: Converte dados brutos de entrada em objetos `HttpRequest`
- **HttpResponse**: Classe fluente para construção de respostas HTTP

## Tratamento de Erros

O servidor possui mecanismos integrados para tratamento de erros:

- **DefaultNotFoundHandler**: Processa requisições para rotas não encontradas
- **DefaultErrorHandler**: Trata exceções lançadas durante o processamento de requisições

## Performance

MyHttpServer é otimizado para alta performance:

- Utiliza buffers eficientes para I/O
- Minimiza alocações de objetos
- Implementa pool de threads configurável
- Reutiliza recursos quando possível

## Exemplo Completo

```kotlin
// Configuração do servidor
val config = HttpServerConfiguration(port = 8080)
val executorProvider = MyHttpServerExecutorServiceProviderImpl()
val notFoundHandler = DefaultNotFoundHandler()
val errorHandler = DefaultErrorHandler()
val pathHandler = RequestPathHandlerImpl(notFoundHandler, errorHandler)
val requestParser = RequestParserImpl()

// Criação do servidor
val server = MyHttpServer(
    configuration = config,
    myHttpServerExecutorServiceProvider = executorProvider,
    requestPathHandler = pathHandler,
    requestParser = requestParser
)

// Registro de handlers
server
    .handler("GET", "/api/products", GetProductsHandler(productService))
    .handler("GET", "/api/products/{id}", GetProductByIdHandler(productService))
    .handler("POST", "/api/products", CreateProductHandler(productService))
    .handler("PUT", "/api/products/{id}", UpdateProductHandler(productService))
    .handler("DELETE", "/api/products/{id}", DeleteProductHandler(productService))

// Iniciar o servidor
server.start()
```

## Boas Práticas

- Mantenha handlers stateless para melhor performance
- Registre handlers durante a inicialização da aplicação
- Utilize injeção de dependências para fornecer serviços aos handlers
- Implemente tratamento adequado de erros em cada handler
- Configure timeouts apropriados para evitar conexões pendentes

## Limitações

- Não suporta WebSockets nativamente
- Não inclui suporte a HTTPS (TLS/SSL) - use um proxy reverso como Nginx para isso
- Não implementa compressão de resposta - considere implementar em handlers específicos quando necessário