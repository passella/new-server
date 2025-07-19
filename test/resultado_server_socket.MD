# Resultados de Performance - ServerSocket com Virtual Threads

## Configuração do Ambiente

- **JVM**: -Xms16m -Xmx16m
- **Implementação**: ServerSocket com Virtual Threads
- **Data do Teste**: 17/07/2025

## Resultados dos Testes de Carga

| Teste     | Requisições/s | Tempo Médio (μs) | Tempo Mínimo (μs) | Tempo Mediano (μs) | Tempo Máximo (ms) | p(90) (μs) | p(95) (μs) |
|-----------|---------------|------------------|-------------------|--------------------|-------------------|------------|------------|
| 1         | 1360.95       | 183.85           | 54.65             | 164.23             | 7.84              | 225.51     | 256.62     |
| 2         | 1358.40       | 184.01           | 57.72             | 162.77             | 6.84              | 228.77     | 265.28     |
| 3         | 1365.48       | 182.04           | 57.21             | 164.78             | 7.95              | 225.99     | 256.22     |
| **Média** | **1361.61**   | **183.30**       | **56.53**         | **163.93**         | **7.54**          | **226.76** | **259.37** |

## Detalhes Adicionais

- **Taxa de Falha**: 0.00%
- **Thresholds**:
    - `p(95)<500`: p(95) = 256.62μs, 265.28μs, 256.22μs
    - `rate<0.01`: Taxa de falha = 0.00%

## Resultados após Refatoração (SystemInfoProvider)

| Teste     | Requisições/s | Tempo Médio (μs) | Tempo Mínimo (μs) | Tempo Mediano (μs) | Tempo Máximo (ms) | p(90) (μs) | p(95) (μs) |
|-----------|---------------|------------------|-------------------|--------------------|-------------------|------------|------------|
| 1         | 1358.01       | 185.78           | 54.64             | 166.73             | 14.11             | 230.91     | 264.59     |
| 2         | 1360.69       | 181.30           | 57.77             | 165.28             | 6.16              | 226.03     | 255.98     |
| 3         | 1362.69       | 182.70           | 55.05             | 165.43             | 8.96              | 226.44     | 256.09     |
| 4         | 1360.19       | 184.97           | 53.61             | 166.49             | 8.86              | 226.80     | 256.60     |
| 5         | 1358.13       | 214.58           | 51.88             | 164.59             | 35.56             | 238.84     | 316.38     |
| 6         | 1355.36       | 181.55           | 56.46             | 164.54             | 7.59              | 224.74     | 254.07     |
| 7         | 1362.82       | 183.98           | 55.46             | 164.91             | 11.18             | 225.62     | 255.69     |
| **Média** | **1359.70**   | **187.84**       | **54.98**         | **165.42**         | **13.20**         | **228.48** | **265.63** |

## Análise Comparativa

- **Requisições/s**: 1361.61 → 1359.70
- **Tempo Médio**: 183.30μs → 187.84μs
- **Tempo Máximo**: 7.54ms → 13.20ms
- **p(95)**: 259.37μs → 265.63μs

## Alterações Implementadas

- Extração da lógica de coleta de informações do sistema do método `startLog` em `MyHttpServer`
- Criação de:
    - Interface `SystemInfoProvider`
    - Implementação `DefaultSystemInfoProvider`
    - Data class `SystemInfo` e `MemoryUsageInfo`
    - Factory `SystemInfoProviderFactory`
- Injeção do `SystemInfoProvider` na classe `MyHttpServer`

## Resultados após Refatoração (Integração com Jackson JSON Parser)

| Teste     | Requisições/s | Tempo Médio (μs) | Tempo Mínimo (μs) | Tempo Mediano (μs) | Tempo Máximo (ms) | p(90) (μs) | p(95) (μs) |
|-----------|---------------|------------------|-------------------|--------------------|-------------------|------------|------------|
| 1         | 1356.62       | 187.93           | 60.79             | 151.84             | 60.10             | 233.90     | 290.36     |
| 2         | 1359.89       | 183.67           | 57.64             | 150.84             | 9.64              | 226.87     | 267.67     |
| 3         | 1361.10       | 178.09           | 56.17             | 151.54             | 10.23             | 224.56     | 259.39     |
| 4         | 1358.21       | 175.52           | 57.90             | 150.95             | 10.13             | 224.72     | 259.79     |
| 5         | 1359.51       | 184.15           | 53.28             | 152.11             | 10.82             | 228.42     | 271.57     |
| 6         | 1360.25       | 178.02           | 59.39             | 151.65             | 9.03              | 225.58     | 261.76     |
| 7         | 1360.64       | 177.60           | 55.53             | 150.99             | 8.67              | 224.16     | 257.96     |
| **Média** | **1359.46**   | **180.71**       | **57.24**         | **151.42**         | **16.94**         | **226.89** | **266.93** |

## Análise Comparativa (Após Integração com Jackson JSON Parser)

- **Requisições/s**: 1359.70 → 1359.46
- **Tempo Médio**: 187.84μs → 180.71μs
- **Tempo Máximo**: 13.20ms → 16.94ms
- **p(95)**: 265.63μs → 266.93μs

## Alterações Implementadas (Integração com Jackson JSON Parser)

- Substituição do parser JSON personalizado por Jackson Databind para maior eficiência e robustez no parsing de JSON.
- Atualização da classe `MyJsonParser` para utilizar `ObjectMapper` com configuração para ignorar propriedades desconhecidas.
- Ajuste na fábrica `PaymentsHandlerFactory` para injetar o parser JSON no `PaymentsHandler`.
- Melhorias na formatação de código e mensagens de log em `PropertyProvider` para maior clareza.
- Atualização da exception `InvalidJsonException` para suportar causa raiz e mensagens detalhadas de erro.
- Pequenas otimizações na classe `DefaultSystemInfoProvider` com uso de constante para conversão de bytes para megabytes.