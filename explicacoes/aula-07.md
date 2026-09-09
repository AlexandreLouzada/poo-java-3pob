# Explicação da Aula 07 -- Herança, Polimorfismo Dinâmico, Classes Abstratas e Interfaces

| | |
|---|---|
| **Disciplina** | Programação Orientada a Objetos |
| **Curso** | Análise e Desenvolvimento de Sistemas (3º Período) |
| **Tema** | Herança (`extends`), Polimorfismo Dinâmico, Cadeia de Construtores (`super`), Classes Abstratas (`abstract`) e Interfaces (`implements`) |
| **Carga Horária** | 100 minutos (2 horas-aula) |
| **Professor** | Alexandre Neves Louzada |
| **Plano de Aula** | `Aulas 3pob/Aula 07.md` |
| **Tutorial** | `aulas/TutorialAula7.md` |
| **Estudo de Caso** | `exemplos/aula-07/` |
| **Exercícios Resolvidos** | `solucoes/aula-07/` |

## 1. Objetivos de Aprendizagem

Esta aula consolida todos os conceitos fundamentais da Orientação a Objetos, fechando o Módulo 2 do curso. Ela aborda herança, polimorfismo dinâmico, classes abstratas e interfaces -- ferramentas que permitem construir hierarquias extensíveis e arquiteturas desacopladas.

**Objetivos conceituais:** Compreender o mecanismo de herança como especialização de tipo e reuso estrutural (*relação É-UM*); dominar o conceito de Polimorfismo Dinâmico (*Dynamic Method Dispatch* / *Late Binding*) e sua relação com o princípio Aberto/Fechado (OCP do SOLID).

**Objetivos técnicos:** Projetar hierarquias extensíveis utilizando a palavra-chave `extends`; invocar construtores e métodos da superclasse através de `super`; aplicar a anotação `@Override` com rigor; projetar classes base incompletas com `abstract class` e métodos `abstract`; definir contratos desacoplados com `interface` e `implements`.

**Objetivos arquiteturais:** Dominar a matriz de decisão entre Classes Abstratas (compartilhamento de estado estrutural e código comum em hierarquias fortemente acopladas) versus Interfaces (contratos comportamentais puros, desacoplados e de múltipla implementação).

**Objetivos práticos:** Implementar um motor de checkout de pagamentos eletrônicos em lote (Cartão de Crédito, Pix, Boleto), onde a camada consumidora opera exclusivamente sobre abstrações polimórficas sem conhecer as classes concretas.

### 1.1 Metodologia Ativa

A aula utiliza a metodologia **Jigsaw Classroom**, onde a turma é dividida em grupos focados no projeto de abstrações estruturais (classes base abstratas) e contratos de comportamento (interfaces), convergindo para a montagem de uma arquitetura limpa de checkout. Também é feita uma **Demonstração Forense de Polimorfismo**, inspecionando no depurador da IDE a tabela de métodos virtuais (*vtable*) da JVM resolvendo em tempo de execução o método da subclasse correta através de uma variável de referência da interface genérica.

## 2. Conteúdo Teórico Detalhado

### 2.1 Herança em Java (`extends`) e o Modificador `protected`

A herança em Java é sempre **simples**: uma classe pode herdar diretamente de apenas uma única superclasse. Toda classe em Java herda, direta ou indiretamente, da classe `java.lang.Object`, que é a raiz universal da hierarquia.

A palavra-chave `extends` é usada para declarar a herança. A subclasse herda todos os membros (atributos e métodos) da superclasse que não são marcados como `private`. Para acessar membros de visibilidade intermediária, o modificador `protected` permite acesso direto por subclasses (mesmo em outros pacotes) e por classes do mesmo pacote.

### 2.2 A Palavra-Chave `super`

O operador `super` é utilizado para acessar membros da superclasse a partir da subclasse. Ele se manifesta de duas formas:

- `super(...)`: Invoca o construtor da superclasse. Esta chamada **deve ser a primeira linha** do construtor da subclasse. Se não for explicitada, o compilador insere automaticamente `super()` (construtor sem argumentos).
- `super.metodo()`: Invoca a versão original do método da superclasse, permitindo que a subclasse estenda o comportamento sem substituí-lo completamente.

### 2.3 Mecânica de Instanciação

Quando uma subclasse é instanciada, a JVM executa os construtores em cadeia, da raiz até a folha:

1. Construtor de `Object` (raiz universal).
2. Construtor da superclasse mais próxima.
3. Construtor da classe filha.

Isso garante que o estado da superclasse esteja completamente inicializado antes que a subclasse adicione seus próprios dados.

### 2.4 Sobrescrita de Métodos (*Override*) vs. Sobrecarga (*Overloading*)

A sobrescrita de métodos (*Override*) é fundamentalmente diferente da sobrecarga (*Overload*). A tabela comparativa abaixo esclarece as diferenças:

| Critério | Sobrecarga (*Overloading* - Aula 06) | Sobrescrita (*Overriding* - Aula 07) |
| :--- | :--- | :--- |
| **Escopo** | Na mesma classe | Entre Superclasse e Subclasse |
| **Assinatura** | **Tipos/quantidades de parâmetros diferentes** | **Assinatura rigorosamente idêntica** |
| **Retorno** | Pode ser diferente | Idêntico ou subtipo covariante |
| **Momento de Resolução** | **Tempo de Compilação** (*Static Binding*) | **Tempo de Execução** (*Dynamic Binding*) |

A anotação `@Override` não é apenas decorativa: ela instrui o compilador a validar se o método realmente existe na superclasse com a assinatura correta, evitando bugs silenciosos por erros de digitação.

### 2.5 Polimorfismo Dinâmico (*Dynamic Method Dispatch*)

O polimorfismo dinâmico é o mecanismo pelo qual a JVM decide **em tempo de execução** qual versão de um método deve ser executada, com base no tipo real do objeto na memória (*Heap*), e não no tipo da variável de referência (*Stack*).

Exemplo conceitual:

```java
// Referência genérica apontando para instâncias concretas distintas
MeioPagamento pagamento1 = new CartaoCredito("TX-001", 2.5, 1500.00);
MeioPagamento pagamento2 = new PagamentoPix("TX-002", "chave@empresa.com");

// A JVM decide em tempo de execução qual método executar!
pagamento1.autorizar(200.00); // Executa a lógica de limite do CartaoCredito
pagamento2.autorizar(200.00); // Executa a lógica de saldo/chave do Pix
```

A variável `pagamento1` é do tipo `MeioPagamento` (superclasse/abstração), mas aponta para um objeto `CartaoCredito`. Quando `autorizar()` é chamado, a JVM consulta a tabela de métodos virtuais (*vtable*) do objeto real e invoca o método de `CartaoCredito`. Esse é o **Late Binding**.

### 2.6 Classes Abstratas (`abstract class`)

Uma classe abstrata é aquela declarada com a palavra-chave `abstract`. Ela serve como **modelo incompleto** que não pode ser instanciada diretamente. Classes abstratas podem conter:

- **Métodos abstratos** (declarados sem corpo): obrigam as subclasses concretas a fornecerem a implementação.
- **Métodos concretos**: implementam lógica compartilhada que todas as subclasses herdam sem precisar reescrever.
- **Atributos e construtores**: compartilhados pelas subclasses.

### 2.7 Interfaces (`interface` / `implements`)

Uma interface define um **contrato comportamental puro**: ela declara quais métodos uma classe deve implementar, sem fornecer estado (atributos de instância) nem implementação de lógica (até Java 7; a partir do Java 8 podem conter métodos *default* e *static*).

Uma classe pode implementar **múltiplas interfaces**, ao contrário da herança que é sempre simples. Isso permite modelar capacidades transversais (como "ser auditável", "ser serializável", etc.) sem violar a hierarquia de herança.

### 2.8 Matriz de Decisão: Abstração vs. Interface

| Aspecto | Classe Abstrata | Interface |
| :--- | :--- | :--- |
| **Herança** | Simples (`extends`) | Múltipla (`implements`) |
| **Atributos de instância** | Sim | Não |
| **Construtores** | Sim | Não |
| **Métodos concretos** | Sim | Apenas *default*/*static* (Java 8+) |
| **Uso ideal** | Compartilhamento de estado e código em hierarquias fortemente acopladas | Contratos comportamentais puros, desacoplados e de múltipla implementação |

## 3. Estudo de Caso Aplicado

O estudo de caso desta aula é um **motor de checkout de pagamentos eletrônicos** localizado em `exemplos/aula-07/src/br/edu/universidade/sistema/checkout/`. Ele é composto por cinco arquivos que demonstram a integração entre interface, classe abstrata, subclasses concretas e camada de serviço.

### 3.1 Auditavel.java -- Interface (Contrato Comportamental)

```java
package br.edu.universidade.sistema.checkout;

public interface Auditavel {
    String emitirComprovante();
}
```

A interface `Auditavel` define um único contrato: qualquer classe que implemente `Auditavel` deve ser capaz de emitir um comprovante. Isso desacopla a camada de auditoria das implementações concretas.

### 3.2 MeioPagamento.java -- Classe Abstrata

```java
package br.edu.universidade.sistema.checkout;

public abstract class MeioPagamento implements Auditavel {
    protected final String codigoTransacao;
    protected double valorAutorizado;

    public MeioPagamento(String codigoTransacao) {
        this.codigoTransacao = codigoTransacao;
    }

    public String getCodigoTransacao() {
        return this.codigoTransacao;
    }

    public double getValorAutorizado() {
        return this.valorAutorizado;
    }

    public abstract boolean autorizar(double valor);
}
```

`MeioPagamento` é abstrata e implementa `Auditavel`. Ela define:

- Atributos `protected` (visíveis para subclasses, mesmo em outros pacotes): `codigoTransacao` (final/imutável) e `valorAutorizado`.
- Um construtor que inicializa o código.
- Um método abstrato `autorizar(double valor)`: cada subclasse concreta é obrigada a implementar sua própria regra de autorização.

### 3.3 CartaoCredito.java -- Subclasse Concreta 1

```java
package br.edu.universidade.sistema.checkout;

public class CartaoCredito extends MeioPagamento {
    private final double taxaPercentualOperadora;
    private double limiteDisponivel;

    public CartaoCredito(String codigoTransacao, double taxaPercentualOperadora, double limiteDisponivel) {
        super(codigoTransacao); // Obrigatório: primeira linha do construtor
        this.taxaPercentualOperadora = taxaPercentualOperadora;
        this.limiteDisponivel = limiteDisponivel;
    }

    @Override
    public boolean autorizar(double valor) {
        double custoTotal = valor * (1 + (taxaPercentualOperadora / 100.0));
        if (valor > 0.0 && this.limiteDisponivel >= custoTotal) {
            this.limiteDisponivel -= custoTotal;
            this.valorAutorizado = custoTotal;
            return true;
        }
        return false;
    }

    @Override
    public String emitirComprovante() {
        return String.format("TX [%s] - CARTAO: Autorizado R$ %.2f (Taxa: %.1f%%) | Limite Restante: R$ %.2f",
                codigoTransacao, valorAutorizado, taxaPercentualOperadora, limiteDisponivel);
    }
}
```

A regra de autorização do cartão de crédito inclui o cálculo da taxa da operadora e a verificação do limite disponível. O método `emitirComprovante()` cumpre o contrato da interface `Auditavel`.

### 3.4 PagamentoPix.java -- Subclasse Concreta 2

```java
package br.edu.universidade.sistema.checkout;

public class PagamentoPix extends MeioPagamento {
    private final String chavePixDestino;

    public PagamentoPix(String codigoTransacao, String chavePixDestino) {
        super(codigoTransacao);
        this.chavePixDestino = chavePixDestino;
    }

    @Override
    public boolean autorizar(double valor) {
        if (valor > 0.0) {
            this.valorAutorizado = valor;
            return true;
        }
        return false;
    }

    @Override
    public String emitirComprovante() {
        return String.format("TX [%s] - PIX: Transferido R$ %.2f para Chave [%s] instantaneamente.",
                codigoTransacao, valorAutorizado, chavePixDestino);
    }
}
```

O Pix é isento de tarifa no modelo de negócio, por isso a autorização é mais simples: basta verificar se o valor é positivo.

### 3.5 CheckoutService.java -- Camada de Serviço Desacoplada

```java
package br.edu.universidade.sistema.checkout;

public class CheckoutService {

    public void processarLote(MeioPagamento[] pagamentos, double valorCobranca) {
        System.out.println("========== INICIANDO PROCESSAMENTO DE CHECKOUT ==========");

        for (MeioPagamento pgto : pagamentos) {
            boolean sucesso = pgto.autorizar(valorCobranca);

            if (sucesso) {
                System.out.println("[SUCESSO] " + pgto.emitirComprovante());
            } else {
                System.out.printf("[RECUSADO] TX [%s] - Saldo/Limite insuficiente para valor R$ %.2f%n",
                        pgto.getCodigoTransacao(), valorCobranca);
            }
        }

        System.out.println("=========================================================");
    }
}
```

O `CheckoutService` opera exclusivamente sobre o tipo abstrato `MeioPagamento[]`. Ele não conhece `CartaoCredito`, `PagamentoPix` nem nenhuma outra classe concreta. Isso é o **polimorfismo dinâmico em ação**: a JVM despacha cada chamada de `autorizar()` e `emitirComprovante()` para a implementação correta em tempo de execução.

## 4. Exercícios Propostos e Solução

O exercício proposto é um **sistema bancário com tarifas**, onde uma classe abstrata `Conta` e uma interface `Tributavel` são combinadas para modelar ContaCorrente e ContaPoupanca. A solução está em `solucoes/aula-07/src/br/edu/universidade/sistema/tarifas/`.

### 4.1 Tributavel.java -- Interface

```java
package br.edu.universidade.sistema.tarifas;

public interface Tributavel {
    double calcularTributo();
}
```

### 4.2 Conta.java -- Classe Abstrata

```java
package br.edu.universidade.sistema.tarifas;

public abstract class Conta {
    protected String numeroConta;
    protected double saldo;

    public Conta(String numeroConta, double saldoInicial) {
        this.numeroConta = numeroConta;
        this.saldo = (saldoInicial >= 0.0) ? saldoInicial : 0.0;
    }

    public void depositar(double valor) {
        if (valor > 0.0) {
            this.saldo += valor;
        }
    }

    public abstract boolean sacar(double valor);

    public double getSaldo() {
        return saldo;
    }

    public void exibirDados() {
        System.out.printf("Conta %s | Saldo: R$ %.2f%n", numeroConta, saldo);
    }
}
```

A classe `Conta` é abstrata porque não faz sentido instanciar uma "conta genérica". O método `sacar()` é abstrato porque cada tipo de conta tem regras diferentes de saque.

### 4.3 ContaCorrente.java -- Herança + Interface

```java
package br.edu.universidade.sistema.tarifas;

public class ContaCorrente extends Conta implements Tributavel {

    private static final double TAXA_SAQUE = 1.50;

    public ContaCorrente(String numeroConta, double saldoInicial) {
        super(numeroConta, saldoInicial);
    }

    @Override
    public boolean sacar(double valor) {
        if (valor > 0.0 && this.saldo >= (valor + TAXA_SAQUE)) {
            this.saldo -= (valor + TAXA_SAQUE);
            return true;
        }
        return false;
    }

    @Override
    public double calcularTributo() {
        return this.saldo * 0.01;
    }
}
```

`ContaCorrente` herda de `Conta` (relação É-UM) e também implementa `Tributavel` (capacidade transversal de tributação). O saque cobra uma taxa fixa de R$ 1,50 além do valor solicitado.

### 4.4 ContaPoupanca.java -- Herança Simples

```java
package br.edu.universidade.sistema.tarifas;

public class ContaPoupanca extends Conta {

    public ContaPoupanca(String numeroConta, double saldoInicial) {
        super(numeroConta, saldoInicial);
    }

    @Override
    public boolean sacar(double valor) {
        if (valor > 0.0 && this.saldo >= valor) {
            this.saldo -= valor;
            return true;
        }
        return false;
    }
}
```

`ContaPoupanca` herda de `Conta` mas **não** implementa `Tributavel`, demonstrando que a poupança é isenta de tributos. A diferença estrutural entre `ContaCorrente` e `ContaPoupanca` ficará evidente quando forem tratadas polimorficamente.

### 4.5 BancoApp.java -- Demonstração

```java
package br.edu.universidade.sistema.tarifas;

public class BancoApp {
    public static void main(String[] args) {
        ContaCorrente cc = new ContaCorrente("0101-CC", 1000.00);
        ContaPoupanca cp = new ContaPoupanca("0202-CP", 2000.00);

        System.out.println("--- Vetor Heterogêneo: Saques Polimórficos ---");
        Conta[] contas = { cc, cp };
        double[] valoresSaque = { 100.00, 300.00 };
        for (int i = 0; i < contas.length; i++) {
            boolean sucesso = contas[i].sacar(valoresSaque[i]);
            System.out.printf("Conta %s | Saque de R$ %.2f autorizado? %s -> ",
                    contas[i].numeroConta, valoresSaque[i], sucesso);
            contas[i].exibirDados();
        }

        System.out.println("\n--- Tabela de Impostos (Tributavel[]) ---");
        Tributavel[] tributaveis = { cc };
        double somaTributos = 0.0;
        for (Tributavel t : tributaveis) {
            double tributo = t.calcularTributo();
            somaTributos += tributo;
            System.out.printf("Tributo recolhido: R$ %.2f%n", tributo);
        }
        System.out.printf("Somatório total de tributos recolhidos: R$ %.2f%n", somaTributos);

        System.out.println("\n--- Comprovação: poupança não é tributável ---");
        System.out.println("ContaPoupanca implements Tributavel? "
                + (cp instanceof Tributavel ? "SIM (incorreto)" : "NÃO (correto, sem tributo)"));
    }
}
```

O array `Conta[] contas` armazena tipos heterogêneos (ContaCorrente e ContaPoupanca). A chamada `sacar()` é despachada dinamicamente. O array `Tributavel[]` só aceita objetos que implementem a interface, e o `instanceof` confirma que a poupança não é tributável.

## 5. Perguntas de Revisão

1. Por que é impossível instanciar uma classe abstrata diretamente em Java?
2. Qual é a diferença entre chamar `super.metodo()` e simplesmente chamar `metodo()` dentro de uma subclasse que sobrescreveu o método?
3. Por que a anotação `@Override` é considerada uma boa prática mesmo não sendo obrigatória?
4. Em que situação é mais apropriado usar uma classe abstrata em vez de uma interface?
5. Uma interface pode ter atributos de instância? E construtores?
6. O que acontece na JVM quando `pagamento1.autorizar(200.00)` é chamado com `pagamento1` sendo do tipo `MeioPagamento` mas apontando para um `CartaoCredito`?
7. Por que `CheckoutService` não conhece nenhuma classe concreta de pagamento? Qual a vantagem arquitetural disso?
8. Se `ContaPoupanca` não implementa `Tributavel`, o que acontece se você tentar adicionar uma `ContaPoupanca` a um array `Tributavel[]`?
9. Qual a diferença entre `protected` e `public` no contexto de herança entre pacotes diferentes?
10. Por que o Java não permite herança múltipla de classes, mas permite múltipla implementação de interfaces?

## 6. Resumo / Pontos-Chave

- **Herança** (`extends`) implementa a relação É-UM e permite reuso estrutural. Java permite apenas herança simples.
- **`super`** invoca construtores e métodos da superclasse. A chamada ao construtor deve ser a primeira linha do construtor filho.
- **Sobrescrita** (`@Override`) redefine comportamento em subclasse com assinatura idêntica e é resolvida em **tempo de execução** (*Late Binding* / *Dynamic Dispatch*).
- **Classes abstratas** são modelos incompletos que compartilham estado e código comum, mas não podem ser instanciadas.
- **Interfaces** definem contratos comportamentais puros que podem ser implementados por múltiplas classes.
- O **polimorfismo dinâmico** permite que uma referência do tipo abstrato aponte para qualquer subclasse concreta, e a JVM resolve o método correto em *runtime*.
- A **matriz de decisão** entre abstração e interface é: use abstração para compartilhar estado e código, e interface para capacidades transversais desacopladas.
- O estudo de caso implementa um checkout de pagamentos onde `CheckoutService` opera sobre `MeioPagamento[]` sem conhecer as implementações concretas, demonstrando o princípio "Programe para abstrações, não para implementações".
