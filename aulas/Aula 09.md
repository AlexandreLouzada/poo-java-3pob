# Plano de Aula e Roteiro de Slides: Aula 09

**Disciplina:** Programação Orientada a Objetos  
**Curso:** Análise e Desenvolvimento de Sistemas (3º Período)  
**Tema:** Tratamento Defensivo com `try`, `catch`, `finally`, `try-with-resources` e Exceções Checked vs. Unchecked  
**Carga Horária:** 100 minutos (2 horas-aula)  
**Professor:** Alexandre Neves Louzada

---

## 1. Plano de Ensino da Aula

### 1.1. Objetivos de Aprendizagem
* **Conceitual:** Compreender a separação estrutural entre falhas previsíveis de infraestrutura/IO checadas pelo compilador (*Checked Exceptions*) e falhas lógicas de tempo de execução não-checadas (*Unchecked / RuntimeExceptions*).
* **Técnico:** Construir blocos defensivos com `try-catch`, encadeamento seletivo de múltiplos tratadores (regra da especialização para a generalização) e uso do operador *Multi-Catch* (`catch (A | B ex)`); compreender a semântica de garantia de execução do bloco `finally`.
* **Arquitetural:** Dominar o gerenciamento moderno e determinístico de recursos do sistema operacional utilizando a instrução `try-with-resources` e o contrato da interface `java.lang.AutoCloseable`, eliminando riscos de vazamento de recursos (*resource leaks*).
* **Prático:** Implementar um processador de arquivos de lote (.csv) resiliente a falhas, capaz de isolar e reportar registros corrompidos em tempo real sem abortar o processamento dos itens válidos subsequentes.

### 1.2. Metodologia Ativa
* **Game de Refatoração (Fragilidade para Resiliência):** Apresentação de um leitor de arquivos frágil que aborta a execução no primeiro erro de formatação; os alunos, em duplas, aplicam `try-with-resources` e blocos de contingência para transformar a rotina em um fluxo resiliente de alta disponibilidade.
* **Demonstração Forense de Fechamento de Recursos:** Inspecionar no depurador da IDE a execução automática do método `close()` ao final de blocos `try-with-resources`, mesmo sob disparo de exceções.

---

## 2. Roteiro Detalhado de Slides

---

### Slide 1: Abertura e Mecanismos Defensivos
* **Título do Slide:** Tolerância a Falhas: Captura, Isolamento e Recuperação de Erros
* **Tópicos Visuais:**
  * O custo da parada de sistema: da falha silenciosa ao *crash* catastrófico.
  * A bifurcação taxonômica: Exceções Checadas (*Checked*) vs. Não-Checadas (*Unchecked*).
  * O bloco defensivo fundamental: `try`, `catch` e `finally`.
  * Gerenciamento automático de recursos da JVM: o padrão `try-with-resources`.
* **Notas Pedagógicas do Professor:**
  * Contextualizar: na Aula 08 diagnosticamos por que e onde o software quebra; na Aula 09 aprendemos como **blindar a aplicação** para que falhas previsíveis não interrompam a experiência do usuário nem derrubem o serviço.
  * Ressaltar a perspectiva de ADS: em sistemas corporativos, uma falha de conexão ou de leitura de arquivo deve gerar fallback ou mensagem amigável, nunca uma tela de erro técnico exposta ao usuário.

---

### Slide 2: Exceções *Checked* vs. *Unchecked*
* **Título do Slide:** A Grande Divisão: Checadas pelo Compilador vs. Falhas de Runtime
* **Tópicos Visuais:**
  * Matriz Comparativa:

| Critério | Exceções *Checked* (Checadas) | Exceções *Unchecked* (Não-Checadas) |
| :--- | :--- | :--- |
| **Herança direta** | Subclasses de `java.lang.Exception` (exceto `RuntimeException`) | Subclasses de `java.lang.RuntimeException` |
| **Papel do Compilador** | **Obrigatório** tratar (`try-catch`) ou declarar (`throws`) | **Opcional** tratar em tempo de compilação |
| **Natureza da falha** | Condições externas, de infraestrutura ou de IO previsíveis | Falhas de lógica do programador ou violações de contrato |
| **Exemplos típicos** | `IOException`, `SQLException`, `FileNotFoundException` | `NullPointerException`, `IllegalArgumentException`, `IndexOutOfBoundsException` |

* **Notas Pedagógicas do Professor:**
  * Enfatizar a regra mnemônica para os alunos: se o código lida com o "mundo exterior" (arquivos, banco de dados, rede, periféricos), o compilador Java impõe a checagem obrigatória (*Checked*).
  * Se a falha é derivada de um ponteiro nulo ou índice inválido (*Unchecked*), ela deveria ter sido evitada com código defensivo antes de ocorrer.

---

### Slide 3: A Estrutura Básica do `try-catch`
* **Título do Slide:** Interceptando Falhas: Os Blocos `try` e `catch`
* **Tópicos Visuais:**
  ```java
  try {
      // Código monitorado que pode disparar uma exceção
      System.out.println("Iniciando operação de conversão...");
      int idade = Integer.parseInt(entradaUsuario);
      System.out.println("Idade processada com sucesso: " + idade);
  } catch (NumberFormatException ex) {
      // Bloco de contingência executado APENAS se a exceção ocorrer
      System.err.println("Entrada inválida! Informe apenas dígitos numéricos.");
      System.err.println("Log técnico: " + ex.getMessage());
  }