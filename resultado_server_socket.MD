# Resultados de Performance - ServerSocket com Virtual Threads

## Configuração do Ambiente

- **JVM**: -Xms16m -Xmx16m
- **Implementação**: ServerSocket com Virtual Threads
- **Data do Teste**: 17/07/2025

## Resultados dos Testes de Carga

| Teste | Requisições/s | Tempo Médio (μs) | Tempo Mínimo (μs) | Tempo Mediano (μs) | Tempo Máximo (ms) | p(90) (μs) | p(95) (μs) |
|-------|--------------|------------------|-------------------|--------------------|--------------------|------------|------------|
| 1     | 1360.95      | 183.85           | 54.65             | 164.23             | 7.84               | 225.51     | 256.62     |
| 2     | 1358.40      | 184.01           | 57.72             | 162.77             | 6.84               | 228.77     | 265.28     |
| 3     | 1365.48      | 182.04           | 57.21             | 164.78             | 7.95               | 225.99     | 256.22     |
| **Média** | **1361.61** | **183.30**     | **56.53**         | **163.93**         | **7.54**           | **226.76** | **259.37** |

## Detalhes Adicionais

- **Taxa de Falha**: 0.00%
- **Thresholds**:
  - `p(95)<500`: p(95) = 256.62μs, 265.28μs, 256.22μs
  - `rate<0.01`: Taxa de falha = 0.00%