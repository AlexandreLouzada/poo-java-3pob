# Tutorial de Java — Aula 09: Tratamento Defensivo com try, catch, finally, try-with-resources e Exceções Checked vs. Unchecked

| | |
|---|---|
| **Disciplina** | Programação Orientada a Objetos |
| **Curso** | Análise e Desenvolvimento de Sistemas (3º Período) |
| **Tema** | Tratamento Defensivo com try, catch, finally, try-with-resources e Exceções Checked vs. Unchecked |
| **Carga Horária** | 100 minutos (2 horas-aula) |
| **Professor Responsável** | Alexandre Neves Louzada |
| **Arquivo de Referência** | `TutorialAula9.md` |

## 1. Objetivos de Aprendizagem

- **Conceitual:** Compreender a separação estrutural entre falhas previsíveis de infraestrutura/IO checadas pelo compilador (*Checked Exceptions*) e falhas lógicas de tempo de execução não-checadas (*Unchecked* / `RuntimeExceptions`).
- **Técnico:** Construir blocos defensivos com `try-catch`, encadeamento seletivo de múltiplos tratadores (regra da especialização para a generalização) e aplicação do operador Multi-Catch (`catch (A | B ex)`); assimilar a semântica de garantia absoluta de execução do bloco `finally`.
- **Arquitetural:** Dominar o gerenciamento moderno e determinístico de recursos do sistema operacional com a instrução `try-with-resources` e o contrato da interface `java.lang.AutoCloseable`, eliminando riscos de vazamento de recursos (*resource leaks*).
- **Prático:** Implementar um leitor e processador em lote de arquivos estruturados (.csv) tolerante a falhas, capaz de isolar registros corrompidos sem interromper o processamento dos itens válidos subsequentes.

## 2. Fundamentação Teórica

### A Grande Divisão: Exceções Checked vs. Unchecked

Na plataforma Java, as exceções que herdam de `java.lang.Exception` dividem-se em duas categorias conceituais com exigências de compilação distintas:

| Critério | Exceções Checadas (Checked Exceptions) | Exceções Não-Checadas (Unchecked Exceptions) |
|---|---|---|
| Hierarquia Direta | Subclasses de `java.lang.Exception` (exceto `RuntimeException`) | Subclasses de `java.lang.RuntimeException` |
| Papel do Compilador | Obrigatório tratar via `try-catch` ou declarar via `throws` | Opcional em tempo de compilação |
| Natureza da Falha | Fatores externos, indisponibilidade de IO, arquivos, banco de dados ou rede | Erros de lógica de programação ou quebras de contrato de domínio |
| Exemplos Típicos | `IOException`, `SQLException`, `FileNotFoundException` | `NullPointerException`, `IllegalArgumentException`, `IndexOutOfBoundsException` |

**Regra Prática da Linguagem:** Se a rotina interage com recursos fora dos limites seguros da memória da JVM (disco, conexões de socket, drivers periféricos), o compilador impõe o contrato de checagem compulsória (*Checked*). Se a falha é derivada de uma operação matemática ilegal ou desreferenciação de ponteiro nulo (*Unchecked*), ela decorre de fragilidade algorítmica e deve ser contida com código defensivo antes do disparo.

### O Bloco Defensivo Fundamental: try, catch e finally

- **`try` (Monitoramento):** Delimita o escopo de instruções sensíveis sujeitas ao disparo de anomalias. Se uma exceção ocorrer dentro dele, o fluxo normal é interrompido imediatamente no ponto da falha.
- **`catch` (Tratador de Contingência):** Intercepta instâncias de exceção compatíveis com o tipo declarado na assinatura do bloco. Podem existir múltiplos blocos `catch` encadeados.
  - **Regra de Especialização:** Os tratadores devem ser declarados obrigatoriamente do subtipo mais específico para o mais genérico. Declarar `catch (Exception ex)` no topo mascara os blocos inferiores e acarreta erro de compilação (*unreachable code*).
  - **Operador Multi-Catch (Java 7+):** Permite consolidar tipos irmãos disjuntos sem duplicação de blocos de contingência: `catch (NumberFormatException | NullPointerException ex)`.
- **`finally` (Garantia de Execução):** Bloco executado compulsoriamente após o `try` ou após o `catch`, mesmo que ocorram retornos antecipados (`return`) ou novas exceções não capturadas. Era o mecanismo tradicional para liberação de buffers e fechamento manual de conexões com o sistema operacional.

### Gerenciamento Moderno: O Padrão try-with-resources

Antes do Java 7, liberar recursos externos no bloco `finally` era verboso e suscetível a erros, pois a chamada do método `.close()` também lançava `IOException`, exigindo blocos `try-catch` aninhados e podendo ocultar a exceção principal da regra de negócio.

O padrão `try-with-resources` resolve esse problema ao permitir que qualquer objeto que implemente o contrato da interface `java.lang.AutoCloseable` seja declarado diretamente entre parênteses logo após o comando `try`:

```java
// O recurso é instanciado na abertura e seu método close() é acionado deterministicamente ao sair do bloco
try (BufferedReader leitor = Files.newBufferedReader(caminhoArquivo)) {
    String linha = leitor.readLine();
    System.out.println(linha);
} catch (IOException ex) {
    System.err.println("Falha na leitura física: " + ex.getMessage());
}
```

**Garantia de Desalocação:** Mesmo que uma exceção seja disparada durante a leitura das linhas, a JVM assegura a execução implícita do método `.close()`, eliminando o risco de bloqueio de arquivos (*file locking*) ou vazamento de descritores de sistema operacional (*resource leak*).

## 3. Estudo de Caso Integrado: Processador Resiliente de Lotes (.csv)

O código abaixo ilustra o processamento em lote de um arquivo de transações financeiras. O algoritmo emprega `try-with-resources` para isolar a leitura física do arquivo e usa blocos defensivos locais internos para que uma linha corrompida não aborte a leitura dos registros subsequentes:

```java
package br.edu.universidade.sistema.lote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class ProcessadorTransacoesLote {

    public static void main(String[] args) {
        // Simulação de conteúdo de arquivo tabular .csv com dados íntegros e registros corrompidos
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

        // try-with-resources: garante o fechamento determinístico do fluxo AutoCloseable
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
                    // Multi-Catch consolidando erros de parsing numérico e regras aritméticas
                    System.err.printf("[ERRO LINHA %d] Registro corrompido descartado: %s | Causa: %s%n",
                            numeroLinha, linha, ex.getMessage());
                }
            }

        } catch (IOException ex) {
            // Tratamento obrigatório para falha física (Checked Exception)
            System.err.println("Falha crítica de I/O no acesso ao arquivo: " + ex.getMessage());
        } finally {
            // Executado compulsoriamente para auditoria de ciclo de vida
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
            // Disparo de falha aritmética sob condição de negócio inválida
            throw new ArithmeticException("Quantidade de parcelas deve ser estritamente maior que zero.");
        }

        double valorParcela = valorTotal / parcelas;
        System.out.printf("[SUCESSO] Transação #%d aprovada: %d parcelas de R$ %.2f%n",
                id, parcelas, valorParcela);
    }
}
```

## 4. Diagnóstico de Erros Comuns e Armadilhas

### Armadilha 1: Inversão na Ordem de Especialização dos catch

**Código Problemático:**

```java
try {
    int valor = Integer.parseInt("abc");
} catch (Exception ex) { // Captura tudo primeiro!
    System.out.println("Erro genérico");
} catch (NumberFormatException ex) { // ERRO DE COMPILAÇÃO!
    System.out.println("Erro de parsing");
}
```

- **Diagnóstico da JVM:** `exception NumberFormatException has already been caught`.
- **Causa & Correção:** Como `NumberFormatException` herda de `Exception`, o primeiro tratador já a interceptaria. A subclasse especializada deve sempre vir antes da superclasse genérica.

### Armadilha 2: Tentar Usar Recursos sem AutoCloseable no try-with-resources

**Código Problemático:**

```java
try (String texto = "Arquivo de Log") { // ERRO DE COMPILAÇÃO!
    System.out.println(texto);
}
```

- **Diagnóstico da JVM:** `incompatible types: try-with-resources statement cannot refer to java.lang.String; java.lang.String cannot be converted to java.lang.AutoCloseable.`
- **Causa & Correção:** A declaração entre parênteses do `try-with-resources` restringe-se exclusivamente a classes que assinam o contrato de `java.lang.AutoCloseable` ou `java.io.Closeable`.

### Armadilha 3: Declarar Tipos com Relação de Herança no Mesmo Multi-Catch

**Código Problemático:**

```java
try {
    executar();
} catch (FileNotFoundException | IOException ex) { // ERRO DE COMPILAÇÃO!
    System.err.println("Erro");
}
```

- **Diagnóstico da JVM:** `Types in multi-catch must be alternatives: java.io.FileNotFoundException is a subclass of java.io.IOException.`
- **Causa & Correção:** O operador Multi-Catch (`|`) aceita apenas tipos disjuntos e alternativos. Se um tipo herda do outro, declare apenas a superclasse.

## 5. Roteiro Prático de Depuração: Verificação de AutoCloseable na IDE

Para inspecionar o fechamento automático de arquivos pelo compilador em sua IDE (IntelliJ IDEA, Eclipse ou VS Code):

1. Construa uma classe simples `ConexaoCustomizada` que implemente `AutoCloseable`:
   ```java
   public class ConexaoCustomizada implements AutoCloseable {
       public void executar() { System.out.println("Executando transação..."); }
       @Override
       public void close() { System.out.println("Recurso liberado com sucesso via close()!"); }
   }
   ```
2. Instancie-a dentro de um `try-with-resources` no método `main`:
   ```java
   try (ConexaoCustomizada conexao = new ConexaoCustomizada()) {
       conexao.executar();
       // Simule um erro deliberado:
       throw new RuntimeException("Falha forçada!");
   } catch (RuntimeException ex) {
       System.out.println("Capturado no catch: " + ex.getMessage());
   }
   ```
3. Posicione um ponto de interrupção (*breakpoint*) na linha do `throw` e outro dentro do método `close()`.
4. Execute em modo de depuração (*Debug*): observe que, antes de a execução saltar para o bloco `catch`, a JVM desvia o fluxo e executa obrigatoriamente as instruções do método `close()`, garantindo a limpeza determinística do ambiente.

## 6. Exercício de Fixação Prática: Ingestão Resiliente de Arquivo de Clientes

Implemente um componente de importação cadastral de clientes aplicando os mecanismos de tolerância a falhas:

1. **Construa a Classe `RegistroCliente`:**
   - Atributos encapsulados (`private`): `id` (`Long`), `nome` (`String`), `idade` (`int`) e `limiteCredito` (`double`).
   - Construtor parametrizado completo com regras de guarda.
   - Método descritivo `exibirDados()` formatando o limite de crédito com `%.2f`.

2. **Construa o Serviço `ImportadorCadastroService`:**
   - Crie o método `public void importarLote(String dadosBrutos)` que receba uma `String` multilinha simulando linhas tabulares separadas por vírgula.
   - Utilize `try-with-resources` envolvendo um `BufferedReader` sobre um `StringReader`.
   - No laço de leitura de cada linha, insira um bloco defensivo com Multi-Catch capturando `NumberFormatException` (idades ou limites não numéricos) e `IllegalArgumentException` (validações de precondições do cliente).
   - Ao interceptar uma falha, registre no console o número da linha descartada e sua causa, permitindo que a importação prossiga para os clientes válidos subsequentes.
   - Inclua um bloco `finally` que apresente no console o número total de linhas lidas e o total de cadastros processados com sucesso.

3. **Construa a Classe Executável `CadastroApp`:**
   - Forneça uma massa de testes contendo ao menos duas linhas válidas, uma linha com idade textual inválida (ex.: "vinte") e uma linha com limite negativo.
   - Execute a rotina e comprove no console que os erros foram contidos individualmente e que o relatório consolidado final foi emitido pelo bloco `finally`.