# Plano de Aula e Roteiro de Slides: Aula 13

**Disciplina:** Programação Orientada a Objetos  
**Curso:** Análise e Desenvolvimento de Sistemas (3º Período)  
**Tema:** Conjuntos e Unicidade: A Interface `Set`, `HashSet`, `TreeSet` e o Contrato `equals`/`hashCode`  
**Carga Horária:** 100 minutos (2 horas-aula)  
**Professor:** Alexandre Neves Louzada

---

## 1. Plano de Ensino da Aula

### 1.1. Objetivos de Aprendizagem
* **Conceitual:** Compreender a abstração de conjuntos no *Java Collections Framework*; entender o contrato de unicidade da interface `java.util.Set<E>` (ausência de duplicatas e inexistência de acesso por índice posicional); dominar a relação matemática indissociável entre os métodos `equals()` e `hashCode()` da classe `java.lang.Object`.
* **Técnico:** Diferenciar a arquitetura interna do `HashSet` (tabelas de espalhamento com complexidade amortizada $O(1)$) da estrutura do `TreeSet` (Árvores Rubro-Negras auto-balanceadas com ordem natural/customizada $O(\log n)$); implementar sobrescritas rigorosas de `equals()` e `hashCode()` utilizando a classe utilitária `java.util.Objects`.
* **Arquitetural:** Prevenir falhas críticas de integridade de dados e vazamentos silenciosos em coleções de memória causados pela violação do contrato do hash; selecionar a implementação de `Set` adequada para requisitos de alta performance vs. ordenação dinâmica.
* **Prático:** Implementar um sistema corporativo de credenciamento e controle de presença em eventos (`ControleCredenciamento`), utilizando `HashSet` para garantia de unicidade por CPF em tempo constante e `TreeSet` para geração da lista de chamada em ordem alfabética.

### 1.2. Metodologia Ativa
* **Investigação Forense (Bug Hunting do Hash):** Demonstração prática de corrupção de dados: inserção de objetos duplicados e falhas silenciosas no método `contains()` causadas pela omissão do método `hashCode()`, permitindo que os alunos diagnostiquem o problema e proponham a correção.
* **Laboratório Hands-on Estruturado:** Modelagem de entidades com chave natural imutável e refatoração de coleções em tempo real.

---

## 2. Roteiro Detalhado de Slides

---

### Slide 1: Abertura e Visão Geral
* **Título do Slide:** Unicidade de Dados: A Interface `Set` e a Matemática do Hashing
* **Tópicos Visuais:**
  * O problema da duplicação de dados em memória e a integridade de registros.
  * O contrato da interface `java.util.Set<E>`: unicidade sem acesso por índice.
  * Tabelas Hash (`HashSet`) vs. Árvores Rubro-Negras (`TreeSet`).
  * O contrato indissociável de `equals()` e `hashCode()`.
* **Notas Pedagógicas do Professor:**
  * Contextualizar para o perfil de ADS: em bancos de dados relacionais usamos restrições `UNIQUE` e `PRIMARY KEY`. Em memória, o contrato equivalente é representado pela interface `Set`.
  * Apresentar o problema: o que acontece se tentarmos cadastrar o mesmo CPF duas vezes em um sistema de eventos? Como a JVM decide se dois objetos são "iguais"?

---

### Slide 2: O Contrato da Interface `java.util.Set<E>`
* **Título do Slide:** A Abstração de Conjuntos no Java
* **Tópicos Visuais:**
  * **Características Fundamentais:**
    * **Sem Elementos Duplicados:** Rejeita inserções de itens considerados equivalentes pelo critério de igualdade (`equals()`).
    * **Sem Acesso Posicional por Índice:** Métodos como `get(int index)` **não existem** no `Set`.
    * **Validação por Retorno Booleano:** O método `add(E e)` retorna `true` se o elemento foi inserido e `false` se já existia no conjunto.
  * **Operações de Teoria dos Conjuntos:**
    * `setA.addAll(setB)` $\rightarrow$ União ($A \cup B$).
    * `setA.retainAll(setB)` $\rightarrow$ Interseção ($A \cap B$).
    * `setA.removeAll(setB)` $\rightarrow$ Diferença ($A \setminus B$).
* **Notas Pedagógicas do Professor:**
  * Destacar a elegância do retorno booleano: em vez de fazer um `if (!conjunto.contains(item))` antes de inserir (o que custa duas operações), basta fazer `if (conjunto.add(item))` para tentar inserir e saber imediatamente se era novo.

---

### Slide 3: Arquitetura Interna do `HashSet<E>`
* **Título do Slide:** `HashSet`: Tabelas Hash e Busca em Tempo Constante $O(1)$
* **Tópicos Visuais:**
  * **Mecanismo:** Internamente, envolve um `HashMap` onde os elementos do conjunto atuam como chaves.
  * **Tabela de Espalhamento (*Hash Table*):**
    * Array de baldes (*buckets*).
    * Cálculo do balde: $\text{Índice} = \text{hashCode}(e) \pmod{\text{Tamanho da Tabela}}$.
  * **Ordem dos Elementos:** Totalmente indeterminada e imprevisível. A ordem de iteração **não reflete** a ordem de inserção.
  * **Complexidade Assintótica:** Inserção (`add`), remoção (`remove`) e consulta (`contains`) em **$O(1)$ amortizado**.
* **Notas Pedagógicas do Professor:**
  * Explicar o funcionamento em duas etapas:
    1. Calcula o `hashCode()` para ir direto ao balde correspondente (operação $O(1)$).
    2. Se o balde tiver mais de um item (colisão), executa o `equals()` apenas nos itens daquele balde.

---

### Slide 4: O Contrato Obrigatório: `equals()` e `hashCode()`
* **Título do Slide:** O Contrato Indissociável da Classe `Object`
* **Tópicos Visuais:**
  * **A Regra de Ouro (Especificação da JDK):**
    $$\text{Se } a.\text{equals}(b) == \text{true} \implies a.\text{hashCode}() == b.\text{hashCode}() \quad \mathbf{\text{(OBRIGATÓRIO!)}}$$
  * **A Recíproca NÃO é Verdadeira:**
    $$\text{Se } a.\text{hashCode}() == b.\text{hashCode}() \implies \text{Pode ser uma colisão! O } \text{equals}() \text{ desempata.}$$
  * **O que acontece se o contrato for quebrado?**
    * Objetos "iguais" geram hashes diferentes.
    * São enviados para baldes diferentes na Tabela Hash.
    * O `HashSet` aceita duplicatas e o método `contains()` falha em encontrar elementos existentes.
* **Notas Pedagógicas do Professor:**
  * Desenhar no quadro a representação dos baldes de memória: se a classe não sobrescrever `hashCode()`, a JVM usa o endereço de memória físico do objeto. Duas instâncias com o mesmo CPF terão endereços de memória diferentes, caindo em baldes diferentes e tornando o `equals()` inútil!

---

### Slide 5: Implementando `equals()` e `hashCode()` Corretamente
* **Título do Slide:** Implementação Profissional Baseada em Chave de Negócio
* **Tópicos Visuais:**
  
```java
  public class Participante {
      private final String cpf; // Chave de identidade natural imutável
      private String nome;

      @Override
      public boolean equals(Object obj) {
          if (this == obj) return true; // 1. Reflexividade
          if (obj == null || getClass() != obj.getClass()) return false; // 2. Checagem de tipo
          Participante outro = (Participante) obj;
          return Objects.equals(this.cpf, outro.cpf); // 3. Comparação de chave
      }

      @Override
      public int hashCode() {
          return Objects.hash(this.cpf); // Gera hash baseado no mesmo atributo do equals!
      }
  }