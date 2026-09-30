# Tutorial de Java — Aula 07: Herança, Polimorfismo Dinâmico, Classes Abstratas e Interfaces

| | |
|---|---|
| **Disciplina** | Programação Orientada a Objetos |
| **Curso** | Análise e Desenvolvimento de Sistemas (3º Período) |
| **Tema** | Herança (extends), Polimorfismo Dinâmico, Cadeia de Construtores (super), Classes Abstratas (abstract) e Interfaces (implements) |
| **Carga Horária** | 100 minutos (2 horas-aula) |
| **Professor Responsável** | Alexandre Neves Louzada |
| **Arquivo de Referência** | `TutorialAula7.md` |

## 1. Objetivos de Aprendizagem

- **Conceitual:** Compreender o mecanismo de herança como especialização de tipo e reuso estrutural (relação "É-UM"); dominar o conceito de Polimorfismo Dinâmico (*Dynamic Method Dispatch* / *Late Binding*) e sua relação direta com o Princípio Aberto/Fechado (OCP do SOLID).
- **Técnico:** Projetar hierarquias extensíveis utilizando a palavra-chave `extends`; invocar construtores e comportamentos da superclasse através de `super`; aplicar a anotação `@Override` com rigor; projetar modelos incompletos com `abstract class` e métodos abstratos; estruturar contratos com `interface` e `implements`.
- **Arquitetural:** Dominar a matriz de decisão entre Classes Abstratas (compartilhamento de estado estrutural e rotinas comuns em hierarquias com forte acoplamento) versus Interfaces (contratos comportamentais puros, desacoplados e de múltipla implementação).
- **Prático:** Implementar um motor de checkout de pagamentos eletrônicos em lote (Cartão de Crédito, Pix e Boleto), operando a camada consumidora exclusivamente sobre abstrações polimórficas sem expor as classes concretas.

## 2. Fundamentação Teórica

### Herança Simples e a Especialização de Classes

A herança permite que uma subclasse herde todos os atributos e métodos de uma superclasse, especializando comportamentos existentes ou adicionando novos campos. Ela deve ser empregada apenas quando a relação semântica do "É-UM" (Is-A) for comprovadamente verdadeira:

- Um `CartaoCredito` **é um** `MeioPagamento`.
- Um `Gerente` **é um** `Funcionario`.

Na plataforma Java, a herança entre classes é estritamente simples: uma subclasse herda diretamente de uma única superclasse utilizando a palavra-chave `extends`. Todas as classes na linguagem que não declaram superclasse explícita herdam automaticamente da raiz universal `java.lang.Object`.

```plaintext
┌──────────────────────────────────────────────┐
│               java.lang.Object               │ (Superclasse Universal)
└──────────────────────┬───────────────────────┘
                       │ extends
                       ▼
┌──────────────────────────────────────────────┐
│           MeioPagamento (Abstrata)           │ (Estado compartilhado + Contrato)
└──────────────┬────────────────┬──────────────┘
               │                │
        extends │                │ extends
               ▼                ▼
┌──────────────────────┐┌──────────────────────┐
│    CartaoCredito     ││     PagamentoPix     │ (Subclasses Concretas)
└──────────────────────┘└──────────────────────┘
```

### A Cadeia de Construtores e a Palavra-Chave `super`

Um objeto instanciado a partir de uma subclasse precisa que o estado definido em seus ancestrais seja alocado e inicializado no Heap antes de seus próprios membros:

- **Invocação de Construtor Pai:** A instrução `super(...)` delega parâmetros para o construtor da superclasse.
- **Regra da Primeira Linha:** A chamada `super(...)` deve ser, compulsoriamente, a primeira instrução executável dentro do construtor da subclasse.
- **Acesso a Métodos Herdados:** A sintaxe `super.metodo()` permite que a classe filha execute a implementação original da superclasse antes de aplicar extensões.
- **Modificador `protected`:** Torna atributos e métodos visíveis para subclasses (mesmo em pacotes diferentes) e para todas as classes pertencentes ao mesmo pacote físico.

### Sobrescrita de Métodos (Override) vs. Sobrecarga (Overload)

A correta distinção entre esses dois mecanismos evita falhas silenciosas de lógica no sistema:

| Critério de Comparação | Sobrecarga (Overloading — Aula 06) | Sobrescrita (Overriding — Aula 07) |
|---|---|---|
| Escopo Arquitetural | Ocorre dentro da mesma classe | Ocorre entre Superclasse e Subclasse |
| Assinatura do Método | Quantidades ou tipos de parâmetros diferentes | Assinatura rigorosamente idêntica |
| Tipo de Retorno | Pode ser alterado livremente | Deve ser idêntico ou um subtipo covariante |
| Momento de Resolução | Tempo de Compilação (Static / Early Binding) | Tempo de Execução (Dynamic / Late Binding) |

A anotação `@Override` instrui o compilador `javac` a verificar se a assinatura declarada realmente sobrescreve um método da superclasse. Omitir a anotação permite que erros sutis de digitação transformem uma sobrescrita pretendida em uma sobrecarga indesejada.

### Polimorfismo Dinâmico (Dynamic Method Dispatch)

O polimorfismo dinâmico permite manipular instâncias de subclasses distintas através de uma variável de referência com o tipo da superclasse ou interface comum:

```java
MeioPagamento pagamento1 = new CartaoCredito(2.5, 1500.00);
MeioPagamento pagamento2 = new PagamentoPix("chave@empresa.com");

pagamento1.autorizar(200.00); // Executa a lógica de CartaoCredito
pagamento2.autorizar(200.00); // Executa a lógica de PagamentoPix
```

Em tempo de compilação, o compilador verifica apenas se o método `autorizar(double)` existe no tipo referenciado (`MeioPagamento`). Em tempo de execução, a JVM consulta a tabela de métodos virtuais (*vtable*) associada ao objeto alocado no Heap e redireciona a chamada para a implementação da classe real instanciada. Esse comportamento atende ao Princípio Aberto/Fechado (OCP): novos meios de pagamento podem ser incluídos sem modificar a rotina consumidora de pagamentos.

### Classes Abstratas (abstract) vs. Interfaces (interface)

A escolha arquitetural entre classes abstratas e interfaces define a flexibilidade e o acoplamento do sistema:

```plaintext
┌─────────────────────────────────────────────────────────────────────────────┐
│                        MATRIZ DECISÓRIA DE ARQUITETURA                      │
├──────────────────────────────────────┬──────────────────────────────────────┤
│ Classes Abstratas (abstract class)   │ Interfaces (interface)               │
├──────────────────────────────────────┼──────────────────────────────────────┤
│ • Compartilha estado (atributos)     │ • Contrato puro de comportamento     │
│ • Fornece métodos concretos e comuns │ • Sem atributos de instância         │
│                                      │   (só constantes)                    │
│ • Vinculada a herança simples        │ • Suporta herança múltipla de tipos  │
│ • Usada para forte acoplamento "É-UM"│ • Usada para capacidades "PODE-FAZER"│
└──────────────────────────────────────┴──────────────────────────────────────┘
```

- **Classe Abstrata:** Não pode ser instanciada diretamente com o operador `new`. Serve de base estrutural, podendo conter atributos com qualquer modificador de acesso e métodos com ou sem corpo (`abstract void processar();`).
- **Interface:** Define contratos públicos. Uma classe concreta pode implementar múltiplas interfaces simultaneamente (`implements Autenticavel, ExportavelJSON`), contornando a limitação da herança simples em Java.

## 3. Estudo de Caso Integrado: Motor de Checkout Eletrônico

O projeto prático abaixo consolida interfaces, classes abstratas com estado estrutural, cadeias de construtores e polimorfismo dinâmico:

```java
package br.edu.universidade.sistema.checkout;

// 1. Contrato Comportamental Puro (Capacidade de Auditoria)
public interface Auditavel {
    String emitirComprovante();
}
```

```java
package br.edu.universidade.sistema.checkout;

// 2. Classe Base Abstrata (Compartilhamento de Estado e Reuso de Código)
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

    // Método abstrato: a subclasse concreta é obrigada a implementar a regra
    public abstract boolean autorizar(double valor);
}
```

```java
package br.edu.universidade.sistema.checkout;

// 3. Subclasse Concreta 1: Cartão de Crédito
public class CartaoCredito extends MeioPagamento {
    private final double taxaPercentualOperadora;
    private double limiteDisponivel;

    public CartaoCredito(String codigoTransacao, double taxaPercentualOperadora, double limiteDisponivel) {
        super(codigoTransacao); // Obrigatório: Primeira linha do construtor
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

```java
package br.edu.universidade.sistema.checkout;

// 4. Subclasse Concreta 2: Pagamento Pix
public class PagamentoPix extends MeioPagamento {
    private final String chavePixDestino;

    public PagamentoPix(String codigoTransacao, String chavePixDestino) {
        super(codigoTransacao); // Invocação encadeada da superclasse
        this.chavePixDestino = chavePixDestino;
    }

    @Override
    public boolean autorizar(double valor) {
        if (valor > 0.0) {
            this.valorAutorizado = valor; // Pix isento de tarifa no modelo de negócio
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

```java
package br.edu.universidade.sistema.checkout;

// 5. Camada de Serviço desacoplada: opera exclusivamente sobre abstrações
public class CheckoutService {

    public void processarLote(MeioPagamento[] pagamentos, double valorCobranca) {
        System.out.println("========== INICIANDO PROCESSAMENTO DE CHECKOUT ==========");

        for (MeioPagamento pgto : pagamentos) {
            // Despacho dinâmico: a JVM localiza o método concreto no Heap
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

## 4. Diagnóstico de Erros Comuns e Armadilhas

### Armadilha 1: Invocação Tardia de `super(...)`

**Código com Falha:**

```java
public CartaoCredito(String codigo, double taxa) {
    this.taxaPercentualOperadora = taxa; // ERRO DE COMPILAÇÃO!
    super(codigo);
}
```

- **Diagnóstico da JVM:** `Constructor call must be the first statement in a constructor.`
- **Causa & Correção:** O estado da superclasse precisa ser montado antes de qualquer linha da subclasse ser executada. A linha `super(codigo);` deve ser obrigatoriamente a primeira instrução dentro do bloco do construtor.

### Armadilha 2: Tentativa de Instanciação Direta de Tipo Abstrato

**Código com Falha:**

```java
MeioPagamento pgto = new MeioPagamento("TX-1001"); // ERRO DE COMPILAÇÃO!
```

- **Diagnóstico da JVM:** `MeioPagamento is abstract; cannot be instantiated`.
- **Causa & Correção:** Classes declaradas com `abstract` possuem implementações incompletas por definição. Instancie exclusivamente as subclasses concretas que fornecem os corpos para todos os métodos abstratos (`new CartaoCredito(...)` ou `new PagamentoPix(...)`).

### Armadilha 3: Omissão de Métodos de Contrato de Interface

**Código com Falha:**

```java
public class BoletoBancario extends MeioPagamento {
    public BoletoBancario(String tx) { super(tx); }
    @Override
    public boolean autorizar(double v) { return true; }
    // O desenvolvedor esqueceu de implementar o método emitirComprovante() da interface Auditavel!
}
```

- **Diagnóstico da JVM:** `BoletoBancario is not abstract and does not override abstract method emitirComprovante() in Auditavel.`
- **Causa & Correção:** Quando uma classe concreta herda de uma classe abstrata que implementa uma interface, ela é obrigada a fornecer implementação para todos os métodos abstratos pendentes da cadeia de herança.

## 5. Roteiro Prático de Depuração: Inspecionando a vtable na IDE

Para visualizar a resolução dinâmica de métodos (*Late Binding*) em tempo real na sua IDE (IntelliJ IDEA, Eclipse ou VS Code):

1. Construa uma classe executável instanciando um array de referências genéricas contendo instâncias concretas distintas: `MeioPagamento[] lista = { new CartaoCredito(...), new PagamentoPix(...) };`.
2. Posicione um ponto de interrupção (*breakpoint*) na linha da chamada polimórfica: `pgto.autorizar(valorCobranca);` dentro do laço `for`.
3. Inicie o programa em modo de depuração (*Debug*).
4. Na primeira iteração, posicione o cursor sobre `pgto` e observe a variável: o tipo da referência é `MeioPagamento`, mas o identificador de instância no Heap aponta para `CartaoCredito`.
5. Execute a instrução com o comando *Step Into* (F7): observe a IDE direcionar a execução para a linha da classe `CartaoCredito`.
6. Na segunda iteração, repita o *Step Into*: note que a chamada salta para o método na classe `PagamentoPix`, comprovando que a JVM decide o caminho em tempo de execução com base no objeto real.

## 6. Exercício de Fixação Prática: Sistema Tarifário de Contas Bancárias

Implemente um motor de gestão de contas bancárias aplicando herança, polimorfismo dinâmico e segregação por interfaces:

1. **Construa a Interface `Tributavel`:**
   - Declaração de método: `double calcularTributo()`.

2. **Construa a Classe Abstrata `Conta`:**
   - Atributos protegidos: `numeroConta` (`String`) e `saldo` (`double`).
   - Construtor parametrizado completo para ambos os campos.
   - Método concreto `void depositar(double valor)` que incrementa o saldo.
   - Método abstrato `public abstract boolean sacar(double valor)`.
   - Getter para saldo e método descritivo `exibirDados()`.

3. **Construa a Subclasse `ContaCorrente`:**
   - Herda de `Conta` e implementa a interface `Tributavel`.
   - Construtor utilizando `super(...)`.
   - Sobrescreve `sacar(double valor)` descontando uma taxa de saque fixa de R$ 1,50 a cada operação bem-sucedida.
   - Implementa `calcularTributo()` retornando 1% do saldo total atual da conta.

4. **Construa a Subclasse `ContaPoupanca`:**
   - Herda de `Conta` (não é tributável).
   - Construtor encadeado com `super(...)`.
   - Sobrescreve `sacar(double valor)` permitindo o saque sem cobrança de taxas operacionais, desde que haja saldo disponível.

5. **Construa a Classe Executável `BancoApp`:**
   - Crie um vetor heterogêneo `Conta[]` com instâncias de ambas as classes filhas.
   - Itere sobre o vetor executando saques de forma polimórfica e exibindo os saldos resultantes.
   - Crie um array separado `Tributavel[]` agrupando as instâncias tributáveis e imprima o somatório total de tributos recolhidos.
