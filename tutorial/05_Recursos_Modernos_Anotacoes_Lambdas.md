# UNIDADE 5 — Recursos Modernos: Anotações, Lambdas e Method References

**Aulas de origem:** Aula 16 (Anotações), Aula 17 (Interfaces Funcionais e Lambdas), Aula 18 (Method References e Comparadores)  
**Questões da prova que esta unidade resolve:** Nenhuma diretamente, mas é **parte da ementa** e apoia a interpretação de código moderno (removeIf da Q20, por exemplo)  
**Tempo estimado de estudo:** 1h30

---

## 🎯 Objetivos

1. Entender o papel das **anotações** como metadados (`@Override`, `@Deprecated`, `@SuppressWarnings`);
2. Compreender **interfaces funcionais** (SAM) e **expressões lambda**;
3. Usar o pacote `java.util.function` (`Predicate`, `Consumer`, `Function`, `Supplier`);
4. Aplicar **method references** (`::`) e comparadores fluentes (`Comparator.comparing`).

---

## 🗺️ Mapa da Unidade

| Aula da ementa | Conteúdo | Uso na prova |
| :--- | :--- | :--- |
| 16 | Anotações e metadados | `@Override` (Q16), `removeIf` (Q20) |
| 17 | Lambdas e interfaces funcionais | `removeIf(p -> ...)` (Q20) |
| 18 | Method References e comparadores | Código exemplo |

---

## 📖 Revisão Teórica

### 1. Anotações — metadados no código (Aula 16)

Anotações (`@...`) adicionam **informações semânticas** a classes, métodos e atributos, sem alterar diretamente a lógica.

| Anotação | Função |
| :--- | :--- |
| `@Override` | Diz ao compilador que o método **sobrescreve** outro (valida assinatura!) |
| `@Deprecated` | Marca elemento como obsoleto (`forRemoval = true` para remoção futura) |
| `@SuppressWarnings("...")` | Suprime avisos de compilação justificados |

```java
// SEM @Override: um erro de digitação cria uma SOBRECARGA acidental (bug silencioso!)
public boolean equals(Cliente outro) { ... }

// COM @Override: o compilador valida a assinatura exata
@Override
public boolean equals(Object outro) { ... }
```

### 2. Interfaces funcionais e lambdas (Aula 17)

Uma **interface funcional** tem **um único método abstrato** (SAM — *Single Abstract Method*). Ex.: `Runnable`, `Comparator`, `Consumer`, `Predicate`.

**Lambda** é a forma concisa de implementar uma interface funcional:

```java
// Verborrágico (classe anônima):
Collections.sort(funcionarios, new Comparator<Funcionario>() {
    @Override
    public int compare(Funcionario f1, Funcionario f2) {
        return Double.compare(f1.getSalario(), f2.getSalario());
    }
});

// Expressivo (lambda):
funcionarios.sort((f1, f2) -> Double.compare(f1.getSalario(), f2.getSalario()));
```

**Sintaxe:** `(parâmetros) -> { corpo }` ou `(parâmetros) -> expressão`.

### 3. O pacote `java.util.function`

| Interface | Método | Uso |
| :--- | :--- | :--- |
| `Predicate<T>` | `boolean test(T)` | Filtros, condições |
| `Consumer<T>` | `void accept(T)` | Consumir (ex.: imprimir) |
| `Function<T,R>` | `R apply(T)` | Transformações |
| `Supplier<T>` | `T get()` | Fábricas / fornecimento |

**Exemplos usados nos projetos:**
```java
// Predicate + removeIf (queridinho da prova/Lista):
produtos.removeIf(produto -> produto.getId() == id);

// Consumer + forEach:
controller.listarProdutos().forEach(System.out::println);
```

### 4. Method References — `::` (Aula 18)

Açúcar sintático para lambdas que apenas chamam um método existente.

| Categoria | Sintaxe | Exemplo |
| :--- | :--- | :--- |
| Método estático | `Classe::metodo` | `Math::abs` |
| Instância de objeto específico | `objeto::metodo` | `System.out::println` |
| Instância de tipo arbitrário | `Classe::metodoInstancia` | `ItemCatalogo::getNome` |
| Construtor | `Classe::new` | `ArrayList::new` |

```java
equipe.forEach(f -> System.out.println(f)); // lambda
equipe.forEach(System.out::println);        // method reference (mais conciso)

nomes.map(n -> n.toUpperCase());            // lambda
nomes.map(String::toUpperCase);             // method reference
```

### 5. Comparadores fluentes (Aula 18)

```java
// Ordenação multicritério declarativa:
itens.sort(Comparator.comparing(ItemCatalogo::getCategoria)
                     .thenComparing(Comparator.comparingDouble(
                                        ItemCatalogo::getPreco).reversed())
                     .thenComparing(ItemCatalogo::getNome));
```

---

## 🧪 Exercícios de Fixação

**Exercício 1.** O que significa SAM?
**R:** Single Abstract Method — interface com um único método abstrato (ex.: `Consumer`, `Predicate`).

**Exercício 2.** Transforme em lambda: `Runnable r = new Runnable() { public void run() { System.out.println("oi"); } };`
**R:** `Runnable r = () -> System.out.println("oi");`

**Exercício 3.** O que faz `produtos.removeIf(p -> p.getValor() <= 0)`?
**R:** Remove da lista todos os produtos com valor menor ou igual a zero (predicado).

**Exercício 4.** Qual method reference equivale a `x -> System.out.println(x)`?
**R:** `System.out::println`

**Exercício 5.** Como ordenar nomes pela ordem natural?
**R:** `nomes.sort(Comparator.naturalOrder());` (se os elementos implementam `Comparable`)

---

## ✅ Checklist de autoavaliação

- [ ] Sei usar `@Override` e entendo o que ele valida
- [ ] Sei reconhecer e escrever expressões lambda
- [ ] Sei usar `Predicate` com `removeIf`
- [ ] Sei usar `Consumer` com `forEach`
- [ ] Sei transformar lambdas em method references (`::`)
- [ ] Sei montar comparadores fluentes com `comparing`/`thenComparing`/`reversed`