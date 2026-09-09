# Explicação da Aula 11 — Introdução ao Java Collections Framework e Parametrização com Generics

| | |
|---|---|
| **Disciplina** | Programação Orientada a Objetos |
| **Curso** | Análise e Desenvolvimento de Sistemas (3o Período) |
| **Tema** | Introdução ao Java Collections Framework e Parametrização com Generics (`<T>`) |
| **Carga Horária** | 100 minutos (2 horas-aula) |
| **Professor** | Alexandre Neves Louzada |
| **Plano de Aula** | `Aulas 3pob/Aula 11.md` |
| **Tutorial** | `aulas/TutorialAula11.md` |
| **Estudo de Caso** | `exemplos/aula-11/` |
| **Exercícios Resolvidos** | `solucoes/aula-11/` |

---

## 1. Objetivos de Aprendizagem

**Conceitual:** Compreender as limitações estruturais e de escalabilidade de vetores primitivos (`Tipo[]`) em memória; entender a arquitetura, interfaces e hierarquia do Java Collections Framework (`java.util.*`); reconhecer os riscos de *Raw Types* e coerções manuais legadas.

**Técnico:** Dominar a parametrização de tipos com Generics (`<T>`, `<E>`, `<K, V>`), compreendendo a inferência pelo operador diamante (`<>`); implementar classes e métodos genéricos customizados; entender o mecanismo interno de *Type Erasure* (apagamento de tipos) e o uso de classes Wrapper (`Integer`, `Double`, `Boolean`) com *Autoboxing/Unboxing*.

**Arquitetural:** Projetar componentes reutilizáveis baseados no padrão repositório em memória (*InMemoryRepository*), garantindo encapsulamento através de visões imutáveis (`Collections.unmodifiableList`).

**Prático:** Implementar uma estrutura de dados de pilha genérica (`PilhaGenerica<E>`) operando dinamicamente em memória e validando a segurança estática de tipos em tempo de compilação.

---

## 2. Conteúdo Teórico Detalhado

### 2.1. Abertura: O Módulo 4 e o Cenário Corporativo

Esta aula marca o início do Módulo 4 do curso, dedicado ao **Collections Framework e Generics**. Após o estudo de resiliência e exceções (Módulo 3), o foco agora desloca-se para as **estruturas de dados dinâmicas em memória**. No ambiente corporativo, o uso de arrays estáticos (`Tipo[]`) é raro; quase sempre manipulamos listas dinâmicas, conjuntos e mapas com validação rigorosa de tipos pelo compilador. A aula também serve de contexto histórico: encerramos um módulo de tratamento de erros e abrimos um módulo de estruturas de dados que serão a base para todos os sistemas de informação que construiremos.

### 2.2. As Limitações dos Arrays Tradicionais

Os arrays em Java possuem características que os tornam insuficientes para muitos cenários corporativos:

- **Tamanho Fixo:** Um array declarado como `new Cliente[100]` não pode ser redimensionado. Se a demanda ultrapassar 100 posições, o único recurso é alocar um novo vetor maior e copiar todos os elementos, uma operação custosa.
- **Falta de Abstrações de Alto Nível:** Arrays não oferecem métodos nativos para busca por predicado, remoção direta de elementos, inserção ordenada ou união de conjuntos. Tudo precisa ser implementado manualmente.
- **Complexidade Manual:** O desenvolvedor deve controlar ponteiros de índice, posições vazias (`null`) e realocações, algo comum em linguagens procedurais como C e Pascal.

O Collections Framework surge como uma **biblioteca padronizada, de alta performance e amplamente testada** da JDK, eliminando a necessidade de implementar estruturas como filas, pilhas e listas encadeadas do zero para cada novo tipo de dado.

### 2.3. Visão Geral da Hierarquia do Collections Framework

A hierarquia de interfaces do Collections Framework segue a seguinte estrutura:

```
                    java.lang.Iterable<T>
                             ^
                             |
                   java.util.Collection<T>
                             ^
            +----------------+----------------+
            |                    |                    |
       java.util.List<T>    java.util.Set<T>     java.util.Queue<T>
       (Indexado/Duplicado) (Único/Sem Índice)   (Ordem de Processamento)

Nota: java.util.Map<K,V> é uma hierarquia paralela baseada em Chave-Valor.
```

Alguns pontos fundamentais:

- **Raiz `Iterable<T>`:** Toda coleção herda de `Iterable<T>`, o que permite o uso automático do laço `for-each`. Isso significa que qualquer coleção pode ser percorrida de forma padronizada.
- **`Collection<T>`:** Interface que agrupa operações comuns a todas as coleções: adicionar, remover, verificar tamanho, contém elemento, etc.
- **`List<T>`:** Sequência ordenada, indexada, que permite duplicatas e elementos nulos.
- **`Set<T>`:** Coleção que garante unicidade, sem acesso posicional por índice.
- **`Queue<T>`:** Fila de processamento, com regras de inserção e remoção nas extremidades.
- **`Map<K,V>` é uma hierarquia paralela:** Mapas não herdam de `Collection` porque manipulam pares associativos (chave-valor), não elementos individuais.

### 2.4. O Cenário Pré-Java 5: Raw Types e a Falha em Tempo de Execução

Antes do Java 5, coleções guardavam apenas objetos `Object`, sem qualquer validação de tipo em tempo de compilação. Esse modelo legado gerava falhas catastróficas em produção:

```java
// Código legado (NÃO RECOMENDADO): lista bruta guardando 'Object'
List listaBruta = new ArrayList();
listaBruta.add("Cliente A");
listaBruta.add(new ContaBancaria("1001-X", "Alice", 500.0)); // Aceita qualquer objeto!

// Cast explícito frágil sujeito a quebra:
String nome = (String) listaBruta.get(1); // Dispara ClassCastException em RUNTIME!
```

O problema é claro: o compilador não consegue detectar o erro porque a lista foi declarada como bruta. A falha só se manifesta em tempo de execução, gerando `ClassCastException` que pode derrubar um sistema em produção.

### 2.5. A Solução: Generics e Segurança de Tipos

A partir do Java 5, os Generics permitem parametrizar classes, interfaces e métodos com tipos seguros:

```java
// Tipagem forte em tempo de compilação
List<Cliente> clientes = new ArrayList<>();
clientes.add(new Cliente("1001", "Alice"));
// clientes.add("texto"); // ERRO DE COMPILAÇÃO: não aceita String!
```

**Vantagens dos Generics:**

- **Type Safety:** O compilador garante que apenas objetos do tipo especificado são inseridos na coleção.
- **Eliminação de Casts:** Não é mais necessário fazer conversões explícitas ao recuperar elementos.
- **Leitura Melhorada de Código:** A declaração `List<Cliente>` torna a intenção do programador imediatamente clara.

**Operador Diamante (`<>`):**

A partir do Java 7, é possível omitir o tipo no lado direito da atribuição:

```java
// O compilador infere o tipo do lado esquerdo
List<Cliente> clientes = new ArrayList<>();
Map<String, Integer> mapa = new HashMap<>();
```

### 2.6. Generics em Métodos e Classes

Generics podem ser aplicados em níveis diferentes:

**Método Genérico:**

```java
public static <T> List<T> filtrar(List<T> lista, Predicate<T> filtro) {
    List<T> resultado = new ArrayList<>();
    for (T elemento : lista) {
        if (filtro.test(elemento)) {
            resultado.add(elemento);
        }
    }
    return resultado;
}
```

**Classe Genérica:**

```java
public class RepositorioInMemory<T> {
    private final List<T> itens = new ArrayList<>();

    public void adicionar(T item) {
        itens.add(item);
    }

    public T buscarPorIndice(int indice) {
        return itens.get(indice);
    }

    public List<T> listarTodos() {
        return Collections.unmodifiableList(itens);
    }
}
```

### 2.7. Type Erasure e Autoboxing

**Type Erasure (Apagamento de Tipos):**

Em tempo de execução, a JVM remove todas as informações de tipo dos Generics. Isso significa que:

```java
List<String> listaA = new ArrayList<>();
List<Integer> listaB = new ArrayList<>();
// Em tempo de execução, ambas são simplesmente ArrayList<Object>
```

A consequência prática é que não é possível fazer `new T()` ou `instanceof List<String>` em tempo de execução. O Generics é uma construção do compilador para garantir segurança, mas não afeta o bytecode final.

**Autoboxing/Unboxing com Classes Wrapper:**

As classes Wrapper permitem tratar tipos primitivos como objetos:

| Tipo Primitivo | Classe Wrapper |
|---|---|
| `int` | `Integer` |
| `double` | `Double` |
| `boolean` | `Boolean` |
| `char` | `Character` |
| `long` | `Long` |

O Java converte automaticamente entre primitivos e wrappers (autoboxing/unboxing):

```java
List<Integer> numeros = new ArrayList<>();
numeros.add(42);       // Autoboxing: int -> Integer
int valor = numeros.get(0); // Unboxing: Integer -> int
```

### 2.8. InMemoryRepository: Padrão de Repositório Genérico

Um padrão arquitetural comum em sistemas corporativos é o repositório em memória:

```java
public class InMemoryRepository<T> {
    private final List<T> repositorio = new ArrayList<>();

    public void salvar(T entidade) {
        repositorio.add(entidade);
    }

    public boolean remover(T entidade) {
        return repositorio.remove(entidade);
    }

    public List<T> listar() {
        return Collections.unmodifiableList(repositorio);
    }
}
```

O uso de `Collections.unmodifiableList()` é fundamental: ele retorna uma **visão imutável** da lista, protegendo o repositório contra mutações externas indevidas. Quem possui a referência da visão não pode adicionar, remover ou alterar elementos diretamente.

---

## 3. Estudo de Caso Aplicado

O estudo de caso desta aula encontra-se em `exemplos/aula-11/`, no pacote `br.edu.universidade.sistema.tarefas`. O sistema demonstra um gerenciador de tarefas que opera sobre a interface `List<T>`, aplicando os conceitos de Generics, coleções dinâmicas e padrão repositório.

### 3.1. Entidade de Domínio: Tarefa

O arquivo `Tarefa.java` (`exemplos/aula-11/src/br/edu/universidade/sistema/tarefas/dominio/Tarefa.java`) define uma entidade com identificador único, título e status de conclusão. A entidade implementa `Comparable<Tarefa>` para definir ordenação natural alfabética pelo título:

```java
public class Tarefa implements Comparable<Tarefa> {
    private final Long id;
    private String titulo;
    private boolean concluida;

    public Tarefa(Long id, String titulo) {
        if (id == null || titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Identificador e título são de fornecimento obrigatório.");
        }
        this.id = id;
        this.titulo = titulo.trim();
        this.concluida = false;
    }

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

    @Override
    public int compareTo(Tarefa outra) {
        return this.titulo.compareToIgnoreCase(outra.titulo);
    }
}
```

Observe que o critério de igualdade é baseado no ID, e a ordenação natural é alfabética pelo título, ambas segurando as regras de contrato do `equals`/`hashCode` e do `Comparable`.

### 3.2. Serviço: GerenciadorTarefasService

O serviço (`exemplos/aula-11/src/br/edu/universidade/sistema/tarefas/service/GerenciadorTarefasService.java`) encapsula toda a lógica de manipulação da lista de tarefas, operando sobre a interface `List` e nunca sobre o tipo concreto:

```java
public class GerenciadorTarefasService {
    private final List<Tarefa> repositorioTarefas;

    public GerenciadorTarefasService() {
        this.repositorioTarefas = new ArrayList<>();
    }

    public void adicionarTarefa(Tarefa tarefa) {
        if (tarefa == null) {
            throw new IllegalArgumentException(
                "Não é permitido inserir registros nulos.");
        }
        if (repositorioTarefas.contains(tarefa)) {
            throw new IllegalStateException(
                "Já existe uma tarefa cadastrada com o ID: " + tarefa.getId());
        }
        repositorioTarefas.add(tarefa);
    }

    public Tarefa buscarPorIndice(int indice) {
        if (indice < 0 || indice >= repositorioTarefas.size()) {
            throw new IndexOutOfBoundsException(
                "Posição solicitada fora do intervalo: " + indice);
        }
        return repositorioTarefas.get(indice);
    }

    public boolean removerTarefaPorId(Long id) {
        return repositorioTarefas.removeIf(t -> t.getId().equals(id));
    }

    public void ordenarPorTitulo() {
        Collections.sort(repositorioTarefas);
    }

    public List<Tarefa> listarTodas() {
        return new ArrayList<>(repositorioTarefas);
    }
}
```

**Pontos-chave do serviço:**

- A lista é declarada como `List<Tarefa>` (interface), mas instanciada como `ArrayList<Tarefa>`. Isso permite trocar a implementação futuramente sem alterar o serviço.
- O método `listarTodas()` retorna uma **cópia defensiva** (`new ArrayList<>(repositorioTarefas)`), protegendo a lista interna contra mutações externas.
- O método `removerTarefaPorId` utiliza `removeIf` com lambda, que internamente usa o iterador de forma segura.
- A ordenação delega para o `compareTo` da classe `Tarefa` via `Collections.sort`.

### 3.3. Aplicação Executável: TarefasApp

A classe principal (`exemplos/aula-11/src/br/edu/universidade/sistema/tarefas/TarefasApp.java`) demonstra o ciclo completo de operações:

```java
public class TarefasApp {
    public static void main(String[] args) {
        GerenciadorTarefasService service = new GerenciadorTarefasService();

        // Inserção de Elementos Dinâmicos
        service.adicionarTarefa(new Tarefa(103L, "Implementar autenticação JWT"));
        service.adicionarTarefa(new Tarefa(101L, "Configurar pool de conexões"));
        service.adicionarTarefa(new Tarefa(102L, "Atualizar documentação Javadoc"));

        System.out.printf("Total de tarefas ativas: %d%n", service.getTotalTarefas());
        service.listarTodas().forEach(System.out::println);

        // Acesso Direto e Atualização por Índice
        Tarefa primeira = service.buscarPorIndice(0);
        System.out.println("Tarefa no índice 0: " + primeira);
        primeira.marcarComoConcluida();

        // Exclusão Dinâmica
        boolean removida = service.removerTarefaPorId(101L);

        // Ordenação Alfabética via Collections.sort
        service.ordenarPorTitulo();
        service.listarTodas().forEach(System.out::println);
    }
}
```

A saída esperada demonstra que a ordem de inserção é preservada (List mantém ordem), que a exclusão por ID funciona corretamente e que a ordenação reorganiza as tarefas em ordem alfabética de título.

---

## 4. Exercícios Propostos e Solução

### Exercício: Catálogo de Produtos

**Enunciado:** Implemente um sistema de catálogo de produtos que armazene entidades `Produto` em uma `List<Produto>`, com operações de cadastro, busca por posição, exclusão por código, ordenação por preço e listagem.

**Pacote da Solução:** `br.edu.universidade.sistema.catalogo` em `solucoes/aula-11/`.

**Entidade Produto:**

```java
public class Produto implements Comparable<Produto> {
    private String codigo;
    private String descricao;
    private double preco;
    private int quantidade;

    public Produto(String codigo, String descricao, double preco, int quantidade) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("Código do produto não pode ser vazio.");
        }
        if (preco < 0.0) {
            throw new IllegalArgumentException("Preço não pode ser negativo: " + preco);
        }
        if (quantidade < 0) {
            throw new IllegalArgumentException("Quantidade não pode ser negativa: " + quantidade);
        }
        this.codigo = codigo;
        this.descricao = descricao;
        this.preco = preco;
        this.quantidade = quantidade;
    }

    @Override
    public int compareTo(Produto outro) {
        return Double.compare(this.preco, outro.preco);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Produto)) return false;
        Produto outro = (Produto) o;
        return Objects.equals(this.codigo, outro.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.codigo);
    }
}
```

**Serviço CatalogoService:**

```java
public class CatalogoService {
    private final List<Produto> produtos = new ArrayList<>();

    public void cadastrar(Produto p) {
        if (p == null) {
            throw new IllegalArgumentException("Produto nulo não pode ser cadastrado.");
        }
        if (produtos.contains(p)) {
            throw new IllegalArgumentException("Código duplicado: " + p.getCodigo());
        }
        produtos.add(p);
    }

    public boolean excluirPorCodigo(String codigo) {
        for (Produto p : produtos) {
            if (p.getCodigo().equals(codigo)) {
                produtos.remove(p);
                return true;
            }
        }
        return false;
    }

    public void ordenarPorPreco() {
        Collections.sort(produtos);
    }

    public List<Produto> listarTodos() {
        return new ArrayList<>(produtos);
    }
}
```

**Aplicação CatalogoApp:**

```java
public class CatalogoApp {
    public static void main(String[] args) {
        CatalogoService service = new CatalogoService();

        service.cadastrar(new Produto("SKU-003", "Monitor 27 Pol", 1400.00, 8));
        service.cadastrar(new Produto("SKU-001", "Teclado Mecânico", 290.90, 20));
        service.cadastrar(new Produto("SKU-004", "Cabo HDMI 2.1", 45.00, 100));
        service.cadastrar(new Produto("SKU-002", "Mouse Sem Fio", 120.50, 15));

        System.out.println("--- Catálogo Após Cadastro (Ordem de Inserção) ---");
        service.listarTodos().forEach(System.out::println);

        boolean removido = service.excluirPorCodigo("SKU-002");
        System.out.println("Produto removido: " + removido);

        service.ordenarPorPreco();
        System.out.println("--- Catálogo Ordenado por Preço ---");
        service.listarTodos().forEach(System.out::println);
    }
}
```

**Observações sobre a solução:**

- O critério de comparação do `Produto` é por preço (via `Double.compare`), permitindo ordenação natural crescente.
- A exclusão por código usa um laço `for-each` que percorre a lista em busca do código correspondente.
- O método `listarTodos()` retorna sempre uma cópia defensiva, mantendo o princípio de encapsulamento.

---

## 5. Perguntas de Revisão

1. Quais são as três principais limitações dos arrays primitivos em Java que justificam o uso do Collections Framework?

2. Qual é a diferença entre a interface `List` e a interface `Set` no que diz respeito à ordem e unicidade dos elementos?

3. Explique o que é Type Erasure e quais são suas implicações práticas em tempo de execução.

4. Por que é recomendável declarar variáveis pelo tipo da interface (ex.: `List<Cliente>`) em vez do tipo concreto (ex.: `ArrayList<Cliente>`)?

5. O que acontece se tentarmos inserir um duplicado em um `HashSet` cuja classe não implementa `equals` e `hashCode`?

6. Qual a diferença entre uma **cópia defensiva** e uma **visão imutável** de uma lista? Quando usar cada uma?

7. Explique como funciona o operador diamante (`<>`) e em qual versão do Java ele foi introduzido.

8. O que é Autoboxing e quais são as classes Wrapper dos tipos primitivos `int`, `double` e `boolean`?

---

## 6. Resumo / Pontos-Chave

- **Arrays são estruturas limitadas:** tamanho fixo, sem métodos de alto nível e controle manual de índices. O Collections Framework resolve todas essas deficiências.
- **Hierarquia do Collections Framework:** `Iterable` -> `Collection` -> `List/Set/Queue`, com `Map` como hierarquia paralela.
- **Generics (`<T>`)**: garantem segurança de tipos em tempo de compilação, eliminando casts e prevenindo `ClassCastException`.
- **Raw Types são perigosas:** listas brutas guardam `Object`, aceitando qualquer tipo e causando falhas em runtime.
- **Type Erasure:** a JVM remove informações de tipo dos Generics em tempo de execução; é uma construção do compilador.
- **Autoboxing/Unboxing:** conversão automática entre tipos primitivos e suas classes Wrapper.
- **InMemoryRepository:** padrão que encapsula uma `List<T>` e expõe operações de CRUD, sempre retornando cópias ou visões imutáveis.
- **Cópia defensiva** (`new ArrayList<>(lista)`) e **visão imutável** (`Collections.unmodifiableList`) são mecanismos distintos de proteção, cada um com seu uso adequado.
- **Operador diamante** (`<>`) simplifica a declaração de coleções genéricas a partir do Java 7.
- **Comparable** define ordenação natural; para ordenações customizadas, usa-se `Comparator`.
