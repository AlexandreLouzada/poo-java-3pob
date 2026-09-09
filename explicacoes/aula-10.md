# Explicação da Aula 10 -- Sinalização Explícita de Falhas, Contratos de Métodos e Exceções Customizadas de Domínio

| | |
|---|---|
| **Disciplina** | Programação Orientada a Objetos |
| **Curso** | Análise e Desenvolvimento de Sistemas (3o Período) |
| **Tema** | Sinalização Explícita de Falhas (`throw`), Contratos de Métodos (`throws`) e Criação de Exceções Customizadas de Domínio |
| **Carga Horária** | 100 minutos (2 horas-aula) |
| **Professor** | Alexandre Neves Louzada |
| **Plano de Aula** | `Aulas 3pob/Aula 10.md` |
| **Tutorial** | `aulas/TutorialAula10.md` |
| **Estudo de Caso** | `exemplos/aula-10/` |
| **Exercícios Resolvidos** | `solucoes/aula-10/` |

## 1. Objetivos de Aprendizagem

Esta aula é a conclusão do Módulo 3, assumindo o papel de **autores de regras de negócio**. Enquanto nas Aulas 08 e 09 aprendemos a diagnosticar e capturar erros existentes na plataforma, na Aula 10 aprendemos quando e como o sistema deve rejeitar estados inválidos de forma estruturada e semântica.

**Objetivos conceituais:** Compreender o papel da sinalização semântica de falhas no design de APIs corporativas; eliminar retornos mágicos de erro (`-1`, `false`, `null`) em favor de exceções ricas de domínio; entender o princípio do desacoplamento entre camadas de serviço/negócio e camadas de apresentação.

**Objetivos técnicos:** Disparar ativamente exceções utilizando a instrução `throw`; declarar contratos de métodos com a cláusula `throws`; projetar e instanciar classes de exceção customizadas (Checked herdando de `Exception` e Unchecked herdando de `RuntimeException`).

**Objetivos arquiteturais:** Implementar a técnica de Encadeamento de Exceções (Exception Chaining) via construtor com `Throwable cause`, preservando a causa raiz técnica de baixo nível dentro de uma abstração elegante de negócio; enriquecer exceções customizadas com atributos contextuais imutáveis (`final`).

**Objetivos práticos:** Implementar um motor de autenticação e segurança bancária em camadas, disparando exceções específicas para usuário inexistente, credenciais incorretas e bloqueio definitivo por excesso de tentativas.

### 1.1 Metodologia Ativa

A aula utiliza **Modelagem Guiada de Regras de Domínio**, apresentando um caso financeiro real onde falhas de saldo ou limite não devem exibir mensagens no console via `System.out.println`, mas sim interromper o fluxo transacional através de exceções estruturadas com dados de auditoria. Também é feita uma **Refatoração de Código em Camadas**, transformando métodos acoplados à interface de usuário em serviços limpos que comunicam o insucesso operacional exclusivamente via contratos de exceções customizadas.

## 2. Conteúdo Teórico Detalhado

### 2.1 A Instrução `throw`: Interrompendo o Fluxo com Intencionalidade

A instrução `throw` é o comando utilizado para instanciar e arremessar **explicitamente** um objeto que herda de `Throwable`. Ao contrário das exceções implícitas (como `NullPointerException` lançada pela JVM quando acessamos uma referência nula), o `throw` é uma ação **intencional** do programador para sinalizar uma violação de regra de negócio.

Sintaxe básica:

```java
if (valor <= 0) {
    throw new IllegalArgumentException("O valor da operação deve ser positivo. Informado: " + valor);
}
```

**Efeito imediato:** A execução do método é interrompida na linha do `throw`. A JVM congela o estado atual, monta o Stack Trace e inicia a busca por um tratador (`catch`) na pilha de execução.

A diferença técnica entre `throw` e `throws`:

- `throw` (verbo no imperativo): a ação de **arremessar** o objeto de erro.
- `throws` (verbo no indicativo): a declaração na assinatura de que o método **pode arremessar** determinado tipo de erro.

### 2.2 A Cláusula `throws`: Transparência na Assinatura da API

A cláusula `throws` informa aos consumidores do método quais exceções do tipo **Checked** podem ser propagadas sem tratamento local. Ela é parte do contrato público da API:

```java
public void transferir(String destino, double valor) 
        throws SaldoInsuficienteException, ContaBloqueadaException {
    // Se a validação falhar, a exceção sobe para quem chamou
}
```

Quando um método declara `throws` com uma `Checked Exception`, o compilador **obriga** o chamador a tratar a exceção (com `try-catch`) ou propagar a declaração (com `throws` próprio). Isso cria uma cadeia de responsabilidade documentada na própria assinatura.

### 2.3 Por que Criar Exceções Customizadas?

Usar apenas `RuntimeException` ou `Exception` genéricas esconde o significado do problema no log. As vantagens de exceções customizadas são:

1. **Semântica Clara:** O próprio nome da classe descreve a falha de negócio (`SaldoInsuficienteException`, `CredenciaisInvalidasException`, `UsuarioNaoEncontradoException`).
2. **Tratamento Específico:** Permite capturar erros de negócio com blocos `catch` dedicados sem capturar erros genéricos indesejados.
3. **Enriquecimento com Atributos:** Podemos anexar dados contextuais à exceção (ex.: saldo disponível, valor da tentativa de saque, login do usuário, tentativas restantes).

Frameworks de mercado (Spring Boot com `@ExceptionHandler`) utilizam essas exceções para montar automaticamente respostas HTTP 400, 404 e 422 em APIs REST.

### 2.4 Estrutura de uma Exceção Customizada Checked

Exceções customizadas do tipo **Checked** (herdam de `Exception`) devem ser tratadas ou declaradas pelo compilador. Elas são indicadas para falhas de negócio das quais o chamador **pode e deve** se recuperar:

```java
public class SaldoInsuficienteException extends Exception {
    private final double saldoAtual;
    private final double valorTentativa;

    public SaldoInsuficienteException(String mensagem, double saldoAtual, double valorTentativa) {
        super(mensagem);
        this.saldoAtual = saldoAtual;
        this.valorTentativa = valorTentativa;
    }

    public double getSaldoAtual() { return saldoAtual; }
    public double getValorTentativa() { return valorTentativa; }
}
```

Os atributos `saldoAtual` e `valorTentativa` são `final` (imutáveis) e enriquecem a exceção com dados de auditoria que a camada de apresentação pode utilizar para exibir uma mensagem detalhada ao usuário.

### 2.5 Estrutura de uma Exceção Customizada Unchecked

Exceções customizadas do tipo **Unchecked** (herdam de `RuntimeException`) não são checadas pelo compilador. Elas são indicadas para violações de pré-condições irrecuperáveis, estados ilegais ou bugs lógicos do sistema:

```java
public class ContaBloqueadaException extends RuntimeException {
    public ContaBloqueadaException(String mensagem) {
        super(mensagem);
    }
    public ContaBloqueadaException(String mensagem, Throwable causaRaiz) {
        super(mensagem, causaRaiz); // Encadeamento de exceções
    }
}
```

O construtor que recebe `Throwable causaRaiz` permite o **encadeamento de exceções** (Exception Chaining), preservando a causa original de baixo nível (como um `SQLException` ou `IOException`) envelopada em uma exceção elegante de alto nível. A cadeia completa de causas pode ser percorrida via `getCause()`.

### 2.6 Matriz de Decisão: Checked vs. Unchecked em Exceções Customizadas

| Tipo de Exceção | Superclasse Base | Quando Utilizar? | Comportamento no Chamador |
| :--- | :--- | :--- | :--- |
| **Checked** | `java.lang.Exception` | O chamador **pode e deve** se recuperar da falha de forma alternativa (ex.: pedir outra forma de pagamento, tentar outro login). | Força o uso explícito de `try-catch` ou `throws` na assinatura. |
| **Unchecked** | `java.lang.RuntimeException` | Violações de pré-condições irrecuperáveis, estados ilegais ou bugs lógicos do sistema. | O código fica limpo, sem poluição de `throws` nas assinaturas. |

A tendência da arquitetura Java moderna é priorizar exceções customizadas herdando de `RuntimeException` para evitar assinaturas excessivamente verbosas. No entanto, exceções de regras de negócio transacionais explícitas continuam sendo excelentes candidatas a Checked.

### 2.7 Princípio da Responsabilidade Única (SRP) nas Exceções

Uma regra fundamental de design é: **classes de serviço/domínio não devem interagir com a tela** (`System.out.println`) para avisar de erros. Elas devem arremessar a exceção para que a camada de controle/apresentação decida como exibir o problema ao usuário.

Usar `try-catch` dentro de métodos de negócio apenas para imprimir mensagem é um **antipadrão clássico**. A camada de negócio deve sinalizar o erro via `throw/throws`, e a camada de interface (CLI, API REST ou Web) é quem trata e exibe.

## 3. Estudo de Caso Aplicado

O estudo de caso desta aula é um **sistema financeiro com transferências bancárias** localizado em `exemplos/aula-10/src/br/edu/universidade/sistema/financeiro/`. Ele é composto por cinco arquivos organizados em pacotes estruturados.

### 3.1 SaldoInsuficienteException.java (Checked)

```java
package br.edu.universidade.sistema.financeiro.exception;

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

Exceção Checked que herda de `Exception`. Carrega dados de auditoria: o saldo disponível na momento da falha e o valor que o usuário tentou sacar.

### 3.2 ContaBloqueadaException.java (Unchecked)

```java
package br.edu.universidade.sistema.financeiro.exception;

public class ContaBloqueadaException extends RuntimeException {
    private final String numeroConta;

    public ContaBloqueadaException(String mensagem, String numeroConta) {
        super(mensagem);
        this.numeroConta = numeroConta;
    }

    public ContaBloqueadaException(String mensagem, String numeroConta, Throwable causa) {
        super(mensagem, causa);
        this.numeroConta = numeroConta;
    }

    public String getNumeroConta() {
        return numeroConta;
    }
}
```

Exceção Unchecked que herda de `RuntimeException`. Carrega o número da conta bloqueada e suporta encadeamento de exceções via construtor com `Throwable causa`.

### 3.3 ContaCorrente.java (Domínio)

```java
package br.edu.universidade.sistema.financeiro.dominio;

import br.edu.universidade.sistema.financeiro.exception.ContaBloqueadaException;
import br.edu.universidade.sistema.financeiro.exception.SaldoInsuficienteException;

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

    public void sacar(double valor) throws SaldoInsuficienteException {
        if (!this.ativa) {
            throw new ContaBloqueadaException("Não é permitido saque em conta inativa.", this.numero);
        }
        if (valor <= 0.0) {
            throw new IllegalArgumentException("O valor de saque deve ser superior a zero.");
        }
        if (this.saldo < valor) {
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

A entidade `ContaCorrente` demonstra o padrão correto de disparo de exceções:

- `ContaBloqueadaException` (Unchecked): lançada quando a conta está inativa. O compilador não obriga tratamento, pois é considerada uma violação de integridade que não deveria ocorrer em código bem validado.
- `SaldoInsuficienteException` (Checked): declarada na assinatura com `throws`. O compilador obriga o chamador a tratar, pois é uma situação transacional legítima que o usuário pode resolver (ex.: escolher outro valor).
- `IllegalArgumentException` (Unchecked): lançada para parâmetros inválidos. É uma violação de pré-condição.

### 3.4 TransferenciaService.java (Serviço)

```java
package br.edu.universidade.sistema.financeiro.service;

import br.edu.universidade.sistema.financeiro.dominio.ContaCorrente;
import br.edu.universidade.sistema.financeiro.exception.ContaBloqueadaException;
import br.edu.universidade.sistema.financeiro.exception.SaldoInsuficienteException;

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
            System.err.println("[FALHA DE SALDO] " + ex.getMessage());
            System.err.printf("Auditoria: Saldo em conta: R$ %.2f | Valor solicitado: R$ %.2f | Déficit: R$ %.2f%n",
                    ex.getSaldoDisponivel(), ex.getValorTentado(), (ex.getValorTentado() - ex.getSaldoDisponivel()));

        } catch (ContaBloqueadaException ex) {
            System.err.println("[SEGURANÇA] Operação cancelada. " + ex.getMessage());
            System.err.println("Conta sinalizada para o departamento de compliance: " + ex.getNumeroConta());

        } catch (IllegalArgumentException ex) {
            System.err.println("[VALOR INVÁLIDO] " + ex.getMessage());
        }
    }
}
```

O `TransferenciaService` opera em **camadas desacopladas**: ele chama `sacar()` e `depositar()` e captura as exceções específicas, cada uma com tratamento adequado. Note como os atributos da `SaldoInsuficienteException` são acessados diretamente no `catch` para gerar relatórios de auditoria detalhados.

### 3.5 OperacoesApp.java (Apresentação)

```java
package br.edu.universidade.sistema.financeiro;

import br.edu.universidade.sistema.financeiro.dominio.ContaCorrente;
import br.edu.universidade.sistema.financeiro.service.TransferenciaService;

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

| Cenário | Operação | Saldo Origem | Valor | Resultado |
| :--- | :--- | :--- | :--- | :--- |
| 1 | Transferência válida | R$ 500.00 | R$ 200.00 | SUCESSO |
| 2 | Saldo insuficiente | R$ 300.00 | R$ 400.00 | FALHA DE SALDO |
| 3 | Conta bloqueada | R$ 300.00 | R$ 50.00 | SEGURANÇA (conta bloqueada) |

Todos os três cenários são tratados gracefulmente. O sistema permanece em execução contínua, demonstrando a resiliência do padrão de exceções customizadas.

## 4. Exercícios Propostos e Solução

O exercício consiste em implementar um **sistema de análise de crédito** com exceções customizadas para score do Serasa e limite de crédito. A solução está em `solucoes/aula-10/src/br/edu/universidade/sistema/financeiro/credito/`.

### 4.1 ScoreSerasaInvalidoException.java (Unchecked)

```java
package br.edu.universidade.sistema.financeiro.credito.exception;

public class ScoreSerasaInvalidoException extends RuntimeException {

    private final int pontuacaoInformada;

    public ScoreSerasaInvalidoException(String mensagem, int pontuacaoInformada) {
        super(mensagem);
        this.pontuacaoInformada = pontuacaoInformada;
    }

    public ScoreSerasaInvalidoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
        this.pontuacaoInformada = -1;
    }

    public int getPontuacaoInformada() {
        return pontuacaoInformada;
    }
}
```

Herdade de `RuntimeException` (Unchecked) porque score inválido é uma violação de integridade de dados que não deveria chegar até o serviço. Carrega a pontuação informada para auditoria.

### 4.2 LimiteCreditoExcedidoException.java (Checked)

```java
package br.edu.universidade.sistema.financeiro.credito.exception;

public class LimiteCreditoExcedidoException extends Exception {

    private final double rendaMensal;
    private final double valorParcelaPretendida;
    private final double percentualComprometimento;

    public LimiteCreditoExcedidoException(double rendaMensal, double valorParcelaPretendida) {
        super("Parcela de R$ " + String.format("%.2f", valorParcelaPretendida)
                + " compromete mais de 30% da renda mensal de R$ "
                + String.format("%.2f", rendaMensal));
        this.rendaMensal = rendaMensal;
        this.valorParcelaPretendida = valorParcelaPretendida;
        this.percentualComprometimento = rendaMensal > 0.0 ? (valorParcelaPretendida / rendaMensal) : 0.0;
    }

    public double getRendaMensal() {
        return rendaMensal;
    }

    public double getValorParcelaPretendida() {
        return valorParcelaPretendida;
    }

    public double getPercentualComprometimento() {
        return percentualComprometimento;
    }
}
```

Herdade de `Exception` (Checked) porque o chamador pode se recuperar da situação (ex.: solicitar valor menor ou prazo maior). A mensagem já descreve o problema, e os atributos permitem auditoria detalhada.

### 4.3 PropostaFinanciamento.java (Domínio)

```java
package br.edu.universidade.sistema.financeiro.credito.dominio;

import br.edu.universidade.sistema.financeiro.credito.exception.LimiteCreditoExcedidoException;
import br.edu.universidade.sistema.financeiro.credito.exception.ScoreSerasaInvalidoException;

public class PropostaFinanciamento {

    private static final double TAXA_FIXA_ENCARGOS = 0.10;

    private String cpfCliente;
    private double rendaMensal;
    private double valorEmprestimo;
    private int quantidadeMeses;

    public PropostaFinanciamento(String cpfCliente, double rendaMensal, double valorEmprestimo, int quantidadeMeses) {
        if (cpfCliente == null || cpfCliente.trim().isEmpty()) {
            throw new IllegalArgumentException("CPF do cliente não pode ser vazio.");
        }
        if (rendaMensal <= 0.0) {
            throw new IllegalArgumentException("Renda mensal deve ser positiva: " + rendaMensal);
        }
        if (valorEmprestimo <= 0.0) {
            throw new IllegalArgumentException("Valor do empréstimo deve ser positivo: " + valorEmprestimo);
        }
        if (quantidadeMeses <= 0) {
            throw new IllegalArgumentException("Quantidade de meses deve ser positiva: " + quantidadeMeses);
        }
        this.cpfCliente = cpfCliente;
        this.rendaMensal = rendaMensal;
        this.valorEmprestimo = valorEmprestimo;
        this.quantidadeMeses = quantidadeMeses;
    }

    public double calcularParcela() {
        double montante = this.valorEmprestimo * (1.0 + TAXA_FIXA_ENCARGOS);
        return montante / this.quantidadeMeses;
    }

    public void validarAprovacao(int scoreSerasa) throws LimiteCreditoExcedidoException {
        if (scoreSerasa < 0 || scoreSerasa > 1000) {
            throw new ScoreSerasaInvalidoException(
                    "Score Serasa fora do intervalo permitido (0 a 1000): " + scoreSerasa, scoreSerasa);
        }
        if (scoreSerasa < 400) {
            throw new ScoreSerasaInvalidoException(
                    "Proposta recusada: score insuficiente para concessão", scoreSerasa);
        }

        double parcela = calcularParcela();
        if (parcela > 0.30 * this.rendaMensal) {
            throw new LimiteCreditoExcedidoException(this.rendaMensal, parcela);
        }
    }

    public String getCpfCliente() { return cpfCliente; }
    public double getRendaMensal() { return rendaMensal; }
    public double getValorEmprestimo() { return valorEmprestimo; }
    public int getQuantidadeMeses() { return quantidadeMeses; }
}
```

O método `validarAprovacao` demonstra o uso de **duas exceções com naturezas diferentes**:

- `ScoreSerasaInvalidoException` (Unchecked): score fora do intervalo ou insuficiente. É uma violação de dados que o chamador não deveria provocar.
- `LimiteCreditoExcedidoException` (Checked): parcela excede 30% da renda. É uma condição transacional legítima que o chamador pode resolver ajustando os valores.

### 4.4 AnaliseCreditoApp.java (Apresentação)

```java
package br.edu.universidade.sistema.financeiro.credito;

import br.edu.universidade.sistema.financeiro.credito.dominio.PropostaFinanciamento;
import br.edu.universidade.sistema.financeiro.credito.exception.LimiteCreditoExcedidoException;
import br.edu.universidade.sistema.financeiro.credito.exception.ScoreSerasaInvalidoException;

public class AnaliseCreditoApp {
    public static void main(String[] args) {
        System.out.println("========== SISTEMA DE ANÁLISE DE CRÉDITO ==========");

        System.out.println("\n--- Caso 1: Comprometimento de Renda Excessivo ---");
        PropostaFinanciamento proposta1 = new PropostaFinanciamento("111.222.333-01", 4000.00, 100000.00, 55);
        try {
            proposta1.validarAprovacao(750);
            System.out.println("Proposta 1 aprovada (inesperado).");
        } catch (LimiteCreditoExcedidoException ex) {
            System.out.println("[LAUDO DE AUDITORIA] " + ex.getMessage());
            System.out.printf("Renda mensal........: R$ %.2f%n", ex.getRendaMensal());
            System.out.printf("Parcela pretendida..: R$ %.2f%n", ex.getValorParcelaPretendida());
            System.out.printf("Comprometimento.....: %.1f%% da renda (teto: 30%%)%n",
                    (ex.getPercentualComprometimento() * 100));
        } catch (ScoreSerasaInvalidoException ex) {
            System.out.println("[NEGADO] " + ex.getMessage() + " (score: " + ex.getPontuacaoInformada() + ")");
        }

        System.out.println("\n--- Caso 2: Score Serasa Negativo (-50) ---");
        PropostaFinanciamento proposta2 = new PropostaFinanciamento("444.555.666-02", 8000.00, 30000.00, 24);
        try {
            proposta2.validarAprovacao(-50);
            System.out.println("Proposta 2 aprovada (inesperado).");
        } catch (LimiteCreditoExcedidoException ex) {
            System.out.println("[NEGADO] " + ex.getMessage());
        } catch (ScoreSerasaInvalidoException ex) {
            System.out.println("[RESILIENTE] " + ex.getMessage() + " | Score informado: "
                    + ex.getPontuacaoInformada());
        }

        System.out.println("\nO sistema permaneceu em execução contínua sem quebras de processo.");
    }
}
```

| Caso | Score | Renda | Empréstimo | Meses | Parcela | Resultado |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| 1 | 750 | R$ 4.000,00 | R$ 100.000,00 | 55 | R$ 2.000,00 | LimiteCreditoExcedidoException (50% da renda) |
| 2 | -50 | R$ 8.000,00 | R$ 30.000,00 | 24 | R$ 1.375,00 | ScoreSerasaInvalidoException (score inválido) |

No Caso 1, o score é válido (750), mas a parcela excede 30% da renda. No Caso 2, o score é negativo (-50), violando o intervalo permitido. Em ambos os cenários, o sistema continua em execução normalmente.

## 5. Perguntas de Revisão

1. Qual é a diferença técnica entre `throw` e `throws`?
2. Por que `ContaBloqueadaException` herda de `RuntimeException` e `SaldoInsuficienteException` herda de `Exception`?
3. O que é Encadeamento de Exceções (Exception Chaining) e como ele é implementado no construtor de uma exceção customizada?
4. Por que não é recomendável usar `System.out.println` dentro de métodos de domínio/serviço para sinalizar erros?
5. Qual a vantagem de adicionar atributos contextuais (como `saldoAtual` e `valorTentativa`) a uma exceção customizada?
6. Em que situação é mais apropriado usar uma exceção Checked em vez de uma Unchecked?
7. O que acontece se uma classe de domínio dispara uma exceção Checked e o chamador não tratar nem declarar `throws`?
8. Por que frameworks como Spring Boot preferem exceções customizadas Unchecked em vez de Checked?
9. Qual é a diferença entre `super(mensagem)` e `super(mensagem, causaRaiz)` no construtor de uma exceção?
10. Se você tem uma exceção `LimiteCreditoExcedidoException` com atributo `percentualComprometimento`, como você acessaria esse dado no bloco `catch`?

## 6. Resumo / Pontos-Chave

- A instrução **`throw`** dispara explicitamente uma exceção, interrompendo a execução do método e iniciando o Stack Unwinding.
- A cláusula **`throws`** na assinatura declara que o método pode lançar exceções Checked, fornece contratos claros aos consumidores.
- **Exceções Customizadas** são classes que herdam de `Exception` ou `RuntimeException` e carregam semântica e dados de domínio específicos.
- **Checked Exceptions** (herdam de `Exception`) são indicadas para falhas das quais o chamador pode se recuperar. O compilador obriga tratamento.
- **Unchecked Exceptions** (herdam de `RuntimeException`) são indicadas para violações de integridade e pré-condições. O código fica limpo.
- **Encadeamento de Exceções** (`Exception Chaining`) preserva a causa raiz técnica dentro de uma abstração de alto nível.
- **Atributos contextuais imutáveis** nas exceções permitem que a camada de apresentação gere relatórios de auditoria detalhados.
- O **princípio da Responsabilidade Única** exige que classes de domínio/serviço não interajam com a tela; elas lançam exceções e a camada de apresentação decide como exibir.
- O estudo de caso demonstra um sistema financeiro com camadas desacopladas (domínio, serviço, apresentação) onde exceções customizadas Checked e Unchecked sinalizam falhas de forma semântica e estruturada.
- O exercício resolvido implementa um sistema de análise de crédito com `ScoreSerasaInvalidoException` (Unchecked) e `LimiteCreditoExcedidoException` (Checked), demonstrando a matriz de decisão na prática.
