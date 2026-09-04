# UNIDADE 3 — Tratamento de Exceções

**Aulas de origem:** Aula 08 (Falhas, Stack Trace, hierarquia Throwable), Aula 09 (try-catch-finally, Checked vs Unchecked, try-with-resources), Aula 10 (throw, throws, exceções customizadas)  
**Questões da prova que esta unidade resolve:** Q5, Q6, Q7, Q8 (**20 pontos**)  
**Tempo estimado de estudo:** 2 horas

---

## 🎯 Objetivos

Ao final desta unidade, você será capaz de:

1. Entender a hierarquia `Throwable` (Error vs Exception vs RuntimeException);
2. Diferenciar exceções **checked** (verificadas) de **unchecked** (não verificadas);
3. Usar blocos `try`, `catch`, `finally` e o mecanismo `try-with-resources`;
4. Disparar e propagar exceções com `throw` e `throws`;
5. Criar exceções customizadas de domínio;
6. Aplicar boas práticas de tratamento de erros (Clean Code).

---

## 🗺️ Mapa da Unidade

| Questão da Prova | Habilidade testada | Conceito-chave |
| :--- | :--- | :--- |
| **Q5** | Erro de compilação por checked exceptions | `throws` / `try-catch` |
| **Q6** | Ordem de execução | `try-catch-finally` |
| **Q7** | Vantagem de exceções customizadas | Semântica de domínio |
| **Q8** | Entrada inválida com `Scanner` | `InputMismatchException` |

---

## 📖 Revisão Teórica

### 1. A hierarquia universal de falhas

Toda falha em Java é um **objeto** que herda, direta ou indiretamente, de `java.lang.Throwable`:

```
                       java.lang.Throwable
                                ▲
                 ┌──────────────┴──────────────┐
                 │                             │
          java.lang.Error             java.lang.Exception
         (Falhas críticas JVM)         (Falhas da aplicação)
                 │                             ▲
           OutOfMemoryError                   │
           StackOverflowError        ┌────────┴──────────┐
                                     │                   │
                       RuntimeException            Demais Exceptions
                      (Unchecked - lógica)        (Checked - ambiente)
```

| Categoria | Exemplos | Tratável? |
| :--- | :--- | :--- |
| `Error` | `OutOfMemoryError`, `StackOverflowError` | ❌ Não — falha estrutural da JVM |
| `Exception` (checked) | `IOException`, `SQLException`, `FileNotFoundException` | ✅ Sim — deve tratar ou declarar |
| `RuntimeException` (unchecked) | `NullPointerException`, `ArithmeticException`, `InputMismatchException` | ✅ Opcional — representa erros de lógica |

### 2. Checked vs Unchecked — a grande divisão

| Critério | Checked (verificadas) | Unchecked (não verificadas) |
| :--- | :--- | :--- |
| Herança | `Exception` (exceto `RuntimeException`) | `RuntimeException` |
| Papel do compilador | **Obriga** a tratar (`try-catch`) ou declarar (`throws`) | **Não obriga** — verificação opcional |
| Natureza | Condições externas (arquivo, rede, banco) | Erros de lógica / violação de contrato |
| Exemplos | `IOException`, `SQLException` | `NullPointerException`, `ArithmeticException` |

> 💡 **Regra mnemônica:** se o código conversa com o "mundo exterior" (arquivo, rede, banco de dados), o compilador impõe o tratamento (*checked*). Se a falha vem de lógica (divisão por zero, ponteiro nulo), é *unchecked*.

### 3. O bloco defensivo `try-catch`

```java
try {
    int idade = Integer.parseInt(entradaUsuario); // pode lançar NumberFormatException
} catch (NumberFormatException ex) {
    System.err.println("Entrada inválida! Informe apenas dígitos.");
}
```

**Regras:**
- O `try` monitora o código que pode gerar exceção;
- O `catch` captura **exceções específicas** — a mais específica vem primeiro;
- Pode haver **múltiplos** `catch` ou **multi-catch**: `catch (A | B ex)`.

### 4. O bloco `finally` — garantia de execução

O `finally` **sempre executa**, independentemente de ocorrer (ou não) uma exceção:

```java
try {
    return a / b;
} catch (ArithmeticException e) {
    return -1;
} finally {
    System.out.println("Finally executado"); // Sempre roda!
}
```

**Quando usar:** liberação de recursos (fechar arquivo, conexão). Alternativa moderna: `try-with-resources`.

### 5. `try-with-resources` — fechamento automático

```java
try (BufferedReader reader = new BufferedReader(new FileReader("dados.txt"))) {
    String linha = reader.readLine();
} catch (IOException e) {
    System.out.println("Erro: " + e.getMessage());
}
```

- O recurso (que implementa `AutoCloseable`) é fechado **automaticamente** ao final do `try`;
- Elimina a necessidade de `finally` para liberar recursos;
- `catch` continua opcional.

### 6. `throw` vs `throws`

| Palavra-chave | Significado | Uso |
| :--- | :--- | :--- |
| `throw` (verbo) | **Arremessar** um objeto de erro | `throw new IllegalArgumentException("msg");` |
| `throws` (declaração) | Declarar que o método **pode arremessar** | `public void sacar(double v) throws SaldoInsuficienteException` |

### 7. Exceções customizadas

**Boas práticas (Clean Code):**
- Use exceções **específicas e semânticas** (nomes que descrevem o problema);
- Mensagens de erro **explicativas**; 
- Prefira `RuntimeException` (unchecked) na maioria dos frameworks modernos;
- Use *exception chaining* para preservar a causa raiz (`super(msg, causaRaiz)`).

```java
public class SalarioNegativoException extends Exception {
    public SalarioNegativoException(String mensagem) {
        super(mensagem);
    }
}
```

### 8. Erros comuns na avaliação

- **`InputMismatchException`**: `Scanner.nextInt()` recebe texto → lança essa exceção. Sem mensagem personalizada, `getMessage()` retorna `null` → "Erro: null";
- **`NumberFormatException`**: `Integer.parseInt("ABC")` falha na conversão;
- **`ArithmeticException`**: divisão inteira por zero;
- **`NullPointerException`**: acesso a membro de referência nula.

---

## ✍️ Questões da Prova Resolvidas

### Questão 5 — Checked exceptions e obrigação do compilador (5 pts)

**ENUNCIADO:** O compilador acusa erro neste código. Qual é a causa?

```java
public void processarArquivo(String caminho) {
    BufferedReader reader = new BufferedReader(new FileReader(caminho));
    String linha = reader.readLine();
    System.out.println(linha);
}
```

A) **`FileReader` e `readLine()` lançam checked exceptions; é necessário `try-catch` ou `throws`**  ✅  
B) O método precisa ser `static` para acessar arquivos  
C) `BufferedReader` não pode ser usado com `FileReader`  
D) O método precisa retornar um valor do tipo `String`  
E) A classe `BufferedReader` não foi importada corretamente  

**PASSO A PASSO:**
1. `new FileReader(...)` pode lançar `FileNotFoundException` (checked);
2. `reader.readLine()` pode lançar `IOException` (checked);
3. O compilador **exige** `try-catch` ou declaração `throws` na assinatura;
4. As demais alternativas são incorretas tecnicamente (leitura funciona com void, o emparelhamento é válido, o import é suposto).

**GABARITO: letra A.** *"Mundo exterior" (arquivo) → checked → tratamento obrigatório.*

---

### Questão 6 — `try-catch-finally` (5 pts)

**ENUNCIADO:** Qual é a saída completa da execução de `dividir(10, 0)`?

```java
public static int dividir(int a, int b) {
    try {
        return a / b;
    } catch (ArithmeticException e) {
        System.out.println("Erro de divisão");
        return -1;
    } finally {
        System.out.println("Finally executado");
    }
}

public static void main(String[] args) {
    System.out.println(dividir(10, 0));
}
```

A) "Erro de divisão" seguido de `-1`  
B) **"Erro de divisão", "Finally executado", `-1`**  ✅  
C) "Finally executado", `-1`  
D) "Erro de divisão", "Finally executado"  
E) O programa propaga a exceção e não executa o `catch`  

**PASSO A PASSO:**
1. `10 / 0` lança `ArithmeticException` → o `catch` imprime "Erro de divisão";
2. O `catch` executa `return -1`, **mas** o `finally` roda **antes** do retorno efetivo → imprime "Finally executado";
3. O valor retornado `-1` é impresso pelo `println` do `main`.
4. **Ordem de exibição:** "Erro de divisão" → "Finally executado" → `-1`.

**GABARITO: letra B.** ⚠️ O `finally` sempre executa, até com `return` no `catch`.

---

### Questão 7 — Exceções customizadas (5 pts)

**ENUNCIADO:** Qual a principal vantagem de criar `SalarioNegativoException` em vez de usar `Exception` genérica?

A) Evitar o uso de `try-catch` no código  
B) **Tornar o tratamento de erro mais específico e semântico, facilitando a manutenção**  ✅  
C) Melhorar a performance em relação a exceções padrão  
D) Permitir que o método ignore a exceção sem declará-la  
E) Transformar uma checked exception em unchecked automaticamente  

**PASSO A PASSO:**
1. Nomes como `SalarioNegativoException` descrevem **o que** falhou → logs e `catch` mais claros;
2. A: ❌ continuam sendo necessários `try-catch` (ou `throws`);
3. C: ❌ exceções não têm relação com performance;
4. D: ❌ estender `Exception` (checked) **exige** declaração;
5. E: ❌ o tipo correta (Exception ou RuntimeException) é decidido na declaração.

**GABARITO: letra B** — segue o princípio de Clean Code de exceções significativas.

---

### Questão 8 — `InputMismatchException` (5 pts)

**ENUNCIADO:** Se o usuário digitar "vinte" em vez de um número, o que acontece?

```java
try {
    System.out.print("Digite sua idade: ");
    int idade = scanner.nextInt();
    System.out.println("Idade: " + idade);
} catch (InputMismatchException e) {
    System.out.println("Erro: " + e.getMessage());
}
```

A) O programa converte "vinte" para 20 automaticamente  
B) **Lançada `InputMismatchException` e exibe "Erro: null"**  ✅  
C) Lançada `NumberFormatException`  
D) Lançada `NullPointerException`  
E) O programa funciona e exibe "Idade: vinte"  

**PASSO A PASSO:**
1. `nextInt()` espera dígitos → ao receber "vinte" lança `InputMismatchException`;
2. Como a exceção foi criada **pela JVM** (sem mensagem customizada), `getMessage()` retorna `null`;
3. A: ❌ não existe conversão automática; C/D: ❌ tipos errados de exceção; E: ❌ o `catch` captura o erro.

**GABARITO: letra B.** 💡 Boa prática: usar mensagens próprias no `catch` em vez de `e.getMessage()`.

---

## 🧪 Exercícios de Fixação

**Exercício 1.** Classifique como Checked ou Unchecked: `IOException`, `NumberFormatException`, `SQLException`, `ArithmeticException`, `NullPointerException`.
**R:** Checked: `IOException`, `SQLException` · Unchecked: `NumberFormatException`, `ArithmeticException`, `NullPointerException`

**Exercício 2.** O que o seguinte código imprime?
```java
try { System.out.print("A"); throw new RuntimeException(); }
catch (RuntimeException e) { System.out.print("B"); }
finally { System.out.print("C"); }
```
**R:** `ABC`

**Exercício 3.** Qual a diferença entre `throw` e `throws`?
**R:** `throw` arremessa um objeto de exceção (dentro do método); `throws` declara na assinatura quais exceções o método pode propagar.

**Exercício 4.** Complete: recursos abertos em `try-with-resources` são fechados automaticamente porque implementam a interface `____`.
**R:** `AutoCloseable` (ou `Closeable`)

**Exercício 5.** Para converter a String `"123"` de uma linha de arquivo em número, qual risco de `Integer.parseInt` devemos tratar?
**R:** `NumberFormatException` (unchecked) — se a String não for numérica.

---

## ✅ Checklist de autoavaliação

- [ ] Sei desenhar a hierarquia `Throwable` → `Error` / `Exception` → `RuntimeException`
- [ ] Sei que checked exceptions **obrigam** `try-catch` ou `throws`
- [ ] Sei que o `finally` **sempre** executa
- [ ] Sei que `try-with-resources` fecha recursos automaticamente
- [ ] Sei usar `throw` (arremessar) e `throws` (declarar)
- [ ] Sei que `InputMismatchException.getMessage()` sem mensagem retorna `null`
- [ ] Sei criar exceções customizadas com nomes semânticos