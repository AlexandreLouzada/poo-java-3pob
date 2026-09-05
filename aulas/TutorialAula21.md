# Tutorial de Java — Aula 21: Testes Unitários Automatizados com JUnit 5

| | |
|---|---|
| **Disciplina** | Programação Orientada a Objetos |
| **Curso** | Análise e Desenvolvimento de Sistemas (3º Período) |
| **Tema** | Arquitetura do JUnit 5 (Jupiter), Ciclo de Vida dos Testes (`@Test`, `@BeforeEach`, `@AfterEach`, `@BeforeAll`, `@AfterAll`), Asserções Fluentes, Validação de Exceções (`assertThrows`), Testes Parametrizados (`@ParameterizedTest`) e Padrão AAA (*Arrange, Act, Assert*) |
| **Carga Horária** | 100 minutos (2 horas-aula) |
| **Professor Responsável** | Alexandre Neves Louzada |
| **Arquivo de Referência** | `TutorialAula21.md` |

## 1. Objetivos de Aprendizagem

- **Conceitual:** Compreender o papel dos testes automatizados na garantia de qualidade contínua e na prevenção de regressões em bases legadas; diferenciar testes manuais pontuais de suites de testes unitários determinísticas e repetíveis; assimilar a anatomia moderna do JUnit 5 (separação entre *Platform*, *Jupiter* e *Vintage*).
- **Técnico:** Estruturar classes de teste seguindo o padrão AAA (*Arrange, Act, Assert*); dominar as anotações fundamentais de ciclo de vida (`@Test`, `@BeforeEach`, `@AfterEach`, `@BeforeAll`, `@AfterAll`, `@DisplayName`); aplicar asserções nativas com `org.junit.jupiter.api.Assertions` (`assertEquals`, `assertTrue`, `assertFalse`, `assertNotNull`, `assertAll`); validar o disparo controlado de exceções de domínio via `assertThrows`.
- **Arquitetural:** Separar rigorosamente a árvore de código de produção (`src/main/java`) do código de teste (`src/test/java`) conforme os padrões de engenharia de software corporativos; implementar testes parametrizados (`@ParameterizedTest` com `@ValueSource` e `@CsvSource`) para cobrir múltiplos cenários de borda sem duplicação de métodos.
- **Prático:** Implementar uma suite de testes unitários para um motor de concessão de crédito e gestão de contas correntes corporativas, validando saldos, regras de saque com tarifas e rejeição de entradas inválidas.

## 2. Fundamentação Teórica

### O Que é um Teste Unitário e Por Que Automatizar?

No desenvolvimento de software corporativo, testar manualmente regras de negócio executando o método `main` e imprimindo resultados via `System.out.println` é ineficiente e propenso a falhas:

- **Falta de Repetibilidade:** O teste manual depende de intervenção humana e não pode ser executado automaticamente em esteiras de integração contínua (CI/CD).
- **Ausência de Oráculo:** `System.out.println` exige que o desenvolvedor leia o console e decida manualmente se o resultado está correto.
- **Risco de Regressão:** Quando uma alteração em uma classe quebra silenciosamente outra funcionalidade pré-existente, apenas suites de testes automatizados conseguem acusar o defeito imediatamente.

Um **Teste Unitário** é um bloco de código que isola e avalia a menor unidade testável da aplicação (tipicamente um método ou classe de domínio) em um ambiente controlado e determinístico.

### A Arquitetura Modular do JUnit 5

Diferente do legado JUnit 4 (que continha uma arquitetura monolítica), o JUnit 5 foi reconstruído a partir do zero de forma modular:

> JUnit 5 = JUnit Platform + JUnit Jupiter + JUnit Vintage

```plaintext
┌──────────────────────────────────────┬──────────────────────────────────────┐
│ Módulo / Componente                  │ Responsabilidade Técnica             │
├──────────────────────────────────────┼──────────────────────────────────────┤
│ JUnit Platform                       │ Fundação responsável por lançar e    │
│                                      │ executar testes em IDE e Maven/Gradle│
├──────────────────────────────────────┼──────────────────────────────────────┤
│ JUnit Jupiter                        │ Motor e modelo de programação atual  │
│                                      │ anotações (@Test) e asserções novas  │
├──────────────────────────────────────┼──────────────────────────────────────┤
│ JUnit Vintage                        │ Garante compatibilidade reversa para │
│                                      │ rodar testes antigos do JUnit 3 e 4  │
└──────────────────────────────────────┴──────────────────────────────────────┘
```

### Organização de Diretórios Corporativa Padrão

Em projetos estruturados (gerenciados por ferramentas como Apache Maven ou Gradle), adota-se a convenção de espelhamento de pacotes:

```plaintext
meu-projeto/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── br/edu/universidade/sistema/banco/
│   │           └── ContaCorrente.java        <-- Código de Produção
│   └── test/
│       └── java/
│           └── br/edu/universidade/sistema/banco/
│               └── ContaCorrenteTest.java    <-- Suite de Teste Unitário
└── pom.xml (ou build.gradle)
```

Essa separação garante que bibliotecas de teste (como JUnit e Mockito) nunca sejam empacotadas no arquivo executável final (.jar ou .war) distribuído em servidores de produção.

### O Ciclo de Vida das Anotações do JUnit Jupiter

A cada método de teste executado, a JVM cria uma nova instância da classe de teste no Heap por padrão, assegurando que um teste nunca contamine o estado do outro (isolamento total entre testes):

```plaintext
[@BeforeAll]  ──► Executa UMA ÚNICA VEZ antes de todos os testes (Método Estático)
     │
     ├─► [@BeforeEach]  ──► Executa ANTES de cada teste individual
     │        │
     │        ▼
     │   [@Test #1]     ──► Método de teste executado
     │        │
     │        ▼
     ├─► [@AfterEach]   ──► Executa DEPOIS de cada teste individual
     │
     ├─► [@BeforeEach]  ──► Executa novamente
     │        │
     │        ▼
     │   [@Test #2]     ──► Segundo teste executado
     │        │
     │        ▼
     ├─► [@AfterEach]   ──► Executa novamente
     │
[@AfterAll]   ──► Executa UMA ÚNICA VEZ ao final de tudo (Método Estático)
```

### O Padrão Estrutural AAA (Arrange, Act, Assert)

Para manter os métodos de teste legíveis, organizados e fáceis de manter por qualquer membro da equipe, utiliza-se a convenção AAA:

- **Arrange (Preparação):** Instancia os objetos necessários, define o cenário inicial e prepara as variáveis de entrada.
- **Act (Ação / Execução):** Invoca rigorosamente a operação de negócio ou método sob teste.
- **Assert (Asserção / Verificação):** Compara o estado final obtido com o resultado esperado através dos métodos de `Assertions`.

### Validação de Exceções com assertThrows

Para testar se uma classe aplica regras de guarda e rejeita operações inválidas disparando exceções de negócio, utiliza-se o método `Assertions.assertThrows()`:

```java
// O teste PASSARÁ se a lambda disparar a exceção esperada; se não disparar, o teste FALHA!
SaldoInsuficienteException ex = Assertions.assertThrows(
        SaldoInsuficienteException.class,
        () -> conta.sacar(1000.00)
);

// É possível auditar a mensagem interna da exceção capturada:
Assertions.assertEquals("Saldo insuficiente para débito.", ex.getMessage());
```

## 3. Estudo de Caso Integrado: Motor de Conta Bancária e Suite de Testes

O exemplo a seguir implementa uma classe de negócio de conta corrente corporativa e sua respectiva suite de testes unitários contendo asserções simples, agrupadas, validações de exceção e testes parametrizados:

```java
package br.edu.universidade.sistema.banco;

// 1. Classe de Produção sob Teste (Localizada em src/main/java)
public class ContaCorrente {
    private final String numeroConta;
    private double saldo;
    private boolean ativa;
    public static final double TARIFA_SAQUE = 2.50;

    public ContaCorrente(String numeroConta, double saldoInicial) {
        if (numeroConta == null || numeroConta.isBlank()) {
            throw new IllegalArgumentException("Número de conta obrigatório.");
        }
        if (saldoInicial < 0.0) {
            throw new IllegalArgumentException("Saldo inicial não pode ser negativo.");
        }
        this.numeroConta = numeroConta.trim();
        this.saldo = saldoInicial;
        this.ativa = true;
    }

    public void depositar(double valor) {
        validarContaAtiva();
        if (valor <= 0.0) {
            throw new IllegalArgumentException("O valor do depósito deve ser estritamente positivo.");
        }
        this.saldo += valor;
    }

    public void sacar(double valor) {
        validarContaAtiva();
        if (valor <= 0.0) {
            throw new IllegalArgumentException("O valor do saque deve ser superior a zero.");
        }
        double custoTotal = valor + TARIFA_SAQUE;
        if (custoTotal > this.saldo) {
            throw new IllegalStateException(
                String.format("Saldo insuficiente. Saldo atual: R$ %.2f, Custo total com tarifa: R$ %.2f",
                        this.saldo, custoTotal)
            );
        }
        this.saldo -= custoTotal;
    }

    public void encerrarConta() {
        if (this.saldo > 0.0) {
            throw new IllegalStateException("Contas com saldo pendente não podem ser encerradas.");
        }
        this.ativa = false;
    }

    private void validarContaAtiva() {
        if (!this.ativa) {
            throw new IllegalStateException("Operação recusada: conta inativa no sistema.");
        }
    }

    public String getNumeroConta() { return numeroConta; }
    public double getSaldo() { return saldo; }
    public boolean isAtiva() { return ativa; }
}
```

```java
package br.edu.universidade.sistema.banco;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

// 2. Suite de Testes Unitários Automatizados (Localizada em src/test/java)
@DisplayName("Suite de Testes Unitários - ContaCorrente")
class ContaCorrenteTest {

    private ContaCorrente conta;

    @BeforeEach
    void setUp() {
        // Arrange comum: executado antes de CADA teste para garantir isolamento
        conta = new ContaCorrente("1001-X", 500.00);
    }

    @Test
    @DisplayName("Deve inicializar conta com saldo e estado ativo válidos")
    void deveInicializarContaComSucesso() {
        // Assert agrupado com assertAll: executa todas as checagens mesmo se uma falhar
        assertAll("Validações de estado inicial da conta",
                () -> assertEquals("1001-X", conta.getNumeroConta(), "Número de conta incorreto"),
                () -> assertEquals(500.00, conta.getSaldo(), 0.001, "Saldo inicial incorreto"),
                () -> assertTrue(conta.isAtiva(), "A conta deveria iniciar com status ativo")
        );
    }

    @Test
    @DisplayName("Deve realizar depósito com sucesso atualizando o saldo")
    void deveRealizarDepositoComSucesso() {
        // Act
        conta.depositar(150.00);

        // Assert
        assertEquals(650.00, conta.getSaldo(), 0.001, "O saldo deveria ter sido acrescido de R$ 150.00");
    }

    @Test
    @DisplayName("Deve abater valor e tarifa de R$ 2.50 em saque com saldo suficiente")
    void deveRealizarSaqueComCobrancaDeTarifa() {
        // Act: Saldo inicial 500.00 - Saque 100.00 - Tarifa 2.50 = 397.50
        conta.sacar(100.00);

        // Assert
        assertEquals(397.50, conta.getSaldo(), 0.001);
    }

    @Test
    @DisplayName("Deve disparar IllegalStateException ao tentar sacar valor que excede o saldo")
    void deveLancarExcecaoQuandoSaldoInsuficiente() {
        // Act & Assert via assertThrows
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> conta.sacar(600.00),
                "Deveria ter lançado IllegalStateException para saque maior que o saldo"
        );

        // Valida se a mensagem técnica detalha o erro
        assertTrue(ex.getMessage().contains("Saldo insuficiente"));
    }

    @ParameterizedTest(name = "Depósito inválido: valor {0} deve lançar IllegalArgumentException")
    @ValueSource(doubles = { 0.0, -10.0, -500.0 })
    @DisplayName("Deve rejeitar valores não positivos de depósito")
    void deveRejeitarDepositosInvalidos(double valorInvalido) {
        assertThrows(IllegalArgumentException.class, () -> conta.depositar(valorInvalido));
    }

    @ParameterizedTest(name = "Cenário [{index}]: Saque {0} com tarifa deve deixar saldo {1}")
    @CsvSource({
            "100.00, 397.50",
            "200.00, 297.50",
            "497.50, 0.00"
    })
    @DisplayName("Deve calcular saques múltiplos com precisão via CsvSource")
    void deveCalcularDiferentesSaques(double valorSaque, double saldoEsperado) {
        conta.sacar(valorSaque);
        assertEquals(saldoEsperado, conta.getSaldo(), 0.001);
    }

    @Test
    @DisplayName("Deve impedir encerramento de conta com saldo positivo remanescente")
    void deveRejeitarEncerramentoComSaldo() {
        assertThrows(IllegalStateException.class, () -> conta.encerrarConta());
    }

    @Test
    @DisplayName("Deve encerrar conta com sucesso quando o saldo for exatamente zero")
    void deveEncerrarContaComSaldoZerado() {
        // Esvazia a conta considerando a tarifa (500 - 2.50 = 497.50)
        conta.sacar(497.50);
        assertEquals(0.00, conta.getSaldo(), 0.001);

        conta.encerrarConta();

        assertFalse(conta.isAtiva(), "A conta deveria estar inativa após encerramento");
    }
}
```

## 4. Diagnóstico de Erros Comuns e Armadilhas

### Armadilha 1: Usar Asserções Invertidas (expected vs. actual)

**Código Problemático:**

```java
// Inversão nos parâmetros do assertEquals:
assertEquals(conta.getSaldo(), 500.00); // ERRADO!
```

- **Diagnóstico Técnico:** A assinatura do método no JUnit é `assertEquals(expected, actual)`. Inverter a ordem não quebra o teste quando o valor bate, mas se o teste falhar, o relatório emitirá uma mensagem enganosa: "Expected: 450.00, Actual: 500.00", confundindo o desenvolvedor sobre qual era a expectativa de negócio.
- **Correção:** Passe sempre o valor esperado no primeiro argumento: `assertEquals(500.00, conta.getSaldo());`.

### Armadilha 2: Omissão da Tolerância (Delta) na Comparação de Ponto Flutuante (double/float)

**Código Problemático:**

```java
double valorCalculado = 0.1 + 0.2;
assertEquals(0.3, valorCalculado); // TESTE FALHA POR ERRO DE ARREDONDAMENTO IEEE 754!
```

- **Diagnóstico do JUnit:** `org.opentest4j.AssertionFailedError: expected: <0.3> but was: <0.30000000000000004>`.
- **Causa & Correção:** Operações de ponto flutuante em computadores possuem imprecisões binárias de representação. Sempre passe uma margem de tolerância (delta) no terceiro parâmetro: `assertEquals(0.3, valorCalculado, 0.0001);`.

### Armadilha 3: Criar Testes Dependentes de Ordem de Execução

**Código Problemático:**

```java
public class PedidoTest {
    private static Pedido pedido = new Pedido(); // Instância estática compartilhada!

    @Test void teste1_criarPedido() { pedido.adicionarItem("Notebook"); }
    @Test void teste2_fecharPedido() { pedido.fechar(); } // Depende do teste1 ter rodado antes!
}
```

- **Diagnóstico Arquitetural:** O JUnit não garante a ordem alfabética ou sequencial de execução dos métodos de teste por padrão. Se `teste2` for executado antes de `teste1`, a suite falhará.
- **Regra de Ouro:** Cada método `@Test` deve ser completamente autocontido, independente e isolado, preparando seu próprio cenário dentro do `@BeforeEach`.

## 5. Roteiro Prático de Execução e Diagnóstico de Falhas na IDE

Para auditar e depurar testes unitários na sua IDE (IntelliJ IDEA, Eclipse ou VS Code):

1. Abra a classe `ContaCorrenteTest`.
2. Clique no ícone de reprodução verde (*Run*) ao lado do nome da classe para executar toda a suite de testes.
3. Observe a janela de execução de testes (*Test Runner*):
   - Todos os testes devem ser exibidos com ícones verdes de sucesso (*Passed*).
4. Simule uma falha proposital: Altere temporariamente a constante de tarifa na classe de produção para `public static final double TARIFA_SAQUE = 5.00;`.
5. Reexecute o teste:
   - A IDE destacará o teste `deveRealizarSaqueComCobrancaDeTarifa` com um ícone vermelho de falha (*Failed*).
   - Clique duas vezes sobre a linha de erro: a IDE abrirá a ferramenta de comparação visual de diferenças (*Diff Viewer*), mostrando lado a lado o valor esperado (397.50) versus o valor real gerado (395.00).
6. Reverta a alteração para restaurar o estado estável da base de código.

## 6. Exercício de Fixação Prática: Testes Unitários de Validação Cadastral e Elegibilidade de Empréstimos

Implemente uma classe de negócio e sua respectiva suite de testes com JUnit 5:

1. **Construa a Classe de Produção `PropostaCredito` (em `src/main/java`):**
   - Atributos privados: `cpfCliente` (`String`), `rendaMensal` (`double`), `valorSolicitado` (`double`) e `quantidadeMeses` (`int`).
   - Construtor parametrizado completo rejeitando parâmetros negativos ou nulos via `IllegalArgumentException`.
   - **Método `double calcularValorParcelaMensal()`:** divide o valor solicitado pelos meses adicionando 5% fixos de encargos operacionais ao montante total.
   - **Método `boolean isAprovada()`:** retorna `true` se o valor da parcela calculada comprometer no máximo 30% da renda mensal do cliente; caso comprometa mais que 30%, retorna `false`.

2. **Construa a Classe de Testes `PropostaCreditoTest` (em `src/test/java`):**
   - Implemente o método `@BeforeEach` preparando uma proposta padrão elegível (Renda: R$ 5.000,00, Valor: R$ 10.000,00, Prazo: 24 meses).
   - **Teste 1:** Valide se uma proposta elegível retorna `true` no método `isAprovada()`.
   - **Teste 2:** Crie um teste que instancie uma proposta onde a parcela exceda os 30% da renda e use `assertFalse` para assegurar a recusa.
   - **Teste 3:** Use `assertThrows` validando se o construtor dispara `IllegalArgumentException` caso seja passada renda mensal menor ou igual a zero.
   - **Teste 4:** Implemente um `@ParameterizedTest` com `@CsvSource` contendo três combinações diferentes de valor e prazo, validando se o cálculo da parcela mensal bate exatamente com o valor esperado (utilizando margem delta de `0.01`).
