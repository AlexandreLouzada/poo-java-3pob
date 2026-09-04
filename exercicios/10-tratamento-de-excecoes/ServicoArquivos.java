import java.io.IOException;

class ServicoArquivos {
    // Simula leitura de baixo nível que lança IOException
    private static void lerArquivoBaixoNivel(String caminho) throws IOException {
        if (caminho == null || caminho.trim().isEmpty()) {
            throw new IOException("O caminho do arquivo não pode ser nulo ou vazio.");
        }
        System.out.println("Arquivo lido com sucesso: " + caminho);
    }

    public static void processarArquivo(String caminho) throws ProcessamentoDadosException {
        try {
            lerArquivoBaixoNivel(caminho);
        } catch (IOException e) {
            // Relança a exceção empacotada na exceção de negócio
            throw new ProcessamentoDadosException("Falha ao processar arquivo de dados do sistema.", e);
        }
    }
}
