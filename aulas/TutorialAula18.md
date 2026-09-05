# Tutorial de Java — Aula 18: Streams Paralelas (parallelStream), Concorrência com Fork/Join e Processamento em Larga Escala

| | |
|---|---|
| **Disciplina** | Programação Orientada a Objetos |
| **Curso** | Análise e Desenvolvimento de Sistemas (3º Período) |
| **Tema** | Streams Paralelas (`parallelStream`), o Framework Fork/Join, Divisão de Tarefas com `Spliterator`, Cuidados com Concorrência (*Thread-Safety*) e Avaliação de Desempenho (Lei de Amdahl) |
| **Carga Horária** | 100 minutos (2 horas-aula) |
| **Professor Responsável** | Alexandre Neves Louzada |
| **Arquivo de Referência** | `TutorialAula18.md` |

## 1. Objetivos de Aprendizagem

- **Conceitual:** Compreender os fundamentos do paralelismo de dados em processadores multi-core modernos; diferenciar concorrência (múltiplas tarefas progredindo simultaneamente) de paralelismo real (múltiplas tarefas executando no mesmo ciclo de clock em núcleos distintos); assimilar a Lei de Amdahl e reconhecer quando o paralelismo degrada em vez de acelerar a execução.
- **Técnico:** Converter pipelines sequenciais em fluxos paralelos utilizando `.parallelStream()` e `.parallel()`; inspecionar e configurar a piscina comum do framework (`ForkJoinPool.commonPool()`); manipular o particionamento de fontes de dados via interface `Spliterator`.
- **Arquitetural:** Prevenir condições de corrida (*race conditions*) e corrupção de estado eliminando efeitos colaterais mutáveis (*side-effects*) dentro de lambdas paralelas; escolher estruturas de dados com divisão balanceada (O(1)) em detrimento de fontes mal condicionadas para paralelismo.
- **Prático:** Implementar um motor analítico de processamento em lote de milhões de transações fiscais e contábeis, comparando métricas de tempo de resposta (throughput e latência) entre abordagens imperativas, streams sequenciais e streams paralelas.

## 2. Fundamentação Teórica

### Paralelismo de Dados vs. Concorrência de Tarefas

No desenvolvimento corporativo contemporâneo, servidores contam com dezenas de núcleos físicos de CPU. Enquanto o modelo concorrente tradicional gerencia threads manuais (`Thread`, `Runnable`, bloqueios com `synchronized`) para lidar com múltiplos fluxos independentes, o **Paralelismo de Dados** visa acelerar uma única operação massiva dividindo o conjunto de dados entre todos os núcleos disponíveis:

```plaintext
Pipeline Sequencial:
[ Núcleo 1 ] ──► Item 1 ──► Item 2 ──► Item 3 ──► Item 4 ──► Item 5 ──► Item 6 ──► [ Fim ]

Pipeline Paralelo (Fork/Join):
[ Núcleo 1 ] ──► Item 1 ──► Item 2 ──┐
[ Núcleo 2 ] ──► Item 3 ──► Item 4 ──┼──► [ Junção / Redução Final ]
[ Núcleo 3 ] ──► Item 5 ──► Item 6 ──┘
```

A Streams API abstrai toda a complexidade de criação, sincronização e encerramento de threads por meio do método `.parallelStream()` presente em `java.util.Collection`.

### A Arquitetura Subjacente: O Framework Fork/Join e o Work-Stealing

Por baixo da abstração, streams paralelas utilizam o Framework Fork/Join (introduzido no Java 7) e sua piscina compartilhada global `ForkJoinPool.commonPool()`:

1. **Fase de Divisão (Fork):** A fonte de dados é decomposta recursivamente em partes menores utilizando uma implementação da interface `Spliterator` (*Splittable Iterator*).
2. **Execução Concorrente:** Cada submassa de dados é atribuída a uma thread da piscina de workers (por padrão, a JVM aloca uma quantidade de threads igual ao número de processadores lógicos disponíveis menos um: `Runtime.getRuntime().availableProcessors() − 1`).
3. **Algoritmo de Roubo de Trabalho (Work-Stealing):** Se um núcleo conclui seu lote mais rápido, ele consulta a fila de tarefas de outros núcleos ocupados e "rouba" trabalho pendente pela cauda da fila *deque*, garantindo equilíbrio contínuo de carga de hardware.
4. **Fase de União (Join):** As operações terminais de redução (como somas ou coleções intermediárias) são recombinadas recursivamente até a entrega do resultado final.

### O Modelo Mental "NQ": Quando Vale a Pena Paralelizar?

O arquiteto de linguagem Brian Goetz propôs o modelo empírico **N×Q** para avaliar a viabilidade de uma stream paralela:

> Métrica de Viabilidade = N × Q

- **N:** Número total de elementos a serem processados.
- **Q:** Custo computacional gasto por elemento (trabalho por item).

Se o produto **N×Q** for relativamente baixo (por exemplo, listas com menos de 10.000 elementos executando simples checagens aritméticas), o pipeline paralelo será consideravelmente mais lento que o sequencial. Isso decorre do *overhead* inevitável de:

- Decomposição da fonte em subárvores de memória;
- Trocas de contexto (*context switching*) no sistema operacional;
- Coordenação e sincronização do *Join* final.

### Decomposição de Fontes: Boas vs. Más Estruturas

A eficiência do `Spliterator` depende diretamente da estrutura física da coleção de origem:

| Estrutura de Origem | Qualidade de Divisão | Desempenho Paralelo | Motivo Arquitetural |
|---|---|---|---|
| `ArrayList` | Excelente (O(1)) | Altíssimo | Baseada em array contíguo: divisão exata pelo meio via índices sem custo. |
| `IntStream.range()` | Excelente (O(1)) | Altíssimo | Sequência matemática pura: particionamento numérico instantâneo. |
| `HashSet` / `TreeSet` | Boa (O(log n)) | Moderado | Decomposição viável, mas com custo ligeiramente maior de travessia. |
| `LinkedList` | Péssima (O(n)) | Muito Ruim | Para achar o meio da lista, é preciso percorrer ponteiro a ponteiro linearmente. |
| `Stream.iterate()` | Péssima (O(n)) | Muito Ruim | Cada elemento depende do anterior: impossível calcular em paralelo sem prévia. |

### O Perigo Fatal: Efeitos Colaterais e Falta de Thread-Safety

A regra número um de streams paralelas é: as funções passadas como argumento (predicados, mapeadores, consumidores) devem ser estritamente livres de efeitos colaterais mutáveis (*Stateless and Non-Interfering*):

```java
// ANTIPADRÃO CATASTRÓFICO: Corrupção de memória por corrida de threads!
List<Integer> listaNaoSincronizada = new ArrayList<>();

IntStream.range(0, 100_000)
        .parallel()
        .forEach(listaNaoSincronizada::add); // Corrida crítica: perda de dados e exceções!
```

Como o `ArrayList` não é sincronizado para concorrência, múltiplas threads tentarão redimensionar o array interno simultaneamente no Heap, resultando em:

- Índices nulos indesejados;
- Perda de metade dos elementos inseridos;
- Disparo de `ArrayIndexOutOfBoundsException` em tempo de execução.

**Solução Correta:** Utilize operações terminais de redução ou coletas nativas com coletores concorrentes: `.collect(Collectors.toList())` ou reduções puras via `.reduce()`.

## 3. Estudo de Caso Integrado: Motor Analítico de Transações em Larga Escala

O projeto prático abaixo processa um volume expressivo de cupons fiscais, calculando o faturamento bruto auditado e comparando o tempo de processamento entre streams sequenciais e paralelas:

```java
package br.edu.universidade.sistema.fiscal.dominio;

// 1. Entidade de Domínio Imutável representando o Cupom Fiscal
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

    // Simulação de custo de CPU (Q) por elemento: cálculo de validação criptográfica
    public double calcularImpostoComplexo() {
        double imposto = 0.0;
        for (int i = 0; i < 50; i++) {
            imposto += Math.sin(valorTotal) * Math.cos(i);
        }
        return Math.max(0.0, valorTotal * 0.12 + (imposto * 0.001));
    }
}
```

```java
package br.edu.universidade.sistema.fiscal.service;

import br.edu.universidade.sistema.fiscal.dominio.CupomFiscal;
import java.util.List;

// 2. Serviço de Auditoria Comparativo (Sequencial vs. Paralelo)
public class AuditoriaFiscalService {

    // Processamento com Stream Sequencial convencional
    public double calcularImpostoTotalSequencial(List<CupomFiscal> cupons) {
        return cupons.stream()
                .filter(c -> !c.isCancelado())
                .mapToDouble(CupomFiscal::calcularImpostoComplexo)
                .sum();
    }

    // Processamento com Stream Paralela (Distribuída no ForkJoinPool)
    public double calcularImpostoTotalParalelo(List<CupomFiscal> cupons) {
        return cupons.parallelStream() // Habilita o pipeline multi-core
                .filter(c -> !c.isCancelado())
                .mapToDouble(CupomFiscal::calcularImpostoComplexo)
                .sum(); // Redução nativa paralela thread-safe
    }

    // Identificação paralela de cupons válidos com valor crítico (> R$ 10.000)
    public long contarTransacoesCriticasParalelo(List<CupomFiscal> cupons, double corte) {
        return cupons.parallelStream()
                .filter(c -> !c.isCancelado())
                .filter(c -> c.getValorTotal() >= corte)
                .count();
    }
}
```

```java
package br.edu.universidade.sistema.fiscal;

import br.edu.universidade.sistema.fiscal.dominio.CupomFiscal;
import br.edu.universidade.sistema.fiscal.service.AuditoriaFiscalService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

// 3. Aplicação Executável com Benchmark de Performance
public class ParalelismoFiscalApp {
    public static void main(String[] args) {
        System.out.println("========== BENCHMARK: STREAMS SEQUENCIAIS VS. PARALELAS ==========");
        System.out.printf("Núcleos de CPU Lógicos Detectados: %d%n", Runtime.getRuntime().availableProcessors());
        System.out.printf("Threads no ForkJoinPool Comum    : %d%n", ForkJoinPool.getCommonPoolParallelism());

        // Geração controlada de massa de dados (1.000.000 de registros)
        int volumeDados = 1_000_000;
        System.out.printf("Carregando massa em memória (%d cupons)...%n", volumeDados);
        List<CupomFiscal> baseCupons = new ArrayList<>(volumeDados);

        for (long i = 1; i <= volumeDados; i++) {
            baseCupons.add(new CupomFiscal(
                    i,
                    "NFE-" + i,
                    100.0 + (i % 500),
                    (i % 10 == 0) // 10% cancelados
            ));
        }

        AuditoriaFiscalService service = new AuditoriaFiscalService();

        // 1. Execução Sequencial
        System.out.println("\nIniciando processamento SEQUENCIAL...");
        long inicioSeq = System.currentTimeMillis();
        double totalImpostosSeq = service.calcularImpostoTotalSequencial(baseCupons);
        long duracaoSeq = System.currentTimeMillis() - inicioSeq;
        System.out.printf("Total Impostos (Seq) : R$ %.2f%n", totalImpostosSeq);
        System.out.printf("Tempo Gasto (Seq)    : %d ms%n", duracaoSeq);

        // 2. Execução Paralela
        System.out.println("\nIniciando processamento PARALELO...");
        long inicioPar = System.currentTimeMillis();
        double totalImpostosPar = service.calcularImpostoTotalParalelo(baseCupons);
        long duracaoPar = System.currentTimeMillis() - inicioPar;
        System.out.printf("Total Impostos (Par) : R$ %.2f%n", totalImpostosPar);
        System.out.printf("Tempo Gasto (Par)    : %d ms%n", duracaoPar);

        // Análise de Eficiência (Speedup)
        double speedup = (double) duracaoSeq / duracaoPar;
        System.out.printf("\nFator de Aceleração Real (Speedup): %.2fx mais rápido%n", speedup);

        // 3. Contagem Paralela
        long criticos = service.contarTransacoesCriticasParalelo(baseCupons, 500.0);
        System.out.printf("Total de Cupons com Valor >= R$ 500: %d%n", criticos);
        System.out.println("==================================================================");
    }
}
```

## 4. Diagnóstico de Erros Comuns e Armadilhas

### Armadilha 1: Usar forEach em Vez de forEachOrdered Quando a Ordem Importa

**Código Problemático:**

```java
List<Integer> dados = List.of(1, 2, 3, 4, 5, 6, 7, 8);
dados.parallelStream().forEach(System.out::print); // SAÍDA CAÓTICA E IMPREVISÍVEL!
```

- **Diagnóstico Técnico:** Em pipelines paralelos, o método `forEach` executa o consumidor em qualquer thread sem respeitar a ordem de inserção da coleção de origem.
- **Correção:** Se a ordenação estrita for obrigatória para a regra de negócio, utilize `.forEachOrdered(System.out::print)`. Contudo, observe que forçar a ordem serializa a entrega final, reduzindo parte dos ganhos de paralelismo.

### Armadilha 2: Bloqueios de I/O Dentro da Piscina Comum (ForkJoinPool.commonPool)

**Código Problemático:**

```java
pedidos.parallelStream().forEach(pedido -> {
    enviarRequisicaoHttpExterna(pedido); // BLOQUEIO DE REDE / LATÊNCIA ALTA!
});
```

- **Diagnóstico Arquitetural:** O `ForkJoinPool.commonPool()` é um recurso estático compartilhado por toda a aplicação na JVM. Colocar operações bloqueantes de I/O (leitura de disco, chamadas web, queries lentas de banco) dentro de uma stream paralela esgota as threads disponíveis de CPU, travando todas as outras rotinas do sistema que dependem de streams paralelas.
- **Diretriz:** Streams paralelas devem ser reservadas para processamento intensivo de CPU em memória. Para I/O concorrente, utilize `CompletableFuture` com um pool dedicado de threads (`ExecutorService`).

### Armadilha 3: Mutações Concorrentes com Reduções Inadequadas

**Código Problemático:**

```java
double[] acumulador = new double[1];
itens.parallelStream().forEach(item -> acumulador[0] += item.getPreco()); // RACE CONDITION!
```

- **Diagnóstico Técnico:** Múltiplas threads lêem e gravam simultaneamente na mesma posição do array primitivo. Como a operação `+=` não é atômica no nível do processador, dezenas de atualizações são sobrescritas e perdidas.
- **Correção:** Utilize o método nativo `.mapToDouble(Item::getPreco).sum()`, que realiza agregações parciais isoladas por thread e soma os subtotais no passo final com segurança *thread-safe*.

## 5. Roteiro Prático de Depuração: Inspecionando as Threads Paralelas na IDE

Para auditar visualmente a distribuição de threads do `ForkJoinPool` no depurador da sua IDE (IntelliJ IDEA, Eclipse ou VS Code):

1. No método `main` de `ParalelismoFiscalApp`, posicione um ponto de interrupção (*breakpoint*) dentro da chamada de imposto: na primeira linha do método `calcularImpostoComplexo`.
2. Inicie a execução em modo de depuração (*Debug*).
3. Na aba de seleção de threads (*Threads / Frames*):
   - Observe que o programa está pausado não apenas na thread `main`, mas exibe diversas threads ativas com nomes como `ForkJoinPool.commonPool-worker-1`, `ForkJoinPool.commonPool-worker-2`, etc.
   - Selecione cada *worker* na lista: veja que cada um possui sua própria pilha de chamadas e está processando um índice diferente da lista `baseCupons` simultaneamente, comprovando o particionamento em nível de hardware.
4. Desative o *breakpoint* e clique em *Resume Program* (F9) para acompanhar o término e a consolidação dos dados.

## 6. Exercício de Fixação Prática: Motor Analítico de Telemetria IoT em Nuvem

Implemente um motor de auditoria de sinais de sensores industriais (IoT) comparando pipelines sequenciais e concorrentes em larga escala:

1. **Construa a Classe `SinalSensor`:**
   - Atributos privados: `idSensor` (`Long`), `macAddress` (`String`), `leituraTemperatura` (`double`), `statusCritico` (`boolean`).
   - Construtor parametrizado completo rejeitando MAC nulo via `IllegalArgumentException`.
   - **Método de cálculo intensivo `double processarNormalizacao()`:**
     - Aplica uma equação de amortecimento matemático (ex.: multiplica a temperatura por raiz quadrada e funções trigonométricas em laço de 100 iterações para simular custo Q).
   - Métodos acessores (*getters*) e método descritivo `toString()`.

2. **Construa o Serviço `AuditoriaTelemetriaService`:**
   - **Método `double consolidarMediaTermicaSequencial(List<SinalSensor> sinais)`:**
     - Filtra apenas sensores com status crítico ativo (`statusCritico == true`).
     - Mapeia com `mapToDouble(SinalSensor::processarNormalizacao)` e calcula a média com `.average().orElse(0.0)`.
   - **Método `double consolidarMediaTermicaParalelo(List<SinalSensor> sinais)`:**
     - Executa rigorosamente a mesma consulta utilizando `parallelStream()`.
   - **Método `List<String> coletarEnderecosMacSuspeitosParalelo(List<SinalSensor> sinais, double tetoTemperatura)`:**
     - Executa em `parallelStream()`.
     - Filtra leituras normalizadas superiores ao teto informado.
     - Mapeia apenas para o endereço MAC em letras maiúsculas.
     - Elimina duplicatas com `.distinct()`.
     - Coleta o resultado em lista imutável com `.toList()`.

3. **Construa a Classe Executável `TelemetriaApp`:**
   - Instancie uma massa de **500.000 sinais** de sensores populada em um `ArrayList`.
   - Execute ambos os métodos de consolidação de média registrando o tempo inicial e final em milissegundos (`System.currentTimeMillis()`).
   - Imprima o resultado da média térmica obtida em ambas as execuções para comprovar que o valor numérico permaneceu exatamente igual.
   - Apresente a aceleração (*Speedup*) obtida e a quantidade de endereços MAC coletados pelo filtro paralelo.