# Plano de Aula e Roteiro de Slides: Aula 02

**Disciplina:** Programação Orientada a Objetos  
**Curso:** Análise e Desenvolvimento de Sistemas (3º Período)  
**Tema:** Ambiente de Desenvolvimento, Comandos Básicos, Sistema de Tipos e Estruturas de Controle  
**Carga Horária:** 100 minutos (2 horas-aula)  
**Professor:** Alexandre Neves Louzada

---

## 1. Plano de Ensino da Aula

### 1.1. Objetivos de Aprendizagem
* **Conceitual:** Compreender o sistema de tipos estático e fortemente tipado de Java, a hierarquia de tipos primitivos versus tipos por referência e a semântica dos operadores com curto-circuito.
* **Técnico:** Dominar a conversão e coerção de tipos (*casting* implícito e explícito), estruturas de decisão encadeadas (`if/else`, *Switch Expressions* modernas) e laços de repetição (`while`, `do-while`, `for`).
* **Operacional:** Manipular fluxos de entrada e saída formatada no console (`java.util.Scanner` e `System.out.printf`), tratando armadilhas de buffer de teclado.
* **Prático:** Utilizar recursos de depuração (*Debug*) em IDE profissional (IntelliJ IDEA / Eclipse / VS Code) para inspecionar variáveis na Stack e acompanhar saltos de fluxo em tempo de execução.

### 1.2. Metodologia Ativa
* **Live Coding & Diagnóstico Coletivo:** Demonstração interativa pelo professor com inserção proposital de erros clássicos (divisão inteira truncada, perda de precisão em coerção, buffer residual do `Scanner` e laços infinitos) para identificação e resolução em tempo real com a turma.
* **Laboratório Hands-on:** Implementação orientada a testes de mesa de um simulador de aplicações financeiras com validação rigorosa de dados de entrada.

---

## 2. Roteiro Detalhado de Slides

---

### Slide 1: Abertura e Visão Geral
* **Título do Slide:** Ambiente de Desenvolvimento, Sistema de Tipos & Controle de Fluxo
* **Tópicos Visuais:**
  * Da linha de comando à IDE profissional: produtividade, refatoração e depuração.
  * O sistema de tipos do Java: tipagem estática, forte e segura.
  * Interação via console: leitura com `Scanner` e saída formatada com `printf`.
  * Estruturas condicionais e laços de repetição aplicados a regras de validação.
* **Notas Pedagógicas do Professor:**
  * Conectar com a Aula 01: agora que dominamos a mecânica da JVM e do compilador, o foco passa a ser a sintaxe fundamental da linguagem e o domínio da ferramenta de desenvolvimento profissional.
  * Destacar a tipagem estática: em Java, toda variável possui um tipo imutável definido em tempo de compilação, o que previne comportamentos imprevisíveis comuns em linguagens dinâmicas.

---

### Slide 2: Anatomia de um Projeto Java em IDE
* **Título do Slide:** Estrutura Física, Pacotes (`package`) e Convenções
* **Tópicos Visuais:**
  * Separação de diretórios:
    * `src/`: Código-fonte (`.java`).
    * `out/` ou `bin/` ou `target/`: Bytecode compilado (`.class`).
  * A instrução `package`:
    ```java
    package br.edu.universidade.sistema.modulo;
    ```
  * Papel dos Pacotes: *Namespace* (evitar conflitos de nomenclatura de classes) e organização modular de componentes.
* **Notas Pedagógicas do Professor:**
  * Mostrar na IDE como a hierarquia de pacotes mapeia diretamente as pastas no sistema de arquivos do sistema operacional.
  * Reforçar o padrão corporativo de nomenclatura: domínio reverso da instituição em letras minúsculas (`br.gov...`, `com.empresa...`).

---

### Slide 3: Sistema de Tipos: Primitivos vs. Referências
* **Título do Slide:** Tipagem Estática: Tipos Primitivos e Tipos por Referência
* **Tópicos Visuais:**
  * **Tipos Primitivos (armazenados diretamente na Stack):**
    * *Inteiros:* `byte` (8 bits), `short` (16 bits), `int` (32 bits — padrão), `long` (64 bits, literal `L`).
    * *Ponto Flutuante:* `float` (32 bits, literal `F`), `double` (64 bits — padrão).
    * *Caractere e Lógico:* `char` (16 bits Unicode, aspas simples `'A'`), `boolean` (`true` ou `false`).
  * **Tipo por Referência Inicial:** `String` (objeto imutável no Heap, aspas duplas `"texto"`).
  * **Coerção de Tipos (Casting):**
    * *Implícito (Promoção):* `int` $\rightarrow$ `double` (sem perda de dados).
    * *Explícito (Coerção):* `(int) 3.85` $\rightarrow$ resulta em `3` (truncamento de fração).
* **Notas Pedagógicas do Professor:**
  * Alertar sobre o erro clássico da divisão inteira: `int a = 5, b = 2; double c = a / b;` resulta em `2.0` (e não `2.5`). A divisão ocorre em inteiros antes da atribuição, exigindo `(double) a / b`.
  * Explicar que tipos primitivos não são objetos e não possuem métodos associados.

---

### Slide 4: Operadores e Avaliação de Curto-Circuito
* **Título do Slide:** Operadores Aritméticos, Relacionais e Lógicos
* **Tópicos Visuais:**
  * **Aritméticos e Atribuição Composta:** `+`, `-`, `*`, `/`, `%` (módulo/resto), `+=`, `-=`.
  * **Incremento / Decremento:** Pré-fixado (`++x` — incrementa antes de avaliar) vs. Pós-fixado (`x++` — avalia antes de incrementar).
  * **Relacionais:** `==`, `!=`, `>`, `<`, `>=`, `<=`.
  * **Lógicos e o Mecanismo de Curto-Circuito (*Short-Circuit*):**
    * `&&` (E com curto-circuito): interrompe a avaliação se o primeiro termo for `false`.
    * `||` (OU com curto-circuito): interrompe a avaliação se o primeiro termo for `true`.
    * `!` (Negação).
* **Notas Pedagógicas do Professor:**
  * Demonstrar por que o curto-circuito é fundamental para blindagem contra erros:
    `if (cliente != null && cliente.temSaldo())` $\rightarrow$ Se `cliente` for nulo, a segunda parte não é executada, evitando `NullPointerException`.

---

### Slide 5: Entrada de Dados e Formatação de Saída
* **Título do Slide:** Interação com o Usuário: `Scanner` e `System.out.printf`
* **Tópicos Visuais:**
  * **Leitura com `java.util.Scanner`:**
    ```java
    Scanner scanner = new Scanner(System.in);
    System.out.print("Informe o salário: R$ ");
    double salario = scanner.nextDouble();
    scanner.nextLine(); // Consome o \n residual deixado pelo nextDouble
    ```
  * **Saída Formatada com `printf`:**
    * `%d`: Inteiro decimal.
    * `%.2f`: Ponto flutuante formatado com 2 casas decimais.
    * `%s`: Texto / String.
    * `%n`: Quebra de linha independente de sistema operacional (POSIX vs. Windows).
* **Notas Pedagógicas do Professor:**
  * Enfatizar a armadilha do **buffer residual**: explicar que `nextInt()` e `nextDouble()` leem apenas os dígitos e deixam a tecla "Enter" (`\n`) no buffer, fazendo com que o próximo `nextLine()` leia uma linha vazia.

---

### Slide 6: Estruturas Condicionais de Decisão
* **Título do Slide:** Controle de Fluxo: `if-else`, Operador Ternário e `switch`
* **Tópicos Visuais:**
  * Estruturas `if`, `else if`, `else` encadeadas.
  * Operador Ternário: `resultado = condicao ? valorSeTrue : valorSeFalse;`
  * *Switch Expressions* Modernas (Java 14+):
    ```java
    double aliquota = switch (categoria) {
        case 1 -> 0.05;
        case 2, 3 -> 0.10;
        case 4 -> 0.15;
        default -> 0.20;
    };
    ```
* **Notas Pedagógicas do Professor:**
  * Comparar a sintaxe moderna com o `switch` clássico, demonstrando como a sintaxe de flecha (`->`) elimina a necessidade de `break` e previne o risco de *fall-through* acidental.
  * Apresentar boas práticas de código limpo: evitar aninhamento profundo de condicionais (*Arrow Anti-Pattern*).

---

### Slide 7: Estruturas de Repetição e Iteração
* **Título do Slide:** Laços de Repetição: `while`, `do-while` e `for`
* **Tópicos Visuais:**
  * `while`: Teste prévio no início (executa 0 ou mais vezes) $\rightarrow$ ideal para validação indeterminada de entrada.
  * `do-while`: Teste no final (executa ao menos 1 vez obrigatoriamente) $\rightarrow$ ideal para menus interativos.
  * `for` tradicional: Controle por contador definido (`for (int i = 0; i < n; i++)`).
  * Comandos de Interrupção de Fluxo:
    * `break`: Encerra o laço imediatamente.
    * `continue`: Ignora as instruções restantes do bloco e salta para a próxima iteração.
* **Notas Pedagógicas do Professor:**
  * Enfatizar o critério de escolha semântica: use `for` quando o espaço amostral ou limite for conhecido de antemão; use `while` quando a condição de parada depender de eventos dinâmicos ou entradas do usuário.

---

### Slide 8: Estudo de Caso de Código Integrado
* **Título do Slide:** Código Integrador: Validação e Simulação Financeira
* **Tópicos Visuais:**
  ```java
  public class ValidadorInvestimento {
      public static void main(String[] args) {
          Scanner scanner = new Scanner(System.in);
          double aporte = 0.0;

          // Validação robusta de entrada positiva com do-while
          do {
              System.out.print("Informe o aporte inicial (> 0): R$ ");
              while (!scanner.hasNextDouble()) {
                  System.out.println("Entrada inválida! Digite um valor numérico.");
                  scanner.next(); // Descarta entrada inválida
              }
              aporte = scanner.nextDouble();
          } while (aporte <= 0);

          System.out.printf("Aporte validado com sucesso: R$ %.2f%n", aporte);
      }
  }