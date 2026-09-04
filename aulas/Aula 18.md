# Plano de Aula e Roteiro de Slides: Aula 18

**Disciplina:** Programação Orientada a Objetos  
**Curso:** Análise e Desenvolvimento de Sistemas (3º Período)  
**Tema:** *Method References* (`::`), Construção Fluente de Comparadores e Consolidação da Ementa  
**Carga Horária:** 100 minutos (2 horas-aula)  
**Professor:** Alexandre Neves Louzada

---

## 1. Plano de Ensino da Aula

### 1.1. Objetivos de Aprendizagem
* **Conceitual:** Compreender os *Method References* (`::`) como açúcar sintático (*syntactic sugar*) para Expressões Lambda que apenas redirecionam chamadas de métodos existentes; dominar a composição fluente de comparadores declarativos; sintetizar e integrar a arquitetura completa desenvolvida ao longo do curso (POO, Encapsulamento, Polimorfismo, Exceções, Collections Framework e Recursos Funcionais).
* **Técnico:** Identificar e aplicar os 4 tipos de *Method References* na JVM (método estático, método de instância de um objeto específico, método de instância de um tipo arbitrário e referência a construtor `Class::new`); construir cadeias de ordenação multicritério fluentes com `Comparator.comparing()`, `thenComparing()` e `reversed()`.
* **Arquitetural:** Escrever código corporativo em nível *Clean Code*, combinando imutabilidade, tratamento de exceções, estruturas de dados otimizadas e pipelines funcionais declarativos.
* **Prático:** Desenvolver o módulo integrador de gestão de catálogo e faturamento comercial (`CatalogoModernoApp`), aplicando filtragens declarativas e ordenações fluentes multicritério por Categoria (ASC), Preço (DESC) e Nome (ASC) utilizando exclusivamente *Method References*.

### 1.2. Metodologia Ativa
* **Clean Code Challenge (Desafio de Refatoração Integrador):** Apresentação de uma rotina legada de faturamento comercial com laços explícitos e múltiplos `if/else` aninhados. Os alunos refatoram o projeto em tempo real para uma pipeline expressiva de poucas linhas utilizando os recursos modernos do Java.
* **Retrospectiva Mapeada de Competências:** Mapeamento visual no quadro interconectando todos os conceitos ministrados das Aulas 01 a 18 para demonstrar a evolução da maturidade de engenharia de software da turma.

---

## 2. Roteiro Detalhado de Slides

---

### Slide 1: Abertura e o Ápice da Expressividade em Java
* **Título do Slide:** *Method References* & Clean Code: A Fronteira da Expressividade em Java
* **Tópicos Visuais:**
  * Encerramento da disciplina: a jornada do modelo estruturado à engenharia Java moderna.
  * A evolução da concisão: Classe Anônima $\rightarrow$ Lambda $\rightarrow$ *Method Reference*.
  * O operador de resolução de escopo duplo dois-pontos (`::`).
  * As 4 formas de referenciar métodos na JVM.
  * Composição declarativa fluente com `Comparator.comparing`.
* **Notas Pedagógicas do Professor:**
  * Contextualizar a aula de encerramento: chegamos ao ponto mais alto da expressividade na linguagem Java. O objetivo hoje é elevar o código a um padrão de legibilidade e elegância adotado pelos melhores times de desenvolvimento do mundo.

---

### Slide 2: O que é um *Method Reference*?
* **Título do Slide:** Referenciando Métodos Existentes como Funções
* **Tópicos Visuais:**
  * **Definição:** Uma sintaxe simplificada e ainda mais concisa para Expressões Lambda que se limitam a invocar um método já existente repassando seus argumentos diretamente.
  * **Equivalência Direta:**
    * Expressão Lambda: `(msg) -> System.out.println(msg)`
    * Method Reference: `System.out::println`
  * **Vantagem de Engenharia:** Elimina redundância sintática quando a lambda não adiciona nenhuma lógica além da própria chamada do método.
* **Notas Pedagógicas do Professor:**
  * Explicar que o *Method Reference* não executa o método no momento da declaração; ele passa a referência/ponteiro do método para ser executado no momento oportuno pela interface funcional.

---

### Slide 3: As Quatro Formas de *Method References*
* **Título do Slide:** Taxonomia das Referências de Método na JVM
* **Tópicos Visuais:**
  * Tabela das 4 Formas de Method References:

| Categoria | Sintaxe | Exemplo Lambda Equivalente | Exemplo Method Reference |
| :--- | :--- | :--- | :--- |
| **1. Método Estático** | `Classe::metodoEstatico` | `(x) -> Math.abs(x)` | `Math::abs` |
| **2. Método de Instância de Objeto Específico** | `instancia::metodoInstancia` | `(s) -> System.out.println(s)` | `System.out::println` |
| **3. Método de Instância de Tipo Arbitrário** | `Classe::metodoInstancia` | `(item) -> item.getNome()` | `ItemCatalogo::getNome` |
| **4. Construtor** | `Classe::new` | `() -> new ArrayList<>()` | `ArrayList::new` |

* **Notas Pedagógicas do Professor:**
  * Detalhar o Caso 3 (Tipo Arbitrário): em `ItemCatalogo::getNome`, o primeiro parâmetro passado para a lambda é assumido pela JVM como o objeto alvo que executará o método de instância `.getNome()`.

---

### Slide 4: Refatorando Expressões Lambda para Method References
* **Título do Slide:** Antes e Depois: Otimizando a Legibilidade
* **Tópicos Visuais:**
  * **Exemplo 1 (Impressão):**
    * *Lambda:* `equipe.forEach(f -> System.out.println(f));`
    * *Method Ref:* `equipe.forEach(System.out::println);`
  * **Exemplo 2 (Transformação):**
    * *Lambda:* `nomes.map(n -> n.toUpperCase());`
    * *Method Ref:* `nomes.map(String::toUpperCase);`
  * **Exemplo 3 (Fábrica):**
    * *Lambda:* `Supplier<List<String>> sup = () -> new ArrayList<>();`
    * *Method Ref:* `Supplier<List<String>> sup = ArrayList::new;`
* **Notas Pedagógicas do Professor:**
  * Demonstrar na IDE como as ferramentas estáticas de análise de código sugerem automaticamente a substituição de lambdas redundantes por *Method References* via atalho (`Alt + Enter`).

---

### Slide 5: A Revolução na Ordenação: `Comparator.comparing`
* **Título do Slide:** Ordenação Declarativa Fluente com Method References
* **Tópicos Visuais:**
  * **O Modelo Legado (Verboso):**
    Criar uma classe separada `ComparadorPorNome implements Comparator<Item>` com 10 linhas de código.
  * **O Modelo Moderno Fluente (Linha Única):**
    
```java
    // Ordena uma lista de itens usando a referência ao getter do atributo
    itens.sort(Comparator.comparing(ItemCatalogo::nome));