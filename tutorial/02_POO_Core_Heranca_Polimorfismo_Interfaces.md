# UNIDADE 2 — Herança, Abstração, Polimorfismo e Interfaces

**Aulas de origem:** Aula 06 (Sobrecarga) e Aula 07 (Herança, Polimorfismo, Abstração, Interfaces)  
**Questões da prova que esta unidade resolve:** Q12, Q13, Q14, Q15, Q16, Q17, Q18 (**35 pontos**)  
**Tempo estimado de estudo:** 2 horas

---

## 🎯 Objetivos

Ao final desta unidade, você será capaz de:

1. Explicar o mecanismo de herança (`extends`) e a relação "É-UM";
2. Utilizar `super(...)` para encadear construtores e `super.metodo()` para acessar membros da superclasse;
3. Diferenciar classes abstratas de interfaces e saber quando usar cada uma;
4. Entender o polimorfismo dinâmico (resolução de métodos em tempo de execução);
5. Diferenciar sobrecarga (`overloading`) de sobrescrita (`overriding`);
6. Compreender a herança múltipla de comportamentos via interfaces.

---

## 🗺️ Mapa da Unidade

| Questão da Prova | Habilidade testada | Conceito-chave |
| :--- | :--- | :--- |
| **Q12** | Inicializar superclasse no construtor | `super(...)` |
| **Q13** | Regras de classes abstratas | `abstract` |
| **Q14** | Visibilidade de atributos | `private` vs `protected` |
| **Q15** | Resolução dinâmica de métodos | Polimorfismo dinâmico |
| **Q16** | Sobrescrita de métodos | `@Override` / Late Binding |
| **Q17** | Interface como contrato | `implements` |
| **Q18** | Múltiplas interfaces | Herança múltipla de comportamento |

---

## 📖 Revisão Teórica

### 1. Herança — o mecanismo de reuso estrutural

A **herança** permite que uma classe (subclasse) aproveite atributos e métodos de outra classe (superclasse), criando uma hierarquia que reflete o domínio do problema.

```java
public class Superclasse {
    public void metodoComum() {
        System.out.println("Método da superclasse");
    }
}

public class Subclasse extends Superclasse {
    public void metodoEspecifico() {
        System.out.println("Método da subclasse");
    }
}
```

**Pontos-chave:**
- Java usa **herança simples**: uma classe herda de *apenas uma* superclasse;
- Toda classe herda, direta ou indiretamente, de `java.lang.Object`;
- A relação é **"É-UM" (Is-A)**: `Gerente` **é um(a)** `Funcionario`.

### 2. A palavra-chave `super`

`super` tem dois usos:

| Uso | Função | Regra |
| :--- | :--- | :--- |
| `super(argumentos)` | Invoca o construtor da superclasse | **Deve ser a 1ª instrução** do construtor filho |
| `super.metodo()` | Invoca a versão original do método na superclasse | Usado quando há sobrescrita |

```java
public class Gerente extends Funcionario {
    public Gerente(String nome, double salario) {
        super(nome, salario); // 1ª linha obrigatória!
    }
}
```

> ⚠️ Se a superclasse **não tiver um construtor sem parâmetros** (default), o compilador exige a chamada explícita `super(...)` no construtor da subclasse.

### 3. Herança de estado: `private` vs `protected`

Um dos erros mais comuns é acreditar que atributos `private` da superclasse são acessíveis nas subclasses. **Não são!**

| Modificador | Na própria classe | No mesmo pacote | Na subclasse (outro pacote) | Qualquer lugar |
| :--- | :---: | :---: | :---: | :---: |
| `private` | ✅ | ❌ | ❌ | ❌ |
| *(default)* | ✅ | ✅ | ❌ | ❌ |
| `protected` | ✅ | ✅ | ✅ | ❌ |
| `public` | ✅ | ✅ | ✅ | ✅ |

- Atributos `private` → acessíveis apenas por **getters/setters**;
- Atributos `protected` → acessíveis pelas **subclasses** (e classes do mesmo pacote).

### 4. Classes abstratas

Uma classe abstrata **não pode ser instanciada**. Ela serve como molde para hierarquias, podendo conter:

- **Métodos concretos** (com implementação) — herdados pelas subclasses;
- **Métodos abstratos** (sem implementação) — que as subclasses **concretas** devem implementar.

```java
public abstract class Funcionario {
    private String nome;
    private double salario;

    public Funcionario(String nome, double salario) {
        this.nome = nome;
        this.salario = salario;
    }

    public String getNome() { return nome; }
    public double getSalario() { return salario; }

    public abstract void mostrarDetalhes(); // Sem corpo!
}
```

```java
public class Desenvolvedor extends Funcionario {
    public Desenvolvedor(String nome, double salario) {
        super(nome, salario); // Chama construtor da classe abstrata
    }

    @Override
    public void mostrarDetalhes() {
        System.out.println("Desenvolvedor: " + getNome()
            + ", Salário: R$ " + getSalario());
    }
}
```

**Regras importantes:**
- Se uma classe tem **pelo menos um método abstrato**, ela **deve** ser declarada `abstract`;
- Uma subclasse que **também é abstrata** não é obrigada a implementar os métodos abstratos herdados (repassa a responsabilidade);
- Uma subclasse **concreta** deve implementar **todos** os métodos abstratos herdados.

### 5. Sobrescrita (`overriding`) vs Sobrecarga (`overloading`)

| Critério | Sobrecarga (Aula 06) | Sobrescrita (Aula 07) |
| :--- | :--- | :--- |
| **Onde ocorre** | Na mesma classe | Entre superclasse e subclasse |
| **Assinatura** | Lista de parâmetros **diferente** | Assinatura **rigorosamente idêntica** |
| **Tipo de retorno** | Pode variar | Idêntico (ou subtipo covariante) |
| **Momento de resolução** | Tempo de compilação (*static binding*) | Tempo de execução (*dynamic binding*) |
| **Anotação** | Não usa | `@Override` (valida pelo compilador) |

```java
// SOBRECARGA — mesma classe, assinaturas diferentes
public double calcular(double peso){ ... }
public double calcular(double a, double b){ ... }

// SOBRESCRITA — mesma assinatura, comportamento redefinido
@Override
public void mostrarDetalhes() { ... }
```

> 💡 A anotação `@Override` instrui o compilador a verificar se o método realmente existe na superclasse. Se você errar a assinatura, o compilador acusa erro — evitando bugs silenciosos (sobrecarga acidental).

### 6. Polimorfismo dinâmico (*Dynamic Method Dispatch*)

O polimorfismo permite que **uma referência da superclasse/interface** aponte para **objetos de classes distintas**, e a JVM decida **em tempo de execução** qual versão do método executar.

```java
Funcionario f1 = new Desenvolvedor("Alice", 5000.00);
Funcionario f2 = new Gerente("Bob", 8000.00);

f1.mostrarDetalhes(); // Executa o método da classe Desenvolvedor
f2.mostrarDetalhes(); // Executa o método da classe Gerente
```

**A regra de ouro:** o método executado é o da **classe real do objeto** (instância criada com `new`), **não** o da classe da variável de referência. Isso é o *late binding*.

### 7. Interfaces — contratos puros de comportamento

Uma **interface** define **o que** uma classe deve fazer, sem dizer **como** fazer. É um contrato.

```java
public interface FiguraGeometrica {
    String getNomeFigura();
    double calcularArea();
    double calcularPerimetro();
}

public class Quadrado implements FiguraGeometrica {
    private double lado;
    public Quadrado(double lado) { this.lado = lado; }

    @Override
    public String getNomeFigura() { return "Quadrado"; }

    @Override
    public double calcularArea() { return lado * lado; }

    @Override
    public double calcularPerimetro() { return 4 * lado; }
}
```

**Características:**
- Interfaces não podem ser instanciadas (`new FiguraGeometrica()` é erro);
- Interfaces **não têm atributos de instância** — apenas constantes (`final static`);
- Uma classe que implementa uma interface **deve implementar todos** os seus métodos.

### 8. Classe Abstrata vs Interface

| Aspecto | Classe Abstrata | Interface |
| :--- | :--- | :--- |
| Herança | Uma classe herda de **uma** classe abstrata | Uma classe implementa **várias** interfaces |
| Métodos | Pode ter **abstratos e concretos** | A partir do Java 8 pode ter `default` |
| Atributos | Pode ter atributos de instância | Apenas constantes (`final static`) |
| Quando usar | Compartilhar **estado e código comum** em hierarquia forte | Definir **contratos comportamentais** desacoplados |

> 🎯 **Regra prática (Gang of Four):** *"Programe para interfaces/abstrações, nunca para implementações concretas."*

### 9. Herança múltipla de comportamentos

Java não permite herança múltipla de classes, mas permite **herança múltipla de comportamentos** via interfaces:

```java
public interface Gerencia {
    void organizarEquipe();
    void conduzirReunioes();
}

public interface Desenvolve {
    void codar();
    void resolverProblemas();
}

public class TechLead extends Funcionario implements Gerencia, Desenvolve {
    // deve implementar todos os métodos das 2 interfaces
}
```

---

## ✍️ Questões da Prova Resolvidas

### Questão 12 — `super(...)` em construtores (5 pts)

**ENUNCIADO:** Qual deve ser a linha X no construtor de `Gerente` para inicializar corretamente os atributos herdados da superclasse?

```java
public class Funcionario {
    protected String nome;
    protected double salario;

    public Funcionario(String nome, double salario) {
        this.nome = nome;
        this.salario = salario;
    }
}

public class Gerente extends Funcionario {
    private double bonus;

    public Gerente(String nome, double salario, double bonus) {
        // linha X
        this.bonus = bonus;
    }
}
```

A) `Funcionario(nome, salario);`  
B) `this(nome, salario);`  
C) `super(nome, salario);`  ✅  
D) `super = new Funcionario(nome, salario);`  
E) `Funcionario.super(nome, salario);`

**PASSO A PASSO:**
1. O construtor da subclasse precisa, **em primeiro lugar**, garantir a inicialização da superclasse;
2. A sintaxe correta é `super(argumentos)` — invoca o construtor da superclasse;
3. Erros comuns: `this(...)` chama outro construtor da **própria** classe; as alternativas D e E são sintaxes inexistentes em Java.

**GABARITO: letra C.** Diagnosticar: `super(nome, salario)` deve ser a primeira instrução.

---

### Questão 13 — Classes abstratas (5 pts)

**ENUNCIADO:** Qual afirmação sobre a hierarquia abaixo está **CORRETA**?

```java
public abstract class Animal {
    public abstract void emitirSom();

    public void dormir() {
        System.out.println("Dormindo...");
    }
}
```

A) `Animal` pode ser instanciada com `new Animal()`  
B) Uma subclasse **abstrata** de `Animal` não precisa implementar `emitirSom()`  ✅  
C) `dormir()` deve ser implementado em todas as subclasses  
D) `Animal` não pode ter métodos concretos  
E) A palavra `abstract` em `emitirSom()` é opcional  

**PASSO A PASSO:**
1. `new Animal()` → ❌ classes abstratas **não podem ser instanciadas**;
2. `dormir()` → ❌ métodos concretos são **herdados**, não reimplementados;
3. A classe abstrata **pode** (e deve, se tiver método abstrato) ter métodos concretos;
4. B: se a subclasse **também** é `abstract`, ela pode **repassar** a obrigação de implementar — a alternativa está correta.

**GABARITO: letra B.**

---

### Questão 14 — `private` vs `protected` (5 pts)

**ENUNCIADO:** Qual benefício **NÃO** está relacionado ao uso de herança?

A) Reutilização dos atributos `nome` e `salario`  
B) Reutilização de `getNome()` e `getSalario()`  
C) Polimorfismo: tratar todos como `Funcionario`  
D) **Acesso direto aos atributos privados da superclasse**  ✅  
E) Facilidade para adicionar novos tipos de funcionários  

**PASSO A PASSO:**
1. Herança garante: reuso de atributos/métodos (A, B), polimorfismo (C) e extensibilidade (E);
2. Atributos `private` **não são acessíveis diretamente** pelas subclasses — a alternativa D afirma o contrário;
3. Para acessar, é preciso `getters`/`setters` ou declarar `protected`.

**GABARITO: letra D** (é a única afirmação *falsa*, e a questão pergunta o que **NÃO** é benefício).

---

### Questão 15 — Polimorfismo dinâmico (5 pts)

**ENUNCIADO:** Qual o resultado da execução do `main`?

```java
abstract class Pagamento {
    abstract double calcularValor();
}

class PagamentoCartao extends Pagamento {
    @Override
    double calcularValor() { return 100.0 * 1.05; }
}

class PagamentoBoleto extends Pagamento {
    @Override
    double calcularValor() { return 100.0 * 0.95; }
}

public static void main(String[] args) {
    Pagamento p1 = new PagamentoCartao();
    Pagamento p2 = new PagamentoBoleto();
    System.out.println(p1.calcularValor() + " - " + p2.calcularValor());
}
```

A) `100.0 - 100.0`  
B) `105.0 - 95.0`  ✅  
C) `95.0 - 105.0`  
D) `105.0 - 105.0`  
E) Erro de compilação  

**PASSO A PASSO:**
1. As variáveis `p1` e `p2` são do tipo `Pagamento`, mas os **objetos** são de `PagamentoCartao` e `PagamentoBoleto`;
2. A JVM chama o método da **classe real do objeto** (late binding / polimorfismo dinâmico);
3. Cartão: `100 × 1,05 = 105,0` · Boleto: `100 × 0,95 = 95,0`.

**GABARITO: letra B.**

---

### Questão 16 — Sobrescrita (5 pts)

**ENUNCIADO:** Qual é a saída da execução?

```java
class A {
    void metodo() { System.out.print("A"); }
}

class B extends A {
    @Override
    void metodo() { System.out.print("B"); }
}

public class Teste {
    public static void main(String[] args) {
        A obj = new B();
        obj.metodo();
    }
}
```

A) `A`  
B) `B`  ✅  
C) Erro de compilação  
D) `AB`  
E) Exceção em tempo de execução  

**PASSO A PASSO:**
1. A variável `obj` é do tipo `A` (referência), mas a instância é de `B` (objeto);
2. `metodo()` foi **sobrescrito** em `B`;
3. Polimorfismo dinâmico: executamos o método da **instância real** → `B`.

**GABARITO: letra B.**

---

### Questão 17 — Interface como contrato (5 pts)

**ENUNCIADO:** Qual alternativa apresenta uma **vantagem** do uso da interface `FiguraGeometrica`?

A) `Quadrado` herda atributos de `FiguraGeometrica`  
B) **Define um contrato que todas as figuras devem seguir, permitindo polimorfismo**  ✅  
C) `FiguraGeometrica` pode ser instanciada com `new`  
D) `Quadrado` não precisa implementar todos os métodos da interface  
E) Interfaces permitem herança múltipla de implementação  

**PASSO A PASSO:**
1. A: ❌ interfaces **não têm atributos de instância**;
2. C: ❌ interfaces **não podem ser instanciadas**;
3. D: ❌ toda classe concreta que implementa uma interface **deve** implementar todos os métodos;
4. E: ❌ interfaces permitem herança múltipla de **comportamentos/contratos**, não de implementação;
5. A resposta correta é o propósito fundamental de uma interface: **contrato + polimorfismo**.

**GABARITO: letra B.**

---

### Questão 18 — Herança múltipla via interfaces (5 pts)

**ENUNCIADO:** Qual conceito de POO está sendo demonstrado por `TechLead`?

```java
interface Gerencia {
    void organizarEquipe();
    void conduzirReunioes();
}

interface Desenvolve {
    void codar();
    void resolverProblemas();
}

class TechLead extends Funcionario implements Gerencia, Desenvolve {
    // implementação dos métodos de ambas as interfaces
}
```

A) Herança simples  
B) **Herança múltipla de comportamentos via interfaces**  ✅  
C) Polimorfismo paramétrico (generics)  
D) Encapsulamento de dados  
E) Abstração com classes abstratas  

**PASSO A PASSO:**
1. `TechLead` estende uma classe (`Funcionario`) — herança simples de classe;
2. E implementa **duas** interfaces (`Gerencia` e `Desenvolve`);
3. Isso é a **herança múltipla de comportamentos** — Java permite várias interfaces, evitando os conflitos da herança múltipla de classes;
4. Não há relação com generics, encapsulamento ou classes abstratas nas alternativas.

**GABARITO: letra B.**

---

## 🧪 Exercícios de Fixação (responda antes de ver o gabarito)

**Exercício 1.** Assinale a alternativa correta:
```java
public class Animal {
    public Animal(String nome) { }
}
public class Cachorro extends Animal {
    public Cachorro() {
        // qual linha é obrigatória aqui?
    }
}
```
A) `Animal();`  
B) `super();`  
C) `super("Rex");`  ✅  
D) `this();`  
E) Nenhuma — o compilador gera sozinho  

**Exercício 2.** O que será impresso?
```java
class X { void f() { System.out.print("X"); } }
class Y extends X { @Override void f() { System.out.print("Y"); } }
public class Teste {
    public static void main(String[] a) {
        X ref = new Y();
        ref.f();
    }
}
```
R: `Y` — polimorfismo dinâmico.

**Exercício 3.** Uma classe `abstract` pode ter apenas métodos concretos?
R: **Sim.** Uma classe abstrata pode ter zero métodos abstratos. O `abstract` impede apenas a instanciação.

**Exercício 4.** Complete: atributos `____` da superclasse não são acessíveis diretamente na subclasse; para isso, usa-se `____` ou getters.
R: `private` · `protected`

**Exercício 5.** Quantas classes abstratas e quantas interfaces podem ser herdadas/implementadas por uma única classe Java?
R: **1** classe (herança simples) e **ilimitadas** interfaces.

---

## ✅ Checklist de autoavaliação

- [ ] Sei explicar por que `super(...)` precisa ser a primeira linha do construtor
- [ ] Sei diferenciar `private` de `protected` em herança
- [ ] Sei que classes abstratas não são instanciadas e subclasses abstratas podem repassar a implementação
- [ ] Sei que o método executado no polimorfismo é o da *instância real* (não o da referência)
- [ ] Sei diferenciar sobrecarga (compile-time) de sobrescrita (runtime)
- [ ] Sei que interfaces definem contratos e não podem ter atributos de instância
- [ ] Sei que uma classe implementa várias interfaces (herança múltipla de comportamentos)