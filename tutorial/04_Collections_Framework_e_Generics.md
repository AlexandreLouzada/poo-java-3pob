# UNIDADE 4 — Collections Framework e Generics

**Aulas de origem:** Aula 11 (Generics + Collections), Aula 12 (List, ArrayList vs LinkedList), Aula 13 (Set), Aula 14 (Map), Aula 15 (Iterator, Comparable/Comparator)  
**Questões da prova que esta unidade resolve:** Q1, Q2, Q3, Q4 (**20 pontos**) + revisão geral  
**Tempo estimado de estudo:** 2h30

---

## 🎯 Objetivos

Ao final desta unidade, você será capaz de:

1. Declarar coleções com **Generics** (segurança de tipos em tempo de compilação);
2. Dominar os métodos essenciais de `ArrayList`: `add`, `set`, `remove`, `get`, `size`, `clear`, `contains`, `isEmpty`;
3. Entender a ordem dos elementos e o comportamento de `remove(Object)` (primeira ocorrência);
4. Aplicar as regras de acesso por **índice** (0-based) e evitar `IndexOutOfBoundsException`;
5. Diferenciar `ArrayList` de `LinkedList` e escolher a implementação correta;
6. Conhecer `Set`, `Map`, `Iterator`, `Comparable`/`Comparator` para a ementa completa.

---

## 🗺️ Mapa da Unidade

| Questão da Prova | Habilidade testada | Conceito-chave |
| :--- | :--- | :--- |
| **Q1** | Substituir elemento mantendo a posição | `set(int, E)` |
| **Q2** | Remoção por objeto | `remove(Object)` — 1ª ocorrência |
| **Q3** | Segurança de tipos | Generics / Raw Types |
| **Q4** | Acesso por índice | `size()` vs `get(i)` / `IndexOutOfBoundsException` |

---

## 📖 Revisão Teórica

### 1. Arrays tradicionais — as limitações

- **Tamanho fixo** — não crescem nem encolhem;
- **Sem métodos** de busca, remoção ou inserção;
- Controle manual de índices, posições vazias e realocações.

### 2. O Java Collections Framework

Coleções dinâmicas na memória, do pacote `java.util`, organizadas por interfaces:

```
                       java.lang.Iterable<T>
                                ▲
                     java.util.Collection<T>
                                ▲
        ┌───────────────────────┼───────────────────────┐
        │                       │                       │
   java.util.List<T>      java.util.Set<T>         java.util.Queue<T>
   (indexada, dup.)      (única, sem índice)     (ordem de processamento)

  * Map<K,V> é hierarquia paralela (pares chave-valor), NÃO estende Collection.
```

### 3. Generics — segurança de tipos

```java
// ❌ Raw type (sem generics) — aceita QUALQUER objeto, exige casting:
ArrayList lista = new ArrayList();
lista.add("Texto");
lista.add(123);          // sem reclamar!
String s = (String) lista.get(1);  // ClassCastException em runtime!

// ✅ Generic — validação em tempo de compilação:
ArrayList<String> nomes = new ArrayList<>();
nomes.add("Ana");
String n = nomes.get(0); // sem casting!
```

| Aspecto | Raw Type (`ArrayList`) | Generic (`ArrayList<String>`) |
| :--- | :--- | :--- |
| Segurança | ❌ Só em runtime | ✅ Em **compilação** |
| Casting | Obrigatório | Dispensado |
| Tipos heterogêneos | Permite | Rejeita |

> 💡 Regra de ouro: **sempre** use generics. Declare pelo tipo da **interface**: `List<String> nomes = new ArrayList<>();`

### 4. `ArrayList` — o vetor dinâmico

- Tamanho **dinâmico** (cresce ~1,5× automaticamente);
- Ordem de inserção **preservada**;
- Acesso por **índice** em $O(1)$.

Tabela dos métodos principais (foco da prova):

| Método | O que faz | Retorno |
| :--- | :--- | :--- |
| `add(E e)` | Adiciona ao final | `boolean` |
| `add(int i, E e)` | Insere na posição `i` (desloca os demais) | `void` |
| `set(int i, E e)` | **Substitui** o elemento na posição `i` | `E` (antigo) |
| `remove(Object o)` | Remove a **1ª ocorrência** de `o` | `boolean` |
| `remove(int i)` | Remove o elemento no índice `i` | `E` |
| `get(int i)` | Retorna o elemento no índice `i` | `E` |
| `size()` | Quantidade de elementos | `int` |
| `contains(Object o)` | Verifica presença | `boolean` |
| `isEmpty()` | Está vazia? | `boolean` |
| `clear()` | Remove todos | `void` |

### 5. Ordem, índices e remoção por objeto — detalhes que caem na prova

**A) Substituir vs inserir:**
```java
List<String> l = new ArrayList<>();
l.add("Notebook");  // [Notebook]
l.add("Mouse");     // [Notebook, Mouse]
l.add("Teclado");   // [Notebook, Mouse, Teclado]

l.set(1, "Mouse Gamer"); // SUBSTITUI: [Notebook, Mouse Gamer, Teclado]
l.add(1, "Headset");     // INSERE (desloca): [Notebook, Headset, Mouse Gamer, Teclado]
```

**B) `remove(Object)` remove a PRIMEIRA ocorrência:**
```java
List<String> t = new ArrayList<>();
t.add("Estudar"); t.add("Trabalhar"); t.add("Estudar"); t.add("Descansar");
t.remove("Estudar"); // remove o 1º "Estudar" (índice 0)
// Resultado: [Trabalhar, Estudar, Descansar]
```

**C) Índices vão de `0` a `size()-1`:**
```java
for (int i = 0; i <= alunos.size(); i++) {  // ❌ i <= size() ESTOURA
    System.out.println(alunos.get(i));
}
// Quando i == size(), get(i) lança IndexOutOfBoundsException!
// Correto: i < alunos.size()
```

### 6. `ArrayList` vs `LinkedList` (Big-O)

| Operação | `ArrayList` | `LinkedList` |
| :--- | :---: | :---: |
| Acesso por índice | **$O(1)$** | $O(n)$ |
| Inserção/remoção no início | $O(n)$ | **$O(1)$** |
| Inserção/remoção no fim | $O(1)$ amortizado | $O(1)$ |
| Inserção/remoção no meio | $O(n)$ | $O(n)$ |
| Memória extra | Baixa | Alta (2 ponteiros/nó) |

> 🎯 **Regra prática (Joshua Bloch):** prefira `ArrayList` em **95%** dos casos. `LinkedList` só para filas/pilhas (FIFO/LIFO) com operações nas extremidades.

### 7. Demais coleções (revisão da ementa)

| Interface | Implementações | Característica |
| :--- | :--- | :--- |
| `Set` | `HashSet`, `TreeSet` | **Única**, sem índice; `add()` retorna `false` se duplicado |
| `Map<K,V>` | `HashMap`, `TreeMap` | Pares chave→valor; chaves únicas; **não** estende `Collection` |
| `Queue`/`Deque` | `LinkedList`, `ArrayDeque` | Filas (FIFO) / pilhas (LIFO) |

**Set — unicidade (Aula 13):** depende do contrato `equals()` + `hashCode()` (valores iguais → mesmo hash obrigatoriamente).

**Map — iteração por chave (Aula 14):**
```java
for (Map.Entry<String, Produto> e : catalogo.entrySet()) {
    System.out.println(e.getKey() + " -> " + e.getValue());
}
```

**Iteração segura (Aula 15):** remova com `Iterator.remove()` — nunca `lista.remove()` dentro de um `for-each` (causa `ConcurrentModificationException`).

```java
Iterator<String> it = lista.iterator();
while (it.hasNext()) {
    if (it.next().equals("zera")) it.remove();
}
```

**Ordenação (Aula 15):** `Comparable` (ordem natural, `compareTo`) e `Comparator` (ordens customizadas).

---

## ✍️ Questões da Prova Resolvidas

### Questão 1 — `set()` para substituir (5 pts)

**ENUNCIADO:** Catálogo `[0]=Notebook [1]=Mouse [2]=Teclado`. Substituir "Mouse" por "Mouse Gamer" mantendo a posição.

A) `add(1, "Mouse Gamer")`  
B) **`set(1, "Mouse Gamer")`**  ✅  
C) `replace(1, "Mouse Gamer")`  
D) `update(1, "Mouse Gamer")`  
E) `put(1, "Mouse Gamer")`

**PASSO A PASSO:**
1. "Mouse" está no índice **1**;
2. `set(índice, valor)` **substitui** no lugar, mantendo o tamanho da lista;
3. `add(1, ...)` **inseriria** e empurraria "Teclado" para o índice 3 — errado;
4. `replace`, `update`, `put` **não existem** na API de `ArrayList`.

**GABARITO: letra B.**

---

### Questão 2 — Remoção de elementos (5 pts)

**ENUNCIADO:** Qual será a saída?
```java
ArrayList<String> tarefas = new ArrayList<>();
tarefas.add("Estudar"); tarefas.add("Trabalhar");
tarefas.add("Estudar"); tarefas.add("Descansar");
tarefas.remove("Estudar");
System.out.println(tarefas);
```

A) **`[Trabalhar, Estudar, Descansar]`**  ✅  
B) `[Trabalhar, Descansar]`  
C) `[Estudar, Trabalhar, Descansar]`  
D) `[Trabalhar, Estudar]`  
E) `[Estudar, Trabalhar, Estudar, Descansar]`

**PASSO A PASSO:**
1. Lista inicial: `[Estudar, Trabalhar, Estudar, Descansar]`;
2. `remove("Estudar")` chama a sobrecarga `remove(Object)` → remove a **primeira ocorrência** (índice 0);
3. Sobra: `[Trabalhar, Estudar, Descansar]`;
4. B seria o resultado se removesse **todas** as ocorrências (não é o comportamento).

**GABARITO: letra A.**

---

### Questão 3 — Generics e segurança de tipos (5 pts)

**ENUNCIADO:** Qual prática está sendo violada?
```java
ArrayList lista = new ArrayList();
lista.add("Texto");
lista.add(123);
lista.add(45.7);
```

A) Uso de tipos primitivos em `ArrayList`  
B) **Ausência de Generics permitindo tipos heterogêneos e exigindo casting**  ✅  
C) Uso de `ArrayList` em vez de array comum  
D) Ausência do método `clear()`  
E) Uso de `add()` sem índice

**PASSO A PASSO:**
1. `ArrayList` **sem** `<Tipo>` é um *raw type*: aceita qualquer `Object`;
2. Isso força `casting` ao recuperar e pode gerar `ClassCastException` em runtime;
3. A: ❌ tipos primitivos **não** podem ser usados; C: ❌ usar ArrayList é correto; D/E: ❌ não são violações (são métodos válidos);
4. Correção: `ArrayList<String> lista = new ArrayList<>();`.

**GABARITO: letra B.**

---

### Questão 4 — Índices e limite (5 pts)

**ENUNCIADO:** O que ocorre com o loop abaixo?
```java
ArrayList<String> alunos = new ArrayList<>();
alunos.add("Ana"); alunos.add("Bruno");
alunos.add("Carla"); alunos.add("Daniel");

for (int i = 0; i <= alunos.size(); i++) {   // condição com <= !
    System.out.println(alunos.get(i));
}
```

A) Exibe os 4 nomes corretamente  
B) Exibe 3 nomes e lança `NullPointerException`  
C) **Exibe 4 nomes e lança `IndexOutOfBoundsException`**  ✅  
D) Exibe 4 nomes e depois `"null"`  
E) O loop não executa

**PASSO A PASSO:**
1. `size()` retorna **4** → índices válidos: 0, 1, 2, 3;
2. A condição `i <= size()` tenta acessar o índice **4** na última iteração;
3. `get(4)` não existe → **`IndexOutOfBoundsException`**;
4. B/D/E: tipos de erro errados; A: a exceção impede a execução limpa.
5. Correção: `i < alunos.size()`.

**GABARITO: letra C.**

---

## 🧪 Exercícios de Fixação

**Exercício 1.** Depois de `lista.set(2, x)` com `lista = [A, B, C, D]`, qual o resultado?
**R:** `[A, B, x, D]` (tamanho inalterado)

**Exercício 2.** `["a","b","c","a"].remove("a")` → qual lista resultante?
**R:** `[b, c, a]` — remove apenas a **primeira** ocorrência.

**Exercício 3.** Por que `for (int i = 0; i <= filmes.size(); i++)` é perigoso?
**R:** Na última iteração `i == size()`, `get(i)` acessa índice inexistente → `IndexOutOfBoundsException`.

**Exercício 4.** Qual a diferença entre `add(0, e)` e `set(0, e)`?
**R:** `add(0, e)` insere e **desloca** os elementos; `set(0, e)` **substitui** o elemento na posição.

**Exercício 5.** Transforme em código seguro (generics): `ArrayList x = new ArrayList(); x.add("oi"); String s = (String) x.get(0);`
**R:** `ArrayList<String> x = new ArrayList<>(); x.add("oi"); String s = x.get(0);`

**Exercício 6.** Quando usar `LinkedList` em vez de `ArrayList`?
**R:** Operações FIFO/LIFO intensas nas extremidades, sem necessidade de acesso aleatório por índice.

---

## ✅ Checklist de autoavaliação

- [ ] Sempre declaro coleções com **generics** (nunca *raw types*)
- [ ] Sei que `set(i, e)` **substitui** e `add(i, e)` **desloca**
- [ ] Sei que `remove(Object)` remove a **1ª ocorrência**
- [ ] Sei que índices vão de `0` a `size()-1` (uso `<`, não `<=`)
- [ ] Sei escolher entre `ArrayList` e `LinkedList`
- [ ] Sei iterar e remover com `Iterator` com segurança