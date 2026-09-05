package br.edu.universidade.sistema.lote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

public class ProcessadorTransacoesLote {

    public static void main(String[] args) {
        // Simulação de conteúdo de arquivo tabular .csv com dados íntegros e registros corrompidos
        String dadosCsvSimulados = """
                ID;VALOR;PARCELAS
                101;1500.50;3
                102;TEXTO_INVALIDO;2
                103;2400.00;0
                104;980.00;4
                """;

        processarArquivo(dadosCsvSimulados);
    }

    public static void processarArquivo(String conteudo) {
        System.out.println("========== INICIANDO PROCESSAMENTO RESILIENTE ==========");

        // try-with-resources: garante o fechamento determinístico do fluxo AutoCloseable
        try (BufferedReader leitor = new BufferedReader(new StringReader(conteudo))) {
            String linha = leitor.readLine(); // Descarta cabeçalho
            int numeroLinha = 1;

            while ((linha = leitor.readLine()) != null) {
                numeroLinha++;
                if (linha.isBlank()) continue;

                // Bloco defensivo interno: isola a falha por registro sem quebrar o laço
                try {
                    processarLinhaTransacao(linha, numeroLinha);
                } catch (NumberFormatException | ArithmeticException ex) {
                    // Multi-Catch consolidando erros de parsing numérico e regras aritméticas
                    System.err.printf("[ERRO LINHA %d] Registro corrompido descartado: %s | Causa: %s%n",
                            numeroLinha, linha, ex.getMessage());
                }
            }

        } catch (IOException ex) {
            // Tratamento obrigatório para falha física (Checked Exception)
            System.err.println("Falha crítica de I/O no acesso ao arquivo: " + ex.getMessage());
        } finally {
            // Executado compulsoriamente para auditoria de ciclo de vida
            System.out.println("Auditoria: Ciclo de leitura finalizado pelo sistema.");
        }

        System.out.println("================ PROCESSAMENTO CONCLUÍDO ================");
    }

    private static void processarLinhaTransacao(String linha, int linhaOrigem) {
        String[] campos = linha.split(";");

        long id = Long.parseLong(campos[0].trim());
        double valorTotal = Double.parseDouble(campos[1].trim());
        int parcelas = Integer.parseInt(campos[2].trim());

        if (parcelas <= 0) {
            // Disparo de falha aritmética sob condição de negócio inválida
            throw new ArithmeticException("Quantidade de parcelas deve ser estritamente maior que zero.");
        }

        double valorParcela = valorTotal / parcelas;
        System.out.printf("[SUCESSO] Transação #%d aprovada: %d parcelas de R$ %.2f%n",
                id, parcelas, valorParcela);
    }
}
