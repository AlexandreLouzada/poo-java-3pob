import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class Exercicio4VarreduraDiretorios {
    public static void main(String[] args) {
        // Escaneia a partir do diretório de execução atual
        Path diretorioRaiz = Path.of(".");

        try (Stream<Path> streamCaminhos = Files.walk(diretorioRaiz)) {
            System.out.println("--- Arquivos Java encontrados na árvore de diretórios ---");

            var arquivosJava = streamCaminhos
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".java"))
                    .toList();

            long tamanhoTotalBytes = 0;

            for (Path arquivo : arquivosJava) {
                long tamanho = Files.size(arquivo);
                tamanhoTotalBytes += tamanho;
                System.out.printf("Arquivo: %-40s | Tamanho: %6d bytes%n", 
                        diretorioRaiz.relativize(arquivo), tamanho);
            }

            System.out.println("---------------------------------------------------------");
            System.out.printf("Total de arquivos: %d | Tamanho total: %d bytes (%.2f KB)%n",
                    arquivosJava.size(), tamanhoTotalBytes, tamanhoTotalBytes / 1024.0);

        } catch (IOException e) {
            System.err.println("Falha ao percorrer os diretórios: " + e.getMessage());
        }
    }
}

