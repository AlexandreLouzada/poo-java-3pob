# Tutorial de Java — Aula 11: Estruturas de Dados Dinâmicas, Arrays vs. Coleções e a Interface List com ArrayList

| | |
|---|---|
| **Disciplina** | Programação Orientada a Objetos |
| **Curso** | Análise e Desenvolvimento de Sistemas (3º Período) |
| **Tema** | Limitações de Arrays Estáticos, Introdução ao Java Collections Framework, a Interface java.util.List e a Classe ArrayList |
| **Carga Horária** | 100 minutos (2 horas-aula) |
| **Professor Responsável** | Alexandre Neves Louzada |
| **Arquivo de Referência** | `TutorialAula11.md` |

## 1. Objetivos de Aprendizagem

- **Conceitual:** Compreender as limitações físicas e operacionais de vetores estáticos primitivos (tamanho fixo, redimensionamento manual e falta de métodos utilitários); assimilar a arquitetura fundamental do Java Collections Framework e o princípio do desacoplamento por interfaces (`List` vs. implementações concretas).
- **Técnico:** Dominar as principais operações de manipulação em `ArrayList` (`add`, `get`, `set`, `remove`, `size`, `contains`, `indexOf`, `clear` e `isEmpty`); utilizar laços tradicionais (`for`), laços simplificados (*enhanced for*) e métodos iteradores seguros; ordenar listas em memória utilizando `java.util.Collections.sort()`.
- **Arquitetural:** Aplicar o princípio de programar voltado para interfaces (*Coding to Interfaces*), declarando variáveis de referência como `List<T>` em vez do tipo concreto `ArrayList<T>`; compreender o impacto do mecanismo de crescimento dinâmico (*amortized time*) e a realocação interna de arrays.
- **Prático:** Implementar um motor de gerenciamento de tarefas e projetos empresariais, provendo busca posicional, substituição atômica, exclusão por referência e ordenação alfabética natural.

## 2. Fundamentação Teórica

### Limitações Estruturais de Arrays Primitivos

Na linguagem Java, arrays nativos (como `int[]` ou `String[]`) são estruturas de alocação estática no Heap. Apesar de seu alto desempenho no acesso direto via índices, impõem entraves severos para cenários corporativos dinâmicos:

- **Capacidade Inelástica:** O tamanho de um array é definido no instante da instanciação e não pode sofrer expansão ou retração. Adicionar um novo registro além do limite exige alocar manualmente um novo array no Heap e copiar todos os dados antigos elemento a elemento.
- **Falta de Abstração para Deleção:** Remover um item no meio do vetor não ajusta a estrutura; o desenvolvedor precisa deslocar os índices subsequentes para a esquerda (*shift left*) e atribuir `null` na última posição para evitar retenção indevida de memória pelo Garbage Collector.
- **Risco Constante de Exceções de Limite:** Qualquer tentativa de acesso a um índice que ultrapasse a capacidade alocada dispara instantaneamente `ArrayIndexOutOfBoundsException`.

### A Revolução do Java Collections Framework

Para resolver essas deficiências de forma padronizada, a plataforma fornece o Java Collections Framework (JCF): um conjunto unificado de interfaces, implementações de estruturas de dados prontas e algoritmos polimórficos de alta performance.

```plaintext
                           java.lang.Iterable<T>
                                     ▲
                                     │
                        java.util.Collection<T>
                                     ▲
             ┌───────────────────────┼───────────────────────┐
             │                       │                       │
      java.util.List<T>       java.util.Set<T>        java.util.Queue<T>
             ▲
             │ implements
    ┌────────┴────────┐
    │                 │
ArrayList<T>     LinkedList<T>
```

A interface `java.util.List<T>` representa uma sequência ordenada de elementos (também chamada de coleção indexada). Suas propriedades basilares são:

- **Preservação da Ordem de Inserção:** Os elementos são indexados iniciando rigorosamente na posição 0.
- **Permissão de Duplicatas:** É permitido armazenar objetos repetidos em posições e índices distintos.
- **Acesso Aleatório Posicional:** Possibilidade de consultar, atualizar ou remover elementos a partir de um número de índice arbitrário.

### A Mecânica Interna da Classe ArrayList<T>

A classe `ArrayList<T>` implementa a interface `List<T>` utilizando internamente um array primitivo que se expande dinamicamente:

- **Capacidade Inicial Padrão:** Ao criar `new ArrayList<>()`, a JVM aloca uma estrutura interna com capacidade padrão para 10 elementos.
- **Fator de Crescimento:** Quando o array interno enche por completo, a classe cria automaticamente um novo vetor interno cerca de 50% maior (`nova capacidade = capacidade antiga + (capacidade antiga / 2)`) e transfere os dados via `System.arraycopy`.
- **Complexidade de Acesso:** A leitura via índice `get(i)` é instantânea (O(1)), pois utiliza a aritmética de ponteiros direta do hardware. Em contrapartida, inserções ou deleções no início ou meio da lista custam mais tempo (O(n)) devido ao deslocamento físico dos dados.

### Diretriz de Arquitetura: Coding to Interfaces

Um dos erros mais comuns de iniciantes é acoplar a declaração das variáveis à implementação concreta:

```java
// ANTIPADRÃO (Acoplamento desnecessário à implementação):
ArrayList<String> nomes = new ArrayList<>();

// BOA PRÁTICA ARQUITETURAL (Programação voltada à interface):
List<String> nomes = new ArrayList<>();
```

Ao declarar a referência como `List<String>`, o sistema desacopla a camada consumidora dos detalhes de armazenamento. Caso o time técnico precise no futuro substituir a implementação por uma lista duplamente encadeada (`LinkedList`) para priorizar inserções no início, nenhum outro método ou assinatura precisará ser refatorado.

## 3. Estudo de Caso Integrado: Sistema de Gestão de Tarefas Corporativas

O projeto abaixo implementa uma solução corporativa para gerenciamento de fluxo de tarefas, demonstrando manipulação, buscas e ordenações em listas dinâmicas:

```java
package br.edu.universidade.sistema.tarefas.dominio;

import java.util.Objects;

// 1. Entidade de Domínio representando uma Tarefa
public class Tarefa implements Comparable<Tarefa> {
    private final Long id;
    private String titulo;
    private boolean concluida;

    public Tarefa(Long id, String titulo) {
        if (id == null || titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("Identificador e título são de fornecimento obrigatório.");
        }
        this.id = id;
        this.titulo = titulo.trim();
        this.concluida = false;
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("O título não pode ser redefinido para vazio.");
        }
        this.titulo = titulo.trim();
    }

    public boolean isConcluida() {
        return concluida;
    }

    public void marcarComoConcluida() {
        this.concluida = true;
    }

    // Critério de igualdade semântica baseado no ID
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Tarefa outra = (Tarefa) obj;
        return Objects.equals(this.id, outra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // Regra de ordenação natural alfabética pelo título
    @Override
    public int compareTo(Tarefa outra) {
        return this.titulo.compareToIgnoreCase(outra.titulo);
    }

    @Override
    public String toString() {
        return String.format("[%s] #%03d - %s",
                concluida ? "CONCLUÍDA" : "PENDENTE ", id, titulo);
    }
}
```

```java
package br.edu.universidade.sistema.tarefas.service;

import br.edu.universidade.sistema.tarefas.dominio.Tarefa;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// 2. Serviço de Gerenciamento desacoplado operando sobre a interface List
public class GerenciadorTarefasService {
    // Declarado como List (Interface) e instanciado como ArrayList
    private final List<Tarefa> repositorioTarefas;

    public GerenciadorTarefasService() {
        this.repositorioTarefas = new ArrayList<>();
    }

    public void adicionarTarefa(Tarefa tarefa) {
        if (tarefa == null) {
            throw new IllegalArgumentException("Não é permitido inserir registros nulos.");
        }
        if (repositorioTarefas.contains(tarefa)) {
            throw new IllegalStateException("Já existe uma tarefa cadastrada com o ID: " + tarefa.getId());
        }
        repositorioTarefas.add(tarefa);
    }

    public Tarefa buscarPorIndice(int indice) {
        if (indice < 0 || indice >= repositorioTarefas.size()) {
            throw new IndexOutOfBoundsException("Posição solicitada fora do intervalo: " + indice);
        }
        return repositorioTarefas.get(indice);
    }

    public boolean removerTarefaPorId(Long id) {
        // Remove utilizando correspondência pelo equals da classe de domínio
        return repositorioTarefas.removeIf(t -> t.getId().equals(id));
    }

    public void ordenarPorTitulo() {
        // Algoritmo nativo da API que delega para o compareTo da Tarefa
        Collections.sort(repositorioTarefas);
    }

    public List<Tarefa> listarTodas() {
        // Retorna uma cópia defensiva para proteger a lista interna contra mutações externas
        return new ArrayList<>(repositorioTarefas);
    }

    public int getTotalTarefas() {
        return repositorioTarefas.size();
    }
}
```

```java
package br.edu.universidade.sistema.tarefas;

import br.edu.universidade.sistema.tarefas.dominio.Tarefa;
import br.edu.universidade.sistema.tarefas.service.GerenciadorTarefasService;

// 3. Aplicação Executável demonstrando o ciclo de operações em List
public class TarefasApp {
    public static void main(String[] args) {
        GerenciadorTarefasService service = new GerenciadorTarefasService();

        System.out.println("--- 1. Inserção de Elementos Dinâmicos ---");
        service.adicionarTarefa(new Tarefa(103L, "Implementar autenticação JWT"));
        service.adicionarTarefa(new Tarefa(101L, "Configurar pool de conexões"));
        service.adicionarTarefa(new Tarefa(102L, "Atualizar documentação Javadoc"));

        System.out.printf("Total de tarefas ativas: %d%n", service.getTotalTarefas());
        service.listarTodas().forEach(System.out::println);

        System.out.println("\n--- 2. Acesso Direto e Atualização por Índice ---");
        Tarefa primeira = service.buscarPorIndice(0);
        System.out.println("Tarefa no índice 0: " + primeira);
        primeira.marcarComoConcluida();

        System.out.println("\n--- 3. Exclusão Dinâmica de Tarefa ---");
        boolean removida = service.removerTarefaPorId(101L);
        System.out.println("Tarefa #101 removida com sucesso? " + removida);
        System.out.printf("Total de tarefas remanescentes: %d%n", service.getTotalTarefas());

        System.out.println("\n--- 4. Ordenação Alfabética via Collections.sort ---");
        service.ordenarPorTitulo();
        service.listarTodas().forEach(System.out::println);
    }
}
```

## 4. Diagnóstico de Erros Comuns e Armadilhas

### Armadilha 1: Mutação em Coleção Durante Iteração Simplificada

**Código Problemático:**

```java
List<String> logs = new ArrayList<>(List.of("INFO", "ERROR", "WARN"));
for (String log : logs) {
    if (log.equals("ERROR")) {
        logs.remove(log); // MODIFICAÇÃO CONCORRENTE INDEVIDA!
    }
}
```

- **Diagnóstico da JVM:** `java.util.ConcurrentModificationException` lançada em tempo de execução.
- **Causa & Correção:** O laço simplificado (*enhanced for*) utiliza internamente um cursor `Iterator`. Se a lista for alterada diretamente pela referência `logs.remove()`, os ponteiros de controle perdem a sincronia. Para remoções durante a iteração, utilize `logs.removeIf(...)` ou acione explicitamente o método `iterator.remove()`.

### Armadilha 2: Confundir Sobrecargas do Método remove() com Inteiros

**Código Problemático:**

```java
List<Integer> numeros = new ArrayList<>();
numeros.add(10);
numeros.add(20);
numeros.add(30);

numeros.remove(10); // Qual método está sendo acionado?
```

- **Diagnóstico da JVM:** `IndexOutOfBoundsException: Index 10 out of bounds for length 3`.
- **Causa & Correção:** A interface `List` possui duas assinaturas sobrecarregadas: `remove(int index)` e `remove(Object obj)`. Quando passamos um número primitivo puro, o compilador sempre resolve para o índice posicional. Para remover pelo valor, force a conversão para objeto: `numeros.remove(Integer.valueOf(10));`.

### Armadilha 3: Criar Listas com List.of() e Tentar Adicionar Elementos

**Código Problemático:**

```java
List<String> itens = List.of("Teclado", "Mouse");
itens.add("Monitor"); // ERRO DE EXECUÇÃO!
```

- **Diagnóstico da JVM:** `java.lang.UnsupportedOperationException` lançada em tempo de execução.
- **Causa & Correção:** O método utilitário `List.of()` produz instâncias estruturalmente imutáveis. Caso a rotina necessite de inclusões e remoções dinâmicas, embrulhe o resultado em uma implementação convencional: `List<String> itens = new ArrayList<>(List.of("Teclado", "Mouse"));`.

## 5. Roteiro Prático de Depuração: Inspecionando o elementData na IDE

Para visualizar o crescimento do array interno do `ArrayList` no depurador da sua IDE (IntelliJ IDEA, Eclipse ou VS Code):

1. No método `main`, instancie uma lista com construtor vazio: `List<String> lista = new ArrayList<>();`.
2. Adicione 11 elementos sequenciais via laço `for`.
3. Posicione um ponto de interrupção (*breakpoint*) logo após a inserção do décimo e do décimo primeiro item.
4. Execute o programa em modo de depuração (*Debug*).
5. Abra a janela de variáveis (*Variables*) e expanda a instância da lista:
   - Localize o campo interno `elementData` (o vetor real de objetos no Heap).
   - Observe que até a décima inserção, o tamanho do array interno `elementData.length` é exatamente `10`.
   - Avance para a linha 11 com o comando *Step Over* (F8): veja que a JVM redimensionou automaticamente `elementData.length` para `15`, comprovando o mecanismo de realocação dinâmica de capacidade sem qualquer intervenção manual de ponteiros.

## 6. Exercício de Fixação Prática: Catálogo de Produtos para E-commerce

Implemente um componente de gerenciamento de catálogo mercantil que consolide o uso de listas dinâmicas:

1. **Construa a Classe `Produto`:**
   - Atributos privados: `codigo` (`String`), `descricao` (`String`), `preco` (`double`) e `quantidade` (`int`).
   - Construtor parametrizado completo rejeitando preços e quantidades negativos via `IllegalArgumentException`.
   - Implemente a interface `Comparable<Produto>` ordenando de forma crescente pelo valor de `preco`.
   - Sobrescreva os métodos `equals()` e `hashCode()` para considerar iguais instâncias que possuam o mesmo `codigo`.
   - Método descritivo `toString()` formatando o preço com `%.2f`.

2. **Construa o Serviço `CatalogoService`:**
   - Declare uma lista interna encapsulada: `private final List<Produto> produtos = new ArrayList<>();`.
   - Método `void cadastrar(Produto p)`: impede cadastros de referências nulas ou códigos duplicados com auxílio de `.contains()`.
   - Método `Produto obterPorPosicao(int indice)`: retorna o produto validando os limites da lista.
   - Método `boolean excluirPorCodigo(String codigo)`: remove o item do catálogo mantendo os índices ajustados.
   - Método `void ordenarPorPreco()`: executa a ordenação com `Collections.sort()`.

3. **Construa a Classe Executável `CatalogoApp`:**
   - Cadastre quatro produtos fora de ordem de preço no serviço.
   - Exiba a lista no console, exclua um produto intermediário e execute a ordenação.
   - Comprove no console que a listagem final reflete os produtos ordenados do menor para o maior preço sem gerar buracos na sequência de índices.