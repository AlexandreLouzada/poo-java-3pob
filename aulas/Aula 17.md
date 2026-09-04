# Plano de Aula e Roteiro de Slides: Aula 17

**Disciplina:** Programação Orientada a Objetos  
**Curso:** Análise e Desenvolvimento de Sistemas (3º Período)  
**Tema:** Paradigma Funcional: Interfaces Funcionais e Expressões Lambda  
**Carga Horária:** 100 minutos (2 horas-aula)  
**Professor:** Alexandre Neves Louzada

---

## 1. Plano de Ensino da Aula

### 1.1. Objetivos of Aprendizagem
* **Conceitual:** Compreender o paradigma funcional introduzido no Java 8; reconhecer o conceito de funções de primeira classe (*First-Class Functions*); entender a evolução sintática da verbosidade de classes anônimas para expressões declarativas concisas; compreender o conceito de contrato SAM (*Single Abstract Method*).
* **Técnico:** Validar e criar interfaces funcionais personalizadas com a anotação `@FunctionalInterface`; dominar a sintaxe e regras de inferência de tipo das Expressões Lambda `(parâmetros) -> { corpo }`; utilizar o pacote padronizado `java.util.function` (`Predicate<T>`, `Consumer<T>`, `Function<T, R>`, `Supplier<T>`); aplicar iteração interna com `Iterable.forEach()` e remoção condicional com `Collection.removeIf()`.
* **Arquitetural:** Projetar motores de filtragem, transformação e processamento genéricos e desacoplados, onde comportamentos de negócio são repassados como argumentos parametrizados para métodos de serviço.
* **Prático:** Implementar um motor de regras de RH (`MotorFiltroRh`), utilizando expressões lambda dinâmicas e composição de predicados para filtragem de colaboradores elegíveis a promoções e reajustes salariais.

### 1.2. Metodologia Ativa
* **Refatoração Sistemática em Pares (Refactoring Challenge):** Apresentação de um código legado baseado em classes anônimas burocráticas (*Anonymous Inner Classes*). Os alunos refatoram o projeto em tempo real, transformando blocos de 8 linhas em expressões lambda funcionais de uma única linha.
* **Composição de Regras com Predicados:** Exercício prático combinando regras lógicas complexas através dos métodos padrão `.and()`, `.or()` e `.negate()`.

---

## 2. Roteiro Detalhado de Slides

---

### Slide 1: Abertura e a Mudança de Paradigma
* **Título do Slide:** Programação Funcional em Java: Da Verbosidade Imperativa à Elegância Declarativa
* **Tópicos Visuais:**
  * O marco do Java 8: a convergência entre Orientação a Objetos e Programação Funcional.
  * O problema da verbosidade das Classes Anônimas (*Anonymous Inner Classes*).
  * O conceito de interfaces SAM (*Single Abstract Method*).
  * Anatomia das Expressões Lambda: `(parâmetros) -> { corpo }`.
  * O arsenal funcional da JDK (`java.util.function`).
* **Notas Pedagógicas do Professor:**
  * Contextualizar para ADS: até agora passávamos **dados** (primitivos ou objetos) como argumentos de métodos. A programação funcional nos permite passar **comportamento** (código/função) diretamente como parâmetro.

---

### Slide 2: A Dor da Verbosidade: Classes Anônimas Legadas
* **Título do Slide:** O Antecessor das Lambdas: Classes Anônimas
* **Tópicos Visuais:**
  * **Código Legado (Antes do Java 8):**
    
```java
    // Necessidade de instanciar uma interface inteira para passar UMA função
    Collections.sort(funcionarios, new Comparator<Funcionario>() {
        @Override
        public int compare(Funcionario f1, Funcionario f2) {
            return Double.compare(f1.getSalario(), f2.getSalario());
        }
    });