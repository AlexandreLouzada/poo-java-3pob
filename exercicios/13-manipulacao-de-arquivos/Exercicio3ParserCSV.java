import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Locale;

public class Exercicio3ParserCSV {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        Path caminhoCsv = Path.of("produtos.csv");

        try {
            // Criando arquivo CSV fictício para teste
            if (Files.notExists(caminhoCsv)) {
                String conteudoCsv = """
                        Nome,Quantidade,PrecoUnitario
                        Monitor 27,5,1250.00
                        Teclado Mecanico,12,230.50
                        Mouse sem Fio,20,85.00
                        Headset USB,8,310.00
                        """;
                Files.writeString(caminhoCsv, conteudoCsv, StandardOpenOption.CREATE);
            }

            List<String> linhas = Files.readAllLines(caminhoCsv);
            double totalGeral = 0.0;

            System.out.println("--- Processamento de Itens do CSV ---");
            // Pula a linha 0 (cabeçalho)
            for (int i = 1; i < linhas.size(); i++) {
                String linha = linhas.get(i).trim();
                if (linha.isEmpty()) continue;

                String[] campos = linha.split(",");
                String nome = campos[0].trim();
                int quantidade = Integer.parseInt(campos[1].trim());
                double precoUnitario = Double.parseDouble(campos[2].trim());

                double subtotal = quantidade * precoUnitario;
                totalGeral += subtotal;

                System.out.printf("Item: %-18s | Qtd: %2d | Preço: R$ %7.2f | Subtotal: R$ %8.2f%n",
                        nome, quantidade, precoUnitario, subtotal);
            }

            System.out.println("------------------------------------------------------------------");
            System.out.printf("Valor Total do Inventário: R$ %.2f%n", totalGeral);

        } catch (IOException e) {
            System.err.println("Erro de leitura do arquivo: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Erro de formato nos dados numéricos do CSV: " + e.getMessage());
        }
    }
}

