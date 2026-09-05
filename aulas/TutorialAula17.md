# Tutorial de Java — Aula 17: Coleta Avançada com Collectors, Agrupamentos, Particionamentos e Reduções Customizadas

| | |
|---|---|
| **Disciplina** | Programação Orientada a Objetos |
| **Curso** | Análise e Desenvolvimento de Sistemas (3º Período) |
| **Tema** | A Classe Utilitária `java.util.stream.Collectors`, Agrupamentos Hierárquicos (`groupingBy`), Particionamentos Booleanos (`partitioningBy`), Operações de Junção (`joining`) e Sumarização Multidimensional |
| **Carga Horária** | 100 minutos (2 horas-aula) |
| **Professor Responsável** | Alexandre Neves Louzada |
| **Arquivo de Referência** | `TutorialAula17.md` |

## 1. Objetivos de Aprendizagem

- **Conceitual:** Compreender o papel da classe utilitária `Collectors` na finalização de pipelines analíticos da Streams API; diferenciar coletas simples para listas/conjuntos de transformações agregadas estruturais; assimilar o conceito de reduções multidimensionais e agrupamentos encadeados (*downstream collectors*).
- **Técnico:** Dominar as operações avançadas de coleta: `Collectors.groupingBy` (agrupamento com base em função classificadora), `Collectors.partitioningBy` (particionamento binário rápido baseado em `Predicate`), `Collectors.joining` (concatenação delimitada de strings), `Collectors.toMap` (transformação em mapas arbitrários), além de coletores compostos (`counting`, `summingDouble`, `mapping`).
- **Arquitetural:** Substituir blocos procedurais imperativos de laços aninhados (`for`/`if`/`else`) com mapas intermediários por pipelines declarativos de alto nível, reduzindo a complexidade ciclomática e eliminando estados mutáveis intermediários.
- **Prático:** Implementar um motor de auditoria contábil e gestão de folha corporativa capaz de particionar equipes entre faixas salariais, agrupar despesas por departamento, calcular médias orçamentárias e gerar sumários executivos formatados.

## 2. Fundamentação Teórica

### A Abstração da Interface Collector e a Classe Collectors

A operação terminal `.collect()` da Streams API representa uma das reduções mutáveis mais poderosas da plataforma Java. Em vez de produzir valores escalares individuais (como um único número retornado por `sum` ou `count`), o coletor orquestra a consolidação de fluxos contínuos de dados em estruturas complexas, como mapas agrupados, conjuntos restritivos ou strings concatenadas:

```plaintext
Stream<Funcionario> ──► .collect(Collectors.groupingBy(Funcionario::getDepartamento))
                                     │
                                     ▼
                     Map<String, List<Funcionario>>
             [ "TI" ]          ──► [ Mariana, Lucas ]
             [ "FINANCEIRO" ]  ──► [ Carlos, Renata ]
```

A classe `java.util.stream.Collectors` fornece métodos de fábrica (*factory methods*) estáticos prontos para os padrões de agregação mais demandados no desenvolvimento de software.

### Coletas Básicas vs. Agrupamentos Multidimensionais

**Coletas Lineares Básicas:**

- `Collectors.toList()` / `Collectors.toSet()`: Agrupa os elementos em coleções concretas do framework (`List` ou `Set`).
- `Collectors.toCollection(TreeSet::new)`: Permite especificar o construtor da implementação desejada via *Method Reference*.
- `Collectors.joining(delimitador, prefixo, sufixo)`: Une elementos do tipo texto em uma única `String` contínua sem exigir construtores manuais de `StringBuilder`.

**Agrupamento (groupingBy):**

Mapeia elementos com base em uma função classificadora (`Function<T, K>`), produzindo um `Map<K, List<T>>`:

> Stream<T> → groupingBy(f) → Map<Chave, List<Elementos>>

**Particionamento (partitioningBy):**

Caso especial e otimizado de agrupamento onde a chave de classificação é estritamente booleana (`Predicate<T>`), produzindo compulsoriamente um `Map<Boolean, List<T>>`:

- **Chave `true`:** Lista de elementos que satisfazem a regra de negócio.
- **Chave `false`:** Lista de elementos que não atendem à regra.

### Agrupamentos Encadeados (Downstream Collectors)

A verdadeira expressividade do `groupingBy` surge quando combinamos a função classificadora com um segundo coletor (*downstream collector*). Em vez de simplesmente agrupar as entidades originais em listas, o Java delega o processamento daquele subgrupo diretamente para outra função de agregação:

```plaintext
┌─────────────────────────────────────────────────────────────────────────────┐
│                 TAXONOMIA DE DOWNSTREAM COLLECTORS FREQUENTES               │
├──────────────────────────────────────┬──────────────────────────────────────┤
│ Assinatura do Coletor                │ Efeito no Agrupamento Final          │
├──────────────────────────────────────┼──────────────────────────────────────┤
│ groupingBy(f, counting())            │ Map<K, Long>: Conta itens do grupo   │
│ groupingBy(f, summingDouble(g))      │ Map<K, Double>: Soma valores no grupo│
│ groupingBy(f, averagingDouble(g))    │ Map<K, Double>: Calcula média grupo  │
│ groupingBy(f, mapping(g, toList()))  │ Map<K, List<R>>: Projeta e agrupa    │
└──────────────────────────────────────┴──────────────────────────────────────┘
```

Essa composição em cascata permite obter matrizes e relatórios analíticos em uma única linha, eliminando dezenas de linhas de código estruturado imperativo.

## 3. Estudo de Caso Integrado: Motor Analítico de Recursos Humanos e Folha

O projeto abaixo consolida agrupamentos departamentais, particionamento salarial, soma de centros de custo e formatação de relatórios tabulares utilizando os recursos de `Collectors`:

```java
package br.edu.universidade.sistema.analytics.dominio;

import java.util.Objects;

// 1. Entidade de Domínio representando o Profissional
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

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getDepartamento() { return departamento; }
    public String getCargo() { return cargo; }
    public double getSalarioMensal() { return salarioMensal; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Funcionario that = (Funcionario) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("[%d] %-15s | Depto: %-12s | Cargo: %-15s | R$ %8.2f",
                id, nome, departamento, cargo, salarioMensal);
    }
}
```

```java
package br.edu.universidade.sistema.analytics.service;

import br.edu.universidade.sistema.analytics.dominio.Funcionario;
import java.util.*;
import java.util.stream.Collectors;

// 2. Serviço de Auditoria Analítica utilizando Coletas Avançadas
public class RhAnalyticsService {

    // 1. Agrupamento Simples 1:N -> Departamento mapeando para Lista de Funcionários
    public Map<String, List<Funcionario>> agruparPorDepartamento(List<Funcionario> funcionarios) {
        return funcionarios.stream()
                .collect(Collectors.groupingBy(Funcionario::getDepartamento));
    }

    // 2. Agrupamento com Downstream de Contagem -> Departamento mapeando para Quantidade
    public Map<String, Long> contarColaboradoresPorDepartamento(List<Funcionario> funcionarios) {
        return funcionarios.stream()
                .collect(Collectors.groupingBy(
                        Funcionario::getDepartamento,
                        Collectors.counting()
                ));
    }

    // 3. Agrupamento com Downstream Aritmético -> Departamento mapeando para Custo Total
    public Map<String, Double> somarFolhaSalarialPorDepartamento(List<Funcionario> funcionarios) {
        return funcionarios.stream()
                .collect(Collectors.groupingBy(
                        Funcionario::getDepartamento,
                        Collectors.summingDouble(Funcionario::getSalarioMensal)
                ));
    }

    // 4. Agrupamento com Transformação (Mapping) -> Departamento mapeando apenas para os Nomes
    public Map<String, List<String>> mapearApenasNomesPorDepartamento(List<Funcionario> funcionarios) {
        return funcionarios.stream()
                .collect(Collectors.groupingBy(
                        Funcionario::getDepartamento,
                        Collectors.mapping(Funcionario::getNome, Collectors.toList())
                ));
    }

    // 5. Particionamento Booleano -> Divide a folha entre quem ganha acima de R$ 7.000 e quem não ganha
    public Map<Boolean, List<Funcionario>> particionarPorTetoSalarial(List<Funcionario> funcionarios, double teto) {
        return funcionarios.stream()
                .collect(Collectors.partitioningBy(f -> f.getSalarioMensal() >= teto));
    }

    // 6. Concatenação de Nomes via joining -> Emite lista corrida para auditoria
    public String emitirListagemNomesFormatada(List<Funcionario> funcionarios) {
        return funcionarios.stream()
                .map(Funcionario::getNome)
                .collect(Collectors.joining(", ", "EQUIPE: [", "]"));
    }
}
```

```java
package br.edu.universidade.sistema.analytics;

import br.edu.universidade.sistema.analytics.dominio.Funcionario;
import br.edu.universidade.sistema.analytics.service.RhAnalyticsService;
import java.util.List;
import java.util.Map;

// 3. Aplicação Executável demonstrando os relatórios de saída
public class AnalyticsApp {
    public static void main(String[] args) {
        List<Funcionario> quadroColaboradores = List.of(
                new Funcionario(101L, "Mariana Silva", "TECNOLOGIA", "ARQUITETA", 9500.00),
                new Funcionario(102L, "Lucas Mendes", "TECNOLOGIA", "DESENVOLVEDOR", 4800.00),
                new Funcionario(103L, "Carlos Prado", "FINANCEIRO", "ANALISTA", 6200.00),
                new Funcionario(104L, "Beatriz Costa", "RH", "COORDENADORA", 7100.00),
                new Funcionario(105L, "Renata Lima", "FINANCEIRO", "DIRETORA", 12500.00),
                new Funcionario(106L, "Thiago Rocha", "TECNOLOGIA", "DESENVOLVEDOR", 5200.00)
        );

        RhAnalyticsService service = new RhAnalyticsService();

        System.out.println("--- 1. Quantidade de Colaboradores por Departamento (counting) ---");
        Map<String, Long> contagem = service.contarColaboradoresPorDepartamento(quadroColaboradores);
        contagem.forEach((depto, total) -> System.out.printf("Depto: %-12s | Total: %d colaboradores%n", depto, total));

        System.out.println("\n--- 2. Custo Total de Folha Salarial por Departamento (summingDouble) ---");
        Map<String, Double> custoPorDepto = service.somarFolhaSalarialPorDepartamento(quadroColaboradores);
        custoPorDepto.forEach((depto, soma) -> System.out.printf("Depto: %-12s | Folha: R$ %10.2f%n", depto, soma));

        System.out.println("\n--- 3. Mapeamento Direto de Nomes por Setor (mapping) ---");
        Map<String, List<String>> nomesPorDepto = service.mapearApenasNomesPorDepartamento(quadroColaboradores);
        nomesPorDepto.forEach((depto, nomes) -> System.out.printf("Depto: %-12s | Membros: %s%n", depto, nomes));

        System.out.println("\n--- 4. Particionamento Salarial Binário (>= R$ 7.000) ---");
        Map<Boolean, List<Funcionario>> particionamento = service.particionarPorTetoSalarial(quadroColaboradores, 7000.00);
        System.out.println(">> Colaboradores com Salário >= R$ 7.000 (Teto Sênior/Liderança):");
        particionamento.get(true).forEach(System.out::println);
        System.out.println(">> Demais Colaboradores:");
        particionamento.get(false).forEach(System.out::println);

        System.out.println("\n--- 5. Concatenação de Nomes via joining ---");
        String relatorioTexto = service.emitirListagemNomesFormatada(quadroColaboradores);
        System.out.println(relatorioTexto);
    }
}
```

## 4. Diagnóstico de Erros Comuns e Armadilhas

### Armadilha 1: Conflito de Chaves Duplicadas em Collectors.toMap()

**Código Problemático:**

```java
// Dois funcionários pertencem ao mesmo departamento:
Map<String, Funcionario> mapa = lista.stream()
        .collect(Collectors.toMap(Funcionario::getDepartamento, f -> f));
```

- **Diagnóstico da JVM:** `java.lang.IllegalStateException: Duplicate key TECNOLOGIA` lançada em tempo de execução.
- **Causa & Correção:** A sobrecarga básica de `toMap()` não sabe o que fazer quando duas chaves colidem. Forneça uma função de resolução de conflito (*merge function*) no terceiro parâmetro: `(f1, f2) -> f1` (mantém o primeiro) ou utilize `groupingBy` para permitir múltiplos itens por chave.

### Armadilha 2: Tentar Realizar Mutações na Lista Retornada por .toList()

**Código Problemático:**

```java
List<String> nomes = lista.stream().map(Funcionario::getNome).toList();
nomes.add("Novo Nome"); // ERRO DE EXECUÇÃO!
```

- **Diagnóstico da JVM:** `java.lang.UnsupportedOperationException` lançada em tempo de execução.
- **Causa & Correção:** O método nativo `.toList()` (Java 16+) produz uma lista estruturalmente imutável. Caso precise de uma lista mutável que aceite adições posteriores, utilize a forma clássica com o coletor configurado: `.collect(Collectors.toCollection(ArrayList::new))`.

### Armadilha 3: Ineficiência ao Usar filter Duplo em Vez de partitioningBy

**Código Problemático:**

```java
// Executa duas varreduras completas no mesmo conjunto de dados:
List<Funcionario> aprovados = lista.stream().filter(f -> f.getSalario() >= 5000).toList();
List<Funcionario> reprovados = lista.stream().filter(f -> f.getSalario() < 5000).toList();
```

- **Diagnóstico Técnico:** O pipeline percorre todos os elementos duas vezes (2n).
- **Correção:** Utilize `partitioningBy`, que divide os dados em uma única passada de complexidade linear (n), populando simultaneamente as chaves `true` e `false` do mapa.

## 5. Roteiro Prático de Depuração: Inspecionando o Coletor de Mapa na IDE

Para auditar o agrupamento multidimensional passo a passo na sua IDE (IntelliJ IDEA, Eclipse ou VS Code):

1. No código `AnalyticsApp`, coloque um ponto de interrupção (*breakpoint*) logo após a chamada `Map<String, Double> custoPorDepto = service.somarFolhaSalarialPorDepartamento(...)`.
2. Inicie a execução em modo de depuração (*Debug*).
3. Na janela de variáveis (*Variables*):
   - Expanda a referência `custoPorDepto`: observe que as chaves mapeadas correspondem exatamente às constantes `"TECNOLOGIA"`, `"FINANCEIRO"` e `"RH"`.
   - Expanda o valor de `"TECNOLOGIA"`: note que o somatório numérico exibido (R$ 19.500,00) representa rigorosamente a agregação realizada pelo coletor `summingDouble` sem que nenhuma lista intermediária tenha permanecido ocupando espaço no Heap.

## 6. Exercício de Fixação Prática: Gestão de Estoque e Precificação por Categoria

Implemente um motor analítico mercantil para lojas de varejo aplicando coletores avançados:

1. **Construa a Classe `ItemMercadoria`:**
   - Atributos privados: `sku` (`String`), `nome` (`String`), `setor` (`String` — `"ALIMENTOS"`, `"LIMPEZA"`, `"BEBIDAS"`), `quantidade` (`int`) e `precoUnitario` (`double`).
   - Construtor parametrizado completo com validação de dados.
   - Métodos acessores (*getters*) e método descritivo `toString()` formatando o preço com `%.2f`.

2. **Construa o Serviço `AuditoriaMercadoriasService`:**
   - **Método `Map<String, Double> calcularValorTotalImobilizadoPorSetor(List<ItemMercadoria> itens)`:**
     - Utiliza `groupingBy` agrupando pelo setor do produto.
     - Aplica o coletor downstream `summingDouble` multiplicando `quantidade * precoUnitario` para obter o capital total preso por categoria de mercado.
   - **Método `Map<Boolean, List<ItemMercadoria>> particionarPorRupturaEstoque(List<ItemMercadoria> itens)`:**
     - Particiona os itens via `partitioningBy` onde a condição booleana avalia se a `quantidade <= 5` (alerta de ruptura de estoque para o setor de compras).
   - **Método `String emitirCatalogoSetorialCsv(List<ItemMercadoria> itens, String setorAlvo)`:**
     - Filtra apenas os produtos do setor informado.
     - Extrai o nome dos produtos em letras maiúsculas.
     - Utiliza `Collectors.joining("; ", "SETOR " + setorAlvo + ": [", "]")` para emitir uma linha única formatada.

3. **Construa a Classe Executável `VarejoAnalyticsApp`:**
   - Instancie seis itens distribuídos entre os três setores, contendo produtos com estoque abundante e produtos em ruptura crítica (<= 5 unidades).
   - Execute os métodos analíticos do serviço exibindo os mapas resultantes no console.
   - Comprove que os produtos em alerta foram particionados na chave `true` e que os relatórios por setor consolidaram a contabilidade corretamente.