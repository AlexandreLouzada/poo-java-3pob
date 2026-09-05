# Tutorial de Java — Aula 20: Entrada e Saída Moderna de Arquivos (Java I/O e NIO.2)

| | |
|---|---|
| **Disciplina** | Programação Orientada a Objetos |
| **Curso** | Análise e Desenvolvimento de Sistemas (3º Período) |
| **Tema** | Evolução do I/O clássico para o NIO.2 (`java.nio.file`), Abstrações `Path` e `Paths`, Operações Utilitárias com a Classe `Files`, Leitura e Escrita Atômica, Processamento de Arquivos com Streams e Tratamento de Exceções de Sistema de Arquivos |
| **Carga Horária** | 100 minutos (2 horas-aula) |
| **Professor Responsável** | Alexandre Neves Louzada |
| **Arquivo de Referência** | `TutorialAula20.md` |

## 1. Objetivos de Aprendizagem

- **Conceitual:** Compreender as limitações estruturais da biblioteca clássica de entrada e saída (`java.io.File`) e as melhorias introduzidas pela especificação JSR 203 (NIO.2 no Java 7); assimilar a separação entre a localização de um recurso no sistema de arquivos (`Path`) e as operações de manipulação física (`Files`).
- **Técnico:** Manipular caminhos absolutos e relativos através da interface `java.nio.file.Path`; executar operações utilitárias de alta performance na classe `java.nio.file.Files` (criação de diretórios, verificação de existência, cópia, movimentação, exclusão atômica e leitura/gravação de arquivos); integrar a leitura sob demanda de arquivos gigantes com a Streams API via `Files.lines()`.
- **Arquitetural:** Prevenir vazamento de descritores de arquivos (*file descriptor leaks*) e travamento de concorrência no sistema operacional (*file locks*) combinando `try-with-resources` com fluxos de I/O; tratar adequadamente a hierarquia de falhas físicas checadas derivadas de `java.io.IOException` (como `NoSuchFileException` e `AccessDeniedException`).
- **Prático:** Implementar um serviço de ingestão e arquivamento corporativo de transações financeiras em formato tabular (.csv), gravando logs em disco, arquivando cópias de segurança com timestamp e processando grandes volumes de dados de forma preguiçosa (*lazy*).

## 2. Fundamentação Teórica

### Limitações do Java I/O Clássico (java.io.File)

A classe `java.io.File`, introduzida no Java 1.0, supriu as necessidades iniciais da linguagem, mas acumulou restrições severas para ambientes corporativos modernos:

- **Tratamento Frágil de Erros:** Diversos métodos críticos (como `file.delete()` ou `file.mkdir()`) retornam apenas um valor booleano (`true` ou `false`) em caso de falha. Se a operação falhar, o desenvolvedor não tem como saber programaticamente se o motivo foi falta de permissão, arquivo inexistente ou bloqueio por outro processo do sistema operacional.
- **Falta de Suporte a Recursos Avançados:** Incapacidade de lidar com atributos específicos de sistemas operacionais modernos, como links simbólicos (*symlinks*), permissões POSIX detalhadas e listas de controle de acesso (ACLs).
- **Desempenho Ruim em Escala:** A listagem de arquivos em diretórios com milhares de itens carregava todos os nomes de uma só vez na memória em um array de `String`, gerando picos de consumo de memória no Heap.

### A Revolução do Java NIO.2 (java.nio.file)

Com a chegada do Java 7, o pacote `java.nio.file` reformulou completamente a arquitetura de I/O em torno de dois pilares:

```plaintext
┌──────────────────────────────────────┬──────────────────────────────────────┐
│ Interface java.nio.file.Path         │ Classe Utilitária java.nio.file.Files│
├──────────────────────────────────────┼──────────────────────────────────────┤
│ • Representa a localização abstrata  │ • Realiza o trabalho operacional     │
│ • Substitui java.io.File             │ • Cria, copia, move e deleta         │
│ • Manipula nomes, pastas e caminhos  │ • Dispara exceções ricas em erros    │
│ • Não acessa o disco diretamente     │ • Integra diretamente com Streams API│
└──────────────────────────────────────┴──────────────────────────────────────┘
```

A interface `Path` é um modelo imutável que representa uma localização hierárquica no sistema de arquivos (como `C:\dados\relatorio.csv` ou `/var/log/app.log`). A obtenção de instâncias é feita de forma fluida via método de fábrica estático `Path.of(...)` (Java 11+) ou `Paths.get(...)` (Java 7+):

```java
// Obtenção de caminho multiplataforma (independente de barra invertida ou normal)
Path caminho = Path.of("dados", "financeiro", "extrato.csv");
```

A classe `Files` concentra métodos estáticos utilitários que recebem instâncias de `Path` como parâmetro para interagir com o sistema de arquivos real.

### Modos de Abertura e Opções Padrão (StandardOpenOption)

Ao gravar ou anexar conteúdo em arquivos, o enum `StandardOpenOption` permite parametrizar com precisão o comportamento desejado:

- `CREATE`: Cria o arquivo caso ele ainda não exista.
- `CREATE_NEW`: Cria um novo arquivo, disparando `FileAlreadyExistsException` se ele já existir (garantia de criação atômica).
- `TRUNCATE_EXISTING`: Se o arquivo já existir, trunca seu tamanho para 0 bytes antes de gravar o novo conteúdo.
- `APPEND`: Abre o arquivo preservando os dados anteriores e anexa novos bytes ao final da estrutura.

### Processamento Sob Demanda (Lazy) de Grandes Arquivos com Files.lines()

Ao lidar com arquivos que contêm centenas de megabytes ou múltiplos gigabytes, ler todo o conteúdo de uma vez para a memória através de `Files.readAllLines()` é um erro grave de arquitetura que resulta em `OutOfMemoryError`:

> `Files.readAllLines()` → Carrega TUDO → `List<String>` no Heap (Esgotamento de Memória)
> `Files.lines()` → Demanda Sob Medida → `Stream<String>` via Iterator (Consumo Estável)

O método utilitário `Files.lines(Path)` lê o arquivo linha por linha sob demanda utilizando a Streams API. O consumo de memória permanece estável em poucos kilobytes, independentemente de o arquivo ter 10 linhas ou 10 milhões de linhas.

**Regra Crítica de Engenharia:** Como `Files.lines()` mantém um descritor de arquivo aberto junto ao sistema operacional, a chamada DEVE ser encapsulada obrigatoriamente dentro de um bloco `try-with-resources` para assegurar o fechamento do canal ao final do processamento.

## 3. Estudo de Caso Integrado: Sistema de Ingestão e Arquivamento de Lançamentos

O projeto corporativo abaixo implementa um serviço de ingestão de arquivos tabulares (.csv), gerando diretórios dinâmicos, escrevendo relatórios com codificação UTF-8, processando registros sob demanda e arquivando cópias de auditoria:

```java
package br.edu.universidade.sistema.io.dominio;

import java.time.LocalDate;
import java.util.Objects;

// 1. Entidade de Domínio representando o Registro de Movimentação
public class MovimentacaoBancaria {
    private final Long id;
    private final String contaOrigem;
    private final String tipo; // DEBITO ou CREDITO
    private final double valor;
    private final LocalDate data;

    public MovimentacaoBancaria(Long id, String contaOrigem, String tipo, double valor, LocalDate data) {
        if (id == null || valor <= 0.0) {
            throw new IllegalArgumentException("Dados de movimentação inconsistentes.");
        }
        this.id = id;
        this.contaOrigem = contaOrigem;
        this.tipo = tipo.toUpperCase();
        this.valor = valor;
        this.data = data;
    }

    public Long getId() { return id; }
    public String getContaOrigem() { return contaOrigem; }
    public String getTipo() { return tipo; }
    public double getValor() { return valor; }
    public LocalDate getData() { return data; }

    // Converte a entidade para formato tabular CSV delimitado por ponto e vírgula
    public String toCsv() {
        return String.format("%d;%s;%s;%.2f;%s", id, contaOrigem, tipo, valor, data);
    }

    // Fábrica estática para criar a entidade a partir de uma linha de texto do CSV
    public static MovimentacaoBancaria fromCsv(String linha) {
        String[] partes = linha.split(";");
        return new MovimentacaoBancaria(
                Long.parseLong(partes[0].trim()),
                partes[1].trim(),
                partes[2].trim(),
                Double.parseDouble(partes[3].trim().replace(",", ".")),
                LocalDate.parse(partes[4].trim())
        );
    }

    @Override
    public String toString() {
        return String.format("[#%04d] Conta: %-8s | Tipo: %-7s | Valor: R$ %8.2f | Data: %s",
                id, contaOrigem, tipo, valor, data);
    }
}
```

```java
package br.edu.universidade.sistema.io.service;

import br.edu.universidade.sistema.io.dominio.MovimentacaoBancaria;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

// 2. Serviço de Gerenciamento de Arquivos com NIO.2
public class IngestaoArquivoService {

    private final Path diretorioBase;
    private final Path diretorioBackup;

    public IngestaoArquivoService(String nomePastaRaiz) throws IOException {
        this.diretorioBase = Path.of(nomePastaRaiz);
        this.diretorioBackup = this.diretorioBase.resolve("backup");

        // Cria a árvore de diretórios caso ela não exista fisicamente no disco
        if (Files.notExists(this.diretorioBase)) {
            Files.createDirectories(this.diretorioBase);
        }
        if (Files.notExists(this.diretorioBackup)) {
            Files.createDirectories(this.diretorioBackup);
        }
    }

    // Grava uma lista de movimentações em um arquivo CSV formatado em UTF-8
    public Path exportarMovimentacoes(List<MovimentacaoBancaria> registros, String nomeArquivo) throws IOException {
        Path arquivoDestino = this.diretorioBase.resolve(nomeArquivo);

        // Prepara as linhas de texto com cabeçalho tabular
        List<String> linhas = Stream.concat(
                Stream.of("ID;CONTA;TIPO;VALOR;DATA"),
                registros.stream().map(MovimentacaoBancaria::toCsv)
        ).toList();

        // Escrita atômica e segura com criação/truncamento automático
        Files.write(arquivoDestino, linhas, StandardCharsets.UTF_8,
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

        return arquivoDestino;
    }

    // Processamento sob demanda (lazy) de arquivo massivo utilizando Files.lines()
    public double calcularVolumeTotalCreditos(Path arquivoCsv) throws IOException {
        if (!Files.exists(arquivoCsv)) {
            throw new NoSuchFileException("Arquivo de entrada não localizado: " + arquivoCsv);
        }

        // try-with-resources é mandatória para fechar o canal de I/O da Stream
        try (Stream<String> fluxoLinhas = Files.lines(arquivoCsv, StandardCharsets.UTF_8)) {
            return fluxoLinhas
                    .skip(1) // Pula a primeira linha (cabeçalho)
                    .filter(linha -> !linha.isBlank())
                    .map(MovimentacaoBancaria::fromCsv)
                    .filter(mov -> mov.getTipo().equalsIgnoreCase("CREDITO"))
                    .mapToDouble(MovimentacaoBancaria::getValor)
                    .sum();
        }
    }

    // Move e arquiva o arquivo processado inserindo timestamp no nome
    public Path arquivarArquivoProcessado(Path arquivoOriginal) throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String novoNome = "processado_" + timestamp + "_" + arquivoOriginal.getFileName().toString();
        Path destinoFinal = this.diretorioBackup.resolve(novoNome);

        // Move o arquivo com suporte a sobrescrita caso necessário
        return Files.move(arquivoOriginal, destinoFinal, StandardCopyOption.REPLACE_EXISTING);
    }
}
```

```java
package br.edu.universidade.sistema.io;

import br.edu.universidade.sistema.io.dominio.MovimentacaoBancaria;
import br.edu.universidade.sistema.io.service.IngestaoArquivoService;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

// 3. Aplicação Executável demonstrando o ciclo completo de I/O moderno
public class ArquivosNioApp {
    public static void main(String[] args) {
        System.out.println("========== SISTEMA DE INGESTÃO DE ARQUIVOS NIO.2 ==========");

        try {
            // Inicializa o serviço preparando os diretórios em disco
            IngestaoArquivoService service = new IngestaoArquivoService("armazenamento_bancario");

            // Massa de dados inicial em memória
            List<MovimentacaoBancaria> lote = List.of(
                    new MovimentacaoBancaria(1001L, "001-A", "CREDITO", 4500.00, LocalDate.now()),
                    new MovimentacaoBancaria(1002L, "002-B", "DEBITO", 1200.50, LocalDate.now()),
                    new MovimentacaoBancaria(1003L, "001-A", "CREDITO", 3800.00, LocalDate.now()),
                    new MovimentacaoBancaria(1004L, "003-C", "DEBITO", 650.00, LocalDate.now())
            );

            // 1. Exportação física para arquivo .csv
            String nomeArquivoLote = "lote_diario.csv";
            Path arquivoGerado = service.exportarMovimentacoes(lote, nomeArquivoLote);
            System.out.println("Arquivo gravado com sucesso em: " + arquivoGerado.toAbsolutePath());

            // 2. Leitura sob demanda (Streaming) calculando totalização
            double totalCreditos = service.calcularVolumeTotalCreditos(arquivoGerado);
            System.out.printf("Total consolidado de créditos processados: R$ %.2f%n", totalCreditos);

            // 3. Arquivamento seguro (Movimentação física com timestamp)
            Path arquivoArquivado = service.arquivarArquivoProcessado(arquivoGerado);
            System.out.println("Arquivo movido para pasta de backup: " + arquivoArquivado.toAbsolutePath());

            System.out.println("\nProcessamento concluído com estabilidade estrutural.");

        } catch (IOException ex) {
            System.err.println("Falha operacional no sistema de arquivos: " + ex.getMessage());
            ex.printStackTrace();
        }
        System.out.println("============================================================");
    }
}
```

## 4. Diagnóstico de Erros Comuns e Armadilhas

### Armadilha 1: Omissão do try-with-resources ao Utilizar Files.lines()

**Código Problemático:**

```java
// O Stream não é fechado explicitamente!
Stream<String> linhas = Files.lines(Path.of("dados.csv"));
linhas.filter(l -> l.contains("ERRO")).forEach(System.out::println);
```

- **Diagnóstico Técnico:** `Files.lines()` mantém o canal de leitura do sistema operacional aberto. Em sistemas Windows, o arquivo fica travado (*file lock*), impedindo sua exclusão ou renomeação por outros processos. Em ambientes Linux/Unix, executar esse método milhares de vezes em um servidor atinge o limite do sistema operacional de arquivos abertos por processo (*Too many open files*).
- **Correção:** Sempre encadeie o uso em um `try-with-resources`: `try (Stream<String> linhas = Files.lines(caminho)) { ... }`.

### Armadilha 2: Concatenar Caminhos Usando Strings com Barras Manuais

**Código Problemático:**

```java
// Quebra em sistemas operacionais diferentes!
String caminhoInvalido = "C:\\pastaraiz" + "\\" + "subpasta" + "\\" + "arquivo.txt";
```

- **Impacto:** O separador de pastas no Windows é a barra invertida (`\`), enquanto em sistemas Linux e macOS é a barra inclinada (`/`). Caminhos manuais concatenados com `String` falham ao serem migrados para servidores em nuvem baseados em Linux.
- **Correção:** Utilize o método `.resolve()` da interface `Path` ou `Path.of("pastaraiz", "subpasta", "arquivo.txt")`, que detecta e aplica o separador nativo do sistema operacional automaticamente.

### Armadilha 3: Carregar Arquivos Gigantes com Files.readAllLines()

**Código Problemático:**

```java
// Carrega centenas de milhares de linhas simultaneamente no Heap:
List<String> todasAsLinhas = Files.readAllLines(Path.of("log_gigante_10gb.log"));
```

- **Diagnóstico da JVM:** `java.lang.OutOfMemoryError: Java heap space` lançada em tempo de execução.
- **Causa & Correção:** `Files.readAllLines()` só deve ser utilizado para arquivos pequenos de configuração. Para arquivos tabulares ou logs operacionais de grande porte, utilize estritamente `Files.lines()`, que processa uma linha por vez sem reter as anteriores no Heap.

## 5. Roteiro Prático de Depuração: Inspecionando o Path na IDE

Para auditar as propriedades estruturais de caminhos de arquivos no depurador da sua IDE (IntelliJ IDEA, Eclipse ou VS Code):

1. No método `main` de `ArquivosNioApp`, coloque um ponto de interrupção (*breakpoint*) logo após a linha `Path arquivoGerado = service.exportarMovimentacoes(...)`.
2. Inicie a execução em modo de depuração (*Debug*).
3. Na janela de variáveis (*Variables*):
   - Expanda a instância `arquivoGerado`: observe as propriedades do objeto da implementação nativa da JVM (como `WindowsPath` no Windows ou `UnixPath` no Linux).
   - Abra a aba *Evaluate Expression* (Alt + F8) e execute os métodos de consulta de caminho da interface `Path`:
     - `arquivoGerado.getFileName()`: retorna apenas o nome do arquivo (`lote_diario.csv`).
     - `arquivoGerado.getParent()`: retorna o diretório pai onde o arquivo reside.
     - `arquivoGerado.isAbsolute()`: informa se o caminho contém a raiz completa do disco.
4. Avance a execução linha a linha com *Step Over* (F8) e veja a chamada de `Files.move(...)` alterar fisicamente a pasta de localização do arquivo no explorador de arquivos do seu computador em tempo real.

## 6. Exercício de Fixação Prática: Central de Auditoria e Sanitização de Logs

Implemente um componente corporativo de análise e expurgo de logs de segurança aplicando a biblioteca NIO.2:

1. **Construa a Classe `RegistroLogServidor`:**
   - Atributos privados: `ipOrigem` (`String`), `nivelSeveridade` (`String` — `"INFO"`, `"WARN"`, `"FATAL"`), `mensagem` (`String`) e `timestamp` (`String`).
   - Construtor parametrizado completo rejeitando campos em branco via `IllegalArgumentException`.
   - Método de fábrica estático `fromLogLine(String linha)`: faz o parse de linhas com formato textual:
     ```plaintext
     2026-09-04 18:00:00 [FATAL] 192.168.1.50 - Falha de autenticação.
     ```
   - Método descritivo `toSanitizedLine()`: mascara os dois últimos octetos do IP por segurança (ex.: `192.168.*.*`) e devolve a linha padronizada.

2. **Construa o Serviço `AuditoriaLogsNioService`:**
   - **Método `void sanitizarEFiltrarLogsCriticos(Path arquivoOrigem, Path arquivoDestino)`:**
     - Lê o arquivo de log de origem utilizando `Files.lines()` dentro de um `try-with-resources`.
     - Filtra apenas registros que contenham o nível `[WARN]` ou `[FATAL]`.
     - Transforma cada linha sanitizando o endereço de IP via `toSanitizedLine()`.
     - Grava as linhas sanitizadas no arquivo de destino utilizando `Files.write()` com codificação UTF-8 e opção de criação atômica (`StandardOpenOption.CREATE_NEW`).
   - **Método `long contarOcorrenciasSeveridade(Path arquivoLog, String severidade)`:**
     - Utiliza Streams para contar a quantidade de linhas que contêm o marcador informado sem alocar coleções no Heap.

3. **Construa a Classe Executável `LogsNioApp`:**
   - Crie uma pasta temporária `logs_auditoria` via `Files.createDirectories()`.
   - Escreva programaticamente um arquivo de exemplo `servidor.log` contendo 6 linhas (alternando entre `INFO`, `WARN` e `FATAL`).
   - Execute o processo de sanitização gerando o arquivo `auditoria_critica.log`.
   - Comprove no console a contagem correta de incidentes fatais e exiba o caminho absoluto do arquivo final gerado.
