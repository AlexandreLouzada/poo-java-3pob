# Plano de Aula e Roteiro de Slides: Aula 05

**Disciplina:** Programação Orientada a Objetos  
**Curso:** Análise e Desenvolvimento de Sistemas (3º Período)  
**Tema:** Encapsulamento, Modificadores de Acesso, Escopo de Variáveis e Membros Estáticos (`static`)  
**Carga Horária:** 100 minutos (2 horas-aula)  
**Professor:** Alexandre Neves Louzada

---

## 1. Plano de Ensino da Aula

### 1.1. Objetivos de Aprendizagem
* **Conceitual:** Compreender o princípio do Encapsulamento (*Information Hiding*) como mecanismo de proteção de estado e redução de acoplamento; entender a diferença semântica entre estado de instância (Heap) e estado de classe (Metaspace).
* **Técnico:** Dominar a matriz de visibilidade do Java (`private`, *default/package-private*, `protected`, `public`); implementar métodos acessores (*getters*) e modificadores (*setters*) com regras de negócio; manipular atributos e métodos estáticos (`static`), constantes globais (`static final`) e blocos estáticos de inicialização.
* **Arquitetural:** Diferenciar os três escopos de variáveis na JVM (instância, local/parâmetro e classe/estática), mapeando seu tempo de vida e localização física na memória.
* **Prático:** Refatorar modelos de dados abertos para entidades encapsuladas e implementar controle de auditoria centralizado com membros estáticos em um sistema bancário.

### 1.2. Metodologia Ativa
* **Think-Pair-Share & Análise de Vulnerabilidade:** Apresentação de um trecho de código bancário com atributos públicos onde valores inconsistentes são injetados externamente; os alunos identificam as falhas em duplas e propõem a refatoração para acesso encapsulado.
* **Live Coding & Mapeamento de Memória:** Demonstração no depurador da IDE inspecionando como atributos estáticos permanecem únicos no Metaspace enquanto múltiplas instâncias ocupam blocos independentes no Heap.

---

## 2. Roteiro Detalhado de Slides

---

### Slide 1: Abertura e a Fragilidade do Estado Exposto
* **Título do Slide:** Encapsulamento e Blindagem de Estado em Sistemas Corporativos
* **Tópicos Visuais:**
  * O perigo da manipulação direta de variáveis de negócio.
  * O princípio da Ocultação de Informação (*Information Hiding*).
  * A matriz de modificadores de visibilidade na JVM.
  * Membros de instância vs. Membros de classe (`static`).
* **Notas Pedagógicas do Professor:**
  * Relembrar a Aula 04: na aula anterior aprendemos a criar classes e instanciar objetos no Heap, mas nossos atributos ainda estavam abertos (`veiculo.precoBase = -999.0;`).
  * Enfatizar para ADS: em sistemas corporativos, **nenhum atributo de entidade deve ser público**. O encapsulamento garante que o objeto seja o único responsável pela integridade de suas regras de negócio.

---

### Slide 2: A Matriz de Modificadores de Acesso
* **Título do Slide:** Controle de Visibilidade na Plataforma Java
* **Tópicos Visuais:**
  * Matriz de Acesso:

| Modificador | Própria Classe | Mesmo Pacote | Subclasses (Outro Pacote) | Qualquer Lugar (*World*) |
| :--- | :---: | :---: | :---: | :---: |
| `private` | **Sim** | Não | Não | Não |
| *(default / package)* | **Sim** | **Sim** | Não | Não |
| `protected` | **Sim** | **Sim** | **Sim** | Não |
| `public` | **Sim** | **Sim** | **Sim** | **Sim** |

  * **Princípio do Menor Privilégio (*Least Privilege Principle*):** Torne cada membro tão restrito quanto possível.
* **Notas Pedagógicas do Professor:**
  * Explicar que a ausência de modificador (*default*) restringe o acesso ao mesmo pacote físico, o que pode causar vazamento inadvertido de dados em pacotes com muitas classes.
  * Regra de ouro da POO em Java: Atributos são `private`; métodos públicos formam a interface de comunicação da classe.

---

### Slide 3: Getters e Setters com Responsabilidade
* **Título do Slide:** Acesso Controlado: Métodos Acessores e Modificadores
* **Tópicos Visuais:**
  ```java
  public class Funcionario {
      private double salarioBase; // Blindado contra alteração externa direta

      public double getSalarioBase() {
          return this.salarioBase;
      }

      public void setSalarioBase(double novoSalario) {
          if (novoSalario >= 1412.00) { // Regra de negócio (Piso Nacional)
              this.salarioBase = novoSalario;
          } else {
              System.err.println("Erro: Salário inferior ao piso legal!");
          }
      }
  }