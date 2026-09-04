import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Exercicio1EscritaArquivo {
    public static void main(String[] args) {
        Path diretorio = Path.of("dados", "relatorios");
        Path arquivo = diretorio.resolve("sumario.txt");

        try {
            // Garante a existência de toda a árvore de diretórios
            if (Files.notExists(diretorio)) {
                Files.createDirectories(diretorio);
                System.out.println("Diretório criado: " + diretorio.toAbsolutePath());
            }

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            String conteudo = String.format("""
                    ========================================
                    RELATÓRIO DE EXECUÇÃO DO SISTEMA
                    ========================================
                    Data/Hora: %s
                    Status: Concluído com sucesso
                    Versão: 2.0
                    ========================================
                    """, timestamp);

            // Grava o arquivo criando ou truncando caso já exista
            Files.writeString(arquivo, conteudo, 
                    StandardOpenOption.CREATE, 
                    StandardOpenOption.TRUNCATE_EXISTING);

            System.out.println("Arquivo gravado com sucesso em: " + arquivo.toAbsolutePath());

        } catch (IOException e) {
            System.err.println("Erro ao manipular o arquivo: " + e.getMessage());
        }
    }
}

