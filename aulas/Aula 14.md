# Plano de Aula e Roteiro de Slides: Aula 14

**Disciplina:** Programação Orientada a Objetos  
**Curso:** Análise e Desenvolvimento de Sistemas (3º Período)  
**Tema:** Mapeamentos Chave-Valor: A Interface `Map`, `HashMap`, `Hashtable` e Estratégias de Iteração  
**Carga Horária:** 100 minutos (2 horas-aula)  
**Professor:** Alexandre Neves Louzada

---

## 1. Plano de Ensino da Aula

### 1.1. Objetivos de Aprendizagem
* **Conceitual:** Compreender o paradigma de armazenamento associativo Chave $\rightarrow$ Valor ($K \rightarrow V$); entender por que a interface `java.util.Map<K, V>` não estende `Collection<T>`; analisar o comportamento de unicidade de chaves vs. duplicidade de valores; entender o mecanismo interno do `HashMap` (baldes, fator de carga, *rehashing* e conversão automática para Árvores Rubro-Negras quando ultrapassado o limiar de 8 elementos por balde); compreender a obsolescência da classe sincronizada `Hashtable`.
* **Técnico:** Dominar os métodos essenciais de manipulação de dicionários (`put`, `get`, `getOrDefault`, `putIfAbsent`, `computeIfAbsent`, `merge`, `containsKey`, `containsValue`, `remove`); manipular com alta performance as três projeções/vistas de coleção do mapa (`keySet()`, `values()` e `entrySet()`).
* **Arquitetural:** Projetar estruturas de dados em memória para buscas diretas por chave de negócio em tempo constante $O(1)$; evitar o antipadrão de iteração por `keySet()` acompanhado de `get(k)` no corpo do laço, priorizando o uso de `Map.Entry<K, V>`.
* **Prático:** Implementar um módulo de catálogo e controle de estoque de produtos (`CatalogoEstoque`), mapeando entidades por SKU em `HashMap`, gerenciando saldos acumulados e emitindo relatórios de valorização total via `entrySet()`.

### 1.2. Metodologia Ativa
* **Mapeamento de Casos Reais & Live Coding:** Resolução de problemas de agregação e contagem de frequência em lote, demonstrando a diferença de legibilidade e performance entre a escrita imperativa tradicional e os métodos atômicos funcionais de `Map` (`getOrDefault` e `computeIfAbsent`).
* **Laboratório Hands-on Estruturado:** Construção guiada do sistema de estoque com validação de chaves imutáveis e cálculo de inventário em tempo real.

---

## 2. Roteiro Detalhado de Slides

---

### Slide 1: Abertura e Visão Geral
* **Título do Slide:** Dicionários em Memória: A Interface `Map` e Mapeamentos Associativos
* **Tópicos Visuais:**
  * O conceito de estruturas de dados associativas ($K \rightarrow V$).
  * A arquitetura da interface `java.util.Map<K, V>`.
  * `HashMap`: busca de alta performance em tempo constante $O(1)$.
  * `Hashtable`: classe sincronizada legada e motivos de obsolescência.
  * Projeções de iteração: `keySet()`, `values()` e `entrySet()`.
* **Notas Pedagógicas do Professor:**
  * Contextualizar: nas Aulas 12 e 13 estudamos listas (`List`) e conjuntos (`Set`). Na Aula 14 aprendemos a associar um identificador exclusivo (chave) a um objeto complexo (valor).
  * Explicar por que `Map` não estende `Collection`: coleções operam sobre elementos individuais ($E$), enquanto mapas operam sobre pares associados de entidades ($K \rightarrow V$).

---

### Slide 2: O Contrato da Interface `java.util.Map<K, V>`
* **Título do Slide:** O Modelo Mental de Chave-Valor no Java
* **Tópicos Visuais:**
  * **Chaves Únicas ($K$):** Cada chave pode estar mapeada para no máximo um valor. Inserir uma chave existente **sobrescreve** o valor anterior.
  * **Valores Duplicados ($V$):** Múltiplas chaves diferentes podem apontar para valores idênticos.
  * **Operações Essenciais:**
    * `put(K key, V value)`: Insere ou atualiza a associação.
    * `get(Object key)`: Retorna o valor associado ou `null` se não existir.
    * `getOrDefault(Object key, V defaultValue)`: Retorna o valor ou um padrão seguro.
    * `containsKey(Object key)` / `containsValue(Object value)`: Verificações de presença.
    * `remove(Object key)`: Remove a associação pela chave.
* **Notas Pedagógicas do Professor:**
  * Alertar os alunos: chaves de mapas **devem ser imutáveis** (ex.: `String`, `Integer`, `UUID` ou records). Alterar o estado interno de um objeto usado como chave após sua inserção no mapa corrompe o cálculo do hash e torna o valor inalcançável.

---

### Slide 3: Arquitetura Interna do `HashMap<K, V>`
* **Título do Slide:** Como Funciona o `HashMap` por Baixo dos Panos
* **Tópicos Visuais:**
  * **Array de Baldes (*Buckets*):** Tabela interna armazena nós da interface `Map.Entry<K, V>`.
  * **Cálculo de Posição:**
    $$\text{Índice} = \text{hashCode}(key) \pmod{\text{Tamanho do Array}}$$
  * **Tratamento de Colisões no Java Moderno (Java 8+):**
    * Inicialmente, colisões no mesmo balde formam uma Lista Encadeada.
    * **Treeificação:** Quando um balde ultrapassa o limiar de **8 elementos**, a lista encadeada é convertida automaticamente em uma **Árvore Rubro-Negra**, reduzindo a busca no balde de $O(n)$ para $O(\log n)$.
  * **Fator de Carga (*Load Factor* 0.75):** Ocorre *rehashing* (dobro do tamanho) quando 75% da capacidade é atingida.
* **Notas Pedagógicas do Professor:**
  * Destacar a genialidade da engenharia da JDK: a conversão para árvore previne ataques do tipo *HashDoS* (onde dados maliciosos provocam colisões deliberadas para derrubar o servidor por degradação algorítmica).

---

### Slide 4: `HashMap` vs. `Hashtable` (Análise Comparativa)
* **Título do Slide:** Análise Comparativa: `HashMap` vs. `Hashtable`
* **Tópicos Visuais:**
  * Tabela Comparativa:

| Característica | `HashMap<K, V>` | `Hashtable<K, V>` |
| :--- | :---: | :---: |
| **Origem na Linguagem** | Java 1.2 (Moderno - Collections) | Java 1.0 (Legado / Obsoleto) |
| **Sincronização de Threads** | Não Sincronizado (Alta Performance) | Sincronizado (`synchronized` / Lento) |
| **Permite Chave `null`** | **Sim** (exatamente 1 chave nula) | **Não** (dispara `NullPointerException`) |
| **Permite Valor `null`** | **Sim** (múltiplos) | **Não** (dispara `NullPointerException`) |
| **Substituto Moderno Multithread** | N/A | `java.util.concurrent.ConcurrentHashMap` |

* **Notas Pedagógicas do Professor:**
  * Explicar por que a `Hashtable` é considerada obsoleta: a sincronização global de todos os seus métodos gera contensão severa de CPU em ambientes concorrentes. Em cenários multithread modernos, usa-se `ConcurrentHashMap`.

---

### Slide 5: As Três Vistas de Coleção de um `Map`
* **Título do Slide:** Como Projetar e Extrair Dados de um Mapa?
* **Tópicos Visuais:**
  * Um mapa não implementa `Iterable` diretamente, mas oferece 3 projeções:
    1. `map.keySet()` $\rightarrow$ Retorna um `Set<K>` com todas as chaves únicas.
    2. `map.values()` $\rightarrow$ Retorna uma `Collection<V>` com todos os valores (com duplicatas).
    3. `map.entrySet()` $\rightarrow$ Retorna um `Set<Map.Entry<K, V>>` com os pares completos de chave-valor.
* **Notas Pedagógicas do Professor:**
  * Enfatizar para a turma: as visões retornadas são ligadas diretamente ao mapa original. Remover um elemento de `map.keySet()` remove automaticamente a entrada do mapa subjacente!

---

### Slide 6: Padrões de Iteração e Performance
* **Título do Slide:** A Forma Correta de Iterar sobre Mapeamentos
* **Tópicos Visuais:**
  * **Antipadrão de Performance (NÃO FAZER):**
    
```java
    // LENTO: Faz duas buscas de hash por elemento (keySet + get)
    for (String chave : mapa.keySet()) {
        Produto p = mapa.get(chave); // Busca desnecessária em O(1) repetida n vezes
    }