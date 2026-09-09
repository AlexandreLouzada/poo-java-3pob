# Explicação da Aula 17 — Paradigma Funcional: Interfaces Funcionais e Expressões Lambda

| | |
|---|---|
| **Disciplina** | Programação Orientada a Objetos |
| **Curso** | Análise e Desenvolvimento de Sistemas (3º Período) |
| **Tema** | Paradigma Funcional: Interfaces Funcionais e Expressões Lambda |
| **Carga Horária** | 100 minutos (2 horas-aula) |
| **Professor** | Alexandre Neves Louzada |
| **Plano de Aula** | `Aulas 3pob/Aula 17.md` |
| **Tutorial** | `aulas/TutorialAula17.md` |
| **Estudo de Caso** | `exemplos/aula-17/` |
| **Exercícios Resolvidos** | `solucoes/aula-17/` |

---

## 1. Objetivos de Aprendizagem

**Conceitual:** Compreender o paradigma funcional introduzido no Java 8; reconhecer o conceito de funções de primeira classe (*First-Class Functions*); entender a evolução sintática da verbosidade de classes anônimas para expressões declarativas concisas; compreender o conceito de contrato SAM (*Single Abstract Method*).

**Técnico:** Validar e criar interfaces funcionais personalizadas com a anotação `@FunctionalInterface`; dominar a sintaxe e regras de inferência de tipo das Expressões Lambda `(parâmetros) -> { corpo }`; utilizar o pacote padronizado `java.util.function` (`Predicate<T>`, `Consumer<T>`, `Function<T, R>`, `Supplier<T>`); aplicar iteração interna com `Iterable.forEach()` e remoção condicional com `Collection.removeIf()`.

**Arquitetural:** Projetar motores de filtragem, transformação e processamento genéricos e desacoplados, onde comportamentos de negócio são repassados como argumentos parametrizados para métodos de serviço.

**Prático:** Implementar um motor de regras de RH (`RhAnalyticsService`), utilizando expressões lambda dinâmicas e composição de predicados para filtragem de colaboradores elegíveis a promoções e reajustes salariais.

---

## 2. Conteúdo Teórico Detalhado

### 2.1. A Mudança de Paradigma: Do Imperativo ao Funcional

O Java 8 (2014) representou um marco na história da linguagem ao introduzir a convergência entre Orientação a Objetos e Programação Funcional. Até então, os desenvolvedores Java passavam apenas **dados** (primitivos ou objetos) como argumentos de métodos. A programação funcional permite passar **comportamento** — código propriamente dito — diretamente como parâmetro.

Isso abre possibilidades antes impossíveis ou extremamente verbosas: ordenar uma lista passando uma regra de comparação como argumento, filtrar uma coleção com base em uma condição definida em tempo de execução, ou reagir a cada elemento de uma coleção com uma ação personalizada.

### 2.2. O Antecessor das Lambdas: Classes Anônimas

Antes do Java 8, a forma de passar comportamento como argumento era instanciar uma **classe anônima** (*Anonymous Inner Classes*). Esse padrão, embora funcional, era extremamente verboso:

```java
// Necessidade de instanciar uma interface inteira para passar UMA função
Collections.sort(funcionarios, new Comparator<Funcionario>() {
    @Override
    public int compare(Funcionario f1, Funcionario f2) {
        return Double.compare(f1.getSalario(), f2.getSalario());
    }
});
```

Esse trecho ocupa 6 linhas para expressar algo que, conceitualmente, é uma única instrução: "ordene por salário". A verbosidade decorre da necessidade de declarar a interface, o tipo do parâmetro, e a estrutura completa da classe anônima.

### 2.3. Interfaces Funcionais e o Contrato SAM

Uma **interface funcional** é toda interface Java que possui exatamente um método abstrato. Esse contrato é chamado de **SAM** (*Single Abstract Method*). O Java 8 reconhece interfaces funcionais de forma implícita, mas a anotação `@FunctionalInterface` documenta essa intenção e garante segurança de compilação.

```java
@FunctionalInterface
public interface Calculadora<T> {
    double calcular(T item);
}
```

Se alguém tentar adicionar um segundo método abstrato a essa interface, o compilador gera um erro — protegendo o contrato SAM.

### 2.4. Anatomia das Expressões Lambda

Uma expressão lambda é a forma concisa de implementar uma interface funcional. Sua sintaxe geral é:

```
(parâmetros) -> { corpo }
```

| Parte | Descrição | Exemplo |
|---|---|---|
| Parâmetros | Lista de argumentos, com ou sem tipo explícito | `(f1, f2)`, `(String nome)`, `(x)` |
| Seta | Separador entre parâmetros e corpo | `->` |
| Corpo | Instrução única (sem chaves) ou bloco completo (com chaves) | `f1.getSalario() > 7000` ou `{ return ...; }` |

Regras de inferência de tipo:
- O tipo dos parâmetros é inferido pelo compilador a partir do contexto da interface funcional.
- Quando há um único parâmetro sem tipo, os parênteses são opcionais: `x -> x * 2`.
- Quando o corpo é uma única expressão com retorno implícito, não se usa `return` nem chaves: `x -> x * 2`.
- Quando o corpo possui múltiplas instruções, é obrigatório usar chaves e `return` explícito.

### 2.5. Conversão de Classes Anônimas em Lambdas

A refatoração de classes anônimas para lambdas é sistemática. Cada classe anônima que implementa uma interface funcional pode ser substituída por uma lambda equivalente:

```java
// ANTES: Classe anônima verbosa (6 linhas)
Collections.sort(funcionarios, new Comparator<Funcionario>() {
    @Override
    public int compare(Funcionario f1, Funcionario f2) {
        return Double.compare(f1.getSalario(), f2.getSalario());
    }
});

// DEPOIS: Lambda concisa (1 linha)
funcionarios.sort((f1, f2) -> Double.compare(f1.getSalario(), f2.getSalario()));
```

### 2.6. O Arsenal Funcional: `java.util.function`

O Java 8 adicionou o pacote `java.util.function` com interfaces funcionais padronizadas para os casos de uso mais comuns:

| Interface | Método Abstrato | Descrição | Exemplo de Uso |
|---|---|---|---|
| `Predicate<T>` | `boolean test(T t)` | Avalia uma condição e retorna booleano | `f -> f.getSalario() > 7000` |
| `Consumer<T>` | `void accept(T t)` | Executa uma ação sobre um objeto sem retorno | `f -> System.out.println(f)` |
| `Function<T, R>` | `R apply(T t)` | Transforma um tipo T em um tipo R | `f -> f.getNome()` |
| `Supplier<T>` | `T get()` | Fornece um valor sem receber argumento | `() -> new ArrayList<>()` |
| `UnaryOperator<T>` | `T apply(T t)` | Transforma T em T (caso especial de Function) | `String::toUpperCase` |
| `BinaryOperator<T>` | `T apply(T a, T b)` | Combina dois valores T em um único T | `(a, b) -> a + b` |

### 2.7. Composição de Predicados

Os métodos padrão das interfaces funcionais permitem combinar predicados de forma fluente:

| Método | Descrição | Exemplo |
|---|---|---|
| `and(Predicate)` | Conjunção lógica (E) | `p1.and(p2)` — ambas devem ser verdadeiras |
| `or(Predicate)` | Disjunção lógica (OU) | `p1.or(p2)` — pelo menos uma verdadeira |
| `negate()` | Negação lógica (NÃO) | `p.negate()` — inverte o resultado |

```java
Predicate<Funcionario> salarioAlto = f -> f.getSalarioMensal() > 7000;
Predicate<Funcionario> deptoTi = f -> f.getDepartamento().equals("TECNOLOGIA");

Predicate<Funcionario> seniorTi = deptoTi.and(salarioAlto);
List<Funcionario> senioresTi = funcionarios.stream()
        .filter(seniorTi)
        .collect(Collectors.toList());
```

### 2.8. Iteração Interna: `forEach` e `removeIf`

O Java 8 adicionou métodos de iteração interna nas interfaces `Iterable` e `Collection`:

```java
// forEach: itera sobre cada elemento executando uma ação (Consumer)
funcionarios.forEach(f -> System.out.println(f));

// removeIf: remove todos os elementos que satisfazem um Predicate
funcionarios.removeIf(f -> f.getSalarioMensal() < 3000);
```

### 2.9. Coletas Avançadas com `Collectors`

O exemplo da aula demonstra operações avançadas de coleta que combinam lambdas com o framework de Streams:

| Operação | Método Collectors | Descrição |
|---|---|---|
| Agrupamento simples | `groupingBy(Function)` | Agrupa por chave, mapeando para lista |
| Contagem por grupo | `groupingBy(Function, counting())` | Conta elementos por chave |
| Soma por grupo | `groupingBy(Function, summingDouble(Function))` | Soma um atributo numérico por chave |
| Mapeamento por grupo | `groupingBy(Function, mapping(Function, toList()))` | Transforma elementos antes de agrupar |
| Particionamento | `partitioningBy(Predicate)` | Divide em dois grupos: true e false |
| Concatenação | `joining(delimiter, prefix, suffix)` | Junta strings com separador |

---

## 3. Estudo de Caso Aplicado

O estudo de caso da aula 17 é o **Sistema de Analytics de RH** da universidade, localizado no pacote `br.edu.universidade.sistema.analytics`. Ele demonstra o uso de lambdas, predicados e coletas avançadas para gerar relatórios analíticos de recursos humanos.

### 3.1. Entidade de Domínio: `Funcionario`

Arquivo: `exemplos/aula-17/src/br/edu/universidade/sistema/analytics/dominio/Funcionario.java`

A classe é imutável (campos `final`), com campos: `id`, `nome`, `departamento`, `cargo` e `salarioMensal`. O construtor normaliza os dados: `nome` com `trim()`, `departamento` e `cargo` com `trim().toUpperCase()`. A validação rejeita IDs nulos e salários negativos.

```java
public class Funcionario {
    private final Long id;
    private final String nome;
    private final String departamento;
    private final String cargo;
    private final double salarioMensal;

    public Funcionario(Long id, String nome, String departamento, String cargo, double salarioMensal) {
        if (id == null || salarioMensal < 0.0) {
            throw new IllegalArgumentException("Dados de identificação salarial inválidos.");
        }
        this.id = id;
        this.nome = nome.trim();
        this.departamento = departamento.trim().toUpperCase();
        this.cargo = cargo.trim().toUpperCase();
        this.salarioMensal = salarioMensal;
    }
}
```

Os métodos `equals` e `hashCode` são baseados no `id`, garantindo que dois funcionários com o mesmo ID sejam considerados equivalentes em coleções.

### 3.2. Serviço: `RhAnalyticsService`

Arquivo: `exemplos/aula-17/src/br/edu/universidade/sistema/analytics/service/RhAnalyticsService.java`

Este serviço é o núcleo didático da aula, apresentando seis operações de coleta avançada:

| # | Método | Retorno | Descrição |
|---|---|---|---|
| 1 | `agruparPorDepartamento(List)` | `Map<String, List<Funcionario>>` | Agrupamento simples 1:N com `groupingBy` |
| 2 | `contarColaboradoresPorDepartamento(List)` | `Map<String, Long>` | Contagem com downstream `counting()` |
| 3 | `somarFolhaSalarialPorDepartamento(List)` | `Map<String, Double>` | Soma com downstream `summingDouble()` |
| 4 | `mapearApenasNomesPorDepartamento(List)` | `Map<String, List<String>>` | Transformação com downstream `mapping()` |
| 5 | `particionarPorTetoSalarial(List, double)` | `Map<Boolean, List<Funcionario>>` | Divisão binária com `partitioningBy()` |
| 6 | `emitirListagemNomesFormatada(List)` | `String` | Concatenação com `joining()` |

Implementação destacada — particionamento salarial:

```java
public Map<Boolean, List<Funcionario>> particionarPorTetoSalarial(List<Funcionario> funcionarios, double teto) {
    return funcionarios.stream()
            .collect(Collectors.partitioningBy(f -> f.getSalarioMensal() >= teto));
}
```

Implementação destacada — concatenação de nomes:

```java
public String emitirListagemNomesFormatada(List<Funcionario> funcionarios) {
    return funcionarios.stream()
            .map(Funcionario::getNome)
            .collect(Collectors.joining(", ", "EQUIPE: [", "]"));
}
```

### 3.3. Aplicação Executável: `AnalyticsApp`

Arquivo: `exemplos/aula-17/src/br/edu/universidade/sistema/analytics/AnalyticsApp.java`

A aplicação principal carrega 6 funcionários distribuídos em 3 departamentos (TECNOLOGIA, FINANCEIRO, RH) e demonstra:

1. **Contagem por departamento** com `counting()` — saída formatada com `forEach`.
2. **Custo total salarial por departamento** com `summingDouble`.
3. **Mapeamento de nomes por setor** com `mapping`.
4. **Particionamento salarial** — separa quem ganha acima de R$ 7.000 (teto sênior/liderança).
5. **Concatenação formatada** com `joining`.

```java
List<Funcionario> quadroColaboradores = List.of(
        new Funcionario(101L, "Mariana Silva", "TECNOLOGIA", "ARQUITETA", 9500.00),
        new Funcionario(102L, "Lucas Mendes", "TECNOLOGIA", "DESENVOLVEDOR", 4800.00),
        new Funcionario(103L, "Carlos Prado", "FINANCEIRO", "ANALISTA", 6200.00),
        new Funcionario(104L, "Beatriz Costa", "RH", "COORDENADORA", 7100.00),
        new Funcionario(105L, "Renata Lima", "FINANCEIRO", "DIRETORA", 12500.00),
        new Funcionario(106L, "Thiago Rocha", "TECNOLOGIA", "DESENVOLVEDOR", 5200.00)
);

// Particionamento por teto salarial
Map<Boolean, List<Funcionario>> particionamento =
        service.particionarPorTetoSalarial(quadroColaboradores, 7000.00);
particionamento.get(true).forEach(System.out::println);  // Acima de R$ 7.000
particionamento.get(false).forEach(System.out::println); // Abaixo de R$ 7.000
```

---

## 4. Exercícios Propostos e Solução

### Exercício: Sistema de Auditoria de Mercadorias de Varejo

O exercício proposto consiste em aplicar lambdas e coletas avançadas em um domínio de varejo: análise de mercadorias por setor, com particionamento por ruptura de estoque e geração de catálogo setorial.

### 4.1. Entidade: `ItemMercadoria`

Arquivo: `solucoes/aula-17/src/br/edu/universidade/sistema/varejo/dominio/ItemMercadoria.java`

```java
public class ItemMercadoria {
    private String sku;
    private String nome;
    private String setor;
    private int quantidade;
    private double precoUnitario;

    public ItemMercadoria(String sku, String nome, String setor, int quantidade, double precoUnitario) {
        if (sku == null || sku.trim().isEmpty()) {
            throw new IllegalArgumentException("SKU não pode ser vazio.");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do item não pode ser vazio.");
        }
        if (setor == null || setor.trim().isEmpty()) {
            throw new IllegalArgumentException("Setor não pode ser vazio.");
        }
        if (quantidade < 0) {
            throw new IllegalArgumentException("Quantidade não pode ser negativa: " + quantidade);
        }
        if (precoUnitario <= 0.0) {
            throw new IllegalArgumentException("Preço unitário deve ser positivo: " + precoUnitario);
        }
        this.sku = sku;
        this.nome = nome;
        this.setor = setor;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    public String getSku() { return sku; }
    public String getNome() { return nome; }
    public String getSetor() { return setor; }
    public int getQuantidade() { return quantidade; }
    public double getPrecoUnitario() { return precoUnitario; }

    @Override
    public String toString() {
        return String.format("Item [SKU: %s | Nome: %s | Setor: %s | Qtd: %d | Preço: R$ %.2f]",
                sku, nome, setor, quantidade, precoUnitario);
    }
}
```

### 4.2. Serviço: `AuditoriaMercadoriasService`

Arquivo: `solucoes/aula-17/src/br/edu/universidade/sistema/varejo/service/AuditoriaMercadoriasService.java`

| Método | Retorno | Descrição |
|---|---|---|
| `calcularValorTotalImobilizadoPorSetor(List)` | `Map<String, Double>` | Soma o valor total (quantidade x preço) por setor |
| `particionarPorRupturaEstoque(List)` | `Map<Boolean, List<ItemMercadoria>>` | Divide entre itens com estoque crítico (qtd <= 5) e normal |
| `emitirCatalogoSetorialCsv(List, String)` | `String` | Filtra por setor e gera lista concatenada no formato CSV |
| `listarSetores(List)` | `Set<String>` | Retorna os setores distintos presentes no estoque |

```java
public Map<String, Double> calcularValorTotalImobilizadoPorSetor(List<ItemMercadoria> itens) {
    return itens.stream()
            .collect(Collectors.groupingBy(
                    ItemMercadoria::getSetor,
                    Collectors.summingDouble(i -> i.getQuantidade() * i.getPrecoUnitario())
            ));
}

public Map<Boolean, List<ItemMercadoria>> particionarPorRupturaEstoque(List<ItemMercadoria> itens) {
    return itens.stream()
            .collect(Collectors.partitioningBy(i -> i.getQuantidade() <= 5));
}
```

### 4.3. Aplicação: `VarejoAnalyticsApp`

Arquivo: `solucoes/aula-17/src/br/edu/universidade/sistema/varejo/VarejoAnalyticsApp.java`

```java
public class VarejoAnalyticsApp {
    public static void main(String[] args) {
        AuditoriaMercadoriasService service = new AuditoriaMercadoriasService();

        List<ItemMercadoria> itens = List.of(
                new ItemMercadoria("SKU-AL-01", "Arroz 5kg", "ALIMENTOS", 120, 24.90),
                new ItemMercadoria("SKU-AL-02", "Feijão 1kg", "ALIMENTOS", 4, 9.50),
                new ItemMercadoria("SKU-LM-01", "Detergente 500ml", "LIMPEZA", 60, 3.20),
                new ItemMercadoria("SKU-LM-02", "Sabão em pó 1kg", "LIMPEZA", 2, 12.00),
                new ItemMercadoria("SKU-BE-01", "Refrigerante 2L", "BEBIDAS", 90, 7.80),
                new ItemMercadoria("SKU-BE-02", "Suco Uva 1L", "BEBIDAS", 5, 6.40)
        );

        Map<String, Double> imobilizado = service.calcularValorTotalImobilizadoPorSetor(itens);
        imobilizado.forEach((setor, total) ->
                System.out.printf("Setor: %-10s | Capital implicado: R$ %.2f%n", setor, total));

        Map<Boolean, List<ItemMercadoria>> particionado = service.particionarPorRupturaEstoque(itens);
        System.out.println("EM ALERTA DE RUPTURA:");
        particionado.get(true).forEach(i -> System.out.println("   " + i));
        System.out.println("ESTOQUE NORMAL:");
        particionado.get(false).forEach(i -> System.out.println("   " + i));

        String csv = service.emitirCatalogoSetorialCsv(itens, "LIMPEZA");
        System.out.println(csv);

        service.listarSetores(itens).forEach(System.out::println);
    }
}
```

---

## 5. Perguntas de Revisão

1. O que é uma interface funcional e por que o contrato SAM é fundamental para o funcionamento de lambdas?
2. Qual a diferença sintática entre uma classe anônima e uma expressão lambda que implementa a mesma interface?
3. Quando os parênteses são opcionais nos parâmetros de uma lambda?
4. Quais são as quatro interfaces funcionais principais do pacote `java.util.function` e qual o método abstrato de cada uma?
5. Como funciona a composição de predicados com `and()`, `or()` e `negate()`?
6. Qual a diferença entre `groupingBy` e `partitioningBy` no `Collectors`?
7. Por que a classe `Funcionario` utiliza `trim().toUpperCase()` nos campos de departamento e cargo?
8. Explique a diferença entre `forEach` com lambda e um loop `for-each` tradicional.
9. Em que situações `removeIf` com lambda é preferível a um loop com condicional para remoção?
10. Por que a imutabilidade dos campos (modificador `final`) é uma boa prática em entidades de domínio?

---

## 6. Resumo / Pontos-Chave

- O **paradigma funcional** no Java permite passar comportamento (funções) como argumentos de métodos, expandindo drasticamente a expressividade da linguagem.
- **Interfaces funcionais** possuem exatamente um método abstrato (contrato SAM) e podem ser implementadas por expressões lambda concisas.
- A anotação `@FunctionalInterface` documenta a intenção e protege contra a adição acidental de novos métodos abstratos.
- **Expressões lambda** substituem classes anônimas verbosas por sintaxe declarativa: `(parâmetros) -> corpo`.
- O pacote `java.util.function` fornece interfaces funcionais padronizadas: `Predicate`, `Consumer`, `Function`, `Supplier`, entre outras.
- **Composição de predicados** (`and`, `or`, `negate`) permite construir regras complexas de filtragem de forma modular e legível.
- O framework `Collectors` oferece operações avançadas de coleta: `groupingBy`, `partitioningBy`, `summingDouble`, `counting`, `mapping`, `joining` e `toSet`.
- O estudo de caso **RhAnalyticsService** demonstra como lambdas e coletas avançadas transformam dados brutos de RH em relatórios analíticos expressivos com poucas linhas de código.
- A refatoração sistemática de classes anônimas para lambdas é uma das primeiras oportunidades concretas de melhoria de legibilidade no código Java moderno.
