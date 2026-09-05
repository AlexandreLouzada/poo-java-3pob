# Tutorial de Java — Aula 03: Documentação Técnica Corporativa, Contratos de Código e Javadoc

| | |
|---|---|
| **Disciplina** | Programação Orientada a Objetos |
| **Curso** | Análise e Desenvolvimento de Sistemas (3º Período) |
| **Tema** | Documentação Técnica Corporativa, Contratos de Código e Javadoc |
| **Carga Horária** | 100 minutos (2 horas-aula) |
| **Professor Responsável** | Alexandre Neves Louzada |
| **Arquivo de Referência** | `TutorialAula3.md` |

## 1. Objetivos de Aprendizagem

- **Conceitual:** Compreender o papel estratégico da documentação técnica na governança corporativa, na prevenção de dívida técnica em sistemas legados e na formalização de contratos de APIs (precondições, pós-condições e invariantes).
- **Técnico:** Dominar a sintaxe dos comentários de documentação Javadoc (`/** ... */`) e o uso rigoroso das tags estruturadas padronizadas (`@author`, `@version`, `@param`, `@return`, `@throws`, `@see`, `@since`).
- **Operacional:** Gerar portais técnicos de documentação completos em formato HTML por meio da linha de comando do JDK (`javadoc` CLI) com parametrização de codificação UTF-8 e via interfaces de IDEs modernas.
- **Prático:** Aplicar processos de revisão de código em pares (*Code Review*), consumindo e testando classes utilitárias exclusivamente através da documentação HTML gerada e do recurso de documentação rápida (*Quick Documentation*) da IDE.

## 2. Fundamentação Teórica

### Governança Corporativa e Contratos de Código

Em equipes de engenharia de software corporativo, desenvolvedores dedicam uma parcela significativamente maior de tempo lendo, integrando e adaptando código existente do que escrevendo novos arquivos a partir do zero. Bases de software que negligenciam a documentação acumulam dívida técnica severa, elevando o tempo médio de integração de novos membros e tornando manutenções corretivas arriscadas e onerosas.

Para garantir a sustentabilidade do código, adota-se uma separação clara entre duas práticas complementares:

- ***Clean Code* (Código Limpo):** Trata do funcionamento interno e do algoritmo (o "como"), tornando a implementação legível através de classes coesas e nomes autoexplicativos.
- **Javadoc (Documentação de API):** Formaliza o contrato público de uso (o "o quê" e o "porquê"), explicitando precondições de entrada, restrições operacionais, garantias de retorno e o comportamento de exceções disparadas para quem consome o componente sem abrir seu código-fonte.

### Taxonomia dos Comentários em Java

O compilador da linguagem Java reconhece três formatos distintos de comentários:

| Tipo de Comentário | Sintaxe | Finalidade no Desenvolvimento | Processado pelo Javadoc? |
|---|---|---|---|
| Linha Única | `// Comentário` | Notas curtas e pontuais no fluxo | Não |
| Bloco Convencional | `/* Bloco */` | Comentários multilinhas de lógica interna | Não |
| Documentação Javadoc | `/** Estruturado */` | Formalização de contratos públicos de APIs | Sim |

- **Regra de Posicionamento:** O bloco Javadoc (`/** ... */`) deve ser colocado imediatamente antes da declaração de classes, interfaces, métodos, atributos protegidos/públicos ou enums.
- **Anti-Padrão da Indústria:** Nunca use blocos de comentários para tentar explicar ou compensar implementações confusas. Refatore o código para que ele seja limpo e use o Javadoc estritamente para documentar as garantias e exigências do contrato público da API.

### Tags Estruturadas e Convenções do Javadoc

O utilitário `javadoc` realiza a leitura de marcadores padronizados iniciados pelo caractere `@` para estruturar as seções do manual.

**Tags para Classes, Interfaces e Enums:**

- `@author`: Identifica o autor, especialista ou equipe técnica responsável pelo desenvolvimento.
- `@version`: Especifica a versão atual do componente (exemplo: `1.0.0`, `2.1.0-RC1`).
- `@since`: Registra a versão da plataforma ou do sistema na qual aquele recurso foi introduzido.
- `@see`: Cria hiperlinks e referências cruzadas para outras classes, métodos correlatos ou documentações externas.

**Tags para Métodos e Construtores:**

- `@param <identificador>`: Define a semântica, o formato esperado e as restrições obrigatórias de cada argumento de entrada.
- `@return`: Detalha o significado, o tipo e as condições do valor retornado (nunca deve ser utilizado em métodos `void`).
- `@throws` (ou `@exception`): Especifica as condições e regras de negócio que disparam exceções em tempo de execução.

**Ordem de Declaração Padronizada (Convenção Oracle):**

Para manter a consistência corporativa, as seções devem seguir a seguinte sequência lógica:

```plaintext
Sumário Breve → Detalhamento Textual → @param → @return → @throws → @see → @since
```

## 3. Estudo de Caso Integrado: Módulo de Validação Fiscal

O código a seguir apresenta a aplicação dos padrões de documentação técnica em uma classe utilitária de validação de dados fiscais:

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

## 4. Geração do Portal Técnico de Documentação

### Geração via Linha de Comando (CLI)

O JDK inclui a ferramenta `javadoc`, permitindo compilar os comentários em páginas HTML sem intermediários. Para garantir que caracteres com acentuação e símbolos gráficos em língua portuguesa sejam processados corretamente, os parâmetros de codificação UTF-8 devem ser explicitados:

```bash
# Executado a partir da pasta raiz do código-fonte (ex.: src)
javadoc -d docs/ -encoding UTF-8 -docencoding UTF-8 -charset UTF-8 br.edu.universidade.util
```

- **Parâmetro `-d docs/`:** Aponta a pasta de saída onde toda a árvore estática do portal HTML será construída.
- **Parâmetros de Codificação:** `-encoding`, `-docencoding` e `-charset` evitam falhas de renderização de caracteres na web.
- **Resultado:** O comando gera o arquivo `index.html`, além de arquivos CSS e scripts de busca interna.

### Consulta Rápida na IDE (Quick Documentation)

Durante a codificação, o desenvolvedor não precisa alternar para um navegador web para consultar contratos de métodos. Ao posicionar o cursor sobre uma chamada de método e acionar o comando de documentação rápida (atalho **Ctrl + Q** no Windows/Linux ou **F1** no macOS nas IDEs IntelliJ IDEA, Eclipse e VS Code), uma janela suspensa renderiza o Javadoc formatado com todos os parâmetros, exceções e retornos previstos.

## 5. Diagnóstico de Erros Comuns e Más Práticas de Documentação

### Omissão da Tag `@throws` em Métodos com Validação de Entrada

- **Problema:** O método lança exceções de negócio (como `IllegalArgumentException`), mas não possui a tag `@throws` associada.
- **Impacto:** O desenvolvedor consumidor da API desconhece as restrições de chamada e não protege o código adequadamente, provocando paradas não planejadas em produção.
- **Correção:** Toda precondição que gere o disparo de uma exceção deve ser documentada com sua respectiva condição disparadora via `@throws`.

### Documentação Redundante ou Tautológica

- **Problema:** Criar comentários óbvios que apenas repetem os nomes dos métodos e parâmetros (exemplo: `@param nome o nome` ou `@return o resultado`).
- **Impacto:** Não agrega valor técnico e polui a leitura do código.
- **Correção:** Foque em limites, regras de validação e restrições de formato (exemplo: `@param nome texto contendo o nome completo do cliente; não aceita referências nulas ou em branco`).

### Uso Incorreto de `@return` em Métodos `void`

- **Problema:** Inserir `@return void` em métodos que não produzem valor de saída.
- **Impacto:** Gera advertências (*warnings*) durante a execução do comando `javadoc`.
- **Correção:** Métodos `void` não devem conter a tag `@return`.

## 6. Exercício de Fixação Prática: Módulo de Estatística Financeira Documentado

Implemente e documente formalmente uma classe utilitária denominada `CalculadoraFinanceira`:

1. **Definição e Estrutura da Classe:**
   - Crie o arquivo no pacote `br.edu.universidade.financeiro`.
   - Utilize o modificador `final` na declaração da classe e implemente um construtor privado para bloquear instanciações indevidas.
   - Insira o cabeçalho Javadoc contendo descrição técnica de finalidade da classe, `@author`, `@version` e `@since`.

2. **Implementação do Método `calcularJurosCompostos`:**
   - Assinatura: `public static double calcularJurosCompostos(double capital, double taxaMensal, int meses)`
   - Documente cada argumento utilizando a tag `@param`.
   - Documente o valor produzido com a tag `@return`.
   - Utilize a tag `@throws IllegalArgumentException` descrevendo que o método rejeita valores menores que zero para capital, taxas fora do intervalo entre `0.0` e `1.0`, e prazos inferiores a 1 mês.

3. **Implementação do Método `calcularAmortizacaoConstante`:**
   - Crie um método utilitário que receba o saldo devedor e o prazo total em meses para calcular a cota fixa de amortização.
   - Documente todos os parâmetros e precondições de cálculo.

4. **Validação do Portal HTML e Auditoria de Código:**
   - Execute o utilitário `javadoc` via terminal ou acione o gerador de documentação da IDE para compilar o portal em uma pasta `docs/`.
   - Abra o arquivo `index.html` em um navegador web e audite se as tags `@param`, `@return` e `@throws` foram transformadas corretamente nas seções de especificação técnica da API.