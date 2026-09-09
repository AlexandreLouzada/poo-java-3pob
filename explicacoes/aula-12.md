# Explicação da Aula 12 — Sequências e Listas: A Interface List e o Comparativo ArrayList vs. LinkedList

| | |
|---|---|
| **Disciplina** | Programação Orientada a Objetos |
| **Curso** | Análise e Desenvolvimento de Sistemas (3o Período) |
| **Tema** | Sequências e Listas: A Interface List e o Comparativo Arquitetural ArrayList vs. LinkedList |
| **Carga Horária** | 100 minutos (2 horas-aula) |
| **Professor** | Alexandre Neves Louzada |
| **Plano de Aula** | `Aulas 3pob/Aula 12.md` |
| **Tutorial** | `aulas/TutorialAula12.md` |
| **Estudo de Caso** | `exemplos/aula-12/` |
| **Exercícios Resolvidos** | `solucoes/aula-12/` |

---

## 1. Objetivos de Aprendizagem

**Conceitual:** Compreender o contrato da interface `java.util.List<E>` como uma sequência ordenada, indexada e com duplicidade permitida; entender as diferenças de representação em memória entre vetores contíguos (`ArrayList`) e nós duplamente encadeados dispersos (`LinkedList`).

**Técnico:** Analisar a complexidade assintótica (notação Big-O) para operações de leitura aleatória por índice (`get`/`set`), inserção/remoção nas extremidades (`addFirst`/`addLast`, `removeFirst`/`removeLast`) e modificações intermediárias; manipular listas imutáveis com `List.of` e visões com `subList`.

**Arquitetural:** Estabelecer critérios de decisão de engenharia de software baseados no impacto do cache de hardware da CPU (localidade espacial), overhead de ponteiros no Heap e perfil de acesso de microsserviços corporativos.

**Prático:** Implementar um motor de helpdesk e atendimento ao cliente combinando uma `LinkedList` para a fila dinâmica de tickets (inserção prioritária na cabeça e cauda) com um `ArrayList` para o histórico consolidado de consultas.

---

## 2. Conteúdo Teórico Detalhado

### 2.1. Abertura: O Papel das Listas nos Sistemas Corporativos

Após a introdução ao Collections Framework e aos Generics na Aula 11, esta aula aprofunda o estudo na família de estruturas de dados mais utilizada no dia a dia de desenvolvimento: as **Listas**. A escolha da estrutura de dados em memória afeta diretamente o tempo de resposta e o consumo de CPU em sistemas de alto volume, como processamento de extratos bancários, carrinhos de compras e feeds de dados.

### 2.2. O Contrato da Interface java.util.List\<E\>

A interface `List<E>` é uma das interfaces mais fundamentais do Java. Ela define o comportamento de uma **sequência ordenada de elementos**, com as seguintes características fundamentais:

- **Ordem de Inserção Preservada:** Os elementos ocupam posições sequenciais explícitas. A primeira inserção fica na posição 0, a segunda na posição 1, e assim por diante.
- **Acesso Posicional (Baseado em Índice 0):** Possível ler, inserir e remover por índice. Métodos como `get(i)`, `add(i, e)` e `remove(i)` permitem acesso direto.
- **Permite Duplicatas e Nulos:** Aceita múltiplos elementos idênticos e referências `null`.

**Operações Principais da Interface List:**

| Método | Descrição |
|---|---|
| `add(E e)` | Adiciona o elemento ao final da lista |
| `add(int index, E element)` | Insere o elemento na posição especificada |
| `get(int index)` | Retorna o elemento na posição especificada |
| `set(int index, E element)` | Substitui o elemento na posição especificada |
| `remove(int index)` | Remove e retorna o elemento na posição especificada |
| `remove(Object o)` | Remove a primeira ocorrência do objeto especificado |
| `contains(Object o)` | Verifica se a lista contém o objeto especificado |
| `indexOf(Object o)` | Retorna o índice da primeira ocorrência do objeto |
| `size()` | Retorna o número de elementos na lista |
| `isEmpty()` | Verifica se a lista está vazia |
| `clear()` | Remove todos os elementos da lista |

**Regra de Ouro do Design de Software:** Sempre declarar variáveis pelo tipo da interface:

```java
List<Cliente> clientes = new ArrayList<>();  // CORRETO
// ArrayList<Cliente> clientes = ...;       // ERRADO - acopla a classe concreta
```

Essa prática permite trocar a implementação concreta futuramente (de `ArrayList` para `LinkedList` ou qualquer outra implementação) sem alterar as classes que consomem a lista.

### 2.3. Arquitetura Interna do ArrayList\<E\>

O `ArrayList` é a implementação mais utilizada da interface `List`. Internamente, ele funciona da seguinte forma:

**Mecanismo:** Envolve um array primitivo interno (`Object[] elementData`).

**Alocação Contígua:** Elementos são alocados em blocos contíguos de memória, garantindo alta eficiência no cache da CPU. Quando o processador acessa um elemento, o próximo já está carregado no cache (localidade espacial).

**Redimensionamento Dinâmico (Growth Policy):**

- Capacidade inicial padrão: 10 elementos.
- Quando atinge o limite, aloca novo array maior, com capacidade equivalente a aproximadamente 1.5x a capacidade anterior.
- Executa cópia de memória em baixo nível via `System.arraycopy()`.

**Acesso por Índice em O(1):**

Como os elementos estão em posições contíguas, o acesso `lista.get(i)` é uma operação puramente aritmética:

```
Endereço do elemento = Endereço_Base + i * Tamanho_do_Ponteiro
```

Isso resulta em tempo constante O(1), independentemente da posição acessada.

### 2.4. Arquitetura Interna do LinkedList\<E\>

O `LinkedList` implementa a lista como uma estrutura de **nós duplamente encadeados**.

**Anatomia do Nó:**

```
+-----------------------------------+
|  Node<E>                          |
|  +----------+------+------------+ |
|  | prev     | item | next       | |
|  +----------+------+------------+ |
+-----------------------------------+
```

Cada nó da lista guarda três informações:
- `item`: a referência para o elemento de dados armazenado.
- `prev`: o ponteiro para o nó anterior.
- `next`: o ponteiro para o próximo nó.

**Características Importantes:**

- **Implementa múltiplas interfaces:** `List<E>`, `Deque<E>` (Fila de ponta dupla) e `Queue<E>`.
- **Overhead de memória:** Além do objeto em si, cada nó gasta 24 bytes extras de metadados de ponteiros no Heap.
- **Dispersão espacial:** Nós ficam espalhados pelo Heap, gerando mais *Cache Misses* na arquitetura do processador, o que degrada significativamente a performance em operações sequenciais de leitura.

### 2.5. Matriz de Complexidade Assintótica (Big-O)

A tabela a seguir apresenta o comparativo algorítmico entre as duas implementações:

| Operação | ArrayList\<E\> | LinkedList\<E\> | Justificativa Técnica |
|---|:---:|:---:|---|
| **Acesso por índice (`get`/`set`)** | **O(1)** | O(n) | `ArrayList` calcula endereço direto; `LinkedList` precisa percorrer a lista nó por nó até a posição. |
| **Inserção/Remoção no Início** | O(n) | **O(1)** | `ArrayList` desloca todos os elementos para a direita/esquerda; `LinkedList` apenas ajusta ponteiros da cabeça (*Head*). |
| **Inserção/Remoção no Fim** | **O(1) amortizado** | **O(1)** | `ArrayList` insere direto no final (exceto se houver resize); `LinkedList` atualiza a cauda (*Tail*). |
| **Inserção/Remoção no Meio** | O(n) | O(n) | `ArrayList` gasta no deslocamento de memória; `LinkedList` gasta para localizar o nó onde ocorrerá o reponteiramento. |
| **Consumo Extra de Memória** | Baixo (apenas slots vazios) | Alto (2 referências extras por nó) | Overhead estrutural de nós. |

**Observação Importante:** Um mito comum é que `LinkedList` é sempre melhor para inserção/remoção em qualquer posição. Na verdade, para inserir no meio de um `LinkedList`, gasta-se O(n) apenas para **encontrar** o ponto de inserção, tornando a operação linear assim como no `ArrayList`.

### 2.6. Regras de Decisão de Engenharia de Software

**Prefira ArrayList quando (95% dos casos corporativos):**

- A leitura e consulta indexada for frequente.
- A maioria das inserções ocorrer ao final da lista.
- Houver restrição de memória ou exigência de eficiência de cache.

**Considere LinkedList quando:**

- A aplicação opera estritamente como **Fila (FIFO)** ou **Pilha/Deque (LIFO)** com inserções e remoções contínuas nas extremidades (início/fim).
- Grandes volumes de remoção/inserção no início da sequência sem necessidade de consultas aleatórias por índice.

Até o próprio criador do Java Collections Framework (Joshua Bloch) admitiu em conferências que `ArrayList` é a escolha padrão recomendada para quase todas as situações cotidianas, devido à arquitetura de cache dos processadores modernos.

### 2.7. Métodos Utilitários e Recursos Modernos da Interface List

**Fábricas de Listas Imutáveis (Java 9+):**

```java
List<String> perfilPadrao = List.of("LEITURA", "CONSULTA");
// Não aceita add/remove/set - gera UnsupportedOperationException
```

O método `List.of()` cria uma lista imutável e fixa. Qualquer tentativa de modificação lança exceção. É útil para inicializar constantes e configurações.

**List.copyOf() (Java 10+):**

```java
List<String> copia = List.copyOf(listaOriginal);
```

Cria uma cópia imutável de uma lista existente. Diferente do `List.of()`, preserva a ordem dos elementos da lista original.

**SubList com subList():**

```java
List<String> sublista = lista.subList(1, 4);
// Retorna uma visão da lista original nas posições 1, 2 e 3
// Alterações na sublista afetam a lista original!
```

O método `subList()` retorna uma **visão** da lista original, não uma cópia. Alterações na sublista são refletidas na lista de origem.

**Ordenação com Collections.sort() e List.sort():**

```java
// Usando Comparator
lista.sort(Comparator.comparing(Pessoa::getNome));

// Usando Comparable (ordem natural)
Collections.sort(lista);
```

---

## 3. Estudo de Caso Aplicado

O estudo de caso desta aula encontra-se em `exemplos/aula-12/`, no pacote `br.edu.universidade.sistema.academico`. O sistema demonstra operações de conjuntos (`Set`) com `HashSet` e `TreeSet`, aplicando operações matemáticas de união, interseção e diferença entre turmas acadêmicas.

### 3.1. Entidade de Domínio: Aluno

O arquivo `Aluno.java` (`exemplos/aula-12/src/br/edu/universidade/sistema/academico/dominio/Aluno.java`) implementa `Comparable<Aluno>` e define igualdade baseada no CPF:

```java
public class Aluno implements Comparable<Aluno> {
    private final String matricula;
    private final String cpf;
    private String nome;

    public Aluno(String matricula, String cpf, String nome) {
        if (matricula == null || cpf == null || nome == null) {
            throw new IllegalArgumentException(
                "Campos de identificação não podem ser nulos.");
        }
        this.matricula = matricula.trim();
        this.cpf = cpf.replaceAll("\\D", "");
        this.nome = nome.trim();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Aluno outro = (Aluno) obj;
        return Objects.equals(this.cpf, outro.cpf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.cpf);
    }

    @Override
    public int compareTo(Aluno outro) {
        return this.nome.compareToIgnoreCase(outro.nome);
    }
}
```

**Pontos importantes:**

- A igualdade é definida pelo CPF, que é a chave natural imutável do aluno.
- O `hashCode` é calculado exclusivamente sobre o CPF, mantendo o contrato indissociável.
- A ordenação natural é alfabética pelo nome, suportando `TreeSet`.
- O CPF é sanitizado no construtor, removendo caracteres não numéricos.

### 3.2. Serviço: GestaoTurmasService

O serviço (`exemplos/aula-12/src/br/edu/universidade/sistema/academico/service/GestaoTurmasService.java`) implementa operações de teoria dos conjuntos:

```java
public class GestaoTurmasService {

    // Operação de União: Junta os alunos de duas turmas sem duplicidade
    public Set<Aluno> consolidarTodosAlunos(Set<Aluno> turmaA, Set<Aluno> turmaB) {
        Set<Aluno> uniao = new HashSet<>(turmaA);
        uniao.addAll(turmaB); // A U B
        return uniao;
    }

    // Operação de Interseção: Alunos matriculados simultaneamente em ambas
    public Set<Aluno> listarAlunosEmComum(Set<Aluno> turmaA, Set<Aluno> turmaB) {
        Set<Aluno> intersecao = new HashSet<>(turmaA);
        intersecao.retainAll(turmaB); // A inter B
        return intersecao;
    }

    // Operação de Diferença: Alunos exclusivos da turma A
    public Set<Aluno> listarAlunosExclusivos(Set<Aluno> turmaA, Set<Aluno> turmaB) {
        Set<Aluno> diferenca = new HashSet<>(turmaA);
        diferenca.removeAll(turmaB); // A - B
        return diferenca;
    }

    // Retorna os alunos ordenados alfabeticamente usando TreeSet
    public Set<Aluno> ordenarAlunos(Set<Aluno> alunos) {
        return new TreeSet<>(alunos);
    }
}
```

As operações de união (`addAll`), interseção (`retainAll`) e diferença (`removeAll`) são métodos nativos da interface `Collection` e funcionam diretamente sobre `Set`. A ordenação via `TreeSet` delega para o `compareTo` da classe `Aluno`.

### 3.3. Aplicação Executável: AcademicoSetApp

A classe principal demonstra a barreira de unicidade e as operações de conjuntos:

```java
public class AcademicoSetApp {
    public static void main(String[] args) {
        GestaoTurmasService service = new GestaoTurmasService();

        Set<Aluno> turmaPOO = new HashSet<>();
        Set<Aluno> turmaBancoDados = new HashSet<>();

        Aluno a1 = new Aluno("M01", "111.222.333-01", "Beatriz Costa");
        Aluno a2 = new Aluno("M02", "111.222.333-02", "Carlos Eduardo");
        Aluno a3 = new Aluno("M03", "111.222.333-03", "Ana Clara");

        // Aluno duplicado com matrículas diferentes mas MESMO CPF
        Aluno a1Duplicado = new Aluno("M99", "111.222.333-01", "Beatriz Outra Matrícula");

        turmaPOO.add(a1);
        turmaPOO.add(a2);
        boolean inseriuDuplicado = turmaPOO.add(a1Duplicado); // Rejeitado!

        System.out.println("Tentativa de inserir CPF duplicado: " + inseriuDuplicado);
        System.out.println("Total de inscritos em POO: " + turmaPOO.size());

        turmaBancoDados.add(a2);
        turmaBancoDados.add(a3);

        Set<Aluno> emComum = service.listarAlunosEmComum(turmaPOO, turmaBancoDados);
        emComum.forEach(System.out::println);

        Set<Aluno> exclusivosPOO = service.listarAlunosExclusivos(turmaPOO, turmaBancoDados);
        exclusivosPOO.forEach(System.out::println);

        Set<Aluno> todos = service.consolidarTodosAlunos(turmaPOO, turmaBancoDados);
        Set<Aluno> todosOrdenados = service.ordenarAlunos(todos);
        todosOrdenados.forEach(System.out::println);
    }
}
```

O ponto crítico é a tentativa de inserção do `a1Duplicado`: apesar de ter matrícula e nome diferentes, o CPF é idêntico ao de `a1`, logo o `HashSet` rejeita a inserção (retorna `false`). Isso demonstra que a unicidade no `Set` é controlada pelo contrato `equals`/`hashCode`, não por todos os atributos do objeto.

---

## 4. Exercícios Propostos e Solução

### Exercício: Controle de Acesso Predial (Catraca)

**Enunciado:** Implemente um sistema de controle de acesso predial que gerencie entradas e saídas de funcionários através de uma catraca, utilizando `Set` para garantir unicidade e `TreeSet` para ordenação.

**Pacote da Solução:** `br.edu.universidade.sistema.acesso` em `solucoes/aula-12/`.

**Entidade CrachaFuncionario:**

```java
public class CrachaFuncionario implements Comparable<CrachaFuncionario> {
    private String codigoCartao;
    private String nome;
    private String departamento;

    public CrachaFuncionario(String codigoCartao, String nome, String departamento) {
        if (codigoCartao == null || codigoCartao.trim().isEmpty()) {
            throw new IllegalArgumentException("Código do cartão não pode ser em branco.");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do funcionário não pode ser vazio.");
        }
        if (departamento == null || departamento.trim().isEmpty()) {
            throw new IllegalArgumentException("Departamento não pode ser vazio.");
        }
        this.codigoCartao = codigoCartao;
        this.nome = nome;
        this.departamento = departamento;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CrachaFuncionario)) return false;
        CrachaFuncionario outro = (CrachaFuncionario) o;
        return Objects.equals(this.codigoCartao, outro.codigoCartao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.codigoCartao);
    }

    @Override
    public int compareTo(CrachaFuncionario outro) {
        int porDepartamento = this.departamento.compareTo(outro.departamento);
        if (porDepartamento != 0) return porDepartamento;
        return this.nome.compareTo(outro.nome);
    }
}
```

**Serviço ControleAcessoPredialService:**

```java
public class ControleAcessoPredialService {
    private final Set<CrachaFuncionario> presentesHoje = new HashSet<>();
    private final Set<CrachaFuncionario> cadastradosGeral = new LinkedHashSet<>();

    public boolean registrarEntrada(CrachaFuncionario c) {
        if (c == null) return false;
        if (cadastradosGeral.contains(c) && presentesHoje.add(c)) {
            return true;
        }
        return false;
    }

    public boolean registrarSaida(CrachaFuncionario c) {
        if (c == null) return false;
        return presentesHoje.remove(c);
    }

    public Set<CrachaFuncionario> obterPresentesOrdenados() {
        return new TreeSet<>(presentesHoje);
    }

    public Set<CrachaFuncionario> obterAusentes() {
        Set<CrachaFuncionario> ausentes = new HashSet<>(cadastradosGeral);
        ausentes.removeAll(presentesHoje);
        return ausentes;
    }
}
```

**Aplicação CatracaApp:**

```java
public class CatracaApp {
    public static void main(String[] args) {
        ControleAcessoPredialService service = new ControleAcessoPredialService();

        CrachaFuncionario c1 = new CrachaFuncionario("CRT-001", "Ana Clara", "TI");
        CrachaFuncionario c2 = new CrachaFuncionario("CRT-002", "Carlos Prado", "TI");
        CrachaFuncionario c3 = new CrachaFuncionario("CRT-003", "Beatriz Costa", "RH");
        CrachaFuncionario c4 = new CrachaFuncionario("CRT-004", "Lucas Mendes", "ADMINISTRATIVO");

        service.cadastrarColaborador(c1);
        service.cadastrarColaborador(c2);
        service.cadastrarColaborador(c3);
        service.cadastrarColaborador(c4);

        service.registrarEntrada(c1);
        service.registrarEntrada(c2);
        service.registrarEntrada(c3);

        boolean reuso = service.registrarEntrada(c1);
        System.out.println("Reentrada do mesmo cartão autorizada? " + reuso);

        service.obterPresentesOrdenados().forEach(System.out::println);
        service.obterAusentes().forEach(System.out::println);
    }
}
```

**Observações sobre a solução:**

- O `LinkedHashSet` preserva a ordem de inserção dos cadastros, útil para relatórios.
- A tentativa de reuso do cartão `c1` é barrada porque `c1` já está no set `presentesHoje` (retorna `false`).
- A diferença entre cadastrados e presentes é obtida por `removeAll`, implementando a operação de diferença de conjuntos.
- O `TreeSet` ordena por departamento e depois por nome, graças ao `compareTo` de `CrachaFuncionario`.

---

## 5. Perguntas de Revisão

1. Quais são as três características fundamentais da interface `List<E>`?

2. Por que o acesso por índice em `ArrayList` é O(1), enquanto em `LinkedList` é O(n)?

3. Em quais cenários específicos `LinkedList` pode ser mais vantajoso que `ArrayList`?

4. O que acontece se declararmos a variável como `ArrayList<Cliente>` em vez de `List<Cliente>`?

5. Qual é a diferença entre `List.of()` e `new ArrayList()` no que diz respeito à mutabilidade?

6. Explique o que é uma visão de sublista (`subList`) e por que alterações nela afetam a lista original.

7. Como o `LinkedList` implementa as interfaces `Deque` e `Queue` simultaneamente?

8. Por que a dispersão espacial dos nós do `LinkedList` causa mais *Cache Misses*?

---

## 6. Resumo / Pontos-Chave

- **Interface List\<E\>:** sequência ordenada, indexada, com duplicatas e acesso posicional.
- **Regra de Ouro:** sempre declarar pelo tipo da interface (`List<T>`), nunca pelo tipo concreto (`ArrayList<T>`).
- **ArrayList:** implementação baseada em array contíguo, com O(1) para acesso por índice e O(n) para inserção/remoção no início. É a escolha padrão para 95% dos cenários corporativos.
- **LinkedList:** implementação baseada em nós duplamente encadeados, com O(1) para inserção/remoção nas extremidades e O(n) para acesso por índice. Adequada para filas/pilhas com operações exclusivamente nas pontas.
- **Complexidade Assintótica:** a análise Big-O é fundamental para a decisão de engenharia de software, considerando não apenas a complexidade algorítmica mas também o overhead de cache e memória.
- **List.of()** (Java 9+): cria listas imutáveis e fixas.
- **subList()**: retorna uma visão mutável da lista original, não uma cópia.
- **TreeSet**: garante ordenação natural delegando para o `compareTo` do elemento.
- **Operações de conjuntos:** `addAll` (união), `retainAll` (interseção) e `removeAll` (diferença) funcionam diretamente sobre `Set`.
