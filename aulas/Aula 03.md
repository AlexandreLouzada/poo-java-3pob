# Plano de Aula e Roteiro de Slides: Aula 03

**Disciplina:** Programação Orientada a Objetos  
**Curso:** Análise e Desenvolvimento de Sistemas (3º Período)  
**Tema:** Documentação Técnica Corporativa, Contratos de Código e Javadoc  
**Carga Horária:** 100 minutos (2 horas-aula)  
**Professor:** Alexandre Neves Louzada

---

## 1. Plano de Ensino da Aula

### 1.1. Objetivos de Aprendizagem
* **Conceitual:** Compreender a importância da documentação de software na governança corporativa, manutenção de código legado e definição de contratos de APIs (precondições, pós-condições e invariantes).
* **Técnico:** Dominar a sintaxe dos comentários Javadoc (`/** ... */`) e a aplicação rigorosa das tags estruturadas padronizadas (`@author`, `@version`, `@param`, `@return`, `@throws`, `@see`, `@since`).
* **Operacional:** Gerar portais técnicos de documentação em HTML via linha de comando (`javadoc` CLI) com parâmetros adequados de codificação UTF-8 e via menus de IDEs modernas.
* **Prático:** Realizar revisão de código em pares (*Code Review*), consumindo classes utilitárias exclusivamente através da documentação gerada no navegador e via *Quick Documentation* na IDE.

### 1.2. Metodologia Ativa
* **Code Review em Pares:** Os alunos implementam classes de cálculo estatístico com documentação técnica e trocam artefatos para auditar a legibilidade e testar a API sem inspecionar o código-fonte.
* **Hands-on CLI & Web:** Execução do utilitário `javadoc` no terminal e exploração das páginas geradas para validar a conformidade da documentação com os padrões da indústria.

---

## 2. Roteiro Detalhado de Slides

---

### Slide 1: Abertura e Visão de Engenharia de Software
* **Título do Slide:** Documentação Técnica de Código: O Papel do Javadoc na Manutenção de Software
* **Tópicos Visuais:**
  * O custo da manutenção: por que bases de código sem documentação geram dívida técnica?
  * Código legível (*Clean Code*) vs. Documentação de API: papéis complementares.
  * O que é o Javadoc: gerador padrão de documentação técnica em HTML a partir do código-fonte.
* **Notas Pedagógicas do Professor:**
  * Contextualizar para o perfil de ADS: em ambientes corporativos e times ágeis, engenheiros passam mais tempo lendo e integrando código existente do que escrevendo novos arquivos.
  * Diferenciar: *Clean Code* torna o algoritmo autoexplicativo (o "como"), enquanto o Javadoc documenta o contrato, limites, pré/pós-condições e exceções (o "o quê" e o "porquê").

---

### Slide 2: Anatomia dos Comentários em Java
* **Título do Slide:** Comentários Comuns vs. Comentários de Documentação
* **Tópicos Visuais:**
  * **Comentário de Linha Única:** `// comentário simples` (ignorado pelo compilador e pelo Javadoc).
  * **Comentário de Bloco:** `/* comentário multilinha */` (ignorado pelo Javadoc).
  * **Comentário Javadoc:** `/** Comentário Estruturado */` (processado pela ferramenta `javadoc`).
* **Notas Pedagógicas do Professor:**
  * Explicar o posicionamento obrigatório: o comentário Javadoc deve ficar **imediatamente antes** de classes, interfaces, métodos, atributos protegidos/públicos e enums.
  * Alertar sobre o antipadrão: jamais use comentários para justificar código ruim ou confuso; refatore primeiro e use Javadoc para formalizar o contrato público da API.

---

### Slide 3: Estrutura Geral e Tags Principais
* **Título do Slide:** Tags Estruturadas do Javadoc
* **Tópicos Visuais:**
  * **Nível de Classe / Interface:**
    * `@author`: Identificação do desenvolvedor ou time responsável.
    * `@version`: Versão do artefato (ex.: `1.0.0`, `2.1.0-RC1`).
    * `@since`: Versão em que o recurso foi incorporado à API.
    * `@see`: Referência cruzada para outras classes, métodos ou links externos.
  * **Nível de Método:**
    * `@param <nome>`: Descrição e restrições de cada parâmetro de entrada.
    * `@return`: Descrição do valor de retorno (omitido em métodos `void`).
    * `@throws` / `@exception`: Condições exatas que disparam exceções.
* **Notas Pedagógicas do Professor:**
  * Apresentar a convenção de ordenação recomendada pela Oracle: Sumário $\rightarrow$ Descrição detalhada $\rightarrow$ `@param` $\rightarrow$ `@return` $\rightarrow$ `@throws` $\rightarrow$ `@see` $\rightarrow$ `@since`.

---

### Slide 4: Estudo de Caso — Classe Utilitária Documentada
* **Título do Slide:** Exemplo Real: Módulo de Validação Fiscal
* **Tópicos Visuais:**
  ```java
  package br.edu.universidade.util;

  /**
   * Fornece rotinas utilitárias para validação de documentos fiscais.
   * <p>Esta classe não pode ser instanciada.</p>
   *
   * @author Prof. Dr.
   * @version 1.0.0
   * @since 1.0.0
   */
  public final class ValidadorDocumento {

      private ValidadorDocumento() {}

      /**
       * Valida o formato e os dígitos de uma cadeia de CPF.
       *
       * @param cpf texto contendo os dígitos do CPF (com ou sem máscara).
       * @return {@code true} se for válido; {@code false} caso contrário ou se nulo.
       * @throws IllegalArgumentException se contiver letras não permitidas.
       * @see #sanitizar(String)
       */
      public static boolean isCpfValido(String cpf) {
          if (cpf == null) return false;
          String limpo = sanitizar(cpf);
          return limpo.length() == 11;
      }

      public static String sanitizar(String texto) {
          return texto == null ? "" : texto.replaceAll("\\D", "");
      }
  }