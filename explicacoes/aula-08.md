# Explicação da Aula 08 -- Diagnóstico de Falhas, Stack Traces e a Hierarquia Throwable

| | |
|---|---|
| **Disciplina** | Programação Orientada a Objetos |
| **Curso** | Análise e Desenvolvimento de Sistemas (3º Período) |
| **Tema** | Diagnóstico de Falhas, *Stack Traces* e a Hierarquia `Throwable` |
| **Carga Horária** | 100 minutos (2 horas-aula) |
| **Professor** | Alexandre Neves Louzada |
| **Plano de Aula** | `Aulas 3pob/Aula 08.md` |
| **Tutorial** | `aulas/TutorialAula8.md` |
| **Estudo de Caso** | `exemplos/aula-08/` |
| **Exercícios Resolvidos** | `solucoes/aula-08/` |

## 1. Objetivos de Aprendizagem

Esta aula inaugura o Módulo 3 do curso (Robustez de Software e Tratamento de Exceções). Ela ensina a diagnosticar falhas em tempo de execução, interpretar logs de *Stack Trace* e compreender a taxonomia de erros da JVM.

**Objetivos conceituais:** Compreender a taxonomia de erros e exceções na JVM; diferenciar erros de compilação (*compile-time*) de falhas em tempo de execução (*runtime*); entender a separação entre falhas estruturais irrecuperáveis (`java.lang.Error`) e anomalias tratáveis da aplicação (`java.lang.Exception`).

**Objetivos arquiteturais:** Mapear o ciclo de vida da pilha de chamadas (*Call Stack*), o mecanismo de congelamento de estado da JVM e o processo de desenrolamento da pilha (*Stack Unwinding*) no momento em que uma falha é arremessada.

**Objetivos técnicos:** Ler, interpretar e dissecar logs de *Stack Trace* com precisão cirúrgica de causa raiz (*Root Cause Analysis*); inspecionar programaticamente a cadeia de quadros através do array `StackTraceElement[]`.

**Objetivos práticos:** Implementar uma aplicação de auditoria e diagnóstico forense capaz de interceptar exceções deliberadas e emitir laudos técnicos estruturados de incidentes no console.

### 1.1 Metodologia Ativa

A aula utiliza **Análise Forense de Código (*Bug Hunting*)** com três logs reais de *Stack Trace* de sistemas corporativos em produção, e **Depuração Guiada no Modo Debug** com provocação deliberada de falhas em cadeia para visualizar na aba *Debugger* da IDE os quadros da pilha sendo empilhados e destruídos pelo desenrolamento.

## 2. Conteúdo Teórico Detalhado

### 2.1 Erros de Compilação vs. Erros de Execução

Existem dois momentos fundamentalmente distintos em que um programa Java pode apresentar falhas. A tabela comparativa abaixo esclarece as diferenças:

| Característica | Erros de Compilação (*Compile-Time*) | Erros de Execução (*Runtime Exceptions*) |
| :--- | :--- | :--- |
| **Detectado por** | Compilador (`javac`) / IDE | Máquina Virtual Java (JVM) |
| **Momento** | Antes do software ser empacotado | Com o sistema em produção rodando |
| **Custo de Correção** | Quase nulo (segundos/minutos na IDE) | Elevado (parada de serviço, perda financeira) |
| **Causas Típicas** | Sintaxe incorreta, tipos incompatíveis | Ponteiro nulo, divisão por zero, rede fora |

O erro de compilação é como um revisor gramatical apontando um erro no texto: é detectado antes de o livro ser impresso. O erro de execução é como uma ponte estourar na primeira viagem do caminhão: só é percebido quando o sistema já está em operação.

### 2.2 A Árvore Taxonômica de `java.lang.Throwable`

Em Java, toda falha de sistema é um **objeto** instanciado no *Heap* que herda direta ou indiretamente de `java.lang.Throwable`. A hierarquia se divide em dois grandes ramos:

```
                       java.lang.Object
                              ^
                              |
                     java.lang.Throwable
                              ^
            +-----------------+-----------------+
            |                                   |
     java.lang.Error                   java.lang.Exception
     (Falhas Críticas JVM)             (Falhas da Aplicação)
            |                                   ^
      +-----+-----+                 +----------+----------+
      |           |                 |                      |
  OutOfMemory  StackOverflow   RuntimeException        Demais Exceptions
                               (Unchecked - Lógica)     (Checked - Ambiente)
```

A raiz `Throwable` é o ancestral comum de todas as falhas. A ramificação imediata separa o que é problema da infraestrutura (`Error`) do que é tratável pela aplicação (`Exception`).

### 2.3 `Error` vs. `Exception`

**`java.lang.Error` (Irrecuperáveis):** São condições anormais graves do ambiente da JVM. A aplicação **não deve** tentar capturar via `catch`. Exemplos clássicos:

- `OutOfMemoryError`: *Heap* esgotado. A JVM não consegue alocar mais memória para nenhum objeto.
- `StackOverflowError`: Recursão infinita estourando os *frames* da *Stack*.

Demonstração ao vivo: executar um método com recursão infinita:

```java
void recursivo() {
    recursivo(); // Chama a si mesmo infinitamente
}
```

A JVM aborta com `StackOverflowError` após atingir o limite de profundidade da pilha.

**`java.lang.Exception` (Recuperáveis):** São condições que uma aplicação bem projetada pode prever, interceptar, registrar e recuperar sem derrubar o processo.

### 2.4 A Mecânica da Pilha de Chamadas (*Call Stack*)

Quando um método é chamado, a JVM cria um novo **frame** (quadro) na pilha de chamadas e o empilha. Cada *frame* contém as variáveis locais, os parâmetros e o endereço de retorno daquele método.

Exemplo de empilhamento:

> `main()` -> `processarTransacao()` -> `validarCpf()` -> `Integer.parseInt()`

Quando uma falha ocorre dentro de `parseInt()`, a JVM:

1. Congela a linha atual e monta o objeto da exceção, capturando o estado de todos os quadros (*frames*) ativos na *Stack*.
2. Inicia o **Desenrolamento (*Stack Unwinding*)**: percorre a pilha de trás para frente buscando um bloco de tratamento (`catch`).
3. Se ninguém capturar, a *thread* morre e o *Stack Trace* é impresso no console.

A execução vai do método mais externo para o mais interno, mas a **propagação da exceção** caminha do mais interno para o mais externo.

### 2.5 Decodificando um *Stack Trace* Real

Exemplo de log de produção:

```text
Exception in thread "main" java.lang.NullPointerException: Cannot invoke "String.trim()" because "codigo" is null
    at br.edu.modulo3.aula08.ServicoVenda.validarCodigo(ServicoVenda.java:24)
    at br.edu.modulo3.aula08.ServicoVenda.processarVenda(ServicoVenda.java:12)
    at br.edu.modulo3.aula08.CheckoutApp.main(CheckoutApp.java:8)
```

As **três perguntas fundamentais** para análise:

1. **O que aconteceu?** Tipo da exceção (`NullPointerException`) e mensagem de detalhe (`Cannot invoke "String.trim()" because "codigo" is null`).
2. **Onde exatamente estourou?** Primeira linha do rastreio (`ServicoVenda.java:24`).
3. **Como o fluxo chegou lá?** Cadeia de métodos (`CheckoutApp.main` -> `processarVenda` -> `validarCodigo`).

Regra prática de mercado: ao abrir um log com 80 linhas de *stack trace*, ignore as linhas de *frameworks* (Spring, Tomcat) e busque a **primeira linha do topo que pertença a um pacote escrito pelo seu time** (`br.edu...`).

### 2.6 Galeria das Exceções Não-Checadas (*RuntimeException*) Mais Comuns

| Exceção | Descrição | Causa Típica |
| :--- | :--- | :--- |
| `NullPointerException` | Tentativa de acessar membro ou método a partir de uma referência `null` | Variável não inicializada ou retorno nulo não tratado |
| `ArithmeticException` | Violação de regra matemática | Divisão inteira por zero |
| `ArrayIndexOutOfBoundsException` | Acesso a índice inexistente em array | Iteração fora dos limites |
| `IndexOutOfBoundsException` | Acesso a índice inexistente em lista | Posição inválida em `ArrayList` |
| `NumberFormatException` | Falha ao converter texto para formato numérico | `Integer.parseInt("abc")` |
| `ClassCastException` | Coerção forçada entre tipos incompatíveis no *Heap* | Cast incorreto de superclass para subclasse |

Quase 100% das `RuntimeException` indicam **falha de lógica do programador** ou ausência de validação defensiva prévia, e não falhas imprevisíveis de infraestrutura.

### 2.7 Inspecionando Programaticamente o `StackTraceElement`

A classe `java.lang.StackTraceElement` permite inspecionar programaticamente cada quadro da pilha de chamadas:

```java
try {
    // Operação arriscada
} catch (Exception ex) {
    System.err.println("Classe da Falha: " + ex.getClass().getName());
    System.err.println("Mensagem: " + ex.getMessage());

    // Inspecionando o topo da pilha via código
    if (ex.getStackTrace().length > 0) {
        StackTraceElement frame = ex.getStackTrace()[0];
        System.err.printf("Local do Incidente: %s.%s() na linha %d do arquivo %s%n",
                frame.getClassName(), frame.getMethodName(), frame.getLineNumber(), frame.getFileName());
    }
}
```

Cada `StackTraceElement` fornece métodos como `getClassName()`, `getMethodName()`, `getLineNumber()` e `getFileName()`, permitindo extrair metadados precisos para auditoria automatizada.

## 3. Estudo de Caso Aplicado

O estudo de caso desta aula é um **serviço de auditoria forense** localizado em `exemplos/aula-08/src/br/edu/universidade/sistema/auditoria/`. Ele é composto por dois arquivos.

### 3.1 ServicoAuditoriaForense.java

```java
package br.edu.universidade.sistema.auditoria;

public class ServicoAuditoriaForense {

    public static void emitirLaudoTecnico(Throwable falha) {
        System.err.println("================ LAUDO DE INCIDENTE TÉCNICO ================");
        System.err.println("Classe da Exceção : " + falha.getClass().getName());
        System.err.println("Mensagem Descritiva: " + falha.getMessage());

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

O método `emitirLaudoTecnico` recebe qualquer `Throwable` e emite um laudo estruturado que inclui: nome da classe da exceção, mensagem descritiva, ponto de origem primário (topo da pilha) e cadeia de rastreio resumida (três primeiros *frames*).

### 3.2 CheckoutApp.java

```java
package br.edu.universidade.sistema.auditoria;

public class CheckoutApp {

    public static void main(String[] args) {
        try {
            processarTransacao(null, "1234-A");
        } catch (RuntimeException ex) {
            ServicoAuditoriaForense.emitirLaudoTecnico(ex);
        }

        System.out.println("O sistema continuou executando normalmente após o diagnóstico.");
    }

    public static void processarTransacao(String codigoItem, String contaDestino) {
        validarParametros(codigoItem);
        System.out.println("Transação autorizada com sucesso!");
    }

    public static void validarParametros(String codigo) {
        if (codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("O código não pode estar vazio.");
        }
    }
}
```

O `main` chama `processarTransacao(null, ...)`. Ao tentar executar `codigo.trim()` com `codigo` sendo `null`, a JVM dispara uma `NullPointerException`. O bloco `catch` captura a falha e delega ao `ServicoAuditoriaForense` para emitir o laudo técnico. Após o diagnóstico, o programa continua sua execução normalmente -- a falha foi isolada sem derrubar a aplicação.

## 4. Exercícios Propostos e Solução

O exercício consiste em implementar um **simulador de transações** que provoca três tipos diferentes de exceções e, para cada uma, emite um laudo técnico usando o `ServicoAuditoriaForense`. A solução está em `solucoes/aula-08/src/br/edu/universidade/sistema/auditoria/`.

### 4.1 SimuladorTransacoes.java

```java
package br.edu.universidade.sistema.auditoria;

public class SimuladorTransacoes {

    public static void efetuarDivisaoLucros(int totalLucro, int totalSocios) {
        int quociente = totalLucro / totalSocios;
        System.out.printf("Divisão de lucros: R$ %d para %d sócios -> R$ %d por sócio.%n",
                totalLucro, totalSocios, quociente);
    }

    public static void converterChaveAcesso(String chave) {
        int codigoNumerico = Integer.parseInt(chave);
        System.out.println("Chave de acesso convertida para inteiro: " + codigoNumerico);
    }

    public static void validarAssinaturaDigital(String assinatura) {
        String normalizada = assinatura.toUpperCase();
        System.out.println("Assinatura digital normalizada: " + normalizada);
    }
}
```

Cada método provoca uma exceção diferente quando recebe dados inválidos:

- `efetuarDivisaoLucros(100000, 0)`: divisão inteira por zero -> `ArithmeticException`.
- `converterChaveAcesso("ABCD-1234")`: tentativa de converter texto não numérico -> `NumberFormatException`.
- `validarAssinaturaDigital(null)`: tentativa de chamar `toUpperCase()` em referência nula -> `NullPointerException`.

### 4.2 AuditoriaIncidentesApp.java

```java
package br.edu.universidade.sistema.auditoria;

public class AuditoriaIncidentesApp {
    public static void main(String[] args) {
        System.out.println("========== AUDITORIA DE INCIDENTES EM EXECUÇÃO ==========");

        System.out.println("\n--- Incidente 1: Divisão por Zero (ArithmeticException) ---");
        try {
            SimuladorTransacoes.efetuarDivisaoLucros(100000, 0);
        } catch (ArithmeticException ex) {
            ServicoAuditoriaForense.emitirLaudoTecnico(ex);
        }

        System.out.println("\n--- Incidente 2: Chave Alfanumérica (NumberFormatException) ---");
        try {
            SimuladorTransacoes.converterChaveAcesso("ABCD-1234");
        } catch (NumberFormatException ex) {
            ServicoAuditoriaForense.emitirLaudoTecnico(ex);
        }

        System.out.println("\n--- Incidente 3: Assinatura Nula (NullPointerException) ---");
        try {
            SimuladorTransacoes.validarAssinaturaDigital(null);
        } catch (NullPointerException ex) {
            ServicoAuditoriaForense.emitirLaudoTecnico(ex);
        }

        System.out.println("\nA aplicação concluiu os três testes sequenciais sem abortar a JVM.");
    }
}
```

Cada incidente é capturado individualmente em seu próprio bloco `try-catch`, permitindo que a aplicação continue processando os próximos testes mesmo após falhas. Após os três testes, a mensagem final confirma que o sistema permaneceu em execução contínua.

## 5. Perguntas de Revisão

1. Qual é a diferença fundamental entre `java.lang.Error` e `java.lang.Exception`? Por que a recomendação é não capturar `Error`?
2. Descreva o processo de *Stack Unwinding*. O que acontece com os *frames* da pilha quando uma exceção é lançada?
3. Qual é a primeira coisa que você deve procurar ao analisar um *Stack Trace* de 80 linhas em produção?
4. Por que `NullPointerException` é considerada uma falha de lógica do programador e não uma falha de infraestrutura?
5. Como funciona o array `StackTraceElement[]` e quais métodos ele oferece para auditoria programática?
6. Qual a diferença entre o momento da execução (empilhamento) e o momento da falha (desenrolamento) na pilha de chamadas?
7. Por que o `ServicoAuditoriaForense` recebe `Throwable` e não `Exception` como tipo do parâmetro?
8. O que acontece se ninguém capturar (`catch`) uma `RuntimeException` em todo o caminho da pilha?
9. Cite três exceções não-checadas clássicas e descreva a causa de cada uma.
10. Por que é importante que a aplicação continue executando após capturar uma exceção, em vez de simplesmente abortar?

## 6. Resumo / Pontos-Chave

- **Erros de compilação** são detectados antes da execução e têm custo de correção baixo. **Erros de execução** ocorrem durante a execução e podem causar parada de serviço.
- `java.lang.Throwable` é a raiz de toda a hierarquia de falhas. Ela se divide em `Error` (irrecuperáveis, não devem ser capturados) e `Exception` (tratáveis pela aplicação).
- `RuntimeException` é uma subclasse de `Exception` que agrupa falhas de lógica do programador (NPE, ArithmeticException, IndexOutOfBoundsException, etc.).
- A **pilha de chamadas** (*Call Stack*) é uma estrutura LIFO onde cada chamada de método empilha um *frame*. Quando uma exceção é lançada, a JVM percorre a pilha de trás para frente buscando um `catch`.
- **Stack Unwinding** é o processo de desenrolamento da pilha. Se ninguém capturar, a *thread* morre.
- **Stack Trace** é o log que registra a cadeia completa de chamadas até o ponto de falha. A regra prática é buscar a primeira linha pertencente ao pacote da sua aplicação.
- **`StackTraceElement`** permite inspecionar programaticamente cada *frame* da pilha, extraindo classe, método, linha e arquivo para auditoria automatizada.
- O estudo de caso demonstra um serviço de auditoria forense que intercepta exceções e emite laudos técnicos estruturados, mantendo a aplicação em execução contínua após cada incidente.
