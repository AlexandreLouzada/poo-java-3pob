# Tutorial de Java — Aula 16: Reduções Numéricas, Streams Primitivas e a Classe java.util.Optional

| | |
|---|---|
| **Disciplina** | Programação Orientada a Objetos |
| **Curso** | Análise e Desenvolvimento de Sistemas (3º Período) |
| **Tema** | Reduções Numéricas (reduce, sum, average, min, max), Streams Primitivas de Alta Performance (IntStream, DoubleStream, LongStream) e a API Optional<T> |
| **Carga Horária** | 100 minutos (2 horas-aula) |
| **Professor Responsável** | Alexandre Neves Louzada |
| **Arquivo de Referência** | `TutorialAula16.md` |

## 1. Objetivos de Aprendizagem

- **Conceitual:** Compreender o custo de desempenho associado ao empacotamento e desempacotamento de tipos numéricos (*Autoboxing* e *Unboxing*) em pipelines de larga escala; dominar o conceito de reduções terminais agregadas; assimilar a semântica da classe container `Optional<T>` como mecanismo para modelar a ausência de valor sem recorrer a referências nulas (`null`).
- **Técnico:** Converter coleções de objetos para fluxos primitivos diretos utilizando `mapToInt`, `mapToDouble` e `mapToLong`; aplicar métodos de agregação nativos (`sum`, `average`, `min`, `max`, `count`) e a operação genérica de dobra/acumulação (`reduce`); manipular instâncias de `Optional` utilizando métodos modernos da API (`isPresent`, `isEmpty`, `ifPresent`, `orElse`, `orElseGet`, `orElseThrow`).
- **Arquitetural:** Prevenir falhas críticas de `NullPointerException` (o erro de um bilhão de dólares de Tony Hoare) através de retornos explícitos com `Optional` em contratos de serviços e repositórios; otimizar a pegada de memória da JVM evitando alocações supérfluas de objetos wrappers (`Double`, `Integer`) no Heap durante processamentos analíticos volumosos.
- **Prático:** Desenvolver um motor de análise financeira e auditoria contábil de contas corporativas, calculando volume total transacionado, ticket médio de faturamento, dispersão e busca resiliente da maior transação registrada.

## 2. Fundamentação Teórica

### O Gargalo do Autoboxing em Streams de Objetos

Ao processar números utilizando a abstração genérica `Stream<Double>` ou `Stream<Integer>`, a JVM é forçada a manipular objetos wrappers instanciados no Heap, e não números binários primitivos de 32 ou 64 bits na Stack:

> Valor Primitivo (Stack: 8 bytes) → Autoboxing → Objeto Wrapper (Heap: 24 bytes + ponteiro) → Unboxing → Soma Aritmética

Em coleções com centenas de milhares de itens, esse ciclo gera:

- **Sobrecarga de Memória no Heap:** Cada wrapper exige cabeçalho de objeto (*object header*), alinhamento de memória e metadados.
- **Pressão no Garbage Collector (GC):** Milhões de instâncias efêmeras descartadas forçam pausas no coletor de lixo (*Stop-the-World pauses*).
- **Falta de Localidade de Cache:** Ponteiros espalhados na memória dinâmica reduzem drasticamente a eficiência do cache L1/L2 do processador.

### Streams Primitivas Especializadas (IntStream, DoubleStream, LongStream)

Para contornar o gargalo de desempenho, a API disponibiliza três especializações primitivas que operam diretamente sobre blocos numéricos puros, sem alocação intermediária de objetos no Heap:

```plaintext
Stream<Transacao> ──► .mapToDouble(Transacao::getValor) ──► DoubleStream (Primitivos Puros)
                                                                 │
                                     ┌───────────────────────────┴───────────────────────────┐
                                     ▼                           ▼                           ▼
                                  .sum()                    .average()                    .max()
                                (Retorna double)      (Retorna OptionalDouble)     (Retorna OptionalDouble)
```

As Streams primitivas oferecem operações aritméticas pré-compiladas que não existem na interface `Stream<T>` genérica:

- `sum()`: Retorna diretamente a somatória numérica (`double`, `int` ou `long`).
- `average()`: Calcula a média aritmética, retornando um container primitivo `OptionalDouble`.
- `summaryStatistics()`: Percorre o fluxo em uma única passada e gera um relatório consolidado com contagem, soma, média, menor e maior valor simultaneamente.

### A Operação Geral de Redução (reduce)

A redução é um processo de agregação que transforma uma sequência de múltiplos elementos em um único valor final, aplicando repetidamente um operador de acumulação binário associativo:

> Resultado Final = (...(((ValorIdentidade op elemento₁) op elemento₂) op elemento₃)...)

Existem duas variações fundamentais de `reduce`:

- **Com Valor de Identidade:** Retorna diretamente o valor primitivo/objeto acumulado:
  ```java
  double total = valores.stream().reduce(0.0, (acumulador, item) -> acumulador + item);
  ```
- **Sem Valor de Identidade:** Como a Stream pode estar vazia e não há valor inicial garantido, o retorno é empacotado obrigatoriamente dentro de um `Optional<T>`.

### A Classe Container java.util.Optional<T>

Tradicionalmente, quando um método de busca não encontrava um registro, retornava `null`. Esse padrão gerava códigos frágeis repletos de verificações manuais de `if (resultado != null)`, cuja omissão levava inevitavelmente ao clássico `NullPointerException`:

```plaintext
                          ┌────────────────────────┐
                          │   Optional<Cliente>    │
                          └───────────┬────────────┘
                                      │
              ┌───────────────────────┴───────────────────────┐
              ▼                                               ▼
     [ Optional.of(cliente) ]                       [ Optional.empty() ]
       (Valor Presente e Válido)                      (Ausência Segura de Valor)
```

A classe `Optional<T>` atua como uma caixa protetora que pode ou não conter um valor:

- `Optional.of(valor)`: Cria um container com um valor que comprovadamente não é nulo (lança exceção caso receba `null`).
- `Optional.ofNullable(valor)`: Cria um container seguro que se torna `Optional.empty()` se o argumento for `null`.
- `Optional.empty()`: Representa explicitamente a ausência controlada de resultado.

**Métodos de Consumo Seguro (Evitando o Antipadrão `.get()` Direto):**

- `isPresent()` e `isEmpty()`: Métodos de consulta booleana de estado.
- `ifPresent(Consumer<T>)`: Executa uma ação caso o valor esteja presente, sem exigir condicionais explícitas.
- `orElse(valorPadrao)`: Retorna o valor interno ou uma alternativa padrão caso vazio.
- `orElseThrow(Supplier<Exception>)`: Retorna o valor ou lança uma exceção de domínio configurada caso ausente.

## 3. Estudo de Caso Integrado: Motor de Auditoria e Fechamento Financeiro

O projeto abaixo implementa um serviço analítico de fechamento contábil, combinando conversões primitivas com `DoubleStream`, agregações estatísticas e consultas defensivas com `Optional`:

```java
package br.edu.universidade.sistema.financeiro.dominio;

import java.time.LocalDate;
import java.util.Objects;

// 1. Entidade de Domínio representando um Lançamento Contábil
public class LancamentoFinanceiro {
    private final Long id;
    private final String descricao;
    private final String centroCusto;
    private final double valor;
    private final LocalDate dataLancamento;

    public LancamentoFinanceiro(Long id, String descricao, String centroCusto, double valor, LocalDate data) {
        if (id == null || valor <= 0.0) {
            throw new IllegalArgumentException("Parâmetros do lançamento inválidos.");
        }
        this.id = id;
        this.descricao = descricao;
        this.centroCusto = centroCusto.toUpperCase();
        this.valor = valor;
        this.dataLancamento = data;
    }

    public Long getId() { return id; }
    public String getDescricao() { return descricao; }
    public String getCentroCusto() { return centroCusto; }
    public double getValor() { return valor; }
    public LocalDate getDataLancamento() { return dataLancamento; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LancamentoFinanceiro that = (LancamentoFinanceiro) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("[#%04d] %-20s | C. Custo: %-12s | Valor: R$ %10.2f | Data: %s",
                id, descricao, centroCusto, valor, dataLancamento);
    }
}
```

```java
package br.edu.universidade.sistema.financeiro.service;

import br.edu.universidade.sistema.financeiro.dominio.LancamentoFinanceiro;
import java.util.*;

// 2. Serviço Contábil operando sobre Streams Primitivas e Optional
public class FechamentoContabilService {

    // 1. Totalização via DoubleStream nativo (elimina Autoboxing e otimiza memória)
    public double calcularVolumeTotalPorCentroCusto(List<LancamentoFinanceiro> lancamentos, String centroCusto) {
        return lancamentos.stream()
                .filter(l -> l.getCentroCusto().equalsIgnoreCase(centroCusto))
                .mapToDouble(LancamentoFinanceiro::getValor) // Conversão para DoubleStream primitivo
                .sum(); // Redução nativa em nível de hardware
    }

    // 2. Média aritmética retornando OptionalDouble defensivo
    public OptionalDouble calcularTicketMedio(List<LancamentoFinanceiro> lancamentos) {
        return lancamentos.stream()
                .mapToDouble(LancamentoFinanceiro::getValor)
                .average(); // Retorna OptionalDouble vazio caso a lista esteja vazia
    }

    // 3. Localização do maior lançamento individual registrado
    public Optional<LancamentoFinanceiro> buscarMaiorLancamento(List<LancamentoFinanceiro> lancamentos) {
        return lancamentos.stream()
                .max(Comparator.comparingDouble(LancamentoFinanceiro::getValor));
    }

    // 4. Busca pontual de lançamento por ID sem risco de retorno nulo
    public Optional<LancamentoFinanceiro> buscarPorId(List<LancamentoFinanceiro> lancamentos, Long id) {
        return lancamentos.stream()
                .filter(l -> l.getId().equals(id))
                .findFirst(); // Retorna o primeiro elemento encontrado ou Optional.empty()
    }

    // 5. Relatório estatístico consolidado gerado em única passada
    public DoubleSummaryStatistics extrairMetricasGlobais(List<LancamentoFinanceiro> lancamentos) {
        return lancamentos.stream()
                .mapToDouble(LancamentoFinanceiro::getValor)
                .summaryStatistics();
    }
}
```

```java
package br.edu.universidade.sistema.financeiro;

import br.edu.universidade.sistema.financeiro.dominio.LancamentoFinanceiro;
import br.edu.universidade.sistema.financeiro.service.FechamentoContabilService;
import java.time.LocalDate;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.OptionalDouble;

// 3. Aplicação Executável demonstrando cálculos e o consumo defensivo do Optional
public class ContabilidadeApp {
    public static void main(String[] args) {
        List<LancamentoFinanceiro> baseLancamentos = List.of(
                new LancamentoFinanceiro(1001L, "Servidores em Nuvem", "TECNOLOGIA", 4200.50, LocalDate.now()),
                new LancamentoFinanceiro(1002L, "Licenças de Software", "TECNOLOGIA", 1850.00, LocalDate.now()),
                new LancamentoFinanceiro(1003L, "Campanha de Marketing", "MARKETING", 8500.00, LocalDate.now()),
                new LancamentoFinanceiro(1004L, "Material de Escritório", "ADMINISTRATIVO", 340.20, LocalDate.now()),
                new LancamentoFinanceiro(1005L, "Manutenção Predial", "ADMINISTRATIVO", 1200.00, LocalDate.now())
        );

        FechamentoContabilService service = new FechamentoContabilService();

        System.out.println("--- 1. Volume Total por Centro de Custo via DoubleStream ---");
        double totalTI = service.calcularVolumeTotalPorCentroCusto(baseLancamentos, "TECNOLOGIA");
        System.out.printf("Total Gasto em TECNOLOGIA: R$ %.2f%n", totalTI);

        System.out.println("\n--- 2. Cálculo Seguro de Média via OptionalDouble ---");
        OptionalDouble ticketMedio = service.calcularTicketMedio(baseLancamentos);
        if (ticketMedio.isPresent()) {
            System.out.printf("Ticket Médio Geral: R$ %.2f%n", ticketMedio.getAsDouble());
        } else {
            System.out.println("Não foi possível calcular a média: base sem registros.");
        }

        System.out.println("\n--- 3. Consumo Funcional de Optional sem IF com ifPresent ---");
        service.buscarMaiorLancamento(baseLancamentos).ifPresent(l ->
                System.out.println("Maior Gasto Detectado: " + l));

        System.out.println("\n--- 4. Busca Pontual com Fallback e Lançamento de Exceção ---");
        // Caso A: Lançamento Encontrado
        LancamentoFinanceiro l1 = service.buscarPorId(baseLancamentos, 1001L)
                .orElseThrow(() -> new NoSuchElementException("Lançamento #1001 não encontrado!"));
        System.out.println("Sucesso na Consulta: " + l1.getDescricao());

        // Caso B: Lançamento Inexistente tratado via orElse com valor sentinela padrão
        LancamentoFinanceiro lPadrao = service.buscarPorId(baseLancamentos, 9999L)
                .orElse(new LancamentoFinanceiro(0L, "Lancamento Padrão de Fallback", "GERAL", 1.0, LocalDate.now()));
        System.out.println("Resultado da Consulta Inexistente (Fallback): " + lPadrao.getDescricao());

        System.out.println("\n--- 5. Métricas Consolidadas (DoubleSummaryStatistics) ---");
        DoubleSummaryStatistics stats = service.extrairMetricasGlobais(baseLancamentos);
        System.out.printf("Registros Analisados : %d%n", stats.getCount());
        System.out.printf("Volume Consolidado   : R$ %.2f%n", stats.getSum());
        System.out.printf("Menor Lançamento     : R$ %.2f%n", stats.getMin());
        System.out.printf("Maior Lançamento     : R$ %.2f%n", stats.getMax());
        System.out.printf("Média de Desembolso  : R$ %.2f%n", stats.getAverage());
    }
}
```

## 4. Diagnóstico de Erros Comuns e Armadilhas

### Armadilha 1: Invocar .get() em um Optional sem Validação Prévia

**Código Problemático:**

```java
Optional<LancamentoFinanceiro> lancamento = service.buscarPorId(lista, 9999L);
LancamentoFinanceiro item = lancamento.get(); // O OPTIONAL ESTÁ VAZIO!
```

- **Diagnóstico da JVM:** `java.util.NoSuchElementException: No value present` lançada em tempo de execução.
- **Causa & Correção:** Chamar `.get()` diretamente anula o propósito do `Optional`, apenas substituindo `NullPointerException` por `NoSuchElementException`. Utilize as alternativas seguras: `.orElse(...)`, `.orElseGet(...)` ou `.orElseThrow(...)`.

### Armadilha 2: Utilizar Optional como Tipo de Atributo de Entidade ou Parâmetro

**Código Problemático:**

```java
public class Cliente {
    private Optional<String> telefone; // MÁ PRÁTICA ARQUITETURAL!
    public void setTelefone(Optional<String> tel) { ... }
}
```

- **Diagnóstico Técnico:** `Optional` não é serializável (`java.io.Serializable`), o que quebra frameworks de persistência (JPA/Hibernate), caches corporativos (Redis) e integrações remotas. Além disso, empacota atributos simples em ponteiros extras no Heap.
- **Diretriz Arquitetural:** `Optional` foi projetado exclusivamente como tipo de retorno de métodos de consulta e busca. Em atributos de entidades, use valores primitivos ou objetos convencionais e trate a ausência nos métodos acessores (*getters*).

### Armadilha 3: Ineficiência de Desempenho usando Stream<Double> em Vez de DoubleStream

**Código Problemático:**

```java
// Converte milhões de números gerando caixas Double no Heap:
double soma = lista.stream()
        .map(Transacao::getValor) // Retorna Stream<Double> (Wrappers pesados)
        .reduce(0.0, Double::sum); // Unboxing contínuo e repetitivo
```

- **Causa & Correção:** A cada soma, a JVM extrai o valor primitivo do wrapper e instancia uma nova referência para o acumulador. Substitua imediatamente pela versão primitiva: `.mapToDouble(Transacao::getValor).sum()`, que mantém os números em registradores rápidos do processador.

## 5. Roteiro Prático de Depuração: Inspecionando o Estado do Optional na IDE

Para auditar o encapsulamento do valor dentro do container `Optional` no depurador da sua IDE (IntelliJ IDEA, Eclipse ou VS Code):

1. No código `ContabilidadeApp`, coloque um ponto de interrupção (*breakpoint*) na linha da consulta com fallback: `LancamentoFinanceiro lPadrao = service.buscarPorId(...)`.
2. Inicie o programa em modo de depuração (*Debug*).
3. Na janela de variáveis (*Variables*):
   - Observe a instância retornada por `buscarPorId`: veja que o campo interno `value` aponta para `null`.
   - A IDE identifica visualmente o objeto como `Optional.empty`.
4. Avance a execução com o comando *Step Into* (F7):
   - Acompanhe a execução entrando dentro do método `orElse(...)`.
   - Observe a avaliação da expressão condicional: como `value == null`, a JVM desvia deterministicamente para a instância de fallback informada, sem disparar interrupções de fluxo na Stack.

## 6. Exercício de Fixação Prática: Motor de Métricas de Vendas de Representantes Comerciais

Implemente um componente analítico de desempenho de equipe comercial combinando reduções numéricas e retornos com `Optional`:

1. **Construa a Classe `VendaRepresentante`:**
   - Atributos privados: `idVenda` (`Long`), `nomeRepresentante` (`String`), `regiao` (`String` — `"SUL"`, `"SUDESTE"`, `"NORTE"`), `valorVenda` (`double`) e `comissaoPaga` (`double`).
   - Construtor parametrizado completo rejeitando comissões negativas ou valores de venda zerados via `IllegalArgumentException`.
   - Métodos acessores (*getters*) e método descritivo `toString()` formatando valores monetários com `%.2f`.

2. **Construa o Serviço `AuditoriaVendasService`:**
   - **Método `double calcularFaturamentoTotalPorRegiao(List<VendaRepresentante> vendas, String regiao)`:**
     - Utiliza Stream para filtrar a região informada.
     - Converte para `DoubleStream` primitivo e retorna a soma total das vendas.
   - **Método `OptionalDouble calcularMediaComissoesPagas(List<VendaRepresentante> vendas)`:**
     - Utiliza `mapToDouble(VendaRepresentante::getComissaoPaga)` e retorna a média com `average()`.
   - **Método `Optional<VendaRepresentante> buscarMaiorVendaPorRepresentante(List<VendaRepresentante> vendas, String nome)`:**
     - Filtra pelo nome do representante comercial.
     - Utiliza `.max()` com base no `valorVenda` e retorna o `Optional` com o registro ou vazio se o vendedor não possuir vendas.
   - **Método `VendaRepresentante obterVendaComGarantia(List<VendaRepresentante> vendas, Long id)`:**
     - Busca a venda por ID.
     - Se não existir, lança uma exceção `NoSuchElementException("Venda não localizada para auditoria: ID " + id)`.

3. **Construa a Classe Executável `VendasAnalyticsApp`:**
   - Crie uma base com quatro vendas de regiões diferentes.
   - Execute o método de faturamento regional comprovando a somatória correta em tela.
   - Consulte a maior venda de um vendedor existente utilizando `.ifPresent(System.out::println)`.
   - Tente consultar a maior venda de um vendedor que não realizou vendas, tratando o retorno com `.orElseGet(...)` para emitir aviso amigável de ausência de registros sem interromper o sistema.