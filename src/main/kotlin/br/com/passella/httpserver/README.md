# MyHttpServer - Componente de Servidor HTTP Reutilizável

Este componente fornece um servidor HTTP leve e de alta performance para aplicações Kotlin/JVM. Ele utiliza virtual threads do Java para lidar com múltiplas conexões de forma eficiente.

## Características

- Servidor HTTP simples e leve
- Utiliza virtual threads para alta concorrência
- Sistema de roteamento baseado em método HTTP e caminho
- Handlers personalizáveis para processar requisições
- Tratamento de erros robusto

## Como usar

### 1. Crie um HttpResponseWriter

Implemente a interface `HttpResponseWriter` ou use a implementação padrão `HttpResponseWriterImpl`:

```kotlin
val responseWriter = HttpResponseWriterImpl()
```

### 2. Configure o registro de handlers

Crie e configure um `HandlerRegistry` para mapear rotas para handlers:

```kotlin
val registry = DefaultHandlerRegistry()
registry.register("GET", "/hello", MyCustomHandler(responseWriter))
```

### 3. Crie e inicie o servidor

```kotlin
val server = MyHttpServer(responseWriter, registry)
server.start(8080) // Inicia o servidor na porta 8080
```

### 4. Implemente seus próprios handlers

Crie classes que implementam a interface `Handler` para processar requisições específicas:

```kotlin
class MyCustomHandler(private val responseWriter: HttpResponseWriter) : Handler {
    override fun handle(socket: Socket) {
        responseWriter.writeResponse(
            socket,
            "Hello, World!",
            200,
            "text/plain"
        )
    }
}
```

## Arquitetura

O componente segue uma arquitetura simples e extensível:

- `MyHttpServer`: Classe principal que gerencia o ciclo de vida do servidor
- `Handler`: Interface para processadores de requisições
- `HandlerRegistry`: Interface para registro e recuperação de handlers
- `HttpResponseWriter`: Interface para escrever respostas HTTP
- `DefaultHandlerRegistry`: Implementação padrão do registro de handlers
- `HttpResponseWriterImpl`: Implementação padrão do escritor de respostas

## Exemplo completo

```kotlin
import br.com.passella.httpserver.*
import java.net.Socket

fun main() {
    // Cria o escritor de respostas
    val responseWriter = HttpResponseWriterImpl()
    
    // Configura o registro de handlers
    val registry = DefaultHandlerRegistry()
    
    // Registra um handler para a rota GET /hello
    registry.register("GET", "/hello", object : Handler {
        override fun handle(socket: Socket) {
            responseWriter.writeResponse(
                socket,
                "Hello, World!",
                200,
                "text/plain"
            )
        }
    })
    
    // Inicia o servidor na porta 8080
    MyHttpServer(responseWriter, registry).start(8080)
}
```