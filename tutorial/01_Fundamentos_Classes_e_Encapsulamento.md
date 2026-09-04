# UNIDADE 1 — Fundamentos, Modelagem de Classes e Encapsulamento

**Aulas de origem:** Aula 01 (Paradigmas e Ecossistema Java), Aula 02 (Tipos e Controle de Fluxo), Aula 03 (Javadoc), Aula 04 (Classes e Construtores), Aula 05 (Encapsulamento e `static`)  
**Questões da prova que esta unidade resolve:** Base conceitual (apoia Q12, Q14, Q19)  
**Tempo estimado de estudo:** 2 horas

---

## 🎯 Objetivos

1. Diferenciar programação procedural de orientação a objetos;
2. Compreender o ecossistema Java (JDK, JRE, JVM, bytecode);
3. Dominar tipos primitivos x referência, casting e estruturas de controle;
4. Modelar classes com atributos, métodos e construtores;
5. Aplicar encapsulamento com modificadores de acesso e getters/setters;
6. Entender membros estáticos (`static`) e o papel da documentação Javadoc.

---

## 🗺️ Mapa da Unidade

| Tópico | Aula de origem | Relevância para a prova |
| :--- | :--- | :--- |
| Paradigmas (procedural vs OO) | Aula 01 | Base conceitual |
| Ecossistema Java (JVM/JDK) | Aula 01 | Base conceitual |
| Tipos e controle de fluxo | Aula 02 | Interpretar código (Q4, Q8) |
| Javadoc | Aula 03 | Base p/ documentação |
| Classes, construtores, `this` | Aula 04 | Apoia Q12 |
| Encapsulamento, `private`/`public` | Aula 05 | Apoia Q14 |
| `static` | Aula 05 | Interpretar código |

---

## 📖 Revisão Teórica

### 1. Procedural vs Orientação a Objetos

| Foco | Procedural | OO |
| :--- | :--- | :--- |
| Pergunta central | *Como* processar? | *Quem* é responsável? |
| Organização | Funções soltas + dados expostos | Classes + objetos encapsulados |
| Segurança dos dados | Baixa | Alta (encapsulamento) |

### 2. Ecossistema Java — *Write Once, Run Anywhere*

```
.java ──javac──▶ .class (bytecode) ──JVM──▶ código nativo
```

- **JDK:** compilador (`javac`) + utilitários + runtime;
- **JRE:** bibliotecas + JVM (execução);
- **JVM:** máquina virtual que isola a aplicação do SO;
- **Stack:** frames de métodos, variáveis locais e referências;
- **Heap:** objetos criados com `new`;
- **Garbage Collector:** desaloca objetos sem referências.

```java
public class OlaMundo {
    public static void main(String[] args) {
        System.out.println("Fundamentos de POO e Plataforma Java!");
    }
}
```

### 3. Sistema de tipos

**Primitivos (Stack):** `byte`, `short`, `int`, `long`, `float`, `double`, `char`, `boolean`.  
**Referência (Heap):** objetos — ex.: `String`, classes do programador.

**Casting:**
```java
// Implícito (promoção):
int i = 5; double d = i;        // int -> double, sem perda

// Explícito (coerção):
double x = 3.85; int y = (int) x;  // y == 3 (trunca!)

// ⚠️ Divisão inteira:
int a = 5, b = 2;
double c = a / b;      // 2.0 (divisão inteira ANTES da atribuição!)
double d2 = (double) a / b; // 2.5 ✅
```

**Curto-circuito (`&&` / `||`):**
```java
if (cliente != null && cliente.temSaldo()) { ... }
// Se cliente for null, a 2ª condição NEM é avaliada (evita NPE!)
```

### 4. Controle de fluxo (resumo da Aula 02)

- `if/else`, ternário `cond ? a : b`, *switch expressions* (`case 1 -> valor`);
- `while` (teste no início), `do-while` (executa ao menos 1× — menus),
  `for` (contador conhecido);
- `break` (encerra laço), `continue` (pula iteração).

```java
do {
    System.out.print("Valor (>0): ");
    aporte = scanner.nextDouble();
} while (aporte <= 0);
```

⚠️ **Armadilha do `Scanner`:** `nextInt()`/`nextDouble()` deixam o `\n` no buffer; use `nextLine()` para consumir antes de ler texto.

### 5. Classes, objetos e construtores (Aula 04)

- **Classe** = molde/especificação · **Objeto** = instância no Heap;
- Criação: `new` + construtor;
- `this.atributo` desambigua escopo; `this(parâmetros)` encadeia construtores.

```java
public class ContaBancaria {
    String numero;
    String titular;
    double saldo;

    void depositar(double valor) {
        if (valor > 0) saldo += valor;
    }
}
```

### 6. Encapsulamento (Aula 05) — foco em Q14

- **Nenhum atributo deve ser `public`** em sistemas corporativos;
- Atributos `private` + acesso via **getters/setters** com regras de negócio.

```java
public class Funcionario {
    private double salarioBase;

    public double getSalarioBase() { return this.salarioBase; }

    public void setSalarioBase(double novoSalario) {
        if (novoSalario >= 1412.00) {        // regra de negócio
            this.salarioBase = novoSalario;
        } else {
            System.err.println("Salário inferior ao piso!");
        }
    }
}
```

**Matriz de visibilidade (decorar):**

| Modificador | Mesma classe | Mesmo pacote | Subclasse | Qualquer lugar |
| :--- | :---: | :---: | :---: | :---: |
| `private` | ✅ | ❌ | ❌ | ❌ |
| *(default)* | ✅ | ✅ | ❌ | ❌ |
| `protected` | ✅ | ✅ | ✅ | ❌ |
| `public` | ✅ | ✅ | ✅ | ✅ |

> 💡 **Ligação com Q14:** atributos `private` da superclasse **não** são acessíveis na subclasse → precisam de getters ou `protected`.

### 7. Membros estáticos (Aula 05)

- `static` = pertence à **classe** (não à instância);
- Constantes: `public static final double PI = 3.14159;`;
- Acesso: `Classe.membro` (sem `new`).

### 8. Javadoc (Aula 03)

Comentários `/** ... */` processados pela ferramenta `javadoc`:

```java
/**
 * Valida o formato de um CPF.
 *
 * @param cpf texto contendo os dígitos do CPF.
 * @return {@code true} se válido; {@code false} caso contrário.
 * @see #sanitizar(String)
 */
public static boolean isCpfValido(String cpf) { ... }
```

Tags principais: `@author`, `@version`, `@param`, `@return`, `@throws`, `@see`, `@since`.

---

## 🧪 Exercícios de Fixação

**Exercício 1.** `double r = 7 / 2;` → quanto vale `r`?
**R:** `3.0` — divisão inteira (7/2 = 3) antes da atribuição.

**Exercício 2.** Qual é a matriz: um atributo acessível em qualquer lugar?
**R:** `public`.

**Exercício 3.** Onde ficam os objetos criados com `new`? E as variáveis locais primitivas?
**R:** Objetos no **Heap**; variáveis locais primitivas na **Stack**.

**Exercício 4.** Por que usar `&&` em `if (x != null && x.validar())`?
**R:** Curto-circuito: se `x` é null, `validar()` não é chamado (evita `NullPointerException`).

**Exercício 5.** Com que ferramenta o `.java` vira `.class` (bytecode)?
**R:** `javac` (o compilador do JDK).

---

## ✅ Checklist de autoavaliação

- [ ] Diferencio procedural de orientação a objetos
- [ ] Explico JDK, JRE, JVM, bytecode, Stack, Heap e Garbage Collector
- [ ] Sei os tipos primitivos, casting e a armadilha da divisão inteira
- [ ] Sei usar `while`, `do-while`, `for`, `break`, `continue`
- [ ] Sei evitar o buffer residual do `Scanner`
- [ ] Modelo classes com construtores e `this`
- [ ] Sei a matriz de modificadores de acesso e o uso de `private` + getters/setters
- [ ] Sei o que é `static` e documento APIs com Javadoc