# Tutorial de Java — Aula 12: Conjuntos e Unicidade com a Interface Set, Tabela Hash e o Contrato equals() e hashCode()

| | |
|---|---|
| **Disciplina** | Programação Orientada a Objetos |
| **Curso** | Análise e Desenvolvimento de Sistemas (3º Período) |
| **Tema** | A Interface java.util.Set, Implementações Concretas (HashSet, LinkedHashSet, TreeSet), Funções Hash e o Contrato Obrigatório de equals() e hashCode() |
| **Carga Horária** | 100 minutos (2 horas-aula) |
| **Professor Responsável** | Alexandre Neves Louzada |
| **Arquivo de Referência** | `TutorialAula12.md` |

## 1. Objetivos de Aprendizagem

- **Conceitual:** Compreender o modelo matemático de conjuntos aplicado à programação; assimilar que a interface `Set` proíbe terminantemente elementos duplicados; dominar o funcionamento interno de uma tabela de dispersão (*Hash Table*), funções *hash* e o tratamento de colisões por encadeamento.
- **Técnico:** Sobrescrever de forma correta e simétrica os métodos `equals(Object)` e `hashCode()` herdados da raiz `java.lang.Object`; dominar as operações fundamentais em conjuntos (`add`, `remove`, `contains`, `size`) e operações matemáticas em lote (união com `addAll`, interseção com `retainAll` e diferença com `removeAll`).
- **Arquitetural:** Avaliar critérios de escolha entre as três principais implementações da interface `Set`: `HashSet` (alta performance sem ordem garantida), `LinkedHashSet` (preservação da ordem de inserção via lista duplamente encadeada interna) e `TreeSet` (ordenação natural ou customizada baseada em árvore rubro-negra).
- **Prático:** Implementar um motor de controle de acesso de catracas e controle de matrículas acadêmicas, garantindo unicidade por CPF/matrícula e realizando cálculos de alunos matriculados em múltiplas disciplinas simultaneamente.

## 2. Fundamentação Teórica

### O Conceito de Conjunto no Java Collections Framework

Diferente de `java.util.List` (onde elementos são indexados numericamente e repetições são permitidas), a interface `java.util.Set<T>` modela a abstração de conjuntos matemáticos:

- **Unicidade Absoluta:** O conjunto nunca aceita elementos duplicados. Ao tentar inserir um elemento já existente, o método `.add()` simplesmente rejeita a operação e retorna `false`, sem alterar a coleção.
- **Ausência de Índices:** Não existe o conceito de acesso posicional (não há métodos como `get(int index)` ou `remove(int index)`).
- **Busca Ultra Rápida:** Dependendo da implementação concreta, a verificação de pertinência (`.contains(objeto)`) ocorre em tempo constante médio (O(1)), sendo imensamente superior à busca linear em listas (O(n)).

### As Três Implementações Concretas de Set

| Implementação | Estrutura de Dados Subjacente | Ordenação dos Elementos | Complexidade Média (add, contains) |
|---|---|---|---|
| `HashSet` | Tabela Hash (Hash Table) | Nenhuma garantia de ordem (a ordem pode mudar com mutações) | O(1) (Tempo constante) |
| `LinkedHashSet` | Tabela Hash + Lista Duplamente Encadeada | Preserva a ordem de inserção dos elementos | O(1) (Tempo constante com leve custo extra de ponteiros) |
| `TreeSet` | Árvore Rubro-Negra balanceada (Red-Black Tree) | Ordenação natural (`Comparable`) ou via `Comparator` | O(log n) (Tempo logarítmico) |

### A Mecânica Interna da Tabela Hash (Hash Table)

A alta performance do `HashSet` apoia-se em uma função matemática que calcula uma posição dentro de uma tabela de compartimentos (*buckets*):

> Índice do Balde (Bucket) = hashCode(objeto) mod Tamanho da Tabela

```plaintext
Entrada: Aluno("111.222.333-44")
                │
                ▼
      hashCode() -> 84920485
                │
                ▼ (Módulo / Hash Spread)
         Balde #4 [Bucket]
                │
                ▼
    [ Colisão detectada? ]
    ├── NÃO: Insere nó diretamente
    └── SIM: Percorre a lista do balde comparando com equals()!
```

Quando dois objetos diferentes produzem o mesmo índice de balde, ocorre uma colisão de hash. O Java resolve colisões organizando os elementos daquele balde em uma lista encadeada (ou convertendo para uma árvore rubro-negra balanceada caso o balde exceda 8 elementos).

### O Contrato Universal entre equals() e hashCode()

Para que o `HashSet` funcione corretamente, os métodos herdados de `java.lang.Object` devem obedecer a regras estritas:

- **Se `objA.equals(objB) == true`, então `objA.hashCode() == objB.hashCode()` deve ser OBRIGATORIAMENTE verdadeiro.** Se dois objetos são considerados iguais em regras de negócio, eles devem gerar rigorosamente o mesmo código hash para caírem no mesmo balde.
- **Se `objA.hashCode() == objB.hashCode()`, NÃO significa necessariamente que `objA.equals(objB)` seja verdadeiro.** Objetos diferentes podem colidir no mesmo balde. Quando isso ocorre, o Java percorre o balde e executa o método `.equals()` em cada elemento para saber se o objeto já existe ali.
- **Consistência:** Se o objeto não sofreu alterações nos atributos usados no cálculo, sucessivas chamadas a `hashCode()` devem produzir rigorosamente o mesmo número inteiro.

**Regra de Ouro:** Se você sobrescrever `equals()`, é compulsório sobrescrever `hashCode()` utilizando exatamente os mesmos atributos. Caso contrário, objetos equivalentes cairão em baldes diferentes e o `HashSet` aceitará cadastros duplicados!

## 3. Estudo de Caso Integrado: Sistema de Matrícula e Controle de Acesso Acadêmico

O projeto abaixo implementa uma entidade acadêmica com o contrato equals/hashCode baseado no CPF e um serviço de auditoria que calcula intersecções e uniões entre disciplinas:

```java
package br.edu.universidade.sistema.academico.dominio;

import java.util.Objects;

// 1. Entidade de Domínio respeitando o contrato de integridade do Set
public class Aluno implements Comparable<Aluno> {
    private final String matricula;
    private final String cpf;
    private String nome;

    public Aluno(String matricula, String cpf, String nome) {
        if (matricula == null || cpf == null || nome == null) {
            throw new IllegalArgumentException("Campos de identificação não podem ser nulos.");
        }
        this.matricula = matricula.trim();
        this.cpf = cpf.replaceAll("\\D", ""); // Sanitização básica
        this.nome = nome.trim();
    }

    public String getMatricula() {
        return matricula;
    }

    public String getCpf() {
        return cpf;
    }

    public String getNome() {
        return nome;
    }

    // Regra de igualdade: Dois alunos são iguais se possuírem o mesmo CPF
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Aluno outro = (Aluno) obj;
        return Objects.equals(this.cpf, outro.cpf);
    }

    // Regra de espalhamento: Hash calculado exclusivamente sobre o mesmo atributo do equals
    @Override
    public int hashCode() {
        return Objects.hash(this.cpf);
    }

    // Ordenação natural de suporte a TreeSet (por nome alfabético)
    @Override
    public int compareTo(Aluno outro) {
        return this.nome.compareToIgnoreCase(outro.nome);
    }

    @Override
    public String toString() {
        return String.format("Aluno [Matrícula: %s | CPF: %s | Nome: %s]", matricula, cpf, nome);
    }
}
```

```java
package br.edu.universidade.sistema.academico.service;

import br.edu.universidade.sistema.academico.dominio.Aluno;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

// 2. Serviço com Operações de Conjuntos (União, Interseção e Diferença)
public class GestaoTurmasService {

    // Operação de União: Junta os alunos de duas turmas sem duplicidade
    public Set<Aluno> consolidarTodosAlunos(Set<Aluno> turmaA, Set<Aluno> turmaB) {
        Set<Aluno> uniao = new HashSet<>(turmaA);
        uniao.addAll(turmaB); // Operação Matemática: A ∪ B
        return uniao;
    }

    // Operação de Interseção: Alunos matriculados simultaneamente em ambas
    public Set<Aluno> listarAlunosEmComum(Set<Aluno> turmaA, Set<Aluno> turmaB) {
        Set<Aluno> intersecao = new HashSet<>(turmaA);
        intersecao.retainAll(turmaB); // Operação Matemática: A ∩ B
        return intersecao;
    }

    // Operação de Diferença: Alunos exclusivos da turma A
    public Set<Aluno> listarAlunosExclusivos(Set<Aluno> turmaA, Set<Aluno> turmaB) {
        Set<Aluno> diferenca = new HashSet<>(turmaA);
        diferenca.removeAll(turmaB); // Operação Matemática: A - B
        return diferenca;
    }

    // Retorna os alunos ordenados alfabeticamente usando TreeSet
    public Set<Aluno> ordenarAlunos(Set<Aluno> alunos) {
        return new TreeSet<>(alunos); // Delega para o compareTo da classe Aluno
    }
}
```

```java
package br.edu.universidade.sistema.academico;

import br.edu.universidade.sistema.academico.dominio.Aluno;
import br.edu.universidade.sistema.academico.service.GestaoTurmasService;
import java.util.HashSet;
import java.util.Set;

// 3. Aplicação Executável demonstrando a barreira de unicidade e as operações
public class AcademicoSetApp {
    public static void main(String[] args) {
        GestaoTurmasService service = new GestaoTurmasService();

        // Criando turmas utilizando a interface Set declarativa
        Set<Aluno> turmaPOO = new HashSet<>();
        Set<Aluno> turmaBancoDados = new HashSet<>();

        Aluno a1 = new Aluno("M01", "111.222.333-01", "Beatriz Costa");
        Aluno a2 = new Aluno("M02", "111.222.333-02", "Carlos Eduardo");
        Aluno a3 = new Aluno("M03", "111.222.333-03", "Ana Clara");

        // Aluno duplicado com matrículas diferentes mas MESMO CPF de Beatriz
        Aluno a1Duplicado = new Aluno("M99", "111.222.333-01", "Beatriz Outra Matricula");

        turmaPOO.add(a1);
        turmaPOO.add(a2);
        boolean inseriuDuplicado = turmaPOO.add(a1Duplicado); // Rejeitado!

        System.out.println("--- 1. Teste de Unicidade no Set ---");
        System.out.println("Tentativa de inserir CPF duplicado teve sucesso? " + inseriuDuplicado);
        System.out.printf("Total de inscritos em POO: %d (Duplicata barrada)%n", turmaPOO.size());

        // Montando a segunda turma
        turmaBancoDados.add(a2); // Carlos também faz Banco de Dados
        turmaBancoDados.add(a3); // Ana Clara só faz Banco de Dados

        System.out.println("\n--- 2. Operação de Interseção (Alunos em Ambas as Disciplinas) ---");
        Set<Aluno> emComum = service.listarAlunosEmComum(turmaPOO, turmaBancoDados);
        emComum.forEach(System.out::println);

        System.out.println("\n--- 3. Operação de Diferença (Alunos Exclusivos de POO) ---");
        Set<Aluno> exclusivosPOO = service.listarAlunosExclusivos(turmaPOO, turmaBancoDados);
        exclusivosPOO.forEach(System.out::println);

        System.out.println("\n--- 4. Operação de União com Ordenação via TreeSet ---");
        Set<Aluno> todos = service.consolidarTodosAlunos(turmaPOO, turmaBancoDados);
        Set<Aluno> todosOrdenados = service.ordenarAlunos(todos);
        todosOrdenados.forEach(System.out::println);
    }
}
```

## 4. Diagnóstico de Erros Comuns e Armadilhas

### Armadilha 1: Sobrescrever equals() mas Esquecer de Sobrescrever hashCode()

**Código Problemático:**

```java
public class Cliente {
    private String cpf;
    @Override
    public boolean equals(Object o) { ... } // Compara CPF
    // Não sobrescreveu hashCode()!
}
```

- **Impacto em Tempo de Execução:** Ao adicionar dois clientes com o mesmo CPF no `HashSet`, a JVM executa a implementação herdada de `Object.hashCode()`, que calcula o código baseando-se no endereço de memória original do Heap. Cada cliente cai em um balde diferente da tabela hash; o método `.equals()` sequer é chamado e o `HashSet` aceita elementos duplicados, corrompendo a integridade do sistema.
- **Correção:** Sempre implemente ambos em conjunto utilizando as mesmas propriedades invariantes: `Objects.hash(cpf)`.

### Armadilha 2: Modificar Atributos Usados no Hash com o Objeto Já Dentro do Set

**Código Problemático:**

```java
Set<Usuario> usuarios = new HashSet<>();
Usuario u = new Usuario("usuario1");
usuarios.add(u);

u.setLogin("novoLoginModificado"); // Altera o atributo que gera o hash!
boolean achou = usuarios.contains(u); // RETORNA FALSE!
```

- **Diagnóstico Técnico:** O objeto foi alocado no balde correspondente ao hash de `"usuario1"`. Quando você consulta passando o objeto com `"novoLoginModificado"`, a JVM calcula um novo índice de balde onde o objeto não está. O objeto torna-se um "fantasma": está dentro da coleção, mas não pode ser localizado por `.contains()` nem removido por `.remove()`.
- **Correção:** Utilize exclusivamente atributos imutáveis (`final`) no cálculo de `equals()` e `hashCode()`.

### Armadilha 3: Inserir Objetos em TreeSet sem Implementar Comparable

**Código Problemático:**

```java
Set<Dispositivo> rede = new TreeSet<>();
rede.add(new Dispositivo("Roteador")); // Dispositivo não implementa Comparable!
```

- **Diagnóstico da JVM:** `java.lang.ClassCastException: class Dispositivo cannot be cast to class java.lang.Comparable` lançada em tempo de execução.
- **Causa & Correção:** O `TreeSet` precisa comparar os nós para organizar a árvore rubro-negra. Ou a classe implementa `Comparable<T>` com o método `compareTo()`, ou um `Comparator<T>` deve ser fornecido obrigatoriamente no construtor do conjunto: `new TreeSet<>(Comparator.comparing(Dispositivo::getNome))`.

## 5. Roteiro Prático de Depuração: Inspecionando Baldes da Tabela Hash na IDE

Para auditar o cálculo do hash e a distribuição em baldes no depurador da sua IDE (IntelliJ IDEA, Eclipse ou VS Code):

1. No código `AcademicoSetApp`, coloque um ponto de interrupção (*breakpoint*) logo após a chamada `turmaPOO.add(a1);`.
2. Inicie o programa em modo de depuração (*Debug*).
3. Abra a janela de variáveis (*Variables*) e expanda o objeto `turmaPOO`:
   - Observe o atributo interno `map` (a implementação do `HashSet` encapsula uma instância privada de `HashMap`).
   - Expanda o vetor `table` (a estrutura de baldes).
   - Localize o índice numérico onde o nó de `a1` foi alocado.
4. Avance a execução (*Step Over* — F8) na inserção do `a1Duplicado`:
   - Veja que a JVM gera o mesmo hash, cai no mesmo índice do vetor `table`, percorre o nó existente, dispara o método `equals()` retornando `true`, e cancela a inserção preservando a unicidade do conjunto.

## 6. Exercício de Fixação Prática: Sistema de Controle de Catracas Corporativas

Implemente uma rotina de controle de acesso físico para edifícios empresariais aplicando conjuntos:

1. **Construa a Classe `CrachaFuncionario`:**
   - Atributos privados: `codigoCartao` (`String`), `nome` (`String`) e `departamento` (`String`).
   - Construtor parametrizado completo rejeitando códigos de cartão em branco via `IllegalArgumentException`.
   - Sobrescreva `equals()` e `hashCode()` considerando exclusivamente o atributo `codigoCartao`.
   - Implemente `Comparable<CrachaFuncionario>` comparando alfabeticamente pelo `departamento` seguido pelo `nome`.
   - Método descritivo `toString()`.

2. **Construa a Classe `ControleAcessoPredialService`:**
   - Declare dois conjuntos internos:
     ```java
     private final Set<CrachaFuncionario> presentesHoje = new HashSet<>();
     private final Set<CrachaFuncionario> cadastradosGeral = new LinkedHashSet<>();
     ```
   - Método `void cadastrarColaborador(CrachaFuncionario c)`: adiciona ao conjunto geral garantindo ordem de cadastro.
   - Método `boolean registrarEntrada(CrachaFuncionario c)`: verifica se o colaborador está cadastrado no geral; se estiver, adiciona ao conjunto de presentes e retorna `true`; caso contrário, recusa a entrada.
   - Método `boolean registrarSaida(CrachaFuncionario c)`: remove o colaborador do conjunto de presentes.
   - Método `Set<CrachaFuncionario> obterPresentesOrdenados()`: retorna um `TreeSet` com os presentes atuais agrupados pela regra natural do `Comparable`.
   - Método `Set<CrachaFuncionario> obterAusentes()`: executa a diferença (`cadastradosGeral - presentesHoje`) para saber quem ainda não compareceu à empresa hoje.

3. **Construa a Classe Executável `CatracaApp`:**
   - Cadastre quatro colaboradores no sistema.
   - Simule a entrada de três colaboradores na catraca, incluindo uma tentativa fraudulenta de reutilizar o mesmo código de cartão.
   - Exiba a listagem consolidada de colaboradores presentes no edifício ordenada por departamento e a listagem dos colaboradores ausentes.