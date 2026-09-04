import java.io.IOException;

public class Exercicio5ExceptionChaining {
    public static void main(String[] args) {
        try {
            ServicoArquivos.processarArquivo(""); // Força falha de baixo nível
        } catch (ProcessamentoDadosException e) {
            System.out.println("Erro na camada de serviço: " + e.getMessage());
            if (e.getCause() != null) {
                System.out.println("Causa raiz identificada: " + e.getCause().getMessage());
            }
        }
    }
}
