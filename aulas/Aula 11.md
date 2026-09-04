# Plano de Aula e Roteiro de Slides: Aula 11

**Disciplina:** Programação Orientada a Objetos  
**Curso:** Análise e Desenvolvimento de Sistemas (3º Período)  
**Tema:** Introdução ao *Java Collections Framework* e Parametrização com *Generics* (`<T>`)  
**Carga Horária:** 100 minutos (2 horas-aula)  
**Professor:** Alexandre Neves Louzada

---

## 1. Plano de Ensino da Aula

### 1.1. Objetivos de Aprendizagem
* **Conceitual:** Compreender as limitações estruturais e de escalabilidade de vetores primitivos (`Tipo[]`) em memória; entender a arquitetura, interfaces e hierarquia do *Java Collections Framework* (`java.util.*`); reconhecer os riscos de *Raw Types* e coerções manuais legadas.
* **Técnico:** Dominar a parametrização de tipos com *Generics* (`<T>`, `<E>`, `<K, V>`), compreendendo a inferência pelo operador diamante (`<>`); implementar classes e métodos genéricos customizados; entender o mecanismo interno de *Type Erasure* (apagamento de tipos) e o uso de classes *Wrapper* (`Integer`, `Double`, `Boolean`) com *Autoboxing/Unboxing*.
* **Arquitetural:** Projetar componentes reutilizáveis baseados no padrão repositório em memória (*InMemoryRepository*), garantindo encapsulamento através de visões imutáveis (`Collections.unmodifiableList`).
* **Prático:** Implementar uma estrutura de dados de pilha genérica (`PilhaGenerica<E>`) operando dinamicamente em memória e validando a segurança estática de tipos em tempo de compilação.

### 1.2. Metodologia Ativa
* **Análise Comparativa Histórica (Antes e Depois do Java 5):** Demonstração do comportamento de listas legadas que guardavam `Object`, gerando falhas catastróficas de `ClassCastException` em produção, em contraste com a validação estrita do compilador utilizando Generics.
* **Laboratório Hands-on de Estruturas Genéricas:** Desenvolvimento guiado de uma estrutura dinâmica parametrizada com testes de mesa na IDE para comprovar a rejeição imediata de incompatibilidade de tipos pelo `javac`.

---

## 2. Roteiro Detalhado de Slides

---

### Slide 1: Abertura e Visão de Engenharia de Software
* **Título do Slide:** Manipulação Dinâmica de Dados: O *Collections Framework* e a Segurança de Tipos
* **Tópicos Visuais:**
  * Abertura do Módulo 4: Collections Framework & Generics.
  * As limitações estruturais dos arrays primitivos em sistemas corporativos.
  * A arquitetura do *Java Collections Framework* (`java.util.*`).
  * O papel dos *Generics* (`<T>`): eliminando *casts* explícitos e garantindo *Type Safety*.
  * Classes genéricas e interfaces parametrizadas.
* **Notas Pedagógicas do Professor:**
  * Contextualizar a transição: encerramos o Módulo 3 de resiliência/exceções e iniciamos o Módulo 4, focado em **estruturas de dados dinâmicas na memória**.
  * Foco para ADS: no dia a dia corporativo, quase nunca usamos vetores estáticos (`Tipo[]`); manipulamos listas dinâmicas, conjuntos e mapas com validação rigorosa de tipos pelo compilador.

---

### Slide 2: As Limitações dos Arrays Tradicionais
* **Título do Slide:** Por que Precisamos de Coleções Dinâmicas?
* **Tópicos Visuais:**
  * **Tamanho Fixo:** Um array `new Cliente[100]` não pode ser redimensionado; expandir a capacidade exige alocar um novo vetor maior e copiar elemento por elemento.
  * **Falta de Abstrações de Alto Nível:** Não há métodos nativos para busca por predicado, remoção direta de elementos, inserção ordenada ou união de conjuntos.
  * **Complexidade Manual:** O desenvolvedor precisa controlar manualmente ponteiros de índice, posições vazias (`null`) e realocações.
* **Notas Pedagógicas do Professor:**
  * Relembrar a experiência dos alunos em linguagens procedurais (C/Pascal): o esforço manual de implementar filas, pilhas e listas encadeadas do zero para cada novo tipo de dado.
  * Apresentar o *Collections Framework* como uma biblioteca padronizada, de alta performance e amplamente testada da JDK.

---

### Slide 3: Visão Geral da Hierarquia do Collections Framework
* **Título do Slide:** O Mapa das Principais Interfaces de Coleções
* **Tópicos Visuais:**
  * Diagrama de Interfaces Raiz:
    ```
                        java.lang.Iterable<T>
                                 ▲
                                 │
                      java.util.Collection<T>
                                 ▲
            ┌────────────────────┼────────────────────┐
            │                    │                    │
       java.util.List<T>    java.util.Set<T>     java.util.Queue<T>
       (Indexado/Duplicado) (Único/Sem Índice)   (Ordem de Processamento)

       *Nota: java.util.Map<K,V> é uma hierarquia paralela baseada em Chave-Valor.
    ```
* **Notas Pedagógicas do Professor:**
  * Destacar a raiz: toda coleção herda de `Iterable<T>`, o que permite o uso automático do laço `for-each`.
  * Explicar por que `Map<K,V>` não herda diretamente de `Collection<T>`: mapas manipulam pares chave-valor, enquanto coleções manipulam elementos individuais.

---

### Slide 4: O Cenário Pré-Java 5 (Coleções Sem Generics / *Raw Types*)
* **Título do Slide:** O Risco dos *Raw Types* e a Falha em Tempo de Execução
* **Tópicos Visuais:**
  ```java
  // Código legado (NÃO RECOMENDADO): lista bruta guardando 'Object'
  List listaBruta = new ArrayList();
  listaBruta.add("Cliente A");
  listaBruta.add(new ContaBancaria("1001-X", "Alice", 500.0)); // Aceita qualquer objeto!

  // Cast explícito frágil sujeito a quebra:
  String nome = (String) listaBruta.get(1); // Dispara ClassCastException em RUNTIME!