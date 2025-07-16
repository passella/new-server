# Servidor HTTP de Alta Performance

Este projeto implementa um servidor HTTP de alta performance em Kotlin usando sockets nativos do Java, sem dependências externas, otimizado para Java 21.

## Características de Alta Performance

1. **Uso de APIs nativas**: Utiliza diretamente `ServerSocket` e `Socket` do Java para comunicação HTTP, evitando overhead de frameworks.
2. **Virtual Threads do Java 21**: Aproveita os virtual threads introduzidos no Java 21 para escalabilidade máxima com baixo overhead.
3. **Minimização de alocações de objetos**: Implementação cuidadosa para reduzir garbage collection.
4. **Fechamento adequado de recursos**: Uso de `use` para garantir que recursos sejam liberados.
5. **Resposta HTTP eficiente**: Implementação mínima do protocolo HTTP para atender apenas ao necessário.
6. **Funções inline**: Uso estratégico de funções inline para eliminar overhead de chamadas de método e alocações de lambdas.

## Arquitetura

O projeto segue rigorosamente a arquitetura hexagonal (Ports and Adapters):

- **Domínio Central**: `GreetingService` define a lógica de negócio pura.
- **Portas**: `HttpResponseWriter` define como o domínio se comunica com o exterior.
- **Adaptadores Primários**: `HttpRequestHandler` converte requisições HTTP em chamadas ao domínio.
- **Adaptadores Secundários**: `HttpResponseWriterImpl` implementa a comunicação HTTP.

## Princípios SOLID

1. **SRP**: Cada classe tem uma única responsabilidade.
2. **OCP**: A arquitetura permite extensão sem modificação (ex: novas implementações de `GreetingService`).
3. **LSP**: As implementações podem ser substituídas por suas interfaces.
4. **ISP**: Interfaces são específicas e coesas.
5. **DIP**: Dependências são em direção a abstrações, não implementações concretas.

## Requisitos

- Java 21 ou superior
- Kotlin 1.9.21 ou superior
- Gradle 8.0 ou superior

## Como executar

### Usando scripts de execução

Para iniciar com configurações padrão:
```bash
./run.sh
```

Para iniciar com uma porta personalizada:
```bash
./run-custom-port.sh 8081
```

### Usando variáveis de ambiente

```bash
export APP_PORT=8081
export APP_LOG_LEVEL=DEBUG
export APP_LOG_ENABLED=true
./gradlew run
```

### Usando propriedades do sistema Java

```bash
./gradlew run -Dapp.port=8081 -Dapp.log.level=DEBUG -Dapp.log.enabled=true
```

## Configuração

A aplicação pode ser configurada através de variáveis de ambiente ou propriedades do sistema:

| Variável de Ambiente | Propriedade do Sistema | Descrição | Valor Padrão |
|----------------------|------------------------|-----------|--------------|
| `APP_PORT` | `app.port` | Porta do servidor HTTP | 8080 |
| `APP_LOG_LEVEL` | `app.log.level` | Nível de log (TRACE, DEBUG, INFO, WARN, ERROR, NONE) | INFO |
| `APP_LOG_ENABLED` | `app.log.enabled` | Habilitar/desabilitar logs | true |

## Testando o servidor

```bash
curl http://localhost:8080/greeting
```

Resposta esperada: `Olá`