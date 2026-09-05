# Tutorial de Java — Aula 19: Tipos Genéricos (Java Generics), Limites e o Princípio PECS

| | |
|---|---|
| **Disciplina** | Programação Orientada a Objetos |
| **Curso** | Análise e Desenvolvimento de Sistemas (3º Período) |
| **Tema** | Tipos Parametrizados (Generics), Segurança em Tempo de Compilação (*Type-Safety*), Classes e Métodos Genéricos, Curingas (*Wildcards* `?`), Princípio PECS (*Producer Extends, Consumer Super*) e Apagamento de Tipos (*Type Erasure*) |
| **Carga Horária** | 100 minutos (2 horas-aula) |
| **Professor Responsável** | Alexandre Neves Louzada |
| **Arquivo de Referência** | `TutorialAula19.md` |

## 1. Objetivos de Aprendizagem

- **Conceitual:** Compreender a necessidade histórica dos tipos genéricos na evolução do Java 5, eliminando a dependência do tipo universal `Object` e das conversões explícitas (casting) inseguras; assimilar o princípio da segurança de tipos em tempo de compilação (*Compile-Time Type-Safety*); entender a invariância inerente aos genéricos.
- **Técnico:** Projetar classes, interfaces e métodos genéricos parametrizados por tipos abstratos (`<T>`, `<K, V>`, `<E>`); declarar restrições de tipos limitados (*Bounded Type Parameters* como `<T extends Number>`); utilizar curingas desconhecidos (`<?>`), curingas com limitador superior (*Upper Bounded Wildcards* `<? extends T>`) e limitadores inferiores (*Lower Bounded Wildcards* `<? super T>`).
- **Arquitetural:** Dominar o princípio PECS (*Producer Extends, Consumer Super*), estabelecendo a assinatura correta para métodos de coleções genéricas em APIs públicas; compreender o funcionamento e as limitações do Apagamento de Tipos (*Type Erasure*) na Máquina Virtual Java (JVM).
- **Prático:** Implementar um repositório genérico em memória desacoplado para entidades de domínio e um motor de processamento e consolidação de documentos fiscais e cadastrais.

## 2. Fundamentação Teórica

### O Contexto Histórico: O Problema das Coleções Pré-Java 5

Antes do Java 5, as estruturas de dados do Java Collections Framework armazenavam exclusivamente referências do tipo genérico universal `java.lang.Object`:

```java
// CÓDIGO LEGADO (Inseguro e frágil):
List nomes = new ArrayList();
nomes.add("Carlos");
nomes.add(Integer.valueOf(100)); // Compila sem qualquer erro!

// Em outro ponto do sistema:
for (int i = 0; i < nomes.size(); i++) {
    String s = (String) nomes.get(i); // Dispara ClassCastException na posição 1!
}
```

Esse modelo causava dois problemas críticos de engenharia:

- **Falta de Verificação Estática:** O compilador não impedia que tipos incompatíveis fossem inseridos na mesma coleção.
- **Proliferação de Castings:** Exigia a conversão explícita manual a cada leitura, transferindo a detecção de erros de tipo para o ambiente de produção (runtime).

Com a introdução dos Generics, o compilador assumiu a responsabilidade de verificar a homogeneidade dos dados em tempo de compilação:

```java
// CÓDIGO MODERNO COM GENERICS:
List<String> nomes = new ArrayList<>();
nomes.add("Carlos");
// nomes.add(100); // ERRO DE COMPILAÇÃO IMEDIATO!
String s = nomes.get(0); // Dispensa casting manual
```

### Convenções de Nomenclatura para Parâmetros de Tipo

Por convenção internacional, utilizam-se letras maiúsculas e únicas para representar parâmetros genéricos:

- **T** (*Type*): Tipo geral genérico.
- **E** (*Element*): Elemento contido em coleções (ex.: `List<E>`, `Set<E>`).
- **K** (*Key*): Chave de mapeamentos associativos (ex.: `Map<K, V>`).
- **V** (*Value*): Valor associado a uma chave (ex.: `Map<K, V>`).
- **N** (*Number*): Tipo restrito a representações numéricas.
- **R** (*Result*): Tipo retornado por métodos funcionais.

### Invariância dos Tipos Genéricos

Embora `Integer` seja uma subclasse de `Number` (`Integer extends Number`), uma lista parametrizada como `List<Integer>` **NÃO** é um subtipo de `List<Number>`:

```plaintext
┌────────────────────────────────────────────────────────┐
│               Integer  ──►  extends Number             │ (Polimorfismo Válido)
└────────────────────────────────────────────────────────┘
                           MAS
┌────────────────────────────────────────────────────────┐
│     List<Integer>  ──►  NÃO É SUBTIPO de List<Number>  │ (Invariância Genérica)
└────────────────────────────────────────────────────────┘
```

Se o Java permitisse que `List<Number> lista = new ArrayList<Integer>();` compilasse, seria possível executar `lista.add(Double.valueOf(3.14));`, corrompendo a lista interna de inteiros. Para contornar essa restrição sem abrir mão do polimorfismo, a linguagem introduziu os **Curingas (Wildcards)**.

### Curingas (Wildcards) e o Princípio PECS

A interrogação (`?`) representa um tipo desconhecido em tempo de compilação. Existem três categorias de wildcards:

- **Curinga Irrestrito (`<?>`):** Representa qualquer tipo desconhecido (`List<?>`). Pode ser consultado como `Object`, mas não aceita inserções (exceto `null`).
- **Curinga com Limitador Superior (`<? extends T>`):** Aceita qualquer tipo que seja `T` ou subclasse de `T`. O limite superior é `T`.
- **Curinga com Limitador Inferior (`<? super T>`):** Aceita qualquer tipo que seja `T` ou ancestral/superclasse de `T`. O limite inferior é `T`.

### O Princípio PECS (Producer Extends, Consumer Super)

Formalizado por Joshua Bloch, o acrônimo PECS define qual wildcard escolher:

> P.E.C.S. ⇔ Producer Extends, Consumer Super

- **Producer Extends (`? extends T`):** Se a coleção for utilizada para fornecer/ler dados para o seu método (a coleção atua como **produtora**), utilize `extends`. É seguro ler instâncias de lá como sendo do tipo `T`. Porém, a coleção torna-se somente leitura (o compilador proíbe a inserção de novos elementos).
- **Consumer Super (`? super T`):** Se a coleção for utilizada para armazenar/receber dados a partir do seu método (a coleção atua como **consumidora**), utilize `super`. É permitido inserir elementos do tipo `T` com total segurança de tipo.

### A Mecânica Interna: O Apagamento de Tipos (Type Erasure)

Para manter a compatibilidade reversa com arquivos binários de versões legadas do Java, os genéricos foram implementados via **Apagamento de Tipos (Type Erasure)**:

- Em tempo de compilação, o compilador verifica todas as regras de tipo e injeta os casts automáticos no Bytecode.
- Em seguida, o compilador remove toda a informação dos tipos genéricos dos arquivos `.class`.
- Na JVM em tempo de execução, um `List<String>` e um `List<Integer>` são idênticos: ambos existem na memória simplesmente como `List` bruto contendo instâncias de `Object`.

```plaintext
Código Fonte (.java):               Bytecode Compilado (.class):
public class Caixa<T> {            public class Caixa {
    private T conteudo;       ──►      private Object conteudo;
    public T get() { ... }             public Object get() { ... }
}                                  }
```

**Consequências Práticas do Type Erasure:**

- Não é possível instanciar um tipo genérico diretamente: `new T()` é proibido.
- Não é possível instanciar arrays genéricos: `new T[10]` é proibido.
- Não é possível usar tipos primitivos em generics: `List<int>` deve ser `List<Integer>`.
- Não é possível usar `instanceof` com tipos parametrizados: `if (x instanceof List<String>)` é ilegal.

## 3. Estudo de Caso Integrado: Repositório Genérico e Processamento PECS

O projeto abaixo implementa uma arquitetura corporativa composta por uma classe base de domínio, um repositório genérico reaproveitável com restrição `<T extends EntidadeBase>` e um serviço que aplica a regra PECS para transferência e auditoria de lotes:

```java
package br.edu.universidade.sistema.generics.dominio;

// 1. Contrato Base de Domínio com Identificador Genérico
public abstract class EntidadeBase<ID> {
    private final ID id;

    public EntidadeBase(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("O identificador não pode ser nulo.");
        }
        this.id = id;
    }

    public ID getId() {
        return id;
    }
}
```

```java
package br.edu.universidade.sistema.generics.dominio;

// 2. Entidade de Domínio Concreta 1: Produto
public class Produto extends EntidadeBase<Long> {
    private final String descricao;
    private final double preco;

    public Produto(Long id, String descricao, double preco) {
        super(id);
        this.descricao = descricao;
        this.preco = preco;
    }

    public String getDescricao() { return descricao; }
    public double getPreco() { return preco; }

    @Override
    public String toString() {
        return String.format("Produto [#%d | %s | R$ %.2f]", getId(), descricao, preco);
    }
}
```

```java
package br.edu.universidade.sistema.generics.dominio;

// 3. Entidade de Domínio Concreta 2: Cliente
public class Cliente extends EntidadeBase<String> {
    private final String nome;

    public Cliente(String cpf, String nome) {
        super(cpf); // ID é a String do CPF
        this.nome = nome;
    }

    public String getNome() { return nome; }

    @Override
    public String toString() {
        return String.format("Cliente [CPF: %s | Nome: %s]", getId(), nome);
    }
}
```

```java
package br.edu.universidade.sistema.generics.repositorio;

import br.edu.universidade.sistema.generics.dominio.EntidadeBase;
import java.util.*;

// 4. Repositório Genérico em Memória com Limite de Tipo (Bounded Type)
public class RepositorioGenerico<T extends EntidadeBase<ID>, ID> {
    private final Map<ID, T> bancoEmMemoria = new HashMap<>();

    public void salvar(T entidade) {
        if (entidade == null) {
            throw new IllegalArgumentException("Entidade nula não pode ser persistida.");
        }
        bancoEmMemoria.put(entidade.getId(), entidade);
    }

    public Optional<T> buscarPorId(ID id) {
        return Optional.ofNullable(bancoEmMemoria.get(id));
    }

    public List<T> listarTodos() {
        return new ArrayList<>(bancoEmMemoria.values());
    }

    public boolean excluir(ID id) {
        return bancoEmMemoria.remove(id) != null;
    }

    public int totalRegistros() {
        return bancoEmMemoria.size();
    }
}
```

```java
package br.edu.universidade.sistema.generics.service;

import br.edu.universidade.sistema.generics.dominio.Produto;
import java.util.List;

// 5. Serviço com Métodos Genéricos aplicando o Princípio PECS
public class ProcessadorLotesService {

    /**
     * Aplica o princípio PECS:
     * - 'origem' é PRODUTORA (Producer Extends): nós lemos elementos dela.
     * - 'destino' é CONSUMIDORA (Consumer Super): nós inserimos elementos nela.
     */
    public static <T> void transferirElementos(
            List<? extends T> origem,  // Producer Extends: aceita T ou qualquer subclasse
            List<? super T> destino    // Consumer Super: aceita T ou qualquer superclasse
    ) {
        for (T elemento : origem) {
            destino.add(elemento); // Operação segura garantida pelo compilador
        }
    }

    // Método com Bounded Wildcard para calcular somas numéricas polimórficas
    public static double calcularPrecoMedio(List<? extends Produto> produtos) {
        if (produtos == null || produtos.isEmpty()) {
            return 0.0;
        }
        double soma = 0.0;
        for (Produto p : produtos) { // Seguro ler como Produto
            soma += p.getPreco();
        }
        return soma / produtos.size();
    }
}
```

```java
package br.edu.universidade.sistema.generics;

import br.edu.universidade.sistema.generics.dominio.Cliente;
import br.edu.universidade.sistema.generics.dominio.Produto;
import br.edu.universidade.sistema.generics.repositorio.RepositorioGenerico;
import br.edu.universidade.sistema.generics.service.ProcessadorLotesService;
import java.util.ArrayList;
import java.util.List;

// 6. Aplicação Executável demonstrando Generics, Repositórios e PECS
public class GenericsApp {
    public static void main(String[] args) {
        System.out.println("--- 1. Repositório Genérico Parametrizado ---");

        // Instanciação com tipos estritos: Repositório de Produto com ID Long
        RepositorioGenerico<Produto, Long> repoProdutos = new RepositorioGenerico<>();
        repoProdutos.salvar(new Produto(101L, "Notebook Gamer", 5500.00));
        repoProdutos.salvar(new Produto(102L, "Monitor Ultrawide", 1800.00));

        System.out.printf("Total de produtos cadastrados: %d%n", repoProdutos.totalRegistros());
        repoProdutos.listarTodos().forEach(System.out::println);

        // Reuso completo da mesma estrutura para Clientes com ID String (CPF)
        RepositorioGenerico<Cliente, String> repoClientes = new RepositorioGenerico<>();
        repoClientes.salvar(new Cliente("111.222.333-44", "Beatriz Costa"));
        repoClientes.salvar(new Cliente("555.666.777-88", "Carlos Eduardo"));

        System.out.printf("%nTotal de clientes cadastrados: %d%n", repoClientes.totalRegistros());
        repoClientes.listarTodos().forEach(System.out::println);

        System.out.println("\n--- 2. Demonstração do Princípio PECS ---");
        List<Produto> loteNovosProdutos = List.of(
                new Produto(103L, "Teclado Mecanico", 350.00),
                new Produto(104L, "Mouse Sem Fio", 180.00)
        );

        // Lista de destino tipada como superclasse direta (Object)
        List<Object> relatorioGeral = new ArrayList<>();

        // PECS em ação: List<Produto> é Produtora (extends) -> List<Object> é Consumidora (super)
        ProcessadorLotesService.transferirElementos(loteNovosProdutos, relatorioGeral);

        System.out.printf("Itens transferidos para a lista geral (%d itens):%n", relatorioGeral.size());
        relatorioGeral.forEach(item -> System.out.println("Relatório Item: " + item));

        System.out.println("\n--- 3. Bounded Wildcard em Operações Aritméticas ---");
        double precoMedio = ProcessadorLotesService.calcularPrecoMedio(loteNovosProdutos);
        System.out.printf("Preço médio do lote de produtos: R$ %.2f%n", precoMedio);
    }
}
```

## 4. Diagnóstico de Erros Comuns e Armadilhas

### Armadilha 1: Tentar Inserir Elementos em uma Coleção com ? extends T

**Código Problemático:**

```java
List<? extends Number> listaNumeros = new ArrayList<Integer>();
listaNumeros.add(Integer.valueOf(10)); // ERRO DE COMPILAÇÃO!
```

- **Diagnóstico da JVM:** `capture of ? extends java.lang.Number cannot be applied to (java.lang.Integer)`.
- **Causa & Correção:** O compilador sabe que a lista contém algum subtipo de `Number`, mas não sabe qual exatamente (pode ser `Double`, `Long`, etc.). Para evitar corrupção de tipo, ele bloqueia qualquer inserção (`? extends` é produtor / somente leitura). Se precisar adicionar elementos, declare a referência com limitador inferior: `List<? super Integer>`.

### Armadilha 2: Tentar Criar Instância ou Array de um Parâmetro de Tipo

**Código Problemático:**

```java
public class Gerenciador<T> {
    private T objeto;
    private T[] vetor;

    public Gerenciador() {
        this.objeto = new T();     // ERRO DE COMPILAÇÃO!
        this.vetor = new T[10];    // ERRO DE COMPILAÇÃO!
    }
}
```

- **Diagnóstico da JVM:** `type parameter T cannot be instantiated directly`.
- **Causa & Correção:** Devido ao Type Erasure, o tipo `T` deixa de existir em tempo de execução, sendo substituído por `Object`. A JVM não saberia qual construtor invocar nem quantos bytes alocar para cada elemento do array. Se a instanciação for mandatória, injete uma fábrica funcional (`Supplier<T>`) ou repasse o objeto literal de classe (`Class<T> clazz`) para instanciar via reflexão.

### Armadilha 3: Incompatibilidade de Tipos Genéricos por Falta de Wildcard

**Código Problemático:**

```java
public void processar(List<Number> lista) { ... }

// No chamador:
List<Integer> meusInteiros = List.of(1, 2, 3);
processar(meusInteiros); // ERRO DE COMPILAÇÃO!
```

- **Diagnóstico da JVM:** `incompatible types: List<Integer> cannot be converted to List<Number>`.
- **Causa & Correção:** Por causa da regra da invariância, `List<Integer>` não é compatível com `List<Number>`. Altere o parâmetro do método para aceitar o limitador superior: `public void processar(List<? extends Number> lista)`.

## 5. Roteiro Prático de Depuração: Inspecionando o Type Erasure na IDE

Para auditar o efeito prático do apagamento de tipos na sua IDE (IntelliJ IDEA, Eclipse ou VS Code):

1. No código `GenericsApp`, coloque um ponto de interrupção (*breakpoint*) logo após a linha `repoProdutos.salvar(...)`.
2. Inicie a aplicação em modo de depuração (*Debug*).
3. Na janela de variáveis (*Variables*):
   - Expanda a instância `repoProdutos` e localize o mapa `bancoEmMemoria`.
   - Observe que os nós da tabela interna contêm instâncias genéricas com chaves e valores tratados como `Object`.
   - O depurador exibe os tipos concretos em tempo de execução (`Produto` e `Long`), mas os metadados estruturais da classe compilada são uniformes para ambos os repositórios.
4. Execute via terminal o comando `javap -c RepositorioGenerico.class`:
   - Observe que a assinatura gerada no Bytecode usa `EntidadeBase` como limite superior nos métodos, confirmando a eliminação dos parâmetros `<T>` após a fase de validação estática.

## 6. Exercício de Fixação Prática: Motor de Auditoria e Validação Genérica

Implemente um componente corporativo reutilizável de auditoria aplicando classes genéricas, limites de tipo e PECS:

1. **Construa a Interface Genérica `Auditor<T>`:**
   - Declaração de método: `boolean auditar(T objeto)`.

2. **Construa a Classe `AuditoriaService` com Métodos Genéricos:**
   - **Método de contagem genérico:**
     ```java
     public static <T> int contarAprovados(List<? extends T> lista, Auditor<? super T> auditor)
     ```
     - Observe o Princípio PECS: a lista é produtora (`extends`) e o auditor é consumidor (`super`).
     - Itera sobre a lista executando `auditor.auditar(item)` e retorna a quantidade de itens que retornaram `true`.
   - **Método de mesclagem e cópia segura:**
     ```java
     public static <T> void mesclarColecoes(List<? extends T> fonte, List<? super T> destino)
     ```
     - Copia todos os elementos da lista `fonte` para a lista de `destino` garantindo a segurança de tipos do compilador.

3. **Construa a Classe de Teste `AuditoriaGenericsApp`:**
   - Crie uma lista com quatro instâncias da classe `Produto` (dois com preço acima de R$ 1.000,00 e dois abaixo).
   - Implemente um `Auditor<Object>` genérico que aprove objetos cujo `toString()` possua mais de 10 caracteres.
   - Implemente um `Auditor<Produto>` específico que aprove produtos com preço superior a R$ 1.000,00.
   - Execute o método `contarAprovados` passando ambos os auditores, comprovando que a assinatura `Auditor<? super T>` permite reaproveitar validadores de superclasses genéricas em listas de subtipos específicos sem erros de compilação.