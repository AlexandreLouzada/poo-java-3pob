# Explicação da Aula 02 — Ambiente de Desenvolvimento, Comandos Básicos, Sistema de Tipos e Estruturas de Controle

| | |
|---|---|
| **Disciplina** | Programação Orientada a Objetos |
| **Curso** | Análise e Desenvolvimento de Sistemas (3º Período) |
| **Tema** | Ambiente de Desenvolvimento, Comandos Básicos, Sistema de Tipos e Estruturas de Controle |
| **Carga Horária** | 100 minutos (2 horas-aula) |
| **Professor** | Alexandre Neves Louzada |
| **Plano de Aula** | `Aulas 3pob/Aula 02.md` |
| **Tutorial** | `aulas/TutorialAula02.md` |
| **Estudo de Caso** | `exemplos/aula-02/` |
| **Exercícios Resolvidos** | `solucoes/aula-02/` |

## 1. Objetivos de Aprendizagem

- **Conceitual:** Compreender o sistema de tipos estático e fortemente tipado de Java, a hierarquia de tipos primitivos versus tipos por referência e a semântica dos operadores com curto-circuito.
- **Técnico:** Dominar a conversão e coerção de tipos (*casting* implícito e explícito), estruturas de decisão encadeadas (`if/else`, *Switch Expressions* modernas) e laços de repetição (`while`, `do-while`, `for`).
- **Operacional:** Manipular fluxos de entrada e saída formatada no console (`java.util.Scanner` e `System.out.printf`), tratando armadilhas de buffer de teclado.
- **Prático:** Utilizar recursos de depuração (*Debug*) em IDE profissional (IntelliJ IDEA / Eclipse / VS Code) para inspecionar variáveis na Stack e acompanhar saltos de fluxo em tempo de execução.

## 2. Conteúdo Teórico Detalhado

### 2.1 Conexão com a Aula Anterior e Objetivo desta Aula

Na Aula 01, dominamos a mecânica da JVM e do compilador Java. Agora o foco passa para a sintaxe fundamental da linguagem e o domínio da ferramenta de desenvolvimento profissional. A transição é clara: conhecemos o "motor" (JVM); agora vamos aprender a "dirigir" (sintaxe e ferramentas).

Destaca-se a tipagem estática de Java: toda variável possui um tipo imutável definido em tempo de compilação, o que previne comportamentos imprevisíveis comuns em linguagens dinâmicas como Python ou JavaScript.

### 2.2 Anatomia de um Projeto Java em IDE

Um projeto Java em ambiente de desenvolvimento segue uma organização física de diretórios padrão:

- `src/`: Contém o código-fonte (arquivos `.java`).
- `out/` ou `bin/` ou `target/`: Contém o bytecode compilado (arquivos `.class`).

A instrução `package` define o pacote ao qual uma classe pertence:

```java
package br.edu.universidade.sistema.modulo;
```

Os pacotes desempenham dois papéis fundamentais:
- **Namespace:** Evitam conflitos de nomenclatura de classes. Duas classes com o mesmo nome podem coexistir pacotes diferentes.
- **Organização modular:** Agrupam componentes relacionados, facilitando a navegação e manutenção em grandes bases de código.

A hierarquia de pacotes mapeia diretamente as pastas no sistema de arquivos do sistema operacional. O padrão corporativo de nomenclatura segue o domínio reverso: `br.gov...`, `com.empresa...`, `br.edu.universidade...`.

### 2.3 Sistema de Tipos: Primitivos vs. Referências

Java possui um sistema de tipos estático e fortemente tipado. Existem duas categorias fundamentais:

**Tipos Primitivos (armazenados diretamente na Stack):**

| Tipo | Tamanho | Faixa / Descrição | Literal |
|:---|:---|:---|:---|
| `byte` | 8 bits | -128 a 127 | `127` |
| `short` | 16 bits | -32.768 a 32.767 | `32000` |
| `int` | 32 bits | -2^31 a 2^31-1 (padrão) | `42` |
| `long` | 64 bits | -2^63 a 2^63-1 | `100L` |
| `float` | 32 bits | Precisão limitada | `3.14F` |
| `double` | 64 bits | Precisão dupla (padrão) | `2.718` |
| `char` | 16 bits | Unicode (0 a 65.535) | `'A'` |
| `boolean` | 1 bit (implementação) | `true` ou `false` | `true` |

Tipos primitivos **não são objetos** e não possuem métodos associados. São armazenados diretamente na Stack, o que os torna extremamente eficientes.

**Tipo por Referência Inicial — `String`:**
- `String` é um objeto imutável alocado no Heap, representado por literais entre aspas duplas (`"texto"``.
- Apesar de ser o primeiro tipo por referência que o estudante encontra, `String` possui comportamento especial no Java (imutabilidade, *intern pool*).

**Coerção de Tipos (Casting):**
- **Implícito (Promoção):** Quando um tipo menor é atribuído a um tipo maior, a conversão ocorre automaticamente sem perda de dados. Exemplo: `int` para `double` (`double c = 5;` resulta em `5.0`).
- **Explícito (Coerção):** Quando é necessário forçar a conversão de um tipo maior para um menor, usando o operador de cast. Exemplo: `(int) 3.85` resulta em `3` (truncamento da fração, não arredondamento).

**Erro clássico da divisão inteira:** `int a = 5, b = 2; double c = a / b;` resulta em `2.0` (e não `2.5`), pois a divisão ocorre entre inteiros antes da atribuição. A correção é `(double) a / b` ou `a / (double) b`, que promove pelo menos um dos operandos para `double` antes da divisão.

### 2.4 Operadores e Avaliação de Curto-Circuito

**Operadores Aritméticos e Atribuição Composta:**

| Operador | Descrição | Exemplo |
|:---|:---|:---|
| `+` | Adição | `a + b` |
| `-` | Subtração | `a - b` |
| `*` | Multiplicação | `a * b` |
| `/` | Divisão | `a / b` |
| `%` | Módulo (resto da divisão) | `a % b` |
| `+=` | Atribuição com adição | `a += 5` (equivalente a `a = a + 5`) |
| `-=` | Atribuição com subtração | `a -= 3` |

**Incremento / Decremento:**
- Pré-fixado (`++x`): incrementa o valor **antes** de avaliar a expressão.
- Pós-fixado (`x++`): avalia a expressão **antes** de incrementar o valor.

**Operadores Relacionais:** `==`, `!=`, `>`, `<`, `>=`, `<=` — retornam `boolean`.

**Operadores Lógicos e Curto-Circuito (Short-Circuit):**

| Operador | Comportamento |
|:---|:---|
| `&&` (E lógico) | Avalia o segundo operando **apenas** se o primeiro for `true`. Se o primeiro for `false`, o resultado é imediatamente `false`. |
| `\|\|` (OU lógico) | Avalia o segundo operando **apenas** se o primeiro for `false`. Se o primeiro for `true`, o resultado é imediatamente `true`. |
| `!` (Negação) | Inverte o valor lógico: `!true` = `false`. |

O curto-circuito é fundamental para blindagem contra erros. Exemplo clássico:

```java
if (cliente != null && cliente.temSaldo())
```

Se `cliente` for nulo, a segunda parte (`cliente.temSaldo()`) **não é executada**, evitando uma `NullPointerException`. Sem o curto-circuito (usando `&` em vez de `&&`), ambos os lados seriam avaliados, causando a exceção.

### 2.5 Entrada de Dados e Formatação de Saída

**Leitura com `java.util.Scanner`:**

```java
Scanner scanner = new Scanner(System.in);
System.out.print("Informe o salário: R$ ");
double salario = scanner.nextDouble();
scanner.nextLine(); // Consome o \n residual deixado pelo nextDouble
```

**Saída Formatada com `printf`:**

| especificador | Tipo | Descrição |
|:---|:---|:---|
| `%d` | `int` / `long` | Inteiro decimal |
| `%.2f` | `float` / `double` | Ponto flutuante com 2 casas decimais |
| `%s` | `String` | Texto / String |
| `%n` | — | Quebra de linha independente de SO ( POSIX vs. Windows) |

**Armadilha do Buffer Residual:** Métodos como `nextInt()` e `nextDouble()` leem apenas os dígitos numéricos e deixam a tecla "Enter" (`\n`) no buffer de entrada. O próximo `nextLine()` lê imediatamente esse `\n` restante, resultando em uma linha vazia. A solução é chamar `scanner.nextLine()` logo após `nextInt()` ou `nextDouble()` para consumir o caractere residual.

### 2.6 Estruturas Condicionais de Decisão

**Estruturas `if-else`:** Permitem ramificação do fluxo de execução com base em condições booleanas:

```java
if (condicao1) {
    // bloco 1
} else if (condicao2) {
    // bloco 2
} else {
    // bloco padrão
}
```

**Operador Ternário:** Forma reduzida de `if-else` para atribuições simples:

```java
resultado = condicao ? valorSeTrue : valorSeFalse;
```

**Switch Expressions Modernas (Java 14+):** A sintaxe de flecha (`->`) elimina a necessidade de `break` e previne o risco de *fall-through* acidental:

```java
double aliquota = switch (categoria) {
    case 1 -> 0.05;
    case 2, 3 -> 0.10;
    case 4 -> 0.15;
    default -> 0.20;
};
```

Comparada com o `switch` clássico (que exige `break` após cada `case`), a sintaxe moderna é mais segura e concisa. Boas práticas de código limpo recomendam evitar aninhamento profundo de condicionais (*Arrow Anti-Pattern*).

### 2.7 Estruturas de Repetição e Iteração

Java oferece três estruturas de laço, cada uma com seu caso de uso ideal:

| Laço | Teste | Execução Mínima | Caso de Uso Ideal |
|:---|:---|:---|:---|
| `while` | Prévio (início) | 0 vezes | Validação indeterminada de entrada |
| `do-while` | Final (fim) | 1 vez | Menus interativos |
| `for` | Prévio (início) | 0 vezes | Iteração com contador de limite conhecido |

**Comandos de Interrupção de Fluxo:**
- `break`: Encerra o laço imediatamente e transfere o controle para a instrução seguinte.
- `continue`: Ignora as instruções restantes do bloco atual e salta para a próxima iteração do laço.

**Critério de escolha semântica:** Use `for` quando o espaço amostral ou limite for conhecido de antemão (ex.: "iterar 10 vezes"). Use `while` quando a condição de parada depender de eventos dinâmicos ou entradas do usuário (ex.: "enquanto o usuário não digitar um valor válido").

## 3. Estudo de Caso Aplicado

O estudo de caso da Aula 02 está em `exemplos/aula-02/src/br/edu/universidade/sistema/modulo/` e contém uma única classe:

- **`ValidadorInvestimento.java`** — Classe com método `main` que implementa a validação robusta de entrada de dados para um aporte financeiro. O código combina múltiplos conceitos da aula: uso de `Scanner` para leitura do console, validação com laço `do-while` (executa ao menos uma vez), validação de tipo com `hasNextDouble()` dentro de um laço `while` interno para descartar entradas não numéricas, formatação de saída com `printf`, e uso de `Locale.US` para garantir ponto como separador decimal. O pacote `br.edu.universidade.sistema.modulo` demonstra a organização em pastas conforme o padrão corporativo.

## 4. Exercícios Propostos e Solução

A solução está em `solucoes/aula-02/src/br/edu/universidade/sistema/modulo/` e contém uma classe:

- **`SimuladorRentabilidadeApp.java`** — Simulador financeiro que exercita todos os conceitos da aula em um contexto prático. O programa lê do console o capital inicial, o prazo em meses e o perfil do investidor (Conservador, Moderado ou Arrojado), aplicando diferentes taxas mensais para cada perfil.

  Conceitos exercitados:
  - **Validação de entrada:** Três métodos privados dedicados (`lerCapital`, `lerPrazoMeses`, `lerPerfilInvestidor`) utilizam laços `while(true)` com `break` e validação de tipo via `hasNextDouble()` / `hasNextInt()`.
  - **Switch Expressions:** Dois `switch` modernos (Java 14+) mapeiam o perfil para a taxa mensal e para o nome do perfil, sem necessidade de `break`.
  - **Laço `for`:** Itera mês a mês, aplicando juros compostos e imprimindo o saldo acumulado com `printf`.
  - **Saída formatada:** Formatação com `printf` para exibir valores monetários com 2 casas decimais e percentuais com 1 casa decimal.
  - **Organização:** Método `main` delega a leitura para métodos auxiliares `private static`, demonstrando separação de responsabilidades mesmo dentro de uma classe procedural.

## 5. Perguntas de Revisão

1. **Qual a diferença entre `int a = 5, b = 2; double c = a / b;` e `double c = (double) a / b;`?**
   - No primeiro caso, a divisão `a / b` ocorre entre inteiros, resultando em `2`, que é promovido para `2.0`. No segundo caso, o cast `(double) a` promove o `5` para `5.0` antes da divisão, resultando em `2.5`.

2. **Por que o curto-circuito (`&&` e `||`) é importante para evitar erros?**
   - Porque interrompe a avaliação do segundo operando quando o resultado já é determinado pelo primeiro. Isso evita `NullPointerException` (ex.: `cliente != null && cliente.temSaldo()`) e outras exceções decorrentes de operações sobre valores nulos ou inválidos.

3. **Qual laço de repetição deve ser usado para menus interativos e por quê?**
   - O `do-while`, porque garante que o menu seja exibido e processado pelo menos uma vez antes da verificação da condição de continuidade.

4. **O que acontece se você não consumir o `\n` residual após `scanner.nextDouble()`?**
   - O próximo `scanner.nextLine()` lerá o `\n` restante no buffer, retornando imediatamente uma string vazia sem aguardar entrada do usuário.

## 6. Resumo / Pontos-Chave

- Java é uma linguagem com tipagem estática e forte: todo tipo é verificado em tempo de compilação.
- Tipos primitivos são armazenados na Stack e não são objetos; `String` é o primeiro tipo por referência (alocado no Heap).
- A coerção implícita (promoção) ocorre sem perda; a coerção explícita (cast) pode truncar dados.
- O curto-circuito lógico (`&&` / `||`) é essencial para evitar exceções em cadeias de validação.
- `Scanner` requer tratamento cuidadoso do buffer residual para evitar leituras vazias.
- `printf` com `%d`, `%.2f`, `%s` e `%n` é a forma padrão de saída formatada em Java.
- Switch Expressions modernas (Java 14+) eliminam o problema de `fall-through` com a sintaxe de flecha (`->`).
- A escolha entre `while`, `do-while` e `for` deve ser guiada pela semântica do problema.
