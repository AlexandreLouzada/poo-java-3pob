# Tutorial de Java — Aula 02: Ambiente de Desenvolvimento, Sistema de Tipos e Estruturas de Controle

| | |
|---|---|
| **Disciplina** | Programação Orientada a Objetos |
| **Curso** | Análise e Desenvolvimento de Sistemas (3º Período) |
| **Tema** | Ambiente de Desenvolvimento, Comandos Básicos, Sistema de Tipos e Estruturas de Controle |
| **Carga Horária** | 100 minutos (2 horas-aula) |
| **Professor Responsável** | Alexandre Neves Louzada |
| **Arquivo de Referência** | `TutorialAula2.md` |

## 1. Objetivos de Aprendizagem

- **Conceitual:** Compreender o sistema de tipos estático e fortemente tipado de Java, a hierarquia de tipos primitivos versus tipos por referência e a semântica dos operadores lógicos com avaliação em curto-circuito.
- **Técnico:** Dominar a conversão e coerção de tipos (*casting* implícito e explícito), estruturas de decisão encadeadas (`if`/`else`, *Switch Expressions* modernas) e laços de repetição (`while`, `do-while`, `for`).
- **Operacional:** Manipular fluxos de entrada e saída formatada no console via `java.util.Scanner` e `System.out.printf`, tratando e prevenindo armadilhas de buffer residual de teclado.
- **Prático:** Utilizar recursos de depuração (*Debug*) em IDE profissional (IntelliJ IDEA, Eclipse ou VS Code) para inspecionar variáveis na Stack e acompanhar saltos de fluxo em tempo de execução.

## 2. Fundamentação Teórica

### Anatomia de um Projeto em IDE e o Papel dos Pacotes (package)

À medida que os projetos evoluem de simples scripts para sistemas corporativos, a organização do código-fonte em pastas físicas torna-se obrigatória para evitar colisão de nomes de classes (*namespaces*) e manter a modularidade da aplicação:

- **Estrutura Física Padrão:** O código-fonte reside no diretório `src/` (arquivos `.java`), enquanto o bytecode compilado é armazenado em pastas de saída como `out/`, `bin/` ou `target/` (arquivos `.class`).
- **A Instrução `package`:** Representa a primeira linha executável de um arquivo fonte:
  ```java
  package br.edu.universidade.sistema.modulo;
  ```
- **Convenção de Nomenclatura:** Adota-se o domínio corporativo em ordem reversa, utilizando exclusivamente letras minúsculas (por exemplo, `br.gov...`, `com.empresa...`). Essa declaração mapeia rigorosamente a árvore de diretórios no sistema operacional.

### Tipagem Estática: Tipos Primitivos vs. Tipos por Referência

Java é uma linguagem de tipagem estática e fortemente tipada: toda variável deve ser formalmente declarada com um tipo de dado imutável antes de ser utilizada.

```plaintext
┌─────────────────────────────────────────────────────────────────────────────┐
│                      SISTEMA DE TIPOS DA PLATAFORMA                         │
├──────────────────────────────────────┬──────────────────────────────────────┤
│ Tipos Primitivos (Alocados na Stack) │ Tipos por Referência (Ponteiro/Heap) │
├──────────────────────────────────────┼──────────────────────────────────────┤
│ Inteiros: byte, short, int, long     │ Classes, Enums, Interfaces, Arrays   │
│ Decimais: float, double              │ Exemplo Fundamental: String          │
│ Lógico / Texto: boolean, char        │ Guardam o endereço do objeto no Heap │
└──────────────────────────────────────┴──────────────────────────────────────┘
```

- **Tipos Primitivos** (armazenados diretamente na Stack):
  - **Inteiros:** `byte` (8 bits), `short` (16 bits), `int` (32 bits — tipo padrão para inteiros) e `long` (64 bits, exige o literal `L` ao final).
  - **Ponto Flutuante:** `float` (32 bits, exige o literal `F`) e `double` (64 bits — tipo padrão para números fracionários).
  - **Caractere e Lógico:** `char` (16 bits em formato Unicode, delimitado por aspas simples `'A'`) e `boolean` (armazenando estritamente `true` ou `false`).
- **Tipos por Referência:** A variável declarada na Stack armazena apenas um endereço de memória (ponteiro) que referencia a instância real alocada no Heap. A classe `String` é o exemplo clássico: trata-se de um objeto imutável cujos literais são delimitados por aspas duplas.

**Conversão de Tipos (*Casting*):**

- **Implícito (Promoção Automática):** Conversão segura de um tipo menor para um de maior capacidade sem perda de dados (exemplo: `int` para `double`).
- **Explícito (Coerção Manual):** Conversão forçada sujeita à perda de precisão ou truncamento da parte fracionária (exemplo: `(int) 3.85` resulta em `3`).

### Operadores e Mecanismo de Avaliação em Curto-Circuito

Além dos operadores aritméticos (`+`, `-`, `*`, `/`, `%`), relacionais (`==`, `!=`, `>`, `<`, `>=`, `<=`) e de atribuição composta (`+=`, `-=`), o comportamento dos operadores lógicos é fundamental para a estabilidade do sistema:

- **Operador `&&` (E com curto-circuito):** Interrompe imediatamente a avaliação caso o primeiro termo seja falso (`false`), não processando as expressões seguintes.
- **Operador `||` (OU com curto-circuito):** Interrompe a avaliação caso o primeiro termo seja verdadeiro (`true`).
- **Blindagem de Código:** O curto-circuito impede o disparo de exceções em tempo de execução:
  ```java
  if (cliente != null && cliente.temSaldo()) {
      // O método temSaldo() só é acionado se a referência for comprovadamente não-nula
  }
  ```

Caso `cliente` aponte para `null`, a segunda instrução não é executada, eliminando o risco de `NullPointerException`.

### Entrada com Scanner e Saída Formatada com printf

A interação através do console exige atenção estrita ao comportamento do buffer de teclado:

- **A Armadilha do Buffer Residual:** Os métodos `nextInt()` e `nextDouble()` da classe `Scanner` leem apenas os dígitos e deixam o caractere delimitador de fim de linha (`\n`, gerado pela tecla Enter) pendente no buffer. Se uma chamada subsequente utilizar `nextLine()`, ela consumirá de imediato esse `\n` residual, retornando um texto vazio. A boa prática requer consumir o caractere pendente com uma leitura extra (`scanner.nextLine()`).
- **Especificadores de Formatação com `printf`:**
  - `%d`: Representação de inteiros decimais.
  - `%.2f`: Representação de números decimais com quantidade fixa de casas fracionárias.
  - `%s`: Representação de textos e objetos do tipo `String`.
  - `%n`: Quebra de linha universal compatível com qualquer sistema operacional (POSIX e Windows).

### Estruturas Condicionais e de Repetição

- **`if`, `else if`, `else` e Operador Ternário:** Avaliação de ramificações booleanas.
- ***Switch Expressions* Modernas (Java 14+):** Substituem o modelo legado por uma sintaxe com flechas (`->`), eliminando a necessidade da cláusula `break` e extinguindo a ocorrência de execuções em cascata não intencionais (*fall-through*):
  ```java
  double aliquota = switch (categoria) {
      case 1 -> 0.05;
      case 2, 3 -> 0.10;
      case 4 -> 0.15;
      default -> 0.20;
  };
  ```
- **Laços de Repetição:**
  - `while`: Avaliação prévia da condição de parada (adequado para rotinas com repetições indeterminadas).
  - `do-while`: Executa o bloco de código ao menos uma vez antes de validar a condição no final (ideal para menus interativos de terminal).
  - `for`: Controle orientado a contador (indicado quando o intervalo de repetições é previamente determinado).

## 3. Diagnóstico de Erros Comuns e Armadilhas de Código

### Armadilha 1: Divisão Inteira Truncada

**Código Problemático:**

```java
int a = 5;
int b = 2;
double c = a / b; // Armazena 2.0 em vez de 2.5!
```

**Diagnóstico Técnico:** Em Java, a divisão entre dois números inteiros resulta em um quociente estritamente inteiro; a conversão para `double` ocorre somente no momento da atribuição.

**Resolução:** Force a promoção aritmética declarando ao menos um operando como ponto flutuante: `double c = (double) a / b;`.

### Armadilha 2: Salto Inadvertido com Buffer Residual de Teclado

**Código Problemático:**

```java
Scanner scanner = new Scanner(System.in);
System.out.print("Informe o código da conta: ");
int codigo = scanner.nextInt(); // Deixa o '\n' no buffer

System.out.print("Informe o nome do titular: ");
String titular = scanner.nextLine(); // Consome o '\n' e grava texto vazio!
```

**Diagnóstico Técnico:** O método `nextInt()` extrai os dígitos, mas ignora o caractere delimitador `\n`, que é imediatamente consumido pela instrução subsequente.

**Resolução:** Limpe o buffer residual antes de ler a próxima linha:

```java
int codigo = scanner.nextInt();
scanner.nextLine(); // Esvazia o buffer residual do teclado
String titular = scanner.nextLine();
```

## 4. Estudo de Caso Integrado: Validação e Simulação Financeira

O programa abaixo demonstra a integração de leitura segura via console, descarte defensivo de dados inválidos e controle de fluxo com `do-while`:

```java
package br.edu.universidade.sistema.modulo;

import java.util.Locale;
import java.util.Scanner;

public class ValidadorInvestimento {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
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
        scanner.close();
    }
}
```

## 5. Roteiro Prático de Depuração (Debug na IDE)

Para visualizar as variáveis na Stack e acompanhar as decisões de controle de fluxo em tempo de execução, execute o seguinte procedimento prático:

1. **Definição do Ponto de Interrupção (*Breakpoint*):** No arquivo `ValidadorInvestimento`, clique na margem esquerda da linha do `{` para adicionar o ponto de parada.
2. **Inicialização em Modo Debug:** Execute o programa acionando o comando de depuração (*Debug*) da IDE.
3. **Inspeção de Variáveis:** Acesse a janela *Variables / Frames* e observe a variável local `aporte` com valor `0.0` alocada no *frame* do método `main`.
4. **Execução Passo a Passo (*Step Over*):** Utilize a tecla F8 (ou comando correspondente) para caminhar linha por linha pelo código.
5. **Simulação de Falha de Entrada:** Quando o console solicitar o valor, digite letras (por exemplo, `abc`). Observe o depurador saltando o fluxo para dentro do laço `while (!scanner.hasNextDouble())`, comprovando a retenção e limpeza da entrada inválida.

## 6. Exercício de Fixação Prática: Simulador de Rentabilidade Bancária

Implemente uma classe executável chamada `SimuladorRentabilidadeApp` respeitando os seguintes requisitos:

1. **Leitura com Scanner e Validação Defensiva:**
   - Solicite o valor do capital principal (rejeitando números menores ou iguais a zero).
   - Solicite o prazo da aplicação em meses (aceitando exclusivamente números inteiros de 1 a 60).
   - Solicite o código do perfil do investidor (1 = Perfil Conservador, 2 = Perfil Moderado, 3 = Perfil Arrojado).

2. **Cálculo da Taxa Mensal com *Switch Expression*:**
   - Caso o perfil seja 1: aplique taxa mensal de `0.007` (0.7%).
   - Caso o perfil seja 2: aplique taxa mensal de `0.011` (1.1%).
   - Caso o perfil seja 3: aplique taxa mensal de `0.016` (1.6%).
   - Para qualquer outro código: retorne taxa padrão de `0.0`.

3. **Projeção Mensal com Laço `for`:**
   - Execute um laço calculando a rentabilidade acumulada de cada mês usando a fórmula: `saldo = saldo × (1 + taxa)`.
   - Apresente a evolução do saldo em cada mês utilizando `System.out.printf`, formatando o número do mês com `%02d` e o saldo final acumulado com `%.2f`.