# Tutorial de Java — Aula 01: Paradigmas de Programação e a Plataforma Java

| | |
|---|---|
| **Disciplina** | Programação Orientada a Objetos |
| **Curso** | Análise e Desenvolvimento de Sistemas (3º Período) |
| **Tema** | Paradigmas de Programação (Procedural vs. OO) e o Ecossistema Java |
| **Carga Horária** | 100 minutos (2 horas-aula) |
| **Professor Responsável** | Alexandre Neves Louzada |
| **Arquivo de Referência** | `TutorialAula1.md` |

## 1. Objetivos de Aprendizagem

- **Conceitual:** Diferenciar o acoplamento e a separação entre dados e funções (modelo procedural/estruturado) do encapsulamento coeso de estado e comportamento (modelo orientado a objetos).
- **Técnico:** Compreender o pipeline de compilação e execução da plataforma Java (JDK, JRE, JVM, Bytecode e compilação JIT).
- **Operacional:** Executar manualmente o ciclo de compilação (`javac`), execução (`java`) e inspeção de instruções de pilha (`javap -c`) no terminal, sem dependência de IDEs.
- **Arquitetural:** Compreender a organização da memória de tempo de execução da JVM (separação entre *frames* da Stack e alocação dinâmica no Heap) e o papel do Garbage Collector (GC).

## 2. Fundamentação Teórica

### Paradigma Procedural vs. Orientado a Objetos

No paradigma procedural/estruturado, o desenvolvimento é orientado a ações e fluxos sequenciais. A clássica equação formulada por Niklaus Wirth resume esse modelo:

```plaintext
Programa = Algoritmos + Estruturas de Dados
```

Nesse cenário, dados (structs, variáveis globais e registros locais) ficam totalmente separados dos procedimentos e funções que os manipulam. Em sistemas de grande porte, essa separação gera fragilidades críticas de engenharia:

- **Exposição Direta de Dados:** Múltiplas funções acessam e modificam os mesmos registros sem barreiras de proteção.
- **Alto Acoplamento:** Mudar a estrutura interna de um registro exige alterar dezenas de funções espalhadas pelo código.
- **Violação de Invariantes de Negócio:** Dados inconsistentes trafegam livremente pelo sistema (por exemplo, contas bancárias com saldos negativos arbitrários).

Na Programação Orientada a Objetos (POO), o foco muda da ação isolada para a entidade responsável. O objeto reúne estado (atributos) e comportamento (métodos) sob uma mesma barreira protetora. O estado interno é blindado via modificadores de acesso (`private`), garantindo que o objeto seja o único responsável pela integridade de suas regras de negócio.

| Critério de Comparação | Paradigma Procedural / Estruturado | Paradigma Orientado a Objetos (POO) |
|---|---|---|
| **Foco Central** | Verbo / Ação (*Como* processar) | Substantivo / Entidade (*Quem* é responsável) |
| **Organização** | Funções e procedimentos soltos manipulando registros | Classes encapsulando estado e comportamento |
| **Segurança dos Dados** | Baixa (dados expostos à mutação direta) | Alta (encapsulamento e visibilidade) |
| **Escalabilidade** | Frágil e sujeita a efeitos colaterais | Modular, desacoplada e extensível |

### A Arquitetura da Plataforma Java: Write Once, Run Anywhere (WORA)

A portabilidade e a independência de plataforma do ecossistema Java apoiam-se no conceito de código intermediário interpretado e compilado dinamicamente:

```plaintext
Código-Fonte (.java)
        │
        │  javac — Compilador
        ▼
Bytecode (.class)
        │
        │  JVM — Interpretação + compilação JIT
        ▼
Código Nativo (SO / Hardware)
```

- **JDK (Java Development Kit):** Kit completo de desenvolvimento que inclui o compilador (`javac`), ferramentas de diagnóstico (`javap`), documentador (`javadoc`) e as bibliotecas básicas.
- **JRE (Java Runtime Environment):** Ambiente voltado para execução em servidores e máquinas de usuários, composto pelas bibliotecas essenciais e pela JVM.
- **JVM (Java Virtual Machine):** Máquina virtual que isola a aplicação das particularidades de cada sistema operacional. Contém o compilador JIT (*Just-In-Time*), que monitora os trechos de código executados com frequência (*hot spots*) e os compila em tempo real diretamente para código de máquina ultraotimizado.

### Ciclo de Vida da Memória: Stack, Heap e Garbage Collector

A execução na JVM divide o gerenciamento de memória em duas regiões principais:

- **Stack (Pilha de Execução):** Gerenciada de forma automática e rápida. Armazena os quadros de chamada de métodos (*Stack Frames*), variáveis locais de tipos primitivos e ponteiros de referência.
- **Heap (Área Dinâmica):** Espaço compartilhado onde residem fisicamente todas as instâncias de objetos alocadas por meio do operador `new`.
- **Garbage Collector (GC):** Processo em segundo plano que rastreia e desaloca objetos inalcançáveis no Heap (sem ponteiros ativos na Stack), prevenindo falhas estruturais e vazamentos de memória.

## 3. Estudo de Caso Comparativo: Quebra de Invariante vs. Estado Blindado

### Abordagem Estruturada (Dados Expostos e Inseguros)

No modelo procedural, os dados ficam abertos a alterações indevidas por qualquer rotina externa:

```java
public class BancoProceduralApp {
    public static void main(String[] args) {
        // Dados soltos na Stack sem proteção contra mutação indevida
        double saldo = 100.0;

        // Função utilitária externa aplicando saque legítimo
        saldo = sacar(saldo, 40.0);
        System.out.printf("Saldo após saque legítimo: R$ %.2f%n", saldo);

        // Ponto de Ruptura: qualquer ponto do sistema pode quebrar a invariante
        saldo = -50000.0;
        System.out.printf("ALERTA: Saldo violado arbitrariamente: R$ %.2f%n", saldo);
    }

    public static double sacar(double saldoAtual, double valor) {
        if (valor > 0 && saldoAtual >= valor) {
            return saldoAtual - valor;
        }
        System.err.println("Erro: Saldo insuficiente ou valor inválido.");
        return saldoAtual;
    }
}
```

### Abordagem Orientada a Objetos (Estado Blindado e Coeso)

Na POO, a responsabilidade pela integridade é transferida para a própria classe, impedindo mutações inconsistentes:

```java
// Entidade autônoma e responsável pela sua própria consistência
class ContaBancaria {
    // Atributo privado residente no Heap: inacessível diretamente por código externo
    private double saldo;

    public ContaBancaria(double saldoInicial) {
        if (saldoInicial >= 0) {
            this.saldo = saldoInicial;
        } else {
            this.saldo = 0.0;
        }
    }

    public boolean sacar(double valor) {
        // A própria classe rejeita a operação e protege sua invariante
        if (valor > 0 && this.saldo >= valor) {
            this.saldo -= valor;
            return true;
        }
        return false;
    }

    public void depositar(double valor) {
        if (valor > 0) {
            this.saldo += valor;
        }
    }

    public double getSaldo() {
        return this.saldo;
    }
}

public class BancoOOApp {
    public static void main(String[] args) {
        // Alocação da referência na Stack e da instância no Heap
        ContaBancaria conta = new ContaBancaria(100.0);

        // Tentativa de saque que viola a regra de negócio é rejeitada pelo objeto
        boolean sucesso = conta.sacar(150.0);
        System.out.println("Saque de R$ 150.00 autorizado? " + sucesso);
        System.out.printf("Saldo preservado: R$ %.2f%n", conta.getSaldo());

        // A instrução abaixo geraria erro de compilação:
        // conta.saldo = -50000.0;
    }
}
```

## 4. Laboratório Hands-on: Compilação Manual e Inspeção de Bytecode

Para compreender o funcionamento da JVM sem o auxílio de IDEs, execute o roteiro abaixo diretamente no terminal do seu sistema operacional.

### Passo 1: Criação do Arquivo Fonte

Crie um arquivo chamado `OlaMundo.java`:

```java
public class OlaMundo {
    public static void main(String[] args) {
        System.out.println("Fundamentos de POO e Plataforma Java!");
    }
}
```

### Passo 2: Compilação Manual via CLI

Abra o terminal na pasta do arquivo e invoque o compilador:

```bash
javac OlaMundo.java
```

**Efeito:** O arquivo `OlaMundo.class` contendo as instruções binárias de Bytecode será gerado.

### Passo 3: Execução na JVM

Execute o programa chamando a máquina virtual:

```bash
java OlaMundo
```

**Saída esperada no console:** `Fundamentos de POO e Plataforma Java!`

### Passo 4: Desmontagem e Inspeção de Bytecode

Utilize o utilitário `javap` para inspecionar as instruções de pilha geradas pelo compilador:

```bash
javap -c OlaMundo
```

**Instruções de baixo nível retornadas:**

```plaintext
Compiled from "OlaMundo.java"
public class OlaMundo {
  public OlaMundo();
    Code:
       0: aload_0
       1: invokespecial #1 // Method java/lang/Object."<init>":()V
       4: return

  public static void main(java.lang.String[]);
    Code:
       0: getstatic     #7 // Field java/lang/System.out:Ljava/io/PrintStream;
       3: ldc           #13 // String Fundamentos de POO e Plataforma Java!
       5: invokevirtual #15 // Method java/io/PrintStream.println:(Ljava/lang/String;)V
       8: return
}
```

**Análise das instruções de pilha:**

- `getstatic #7`: Carrega a referência estática de `System.out` na Stack.
- `ldc #13`: Carrega a literal String no topo da Stack (*Load Constant*).
- `invokevirtual #15`: Executa o método `println`, desempilhando os argumentos necessários.
- `return`: Encerra a execução do *frame* do método `main`.

## 5. Diagnóstico de Erros Comuns de Ambiente e Compilação

### Incompatibilidade de Nome de Arquivo e Classe Pública

- **Cenário:** Arquivo salvo como `Principal.java`, contendo internamente `public class OlaMundo`.
- **Mensagem:**
```plaintext
class OlaMundo is public, should be declared in a file named OlaMundo.java
  ```
- **Causa & Correção:** Em Java, classes públicas exigem obrigatoriamente que o nome do arquivo seja idêntico ao nome da classe, respeitando maiúsculas e minúsculas (*case-sensitive*).

### Ausência do Modificador Estático no Método main

- **Cenário:** Método declarado como `public void main(String[] args)`.
- **Mensagem:**
```plaintext
Main method is not static in class OlaMundo, please define the main method as:
  public static void main(String[] args)
  ```
- **Causa & Correção:** O ponto de entrada da aplicação precisa ser `static` para permitir que a JVM o execute sem precisar instanciar a classe no Heap previamente.

### Tentativa de Acesso Direto a Membro Privado

- **Cenário:** Escrever `conta.saldo = 200.0;` a partir de uma classe externa.
- **Mensagem:**
```plaintext
saldo has private access in ContaBancaria
  ```
- **Causa & Correção:** Atributos com modificador `private` só podem ser acessados ou alterados através dos métodos públicos fornecidos pela própria classe.

## 6. Exercício de Fixação Prática: Controle de Estoque Seguro

Implemente uma rotina que aplique os princípios de blindagem de estado e encapsulamento corporativo:

1. **Construa a classe `ItemEstoque`:**
   - Atributos privados: `codigo` (String), `nome` (String), `quantidade` (int) e `valorUnitario` (double).
   - Construtor parametrizado que impeça estoque inicial negativo (iniciando em 0 caso um valor inválido seja passado) e bloqueie valores unitários menores ou iguais a zero.
   - Método de negócio `boolean adicionarEstoque(int qtd)`: incrementa o estoque apenas se a quantidade for maior que zero.
   - Método de negócio `boolean removerEstoque(int qtd)`: decrementa o estoque apenas se a quantidade solicitada for positiva e houver saldo suficiente, retornando `false` caso contrário.

2. **Construa a classe `EstoqueApp`:**
   - Instancie dois itens de estoque no Heap.
   - Realize simulações de movimentação, demonstrando no console que a entidade barra retiradas excessivas e preserva suas invariantes.

3. **Desafio CLI:** Compile e execute ambas as classes via terminal com `javac` e `java`, inspecionando o bytecode da classe compilada com o comando `javap -c ItemEstoque`.