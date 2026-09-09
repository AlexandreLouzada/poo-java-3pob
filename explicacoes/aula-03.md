# Explicação da Aula 03 — Documentação Técnica Corporativa, Contratos de Código e Javadoc

| | |
|---|---|
| **Disciplina** | Programação Orientada a Objetos |
| **Curso** | Análise e Desenvolvimento de Sistemas (3º Período) |
| **Tema** | Documentação Técnica Corporativa, Contratos de Código e Javadoc |
| **Carga Horária** | 100 minutos (2 horas-aula) |
| **Professor** | Alexandre Neves Louzada |
| **Plano de Aula** | `Aulas 3pob/Aula 03.md` |
| **Tutorial** | `aulas/TutorialAula03.md` |
| **Estudo de Caso** | `exemplos/aula-03/` |
| **Exercícios Resolvidos** | `solucoes/aula-03/` |

## 1. Objetivos de Aprendizagem

- **Conceitual:** Compreender a importância da documentação de software na governança corporativa, manutenção de código legado e definição de contratos de APIs (precondições, pós-condições e invariantes).
- **Técnico:** Dominar a sintaxe dos comentários Javadoc (`/** ... */`) e a aplicação rigorosa das tags estruturadas padronizadas (`@author`, `@version`, `@param`, `@return`, `@throws`, `@see`, `@since`).
- **Operacional:** Gerar portais técnicos de documentação em HTML via linha de comando (`javadoc` CLI) com parâmetros adequados de codificação UTF-8 e via menus de IDEs modernas.
- **Prático:** Realizar revisão de código em pares (*Code Review*), consumindo classes utilitárias exclusivamente através da documentação gerada no navegador e via *Quick Documentation* na IDE.

## 2. Conteúdo Teórico Detalhado

### 2.1 O Papel da Documentação na Engenharia de Software

Em ambientes corporativos e times ágeis, engenheiros de software passam muito mais tempo lendo e integrando código existente do que escrevendo novos arquivos. Sem documentação, bases de código geram **dívida técnica** crescente: cada desenvolvedor precisa decifrar a intenção do autor original, perdendo tempo e incorrendo em riscos de reintrodução de bugs.

A documentação de software e o *Clean Code* desempenham papéis complementares:

- **Clean Code** torna o algoritmo autoexplicativo — ele responde ao "como" o código funciona. Nomes bem escolhidos, funções curtas e estruturas claras reduzem a necessidade de comentários.
- **Javadoc** documenta o contrato público da API — ele responde ao "o quê" o método faz e ao "porquê" das suas decisões de design, incluindo pré-condições, pós-condições, invariantes, efeitos colaterais e exceções.

O **Javadoc** é a ferramenta padrão do Java para gerar documentação técnica em HTML diretamente a partir do código-fonte. Ele processa comentários estruturados de tipo especial (`/** ... */`) e produz páginas navegáveis com descrições, assinaturas de métodos e referências cruzadas.

### 2.2 Anatomia dos Comentários em Java

Java suporta três tipos de comentários:

| Tipo | Sintaxe | Processado pelo `javadoc`? | Exemplo |
|:---|:---|:---|:---|
| Linha única | `// comentário` | Não | `// Calcula o juro mensal` |
| Bloco | `/* ... */` | Não | `/* Lógica de validação do CPF */` |
| Javadoc | `/** ... */` | **Sim** | `/** Valida o formato do CPF. */` |

**Posicionamento obrigatório:** O comentário Javadoc deve ficar **imediatamente antes** de classes, interfaces, métodos, atributos protegidos/públicos e enums. Se houver uma linha em branco entre o comentário e a declaração, o `javadoc` não o associará ao elemento.

**Antipadrão:** Jamais use comentários para justificar código ruim ou confuso. O correto é refatorar o código primeiro e usar Javadoc para formalizar o contrato público da API.

### 2.3 Estrutura Geral e Tags Principais do Javadoc

A documentação Javadoc é estruturada em dois níveis:

**Nível de Classe / Interface:**

| Tag | Descrição | Exemplo |
|:---|:---|:---|
| `@author` | Identificação do desenvolvedor ou time responsável | `@author Prof. Dr.` |
| `@version` | Versão do artefato | `@version 1.0.0` |
| `@since` | Versão em que o recurso foi incorporado à API | `@since 1.0.0` |
| `@see` | Referência cruzada para outras classes, métodos ou links | `@see #sanitizar(String)` |

**Nível de Método:**

| Tag | Descrição | Exemplo |
|:---|:---|:---|
| `@param <nome>` | Descrição e restrições de cada parâmetro de entrada | `@param cpf texto contendo os dígitos` |
| `@return` | Descrição do valor de retorno (omitido em métodos `void`) | `@return {@code true} se for válido` |
| `@throws` / `@exception` | Condições exatas que disparam exceções | `@throws IllegalArgumentException` |

**Convenção de ordenação recomendada pela Oracle:**

1. Sumário (primeira linha)
2. Descrição detalhada (parágrafos seguintes)
3. `@param` (para cada parâmetro, na ordem da assinatura)
4. `@return`
5. `@throws`
6. `@see`
7. `@since`

**HTML dentro do Javadoc:** Tags como `<p>`, `<code>`, `<pre>` são permitidas. A tag `{@code ...}` é a forma mais segura de inserir trechos de código ou identificadores dentro da documentação, pois previne a interpretação errada de caracteres especiais.

### 2.4 Geração de Documentação com `javadoc` CLI

A ferramenta `javadoc` pode ser executada diretamente no terminal para gerar portais HTML de documentação. O comando básico é:

```bash
javadoc -encoding UTF-8 -charset UTF-8 -d doc -sourcepath src br.edu.universidade.util.ValidadorDocumento
```

Parâmetros importantes:
- `-encoding UTF-8`: Define a codificação do código-fonte de entrada.
- `-charset UTF-8`: Define a codificação do HTML gerado.
- `-d doc`: Define o diretório de saída da documentação.
- `-sourcepath src`: Indica onde encontrar os arquivos-fonte.

Após a execução, o diretório `doc/` conterá arquivos HTML navegáveis com toda a documentação da classe, incluindo os links cruzados das tags `@see`.

### 2.5 Estudo de Caso — Classe Utilitária Documentada

O exemplo da aula apresenta uma classe utilitária para validação de documentos fiscais, pacote `br.edu.universidade.util`:

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

    /**
     * Construtor privado para impedir a instanciação da classe utilitária.
     */
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

    /**
     * Remove quaisquer caracteres não numéricos de uma cadeia de texto.
     *
     * @param texto cadeia de caracteres de entrada a ser filtrada.
     * @return texto contendo unicamente dígitos decimais, ou cadeia vazia se nulo.
     */
    public static String sanitizar(String texto) {
        return texto == null ? "" : texto.replaceAll("\\D", "");
    }
}
```

Elementos-chave demonstrados:
- **`final class`:** Impede que a classe seja herdada.
- **Construtor privado:** Impede a instanciação — todos os métodos são `static`.
- **`@see #sanitizar(String)`:** Cria referência cruzada para outro método da mesma classe.
- **Tag `{@code ...}`:** Formata corretamente o valor de retorno na documentação.
- **Descrição do construtor privado:** Documenta a intenção de design (impedir instanciação).

## 3. Estudo de Caso Aplicado

O estudo de caso da Aula 03 está em `exemplos/aula-03/src/br/edu/universidade/util/` e contém a classe:

- **`ValidadorDocumento.java`** — Classe utilitária `final` com construtor privado e métodos estáticos para validação de documentos fiscais. Demonstrada acima na íntegra. A classe serve como modelo de como documentar uma API pública de forma profissional, incluindo o contrato (pré-condições, pós-condições, exceções) e as referências cruzadas entre métodos.

O exercício prático da aula (Code Review em Pares) instrui os alunos a implementarem classes de cálculo estatístico com documentação técnica completa, e depois trocarem os artefatos para auditar a legibilidade e testar a API consumindo apenas a documentação, sem inspecionar o código-fonte.

O passo operacional envolve a geração de documentação em HTML via `javadoc` no terminal:

```bash
javadoc -encoding UTF-8 -charset UTF-8 -d doc br/edu/universidade/util/ValidadorDocumento.java
```

## 4. Exercícios Propostos e Solução

A solução está em `solucoes/aula-03/src/br/edu/universidade/financeiro/` e contém duas classes:

- **`CalculadoraFinanceira.java`** — Classe utilitária `final` com construtor privado, pacote `br.edu.universidade.financeiro`. Contém dois métodos estáticos documentados com Javadoc completo:

  - `calcularJurosCompostos(double capital, double taxaMensal, int meses)`: Calcula o montante final sob regime de juros compostos usando a fórmula M = C x (1 + i)^n. Valida pré-condições (capital >= 0, taxa entre 0.0 e 1.0, prazo >= 1 mês) e lança `IllegalArgumentException` com mensagem descritiva em caso de violação. Documentação inclui `@param` para cada parâmetro com restrições, `@return` com descrição do montante, `@throws` com as condições de exceção.

  - `calcularAmortizacaoConstante(double saldoDevedor, int prazoMeses)`: Calcula a cota fixa do sistema SAC (Sistema de Amortização Constante). Mesmo padrão de documentação e validação de pré-condições.

- **`CalculadoraFinanceiraApp.java`** — Classe executável que demonstra o uso da `CalculadoraFinanceira`. Executa três cenários: (1) teste de juros compostos com parâmetros válidos; (2) teste de amortização constante; (3) validação de pré-condições através de `try/catch` capturando `IllegalArgumentException` para capital negativo, taxa acima de 1.0 e prazo zerado. A classe também possui construtor privado, seguindo o padrão de classes utilitárias.

## 5. Perguntas de Revisão

1. **Qual a diferença entre `//`, `/* ... */` e `/** ... */`?**
   - `//` é comentário de linha único; `/* ... */` é comentário de bloco multilinha; ambos são ignorados pelo `javadoc`. Apenas `/** ... */` é processado pela ferramenta `javadoc` para gerar documentação em HTML.

2. **Por que uma classe utilitária deve ser declarada `final` com construtor privado?**
   - `final` impede que a classe seja herdada (subclasses quebrariam a intenção utilitária). O construtor privado impede a instanciação, forçando o uso apenas dos métodos estáticos.

3. **Qual a importância da tag `@throws` na documentação?**
   - Ela formaliza o contrato da API ao declarar exatamente quais condições geram exceções, permitindo que o consumidor trate adequadamente os erros sem precisar inspecionar o código-fonte.

4. **Como a tag `@see` contribui para a navegabilidade da documentação?**
   - Cria referências cruzadas clicáveis entre classes e métodos, permitindo ao desenvolvedor navegar pela API de forma contextual sem precisar buscar manualmente.

## 6. Resumo / Pontos-Chave

- Documentação de software é um investimento em manutenibilidade e governança corporativa.
- Javadoc (`/** ... */`) é o padrão Java para documentação técnica gerável em HTML.
- Tags estruturadas (`@author`, `@param`, `@return`, `@throws`, `@see`) formalizam contratos de API.
- A convenção de ordenação da Oracle assegura consistência: sumário, descrição, `@param`, `@return`, `@throws`, `@see`, `@since`.
- Classes utilitárias devem ser `final` com construtor privado, impedindo herança e instanciação.
- A ferramenta `javadoc` gera portais HTML via CLI com `-encoding UTF-8` e `-charset UTF-8`.
- Code Review em Pares é uma metodologia ativa que valida a eficácia da documentação como contrato de uso.
- Comentários devem documentar a intenção e o contrato, não justificar código ruim — refatoração primeiro, Javadoc depois.
