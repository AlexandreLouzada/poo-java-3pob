# Plano de Aula e Roteiro de Slides: Aula 15

**Disciplina:** Programação Orientada a Objetos  
**Curso:** Análise e Desenvolvimento de Sistemas (3º Período)  
**Tema:** Iteração Segura e Ordenação de Coleções: O Padrão `Iterator`, Mecanismo *Fail-Fast*, `Comparable` e `Comparator`  
**Carga Horária:** 100 minutos (2 horas-aula)  
**Professor:** Alexandre Neves Louzada

---

## 1. Plano de Ensino da Aula

### 1.1. Objetivos de Aprendizagem
* **Conceitual:** Compreender o padrão comportamental *Iterator* do GoF e sua implementação na JDK; entender a causa raiz da exceção `ConcurrentModificationException` e a mecânica do contador de modificações estruturais (`modCount`) que aciona o comportamento *Fail-Fast*; diferenciar a Ordem Natural de uma entidade (interface `java.lang.Comparable<T>`) de Ordenações Customizadas/Multicritério (interface `java.util.Comparator<T>`).
* **Técnico:** Manipular ponteiros de iteração com os métodos `hasNext()`, `next()` e `remove()` do `Iterator<E>`; efetuar expurgos e remoções de elementos em coleções durante a travessia de forma segura; implementar o método `compareTo()` respeitando as propriedades matematicamente reflexivas, anti-simétricas e transitivas; criar comparadores independentes com a sintaxe clássica e métodos estáticos fluentes (`Comparator.comparing`).
* **Arquitetural:** Projetar modelos de domínio onde a regra de comparação primária reflete a identidade natural do registro, enquanto relatórios e visões analíticas utilizam comparadores desacoplados e compostos.
* **Prático:** Implementar o módulo de relatórios e auditoria de vendas de e-commerce (`CentralVendasApp`), aplicando expurgo seguro de registros zerados/inválidos via `Iterator.remove()` e ordenações dinâmicas por ID (ordem natural), valor total decrescente e nome do cliente.

### 1.2. Metodologia Ativa
* **Análise de Causa Raiz & Live Debugging:** Provocação deliberada do erro `ConcurrentModificationException` através de modificações na lista dentro de um laço `for-each` tradicional, inspecionando no depurador da IDE o descompasso entre os ponteiros do iterador e a contagem `modCount` da coleção.
* **Laboratório Hands-on Estruturado:** Construção guiada de uma suíte de ordenação multicritério para relatórios comerciais.

---

## 2. Roteiro Detalhado de Slides

---

### Slide 1: Abertura e Visão Geral
* **Título do Slide:** Navegação e Ordenação Estruturada de Coleções
* **Tópicos Visuais:**
  * O padrão de projeto comportamental *Iterator* na JDK.
  * A armadilha do laço *enhanced-for* e o mecanismo *Fail-Fast*.
  * Ordem natural de entidades com `java.lang.Comparable<T>`.
  * Ordenações flexíveis e desacopladas com `java.util.Comparator<T>`.
* **Notas Pedagógicas do Professor:**
  * Contextualizar: nas Aulas 12, 13 e 14 estudamos Listas, Conjuntos e Mapas. Na Aula 15 aprenderemos a percorrer essas estruturas com segurança contra corrupção e a ordená-las sob múltiplos critérios de negócio.
  * Destacar para ADS: tabelas corporativas, dashboards e relatórios exigem ordenação dinamicamente configurável pelos usuários do sistema.

---

### Slide 2: O Padrão de Projeto *Iterator*
* **Título do Slide:** Iteração Abstrata e Segura com `java.util.Iterator<E>`
* **Tópicos Visuais:**
  * **Conceito:** Padrão de projeto GoF que permite percorrer todos os elementos de uma coleção sem expor sua representação interna (array, lista encadeada ou árvore).
  * **Contrato da Interface `Iterator<E>`:**
    * `hasNext()`: Retorna `true` se houver mais elementos na travessia.
    * `next()`: Avança o ponteiro e retorna o próximo elemento.
    * `remove()`: Remove da coleção subjacente o último elemento retornado por `next()`.
  * **A Interface `Iterable<T>`:** Interface raiz que exige o método `iterator()`, permitindo o suporte nativo ao laço `for-each`.
* **Notas Pedagógicas do Professor:**
  * Explicar o encapsulamento: usando `Iterator`, o código cliente pode percorrer qualquer coleção sem se importar se ela é um `ArrayList`, `LinkedList` ou `HashSet`.

---

### Slide 3: A Armadilha da Modificação Concorrente
* **Título do Slide:** O Erro Clássico: `ConcurrentModificationException`
* **Tópicos Visuais:**
  * **Antipadrão de Remoção:**
    
```java
    // FALHA GRAVE: Tentar modificar a lista diretamente dentro do for-each
    for (Venda v : listaVendas) {
        if (v.getValorTotal() <= 0) {
            listaVendas.remove(v); // DISPARA ConcurrentModificationException!
        }
    }