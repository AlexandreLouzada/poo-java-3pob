# Explicação da Aula 23 — Concorrência Multithread Clássica e a API java.util.concurrent

| | |
|---|---|
| **Disciplina** | Programação Orientada a Objetos |
| **Curso** | Análise e Desenvolvimento de Sistemas (3º Período) |
| **Tema** | Fundamentos de Multithreading, Ciclo de Vida de Threads, Sincronização (`synchronized`), Problemas Clássicos (*Race Conditions*, *Deadlocks*), Pools de Threads com `ExecutorService`, Tarefas com Retorno (`Callable` e `Future`) e Processamento Assíncrono com `CompletableFuture` |
| **Carga Horária** | 100 minutos (2 horas-aula) |
| **Professor** | Alexandre Neves Louzada |
| **Tutorial** | `aulas/TutorialAula23.md` |
| **Estudo de Caso** | `exemplos/aula-23/` |
| **Exercícios Resolvidos** | `solucoes/aula-23/` |

## 1. Objetivos de Aprendizagem

- **Conceitual:** Compreender a diferença entre processos do sistema operacional e threads da JVM; entender o compartilhamento do Heap entre múltiplas threads e o isolamento de cada *Call Stack*; reconhecer os perigos de condições de corrida (*Race Conditions*), visibilidade de memória e travamentos mútuos (*Deadlocks*).
- **Técnico:** Criar e disparar threads manuais com `Thread` e `Runnable`; aplicar exclusão mútua com `synchronized`; utilizar variáveis atômicas (`AtomicInteger`, `AtomicReference`) e coleções concorrentes (`ConcurrentHashMap`).
- **Arquitetural:** Superar o antipadrão da criação desenfreada de threads manuais com `java.util.concurrent`; gerenciar pools com `ExecutorService` e `Executors`; manipular tarefas com retorno via `Callable<V>` e `Future<V>`; construir pipelines não-bloqueantes com `CompletableFuture`.
- **Prático:** Implementar um motor de processamento e liquidação de pedidos para e-commerce corporativo, orquestrando consultas assíncronas paralelas (estoque, antifraude e gateway de pagamento) com união não-bloqueante de resultados.

## 2. Conteúdo Teórico Detalhado

### 2.1 Processos vs. Threads e a Memória da JVM

- **Processo:** Instância de programa em execução gerenciada pelo SO, com espaço de endereçamento totalmente isolado.
- **Thread:** Menor unidade de código despachável pela CPU. Dentro de um mesmo processo Java:
  - Cada Thread possui sua própria *Call Stack* privativa para variáveis locais.
  - Todas as Threads compartilham o mesmo Heap (onde residem as instâncias de objetos).

Quando múltiplas threads tentam ler e alterar o mesmo atributo de um objeto no Heap sem coordenação, ocorre uma **Condição de Corrida (Race Condition)**, corrompendo o estado interno da aplicação.

### 2.2 Concorrência Clássica: Thread, Runnable e synchronized

A execução concorrente tradicional tem duas formas:

- **Herdando de `Thread`:** Estende a classe base e sobrescreve `run()`. Não recomendada, pois consome a herança simples.
- **Implementando `Runnable`:** Separa o trabalho (tarefa funcional) do mecanismo que executa (`Thread`).

Toda instância de classe possui um *Monitor Lock* (lock intrínseco). Um método/bloco `synchronized` exige a trava antes de entrar; outras threads ficam `BLOCKED` até a liberação:

```java
public synchronized void sacar(double valor) {
    if (this.saldo >= valor) {
        this.saldo -= valor;
    }
}
```

### 2.3 O Problema do Deadlock (Travamento Mútuo)

Um *Deadlock* ocorre quando duas ou mais threads ficam bloqueadas para sempre, cada uma esperando por um recurso preso pela outra. Para prevenir, estabeleça uma ordem estrita e universal de aquisição de travas.

### 2.4 A Revolução do java.util.concurrent: Pools com ExecutorService

Instanciar `new Thread()` para cada requisição é antipadrão corporativo: criar thread nativa consome cerca de 1 MB de pilha e exige chamadas caras de kernel. O framework de executores desacopla submissão de tarefas da execução física:

- `Executors.newFixedThreadPool(n)`: Pool de tamanho fixo; tarefas excedentes aguardam em fila.
- `Executors.newCachedThreadPool()`: Cria threads sob demanda e reutiliza ociosas, descartando inativas por mais de 60 segundos.

### 2.5 Runnable vs. Callable<V> e o Papel do Future<V>

| Característica | `java.lang.Runnable` | `java.util.concurrent.Callable<V>` |
|---|---|---|
| Método Contratual | `public void run()` | `public V call() throws Exception` |
| Retorno de Valor | Nenhum (void) | Retorna resultado genérico (V) |
| Tratamento de Falhas | Não pode lançar exceções checadas | Pode lançar qualquer `Exception` |

Ao submeter um `Callable`, retorna-se imediatamente um `Future<V>`:

- `future.isDone()`: informa se a execução já foi concluída.
- `future.get()`: bloqueia a thread atual até o término; se houve exceção, empacota em `ExecutionException`.

### 2.6 Programação Não-Bloqueante com CompletableFuture

O `CompletableFuture<T>` (Java 8+) permite composição assíncrona orientada a eventos e callbacks:

- `supplyAsync(Supplier<U>)`: inicia operação assíncrona em segundo plano.
- `thenApply(Function<T, R>)`: transforma o resultado assíncrono (equivalente ao `map`).
- `thenAccept(Consumer<T>)`: consome o resultado final.
- `thenCombine(outroFuture, biFunction)`: executa duas tarefas independentes em paralelo e combina resultados.

### 2.7 Diagnóstico de Erros Comuns e Armadilhas

**Armadilha 1: Ignorar o InterruptedException com Bloco Vazio**

```java
try {
    Thread.sleep(1000);
} catch (InterruptedException e) {
    // Bloco vazio: engoliu a interrupção!
}
```

Diagnóstico: quando um pool solicita a parada (`shutdownNow()`), ele envia um sinal de interrupção para as threads ativas. Se o código engolir o erro sem restaurar o sinal, a thread nunca saberá que deve parar, tornando-se uma thread zumbi. Correção: `Thread.currentThread().interrupt();`.

**Armadilha 2: Disparar future.get() Imediatamente Após a Submissão**

```java
Future<String> f1 = executor.submit(tarefa1);
String r1 = f1.get(); // BLOQUEIA AQUI E ESPERA!

Future<String> f2 = executor.submit(tarefa2); // Só inicia depois que tarefa1 acabou!
String r2 = f2.get();
```

Diagnóstico: chamar `.get()` logo após o `submit()` serializa a execução, eliminando o paralelismo. Correção: submeter todas as tarefas primeiro, guardar os `Future` em uma lista e apenas no final coletar os resultados, ou usar `CompletableFuture.allOf(...)`.

**Armadilha 3: Omissão de executor.shutdown()**

```java
ExecutorService pool = Executors.newFixedThreadPool(5);
pool.submit(tarefa);
// O programa chega ao final do main() mas NÃO finaliza!
```

Diagnóstico: threads de pools padrão são *User Threads*, não *Daemon Threads*. A JVM só finaliza quando todas as threads de usuário são encerradas. Regra de ouro: fechar o pool no encerramento da aplicação, preferencialmente em `finally` ou método de ciclo de vida.

### 2.8 Roteiro Prático de Depuração na IDE

Para auditar o paralelismo real na IDE:

1. Coloque um breakpoint na lambda do método `calcularScoreFraudeAsync` (linha `int score = ...`).
2. Clique com o botão direito no ponto vermelho e altere *Suspend* de *All* para *Thread* (pausa apenas a thread que atingiu a linha).
3. Inicie em modo Debug.
4. Na janela de threads, observe a thread pausada com nome `pool-1-thread-2`; selecione a thread `main` e veja que ela avançou e aguarda no `join()`; selecione `pool-1-thread-1` e veja a rotina de estoque executando em paralelo, comprovando a concorrência real.
5. Pressione Resume Program (F9) e acompanhe a finalização das tarefas.

## 3. Estudo de Caso Aplicado

O estudo de caso implementa um serviço de checkout corporativo que dispara validações simultâneas de estoque, antifraude e gateway bancário de forma assíncrona e não-bloqueante.

**Estrutura de pacotes (exemplos/aula-23/src):**

```
br.edu.universidade.sistema.concorrencia
  |-- dominio/
  |     |-- SolicitacaoPedido.java
  |-- service/
  |     |-- CheckoutAssincronoService.java
  |-- ConcorrenciaApp.java
```

A entidade `SolicitacaoPedido` encapsula id, cliente e valor total do pedido.

O `CheckoutAssincronoService` mantém um pool dedicado via `Executors.newFixedThreadPool(4)`:

- `verificarEstoqueAsync`: `CompletableFuture.supplyAsync` simulando 300 ms de latência de rede, retornando `Boolean`.
- `calcularScoreFraudeAsync`: simula 500 ms de latência e retorna score de risco (85 se o valor total for maior que R$ 5.000,00, senão 20).
- `processarCheckoutCompleto`: combina as duas tarefas em paralelo com `thenCombineAsync`. Se o estoque não estiver disponível, recusa; se o score passar de 70, recusa pela antifraude; caso contrário, aprova.
- `encerrar()`: desliga o pool graciosamente com `shutdown()` e `awaitTermination(3, TimeUnit.SECONDS)`, usando `shutdownNow()` como fallback.

O `ConcorrenciaApp` dispara dois pedidos (R$ 850,00 e R$ 7.500,00) sem bloquear a thread main, comprovando a concorrência; aguarda com `CompletableFuture.allOf(...).join()` e exibe os resultados finais.

## 4. Exercícios Propostos e Solução

O exercício propõe um agregador corporativo de câmbio que consulta simultaneamente taxas em múltiplos provedores financeiros de forma assíncrona.

**Estrutura da solução (solucoes/aula-23/src):**

```
br.edu.universidade.sistema.cambio
  |-- CotacaoMoeda.java
  |-- AgregadorCambioService.java
  |-- CambioApp.java
```

A classe `CotacaoMoeda` tem provedor, moeda (`"USD"`, `"EUR"`), valorTaxa e tempoRespostaMs. O construtor rejeita taxas zeradas e tempos negativos. O `toString()` formata a taxa com 4 casas decimais.

O `AgregadorCambioService`:

```java
public CompletableFuture<CotacaoMoeda> consultarProvedorAsync(CotacaoMoeda cotacao) {
    return CompletableFuture.supplyAsync(() -> {
        try {
            Thread.sleep(cotacao.getTempoRespostaMs());
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Consulta interrompida para o provedor: " + cotacao.getProvedor());
        }
        return cotacao;
    }, executor);
}

public CotacaoMoeda buscarMelhorCotacao(List<CotacaoMoeda> candidatas) {
    CompletableFuture<?>[] tarefas = candidatas.stream()
            .map(this::consultarProvedorAsync)
            .toArray(CompletableFuture[]::new);
    CompletableFuture.allOf(tarefas).join();

    return candidatas.stream()
            .min((a, b) -> Double.compare(a.getValorTaxa(), b.getValorTaxa()))
            .orElseThrow(() -> new IllegalStateException("Nenhuma cotacao disponivel."));
}

public long calcularTempoTotal(List<CotacaoMoeda> candidatas) {
    return candidatas.stream()
            .map(CotacaoMoeda::getTempoRespostaMs)
            .reduce(0L, Long::sum);
}
```

Note que `buscarMelhorCotacao` submete todas as consultas primeiro, aguarda com `allOf(...).join()` apenas no ponto final e seleciona a menor taxa com `Stream.min`.

O `CambioApp` (em `br.edu.universidade.sistema.cambio`) dispara em paralelo os três provedores:

- `"Banco Alpha"` (taxa 5.45, latência 400 ms)
- `"Banco Beta"` (taxa 5.41, latência 700 ms)
- `"Banco Gamma"` (taxa 5.48, latência 200 ms)

O vencedor é o **Banco Beta** (menor taxa 5.4100). O console compara o tempo da execução paralela (próximo do maior tempo individual, ~700 ms) contra a soma dos tempos individuais na execução sequencial (1.300 ms), comprovando o paralelismo. O pool é encerrado com `agregador.encerrar()`.

A classe executável `CambioApp` completa o cenário:

```java
package br.edu.universidade.sistema.cambio;

import java.util.List;

public class CambioApp {
    public static void main(String[] args) {
        System.out.println("--- Executor com 3 Threads consultando 3 provedores em paralelo ---");
        AgregadorCambioService agregador = new AgregadorCambioService(3);

        List<CotacaoMoeda> cotacoes = List.of(
                new CotacaoMoeda("Banco Alpha", "USD", 5.45, 400),
                new CotacaoMoeda("Banco Beta", "USD", 5.41, 700),
                new CotacaoMoeda("Banco Gamma", "USD", 5.48, 200)
        );

        long inicio = System.currentTimeMillis();
        CotacaoMoeda melhor = agregador.buscarMelhorCotacao(cotacoes);
        long fim = System.currentTimeMillis();

        System.out.println("\n--- Cotacoes consultadas com supplyAsync ---");
        cotacoes.forEach(System.out::println);

        System.out.println("\n--- Melhor cotacao (menor taxa) ---");
        System.out.println(melhor);

        long tempoParalelo = fim - inicio;
        System.out.printf("%nTempo total na execucao PARALELA: %d ms (limite do maior tempo: ~%d ms)%n",
                tempoParalelo, melhor.getTempoRespostaMs());
        System.out.printf("Soma dos tempos individuais (execucao sequencial): %d ms%n",
                agregador.calcularTempoTotal(cotacoes));

        if (melhor.getProvedor().equals("Banco Beta")) {
            System.out.println("\nResultado esperado pelo desafio: Banco Beta venceu (taxa 5.4100). OK!");
        }

        agregador.encerrar();
    }
}
```

O contrato do `AgregadorCambioService` também possui `encerrar()` para desligar o pool com segurança, usando `shutdown()`, `awaitTermination(5, TimeUnit.SECONDS)` e `shutdownNow()` como fallback — sempre restaurando o flag de interrupção quando `InterruptedException` é capturada.

Saída esperada ao final da execução:

```
--- Cotacoes consultadas com supplyAsync ---
Cotacao [Provedor: Banco Alpha | Moeda: USD | Taxa: 5.4500 | Tempo: 400 ms]
Cotacao [Provedor: Banco Beta | Moeda: USD | Taxa: 5.4100 | Tempo: 700 ms]
Cotacao [Provedor: Banco Gamma | Moeda: USD | Taxa: 5.4800 | Tempo: 200 ms]

--- Melhor cotacao (menor taxa) ---
Cotacao [Provedor: Banco Beta | Moeda: USD | Taxa: 5.4100 | Tempo: 700 ms]

Tempo total na execucao PARALELA: ~700 ms (limite do maior tempo)
Soma dos tempos individuais (execucao sequencial): 1300 ms
```

Isso confirma que, apesar do Banco Beta ter a maior latência individual (700 ms), a decisão de negócio só é emitida após todas as consultas responderem — mas em tempo paralelo, não sequencial.

## 5. Perguntas de Revisão

1. Qual a diferença entre processo e thread? O que é compartilhado e o que é privado entre threads?
2. O que é uma race condition e como `synchronized` a previne?
3. Explique o que é um deadlock e como evitá-lo com ordem de aquisição de locks.
4. Por que `new Thread()` por requisição é um antipadrão corporativo? Qual a alternativa?
5. Diferencie `Runnable` e `Callable<V>` e explique como `Future<V>` entrega o resultado.
6. Por que chamar `future.get()` imediatamente após o `submit()` serializa a execução?
7. Quais são as funções de `supplyAsync`, `thenApply`, `thenAccept` e `thenCombine` no `CompletableFuture`?
8. O que ocorre se `InterruptedException` for ignorada em um bloco vazio? Qual a boa prática?

## 6. Resumo / Pontos-Chave

- **Threads** da JVM compartilham o Heap mas possuem *Call Stacks* privativas; acesso concorrente sem coordenação gera **race conditions**.
- **`synchronized`** garante exclusão mútua via monitor lock do objeto; **deadlocks** são evitados com ordem universal de aquisição de travas.
- **`ExecutorService`** substitui o antipadrão `new Thread()` com pools reutilizáveis (`newFixedThreadPool`, `newCachedThreadPool`).
- **`Callable<V>` + `Future<V>`** permitem tarefas com retorno e tratamento de exceções checadas.
- **`CompletableFuture`** permite pipelines não-bloqueantes com `supplyAsync`, `thenApply`, `thenAccept`, `thenCombine` e `allOf`.
- Sempre submeta todas as tarefas antes de coletar resultados, para preservar o paralelismo.
- **`executor.shutdown()`** é obrigatório no encerramento para que a JVM finalize; restaure `Thread.currentThread().interrupt()` ao capturar `InterruptedException`.
- O estudo de caso (checkout de e-commerce) e o exercício (agregador de câmbio) consolidam a orquestração assíncrona com escolha da melhor cotação em tempo paralelo.