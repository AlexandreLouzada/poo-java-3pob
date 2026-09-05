# Tutorial de Java — Aula 23: Concorrência Multithread Clássica e a API java.util.concurrent

| | |
|---|---|
| **Disciplina** | Programação Orientada a Objetos |
| **Curso** | Análise e Desenvolvimento de Sistemas (3º Período) |
| **Tema** | Fundamentos de Multithreading, Ciclo de Vida de Threads, Sincronização (`synchronized`), Problemas Clássicos (*Race Conditions*, *Deadlocks*), Pools de Threads com `ExecutorService`, Tarefas com Retorno (`Callable` e `Future`) e Processamento Assíncrono com `CompletableFuture` |
| **Carga Horária** | 100 minutos (2 horas-aula) |
| **Professor Responsável** | Alexandre Neves Louzada |
| **Arquivo de Referência** | `TutorialAula23.md` |

## 1. Objetivos de Aprendizagem

- **Conceitual:** Compreender a diferença entre processos do sistema operacional e threads da JVM; entender o compartilhamento do Heap entre múltiplas threads e o isolamento de cada *Call Stack*; reconhecer os perigos de condições de corrida (*Race Conditions*), visibilidade de memória e travamentos mútuos (*Deadlocks*).
- **Técnico:** Criar e disparar threads manuais utilizando a classe `Thread` e a interface `Runnable`; aplicar exclusão mútua por meio da palavra-chave `synchronized` e blocos de sincronização com objetos de lock; utilizar variáveis atômicas (`AtomicInteger`, `AtomicReference`) e coleções concorrentes (`ConcurrentHashMap`).
- **Arquitetural:** Superar o antipadrão da criação desenfreada de threads manuais utilizando a API de alto nível `java.util.concurrent`; gerenciar pools de execução com `ExecutorService` e `Executors`; manipular tarefas que retornam valor e lançam exceções via `Callable<V>` e `Future<V>`; construir pipelines não-bloqueantes com `CompletableFuture`.
- **Prático:** Implementar um motor de processamento e liquidação de pedidos para e-commerce corporativo, orquestrando consultas assíncronas paralelas (estoque, antifraude e gateway de pagamento) com união não-bloqueante de resultados.

## 2. Fundamentação Teórica

### Processos vs. Threads e a Memória da JVM

- **Processo:** Uma instância de programa em execução gerenciada pelo sistema operacional, com espaço de endereçamento de memória totalmente isolado.
- **Thread (Linha de Execução):** A menor unidade de código despachável pela CPU. Dentro de um mesmo processo Java:
  - Cada Thread possui sua própria *Call Stack* privativa para variáveis locais e chamadas de métodos.
  - Todas as Threads compartilham o mesmo Heap (onde residem as instâncias de objetos e variáveis de classe no Metaspace).

```plaintext
┌──────────────────────────┬──────────────────────────┬───────────────────────┐
│ Thread 1 (Call Stack)    │ Thread 2 (Call Stack)    │ Heap Compartilhado    │
├──────────────────────────┼──────────────────────────┼───────────────────────┤
│ • Frame main()           │ • Frame run()            │ • Instância de Conta  │
│ • Variáveis primitivas   │ • Variáveis primitivas   │   [saldo = 500.0]     │
│ • Referências para Heap  │ • Referências para Heap  │ (Ponto crítico de     │
│                          │                          │  concorrência!)       │
└──────────────────────────┴──────────────────────────┴───────────────────────┘
```

Quando múltiplas threads tentam ler e alterar o mesmo atributo de um objeto no Heap sem coordenação, ocorre uma **Condição de Corrida (Race Condition)**, corrompendo o estado interno da aplicação.

### Concorrência Clássica: Thread, Runnable e synchronized

Tradicionalmente, a execução concorrente em Java é implementada de duas formas:

- **Herdando de `Thread`:** Estende a classe base e sobrescreve o método `run()`. (Não recomendada, pois consome a herança simples de classes do Java).
- **Implementando `Runnable`:** Separa o trabalho a ser feito (tarefa funcional) do mecanismo que executa o trabalho (`Thread`).

### Sincronização e Locks Intrínsecos (synchronized)

Toda instância de classe no Java possui internamente uma trava lógica chamada *Monitor Lock* (ou lock intrínseco):

- Um método ou bloco marcado com `synchronized` exige que a thread adquira a trava daquele objeto antes de entrar.
- Outras threads que tentarem acessar qualquer bloco protegido pelo mesmo lock são colocadas no estado `BLOCKED` até que a primeira thread saia e libere a trava.

```java
public class ContaBancaria {
    private double saldo;

    // Apenas UMA thread por vez consegue executar este método no mesmo objeto:
    public synchronized void sacar(double valor) {
        if (this.saldo >= valor) {
            this.saldo -= valor;
        }
    }
}
```

### O Problema do Deadlock (Travamento Mútuo)

Um *Deadlock* ocorre quando duas ou mais threads ficam bloqueadas para sempre, cada uma esperando por um recurso preso pela outra:

> Thread A retém Lock 1 e solicita Lock 2 ⇔ Thread B retém Lock 2 e solicita Lock 1

Para prevenir deadlocks, estabeleça uma ordem estrita e universal de aquisição de travas em todo o sistema (por exemplo, sempre adquirir o lock da conta com menor ID primeiro).

### A Revolução do java.util.concurrent: Pools com ExecutorService

Instanciar threads manuais com `new Thread()` para cada requisição de usuário é um antipadrão corporativo crítico: criar uma thread nativa consome cerca de 1 MB de memória de pilha do SO e exige chamadas caras de kernel.

O framework de executores desacopla a submissão de tarefas da sua execução física, reaproveitando uma quantidade controlada de threads ativas (*Thread Pool*):

```plaintext
Tarefas Submetidas                 Pool de Threads Ativas (Workers)
┌──────────────┐                   ┌──────────────┐
│ Tarefa 1     │                   │   Worker 1   │ ──► Processa Tarefa 1
├──────────────┤                   ├──────────────┤
│ Tarefa 2     │ ──► [ Fila / ] ──►│   Worker 2   │ ──► Processa Tarefa 2
├──────────────┤     [ Deque  ]    ├──────────────┤
│ Tarefa 3     │                   │   Worker 3   │ (Aguardando próxima)
└──────────────┘                   └──────────────┘
```

- `Executors.newFixedThreadPool(n)`: Cria um pool com tamanho fixo de threads. Se novas tarefas forem submetidas, elas aguardam em uma fila ordenada até que uma thread fique livre.
- `Executors.newCachedThreadPool()`: Cria novas threads conforme a demanda e reutiliza as ociosas, descartando threads inativas por mais de 60 segundos.

### Runnable vs. Callable<V> e o Papel do Future<V>

| Característica | `java.lang.Runnable` | `java.util.concurrent.Callable<V>` |
|---|---|---|
| Método Contratual | `public void run()` | `public V call() throws Exception` |
| Retorno de Valor | Nenhum (void) | Retorna resultado genérico (V) |
| Tratamento de Falhas | Não pode lançar exceções checadas (Checked) | Pode lançar qualquer `Exception` |

Ao submeter um `Callable` para um `ExecutorService`, ele retorna imediatamente um objeto `Future<V>`: uma promessa de resultado futuro:

- `future.isDone()`: Informa se a execução paralela já foi concluída.
- `future.get()`: Bloqueia a thread atual até que o resultado termine e o entrega. Se a tarefa lançou exceção, `get()` empacota a falha dentro de uma `ExecutionException`.

### Programação Não-Bloqueante com CompletableFuture

Embora `Future` permita recuperar resultados assíncronos, o método `.get()` é bloqueante. O `CompletableFuture<T>` (Java 8+) introduziu a composição assíncrona orientada a eventos e callbacks, permitindo encadear transformações sem travar a thread chamadora:

- `supplyAsync(Supplier<U>)`: Inicia uma operação assíncrona em segundo plano.
- `thenApply(Function<T, R>)`: Transforma o resultado assíncrono assim que ele ficar pronto (equivalente ao `map`).
- `thenAccept(Consumer<T>)`: Consome o resultado final (efeito colateral).
- `thenCombine(outroFuture, biFunction)`: Executa duas tarefas independentes em paralelo e combina seus resultados assim que ambas terminarem.

## 3. Estudo de Caso Integrado: Motor de Checkout Assíncrono com CompletableFuture

O projeto abaixo implementa um serviço de checkout para comércio eletrônico corporativo, disparando validações simultâneas de estoque, antifraude e gateway bancário de forma assíncrona e não-bloqueante:

```java
package br.edu.universidade.sistema.concorrencia.dominio;

// 1. Entidade de Domínio representando a Solicitação de Pedido
public class SolicitacaoPedido {
    private final Long idPedido;
    private final String cliente;
    private final double valorTotal;

    public SolicitacaoPedido(Long idPedido, String cliente, double valorTotal) {
        this.idPedido = idPedido;
        this.cliente = cliente;
        this.valorTotal = valorTotal;
    }

    public Long getIdPedido() { return idPedido; }
    public String getCliente() { return cliente; }
    public double getValorTotal() { return valorTotal; }

    @Override
    public String toString() {
        return String.format("Pedido #%d | Cliente: %s | Total: R$ %.2f", idPedido, cliente, valorTotal);
    }
}
```

```java
package br.edu.universidade.sistema.concorrencia.service;

import br.edu.universidade.sistema.concorrencia.dominio.SolicitacaoPedido;
import java.util.concurrent.*;

// 2. Serviço de Checkout Assíncrono e Não-Bloqueante
public class CheckoutAssincronoService {

    private final ExecutorService threadPool;

    public CheckoutAssincronoService(int totalWorkers) {
        // Pool de threads dedicado para isolamento de carga corporativa
        this.threadPool = Executors.newFixedThreadPool(totalWorkers);
    }

    // Tarefa Assíncrona 1: Consulta de Estoque em microsserviço externo
    public CompletableFuture<Boolean> verificarEstoqueAsync(SolicitacaoPedido pedido) {
        return CompletableFuture.supplyAsync(() -> {
            simularLatenciaRede(300); // 300 ms de latência simulada
            System.out.printf("[%s] Estoque validado com sucesso para Pedido #%d%n",
                    Thread.currentThread().getName(), pedido.getIdPedido());
            return true;
        }, threadPool);
    }

    // Tarefa Assíncrona 2: Motor Antifraude de Risco e Score
    public CompletableFuture<Integer> calcularScoreFraudeAsync(SolicitacaoPedido pedido) {
        return CompletableFuture.supplyAsync(() -> {
            simularLatenciaRede(500); // 500 ms de latência simulada
            int score = (pedido.getValorTotal() > 5000.0) ? 85 : 20; // Risco de 0 a 100
            System.out.printf("[%s] Antifraude concluído. Score apurado: %d%n",
                    Thread.currentThread().getName(), score);
            return score;
        }, threadPool);
    }

    // Tarefa Assíncrona 3: Orquestração e Autorização de Pagamento
    public CompletableFuture<String> processarCheckoutCompleto(SolicitacaoPedido pedido) {
        CompletableFuture<Boolean> tarefaEstoque = verificarEstoqueAsync(pedido);
        CompletableFuture<Integer> tarefaAntifraude = calcularScoreFraudeAsync(pedido);

        // thenCombine: Dispara ambas em paralelo e junta os resultados sem travar threads
        return tarefaEstoque.thenCombineAsync(tarefaAntifraude, (estoqueDisponivel, scoreFraude) -> {
            System.out.printf("[%s] Consolidando decisões para Pedido #%d...%n",
                    Thread.currentThread().getName(), pedido.getIdPedido());

            if (!estoqueDisponivel) {
                return "RECUSADO: Produto esgotado nos armazéns.";
            }
            if (scoreFraude > 70) {
                return "RECUSADO: Reprovado pela mesa de análise de risco e antifraude.";
            }

            return String.format("APROVADO: Pedido #%d faturado e autorizado via Cartão!", pedido.getIdPedido());
        }, threadPool);
    }

    public void encerrar() {
        // Encerramento gracioso do pool liberando descritores do SO
        this.threadPool.shutdown();
        try {
            if (!this.threadPool.awaitTermination(3, TimeUnit.SECONDS)) {
                this.threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            this.threadPool.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private void simularLatenciaRede(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
```

```java
package br.edu.universidade.sistema.concorrencia;

import br.edu.universidade.sistema.concorrencia.dominio.SolicitacaoPedido;
import br.edu.universidade.sistema.concorrencia.service.CheckoutAssincronoService;
import java.util.concurrent.CompletableFuture;

// 3. Aplicação Executável demonstrando a composição assíncrona
public class ConcorrenciaApp {
    public static void main(String[] args) {
        System.out.println("========== MOTOR DE CONCORRÊNCIA CORPORATIVO ==========");
        System.out.printf("Thread Principal: %s%n%n", Thread.currentThread().getName());

        CheckoutAssincronoService checkoutService = new CheckoutAssincronoService(4);

        SolicitacaoPedido p1 = new SolicitacaoPedido(101L, "Beatriz Costa", 850.00);
        SolicitacaoPedido p2 = new SolicitacaoPedido(102L, "Carlos Eduardo", 7500.00); // Alto valor

        long inicio = System.currentTimeMillis();

        // Disparo assíncrono: a thread main continua livre imediatamente!
        CompletableFuture<String> resultadoP1 = checkoutService.processarCheckoutCompleto(p1);
        CompletableFuture<String> resultadoP2 = checkoutService.processarCheckoutCompleto(p2);

        System.out.println("[Main] Os dois pedidos foram submetidos de forma concorrente.");
        System.out.println("[Main] A aplicação está livre para processar outras rotinas...\n");

        // join() é utilizado aqui apenas no ponto final de entrega para aguardar o término
        CompletableFuture.allOf(resultadoP1, resultadoP2).join();

        long duracaoTotal = System.currentTimeMillis() - inicio;

        System.out.println("\n--- RESULTADOS FINAIS DA LIQUIDAÇÃO ---");
        System.out.println("Resultado Pedido 101: " + resultadoP1.join());
        System.out.println("Resultado Pedido 102: " + resultadoP2.join());
        System.out.printf("Tempo Total Gasto     : %d ms (Execução paralela confirmada)%n", duracaoTotal);

        checkoutService.encerrar();
        System.out.println("========================================================");
    }
}
```

## 4. Diagnóstico de Erros Comuns e Armadilhas

### Armadilha 1: Ignorar o InterruptedException com Bloco Vazio

**Código Problemático:**

```java
try {
    Thread.sleep(1000);
} catch (InterruptedException e) {
    // Bloco vazio: engoliu a interrupção!
}
```

- **Diagnóstico Técnico:** Quando um pool de threads solicita a parada (`shutdownNow()`), ele envia um sinal de interrupção para as threads ativas. Se o código engolir o erro sem restaurar o sinal, a thread nunca saberá que deve parar, tornando-se uma thread zumbi que impede a finalização da JVM.
- **Correção:** Sempre restaure o status de interrupção: `Thread.currentThread().interrupt();`.

### Armadilha 2: Disparar Chamadas Bloqueantes de future.get() Logo Após a Submissão

**Código Problemático:**

```java
Future<String> f1 = executor.submit(tarefa1);
String r1 = f1.get(); // BLOQUEIA AQUI E ESPERA!

Future<String> f2 = executor.submit(tarefa2); // Só inicia depois que tarefa1 acabou!
String r2 = f2.get();
```

- **Diagnóstico Técnico:** Chamar `.get()` imediatamente após o `submit()` serializa a execução, eliminando 100% das vantagens do paralelismo e fazendo com que duas tarefas que poderiam rodar simultaneamente rodem em sequência.
- **Correção:** Submeta todas as tarefas primeiro; guarde os objetos `Future` em uma lista e apenas no final itere sobre eles coletando os resultados com `.get()`, ou utilize `CompletableFuture.allOf(...)`.

### Armadilha 3: Omissão de executor.shutdown()

**Código Problemático:**

```java
ExecutorService pool = Executors.newFixedThreadPool(5);
pool.submit(tarefa);
// O programa chega ao final do main() mas NÃO finaliza!
```

- **Diagnóstico da JVM:** As threads criadas em pools padrão do Java são configuradas como *Threads de Usuário* (User Threads), e não *Daemon Threads*. A JVM só finaliza seu processo quando todas as threads de usuário forem encerradas. Sem o `shutdown()`, o programa fica travado indefinidamente no terminal.
- **Regra de Ouro:** Sempre feche o pool no encerramento da aplicação, preferencialmente dentro de um bloco `finally` ou método de ciclo de vida.

## 5. Roteiro Prático de Depuração: Inspecionando Múltiplas Threads na IDE

Para auditar o paralelismo real e inspecionar diferentes *Call Stacks* na sua IDE (IntelliJ IDEA, Eclipse ou VS Code):

1. No código `CheckoutAssincronoService`, coloque um ponto de interrupção (*breakpoint*) dentro da lambda do método `calcularScoreFraudeAsync` na linha `int score = ...`.
2. Configure o *breakpoint* na IDE: clique com o botão direito sobre o ponto vermelho e altere a opção *Suspend* de *All* para *Thread* (isso pausa apenas a thread que atingiu a linha, permitindo que as outras continuem executando).
3. Inicie o programa em modo de depuração (*Debug*).
4. Na janela de threads (*Threads / Call Stack*):
   - Observe que a thread pausada possui um nome do tipo `pool-1-thread-2`.
   - Selecione a thread `main`: veja que ela já avançou e está aguardando no método `join()`.
   - Selecione a thread `pool-1-thread-1`: veja que ela está executando paralelamente a rotina de validação de estoque, comprovando a concorrência real entre núcleos de processamento.
5. Pressione *Resume Program* (F9) para liberar a execução e acompanhar a finalização das tarefas.

## 6. Exercício de Fixação Prática: Motor de Cotação Cambial e Financeira Multibancos

Implemente um agregador corporativo de câmbio que consulte simultaneamente taxas em múltiplos provedores financeiros de forma assíncrona:

1. **Construa a Classe `CotacaoMoeda`:**
   - Atributos privados: `provedor` (`String`), `moeda` (`String` — `"USD"`, `"EUR"`), `valorTaxa` (`double`) e `tempoRespostaMs` (`long`).
   - Construtor parametrizado completo rejeitando taxas zeradas via `IllegalArgumentException`.
   - Métodos acessores (*getters*) e método descritivo `toString()` formatando a taxa com `%.4f`.

2. **Construa o Serviço `AgregadorCambioService`:**
   - Inicie uma instância privada encapsulada de `ExecutorService` com 3 threads.
   - **Método `CompletableFuture<CotacaoMoeda> consultarProvedorAsync(String nomeProvedor, double taxaSimulada, long latenciaMs)`:**
     - Dispara assincronamente via `CompletableFuture.supplyAsync()`.
     - Simula a latência informada com `Thread.sleep()`.
     - Retorna a instância de `CotacaoMoeda` preenchida.
   - **Método `CompletableFuture<CotacaoMoeda> buscarMelhorCotacao(List<CompletableFuture<CotacaoMoeda>> consultas)`:**
     - Utiliza `CompletableFuture.allOf(...)` para aguardar a finalização de todas as cotações.
     - Ao término, itera sobre os resultados das promessas e seleciona a de menor valor de taxa (a mais vantajosa para compra).
   - **Método `void encerrar()`** para desligar o pool de threads com segurança.

3. **Construa a Classe Executável `CambioApp`:**
   - Dispare em paralelo a consulta de três provedores: `"Banco Alpha"` (taxa: 5.45, latência: 400 ms), `"Banco Beta"` (taxa: 5.41, latência: 700 ms) e `"Banco Gamma"` (taxa: 5.48, latência: 200 ms).
   - Sem bloquear a thread principal prematuramente, componha a busca pela melhor taxa.
   - Exiba no console a cotação vencedora (`"Banco Beta"`) comprovando que a aplicação aguardou todas as respostas de forma assíncrona antes de emitir a decisão de negócio.