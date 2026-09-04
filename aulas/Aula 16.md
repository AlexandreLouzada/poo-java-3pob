# Plano de Aula e Roteiro de Slides: Aula 16

**Disciplina:** Programação Orientada a Objetos  
**Curso:** Análise e Desenvolvimento de Sistemas (3º Período)  
**Tema:** Introdução a Metadados com Anotações (`@Override`, `@Deprecated`, `@SuppressWarnings` e Anotações Customizadas)  
**Carga Horária:** 100 minutos (2 horas-aula)  
**Professor:** Alexandre Neves Louzada

---

## 1. Plano de Ensino da Aula

### 1.1. Objetivos de Aprendizagem
* **Conceitual:** Compreender o papel dos metadados no desenvolvimento de software moderno; entender a evolução histórica do ecossistema Java (migração da verbosidade de arquivos XML para anotações declarativas no código); compreender o ciclo de vida dos metadados e as políticas de retenção na JVM.
* **Técnico:** Aplicar anotações nativas do compilador para governança e segurança de código (`@Override`, `@Deprecated`, `@SuppressWarnings`); utilizar meta-anotações (`@Target`, `@Retention`, `@Documented`) para estruturar anotações customizadas; inspecionar programaticamente metadados em tempo de execução utilizando a API de *Reflection*.
* **Arquitetural:** Projetar contratos de governança e segurança declarativa utilizando anotações de domínio de negócio; reconhecer a arquitetura declarativa utilizada por frameworks corporativos do mercado (Spring Boot, Jakarta EE, Hibernate/JPA e JUnit 5).
* **Prático:** Refatorar uma API fiscal legada marcando métodos obsoletos com `@Deprecated(forRemoval = true)`, suprimir alertas de compilação justificados e criar uma anotação customizada de controle de acesso (`@PerfilAcesso`) processada dinamicamente em tempo de execução.

### 1.2. Metodologia Ativa
* **Análise de Ciclo de Vida de Software & Refatoração:** Avaliação em duplas de um módulo legado em degradação. Os alunos aplicam anotações de descontinuação controlada e documentação Javadoc associada para orientar os clientes da API sobre a migração de versão.
* **Inspeção de Metadados via Reflection (Hands-on):** Desenvolvimento guiado de um mecanismo simples de autorização capaz de ler anotações em tempo de execução e decidir se um método pode ser invocado.

---

## 2. Roteiro Detalhado de Slides

---

### Slide 1: Abertura e Visão de Engenharia de Software
* **Título do Slide:** Metadados no Código: O Papel das Anotações na Plataforma Java
* **Tópicos Visuais:**
  * Abertura do Módulo 5: Recursos Modernos da Linguagem e Metadados.
  * O que são metadados: "dados sobre dados" anexados ao código-fonte.
  * A revolução do Java 5: substituição da configuração XML por anotações declarativas.
  * Anotações nativas da JDK (`@Override`, `@Deprecated`, `@SuppressWarnings`).
  * Introdução a Anotações Customizadas e Reflexão (*Reflection*).
* **Notas Pedagógicas do Professor:**
  * Contextualizar: no Módulo 4 consolidamos estruturas de dados e ordenação. No Módulo 5 entramos na metaprogramação e recursos modernos do Java.
  * Destacar para ADS: frameworks corporativos modernos (Spring Boot, Quarkus, Hibernate) funcionam quase inteiramente baseados em anotações. Entender como elas operam por baixo dos panos demistifica a "mágica" dos frameworks.

---

### Slide 2: O Conceito de Metadados e a Sintaxe `@`
* **Título do Slide:** O que São e Como Operam as Anotações?
* **Tópicos Visuais:**
  * **Definição:** Marcadores estruturados iniciados pelo símbolo `@` que adicionam informações semânticas a elementos do código (classes, métodos, atributos, parâmetros ou pacotes).
  * **Comportamento Neutro:** Anotações **não alteram diretamente a lógica de execução** do método onde estão colocadas; elas servem como etiquetas para o compilador, ferramentas de análise estática (SonarQube) ou para o container do framework em tempo de execução.
* **Notas Pedagógicas do Professor:**
  * Usar a metáfora da etiqueta de remessa em uma caixa de mudança: a etiqueta "FRÁGIL" não muda o conteúdo dentro da caixa, mas orienta o transportador sobre como manuseá-la.

---

### Slide 3: Garantia em Tempo de Compilação: `@Override`
* **Título do Slide:** `@Override`: Blindagem Contra Erros Sutis de Sobrescrita
* **Tópicos Visuais:**
  * **Objetivo:** Informar ao compilador que o método marcado pretende redefinir um método da superclasse ou interface.
  * **Segurança de Código:**
    
```java
    // SEM @Override: Se errar a digitação, cria uma SOBRECARGA acidental!
    public boolean equals(Cliente outro) { ... } // Bug silencioso!

    // COM @Override: O compilador valida a assinatura exata
    @Override
    public boolean equals(Object outro) { ... } // Se errar, O COMPILADOR TRAVA!