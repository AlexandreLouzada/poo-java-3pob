# Plano de Aula e Roteiro de Slides: Aula 04

**Disciplina:** Programação Orientada a Objetos  
**Curso:** Análise e Desenvolvimento de Sistemas (3º Período)  
**Tema:** Abstração, Classes, Atributos, Métodos, Instanciação e Construtores  
**Carga Horária:** 100 minutos (2 horas-aula)  
**Professor:** Alexandre Neves Louzada

---

## 1. Plano de Ensino da Aula

### 1.1. Objetivos de Aprendizagem
* **Conceitual:** Compreender o princípio da abstração no projeto orientado a objetos; diferenciar a classe (modelo/especificação) de um objeto (entidade concreta instanciada em memória); entender a semântica de estado e comportamento.
* **Arquitetural:** Mapear o ciclo de vida dos objetos em memória com o operador `new`, diferenciando o armazenamento de variáveis de referência na Stack dos dados dinâmicos do objeto no Heap; compreender os efeitos de cópia de referências (*aliasing*).
* **Técnico:** Projetar classes com atributos de instância e métodos com regras de guarda; dominar a criação de construtores padrão e sobrecarregados; utilizar a palavra-chave `this` para desambiguação de escopo e encadeamento de construtores (`this(...)`).
* **Prático:** Implementar uma modelagem orientada a domínio para gestão de veículos de concessionária, validando invariantes no momento da instanciação.

### 1.2. Metodologia Ativa
* **Problem-Based Learning (PBL):** Apresentação de um problema de inconsistência cadastral em sistemas de concessionárias para guiar a modelagem de classes com construtores protetivos.
* **Depuração Visual de Memória:** Demonstração no depurador da IDE para visualizar referências na Stack apontando para blocos distintos (e compartilhados) no Heap.

---

## 2. Roteiro Detalhado de Slides

---

### Slide 1: Abertura e Transição de Modelo Mental
* **Título do Slide:** O Núcleo da POO: Da Modelagem Conceitual à Instanciação em Memória
* **Tópicos Visuais:**
  * Abertura do Módulo 2: O Núcleo da Orientação a Objetos.
  * O princípio da abstração: isolando o essencial para o domínio de negócio.
  * Classe (especificação) vs. Objeto (instância real em memória).
  * O ciclo de instanciação: `new`, alocação no Heap e construtores.
* **Notas Pedagógicas do Professor:**
  * Enfatizar a transição: até a Aula 03 usamos métodos utilitários estáticos. Agora abandonamos o uso indiscriminado de `static` para focar em instâncias dinâmicas.
  * Destacar para ADS: cada classe modelada deve refletir uma entidade do modelo de negócio (`Veiculo`, `Cliente`, `Conta`, `ItemPedido`).

---

### Slide 2: O Princípio da Abstração no Projeto de Software
* **Título do Slide:** Abstração Orientada ao Contexto do Negócio
* **Tópicos Visuais:**
  * **Definição:** Capacidade de selecionar apenas as propriedades e ações relevantes de um elemento real para o escopo do software em desenvolvimento.
  * **Estudo de Caso - A Entidade "Pessoa" em Diferentes Domínios:**
    * *Detran:* CNH, categoria, pontuação na carteira, vencimento do exame.
    * *Hospital:* tipo sanguíneo, histórico cirúrgico, prontuário, alergias.
    * *Universidade:* matrícula, curso, coeficiente de rendimento (CR).
* **Notas Pedagógicas do Professor:**
  * Perguntar aos alunos: "Existe uma modelagem universal e perfeita para uma classe `Carro`?".
  * Concluir que a modelagem depende do contexto: uma locadora precisa de placa e quilometragem; um jogo de corrida precisa de torque, aerodinâmica e atrito dos pneus.

---

### Slide 3: Anatomia de uma Classe em Java
* **Título do Slide:** Estrutura de uma Classe: Declaração de Estado e Comportamento
* **Tópicos Visuais:**
  ```java
  public class ContaBancaria {
      // 1. Atributos (Estado do Objeto)
      String numero;
      String titular;
      double saldo;

      // 2. Métodos (Comportamento e Regras de Guarda)
      void depositar(double valor) {
          if (valor > 0) {
              saldo += valor;
          }
      }

      boolean sacar(double valor) {
          if (valor > 0 && saldo >= valor) {
              saldo -= valor;
              return true;
          }
          return false;
      }
  }