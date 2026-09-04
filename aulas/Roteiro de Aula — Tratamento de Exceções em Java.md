# Roteiro de Aula — Tratamento de Exceções em Java

## 1. Identificação

**Tema:** Tratamento de Exceções em Java  
**Conteúdo:** `Exception`, `try`, `catch`, múltiplos `catch`, `finally`, `throw` e `throws`  
**Duração sugerida:** 2 a 3 horas  
**Pré-requisitos:** variáveis, métodos, estruturas condicionais, repetição, arrays e classes básicas em Java.

---

## 2. Objetivos da aula

Ao final da aula, o aluno deverá ser capaz de:

- compreender o conceito de exceção;
- diferenciar erros de programação de situações excepcionais;
- identificar exceções comuns em Java;
- interpretar mensagens e stack traces;
- utilizar `try` e `catch`;
- utilizar múltiplos blocos `catch`;
- compreender a finalidade do `finally`;
- entender a diferença entre `throw` e `throws`;
- tratar entradas inválidas fornecidas pelo usuário;
- construir programas Java mais robustos.

---

# 3. Problematização inicial

Começar a aula apresentando um programa aparentemente correto:

```java
import java.util.Scanner;

public class Exemplo01 {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.print("Digite o primeiro número: ");
        int n1 = scanner.nextInt();

        System.out.print("Digite o segundo número: ");
        int n2 = scanner.nextInt();

        int resultado = n1 / n2;

        System.out.println("Resultado: " + resultado);

        scanner.close();
    }
}
```

Perguntar aos alunos:

**O que pode dar errado nesse programa?**

Testar inicialmente:

```text
Digite o primeiro número: 10
Digite o segundo número: 2

Resultado: 5
```

Depois testar:

```text
Digite o primeiro número: 10
Digite o segundo número: 0
```

O programa apresentará uma exceção semelhante a:

```text
Exception in thread "main" java.lang.ArithmeticException: / by zero
```

Utilizar esse momento para introduzir o conceito de exceção.

---

# 4. O que é uma exceção?

Uma **exceção** representa uma situação anormal que ocorre durante a execução de um programa e interfere no seu fluxo normal.

Exemplos:

- divisão inteira por zero;
- acesso a uma posição inexistente de um array;
- conversão inválida de texto para número;
- tentativa de abrir um arquivo inexistente;
- acesso a uma referência `null`;
- entrada incompatível fornecida pelo usuário.

se uma exceção não for tratada, normalmente o fluxo daquele programa ou thread é interrompido.

---

# 5. Primeira solução com try/catch

Modificar o exemplo anterior:

```java
try {

    int resultado = n1 / n2;

    System.out.println("Resultado: " + resultado);

} catch (ArithmeticException e) {

    System.out.println("Não é possível realizar divisão inteira por zero.");

}
```

Apresentar a estrutura geral:

```java
try {

    // código que pode gerar uma exceção

} catch (TipoDaExcecao e) {

    // código executado se a exceção ocorrer

}
```

Explicar:

- `try` → região monitorada;
- `catch` → tratamento da exceção;
- `ArithmeticException` → tipo da exceção;
- `e` → objeto que representa a exceção ocorrida.

---

# 6. Segundo exemplo — ArrayIndexOutOfBoundsException

Apresentar:

```java
public class ExemploArray {

    public static void main(String[] args) {

        int[] numeros = {10, 20, 30, 40, 50};

        System.out.println(numeros[8]);

    }
}
```

Perguntar:

**Qual será o resultado?**

Depois tratar o problema:

```java
public class ExemploArray {

    public static void main(String[] args) {

        int[] numeros = {10, 20, 30, 40, 50};

        try {

            System.out.println(numeros[8]);

        } catch (ArrayIndexOutOfBoundsException e) {

            System.out.println("Posição inexistente no array.");

        }

        System.out.println("Programa finalizado.");
    }
}
```

o tratamento permite que o programa continue sua execução de forma controlada.

---

# 7. Terceiro exemplo — NumberFormatException

```java
public class ExemploConversao {

    public static void main(String[] args) {

        String texto = "ABC";

        try {

            int numero = Integer.parseInt(texto);

            System.out.println(numero);

        } catch (NumberFormatException e) {

            System.out.println("Não foi possível converter o texto para inteiro.");

        }
    }
}
```

Depois experimentar:

```java
String texto = "123";
```

e:

```java
String texto = "12A";
```

---

# 8. Múltiplos blocos catch

um mesmo trecho pode produzir diferentes exceções.

```java
import java.util.Scanner;

public class ExemploMultiplasExcecoes {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        try {

            System.out.print("Digite um número: ");
            int n1 = scanner.nextInt();

            System.out.print("Digite outro número: ");
            int n2 = scanner.nextInt();

            System.out.println("Resultado: " + (n1 / n2));

        } catch (ArithmeticException e) {

            System.out.println("Erro: divisão por zero.");

        } catch (java.util.InputMismatchException e) {

            System.out.println("Erro: você deve informar um número inteiro.");

        }

        scanner.close();
    }
}
```

Realizar três testes:

```text
10
2
```

```text
10
0
```

```text
10
ABC
```

---

# 9. Introdução ao finally

Apresentar a estrutura:

```java
try {

    // operação

} catch (Exception e) {

    // tratamento

} finally {

    // código de finalização

}
```

Exemplo:

```java
try {

    int resultado = 10 / 0;

    System.out.println(resultado);

} catch (ArithmeticException e) {

    System.out.println("Erro na divisão.");

} finally {

    System.out.println("Finalizando a operação.");

}
```

o `finally` é utilizado para ações de finalização que devem ocorrer independentemente de uma exceção ter sido lançada, ressalvadas situações excepcionais de término abrupto da JVM.

Relacionado posteriormente com:

- fechamento de arquivos;
- liberação de recursos;
- conexões com banco de dados.

em Java moderno, determinados recursos são preferencialmente gerenciados com **try-with-resources**.

---

# 10. Hierarquia básica das exceções

Apresentar uma visão simplificada:

```text
Throwable
│
├── Error
│
└── Exception
     │
     ├── RuntimeException
     │    ├── ArithmeticException
     │    ├── NullPointerException
     │    ├── NumberFormatException
     │    └── IndexOutOfBoundsException
     │
     └── outras exceções verificadas
```

Vamos analisar a diferença entre:

### Checked Exceptions

O compilador exige que sejam tratadas ou declaradas.

Exemplo:

```java
IOException
```

### Unchecked Exceptions

São subclasses de `RuntimeException` e o compilador não obriga seu tratamento.

Exemplos:

```java
ArithmeticException
NullPointerException
NumberFormatException
IndexOutOfBoundsException
```

---

# 11. Introdução ao throw

Também podemos lançar uma exceção intencionalmente.

Exemplo:

```java
public static void verificarIdade(int idade) {

    if (idade < 0) {
        throw new IllegalArgumentException("A idade não pode ser negativa.");
    }

    System.out.println("Idade: " + idade);
}
```

Uso:

```java
public static void main(String[] args) {

    verificarIdade(-10);

}
```

Destacar:

```java
throw new IllegalArgumentException(...);
```

`throw` significa:

**"Lance esta exceção agora."**

---

# 12. Introdução ao throws

Apresentar posteriormente:

```java
public static void lerArquivo() throws IOException {

    // código que pode gerar IOException

}
```

Explicar a diferença:

```text
throw
```

lança uma exceção.

Enquanto:

```text
throws
```

declara que determinado método pode propagar uma ou mais exceções para quem o chamou.

---

# 13. Exercício guiado

Solicitar que os alunos construam uma calculadora simples.

O programa deverá solicitar:

```text
Primeiro número:
Segundo número:
Operação:
```

Operações:

```text
+
-
*
/
```

O programa deverá tratar pelo menos:

- entrada inválida;
- divisão por zero;
- operação inexistente.

Exemplo:

```text
Primeiro número: 20
Segundo número: 0
Operação: /

Não é possível dividir por zero.
```

---

# 14. Exercício 2 — Acesso ao vetor

Criar um array:

```java
int[] notas = {8, 7, 9, 10, 6};
```

Solicitar ao usuário uma posição.

Exemplo:

```text
Digite uma posição: 2

Valor encontrado: 9
```

Se informar:

```text
Digite uma posição: 10
```

o programa deverá apresentar uma mensagem amigável em vez de encerrar devido à exceção.

---

# 15. Exercício 3 — Conversão

Solicitar ao usuário sua idade inicialmente como texto:

```java
String idade;
```

Converter utilizando:

```java
Integer.parseInt()
```

Tratar adequadamente uma entrada como:

```text
vinte
```

---

# 16. Exercício 4 — Validação com throw

Criar o método:

```java
public static void cadastrarAluno(String nome, double nota)
```

A nota deverá estar entre `0` e `10`.

Caso contrário:

```java
throw new IllegalArgumentException("Nota deve estar entre 0 e 10.");
```

Testar:

```java
cadastrarAluno("Maria", 8.5);
```

e:

```java
cadastrarAluno("João", 15);
```

---

# 17. Desafio final

Construir um programa para cadastro de alunos contendo:

```text
Nome:
Idade:
Nota:
```

Regras:

- nome não pode ser vazio;
- idade deve ser um número inteiro;
- idade não pode ser negativa;
- nota deve ser numérica;
- nota deve estar entre 0 e 10.

O programa deverá utilizar tratamento de exceções para impedir que entradas inválidas encerrem inesperadamente sua execução.

---

# 18. Fechamento da aula

Retomar as principais estruturas:

```java
try
```

Executa o código que pode produzir uma exceção.

```java
catch
```

Captura e trata determinada exceção.

```java
finally
```

Executa ações de finalização.

```java
throw
```

Lança explicitamente uma exceção.

```java
throws
```

Declara que um método pode propagar determinada exceção.

Finalizar reforçando uma ideia importante:

> **Tratamento de exceções não serve para esconder erros de programação. Ele serve para lidar de maneira controlada com situações excepcionais que podem ocorrer durante a execução do software.**