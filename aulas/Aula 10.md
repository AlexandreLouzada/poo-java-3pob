# Plano de Aula e Roteiro de Slides: Aula 10

**Disciplina:** Programação Orientada a Objetos  
**Curso:** Análise e Desenvolvimento de Sistemas (3º Período)  
**Tema:** Sinalização Explícita de Falhas (`throw`), Contratos de Métodos (`throws`) e Criação de Exceções Customizadas de Domínio  
**Carga Horária:** 100 minutos (2 horas-aula)  
**Professor:** Alexandre Neves Louzada

---

## 1. Plano de Ensino da Aula

### 1.1. Objetivos de Aprendizagem
* **Conceitual:** Compreender o papel da sinalização semântica de falhas no design de APIs corporativas; eliminar retornos mágicos de erro (`-1`, `false`, `null`) em favor de exceções ricas de domínio; entender o princípio do desacoplamento entre camadas de serviço/negócio e camadas de apresentação.
* **Técnico:** Disparar ativamente exceções utilizando a instrução `throw`; declarar contratos de métodos com a cláusula `throws`; projetar e instanciar classes de exceção customizadas (*Checked* herdando de `Exception` e *Unchecked* herdando de `RuntimeException`).
* **Arquitetural:** Implementar a técnica de Encadeamento de Exceções (*Exception Chaining*) via construtor com `Throwable cause`, preservando a causa raiz técnica de baixo nível dentro de uma abstração elegante de negócio; enriquecer exceções customizadas com atributos contextuais imutáveis (`final`).
* **Prático:** Implementar um motor de autenticação e segurança bancária em camadas, disparando exceções específicas para usuário inexistente, credenciais incorretas e bloqueio definitivo por excesso de tentativas.

### 1.2. Metodologia Ativa
* **Modelagem Guiada de Regras de Domínio:** Apresentação de um caso financeiro real onde falhas de saldo ou limite não devem exibir mensagens no console via `System.out.println`, mas sim interromper o fluxo transacional através de exceções estruturadas com dados de auditoria.
* **Refatoração de Código em Camadas:** Transformação de métodos acoplados à interface de usuário em serviços limpos que comunicam o insucesso operacional exclusivamente via contratos de exceções customizadas.

---

## 2. Roteiro Detalhado de Slides

---

### Slide 1: Abertura e Visão Geral
* **Título do Slide:** Sinalização Explícita de Falhas e Exceções de Domínio de Negócio
* **Tópicos Visuais:**
  * O papel das exceções no design de APIs corporativas.
  * Disparando falhas ativamente: a palavra-chave `throw`.
  * Definindo contratos e delegando responsabilidade: a palavra-chave `throws`.
  * Criação de Exceções Customizadas (*Checked* e *Unchecked*).
* **Notas Pedagógicas do Professor:**
  * Contextualizar a evolução do Módulo 3: nas Aulas 08 e 09 aprendemos a diagnosticar e capturar erros existentes na plataforma. Na Aula 10, assumimos o papel de **autores de regras de negócio**, definindo quando e como o sistema deve rejeitar estados inválidos.
  * Foco de ADS: linguagens fracas usam retornos numéricos mágicos (ex.: retornar `-1` ou `false` em caso de erro), o que polui a lógica do chamador. Em Java corporativo, violações de regras de negócio são sinalizadas através de **exceções semânticas de domínio**.

---

### Slide 2: Lançando Exceções com `throw`
* **Título do Slide:** A Instrução `throw`: Interrompendo o Fluxo com Intencionalidade
* **Tópicos Visuais:**
  * **Conceito:** Comando utilizado para instanciar e arremessar explicitamente um objeto que herda de `Throwable`.
  * Sintaxe:
    ```java
    if (valor <= 0) {
        throw new IllegalArgumentException("O valor da operação deve ser positivo. Informado: " + valor);
    }
    ```
  * **Efeito imediato:** A execução do método é interrompida na linha do `throw`; a JVM congela o estado atual, monta o *Stack Trace* e inicia a busca por um tratador (`catch`) na pilha de execução.
* **Notas Pedagógicas do Professor:**
  * Diferenciar os termos técnicos para os alunos:
    * `throw` (verbo no imperativo): a ação de **arremessar** o objeto de erro.
    * `throws` (verbo no indicativo): a declaração na assinatura de que o método **pode arremessar** determinado tipo de erro.

---

### Slide 3: Delegando com `throws` (Contratos de Métodos)
* **Título do Slide:** A Cláusula `throws`: Transparência na Assinatura da API
* **Tópicos Visuais:**
  * **Propósito:** Informar aos consumidores do método quais exceções do tipo *Checked* podem ser propagadas sem tratamento local.
  * Sintaxe de declaração:
    ```java
    public void transferir(String destino, double valor) 
            throws SaldoInsuficienteException, ContaBloqueadaException {
        // Se a validação falhar, a exceção sobe para quem chamou
    }
    ```
  * **Princípio da Responsabilidade Única (SRP):** Classes de serviço/domínio não devem interagir com a tela (`System.out.println`) para avisar de erros; elas devem arremessar a exceção para que a camada de controle/apresentação decida como exibir o problema ao usuário.
* **Notas Pedagógicas do Professor:**
  * Explicar que usar `try-catch` dentro de métodos de negócio apenas para imprimir mensagem é um antipadrão clássico. A camada de negócio deve sinalizar o erro via `throw/throws`, e a camada de interface (CLI, API REST ou Web) é quem trata e exibe.

---

### Slide 4: Por que Criar Exceções Customizadas?
* **Título do Slide:** Expressividade e Semântica no Modelo de Domínio
* **Tópicos Visuais:**
  * **Limitação das exceções padrão:** Usar apenas `RuntimeException` ou `Exception` genéricas esconde o significado do problema no log.
  * **Vantagens de Exceções Customizadas:**
    1. **Semântica Clara:** O próprio nome da classe descreve a falha de negócio (`SaldoInsuficienteException`, `CredenciaisInvalidasException`, `UsuarioNaoEncontradoException`).
    2. **Tratamento Específico:** Permite capturar erros de negócio com blocos `catch` dedicados sem capturar erros genéricos indesejados.
    3. **Enriquecimento com Atributos:** Podemos anexar dados contextuais à exceção (ex.: saldo disponível, valor da tentativa de saque, login do usuário, tentativas restantes).
* **Notas Pedagógicas do Professor:**
  * Fazer uma pergunta: "O que é mais informativo no log de auditoria: `java.lang.IllegalArgumentException` ou `br.com.banco.SaldoInsuficienteException`?".
  * Discutir como frameworks de mercado (Spring Boot com `@ExceptionHandler`) utilizam essas exceções para montar automaticamente respostas HTTP 400, 404 e 422.

---

### Slide 5: Estrutura de uma Exceção Customizada
* **Título do Slide:** Implementação de Exceções de Domínio
* **Tópicos Visuais:**
  * **1. Exceção de Negócio com Dados de Contexto (Checked):**
    ```java
    public class SaldoInsuficienteException extends Exception {
        private final double saldoAtual;
        private final double valorTentativa;

        public SaldoInsuficienteException(String mensagem, double saldoAtual, double valorTentativa) {
            super(mensagem);
            this.saldoAtual = saldoAtual;
            this.valorTentativa = valorTentativa;
        }

        public double getSaldoAtual() { return saldoAtual; }
        public double getValorTentativa() { return valorTentativa; }
    }
    ```
  * **2. Exceção de Regra Crítica/Invariante (Unchecked):**
    ```java
    public class ContaBloqueadaException extends RuntimeException {
        public ContaBloqueadaException(String mensagem) {
            super(mensagem);
        }
        public ContaBloqueadaException(String mensagem, Throwable causaRaiz) {
            super(mensagem, causaRaiz); // Encadeamento de exceções (Chained Exceptions)
        }
    }
    ```
* **Notas Pedagógicas do Professor:**
  * Explicar o papel do construtor que recebe `Throwable causaRaiz`: permite o **encadeamento de exceções** (*Exception Chaining*), preservando a causa original de baixo nível (como um `SQLException` ou `IOException`) envelopada em uma exceção elegante de alto nível.

---

### Slide 6: Decisão de Arquitetura: Estender `Exception` ou `RuntimeException`?
* **Título do Slide:** Matriz de Decisão: Checked vs. Unchecked em Exceções Customizadas
* **Tópicos Visuais:**
  * Matriz Comparativa:

| Tipo de Exceção | Superclasse Base | Quando Utilizar? | Comportamento no Chamador |
| :--- | :--- | :--- | :--- |
| **Checked** | `java.lang.Exception` | O chamador **pode e deve** se recuperar da falha de forma alternativa (ex.: pedir outra forma de pagamento, tentar outro login). | Força o uso explícito de `try-catch` ou `throws` na assinatura. |
| **Unchecked** | `java.lang.RuntimeException` | Violações de pré-condições irrecuperáveis, estados ilegais ou bugs lógicos do sistema. | O código fica limpo, sem poluição de `throws` nas assinaturas. |

* **Notas Pedagógicas do Professor:**
  * Apresentar a tendência da arquitetura Java moderna: a maioria dos frameworks enterprise contemporâneos (Spring, Quarkus, Jakarta) prioriza exceções customizadas herdando de `RuntimeException` para evitar assinaturas excessivamente verbosas.
  * No entanto, exceções de regras de negócio transacionais explícitas continuam sendo excelentes candidatas a *Checked*.

---

### Slide 7: Fluxo Completo de Negócio (Camadas Desacopladas)
* **Título do Slide:** Arquitetura em Camadas: Do Lançamento à Apresentação
* **Tópicos Visuais:**
  ```java
  // Camada de Domínio/Serviço (Isolada de Console/Web)
  public class ContaBancaria {
      private double saldo;
      private boolean ativa;

      public void sacar(double valor) throws SaldoInsuficienteException {
          if (!this.ativa) {
              throw new IllegalStateException("Conta inativa para movimentação.");
          }
          if (valor > this.saldo) {
              throw new SaldoInsuficienteException("Saldo insuficiente para efetuar o saque.", this.saldo, valor);
          }
          this.saldo -= valor;
      }
  }

  // Camada de Apresentação / Controlador (Trata e Formata)
  public class CaixaEletronicoApp {
      public static void processarOperacao(ContaBancaria conta, double valor) {
          try {
              conta.sacar(valor);
              System.out.println("Saque realizado com sucesso!");
          } catch (SaldoInsuficienteException ex) {
              System.out.printf("Falha na operação: %s (Disponível: R$ %.2f | Solicitado: R$ %.2f)%n",
                      ex.getMessage(), ex.getSaldoAtual(), ex.getValorTentativa());
          } catch (IllegalStateException ex) {
              System.err.println("Atenção: " + ex.getMessage());
          }
      }
  }