# Plano de Aula e Roteiro de Slides: Aula 01

**Disciplina:** Programação Orientada a Objetos  
**Curso:** Análise e Desenvolvimento de Sistemas (3º Período)  
**Tema:** Paradigmas de Programação (Procedural vs. OO) e o Ecossistema Java  
**Carga Horária:** 100 minutos (2 horas-aula)  
**Professor:** Alexandre Neves Louzada

---

## 1. Plano de Ensino da Aula

### 1.1. Objetivos de Aprendizagem
* **Conceitual:** Diferenciar acoplamento e separação entre dados e funções (modelo procedural/estruturado) do encapsulamento coeso de estado e comportamento (modelo orientado a objetos).
* **Técnico:** Compreender o pipeline de compilação e execução da plataforma Java (JDK, JRE, JVM, Bytecode e compilação JIT).
* **Operacional:** Executar manualmente o ciclo de compilação (`javac`), execução (`java`) e inspeção de instruções de pilha (`javap -c`) no terminal sem dependência de IDEs.
* **Arquitetural:** Compreender a organização da memória de tempo de execução da JVM (separação entre frames da Stack e alocação dinâmica no Heap) e o papel do Garbage Collector (GC).

### 1.2. Metodologia Ativa
* **Peer Instruction:** Apresentação de um caso de violação de regra de negócio em código procedural e discussão em pares sobre como o encapsulamento transfere a responsabilidade da integridade para a própria entidade.
* **Laboratório Hand-on / CLI:** Desmistificação da "mágica" das IDEs através da compilação e inspeção de bytecode diretamente no terminal do sistema operacional.

---

## 2. Roteiro Detalhado de Slides

---

### Slide 1: Abertura e Visão Geral
* **Título do Slide:** Paradigmas de Programação & Arquitetura da Plataforma Java
* **Tópicos Visuais:**
  * Da computação orientada a ações/funções para a computação orientada a modelos de domínio.
  * O ecossistema Java: JDK, JRE, JVM e a filosofia *Write Once, Run Anywhere* (WORA).
  * Gestão de memória na JVM: Stack, Heap e o papel do Garbage Collector.
* **Notas Pedagógicas do Professor:**
  * Contextualizar o perfil de ADS: o foco no 3º período não é apenas "fazer o código compilar", mas projetar software manutenível, coeso e preparado para regras corporativas complexas.
  * Estabelecer o objetivo da disciplina: transição estruturada do pensamento algorítmico básico (C/Python estruturado) para a engenharia de software orientada a objetos em Java.

---

### Slide 2: O Paradigma Procedural / Estruturado
* **Título do Slide:** Paradigma Procedural: Foco no Algoritmo e no Fluxo de Execução
* **Tópicos Visuais:**
  * $Programa = Algoritmos + Estruturas\ de\ Dados$ (Niklaus Wirth).
  * Separação estrita entre **dados** (`structs`, variáveis globais e locais) e **comportamento** (funções, procedimentos).
  * Diagrama de fluxo sequencial: chamadas encadeadas de sub-rotinas manipulando estruturas passadas como parâmetro.
* **Notas Pedagógicas do Professor:**
  * Relembrar a experiência dos alunos em linguagens procedurais (C, Pascal).
  * Destacar o modelo mental procedural: *"O que o sistema precisa fazer passo a passo?"*.
  * Apontar as forças do paradigma: ideal para drivers de baixo nível, scripts lineares e processamento puramente matemático.

---

### Slide 3: O Ponto de Ruptura do Modelo Procedural
* **Título do Slide:** O Gargalo de Manutenção em Sistemas Corporativos
* **Tópicos Visuais:**
  * **Efeito Colateral Indesejado:** Múltiplas funções acessam e modificam diretamente os mesmos registros de dados sem barreira de proteção.
  * **Alto Acoplamento:** Alterar o layout de uma estrutura quebra dezenas de funções dependentes espalhadas pela base de código.
  * **Falta de Invariantes de Negócio:** Dados em estado inconsistente trafegam livremente pelo sistema (ex.: saldos bancários negativos, cadastros sem documento).
* **Notas Pedagógicas do Professor:**
  * Exemplo prático de sala: Imagine um sistema bancário com `struct Conta { double saldo; }`. Qualquer programador pode escrever `conta.saldo = -50000;` em qualquer parte do sistema sem que nenhuma validação de saldo seja acionada.
  * Mostrar como bases de código com centenas de milhares de linhas tornam a depuração estruturada caótica.

---

### Slide 4: O Paradigma Orientado a Objetos (POO)
* **Título do Slide:** Orientação a Objetos: Unindo Estado e Comportamento
* **Tópicos Visuais:**
  * **Objeto:** Entidade de software que agrupa estado (atributos/dados) e comportamento (métodos/operações).
  * **Encapsulamento:** O estado interno é blindado (`private`); a manipulação ocorre exclusivamente através de mensagens/métodos públicos.
  * Matriz Comparativa:

| Aspecto | Paradigma Procedural | Paradigma Orientado a Objetos |
| :--- | :--- | :--- |
| **Foco Central** | Verbo / Ação (Como processar) | Substantivo / Entidade (Quem é responsável) |
| **Organização** | Funções e procedimentos soltos | Classes e Objetos encapsulados |
| **Segurança dos Dados** | Baixa (dados expostos à mutação direta) | Alta (encapsulamento e visibilidade) |
| **Escalabilidade** | Complexa e propensa a efeitos colaterais | Modular, desacoplada e extensível |

* **Notas Pedagógicas do Professor:**
  * Destacar a mudança de pergunta: em vez de *"Como calcular o juro?"*, o desenvolvedor de software pergunta: *"Qual classe do domínio tem a responsabilidade de gerenciar as suas próprias regras de juros?"*.
  * Introduzir a metáfora da "caixa-preta": o consumidor do objeto conhece a interface pública, mas não precisa se preocupar com os detalhes internos de implementação.

---

### Slide 5: Estudo de Caso Comparativo de Código
* **Título do Slide:** Na Prática: Controle de Integridade de Saldo
* **Tópicos Visuais:**
  * **Abordagem Estruturada (Dados Expostos):**
    ```java
    // Dados soltos sem proteção contra mutação indevida
    double saldo = 100.0;
    
    // Função utilitária externa
    saldo = sacar(saldo, 150.0); // Onde está a garantia contra saldo negativo?
    ```
  * **Abordagem Orientada a Objetos (Estado Blindado):**
    ```java
    // Objeto autônomo e responsável pela sua própria consistência
    ContaBancaria conta = new ContaBancaria(100.0);
    conta.sacar(150.0); // A própria classe rejeita a operação e protege sua invariante
    ```
* **Notas Pedagógicas do Professor:**
  * Conduzir dinâmica de *Peer Instruction*: perguntar aos alunos o que acontece no modelo procedural se um desenvolvedor desatento criar uma função `aplicarTarifa()` sem checar se o saldo é suficiente.
  * Mostrar que a POO transfere o controle de integridade da mão do chamador para a própria entidade.

---

### Slide 6: A Arquitetura da Plataforma Java
* **Título do Slide:** O Ecossistema Java: *Write Once, Run Anywhere* (WORA)
* **Tópicos Visuais:**
  * Pipeline de Compilação e Execução:
    $$\text{Código-Fonte (.java)} \xrightarrow[\text{javac}]{Compilador} \text{Bytecode (.class)} \xrightarrow[\text{JVM}]{Interpretação / JIT} \text{Código Nativo (SO/Hardware)}$$
  * **JDK (Java Development Kit):** Compilador (`javac`), utilitários (`javadoc`, `javap`) e ambiente de desenvolvimento.
  * **JRE (Java Runtime Environment):** Bibliotecas de classes padrão + JVM (voltado para execução em servidores de produção).
  * **JVM (Java Virtual Machine):** Máquina virtual que isola a aplicação das particularidades do sistema operacional.
* **Notas Pedagógicas do Professor:**
  * Explicar o conceito de Bytecode: conjunto padronizado de instruções de baixo nível que não dependem do conjunto de instruções do processador físico (x86, ARM, RISC-V).
  * Destacar o compilador JIT (*Just-In-Time*): a JVM monitora em tempo real os trechos mais executados (*hot spots*) e os compila dinamicamente para código nativo ultraotimizado.

---

### Slide 7: Gerenciamento de Memória: Stack, Heap e o Garbage Collector
* **Título do Slide:** Ciclo de Vida da Memória na JVM
* **Tópicos Visuais:**
  * **Stack (Pilha de Execução):**
    * Armazena os quadros de chamada de métodos (*Stack Frames*).
    * Guarda variáveis locais de tipos primitivos e ponteiros de referência.
    * Alocação e desalocação automática e extremamente rápida.
  * **Heap (Área Dinâmica):**
    * Espaço onde residem todas as instâncias de objetos instanciados via operador `new`.
  * **Garbage Collector (GC):**
    * Processo em segundo plano que rastreia e desaloca objetos inalcançáveis (sem referências ativas na Stack).
* **Notas Pedagógicas do Professor:**
  * Comparar com C/C++ (`malloc`/`free`, `delete`), ressaltando a eliminação de ponteiros pendentes (*dangling pointers*) e vazamentos de memória estruturais.
  * Fazer a ressalva de engenharia: o GC gerencia a desalocação, mas não impede más práticas (como acumular objetos desnecessários em coleções estáticas).

---

### Slide 8: Estrutura Mínima e Compilação Manual
* **Título do Slide:** Anatomia de um Programa Java e a Linha de Comando
* **Tópicos Visuais:**
  ```java
  public class OlaMundo {
      public static void main(String[] args) {
          System.out.println("Fundamentos de POO e Plataforma Java!");
      }
  }