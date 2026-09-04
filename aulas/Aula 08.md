# Plano de Aula e Roteiro de Slides: Aula 08

**Disciplina:** Programação Orientada a Objetos  
**Curso:** Análise e Desenvolvimento de Sistemas (3º Período)  
**Tema:** Diagnóstico de Falhas, Stack Traces e a Hierarquia `Throwable`  
**Carga Horária:** 100 minutos (2 horas-aula)  
**Professor:** Alexandre Neves Louzada

---

## 1. Plano de Ensino da Aula

### 1.1. Objetivos de Aprendizagem
* **Conceitual:** Compreender a taxonomia de erros e exceções na JVM; diferenciar erros de compilação (*compile-time*) de falhas em tempo de execução (*runtime*); entender a separação entre falhas estruturais irrecuperáveis (`java.lang.Error`) e anomalias tratáveis da aplicação (`java.lang.Exception`).
* **Arquitetural:** Mapear o ciclo de vida da pilha de chamadas (*Call Stack*), o mecanismo de congelamento de estado da JVM e o processo de desenrolamento da pilha (*Stack Unwinding*) no momento em que uma falha é arremessada.
* **Técnico:** Ler, interpretar e dissecar logs de *Stack Trace* com precisão cirúrgica de causa raiz (*Root Cause Analysis*); inspecionar programaticamente a cadeia de quadros através do array `StackTraceElement[]`.
* **Prático:** Implementar uma aplicação de auditoria e diagnóstico forense capaz de interceptar exceções deliberadas e emitir laudos técnicos estruturados de incidentes no console.

### 1.2. Metodologia Ativa
* **Análise Forense de Código (Bug Hunting):** Apresentação de três logs reais de *Stack Trace* de sistemas corporativos em produção para que os alunos, em duplas, identifiquem a classe causadora, o método, a linha exata e a precondição violada antes de inspecionar o código.
* **Depuração Guiada no Modo Debug:** Provocação deliberada de falhas em cadeia para visualizar na aba *Debugger* da IDE os quadros da pilha sendo empilhados e destruídos pelo desenrolamento.

---

## 2. Roteiro Detalhado de Slides

---

### Slide 1: Abertura e Visão Geral
* **Título do Slide:** Engenharia de Confiabilidade: Diagnóstico de Falhas e a Taxonomia de Erros na JVM
* **Tópicos Visuais:**
  * Abertura do Módulo 3: Robustez de Software e Tratamento de Exceções.
  * O ciclo de vida do software: por que sistemas em produção falham?
  * Erros de Compilação (*Compile-time*) vs. Falhas de Execução (*Runtime*).
  * A arquitetura da pilha de chamadas e o *Stack Trace*.
  * O mapa taxonômico da árvore `java.lang.Throwable`.
* **Notas Pedagógicas do Professor:**
  * Estabelecer a transição de mentalidade: no Módulo 2 aprendemos a modelar o mundo ideal orientado a objetos; no Módulo 3 aprendemos como lidar com o "mundo real", onde conexões caem, usuários digitam dados inválidos e recursos de hardware se esgotam.
  * Enfatizar para ADS: em ambientes corporativos e microsserviços, saber ler um *Stack Trace* com rapidez reduz o *Mean Time to Recovery* (MTTR) em incidentes críticos.

---

### Slide 2: Erros de Compilação vs. Erros de Execução
* **Título do Slide:** Dois Momentos, Duas Naturezas de Falha
* **Tópicos Visuais:**
  * Tabela Comparativa:

| Característica | Erros de Compilação (*Compile-Time*) | Erros de Execução (*Runtime Exceptions*) |
| :--- | :--- | :--- |
| **Detectado por** | Compilador (`javac`) / IDE | Máquina Virtual Java (JVM) |
| **Momento** | Antes do software ser empacotado | Com o sistema em produção rodando |
| **Custo de Correção** | Quase nulo (segundos/minutos na IDE) | Elevado (parada de serviço, perda financeira) |
| **Causas Típicas** | Sintaxe incorreta, tipos incompatíveis | Ponteiro nulo, divisão por zero, rede fora |

* **Notas Pedagógicas do Professor:**
  * Usar a analogia clássica: o erro de compilação é o revisor gramatical de uma editora apontando um erro no texto do livro; o erro de execução é a ponte estourar na primeira viagem do caminhão.

---

### Slide 3: A Árvore Taxonômica de `java.lang.Throwable`
* **Título do Slide:** A Hierarquia Universal de Falhas no Java
* **Tópicos Visuais:**
  * Diagrama da Hierarquia:
    ```
                           java.lang.Object
                                  ▲
                                  │
                         java.lang.Throwable
                                  ▲
            ┌─────────────────────┴─────────────────────┐
            │                                           │
     java.lang.Error                         java.lang.Exception
     (Falhas Críticas JVM)                   (Falhas da Aplicação)
            │                                           ▲
      ┌─────┴─────┐                         ┌───────────┴───────────┐
      │           │                         │                       │
  OutOfMemory  StackOverflow       RuntimeException        Demais Exceptions
                                 (Unchecked - Lógica)     (Checked - Ambiente)
    ```
* **Notas Pedagógicas do Professor:**
  * Ressaltar a raiz: em Java, toda falha de sistema é um **objeto** instanciado no Heap que herda direta ou indiretamente de `Throwable`.
  * Explicar que a ramificação divide imediatamente o que é problema da infraestrutura (`Error`) do que é tratável pela aplicação (`Exception`).

---

### Slide 4: `Error` vs. `Exception`
* **Título do Slide:** Falhas Críticas do Ambiente vs. Exceções da Aplicação
* **Tópicos Visuais:**
  * **`java.lang.Error` (Irrecuperáveis):**
    * Condições anormais graves do ambiente da JVM.
    * A aplicação **não deve** tentar capturar via `catch`.
    * Exemplos: `OutOfMemoryError` (Heap esgotado), `StackOverflowError` (recursão infinita estourando os frames da Stack).
  * **`java.lang.Exception` (Recuperáveis):**
    * Condições que uma aplicação bem projetada pode prever, interceptar, registrar e recuperar sem derrubar o processo.
* **Notas Pedagógicas do Professor:**
  * Fazer uma demonstração ao vivo: executar um método com recursão infinita (`void recursivo() { recursivo(); }`) e mostrar a JVM abortando com `StackOverflowError`.

---

### Slide 5: Anatomia da Pilha de Execução (*Call Stack*)
* **Título do Slide:** A Mecânica da Pilha de Chamadas e o *Stack Unwinding*
* **Tópicos Visuais:**
  * **Empilhamento de Frames:**
    $$main() \xrightarrow{chama} processarTransacao() \xrightarrow{chama} validarCpf() \xrightarrow{chama} Integer.parseInt()$$
  * **O Momento do Disparo da Falha:**
    1. Uma falha ocorre dentro de `parseInt()`.
    2. A JVM congela a linha atual e monta o objeto da exceção capturando o estado de todos os quadros (*frames*) ativos na Stack.
    3. **Desenrolamento (*Stack Unwinding*):** A JVM percorre a pilha de trás para frente buscando um bloco de tratamento (`catch`). Se ninguém capturar, a thread morre e imprime o *Stack Trace*.
* **Notas Pedagógicas do Professor:**
  * Desenhar a pilha no quadro para deixar claro que a execução vai do método mais externo para o mais interno, mas a propagação da exceção caminha do mais interno para o mais externo.

---

### Slide 6: Decodificando um *Stack Trace* Real
* **Título do Slide:** Análise Forense de Falhas: O Roteiro de Leitura
* **Tópicos Visuais:**
  * Exemplo de Log de Produção:
    ```text
    Exception in thread "main" java.lang.NullPointerException: Cannot invoke "String.trim()" because "codigo" is null
        at br.edu.modulo3.aula08.ServicoVenda.validarCodigo(ServicoVenda.java:24)
        at br.edu.modulo3.aula08.ServicoVenda.processarVenda(ServicoVenda.java:12)
        at br.edu.modulo3.aula08.CheckoutApp.main(CheckoutApp.java:8)
    ```
  * **As Três Perguntas Fundamentais:**
    1. **O quê aconteceu?** Tipo da exceção (`NullPointerException`) e mensagem de detalhe.
    2. **Onde exatamente estourou?** Primeira linha do rastreio (`ServicoVenda.java:24`).
    3. **Como o fluxo chegou lá?** Cadeia de métodos (`CheckoutApp.main` $\rightarrow$ `processarVenda` $\rightarrow$ `validarCodigo`).
* **Notas Pedagógicas do Professor:**
  * Ensinar a regra prática de mercado: ao abrir um log com 80 linhas de stack trace, ignore as linhas de frameworks (Spring, Tomcat) e busque a **primeira linha do topo que pertença a um pacote escrito pelo seu time** (`br.edu...`).

---

### Slide 7: Galeria das Exceções Não-Checadas (*Runtime*) Mais Comuns
* **Título do Slide:** O Catálogo Clássico de Falhas de Execução
* **Tópicos Visuais:**
  * `NullPointerException` (NPE): Tentativa de acessar membro ou método a partir de uma referência que aponta para `null`.
  * `ArithmeticException`: Violação de regra matemática (ex.: divisão inteira por zero).
  * `ArrayIndexOutOfBoundsException` / `IndexOutOfBoundsException`: Acesso a índice inexistente em arrays ou listas.
  * `NumberFormatException`: Falha ao tentar converter texto alfanumérico para formato numérico.
  * `ClassCastException`: Tentativa de coerção forçada (*cast*) entre tipos incompatíveis no Heap.
* **Notas Pedagógicas do Professor:**
  * Destacar que quase 100% das `RuntimeException` indicam **falha de lógica do programador** ou ausência de validação defensiva prévia, e não falhas imprevisíveis de infraestrutura.

---

### Slide 8: Inspecionando Programaticamente o `StackTraceElement`
* **Título do Slide:** Engenharia de Observabilidade: Extraindo Metadados da Pilha
* **Tópicos Visuais:**
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