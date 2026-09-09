# Explicação da Aula 21 — Testes Unitários Automatizados com JUnit 5

| | |
|---|---|
| **Disciplina** | Programação Orientada a Objetos |
| **Curso** | Análise e Desenvolvimento de Sistemas (3º Período) |
| **Tema** | Arquitetura do JUnit 5 (Jupiter), Ciclo de Vida dos Testes (`@Test`, `@BeforeEach`, `@AfterEach`, `@BeforeAll`, `@AfterAll`), Asserções Fluentes, Validação de Exceções (`assertThrows`), Testes Parametrizados (`@ParameterizedTest`) e Padrão AAA (*Arrange, Act, Assert*) |
| **Carga Horária** | 100 minutos (2 horas-aula) |
| **Professor** | Alexandre Neves Louzada |
| **Tutorial** | `aulas/TutorialAula21.md` |
| **Estudo de Caso** | `exemplos/aula-21/` |
| **Exercícios Resolvidos** | `solucoes/aula-21/` |

## 1. Objetivos de Aprendizagem

- **Conceitual:** Compreender o papel dos testes automatizados na garantia de qualidade contínua e na prevenção de regressões em bases legadas; diferenciar testes manuais pontuais de suites de testes unitários determinísticas e repetíveis; assimilar a anatomia moderna do JUnit 5 (separação entre *Platform*, *Jupiter* e *Vintage*).
- **Técnico:** Estruturar classes de teste seguindo o padrão AAA (*Arrange, Act, Assert*); dominar as anotações fundamentais de ciclo de vida (`@Test`, `@BeforeEach`, `@AfterEach`, `@BeforeAll`, `@AfterAll`, `@DisplayName`); aplicar asserções nativas com `org.junit.jupiter.api.Assertions` (`assertEquals`, `assertTrue`, `assertFalse`, `assertNotNull`, `assertAll`); validar o disparo controlado de exceções de domínio via `assertThrows`.
- **Arquitetural:** Separar rigorosamente a árvore de código de produção (`src/main/java`) do código de teste (`src/test/java`); implementar testes parametrizados (`@ParameterizedTest` com `@ValueSource` e `@CsvSource`) para cobrir múltiplos cenários de borda sem duplicação de métodos.
- **Prático:** Implementar uma suite de testes unitários para um motor de concessão de crédito e gestão de contas correntes corporativas, validando saldos, regras de saque com tarifas e rejeição de entradas inválidas.

## 2. Conteúdo Teórico Detalhado

### 2.1 O Que é um Teste Unitário e Por Que Automatizar?

No desenvolvimento de software corporativo, testar manualmente regras de negócio executando o método `main` e imprimindo resultados via `System.out.println` é ineficiente e propenso a falhas:

- **Falta de Repetibilidade:** O teste manual depende de intervenção humana e não pode ser executado automaticamente em esteiras de integração contínua (CI/CD).
- **Ausência de Oráculo:** `System.out.println` exige que o desenvolvedor leia o console e decida manualmente se o resultado está correto.
- **Risco de Regressão:** Quando uma alteração em uma classe quebra silenciosamente outra funcionalidade pré-existente, apenas suites de testes automatizados conseguem acusar o defeito imediatamente.

Um **Teste Unitário** é um bloco de código que isola e avalia a menor unidade testável da aplicação (tipicamente um método ou classe de domínio) em um ambiente controlado e determinístico.

### 2.2 A Arquitetura Modular do JUnit 5

Diferente do legado JUnit 4 (arquitetura monolítica), o JUnit 5 foi reconstruído do zero de forma modular:

| Módulo / Componente | Responsabilidade Técnica |
|---|---|
| **JUnit Platform** | Fundação responsável por lançar e executar testes em IDE e Maven/Gradle |
| **JUnit Jupiter** | Motor e modelo de programação atual (anotações `@Test` e asserções novas) |
| **JUnit Vintage** | Garante compatibilidade reversa para rodar testes antigos do JUnit 3 e 4 |

### 2.3 Organização de Diretórios Corporativa Padrão

Em projetos estruturados (gerenciados por Maven ou Gradle), adota-se a convenção de espelhamento de pacotes:

```
meu-projeto/
  src/
    main/
      java/
        br/edu/universidade/sistema/banco/
          ContaCorrente.java        <- Código de Produção
    test/
      java/
        br/edu/universidade/sistema/banco/
          ContaCorrenteTest.java    <- Suite de Teste Unitário
  pom.xml (ou build.gradle)
```

Essa separação garante que bibliotecas de teste (como JUnit e Mockito) nunca sejam empacotadas no arquivo executável final distribuído em servidores de produção.

### 2.4 O Ciclo de Vida das Anotações do JUnit Jupiter

A cada método de teste executado, a JVM cria uma nova instância da classe de teste no Heap por padrão, assegurando que um teste nunca contamine o estado do outro:

1. **`@BeforeAll`** — Executa UMA ÚNICA VEZ antes de todos os testes (método estático).
2. **`@BeforeEach`** — Executa ANTES de cada teste individual (comum para prepare o cenário).
3. **`@Test`** — Método de teste executado.
4. **`@AfterEach`** — Executa DEPOIS de cada teste individual.
5. **`@AfterAll`** — Executa UMA ÚNICA VEZ ao final de tudo (método estático).

### 2.5 O Padrão Estrutural AAA (Arrange, Act, Assert)

- **Arrange (Preparação):** Instancia os objetos necessários e define o cenário inicial.
- **Act (Ação / Execução):** Invoca rigorosamente a operação de negócio sob teste.
- **Assert (Asserção / Verificação):** Compara o estado final obtido com o resultado esperado via `Assertions`.

### 2.6 Validação de Exceções com assertThrows

Para testar se uma classe aplica regras de guarda e rejeita operações inválidas:

```java
IllegalStateException ex = Assertions.assertThrows(
        IllegalStateException.class,
        () -> conta.sacar(1000.00)
);
Assertions.assertTrue(ex.getMessage().contains("Saldo insuficiente"));
```

O teste passará se a lambda disparar a exceção esperada; caso contrário, o teste falha.

## 3. Estudo de Caso Aplicado

O estudo de caso implementa um motor de conta corrente corporativa e sua respectiva suite de testes unitários, contendo asserções simples, agrupadas, validações de exceção e testes parametrizados.

**Estrutura de pacotes (exemplos/aula-21/src):**

```
br.edu.universidade.sistema.banco
  |-- ContaCorrente.java         (classe de produção)
  |-- ContaCorrenteTest.java     (suite de testes)
```

A classe `ContaCorrente` em `src/main/java` implementa regras rigorosas:

- Construtor que valida número de conta não vazio e saldo inicial não negativo.
- Constante `TARIFA_SAQUE = 2.50` cobrada em cada saque.
- `depositar()` e `sacar()` com validação de valores positivos; o saque verifica saldo suficiente incluindo a tarifa.
- `encerrarConta()` recusa encerramento com saldo pendente; contas inativas recusam qualquer operação via `validarContaAtiva()`.

A suite `ContaCorrenteTest` usa `@BeforeEach` para criar uma conta com saldo de R$ 500,00 antes de cada teste, garantindo isolamento. Os testes incluem:

- `deveInicializarContaComSucesso`: usa `assertAll` para checar número, saldo e status ativo em uma única asserção agrupada.
- `deveRealizarDepositoComSucesso`: valida saldo 650,00 após depósito de 150,00.
- `deveRealizarSaqueComCobrancaDeTarifa`: valida 397,50 = 500,00 - 100,00 - 2,50.
- `deveLancarExcecaoQuandoSaldoInsuficiente`: usa `assertThrows` e verifica a mensagem.
- `deveRejeitarDepositosInvalidos`: `@ParameterizedTest` com `@ValueSource` para rejeitar 0.0, -10.0 e -500.0.
- `deveCalcularDiferentesSaques`: `@ParameterizedTest` com `@CsvSource` para múltiplos cenários.
- Testes de encerramento com e sem saldo remanescente.

## 4. Exercícios Propostos e Solução

O exercício propõe construir uma classe de negócio `PropostaCredito` e sua suite de testes JUnit 5, seguindo o layout Maven.

**Estrutura da solução (solucoes/aula-21/src):**

```
src/
  main/java/br/edu/universidade/sistema/credito/PropostaCredito.java
  test/java/br/edu/universidade/sistema/credito/PropostaCreditoTest.java
```

A classe de produção `PropostaCredito`:

```java
package br.edu.universidade.sistema.credito;

public class PropostaCredito {
    private final String cpfCliente;
    private final double rendaMensal;
    private final double valorSolicitado;
    private final int quantidadeMeses;

    public PropostaCredito(String cpfCliente, double rendaMensal, double valorSolicitado, int quantidadeMeses) {
        if (cpfCliente == null || cpfCliente.trim().isEmpty()) {
            throw new IllegalArgumentException("CPF do cliente nao pode ser nulo ou vazio.");
        }
        if (rendaMensal <= 0.0) {
            throw new IllegalArgumentException("Renda mensal deve ser maior que zero: " + rendaMensal);
        }
        if (valorSolicitado <= 0.0) {
            throw new IllegalArgumentException("Valor solicitado deve ser maior que zero: " + valorSolicitado);
        }
        if (quantidadeMeses <= 0) {
            throw new IllegalArgumentException("Quantidade de meses deve ser maior que zero: " + quantidadeMeses);
        }
        this.cpfCliente = cpfCliente;
        this.rendaMensal = rendaMensal;
        this.valorSolicitado = valorSolicitado;
        this.quantidadeMeses = quantidadeMeses;
    }

    public double calcularValorParcelaMensal() {
        double montante = this.valorSolicitado * 1.05; // 5% de encargos
        return montante / this.quantidadeMeses;
    }

    public boolean isAprovada() {
        double parcela = calcularValorParcelaMensal();
        return parcela <= 0.30 * this.rendaMensal; // máx. 30% da renda
    }

    public String getCpfCliente() { return cpfCliente; }
    public double getRendaMensal() { return rendaMensal; }
    public double getValorSolicitado() { return valorSolicitado; }
    public int getQuantidadeMeses() { return quantidadeMeses; }
}
```

A suite de testes `PropostaCreditoTest`:

```java
package br.edu.universidade.sistema.credito;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Suite de Testes Unitarios - PropostaCredito")
class PropostaCreditoTest {

    private PropostaCredito propostaPadrao;

    @BeforeEach
    void setUp() {
        propostaPadrao = new PropostaCredito("111.222.333-04", 5000.00, 10000.00, 24);
    }

    @Test
    @DisplayName("Deve aprovar proposta elegivel (parcela <= 30% da renda)")
    void deveAprovarPropostaElegivel() {
        assertTrue(propostaPadrao.isAprovada(),
                "Proposta com parcela de R$ 437,50 contra renda de R$ 5.000 deveria ser aprovada");
    }

    @Test
    @DisplayName("Deve recusar proposta cuja parcela excede 30% da renda")
    void deveRecusarPropostaComComprometimentoExcessivo() {
        PropostaCredito pesada = new PropostaCredito("444.555.666-07", 3000.00, 100000.00, 36);
        assertFalse(pesada.isAprovada(),
                "Parcela acima de 30% da renda deve tornar a proposta inelegivel");
    }

    @Test
    @DisplayName("Deve lancar IllegalArgumentException para renda mensal menor ou igual a zero")
    void deveLancarExcecaoParaRendaInvalida() {
        assertThrows(IllegalArgumentException.class,
                () -> new PropostaCredito("111.222.333-04", 0.0, 10000.00, 24));
        assertThrows(IllegalArgumentException.class,
                () -> new PropostaCredito("111.222.333-04", -500.0, 10000.00, 24));
    }

    @ParameterizedTest(name = "Parcela de {0} em {1} meses deve ser {2}")
    @CsvSource({
            "12000.00, 24, 525.00",
            "6000.00, 12, 525.00",
            "10000.00, 10, 1050.00"
    })
    @DisplayName("Deve calcular a parcela mensal com 5% de encargos (delta 0.01)")
    void deveCalcularParcelaMensal(double valorSolicitado, int meses, double parcelaEsperada) {
        PropostaCredito proposta = new PropostaCredito("123.456.789-00", 8000.00, valorSolicitado, meses);
        double parcela = proposta.calcularValorParcelaMensal();
        assertEquals(parcelaEsperada, parcela, 0.01);
    }
}
```

Cálculos verificados: para a proposta padrão (10.000 em 24 meses), montante = 10.500,00 e parcela = 437,50, o que representa 8,75% da renda de 5.000,00, portanto aprovada. Para o caso pesado (100.000 em 36 meses), parcela = 2.916,67 contra renda de 3.000,00 (97,2%), logo recusada. No teste parametrizado, por exemplo, 12.000 em 24 meses gera parcela 525,00, e 6.000 em 12 meses também 525,00, e 10.000 em 10 meses 1.050,00, todos validados com delta de 0.01.

## 5. Perguntas de Revisão

1. Por que testes automatizados são superiores à verificação manual via `System.out.println`?
2. Quais são os três módulos do JUnit 5 e qual a função de cada um?
3. Por que a estrutura de diretórios separa `src/main/java` de `src/test/java`?
4. Explique o padrão AAA e aplique-o ao teste de saque com tarifa.
5. O que acontece se dois testes compartilham uma instância estática e dependem de ordem de execução? Qual é a regra de ouro?
6. Por que é necessário passar delta (tolerância) em `assertEquals` para valores `double`?
7. Qual a vantagem do `@ParameterizedTest` com `@CsvSource` em relação a escrever vários `@Test`?

## 6. Resumo / Pontos-Chave

- **Teste unitário** isola e avalia a menor unidade testável da aplicação, de forma determinística e repetível em esteiras CI/CD.
- **JUnit 5** é modular: Platform executa, Jupiter fornece o modelo de programação atual e Vintage garante compatibilidade com JUnit 3/4.
- A convenção **Maven/Gradle** separa estritamente código de produção (`src/main/java`) de código de teste (`src/test/java`).
- As anotações de ciclo de vida (`@BeforeAll`/`@AfterAll`/`@BeforeEach`/`@AfterEach`) controlam a preparação e limpeza de cada teste; cada teste roda em uma nova instância da classe de teste.
- O padrão **AAA (Arrange, Act, Assert)** organiza a legibilidade e a manutenção dos métodos de teste.
- **`assertThrows`** valida o disparo controlado de exceções de domínio e permite auditar a mensagem capturada.
- **`@ParameterizedTest`** com `@ValueSource` e `@CsvSource` cobre múltiplos cenários sem duplicação de código.
- Sempre coloque o valor esperado como primeiro argumento de `assertEquals` e informe delta para comparações de ponto flutuante.