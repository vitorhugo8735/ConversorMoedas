# Conversor de Moedas (Java)

Aplicação de console que consulta taxas da ExchangeRate-API v6, converte valores entre diversas moedas e mantém histórico com timestamps.

## Funcionalidades
- Conversão entre múltiplas moedas suportadas pela API.
- Histórico da sessão com timestamps (java.time).
- Comando HIST para visualizar o histórico a qualquer momento.
- Parsing de JSON sem dependências externas (regex).

## Pré-requisitos
- Java 11+.
- Configurar a API Key em CurrencyService.API_KEY.

## Configuração
1. Abra `src/CurrencyService.java`.
2. Substitua `API_KEY` pela sua chave da ExchangeRate-API.
3. Opcional: altere a `BASE_URL` para outra base (padrão: USD).

## Execução
1. Compile o projeto e execute `Main`.
2. Use `HIST` para ver o histórico e `SAIR` para encerrar.

## Fluxo geral
```mermaid
flowchart TD
    A[Início Main] --> B[Buscar taxas via CurrencyService]
    B -->|Sucesso| C[Exibir base e iniciar loop]
    B -->|Erro| Z[Mostrar erro e finalizar]
    C --> D[Solicitar moeda de origem]
    D -->|HIST| H[Exibir histórico] --> C
    D -->|SAIR| X[Exibir histórico final e sair]
    D --> E[Solicitar moeda de destino]
    E -->|HIST| H --> C
    E -->|SAIR| X
    E --> F[Solicitar valor]
    F --> G[Calcular resultado]
    G --> I[Exibir resultado e logar no histórico]
    I --> C
```

## Sequência de uma conversão
```mermaid
sequenceDiagram
    participant User
    participant Main
    participant CurrencyService
    participant API

    User->>Main: Executa programa
    Main->>CurrencyService: getRates()
    CurrencyService->>API: GET /latest/USD
    API-->>CurrencyService: JSON (base_code, conversion_rates)
    CurrencyService-->>Main: CurrencyResponse
    Main->>User: Prompt ORIGEM/DESTINO/VALOR
    User-->>Main: Inputs
    Main->>Main: Calcula (amount * rateTo) / rateFrom
    Main->>Main: Loga no histórico com timestamp
    Main-->>User: Exibe resultado
```

## Estrutura das classes
```mermaid
classDiagram
    class Main {
        +main(String[] args)
        -history: List<String>
        -fmt: DateTimeFormatter
    }

    class CurrencyService {
        -API_KEY: String
        -BASE_URL: String
        +getRates(): CurrencyResponse
        -extractBaseCode(json): String
        -extractConversionRates(json): Map<String, Double>
    }

    class CurrencyResponse {
        +base_code: String
        +conversion_rates: Map<String, Double>
    }

    Main --> CurrencyService : usa
    CurrencyService --> CurrencyResponse : retorna
```

## Estados do histórico na sessão
```mermaid
stateDiagram-v2
    [*] --> Vazio
    Vazio --> Registrando : primeira conversão
    Registrando --> Registrando : novas conversões
    Registrando --> Exibido : HIST ou SAIR
    Exibido --> Registrando : novas conversões
```

## Notas
- O histórico é apenas em memória (limpa ao encerrar).
- A lista de moedas sugeridas é informativa; qualquer código presente em `conversion_rates` é aceito.
- Em caso de erro HTTP ou resposta inválida, é lançada exceção com mensagem descritiva.
