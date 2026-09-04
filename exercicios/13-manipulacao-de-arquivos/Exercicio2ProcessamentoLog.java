import java.io.File;
import java.util.Map;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.stream.Stream;

public class Exercicio2ProcessamentoLog {
    public static void main(String[] args) {
        Path caminhoLog = Path.of("servidor.log");

        try {
            // Criando arquivo de teste caso não exista
            if (Files.notExists(caminhoLog)) {
                String logInicial = """
                        [INFO] Servidor iniciado na porta 8080
                        [ERROR] Falha ao conectar ao banco de dados principal
                        [INFO] Tentativa de reconexão agendada
                        [ERROR] Timeout de resposta da API de pagamentos
                        [WARN] Uso de memória acima de 80%
                        [ERROR] Falha de autenticação do usuário admin
                        """;
                Files.writeString(caminhoLog, logInicial, StandardOpenOption.CREATE);
            }

            // O try-with-resources é obrigatório para fechar o stream e liberar o descritor de arquivo
            try (Stream<String> linhas = Files.lines(caminhoLog)) {
                List<String> erros = linhas
                        .filter(linha -> linha.startsWith("[ERROR]"))
                        .map(linha -> linha.replace("[ERROR]", "").trim())
                        .toList();

                System.out.println("Total de falhas registradas: " + erros.size());
                System.out.println("Mensagens de erro extraídas:");
                erros.forEach(erro -> System.out.println("  - " + erro));
            }

        } catch (IOException e) {
            System.err.println("Erro ao ler arquivo de log: " + e.getMessage());
        }
    }
}

