# Servidor de Pagamentos de Alta Performance

Um servidor HTTP de alta performance implementado em Kotlin usando sockets nativos do Java, sem dependências externas, otimizado para Java 21. Este projeto demonstra como construir
uma aplicação de processamento de pagamentos altamente eficiente e escalável.

## Visão Geral

Este projeto implementa um servidor de pagamentos utilizando uma arquitetura hexagonal (ports and adapters) com componentes modulares e reutilizáveis:

- **MyHttpServer**: Servidor HTTP leve e de alta performance
- **FastLogger**: Sistema de logging de baixa latência
- **MyJsonParser**: Parser JSON eficiente sem dependências externas
- **PropertyProvider**: Gerenciador de configurações via variáveis de ambiente e propriedades do sistema

A aplicação é projetada para lidar com alto volume de requisições de pagamentos com latência mínima e uso eficiente de recursos.

## CI/CD com GitHub Actions

Este projeto utiliza GitHub Actions para automatizar o processo de build e publicação de imagens Docker. A cada commit no branch principal (main/master), o workflow executa:

1. Build do projeto Kotlin com Gradle
2. Build da imagem Docker
3. Publicação da imagem no GitHub Container Registry (ghcr.io)

### URLs das Imagens Docker

Após cada commit bem-sucedido, as imagens Docker são publicadas nos seguintes formatos:

- **Tag baseada no SHA do commit**: `ghcr.io/[seu-usuario]/[nome-do-repo]:sha-[short-sha]`
- **Tag baseada no branch**: `ghcr.io/[seu-usuario]/[nome-do-repo]:[branch-name]`
- **Tag latest**: `ghcr.io/[seu-usuario]/[nome-do-repo]:latest`

Para usar a imagem Docker mais recente:

```bash
docker pull ghcr.io/[seu-usuario]/[nome-do-repo]:latest
docker run -p 8080:8080 ghcr.io/[seu-usuario]/[nome-do-repo]:latest
```

## Características

- **Alta Performance**: Otimizado para processamento rápido de requisições HTTP
- **Baixo Overhead**: Implementação leve sem dependências externas
- **Configurável**: Facilmente configurável via variáveis de ambiente
- **Escalável**: Utiliza pool de threads configurável para processamento paralelo
- **Testável**: Arquitetura que facilita testes unitários e de carga
- **Modular**: Componentes independentes e reutilizáveis

## Requisitos

- Java 21 ou superior
- Kotlin 1.9.21 ou superior
- Gradle 8.5 ou superior

## Executando a Aplicação

### Via Gradle

```bash
./gradlew run
```

### Via JAR

```bash
./gradlew build
java -jar build/libs/payment-server-1.0-SNAPSHOT.jar
```

### Via Docker

```bash
docker pull ghcr.io/[seu-usuario]/[nome-do-repo]:latest
docker run -p 8080:8080 ghcr.io/[seu-usuario]/[nome-do-repo]:latest
```

### Configuração

A aplicação pode ser configurada através de variáveis de ambiente:

| Variável        | Descrição                                            | Valor Padrão |
|-----------------|------------------------------------------------------|--------------|
| APP_PORT        | Porta do servidor HTTP                               | 8080         |
| APP_LOG_LEVEL   | Nível de log (TRACE, DEBUG, INFO, WARN, ERROR, NONE) | INFO         |
| APP_LOG_ENABLED | Habilita/desabilita logs                             | true         |

Exemplo:

```bash
APP_PORT=9090 APP_LOG_LEVEL=DEBUG ./gradlew run
```

Com Docker:

```bash
docker run -p 9090:9090 -e APP_PORT=9090 -e APP_LOG_LEVEL=DEBUG ghcr.io/[seu-usuario]/[nome-do-repo]:latest
```

## API de Pagamentos

### Processar Pagamento

**Endpoint**: `POST /payments`

**Corpo da Requisição**:

```json
{
  "correlationId": "550e8400-e29b-41d4-a716-446655440000",
  "amount": 1000,
  "requestedAt": "2023-04-29T16:14:30.123Z"
}
```

**Resposta de Sucesso**:

```json
{
  "status": "ACCEPTED",
  "id": "payment-123"
}
```

## Arquitetura

O projeto segue a arquitetura hexagonal (ports and adapters):

```
┌────────────────────────────────────────────────────────────┐
│                      Aplicação                             │
│                                                            │
│  ┌─────────────┐      ┌─────────────┐      ┌────────────┐  │
│  │  Payments   │      │  HttpServer │      │ FastLogger │  │
│  │   Domain    │◄────►│    Adapter  │◄────►│  Adapter   │  │
│  └─────────────┘      └─────────────┘      └────────────┘  │
│         ▲                    ▲                   ▲         │
└─────────┼────────────────────┼───────────────────┼─────────┘
          │                    │                   │
┌─────────┼────────────────────┼───────────────────┼─────────┐
│         ▼                    ▼                   ▼         │
│  ┌─────────────┐      ┌─────────────┐      ┌────────────┐  │
│  │  Payments   │      │  HttpServer │      │ FastLogger │  │
│  │    Port     │      │    Port     │      │   Port     │  │
│  └─────────────┘      └─────────────┘      └────────────┘  │
│                                                            │
│                       Infraestrutura                       │
└────────────────────────────────────────────────────────────┘
```

### Componentes Principais

- **MyHttpServer**: Servidor HTTP leve e configurável
- **PaymentsHandler**: Processa requisições de pagamento
- **FastLogger**: Sistema de logging de alta performance
- **PropertyProvider**: Gerencia configurações da aplicação
- **MyJsonParser**: Parser JSON eficiente

## Testes de Carga

O projeto inclui scripts para testes de carga usando [k6](https://k6.io/):

```bash
cd test
./run_test.sh
```

O script de teste simula até 1000 usuários virtuais enviando requisições de pagamento simultaneamente.

## Desenvolvimento

### Estrutura do Projeto

```
src/
├── main/
│   └── kotlin/
│       └── br/
│           └── com/
│               └── passella/
│                   ├── config/
│                   │   └── PropertyProvider.kt
│                   ├── fastlogger/
│                   │   ├── FastLogger.kt
│                   │   └── LoggerExtensions.kt
│                   ├── httpserver/
│                   │   ├── adapter/
│                   │   ├── config/
│                   │   ├── core/
│                   │   ├── handler/
│                   │   ├── parser/
│                   │   └── MyHttpServer.kt
│                   ├── jsonparser/
│                   │   ├── config/
│                   │   ├── exceptions/
│                   │   ├── MyJson.kt
│                   │   └── MyJsonParser.kt
│                   └── payments/
│                       ├── config/
│                       ├── handler/
│                       └── Application.kt
└── test/
    └── kotlin/
        └── br/
            └── com/
                └── passella/
                    ├── httpserver/
                    ├── jsonparser/
                    └── payments/
```

### Compilação

```bash
./gradlew build
```

### Testes Unitários

```bash
./gradlew test
```

## Contribuindo

1. Faça um fork do projeto
2. Crie uma branch para sua feature (`git checkout -b feature/amazing-feature`)
3. Commit suas mudanças (`git commit -m 'Add some amazing feature'`)
4. Push para a branch (`git push origin feature/amazing-feature`)
5. Abra um Pull Request

## Licença

Este projeto está licenciado sob a licença MIT - veja o arquivo LICENSE para detalhes.

## Componentes

Para mais informações sobre os componentes individuais, consulte os READMEs específicos:

- [MyHttpServer](src/main/kotlin/br/com/passella/httpserver/README.md)

teste9