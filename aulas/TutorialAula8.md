# Tutorial de Java — Aula 08: Diagnóstico de Falhas, Stack Traces e a Hierarquia Throwable

| | |
|---|---|
| **Disciplina** | Programação Orientada a Objetos |
| **Curso** | Análise e Desenvolvimento de Sistemas (3º Período) |
| **Tema** | Diagnóstico de Falhas, Stack Traces e a Hierarquia Throwable |
| **Carga Horária** | 100 minutos (2 horas-aula) |
| **Professor Responsável** | Alexandre Neves Louzada |
| **Arquivo de Referência** | `TutorialAula8.md` |

## 1. Objetivos de Aprendizagem

- **Conceitual:** Compreender a taxonomia de erros e exceções na JVM; diferenciar falhas detectadas em tempo de compilação (*compile-time*) de exceções lançadas em tempo de execução (*runtime*); entender a separação entre falhas de infraestrutura irrecuperáveis (`java.lang.Error`) e anomalias tratáveis da aplicação (`java.lang.Exception`).
- **Arquitetural:** Mapear o ciclo de vida da pilha de chamadas (*Call Stack*), o congelamento de estado da JVM e o mecanismo de desenrolamento da pilha (*Stack Unwinding*) no instante em que uma exceção é arremessada.
- **Técnico:** Ler, interpretar e dissecar registros de *Stack Trace* com foco na identificação de causa raiz (*Root Cause Analysis*); inspecionar metadados de execução por meio do array `StackTraceElement[]`.
- **Prático:** Construir um módulo de auditoria e diagnóstico forense capaz de interceptar anomalias e estruturar relatórios técnicos de falhas no console.

## 2. Fundamentação Teórica

### Erros de Compilação vs. Erros de Execução

No desenvolvimento corporativo, problemas de software manifestam-se em dois momentos com impactos totalmente distintos:

| Característica | Erros de Compilação (Compile-Time) | Erros de Execução (Runtime Exceptions) |
|---|---|---|
| Detectado por | Compilador (`javac`) ou IDE durante a escrita | Máquina Virtual Java (JVM) com o sistema ativo |
| Momento de Descoberta | Antes da geração do Bytecode e do empacotamento | Durante a execução em ambiente de produção |
| Custo de Correção | Muito baixo (resolvido em segundos pelo desenvolvedor) | Muito alto (indisponibilidade, prejuízo financeiro) |
| Causas Típicas | Sintaxe inválida, tipos incompatíveis, referências não resolvidas | Divisão inteira por zero, ponteiro nulo, estouro de índices |

Erros de compilação impedem a geração dos arquivos `.class`, agindo como uma barreira inicial de qualidade. Falhas de tempo de execução (*runtime*), por outro lado, surgem após o código já estar em produção, demandando arquiteturas resilientes e capacidade de diagnóstico rápido.

### A Árvore Taxonômica de java.lang.Throwable

Na JVM, qualquer condição anormal que interrompa o fluxo ordinário de instruções é representada como uma instância de classe alocada no Heap, cuja raiz comum é `java.lang.Throwable`:

```plaintext
                           java.lang.Object
                                  ▲
                                  │
                         java.lang.Throwable
                                  ▲
            ┌─────────────────────┴─────────────────────┐
            │                                           │
     java.lang.Error                   java.lang.Exception
 (Falhas Críticas de Ambiente)               (Anomalias da Aplicação)
            │                                           ▲
      ┌─────┴─────┐                         ┌───────────┴───────────┐
      │           │                         │                       │
 OutOfMemory   StackOverflow         RuntimeException       Demais Exceptions
    Error         Error           (Falhas Lógicas Não-     (Exceções Checadas de
                                   Checadas - Unchecked)    Ambiente - Checked)
```

- **`java.lang.Error` (Irrecuperáveis):** Condições graves do ambiente de execução da JVM. A aplicação não deve tentar capturá-las com blocos `catch` convencionais. Exemplos clássicos incluem o esgotamento do Heap (`OutOfMemoryError`) e o transbordamento da área de frames da pilha por recursão infinita (`StackOverflowError`).
- **`java.lang.Exception` (Recuperáveis):** Anomalias originadas pela lógica da aplicação ou por interações com recursos externos. Devem ser previstas, tratadas ou registradas pelo software sem interromper a execução do processo global.
- **`RuntimeException` (Não-Checadas / Unchecked):** Subclasse de `Exception` que representa violações de contrato ou erros lógicos do próprio programador (como acessar uma referência nula ou passar argumentos ilegais). O compilador não obriga a inclusão de cláusulas `try-catch` ou declarações `throws` para elas.

### A Mecânica da Pilha de Chamadas (Call Stack) e o Stack Unwinding

Durante o ciclo de execução, a JVM empilha um novo quadro (*Stack Frame*) para cada método acionado pela thread ativa:

```plaintext
main() → processarVenda() → validarCodigo() → String.trim()
```

Quando uma linha de código dispara uma anomalia (como chamar um método em uma referência `null`):

1. A JVM congela a linha executada naquele momento e instancia um objeto derivado de `Throwable` no Heap.
2. Esse objeto fotografa todos os frames da pilha ativos naquele milissegundo, preservando nomes de classes, métodos, arquivos e números de linha.
3. **Desenrolamento da Pilha (*Stack Unwinding*):** A JVM inicia uma busca retroativa por um bloco `catch` correspondente. O quadro do método atual é destruído e o controle retorna para o método chamador. Se nenhum método na cadeia capturar a exceção, a thread é finalizada com erro e a JVM imprime o *Stack Trace* no fluxo `System.err`.

### Decodificando um Stack Trace Corporativo

Considere a captura de um incidente técnico em um serviço de checkout:

```plaintext
Exception in thread "main" java.lang.NullPointerException: Cannot invoke "String.trim()" because "codigo" is null
    at br.edu.modulo3.aula08.ServicoVenda.validarCodigo(ServicoVenda.java:24)
    at br.edu.modulo3.aula08.ServicoVenda.processarVenda(ServicoVenda.java:12)
    at br.edu.modulo3.aula08.CheckoutApp.main(CheckoutApp.java:8)
```

Para isolar a causa raiz em ambientes corporativos de forma eficiente, responda a três perguntas estruturadas:

1. **O que aconteceu?** Analise o cabeçalho: a classe da falha (`NullPointerException`) e a mensagem técnica descritiva (`Cannot invoke "String.trim()" because "codigo" is null`).
2. **Onde exatamente ocorreu o disparo primário?** Localize a linha do topo da cadeia de chamadas: `ServicoVenda.java:24`, dentro da rotina `validarCodigo`.
3. **Como o fluxo da aplicação chegou até lá?** Reconstrua o caminho de trás para frente a partir do ponto de entrada: `CheckoutApp.main` (linha 8) → `processarVenda` (linha 12) → `validarCodigo` (linha 24).

**Diretriz Prática:** Em rastreios longos contendo dezenas de linhas de infraestrutura ou servidores de aplicação, ignore quadros externos e localize a primeira linha do topo que pertença a um pacote desenvolvido pelo seu time.

### Catálogo de Exceções Não-Checadas Mais Frequentes

- **`NullPointerException` (NPE):** Ocorre ao tentar desreferenciar, invocar métodos ou ler propriedades a partir de uma variável que aponta para `null`.
- **`ArithmeticException`:** Ocorre em operações matemáticas inválidas no domínio dos números inteiros (como divisão inteira por zero).
- **`ArrayIndexOutOfBoundsException`:** Tentativa de acessar um índice negativo ou maior/igual ao tamanho total alocado em um array primitivo.
- **`NumberFormatException`:** Falha lançada por classes utilitárias (ex.: `Integer.parseInt`) ao receberem textos contendo letras ou formatos incompatíveis com números.
- **`ClassCastException`:** Ocorre quando o código força a conversão explícita (*cast*) de uma referência para um tipo concreto incompatível no Heap.

## 3. Estudo de Caso Integrado: Auditoria Forense com StackTraceElement

O exemplo abaixo intercepta exceções ocorridas durante o processamento de vendas e usa a inspeção programática dos quadros da pilha para estruturar um laudo técnico sem derrubar o sistema:

```java
package br.edu.universidade.sistema.auditoria;

public class ServicoAuditoriaForense {

    public static void emitirLaudoTecnico(Throwable falha) {
        System.err.println("================ LAUDO DE INCIDENTE TÉCNICO ================");
        System.err.println("Classe da Exceção : " + falha.getClass().getName());
        System.err.println("Mensagem Descritiva: " + falha.getMessage());

        // Inspeção programática dos quadros da pilha através de StackTraceElement
        StackTraceElement[] frames = falha.getStackTrace();

        if (frames.length > 0) {
            StackTraceElement pontoFalha = frames[0]; // Topo da pilha: causa primária
            System.err.println("--- PONTO DE ORIGEM PRIMÁRIO ---");
            System.err.println("Classe Afetada   : " + pontoFalha.getClassName());
            System.err.println("Método Violado   : " + pontoFalha.getMethodName() + "()");
            System.err.println("Arquivo Fonte    : " + pontoFalha.getFileName());
            System.err.println("Linha do Disparo : " + pontoFalha.getLineNumber());
        }

        System.err.println("--- CADEIA DE RASTREIO RESUMIDA ---");
        for (int i = 0; i < Math.min(frames.length, 3); i++) {
            System.err.printf("  [Frame #%d] %s.%s() -> Linha %d%n",
                    i, frames[i].getClassName(), frames[i].getMethodName(), frames[i].getLineNumber());
        }
        System.err.println("============================================================");
    }
}
```

```java
package br.edu.universidade.sistema.auditoria;

public class CheckoutApp {

    public static void main(String[] args) {
        try {
            processarTransacao(null, "1234-A");
        } catch (RuntimeException ex) {
            // Em vez de finalizar a JVM, isolamos a falha e emitimos o diagnóstico
            ServicoAuditoriaForense.emitirLaudoTecnico(ex);
        }

        System.out.println("O sistema continuou executando normalmente após o diagnóstico.");
    }

    public static void processarTransacao(String codigoItem, String contaDestino) {
        validarParametros(codigoItem);
        System.out.println("Transação autorizada com sucesso!");
    }

    public static void validarParametros(String codigo) {
        // Disparo deliberado de NullPointerException na invocação de trim()
        if (codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("O código não pode estar vazio.");
        }
    }
}
```

## 4. Diagnóstico de Erros Comuns e Armadilhas

### Armadilha 1: Capturar java.lang.Error em Blocos catch

**Código Problemático:**

```java
try {
    executarProcessamentoPesado();
} catch (Error erro) { // MÁ PRÁTICA ARQUITETURAL!
    System.out.println("Erro de memória contido!");
}
```

- **Diagnóstico Técnico:** Falhas que estendem `Error` (como `OutOfMemoryError`) indicam que a própria JVM está em estado instável ou com memória corrompida. Tentar capturá-las mascara o esgotamento da aplicação, gerando comportamentos imprevisíveis. Trate apenas exceções derivadas de `Exception`.

### Armadilha 2: Tratar Exceções com Blocos Vazios (Swallowing Exceptions)

**Código Problemático:**

```java
try {
    int divisor = 0;
    int valor = 100 / divisor;
} catch (ArithmeticException e) {
    // Bloco vazio: a exceção foi engolida silenciosamente!
}
```

- **Impacto:** A falha ocorre, mas nenhuma informação é registrada no console ou log técnico. Localizar a origem de um dado corrompido que decorreu dessa operação torna-se extremamente custoso.
- **Correção:** Sempre registre a falha nos fluxos de erro ou relance uma exceção contendo a causa associada.

### Armadilha 3: Ignorar a Causa Raiz em Falhas Encadeadas

- **Cenário:** O log apresenta múltiplas seções `Caused by: ...` abaixo do primeiro rastreio.
- **Diagnóstico Técnico:** Em arquiteturas corporativas, exceções de baixo nível (como um erro de conexão) são frequentemente empacotadas dentro de exceções de domínio de mais alto nível.
- **Correção:** Role até a última cláusula `Caused by:` do relatório técnico para localizar o evento de hardware ou infraestrutura que iniciou a sequência de falhas na aplicação.

## 5. Roteiro Prático de Depuração: Acompanhando a Pilha na IDE

Para visualizar a criação dos frames e o processo de desenrolamento (*Stack Unwinding*) utilizando o depurador da sua IDE (IntelliJ IDEA, Eclipse ou VS Code):

1. No código de `CheckoutApp`, posicione um ponto de interrupção (*breakpoint*) dentro do método `validarParametros` na linha `if (codigo.trim().isEmpty())`.
2. Inicie a execução em modo de depuração (*Debug*).
3. Acesse a janela *Frames / Debugger*:
   - Observe a lista de métodos empilhados verticalmente: `validarParametros`, precedido por `processarTransacao`, que por sua vez foi acionado por `main`.
   - Selecione cada um dos quadros empilhados: veja que as variáveis locais de cada escopo ficam preservadas na Stack enquanto aguardam a conclusão do método no topo.
4. Execute um avanço passo a passo (*Step Over*) na linha de desreferenciação nula: veja a JVM interromper o fluxo sequencial e iniciar o salto reverso (*unwinding*), desempilhando os quadros até alcançar o bloco `catch` em `main`.

## 6. Exercício de Fixação Prática: Módulo Diagnóstico de Transações Financeiras

Construa uma aplicação de diagnóstico técnico capaz de simular incidentes e extrair relatórios estruturados:

1. **Construa a Classe `SimuladorTransacoes`:**
   - Crie o método `public static void efetuarDivisaoLucros(int totalLucro, int totalSocios)` que realize a divisão inteira entre os valores (disparando `ArithmeticException` caso o número de sócios seja zero).
   - Crie o método `public static void converterChaveAcesso(String chave)` que converta uma chave alfanumérica via `Integer.parseInt(chave)` (disparando `NumberFormatException` caso haja caracteres não numéricos).
   - Crie o método `public static void validarAssinaturaDigital(String assinatura)` que invoque `.toUpperCase()` em uma `String` (disparando `NullPointerException` se a assinatura for passada como `null`).

2. **Construa a Classe Executável `AuditoriaIncidentesApp`:**
   - Execute cada um dos métodos do simulador dentro de blocos `try-catch` independentes.
   - Em cada captura de falha, acione o método `ServicoAuditoriaForense.emitirLaudoTecnico(ex)` desenvolvido nesta aula.
   - Garanta que a aplicação execute os três testes em sequência sem abortar a execução global, comprovando que o isolamento de falhas manteve a aplicação estável mesmo após múltiplos incidentes de tempo de execução.