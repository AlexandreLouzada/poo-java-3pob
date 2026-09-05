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
