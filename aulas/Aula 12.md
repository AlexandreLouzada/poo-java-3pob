# Plano de Aula e Roteiro de Slides: Aula 12

**Disciplina:** Programação Orientada a Objetos  
**Curso:** Análise e Desenvolvimento de Sistemas (3º Período)  
**Tema:** Sequências e Listas: A Interface `List` e o Comparativo Arquitetural `ArrayList` vs. `LinkedList`  
**Carga Horária:** 100 minutos (2 horas-aula)  
**Professor:** Alexandre Neves Louzada

---

## 1. Plano de Ensino da Aula

### 1.1. Objetivos de Aprendizagem
* **Conceitual:** Compreender o contrato da interface `java.util.List<E>` como uma sequência ordenada, indexada e com duplicidade permitida; entender as diferenças de representação em memória entre vetores contíguos (`ArrayList`) e nós duplamente encadeados dispersos (`LinkedList`).
* **Técnico:** Analisar a complexidade assintótica (notação Big-O) para operações de leitura aleatória por índice (`get`/`set`), inserção/remoção nas extremidades (`addFirst`/`addLast`, `removeFirst`/`removeLast`) e modificações intermediárias; manipular listas imutáveis com `List.of` e visões com `subList`.
* **Arquitetural:** Estabelecer critérios de decisão de engenharia de software baseados no impacto do cache de hardware da CPU (localidade espacial), *overhead* de ponteiros no Heap e perfil de acesso de microsserviços corporativos.
* **Prático:** Implementar um motor de helpdesk e atendimento ao cliente combinando uma `LinkedList` para a fila dinâmica de tickets (inserção prioritária na cabeça e cauda) com um `ArrayList` para o histórico consolidado de consultas.

### 1.2. Metodologia Ativa
* **Benchmark Empírico ao Vivo:** Execução de testes de desempenho na IDE com 100.000 elementos comparando em tempo real os milissegundos gastos por `ArrayList` e `LinkedList` para inserções na primeira posição (`add(0, item)`) versus consultas aleatórias (`get(i)`).
* **Laboratório Hands-on Estruturado:** Desenvolvimento guiado de uma arquitetura de atendimento em camadas com separação clara de responsabilidades entre estruturas de dados.

---

## 2. Roteiro Detalhado de Slides

---

### Slide 1: Abertura e Visão Geral
* **Título do Slide:** Sequências Ordenadas: A Interface `List` e Trade-offs de Estruturas de Dados
* **Tópicos Visuais:**
  * O papel da interface `java.util.List<E>`: coleções indexadas e com duplicidade permitida.
  * Implementação contígua em memória: `ArrayList<E>`.
  * Implementação duplamente encadeada: `LinkedList<E>`.
  * Análise de Complexidade Assintótica ($O(1)$ vs. $O(n)$) e impacto na arquitetura de software corporativo.
* **Notas Pedagógicas do Professor:**
  * Contextualizar: na Aula 11 introduzimos o Collections Framework e Generics. Agora entramos no estudo específico de cada família de estruturas de dados.
  * Foco de ADS: a escolha da estrutura de dados em memória afeta diretamente o tempo de resposta e o consumo de CPU em sistemas de alto volume (ex.: processamento de extratos bancários, carrinhos de compras e feeds de dados).

---

### Slide 2: O Contrato da Interface `java.util.List<E>`
* **Título do Slide:** O Conceito de Lista na Plataforma Java
* **Tópicos Visuais:**
  * **Características Fundamentais:**
    * **Ordem de Inserção Preservada:** Os elementos ocupam posições sequenciais explícitas.
    * **Acesso Posicional (Baseado em Índice 0):** Possibilidade de ler, inserir e remover por índice (`get(i)`, `add(i, e)`, `remove(i)`).
    * **Permite Duplicatas e Nulos:** Aceita múltiplos elementos idênticos e referências `null`.
  * **Operações Principais:**
    * `add(E e)` / `add(int index, E element)`
    * `get(int index)` / `set(int index, E element)`
    * `remove(int index)` / `remove(Object o)`
    * `contains(Object o)` / `indexOf(Object o)`
    * `size()` / `isEmpty()` / `clear()`
* **Notas Pedagógicas do Professor:**
  * Reforçar a regra de ouro do design de software: **sempre declarar variáveis pelo tipo da interface**:
    `List<Cliente> clientes = new ArrayList<>();` (e nunca `ArrayList<Cliente> clientes = ...`).
  * Explicar que isso permite trocar a implementação concreta futura sem quebrar as classes consumidoras.

---

### Slide 3: Arquitetura Interna do `ArrayList<E>`
* **Título do Slide:** `ArrayList`: Vetores Dinâmicos e Acesso Rápido em Memória
* **Tópicos Visuais:**
  * **Mecanismo:** Envolve um array primitivo interno (`Object[] elementData`).
  * **Alocação Contígua:** Elementos são alocados em blocos contíguos de memória, garantindo alta eficiência no cache da CPU.
  * **Redimensionamento Dinâmico (*Growth Policy*):**
    * Capacidade inicial padrão: 10 elementos.
    * Quando atinge o limite, aloca novo array maior:
      $$\text{Nova Capacidade} = \text{Capacidade Antiga} + (\text{Capacidade Antiga} \gg 1) \approx 1.5\times$$
    * Executa cópia de memória em baixo nível via `System.arraycopy()`.
* **Notas Pedagógicas do Professor:**
  * Explicar o cálculo de endereço de memória: como os elementos estão em posições contíguas, o acesso `lista.get(i)` é uma operação puramente aritmética ($\text{Endereço Base} + i \times \text{Tamanho do Ponteiro}$), resultando em tempo constante $O(1)$.

---

### Slide 4: Arquitetura Interna do `LinkedList<E>`
* **Título do Slide:** `LinkedList`: Nós Duplamente Encadeados e Dispersão de Memória
* **Tópicos Visuais:**
  * **Mecanismo:** Estrutura formada por objetos internos independentes chamados **Nós** (`Node<E>`).
  * **Anatomia do Nó:**
    ```text
    ┌───────────────────────────────┐
    │  Node<E>                      │
    │  ┌──────────┬──────┬────────┐ │
    │  │ prev     │ item │ next   │ │
    │  └──────────┴──────┴────────┘ │
    └───────────────────────────────┘
    ```
  * Cada nó guarda: a referência para o elemento de dados (`item`), o ponteiro para o nó anterior (`prev`) e para o próximo nó (`next`).
  * **Implementa múltiplas interfaces:** `List<E>`, `Deque<E>` (Fila de ponta dupla) e `Queue<E>`.
* **Notas Pedagógicas do Professor:**
  * Apontar o *overhead* de memória: além do objeto em si, cada nó do `LinkedList` gasta 24 bytes extras de metadados de ponteiros no Heap.
  * Destacar a dispersão espacial: nós ficam espalhados pelo Heap, gerando mais *Cache Misses* na arquitetura do processador.

---

### Slide 5: Matriz de Complexidade Assintótica (Big-O)
* **Título do Slide:** Comparativo Algorítmico: `ArrayList` vs. `LinkedList`
* **Tópicos Visuais:**
  * Tabela Comparativa:

| Operação | `ArrayList<E>` | `LinkedList<E>` | Justificativa Técnica |
| :--- | :---: | :---: | :--- |
| **Acesso por índice (`get`/`set`)** | **$O(1)$** | $O(n)$ | `ArrayList` calcula endereço direto; `LinkedList` precisa percorrer a lista nó por nó até a posição. |
| **Inserção/Remoção no Início** | $O(n)$ | **$O(1)$** | `ArrayList` desloca todos os elementos para a direita/esquerda; `LinkedList` apenas ajusta ponteiros da cabeça (*Head*). |
| **Inserção/Remoção no Fim** | **$O(1)$ amortizado** | **$O(1)$** | `ArrayList` insere direto no final (exceto se houver resize); `LinkedList` atualiza a cauda (*Tail*). |
| **Inserção/Remoção no Meio** | $O(n)$ | $O(n)$ | `ArrayList` gasta no deslocamento de memória; `LinkedList` gasta para localizar o nó onde ocorrerá o reponteiramento. |
| **Consumo Extra de Memória** | Baixo (apenas slots vazios) | Alto (2 referências extras por nó) | Overhead estrutural de nós. |

* **Notas Pedagógicas do Professor:**
  * Desmistificar um mito comum: muitos acham que `LinkedList` é sempre melhor para inserção/remoção em qualquer posição. Mostrar que para inserir no meio de um `LinkedList`, gasta-se $O(n)$ para *encontrar* o ponto de inserção.

---

### Slide 6: Regras de Decisão de Engenharia de Software
* **Título do Slide:** Qual Implementação Escolher no Mundo Real?
* **Tópicos Visuais:**
  * **Prefira `ArrayList` quando (95% dos casos corporativos):**
    * A leitura e consulta indexada for frequente.
    * A maioria das inserções ocorrer ao final da lista.
    * Houver restrição de memória ou exigência de eficiência de cache.
  * **Considere `LinkedList` quando:**
    * A aplicação opera estritamente como **Fila (FIFO)** ou **Pilha/Deque (LIFO)** com inserções e remoções contínuas nas extremidades (início/fim).
    * Grandes volumes de remoção/inserção no início da sequência sem necessidade de consultas aleatórias por índice.
* **Notas Pedagógicas do Professor:**
  * Ressaltar que até o próprio criador do Java Collections Framework (Joshua Bloch) admitiu em conferências que `ArrayList` é a escolha padrão recomendada para quase todas as situações cotidianas devido à arquitetura de cache dos processadores modernos.

---

### Slide 7: Métodos Utilitários e Recursos Modernos da Interface `List`
* **Título do Slide:** Operações Avançadas e Listas Imutáveis
* **Tópicos Visuais:**
  * **Fábricas de Listas Imutáveis (Java 9+):**
    ```java
    List<String> perfilPadrao = List.of("LEITURA", "CONSULTA"); // Não aceita add/remove/set