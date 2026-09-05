package br.edu.universidade.sistema.io.logs;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class LogsNioApp {
    public static void main(String[] args) {
        AuditoriaLogsNioService service = new AuditoriaLogsNioService();

        try {
            Path pasta = Files.createDirectories(Path.of("logs_auditoria"));
            Path arquivoOrigem = pasta.resolve("servidor.log");

            List<String> linhas = List.of(
                    "2026-09-04 17:59:10 [INFO] 192.168.1.10 - Conexao estabelecida.",
                    "2026-09-04 18:00:00 [FATAL] 192.168.1.50 - Falha de autenticacao.",
                    "2026-09-04 18:01:22 [WARN] 192.168.1.51 - Tentativa de acesso restrito.",
                    "2026-09-04 18:02:05 [INFO] 192.168.1.12 - Backup iniciado.",
                    "2026-09-04 18:03:41 [FATAL] 192.168.1.60 - Overflow de memoria detectado.",
                    "2026-09-04 18:04:03 [INFO] 192.168.1.13 - Sessao encerrada."
            );
            Files.write(arquivoOrigem, linhas, StandardCharsets.UTF_8);
            System.out.println("Arquivo de origem criado: " + arquivoOrigem.toAbsolutePath());

            Path arquivoDestino = pasta.resolve("auditoria_critica.log");
            service.sanitizarEFiltrarLogsCriticos(arquivoOrigem, arquivoDestino);
            System.out.println("\nArquivo sanitizado gerado: " + arquivoDestino.toAbsolutePath());

            System.out.println("\n--- Conteudo do Arquivo de Auditoria (WARN/FATAL, IPs mascarados) ---");
            Files.lines(arquivoDestino, StandardCharsets.UTF_8).forEach(System.out::println);

            long fatais = service.contarOcorrenciasSeveridade(arquivoDestino, "FATAL");
            long warnings = service.contarOcorrenciasSeveridade(arquivoDestino, "WARN");
            System.out.printf("%nContagem de severidade no arquivo final -> FATAL: %d | WARN: %d%n", fatais, warnings);
        } catch (IOException ex) {
            System.err.println("Falha de I/O no processamento NIO.2: " + ex.getMessage());
        }
    }
}