package br.edu.universidade.sistema.io.logs;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Collectors;

public class AuditoriaLogsNioService {

    public void sanitizarEFiltrarLogsCriticos(Path arquivoOrigem, Path arquivoDestino) throws IOException {
        try (var linhas = Files.lines(arquivoOrigem, StandardCharsets.UTF_8)) {
            List<String> sanitizadas = linhas
                    .filter(l -> l.contains("[WARN]") || l.contains("[FATAL]"))
                    .map(RegistroLogServidor::fromLogLine)
                    .map(RegistroLogServidor::toSanitizedLine)
                    .collect(Collectors.toList());

            Files.write(arquivoDestino, sanitizadas, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
        }
    }

    public long contarOcorrenciasSeveridade(Path arquivoLog, String severidade) throws IOException {
        try (var linhas = Files.lines(arquivoLog, StandardCharsets.UTF_8)) {
            return linhas
                    .filter(l -> l.contains("[" + severidade + "]"))
                    .count();
        }
    }
}