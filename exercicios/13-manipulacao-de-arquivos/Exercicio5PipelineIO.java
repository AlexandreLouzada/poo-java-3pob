import java.io.File;
import java.util.Map;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Exercicio5PipelineIO {

    // Transforma "joao silva" -> "Joao Silva"
    private static String padronizarNome(String texto) {
        return Arrays.stream(texto.trim().toLowerCase().split("\\s+"))
                .filter(palavra -> !palavra.isEmpty())
                .map(palavra -> Character.toUpperCase(palavra.charAt(0)) + palavra.substring(1))
                .collect(Collectors.joining(" "));
    }

    public static void main(String[] args) {
        Path entrada = Path.of("clientes_bruto.txt");
        Path saida = Path.of("clientes_padronizado.txt");

        try {
            // Gerando arquivo de teste bruto
            if (Files.notExists(entrada)) {
                String dadosBrutos = """
                          carlos eduardo silva  
                        
                        ana beatriz souza
                            MARCOS PAULO FERREIRA   
                        
                        lucas mendes
                        """;
                Files.writeString(entrada, dadosBrutos, StandardOpenOption.CREATE);
            }

            // Pipeline: Leitura streaming -> Normalização -> Coleta em Lista
            List<String> nomesPadronizados;
            try (Stream<String> streamLinhas = Files.lines(entrada)) {
                nomesPadronizados = streamLinhas
                        .filter(linha -> !linha.isBlank())
                        .map(Exercicio5PipelineIO::padronizarNome)
                        .sorted()
                        .toList();
            }

            // Gravação atômica da lista tratada
            Files.write(saida, nomesPadronizados, 
                    StandardOpenOption.CREATE, 
                    StandardOpenOption.TRUNCATE_EXISTING);

            System.out.println("Processamento concluído com sucesso!");
            System.out.println("Linhas gravadas em " + saida.getFileName() + ":");
            nomesPadronizados.forEach(nome -> System.out.println("  - " + nome));

        } catch (IOException e) {
            System.err.println("Erro no pipeline de processamento: " + e.getMessage());
        }
    }
}

