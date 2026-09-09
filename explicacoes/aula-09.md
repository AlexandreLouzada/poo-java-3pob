# Explicação da Aula 09 -- Tratamento Defensivo com `try`, `catch`, `finally`, `try-with-resources` e Exceções Checked vs. Unchecked

| | |
|---|---|
| **Disciplina** | Programação Orientada a Objetos |
| **Curso** | Análise e Desenvolvimento de Sistemas (3º Período) |
| **Tema** | Tratamento Defensivo com `try`, `catch`, `finally`, `try-with-resources` e Exceções *Checked* vs. *Unchecked* |
| **Carga Horária** | 100 minutos (2 horas-aula) |
| **Professor** | Alexandre Neves Louzada |
| **Plano de Aula** | `Aulas 3pob/Aula 09.md` |
| **Tutorial** | `aulas/TutorialAula9.md` |
| **Estudo de Caso** | `exemplos/aula-09/` |
| **Exercícios Resolvidos** | `solucoes/aula-09/` |

## 1. Objetivos de Aprendizagem

Esta aula completa o par com a Aula 08: enquanto na aula anterior aprendemos a **diagnosticar** por que e onde o software quebra, nesta aula aprendemos a **blindar a aplicação** para que falhas previsíveis não interrompam a experiência do usuário nem derrubem o serviço.

**Objetivos conceituais:** Compreender a separação estrutural entre falhas previsíveis de infraestrutura/IO checadas pelo compilador (*Checked Exceptions*) e falhas lógicas de tempo de execução não-checadas (*Unchecked / RuntimeExceptions*).

**Objetivos técnicos:** Construir blocos defensivos com `try-catch`, encadeamento seletivo de múltiplos tratadores (regra da especialização para a generalização) e uso do operador *Multi-Catch* (`catch (A | B ex)`); compreender a semântica de garantia de execução do bloco `finally`.

**Objetivos arquiteturais:** Dominar o gerenciamento moderno e determinístico de recursos do sistema operacional utilizando a instrução `try-with-resources` e o contrato da interface `java.lang.AutoCloseable`, eliminando riscos de vazamento de recursos (*resource leaks*).

**Objetivos práticos:** Implementar um processador de arquivos de lote (.csv) resiliente a falhas, capaz de isolar e reportar registros corrompidos em tempo real sem abortar o processamento dos itens válidos subsequentes.

### 1.1 Metodologia Ativa

A aula utiliza um **Game de Refatoração** (de Fragilidade para Resiliência), onde um leitor de arquivos frágil é transformado em um fluxo resiliente, e uma **Demonstração Forense de Fechamento de Recursos** inspecionando no depurador a execução automática do método `close()` em `try-with-resources`, mesmo sob disparo de exceções.

## 2. Conteúdo Teórico Detalhado

### 2.1 Exceções *Checked* vs. *Unchecked*

A divisão entre exceções checadas e não-checadas é uma das decisões arquiteturais mais importantes em Java. A matriz comparativa abaixo esclarece os critérios:

| Critério | Exceções *Checked* (Checadas) | Exceções *Unchecked* (Não-Checadas) |
| :--- | :--- | :--- |
| **Herança direta** | Subclasses de `java.lang.Exception` (exceto `RuntimeException`) | Subclasses de `java.lang.RuntimeException` |
| **Papel do Compilador** | **Obrigatório** tratar (`try-catch`) ou declarar (`throws`) | **Opcional** tratar em tempo de compilação |
| **Natureza da falha** | Condições externas, de infraestrutura ou de IO previsíveis | Falhas de lógica do programador ou violações de contrato |
| **Exemplos típicos** | `IOException`, `SQLException`, `FileNotFoundException` | `NullPointerException`, `IllegalArgumentException`, `IndexOutOfBoundsException` |

A regra mnemônica é:

- Se o código lida com o **"mundo exterior"** (arquivos, banco de dados, rede, periféricos), o compilador Java impõe a checagem obrigatória (*Checked*).
- Se a falha é derivada de um ponteiro nulo ou índice inválido (*Unchecked*), ela deveria ter sido evitada com código defensivo antes de ocorrer.

No contexto de ADS (Análise e Desenvolvimento de Sistemas), uma falha de conexão ou de leitura de arquivo deve gerar *fallback* ou mensagem amigável, nunca uma tela de erro técnico exposta ao usuário.

### 2.2 A Estrutura Básica do `try-catch`

O bloco `try-catch` é o mecanismo fundamental de tratamento de exceções. O bloco `try` monitora um trecho de código que pode disparar uma exceção, e o bloco `catch` define o que fazer caso a exceção ocorra:

```java
try {
    // Código monitorado que pode disparar uma exceção
    System.out.println("Iniciando operação de conversão...");
    int idade = Integer.parseInt(entradaUsuario);
    System.out.println("Idade processada com sucesso: " + idade);
} catch (NumberFormatException ex) {
    // Bloco de contingência executado APENAS se a exceção ocorrer
    System.err.println("Entrada inválida! Informe apenas dígitos numéricos.");
    System.err.println("Log técnico: " + ex.getMessage());
}
```

Se `entradaUsuario` contiver texto não numérico (como "vinte"), o `Integer.parseInt()` dispara `NumberFormatException`. A execução interrompe imediatamente na linha do erro e o fluxo entra no bloco `catch`, que emite a mensagem amigável e o log técnico. Se não houver falha, o bloco `catch` é simplesmente ignorado.

### 2.3 Encadeamento de Múltiplos Tratadores (`catch`)

Quando um trecho de código pode gerar diferentes tipos de exceções, é possível encadear múltiplos blocos `catch`. A regra é: **do mais específico para o mais geral**. O compilador exige que exceções mais específicas (subclasses) sejam capturadas antes das mais genéricas (superclasses):

```java
try {
    // Operação que pode gerar vários tipos de falha
} catch (FileNotFoundException ex) {
    // Mais específica: arquivo não encontrado
} catch (IOException ex) {
    // Mais genérica: qualquer falha de I/O
} catch (Exception ex) {
    // Mais genérica de todas: qualquer exceção
}
```

Se a ordem for invertida (por exemplo, `Exception` antes de `FileNotFoundException`), o compilador gera erro porque o `catch` mais genérico capturaria todas as exceções, tornando os blocos seguintes inalcançáveis.

### 2.4 Multi-Catch (Java 7+)

O operador *Multi-Catch* permite consolidar o tratamento de múltiplos tipos de exceção em um único bloco `catch`, usando o operador pipe (`|`):

```java
try {
    // Operação de parsing
} catch (NumberFormatException | ArithmeticException ex) {
    System.err.println("Erro de parsing ou aritmético: " + ex.getMessage());
}
```

Isso é útil quando diferentes exceções devem receber o mesmo tratamento. No entanto, deve ser usado com moderação, pois pode mascarar a causa real da falha se for muito abrangente.

### 2.5 O Bloco `finally`

O bloco `finally` é executado **compulsoriamente** independentemente de ter ocorrido exceção ou não. Ele é ideal para liberação de recursos (fechar arquivos, conexões, etc.):

```java
BufferedReader leitor = null;
try {
    leitor = new BufferedReader(new FileReader("dados.csv"));
    // processar arquivo
} catch (IOException ex) {
    System.err.println("Falha de I/O: " + ex.getMessage());
} finally {
    if (leitor != null) {
        try {
            leitor.close();
        } catch (IOException ex) {
            System.err.println("Erro ao fechar leitor: " + ex.getMessage());
        }
    }
}
```

O problema desse padrão é a verbosidade: é necessário verificar `null` e tratar possíveis exceções no próprio `finally`, o que torna o código propenso a erros e difícil de manter.

### 2.6 `try-with-resources` (Java 7+)

O `try-with-resources` resolve o problema de gerenciamento manual de recursos. Qualquer objeto que implemente a interface `java.lang.AutoCloseable` pode ser declarado entre parênteses após o `try`, e o compilador gera automaticamente a chamada ao método `close()` ao final do bloco, mesmo se ocorrer uma exceção:

```java
try (BufferedReader leitor = new BufferedReader(new FileReader("dados.csv"))) {
    String linha;
    while ((linha = leitor.readLine()) != null) {
        // processar linha
    }
} catch (IOException ex) {
    System.err.println("Falha de I/O: " + ex.getMessage());
}
// leitor.close() é chamado automaticamente aqui
```

A interface `AutoCloseable` é implementada por todas as classes de recursos do Java (*streams*, *readers*, *writers*, conexões de banco, etc.). O método `close()` é executado mesmo se uma exceção for lançada dentro do bloco `try`.

### 2.7 Regra de Especialização para Generalização

Quando se usam múltiplos blocos `catch`, a ordem deve ser **do mais específico para o mais geral**. A razão é que a JVM testa os blocos de cima para baixo e executa o primeiro que corresponder. Um `catch (Exception ex)` capturaria qualquer exceção, tornando inúteis todos os blocos abaixo dele.

## 3. Estudo de Caso Aplicado

O estudo de caso desta aula é um **processador de transações em lote** localizado em `exemplos/aula-09/src/br/edu/universidade/sistema/lote/ProcessadorTransacoesLote.java`. Ele demonstra todas as técnicas de tratamento defensivo em um único fluxo de processamento de arquivos CSV.

### 3.1 ProcessadorTransacoesLote.java

```java
package br.edu.universidade.sistema.lote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class ProcessadorTransacoesLote {

    public static void main(String[] args) {
        String dadosCsvSimulados = """
                ID;VALOR;PARCELAS
                101;1500.50;3
                102;TEXTO_INVALIDO;2
                103;2400.00;0
                104;980.00;4
                """;

        processarArquivo(dadosCsvSimulados);
    }

    public static void processarArquivo(String conteudo) {
        System.out.println("========== INICIANDO PROCESSAMENTO RESILIENTE ==========");

        try (BufferedReader leitor = new BufferedReader(new StringReader(conteudo))) {
            String linha = leitor.readLine(); // Descarta cabeçalho
            int numeroLinha = 1;

            while ((linha = leitor.readLine()) != null) {
                numeroLinha++;
                if (linha.isBlank()) continue;

                // Bloco defensivo interno: isola a falha por registro sem quebrar o laço
                try {
                    processarLinhaTransacao(linha, numeroLinha);
                } catch (NumberFormatException | ArithmeticException ex) {
                    System.err.printf("[ERRO LINHA %d] Registro corrompido descartado: %s | Causa: %s%n",
                            numeroLinha, linha, ex.getMessage());
                }
            }

        } catch (IOException ex) {
            System.err.println("Falha crítica de I/O no acesso ao arquivo: " + ex.getMessage());
        } finally {
            System.out.println("Auditoria: Ciclo de leitura finalizado pelo sistema.");
        }

        System.out.println("================ PROCESSAMENTO CONCLUÍDO ================");
    }

    private static void processarLinhaTransacao(String linha, int linhaOrigem) {
        String[] campos = linha.split(";");

        long id = Long.parseLong(campos[0].trim());
        double valorTotal = Double.parseDouble(campos[1].trim());
        int parcelas = Integer.parseInt(campos[2].trim());

        if (parcelas <= 0) {
            throw new ArithmeticException("Quantidade de parcelas deve ser estritamente maior que zero.");
        }

        double valorParcela = valorTotal / parcelas;
        System.out.printf("[SUCESSO] Transação #%d aprovada: %d parcelas de R$ %.2f%n",
                id, parcelas, valorParcela);
    }
}
```

### 3.2 Análise da Arquitetura de Tratamento

Este exemplo demonstra **três níveis de tratamento**:

1. **Nível externo** (`try-with-resources` + `catch (IOException)`): gerencia o recurso `BufferedReader` e captura falhas de I/O do sistema operacional (*Checked Exception*). O `finally` registra a auditoria de ciclo de vida.

2. **Nível interno** (`try-catch` com *Multi-Catch*): isola a falha de **cada registro individual** sem quebrar o laço de processamento. O `catch (NumberFormatException | ArithmeticException ex)` trata tanto erros de *parsing* quanto violações de regras de negócio (parcelas <= 0).

3. **Nível de lógica** (`processarLinhaTransacao`): valida os campos, parseia valores e dispara `ArithmeticException` quando o número de parcelas é inválido.

O resultado é um processador **resiliente**: registros corrompidos são descartados com mensagem de erro, mas o processamento continua normalmente para os registros válidos. Após o laço, o `finally` garante que a auditoria de ciclo seja executada, e a mensagem final confirma a conclusão.

### 3.3 Análise dos Dados de Entrada

O CSV simulado contém quatro registros:

| Linha | ID | VALOR | PARCELAS | Resultado |
| :--- | :--- | :--- | :--- | :--- |
| 101 | 101 | 1500.50 | 3 | SUCESSO: 3 parcelas de R$ 500.17 |
| 102 | 102 | TEXTO_INVALIDO | 2 | ERRO: NumberFormatException (parse de "TEXTO_INVALIDO" para double) |
| 103 | 103 | 2400.00 | 0 | ERRO: ArithmeticException (parcelas <= 0) |
| 104 | 104 | 980.00 | 4 | SUCESSO: 4 parcelas de R$ 245.00 |

Apenas os registros 101 e 104 são processados com sucesso. Os registros 102 e 103 são descartados com mensagens de erro, e o processamento continua.

## 4. Exercícios Propostos e Solução

O exercício consiste em criar um **serviço de importação de cadastro de clientes** em lote, resiliente a registros inválidos. A solução está em `solucoes/aula-09/src/br/edu/universidade/sistema/cadastro/`.

### 4.1 RegistroCliente.java

```java
package br.edu.universidade.sistema.cadastro;

public class RegistroCliente {
    private Long id;
    private String nome;
    private int idade;
    private double limiteCredito;

    public RegistroCliente(Long id, String nome, int idade, double limiteCredito) {
        if (id == null) {
            throw new IllegalArgumentException("ID do cliente não pode ser nulo.");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do cliente não pode ser vazio.");
        }
        if (idade < 0) {
            throw new IllegalArgumentException("Idade do cliente não pode ser negativa: " + idade);
        }
        if (limiteCredito < 0.0) {
            throw new IllegalArgumentException("Limite de crédito não pode ser negativo: " + limiteCredito);
        }
        this.id = id;
        this.nome = nome;
        this.idade = idade;
        this.limiteCredito = limiteCredito;
    }

    public void exibirDados() {
        System.out.printf("Cliente [ID: %d | Nome: %s | Idade: %d | Limite: R$ %.2f]%n",
                id, nome, idade, limiteCredito);
    }
}
```

O construtor de `RegistroCliente` implementa validação defensiva, disparando `IllegalArgumentException` (*Unchecked*) quando os dados são inválidos. Isso é intencional: são violações de pré-condições que o chamador deveria ter evitado.

### 4.2 ImportadorCadastroService.java

```java
package br.edu.universidade.sistema.cadastro;

import java.io.BufferedReader;
import java.io.StringReader;

public class ImportadorCadastroService {

    public void importarLote(String dadosBrutos) {
        int totalLinhas = 0;
        int cadastrosSucesso = 0;

        try (BufferedReader leitor = new BufferedReader(new StringReader(dadosBrutos))) {
            String linha;
            while ((linha = leitor.readLine()) != null) {
                totalLinhas++;
                int numeroLinha = totalLinhas;
                try {
                    String[] campos = linha.split(",");
                    Long id = Long.parseLong(campos[0].trim());
                    String nome = campos[1].trim();
                    int idade = Integer.parseInt(campos[2].trim());
                    double limiteCredito = Double.parseDouble(campos[3].trim());

                    RegistroCliente cliente = new RegistroCliente(id, nome, idade, limiteCredito);
                    cliente.exibirDados();
                    cadastrosSucesso++;
                } catch (IllegalArgumentException ex) {
                    System.out.println("[LINHA " + numeroLinha + " DESCARTADA] Causa: " + ex.getMessage());
                }
            }
        } catch (Exception ex) {
            System.err.println("Falha estrutural na leitura do lote: " + ex.getMessage());
        } finally {
            System.out.printf("%n--- RELATÓRIO CONSOLIDADO ---%n");
            System.out.printf("Total de linhas lidas: %d%n", totalLinhas);
            System.out.printf("Cadastros processados com sucesso: %d%n", cadastrosSucesso);
            System.out.printf("Linhas descartadas: %d%n", (totalLinhas - cadastrosSucesso));
        }
    }
}
```

O serviço demonstra o padrão de **processamento resiliente em lote**: o `try-with-resources` garante o fechamento do leitor, o `try-catch` interno isola falhas por registro, e o `finally` emite um relatório consolidado com estatísticas de processamento.

### 4.3 CadastroApp.java

```java
package br.edu.universidade.sistema.cadastro;

public class CadastroApp {
    public static void main(String[] args) {
        String massaTeste = """
                101,Ana Clara Souza,28,5000.00
                102,Carlos Eduardo,35,12000.50
                103,Beatriz Costa,vinte,8000.00
                104,Mariana Silva,32,-1500.00
                105,Lucas Mendes,41,20000.00
                """;

        ImportadorCadastroService importador = new ImportadorCadastroService();
        importador.importarLote(massaTeste);
    }
}
```

A massa de teste contém dois registros inválidos:

| Linha | ID | Nome | Idade | Limite | Resultado |
| :--- | :--- | :--- | :--- | :--- | :--- |
| 1 | 101 | Ana Clara Souza | 28 | 5000.00 | SUCESSO |
| 2 | 102 | Carlos Eduardo | 35 | 12000.50 | SUCESSO |
| 3 | 103 | Beatriz Costa | vinte | 8000.00 | DESCARTADA: idade inválida (NumberFormatException) |
| 4 | 104 | Mariana Silva | 32 | -1500.00 | DESCARTADA: limite negativo (IllegalArgumentException) |
| 5 | 105 | Lucas Mendes | 41 | 20000.00 | SUCESSO |

## 5. Perguntas de Revisão

1. Qual é a diferença entre uma `Checked Exception` e uma `Unchecked Exception` em termos de obrigação do compilador?
2. Por que a ordem dos blocos `catch` deve ser do mais específico para o mais generalizado?
3. O que é o *Multi-Catch* e quando é apropriado usá-lo?
4. Em quais situações o bloco `finally` não é executado? (Dica: há exceções especiais.)
5. Qual é a vantagem do `try-with-resources` em relação ao tratamento manual com `finally`?
6. Por que `IllegalArgumentException` é tipicamente uma `Unchecked Exception` e não uma `Checked Exception`?
7. O que acontece se você tentar usar `try-with-resources` com uma classe que não implementa `AutoCloseable`?
8. Qual é o padrão ideal de tratamento quando se processa um lote de registros onde cada registro pode falhar independentemente?
9. Por que não é recomendável usar `catch (Exception ex)` como primeiro bloco de captura?
10. Qual é a diferença entre `RuntimeException` e `Exception` na hierarquia `Throwable`?

## 6. Resumo / Pontos-Chave

- **Checked Exceptions** são obrigatoriamente tratadas ou declaradas pelo compilador. Elas representam falhas previsíveis de infraestrutura/IO que a aplicação deve prever.
- **Unchecked Exceptions** (subclasses de `RuntimeException`) não são checadas pelo compilador. Elas representam falhas de lógica do programador que deveriam ser evitadas com código defensivo.
- O bloco **`try-catch`** isola trechos de código que podem gerar exceções e define tratamento alternativo. Múltiplos `catch` devem ir do mais específico para o mais geral.
- **Multi-Catch** (`catch (A | B ex)`) consolida o tratamento de múltiplos tipos de exceção em um único bloco.
- O bloco **`finally`** é executado compulsoriamente, independente de exceções. É ideal para liberação de recursos.
- **`try-with-resources`** gerencia automaticamente o fechamento de recursos que implementam `AutoCloseable`, eliminando verbosidade e riscos de vazamento.
- O padrão de **processamento resiliente em lote** combina `try-with-resources` no nível externo com `try-catch` interno por registro, permitindo que falhas individuais não abortem o processamento geral.
- O estudo de caso demonstra um processador CSV que isola registros corrompidos, continua processando os válidos, e emite um relatório consolidado no `finally`.
