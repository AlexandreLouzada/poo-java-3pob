# Tutorial de Java — Aula 10: Criação de Exceções Customizadas, Encadeamento de Falhas e Validações de Domínio

| | |
|---|---|
| **Disciplina** | Programação Orientada a Objetos |
| **Curso** | Análise e Desenvolvimento de Sistemas (3º Período) |
| **Tema** | Exceções Customizadas (Checked vs. Unchecked), Cláusulas throw e throws, Encadeamento de Falhas (Exception Chaining) e Defesa de Invariantes de Negócio |
| **Carga Horária** | 100 minutos (2 horas-aula) |
| **Professor Responsável** | Alexandre Neves Louzada |
| **Arquivo de Referência** | `TutorialAula10.md` |

## 1. Objetivos de Aprendizagem

- **Conceitual:** Compreender o papel das exceções customizadas de domínio na modelagem de software corporativo; discernir tecnicamente quando estender `java.lang.Exception` (falhas de negócio previsíveis) ou `java.lang.RuntimeException` (violações de pré-condições ou regras de integridade inegociáveis); compreender o padrão de encapsulamento e encadeamento de causas (*Exception Chaining*).
- **Técnico:** Criar classes de exceção especializadas contendo metadados de auditoria; utilizar rigorosamente as palavras-chave `throw` (disparo deliberado) e `throws` (declaração formal de contrato na assinatura do método); implementar construtores que recebam a causa raiz (`Throwable cause`).
- **Arquitetural:** Evitar o antipadrão de expor exceções técnicas de baixo nível (como `SQLException` ou `IOException`) diretamente para as camadas de apresentação ou consumidores da API, encapsulando-as em exceções de serviço ricas sem perder o rastreio da falha original.
- **Prático:** Implementar um motor de transferências bancárias e operações de crédito com validações rigorosas de saldo, limites de conta e regras cadastrais, blindando as entidades com exceções personalizadas de negócio.

## 2. Fundamentação Teórica

### Por que Criar Exceções Customizadas de Domínio?

A biblioteca padrão do Java fornece dezenas de exceções prontas (como `IllegalArgumentException`, `NullPointerException` e `IllegalStateException`). Contudo, em arquiteturas corporativas de médio e grande porte, utilizar apenas exceções genéricas apresenta limitações graves:

- **Falta de Expressividade Semântica:** Capturar `IllegalArgumentException` não esclarece se o erro decorreu de um CPF nulo, um saldo bancário abaixo do mínimo ou uma data de validade expirada.
- **Ausência de Metadados de Negócio:** Exceções genéricas transportam apenas uma mensagem em formato `String`. Exceções customizadas podem conter atributos próprios (como códigos de erro do sistema, números de protocolo, saldo atual e valor tentado), permitindo que a camada de controle trate o incidente de forma programática.
- **Granularidade no Tratamento:** Classes de exceção próprias permitem capturas cirúrgicas via blocos `catch` seletivos, sem interceptar falhas não intencionais da JVM.

### Decisão de Projeto: Herdar de Exception ou de RuntimeException?

A escolha da superclasse define como o compilador `javac` interagirá com o desenvolvedor que consumir o método:

```plaintext
                     java.lang.Throwable
                              ▲
                              │
                     java.lang.Exception
                              ▲
           ┌──────────────────┴──────────────────┐
           │                                     │
Exceção Customizada Checked           java.lang.RuntimeException
(Herda direto de Exception)                      ▲
• Compilador exige tratamento                  │
• Falhas previsíveis de negócio       Exceção Customizada Unchecked
• Obriga uso de 'throws'              (Herda de RuntimeException)
                                      • Tratamento opcional na assinatura
                                      • Violação de regras de guarda internas
```

| Critério de Escolha | Exceção Customizada Checked | Exceção Customizada Unchecked |
|---|---|---|
| Classe Herdada | `extends Exception` | `extends RuntimeException` |
| Exigência do Compilador | Exige `try-catch` ou declaração `throws` na assinatura | Não exige declaração no método nem captura obrigatória |
| Cenário de Aplicação | O chamador tem como tomar uma ação corretiva alternativa (ex.: `SaldoInsuficienteException` sugerindo uso de cheque especial) | Falha grave de integridade, violação de precondição ou inconsistência lógica irrecuperável (ex.: `ContaBloqueadaException`) |

### As Palavras-Chave throw e throws

É crucial não confundir esses dois modificadores da linguagem:

- **`throw` (Ação de Lançar):** Verbo imperativo utilizado no corpo do método para instanciar e arremessar uma anomalia na pilha de execução:
  ```java
  throw new SaldoInsuficienteException("Saldo insuficiente para efetuar o saque.");
  ```
  A execução da linha atual é interrompida imediatamente e a JVM inicia o desenrolamento da pilha (*Stack Unwinding*).

- **`throws` (Contrato de Assinatura):** Declaração explícita no cabeçalho do método que avisa aos chamadores que aquela rotina pode lançar uma ou mais exceções checadas (*Checked*):
  ```java
  public void transferir(double valor, Conta destino) throws SaldoInsuficienteException, OperacaoNaoPermitidaException { ... }
  ```
  Exceções não-checadas (*Unchecked*) não exigem a cláusula `throws`, embora possam ser documentadas via tag `@throws` do Javadoc.

### Encadeamento de Exceções (Exception Chaining) e Causa Raiz

Em camadas corporativas, um repositório ou serviço de integração pode capturar uma falha técnica de baixo nível (como uma `SQLException` ao abrir uma transação). Expor essa falha diretamente para as camadas superiores quebra o isolamento arquitetural e vaza detalhes de banco de dados para a interface de usuário.

A prática recomendada consiste em capturar a falha técnica e relançar uma exceção de serviço de mais alto nível, repassando a exceção original como argumento do construtor (*cause*):

```java
try {
    driver.gravarRegistro(dados);
} catch (SQLException ex) {
    // Encapsula a falha de SQL em uma exceção de negócio, mantendo o rastro original
    throw new ServicoFinanceiroException("Falha ao persistir transação no livro contábil.", ex);
}
```

Ao imprimir o *Stack Trace*, a JVM exibirá a nova exceção no topo e incluirá a seção `Caused by: java.sql.SQLException...`, permitindo auditar o erro de infraestrutura sem acoplar a assinatura dos métodos às classes de banco.

## 3. Estudo de Caso Integrado: Módulo de Cobrança e Transferências

O projeto a seguir implementa classes de exceção com atributos de auditoria, encadeamento de falhas e regras de guarda em operações financeiras:

```java
package br.edu.universidade.sistema.financeiro.exception;

// 1. Exceção de Negócio Checada (Checked): O chamador deve tratar
public class SaldoInsuficienteException extends Exception {
    private final double saldoDisponivel;
    private final double valorTentado;

    public SaldoInsuficienteException(String mensagem, double saldoDisponivel, double valorTentado) {
        super(mensagem);
        this.saldoDisponivel = saldoDisponivel;
        this.valorTentado = valorTentado;
    }

    public double getSaldoDisponivel() {
        return saldoDisponivel;
    }

    public double getValorTentado() {
        return valorTentado;
    }
}
```

```java
package br.edu.universidade.sistema.financeiro.exception;

// 2. Exceção de Domínio Não-Checada (Unchecked): Violação de Integridade
public class ContaBloqueadaException extends RuntimeException {
    private final String numeroConta;

    public ContaBloqueadaException(String mensagem, String numeroConta) {
        super(mensagem);
        this.numeroConta = numeroConta;
    }

    public ContaBloqueadaException(String mensagem, String numeroConta, Throwable causa) {
        super(mensagem, causa); // Preserva o encadeamento de falha
        this.numeroConta = numeroConta;
    }

    public String getNumeroConta() {
        return numeroConta;
    }
}
```

```java
package br.edu.universidade.sistema.financeiro.dominio;

import br.edu.universidade.sistema.financeiro.exception.ContaBloqueadaException;
import br.edu.universidade.sistema.financeiro.exception.SaldoInsuficienteException;

// 3. Entidade de Domínio aplicando regras de guarda e disparando exceções
public class ContaCorrente {
    private final String numero;
    private double saldo;
    private boolean ativa;

    public ContaCorrente(String numero, double saldoInicial) {
        this.numero = numero;
        this.saldo = Math.max(0.0, saldoInicial);
        this.ativa = true;
    }

    public void bloquearConta() {
        this.ativa = false;
    }

    public void depositar(double valor) {
        if (!this.ativa) {
            throw new ContaBloqueadaException("Não é permitido depósito em conta inativa.", this.numero);
        }
        if (valor <= 0.0) {
            throw new IllegalArgumentException("O valor do depósito deve ser estritamente positivo.");
        }
        this.saldo += valor;
    }

    // Declara explicitamente a exceção checada no contrato do método
    public void sacar(double valor) throws SaldoInsuficienteException {
        if (!this.ativa) {
            throw new ContaBloqueadaException("Não é permitido saque em conta inativa.", this.numero);
        }
        if (valor <= 0.0) {
            throw new IllegalArgumentException("O valor de saque deve ser superior a zero.");
        }
        if (this.saldo < valor) {
            // Disparo com passagem de metadados para auditoria
            throw new SaldoInsuficienteException(
                String.format("Tentativa de débito de R$ %.2f excede o saldo atual de R$ %.2f.", valor, this.saldo),
                this.saldo,
                valor
            );
        }
        this.saldo -= valor;
    }

    public double getSaldo() {
        return saldo;
    }

    public String getNumero() {
        return numero;
    }
}
```

```java
package br.edu.universidade.sistema.financeiro.service;

import br.edu.universidade.sistema.financeiro.dominio.ContaCorrente;
import br.edu.universidade.sistema.financeiro.exception.ContaBloqueadaException;
import br.edu.universidade.sistema.financeiro.exception.SaldoInsuficienteException;

// 4. Camada de Serviço orquestrando transferências de forma resiliente
public class TransferenciaService {

    public void transferir(ContaCorrente origem, ContaCorrente destino, double valor) {
        System.out.printf("Iniciando transferência de R$ %.2f [Conta %s -> Conta %s]...%n",
                valor, origem.getNumero(), destino.getNumero());

        try {
            origem.sacar(valor);
            destino.depositar(valor);
            System.out.printf("[SUCESSO] Transferência concluída! Saldo atualizado da origem: R$ %.2f%n",
                    origem.getSaldo());

        } catch (SaldoInsuficienteException ex) {
            // Tratamento especializado acessando os atributos da exceção própria
            System.err.println("[FALHA DE SALDO] " + ex.getMessage());
            System.err.printf("Auditoria: Saldo em conta: R$ %.2f | Valor solicitado: R$ %.2f | Déficit: R$ %.2f%n",
                    ex.getSaldoDisponivel(), ex.getValorTentado(), (ex.getValorTentado() - ex.getSaldoDisponivel()));

        } catch (ContaBloqueadaException ex) {
            // Tratamento para conta inativa
            System.err.println("[SEGURANÇA] Operação cancelada. " + ex.getMessage());
            System.err.println("Conta sinalizada para o departamento de compliance: " + ex.getNumeroConta());

        } catch (IllegalArgumentException ex) {
            // Tratamento de parâmetros ilegais
            System.err.println("[VALOR INVÁLIDO] " + ex.getMessage());
        }
    }
}
```

```java
package br.edu.universidade.sistema.financeiro;

import br.edu.universidade.sistema.financeiro.dominio.ContaCorrente;
import br.edu.universidade.sistema.financeiro.service.TransferenciaService;

// 5. Execução demonstrando os diferentes cenários de captura
public class OperacoesApp {
    public static void main(String[] args) {
        ContaCorrente c1 = new ContaCorrente("001-A", 500.00);
        ContaCorrente c2 = new ContaCorrente("002-B", 100.00);
        TransferenciaService service = new TransferenciaService();

        System.out.println("--- Cenário 1: Transferência Válida ---");
        service.transferir(c1, c2, 200.00);

        System.out.println("\n--- Cenário 2: Saldo Insuficiente (Checked Exception) ---");
        service.transferir(c1, c2, 400.00);

        System.out.println("\n--- Cenário 3: Conta Inativa (Unchecked Exception) ---");
        c2.bloquearConta();
        service.transferir(c1, c2, 50.00);

        System.out.println("\nO sistema permaneceu em execução contínua sem quebras de processo.");
    }
}
```

## 4. Diagnóstico de Erros Comuns e Armadilhas

### Armadilha 1: Declarar Exceção Checada sem a Cláusula throws no Método

**Código Problemático:**

```java
public void validarOperacao(double v) { // Falta o 'throws'!
    if (v < 0) {
        throw new MinhaExcecaoChecked("Valor negativo"); // ERRO DE COMPILAÇÃO!
    }
}
```

- **Diagnóstico da JVM:** `unreported exception MinhaExcecaoChecked; must be caught or declared to be thrown`.
- **Causa & Correção:** Se a exceção herda diretamente de `Exception`, o compilador força o contrato: o método deve capturá-la internamente em um bloco `try-catch` ou declarar `throws MinhaExcecaoChecked` no cabeçalho.

### Armadilha 2: Perda da Causa Raiz ao Relançar Nova Exceção

**Código Problemático:**

```java
try {
    realizarLeituraRede();
} catch (IOException ex) {
    throw new ServicoException("Erro no serviço"); // Construtor sem a causa original!
}
```

- **Impacto:** O *Stack Trace* original da `IOException` (com o endereço IP, linha exata e porta de rede) é destruído e substituído pelo frame onde a `ServicoException` foi criada, dificultando a depuração em produção.
- **Correção:** Passe a exceção interceptada para o construtor da nova exceção: `throw new ServicoException("Erro no serviço", ex);`.

### Armadilha 3: Criar Exceções sem Construtores Parametrizados Adequados

**Código Problemático:**

```java
public class RegraNegocioException extends Exception {
    // Nenhum construtor declarado: herda apenas o construtor vazio default!
}
```

- **Impacto:** Torna impossível instanciar a classe passando mensagens detalhadas (`new RegraNegocioException("Mensagem")`) ou amarrar causas raiz (`Throwable cause`).
- **Correção:** Sempre declare ao menos os construtores clássicos recebendo `(String mensagem)` e `(String mensagem, Throwable causa)`.

## 5. Roteiro Prático de Depuração: Inspecionando a Causa Raiz na IDE

Para rastrear o encadeamento de falhas (*Exception Chaining*) no depurador da sua IDE (IntelliJ IDEA, Eclipse ou VS Code):

1. Crie uma rotina onde um método lance uma `NumberFormatException` proposital.
2. No bloco `catch`, lance uma exceção customizada `TransacaoInvalidaException(msg, ex)` repassando a falha como causa raiz.
3. Posicione um ponto de interrupção (*breakpoint*) na linha do `throw` da sua nova exceção.
4. Inicie o programa em modo de depuração (*Debug*).
5. Abra a janela de variáveis (*Variables*) e expanda a instância da sua nova exceção:
   - Localize o campo `cause` dentro do objeto.
   - Observe que a instância de `NumberFormatException` está encapsulada dentro dele, mantendo os quadros originais da falha primária intactos.
6. Deixe o programa imprimir o rastreio no console e observe a presença da instrução `Caused by: java.lang.NumberFormatException...`, comprovando o encadeamento de integridade.

## 6. Exercício de Fixação Prática: Sistema de Análise de Crédito e Financiamento

Implemente um motor de concessão de empréstimos corporativos aplicando o padrão de exceções customizadas:

1. **Construa a Exceção Checada `LimiteCreditoExcedidoException`:**
   - Deve estender `java.lang.Exception`.
   - Atributos de auditoria encapsulados: `rendaMensal` (`double`), `valorParcelaPretendida` (`double`) e `percentualComprometimento` (`double`).
   - Construtor parametrizado completo repassando a mensagem para `super(mensagem)`.
   - Métodos acessores (*getters*) para os campos.

2. **Construa a Exceção Não-Checada `ScoreSerasaInvalidoException`:**
   - Deve estender `java.lang.RuntimeException`.
   - Atributo encapsulado: `pontuacaoInformada` (`int`).
   - Construtores recebendo mensagem e causa raiz.

3. **Construa a Classe `PropostaFinanciamento`:**
   - Atributos privados: `cpfCliente` (`String`), `rendaMensal` (`double`), `valorEmprestimo` (`double`) e `quantidadeMeses` (`int`).
   - Construtor completo com validações defensivas.
   - Método de cálculo `double calcularParcela()`: divide o valor do empréstimo pelo número de meses com acréscimo de taxa fixa de 10% no montante.
   - Método de negócio `void validarAprovacao(int scoreSerasa)`:
     - Se o `scoreSerasa` estiver fora do intervalo de 0 a 1000, lance `ScoreSerasaInvalidoException`.
     - Se o score for menor que 400, lance `ScoreSerasaInvalidoException("Proposta recusada: score insuficiente para concessão", scoreSerasa)`.
     - Calcule a parcela; se o valor da parcela exceder 30% da renda mensal do proponente, lance a exceção checada `LimiteCreditoExcedidoException`.

4. **Construa a Classe Executável `AnaliseCreditoApp`:**
   - Crie uma proposta com comprometimento de renda excessivo (ex.: parcela de R$ 2.000 para renda de R$ 4.000) e trate `LimiteCreditoExcedidoException`, emitindo laudo de auditoria com os valores.
   - Simule uma proposta informando score negativo (`-50`) e trate `ScoreSerasaInvalidoException`, demonstrando a resiliência do sistema.