# Explicação da Aula 18 — Method References (::), Construção Fluente de Comparadores e Consolidação

| | |
|---|---|
| **Disciplina** | Programação Orientada a Objetos |
| **Curso** | Análise e Desenvolvimento de Sistemas (3º Período) |
| **Tema** | *Method References* (`::`), Construção Fluente de Comparadores e Consolidação da Ementa |
| **Carga Horária** | 100 minutos (2 horas-aula) |
| **Professor** | Alexandre Neves Louzada |
| **Plano de Aula** | `Aulas 3pob/Aula 18.md` |
| **Tutorial** | `aulas/TutorialAula18.md` |
| **Estudo de Caso** | `exemplos/aula-18/` |
| **Exercícios Resolvidos** | `solucoes/aula-18/` |

---

## 1. Objetivos de Aprendizagem

**Conceitual:** Compreender os *Method References* (`::`) como açúcar sintático (*syntactic sugar*) para Expressões Lambda que apenas redirecionam chamadas de métodos existentes; dominar a composição fluente de comparadores declarativos; sintetizar e integrar a arquitetura completa desenvolvida ao longo do curso (POO, Encapsulamento, Polimorfismo, Exceções, Collections Framework e Recursos Funcionais).

**Técnico:** Identificar e aplicar os 4 tipos de *Method References* na JVM (método estático, método de instância de um objeto específico, método de instância de um tipo arbitrário e referência a construtor `Class::new`); construir cadeias de ordenação multicritério fluentes com `Comparator.comparing()`, `thenComparing()` e `reversed()`.

**Arquitetural:** Escrever código corporativo em nível *Clean Code*, combinando imutabilidade, tratamento de exceções, estruturas de dados otimizadas e pipelines funcionais declarativos.

**Prático:** Desenvolver o módulo integrador de gestão de catálogo e faturamento comercial (`CatalogoModernoApp`), aplicando filtragens declarativas e ordenações fluentes multicritério por Categoria (ASC), Preço (DESC) e Nome (ASC) utilizando exclusivamente *Method References*.

---

## 2. Conteúdo Teórico Detalhado

### 2.1. A Evolução da Expressividade: Classe Anônima, Lambda e Method Reference

A jornada da expressividade em Java pode ser resumida em três etapas de concisão crescente:

| Etapa | Forma | Exemplo para imprimir cada elemento |
|---|---|---|
| Pre-Java 8 | Classe Anônima | `for (String s : lista) { System.out.println(s); }` |
| Java 8 | Expressão Lambda | `lista.forEach(s -> System.out.println(s));` |
| Java 8+ | Method Reference | `lista.forEach(System.out::println);` |

O *Method Reference* é a forma mais concisa possível de expressar um comportamento que se limita a invocar um método existente, repassando seus argumentos diretamente.

### 2.2. O que é um Method Reference?

Um *Method Reference* é uma sintaxe simplificada e ainda mais concisa para Expressões Lambda que se limitam a invocar um método já existente repassando seus argumentos diretamente.

**Equivalência direta:**

| Expressão Lambda | Method Reference |
|---|---|
| `(msg) -> System.out.println(msg)` | `System.out::println` |
| `(f) -> f.getNome()` | `Funcionario::getNome` |
| `() -> new ArrayList<>()` | `ArrayList::new` |

A **vantagem de engenharia** é a eliminação de redundância sintática quando a lambda não adiciona nenhuma lógica além da própria chamada do método. O *Method Reference* não executa o método no momento da declaração — ele passa a referência/ponteiro do método para ser executado no momento oportuno pela interface funcional.

### 2.3. As Quatro Formas de Method References na JVM

| Categoria | Sintaxe | Exemplo Lambda Equivalente | Exemplo Method Reference |
|---|---|---|---|
| **1. Método Estático** | `Classe::metodoEstatico` | `(x) -> Math.abs(x)` | `Math::abs` |
| **2. Método de Instância de Objeto Específico** | `instancia::metodoInstancia` | `(s) -> System.out.println(s)` | `System.out::println` |
| **3. Método de Instância de Tipo Arbitrário** | `Classe::metodoInstancia` | `(item) -> item.getNome()` | `ItemCatalogo::getNome` |
| **4. Construtor** | `Classe::new` | `() -> new ArrayList<>()` | `ArrayList::new` |

**Detalhamento de cada categoria:**

**Categoria 1 — Método Estático:** Utiliza o nome da classe seguido de `::` e do nome do método estático. A lambda equivalente recebe os mesmos parâmetros que o método estático e simplesmente o invoca.

```java
// Lambda
(x) -> Math.abs(x)

// Method Reference
Math::abs
```

**Categoria 2 — Método de Instância de Objeto Específico:** Utiliza uma instância concreta seguida de `::` e do nome do método. Utilizado quando a lambda sempre opera sobre o mesmo objeto.

```java
// Lambda
(s) -> System.out.println(s)

// Method Reference
System.out::println
```

**Categoria 3 — Método de Instância de Tipo Arbitrário:** Utiliza o nome da classe seguido de `::` e do nome do método de instância. O primeiro parâmetro da lambda é assumido pela JVM como o objeto alvo que executará o método.

```java
// Lambda
(item) -> item.getNome()

// Method Reference
ItemCatalogo::getNome
```

**Categoria 4 — Construtor:** Utiliza o nome da classe seguido de `::new`. A lambda equivalente simplesmente instancia um novo objeto.

```java
// Lambda
() -> new ArrayList<>()

// Method Reference
ArrayList::new
```

### 2.4. Exemplos Práticos de Refatoração

**Exemplo 1 — Impressão:**

```java
// ANTES (Lambda)
equipe.forEach(f -> System.out.println(f));

// DEPOIS (Method Reference)
equipe.forEach(System.out::println);
```

**Exemplo 2 — Transformação:**

```java
// ANTES (Lambda)
nomes.map(n -> n.toUpperCase());

// DEPOIS (Method Reference)
nomes.map(String::toUpperCase);
```

**Exemplo 3 — Fábrica:**

```java
// ANTES (Lambda)
Supplier<List<String>> sup = () -> new ArrayList<>();

// DEPOIS (Method Reference)
Supplier<List<String>> sup = ArrayList::new;
```

Nas IDEs modernas, as ferramentas de análise estática de código (como IntelliJ IDEA e Eclipse) sugerem automaticamente a substituição de lambdas redundantes por *Method References* via atalho (`Alt + Enter`).

### 2.5. Ordenação Declarativa Fluente com `Comparator.comparing`

O modelo legado de ordenação exigia a criação de uma classe separada que implementasse `Comparator`, resultando em cerca de 10 linhas de código para cada critério. O modelo moderno utiliza `Comparator.comparing` com *Method References* para expressar o mesmo comportamento em uma única linha:

```java
// MODELO LEGADO (verboso)
class ComparadorPorNome implements Comparator<Item> {
    @Override
    public int compare(Item i1, Item i2) {
        return i1.getNome().compareTo(i2.getNome());
    }
}
itens.sort(new ComparadorPorNome());

// MODELO MODERNO (fluente, uma linha)
itens.sort(Comparator.comparing(ItemCatalogo::nome));
```

### 2.6. Composição Fluente Multicritério

A verdadeira potência dos comparadores fluentes emerge na ordenação por múltiplos critérios, utilizando `thenComparing()` para adicionar critérios secundários e `reversed()` para inverter a direção:

| Método | Descrição |
|---|---|
| `Comparator.comparing(Function)` | Estabelece o critério primário de ordenação |
| `thenComparing(Function)` | Adiciona critério de desempate secundário |
| `thenComparingDouble/Int/Long(Function)` | Critério numérico de desempate (evita autoboxing) |
| `reversed()` | Inverte a direção da ordenação (ASC para DESC ou vice-versa) |

Exemplo de ordenação multicritério:

```java
Comparator<ItemCatalogo> comparador = Comparator
        .comparing(ItemCatalogo::categoria)         // Primário: Categoria ASC
        .thenComparing(ItemCatalogo::preco)          // Secundário: Preço ASC
        .reversed()                                   // Inverte para DESC
        .thenComparing(ItemCatalogo::nome);           // Terciário: Nome ASC
```

---

## 3. Estudo de Caso Aplicado

### 3.1. Sistema de Auditoria Fiscal — Stream Paralela

O estudo de caso da aula 18 é o **Sistema de Auditoria Fiscal** da universidade, localizado no pacote `br.edu.universidade.sistema.fiscal`. Ele demonstra a comparação de performance entre Streams sequenciais e paralelas em um cenário de alto custo computacional.

#### Entidade de Domínio: `CupomFiscal`

Arquivo: `exemplos/aula-18/src/br/edu/universidade/sistema/fiscal/dominio/CupomFiscal.java`

```java
public class CupomFiscal {
    private final Long id;
    private final String chaveNfe;
    private final double valorTotal;
    private final boolean cancelado;

    public CupomFiscal(Long id, String chaveNfe, double valorTotal, boolean cancelado) {
        this.id = id;
        this.chaveNfe = chaveNfe;
        this.valorTotal = valorTotal;
        this.cancelado = cancelado;
    }

    public Long getId() { return id; }
    public String getChaveNfe() { return chaveNfe; }
    public double getValorTotal() { return valorTotal; }
    public boolean isCancelado() { return cancelado; }

    // Simulação de custo de CPU por elemento: cálculo de validação criptográfica
    public double calcularImpostoComplexo() {
        double imposto = 0.0;
        for (int i = 0; i < 50; i++) {
            imposto += Math.sin(valorTotal) * Math.cos(i);
        }
        return Math.max(0.0, valorTotal * 0.12 + (imposto * 0.001));
    }
}
```

A classe é **imutável** (todos os campos `final`). O método `calcularImpostoComplexo()` simula um custo computacional realista com 50 iterações de operações trigonométricas — representando um cálculo de validação criptográfica ou algoritmo de tributação complexo.

#### Serviço: `AuditoriaFiscalService`

Arquivo: `exemplos/aula-18/src/br/edu/universidade/sistema/fiscal/service/AuditoriaFiscalService.java`

| Método | Tipo | Descrição |
|---|---|---|
| `calcularImpostoTotalSequencial(List)` | Sequencial | Usa `.stream()` — processa em uma única thread |
| `calcularImpostoTotalParalelo(List)` | Paralelo | Usa `.parallelStream()` — distribui no ForkJoinPool |
| `contarTransacoesCriticasParalelo(List, double)` | Paralelo | Conta cupons não cancelados acima de um corte |

```java
public double calcularImpostoTotalSequencial(List<CupomFiscal> cupons) {
    return cupons.stream()
            .filter(c -> !c.isCancelado())
            .mapToDouble(CupomFiscal::calcularImpostoComplexo)
            .sum();
}

public double calcularImpostoTotalParalelo(List<CupomFiscal> cupons) {
    return cupons.parallelStream()
            .filter(c -> !c.isCancelado())
            .mapToDouble(CupomFiscal::calcularImpostoComplexo)
            .sum();
}
```

A única diferença entre os dois métodos é `.stream()` versus `.parallelStream()`. A operação `.sum()` é thread-safe por padrão no contexto de `parallelStream`.

#### Aplicação: `ParalelismoFiscalApp`

Arquivo: `exemplos/aula-18/src/br/edu/universidade/sistema/fiscal/ParalelismoFiscalApp.java`

A aplicação gera 1.000.000 de cupons fictícios (10% cancelados) e executa o benchmark comparativo:

```java
public static void main(String[] args) {
    System.out.printf("Núcleos de CPU Lógicos Detectados: %d%n",
            Runtime.getRuntime().availableProcessors());
    System.out.printf("Threads no ForkJoinPool Comum    : %d%n",
            ForkJoinPool.getCommonPoolParallelism());

    int volumeDados = 1_000_000;
    List<CupomFiscal> baseCupons = new ArrayList<>(volumeDados);

    for (long i = 1; i <= volumeDados; i++) {
        baseCupons.add(new CupomFiscal(
                i, "NFE-" + i, 100.0 + (i % 500), (i % 10 == 0)
        ));
    }

    AuditoriaFiscalService service = new AuditoriaFiscalService();

    // Execução Sequencial
    long inicioSeq = System.currentTimeMillis();
    double totalImpostosSeq = service.calcularImpostoTotalSequencial(baseCupons);
    long duracaoSeq = System.currentTimeMillis() - inicioSeq;

    // Execução Paralela
    long inicioPar = System.currentTimeMillis();
    double totalImpostosPar = service.calcularImpostoTotalParalelo(baseCupons);
    long duracaoPar = System.currentTimeMillis() - inicioPar;

    // Análise de Eficiência (Speedup)
    double speedup = (double) duracaoSeq / duracaoPar;
    System.out.printf("Fator de Aceleração Real (Speedup): %.2fx mais rápido%n", speedup);

    long criticos = service.contarTransacoesCriticasParalelo(baseCupons, 500.0);
    System.out.printf("Total de Cupons com Valor >= R$ 500: %d%n", criticos);
}
```

### 3.2. Por que Stream Paralela Funciona?

A `parallelStream()` distribui o processamento entre os núcleos de CPU disponíveis utilizando o **ForkJoinPool**, o framework de execução paralela embutido na JVM. Cada núcleo de CPU processa uma fatia dos dados, e os resultados são combinados ao final.

| Aspecto | Stream Sequencial | Stream Paralela |
|---|---|---|
| Execução | Uma única thread | Múltiplas threads (ForkJoinPool) |
| Uso de CPU | ~1 núcleo | Todos os núcleos disponíveis |
| Overhead | Mínimo | Compartilhamento de tarefas |
| Ideal para | Operações simples, poucos dados | Operações custosas, grandes volumes |
| Thread-safety | N/A | Reduções (sum, count) são thread-safe |

**Quando usar `parallelStream()`:**
- Quando cada elemento exige processamento pesado (cálculos complexos, operações de E/S).
- Quando o volume de dados é grande (milhares ou milhões de registros).
- Quando a operação é stateless (não depende de estado compartilhado).

**Quando NÃO usar:**
- Quando o pipeline é simples e o volume é pequeno (o overhead de criação de threads supera o ganho).
- Quando a operação é stateful e requer sincronização explícita.

---

## 4. Exercícios Propostos e Solução

### Exercício: Sistema de Telemetria IoT com Method References e Paralelismo

O exercício proposto combina os dois temas da aula — *Method References* e *Stream Paralela* — em um domínio de telemetria industrial com sensores IoT.

### 4.1. Entidade: `SinalSensor`

Arquivo: `solucoes/aula-18/src/br/edu/universidade/sistema/telemetria/dominio/SinalSensor.java`

```java
public class SinalSensor {
    private Long idSensor;
    private String macAddress;
    private double leituraTemperatura;
    private boolean statusCritico;

    public SinalSensor(Long idSensor, String macAddress, double leituraTemperatura, boolean statusCritico) {
        if (idSensor == null) {
            throw new IllegalArgumentException("ID do sensor não pode ser nulo.");
        }
        if (macAddress == null) {
            throw new IllegalArgumentException("Endereço MAC não pode ser nulo.");
        }
        this.idSensor = idSensor;
        this.macAddress = macAddress;
        this.leituraTemperatura = leituraTemperatura;
        this.statusCritico = statusCritico;
    }

    public double processarNormalizacao() {
        double resultado = leituraTemperatura;
        for (int i = 0; i < 100; i++) {
            double angulo = i * Math.PI / 180.0;
            resultado += leituraTemperatura * Math.sqrt(Math.abs(Math.sin(angulo)))
                    + Math.cos(angulo) * 0.5;
        }
        return resultado;
    }

    public Long getIdSensor() { return idSensor; }
    public String getMacAddress() { return macAddress; }
    public double getLeituraTemperatura() { return leituraTemperatura; }
    public boolean isStatusCritico() { return statusCritico; }

    @Override
    public String toString() {
        return String.format("Sensor [ID: %d | MAC: %s | Temp: %.2f | Crítico: %s]",
                idSensor, macAddress, leituraTemperatura, statusCritico);
    }
}
```

O método `processarNormalizacao()` simula um algoritmo de normalização de sinais com 100 iterações de operações trigonométricas, representando o custo computacional real de processamento de dados IoT.

### 4.2. Serviço: `AuditoriaTelemetriaService`

Arquivo: `solucoes/aula-18/src/br/edu/universidade/sistema/telemetria/service/AuditoriaTelemetriaService.java`

| Método | Tipo | Descrição |
|---|---|---|
| `consolidarMediaTermicaSequencial(List)` | Sequencial | Média térmica de sensores críticos, via `.stream()` |
| `consolidarMediaTermicaParalelo(List)` | Paralelo | Média térmica de sensores críticos, via `.parallelStream()` |
| `coletarEnderecosMacSuspeitosParalelo(List, double)` | Paralelo | Coleta endereços MAC de sensores com temperatura acima do corte |

```java
public double consolidarMediaTermicaSequencial(List<SinalSensor> sinais) {
    return sinais.stream()
            .filter(SinalSensor::isStatusCritico)
            .mapToDouble(SinalSensor::processarNormalizacao)
            .average()
            .orElse(0.0);
}

public double consolidarMediaTermicaParalelo(List<SinalSensor> sinais) {
    return sinais.parallelStream()
            .filter(SinalSensor::isStatusCritico)
            .mapToDouble(SinalSensor::processarNormalizacao)
            .average()
            .orElse(0.0);
}

public List<String> coletarEnderecosMacSuspeitosParalelo(List<SinalSensor> sinais, double tetoTemperatura) {
    return sinais.parallelStream()
            .filter(s -> s.processarNormalizacao() > tetoTemperatura)
            .map(s -> s.getMacAddress().toUpperCase())
            .distinct()
            .toList();
}
```

Note o uso extensivo de *Method References*: `SinalSensor::isStatusCritico` (Categoria 3 — método de instância de tipo arbitrário), `SinalSensor::processarNormalizacao` (Categoria 3), `String::toUpperCase` (Categoria 3).

### 4.3. Aplicação: `TelemetriaApp`

Arquivo: `solucoes/aula-18/src/br/edu/universidade/sistema/telemetria/TelemetriaApp.java`

```java
public class TelemetriaApp {
    public static void main(String[] args) {
        AuditoriaTelemetriaService service = new AuditoriaTelemetriaService();

        // Geração de 500.000 sinais IoT
        final int total = 500_000;
        List<SinalSensor> sinais = new ArrayList<>(total);
        for (int i = 0; i < total; i++) {
            boolean critico = (i % 3 == 0);
            double temperatura = 20.0 + (i % 40);
            sinais.add(new SinalSensor((long) i,
                    String.format("AA:BB:CC:%02X:%02X:%02X",
                            (i >> 16) & 0xFF, (i >> 8) & 0xFF, i & 0xFF),
                    temperatura, critico));
        }

        // Benchmark Sequencial vs. Paralelo
        long inicioSeq = System.currentTimeMillis();
        double mediaSeq = service.consolidarMediaTermicaSequencial(sinais);
        long tempoSeq = System.currentTimeMillis() - inicioSeq;

        long inicioPar = System.currentTimeMillis();
        double mediaPar = service.consolidarMediaTermicaParalelo(sinais);
        long tempoPar = System.currentTimeMillis() - inicioPar;

        System.out.printf("Média térmica (sequencial): %.6f | Tempo: %d ms%n", mediaSeq, tempoSeq);
        System.out.printf("Média térmica (paralela)  : %.6f | Tempo: %d ms%n", mediaPar, tempoPar);
        System.out.printf("Resultados equivalentes (tolerância 1e-9): %b%n",
                (Math.abs(mediaSeq - mediaPar) < 1e-9));

        double speedup = (double) tempoSeq / Math.max(tempoPar, 1);
        System.out.printf("Speedup obtido: %.2fx%n", speedup);

        // Coleta de MACs suspeitos
        List<String> macsSuspeitos = service.coletarEnderecosMacSuspeitosParalelo(sinais, 2000.0);
        System.out.printf("Quantidade de endereços MAC coletados: %d%n", macsSuspeitos.size());
    }
}
```

A verificação de equivalência dos resultados (`Math.abs(mediaSeq - mediaPar) < 1e-9`) demonstra que o processamento paralelo produz resultados idênticos ao sequencial, validando a correção da abordagem.

---

## 5. Perguntas de Revisão

1. Qual é a diferença entre um *Method Reference* e uma expressão lambda equivalente em termos de funcionalidade?
2. Por que o *Method Reference* é considerado açúcar sintático e não uma funcionalidade nova da linguagem?
3. Quais são as quatro categorias de *Method References* e qual o operador que as define?
4. Na Categoria 3 (Tipo Arbitrário), como a JVM identifica qual objeto será utilizado como alvo do método?
5. Qual a diferença entre `Comparator.comparing()` e `thenComparing()` na construção de ordenações multicritério?
6. Como o método `reversed()` modifica o comportamento de um comparador fluente?
7. Explique a diferença entre `.stream()` e `.parallelStream()` em termos de execução na JVM.
8. O que é o ForkJoinPool e qual seu papel na execução de `parallelStream()`?
9. Em quais situações o uso de `parallelStream()` pode ser prejudicial em vez de benéfico?
10. Por que o método `calcularImpostoComplexo()` em `CupomFiscal` é essencial para demonstrar o ganho de performance do paralelismo?

---

## 6. Resumo / Pontos-Chave

- **Method References** (`::`) são a forma mais concisa de representar comportamento em Java, funcionando como açúcar sintático para lambdas que apenas delegam chamadas a métodos existentes.
- Existem **4 categorias** de Method References: método estático, método de instância de objeto específico, método de instância de tipo arbitrário e referência a construtor.
- **`Comparator.comparing()`** inaugura a ordenação declarativa fluente, eliminando a necessidade de criar classes separadas para cada critério de ordenação.
- A composição de comparadores com **`thenComparing()`** e **`reversed()`** permite expressar ordenações multicritério complexas em poucas linhas de código legível.
- **`parallelStream()`** distribui o processamento entre núcleos de CPU via ForkJoinPool, oferecendo ganhos significativos de performance para operações custosas em grandes volumes de dados.
- A **verificação de equivalência** entre resultados sequenciais e paralelos é uma prática essencial para validar a correção do paralelismo.
- O módulo integrador de paralelismo fiscal demonstra que a diferença entre `.stream()` e `.parallelStream()` é mínima em sintaxe, mas potencialmente enorme em performance.
- O estudo de caso **TelemetriaApp** combina Method References e paralelismo para processar 500.000 sinais IoT, demonstrando a aplicação real dos conceitos em cenários industriais de grande escala.
- Esta aula encerra a disciplina de Programação Orientada a Objetos consolidando todos os conceitos ministrados: da POO básica até os recursos modernos de metaprogramação e expressividade do Java contemporâneo.
