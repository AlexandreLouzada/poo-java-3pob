# Explicação da Aula 20 — Entrada e Saída Moderna de Arquivos (Java I/O e NIO.2)

| | |
|---|---|
| **Disciplina** | Programação Orientada a Objetos |
| **Curso** | Análise e Desenvolvimento de Sistemas (3º Período) |
| **Tema** | Evolução do I/O clássico para o NIO.2 (`java.nio.file`), Abstrações `Path` e `Paths`, Operações Utilitárias com a Classe `Files`, Leitura e Escrita Atômica, Processamento de Arquivos com Streams e Tratamento de Exceções de Sistema de Arquivos |
| **Carga Horária** | 100 minutos (2 horas-aula) |
| **Professor** | Alexandre Neves Louzada |
| **Tutorial** | `aulas/TutorialAula20.md` |
| **Estudo de Caso** | `exemplos/aula-20/` |
| **Exercícios Resolvidos** | `solucoes/aula-20/` |

## 1. Objetivos de Aprendizagem

- **Conceitual:** Compreender as limitações estruturais da biblioteca clássica de entrada e saída (`java.io.File`) e as melhorias introduzidas pela especificação JSR 203 (NIO.2 no Java 7); assimilar a separação entre a localização de um recurso no sistema de arquivos (`Path`) e as operações de manipulação física (`Files`).
- **Técnico:** Manipular caminhos absolutos e relativos através da interface `java.nio.file.Path`; executar operações utilitárias de alta performance na classe `java.nio.file.Files` (criação de diretórios, verificação de existência, cópia, movimentação, exclusão atômica e leitura/gravação de arquivos); integrar a leitura sob demanda de arquivos gigantes com a Streams API via `Files.lines()`.
- **Arquitetural:** Prevenir vazamento de descritores de arquivos (*file descriptor leaks*) e travamento de concorrência no sistema operacional combinando `try-with-resources` com fluxos de I/O; tratar adequadamente a hierarquia de falhas físicas checadas derivadas de `java.io.IOException` (como `NoSuchFileException` e `AccessDeniedException`).
- **Prático:** Implementar um serviço de ingestão e arquivamento corporativo de transações financeiras em formato tabular (.csv), gravando logs em disco, arquivando cópias de segurança com timestamp e processando grandes volumes de dados de forma preguiçosa (*lazy*).

## 2. Conteúdo Teórico Detalhado

### 2.1 Limitações do Java I/O Clássico (java.io.File)

A classe `java.io.File`, introduzida no Java 1.0, supriu as necessidades iniciais da linguagem, mas acumulou restrições severas para ambientes corporativos modernos:

- **Tratamento Frágil de Erros:** Métodos críticos como `file.delete()` ou `file.mkdir()` retornam apenas um valor booleano (`true` ou `false`) em caso de falha. Se a operação falhar, o desenvolvedor não tem como saber programaticamente se o motivo foi falta de permissão, arquivo inexistente ou bloqueio por outro processo do sistema operacional.
- **Falta de Suporte a Recursos Avançados:** Incapacidade de lidar com atributos específicos de sistemas operacionais modernos, como links simbólicos (*symlinks*), permissões POSIX detalhadas e listas de controle de acesso (ACLs).
- **Desempenho Ruim em Escala:** A listagem de arquivos em diretórios com milhares de itens carregava todos os nomes de uma só vez na memória em um array de `String`, gerando picos de consumo de memória no Heap.

### 2.2 A Revolução do Java NIO.2 (java.nio.file)

Com a chegada do Java 7, o pacote `java.nio.file` reformulou completamente a arquitetura de I/O em torno de dois pilares:

| Interface `java.nio.file.Path` | Classe Utilitária `java.nio.file.Files` |
|---|---|
| Representa a localização abstrata | Realiza o trabalho operacional |
| Substitui `java.io.File` | Cria, copia, move e deleta |
| Manipula nomes, pastas e caminhos | Dispara exceções ricas em erros |
| Não acessa o disco diretamente | Integra diretamente com Streams API |

A interface `Path` é um modelo imutável que representa uma localização hierárquica no sistema de arquivos (como `C:\dados\relatorio.csv` ou `/var/log/app.log`). A obtenção de instâncias é feita de forma fluida via método de fábrica estático `Path.of(...)` (Java 11+) ou `Paths.get(...)` (Java 7+):

```java
// Obtenção de caminho multiplataforma (independente de barra invertida ou normal)
Path caminho = Path.of("dados", "financeiro", "extrato.csv");
```

A classe `Files` concentra métodos estáticos utilitários que recebem instâncias de `Path` como parâmetro para interagir com o sistema de arquivos real.

### 2.3 Modos de Abertura e Opções Padrão (StandardOpenOption)

Ao gravar ou anexar conteúdo em arquivos, o enum `StandardOpenOption` permite parametrizar com precisão o comportamento desejado:

| Opção | Comportamento |
|---|---|
| `CREATE` | Cria o arquivo caso ele ainda não exista |
| `CREATE_NEW` | Cria um novo arquivo, disparando `FileAlreadyExistsException` se ele já existir (garantia de criação atômica) |
| `TRUNCATE_EXISTING` | Se o arquivo já existir, trunca seu tamanho para 0 bytes antes de gravar |
| `APPEND` | Abre o arquivo preservando os dados anteriores e anexa novos bytes ao final |

### 2.4 Processamento Sob Demanda (Lazy) de Grandes Arquivos com Files.lines()

Ao lidar com arquivos que contêm centenas de megabytes ou múltiplos gigabytes, ler todo o conteúdo de uma vez para a memória através de `Files.readAllLines()` é um erro grave de arquitetura que resulta em `OutOfMemoryError`:

- `Files.readAllLines()`: carrega TUDO -> `List<String>` no Heap (esgotamento de memória).
- `Files.lines()`: demanda sob medida -> `Stream<String>` via Iterator (consumo estável).

O método utilitário `Files.lines(Path)` lê o arquivo linha por linha sob demanda utilizando a Streams API. O consumo de memória permanece estável em poucos kilobytes, independentemente de o arquivo ter 10 linhas ou 10 milhões de linhas.

**Regra Crítica de Engenharia:** Como `Files.lines()` mantém um descritor de arquivo aberto junto ao sistema operacional, a chamada DEVE ser encapsulada obrigatoriamente dentro de um bloco `try-with-resources` para assegurar o fechamento do canal ao final do processamento.

### 2.5 Diagnóstico de Erros Comuns e Armadilhas

**Armadilha 1: Omissão do try-with-resources ao Utilizar Files.lines()**

```java
// O Stream não é fechado explicitamente!
Stream<String> linhas = Files.lines(Path.of("dados.csv"));
linhas.filter(l -> l.contains("ERRO")).forEach(System.out::println);
```

Diagnóstico: `Files.lines()` mantém o canal de leitura aberto. No Windows, o arquivo fica travado (*file lock*), impedindo exclusão ou renomeação. No Linux/Unix, reprisar milhares de vezes atinge o limite de arquivos abertos (*Too many open files*). Correção: sempre usar `try (Stream<String> linhas = Files.lines(caminho)) { ... }`.

**Armadilha 2: Concatenar Caminhos Usando Strings com Barras Manuais**

```java
// Quebra em sistemas operacionais diferentes!
String caminhoInvalido = "C:\\pastaraiz" + "\\" + "subpasta" + "\\" + "arquivo.txt";
```

Impacto: o separador no Windows é a barra invertida (`\`), enquanto em Linux/macOS é a barra normal (`/`). Correção: usar `.resolve()` da interface `Path` ou `Path.of("pastaraiz", "subpasta", "arquivo.txt")`, que detecta o separador nativo automaticamente.

**Armadilha 3: Carregar Arquivos Gigantes com Files.readAllLines()**

```java
// Carrega centenas de milhares de linhas simultaneamente no Heap:
List<String> todasAsLinhas = Files.readAllLines(Path.of("log_gigante_10gb.log"));
```

Diagnóstico: `java.lang.OutOfMemoryError: Java heap space`. Correção: `readAllLines()` só para arquivos pequenos de configuração; para logs grandes usar estritamente `Files.lines()`.

## 3. Estudo de Caso Aplicado

O projeto corporativo implementa um serviço de ingestão de arquivos tabulares (.csv), gerando diretórios dinâmicos, escrevendo relatórios com codificação UTF-8, processando registros sob demanda e arquivando cópias de auditoria.

**Estrutura de pacotes (exemplos/aula-20/src):**

```
br.edu.universidade.sistema.io
  |-- dominio/
  |     |-- MovimentacaoBancaria.java
  |-- service/
  |     |-- IngestaoArquivoService.java
  |-- ArquivosNioApp.java
```

A entidade `MovimentacaoBancaria` modela um registro de movimentação com id, conta de origem, tipo (DEBITO/CREDITO), valor e data. O construtor valida id não nulo e valor positivo. A classe fornece:

- `toCsv()`: converte a entidade para formato CSV delimitado por ponto e vírgula.
- `fromCsv(String linha)`: fábrica estática que faz o parse de uma linha CSV de volta para a entidade (incluindo tratamento da vírgula decimal de valores).

O serviço `IngestaoArquivoService` é o núcleo da solução. No construtor, cria a árvore de diretórios `diretorioBase` e `diretorioBase/backup` com `Files.createDirectories()` (que cria toda a hierarquia de pastas de uma vez). O construtor declara `throws IOException` por causa das operações físicas de criação.

O método `exportarMovimentacoes` prepara as linhas com cabeçalho `ID;CONTA;TIPO;VALOR;DATA` usando `Stream.concat` combinando o cabeçalho com o mapeamento das movimentações via `MovimentacaoBancaria::toCsv`. Em seguida grava com escrita atômica:

```java
List<String> linhas = Stream.concat(
        Stream.of("ID;CONTA;TIPO;VALOR;DATA"),
        registros.stream().map(MovimentacaoBancaria::toCsv)
).toList();

Files.write(arquivoDestino, linhas, StandardCharsets.UTF_8,
        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
```

O método `calcularVolumeTotalCreditos` demonstra o processamento lazy:

```java
try (Stream<String> fluxoLinhas = Files.lines(arquivoCsv, StandardCharsets.UTF_8)) {
    return fluxoLinhas
            .skip(1) // Pula o cabeçalho
            .filter(linha -> !linha.isBlank())
            .map(MovimentacaoBancaria::fromCsv)
            .filter(mov -> mov.getTipo().equalsIgnoreCase("CREDITO"))
            .mapToDouble(MovimentacaoBancaria::getValor)
            .sum();
}
```

O método `arquivarArquivoProcessado` move o arquivo com timestamp no nome:

```java
String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
String novoNome = "processado_" + timestamp + "_" + arquivoOriginal.getFileName().toString();
Path destinoFinal = this.diretorioBackup.resolve(novoNome);
return Files.move(arquivoOriginal, destinoFinal, StandardCopyOption.REPLACE_EXISTING);
```

O `ArquivosNioApp` executa o ciclo completo: inicializa o serviço, cria um lote de 4 movimentações em memória, exporta para `lote_diario.csv`, calcula o total de créditos (R$ 8.300,00) e move o arquivo para o backup. O tratamento de `IOException` é feito no catch do `main`.

A saída esperada da aplicação demonstra cada etapa do fluxo:

```
========== SISTEMA DE INGESTÃO DE ARQUIVOS NIO.2 ==========
Arquivo gravado com sucesso em: .../armazenamento_bancario/lote_diario.csv
Total consolidado de créditos processados: R$ 8300,00
Arquivo movido para pasta de backup: .../armazenamento_bancario/backup/processado_20260904_153000_lote_diario.csv
Processamento concluído com estabilidade estrutural.
============================================================
```

Observe que a soma dos créditos considera apenas as duas movimentações do tipo CREDITO (4.500,00 + 3.800,00), pois os registros DEBITO são filtrados no pipeline de Streams antes da somatória.

### 3.1 Roteiro Prático de Depuração na IDE

Para auditar as propriedades estruturais de caminhos na IDE (IntelliJ IDEA, Eclipse ou VS Code):

1. Coloque um breakpoint após `Path arquivoGerado = service.exportarMovimentacoes(...)`.
2. Inicie em modo Debug e expanda `arquivoGerado`: observe a implementação nativa (`WindowsPath` no Windows ou `UnixPath` no Linux).
3. Na aba Evaluate Expression (Alt + F8), execute `arquivoGerado.getFileName()`, `arquivoGerado.getParent()` e `arquivoGerado.isAbsolute()`.
4. Avance com Step Over (F8) e observe a chamada `Files.move(...)` alterar fisicamente a pasta do arquivo.

## 4. Exercícios Propostos e Solução

O exercício propõe a construção de um componente corporativo de análise e expurgo de logs de segurança aplicando a biblioteca NIO.2.

**Solucao completa (solucoes/aula-20/src):**

```
br.edu.universidade.sistema.io.logs
  |-- RegistroLogServidor.java
  |-- AuditoriaLogsNioService.java
  |-- LogsNioApp.java
```

**Etapa 1 - Classe `RegistroLogServidor`:** Atributos privados `ipOrigem`, `nivelSeveridade`, `mensagem` e `timestamp`. O construtor rejeita campos em branco via `IllegalArgumentException`. O método estático `fromLogLine(String linha)` faz o parse de linhas no formato textual:

```
2026-09-04 18:00:00 [FATAL] 192.168.1.50 - Falha de autenticação.
```

O método `toSanitizedLine()` mascara os dois últimos octetos do IP por segurança (ex.: `192.168.*.*`):

```java
private String mascararIp(String ip) {
    String[] octetos = ip.split("\\.");
    if (octetos.length < 2) {
        return "*.*";
    }
    for (int i = 0; i < octetos.length; i++) {
        octetos[i] = (i == octetos.length - 1 || i == octetos.length - 2) ? "*" : octetos[i];
    }
    return String.join(".", octetos);
}
```

**Etapa 2 - Classe `AuditoriaLogsNioService`:** O método `sanitizarEFiltrarLogsCriticos(Path origem, Path destino)` lê o log de origem com `Files.lines()` dentro de `try-with-resources`, filtra apenas `[WARN]` ou `[FATAL]`, sanitiza cada linha via `toSanitizedLine()` e grava no destino com `Files.write()` usando `StandardOpenOption.CREATE_NEW` (criação atômica). O método `contarOcorrenciasSeveridade(Path arquivoLog, String severidade)` usa Streams para contar ocorrências do marcador sem alocar coleções no Heap.

**Etapa 3 - Classe Executável `LogsNioApp`:** Cria a pasta `logs_auditoria` via `Files.createDirectories()`, escreve programaticamente o arquivo `servidor.log` com 6 linhas (alternando INFO, WARN e FATAL), executa a sanitização gerando `auditoria_critica.log` e exibe a contagem de severidades.

Saída esperada:

```
Arquivo sanitizado gerado: .../logs_auditoria/auditoria_critica.log

--- Conteudo do Arquivo de Auditoria (WARN/FATAL, IPs mascarados) ---
2026-09-04 18:00:00 [FATAL] 192.168.*.* - Falha de autenticacao.
2026-09-04 18:01:22 [WARN] 192.168.*.* - Tentativa de acesso restrito.
2026-09-04 18:03:41 [FATAL] 192.168.*.* - Overflow de memoria detectado.

Contagem de severidade no arquivo final -> FATAL: 2 | WARN: 1
```

## 5. Perguntas de Revisão

1. Quais são as principais limitações da classe `java.io.File` que motivaram a criação do NIO.2?
2. Qual a diferença entre `Path.of(...)` e `Files.write(...)`? Por que foram separados em classes distintas?
3. Por que `Files.lines()` exige o uso de `try-with-resources`? O que acontece se ele for omitido?
4. Qual a diferença entre `Files.readAllLines()` e `Files.lines()`? Em que situações cada um deve ser usado?
5. O que é o enum `StandardOpenOption` e quais opções garantem escrita atômica no estudo de caso?
6. Por que é problemático concatenar caminhos de arquivos usando strings com barras manuais?
7. Como o método `fromLogLine` da classe `RegistroLogServidor` extrai os campos de uma linha de log estruturada?
8. Quais métodos da interface `Path` são úteis para inspecionar um caminho no depurador?

## 6. Resumo / Pontos-Chave

- **NIO.2** (Java 7+) revolucionou o I/O do Java com as abstrações `Path` (localização) e `Files` (operações), substituindo a classe `java.io.File` ultrapassada.
- **`Path`** é imutável e fornece métodos como `resolve()`, `getFileName()`, `getParent()` e `isAbsolute()` para manipulação de caminhos multiplataforma.
- **`Files`** concentra métodos estáticos para criação, cópia, movimentação e exclusão de arquivos com tratamento de erros rico via exceções (ex.: `NoSuchFileException`, `AccessDeniedException`).
- **`Files.write()`** com `StandardOpenOption` permite escrita atômica e controlada (`CREATE`, `CREATE_NEW`, `TRUNCATE_EXISTING`, `APPEND`).
- **`Files.lines()`** é o método correto para processar arquivos grandes de forma sob demanda (lazy), mantendo o consumo de memória estável.
- **`try-with-resources`** é mandatório ao usar `Files.lines()` para garantir o fechamento do descritor de arquivo do SO e evitar *file locks*.
- O estudo de caso demonstra um serviço corporativo completo de ingestão CSV com exportação, processamento stream e arquivamento com timestamp.
- O exercício consolida os conceitos com um sistema de auditoria de logs que sanitiza IPs e filtra registros críticos usando NIO.2 e Streams.