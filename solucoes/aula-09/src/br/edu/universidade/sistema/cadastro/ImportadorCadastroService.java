package br.edu.universidade.sistema.cadastro;

import java.io.BufferedReader;
import java.io.StringReader;

public class ImportadorCadastroService {

    public void importarLote(String dadosBrutos) {
        int totalLinhas = 0;
        int cadastrosSucesso = 0;

        try (BufferedReader leitor = new BufferedReader(new StringReader(dadosBrutos))) {
            String linha;
            while ((linha = leitor.readLine()) != null) {
                totalLinhas++;
                int numeroLinha = totalLinhas;
                try {
                    String[] campos = linha.split(",");
                    Long id = Long.parseLong(campos[0].trim());
                    String nome = campos[1].trim();
                    int idade = Integer.parseInt(campos[2].trim());
                    double limiteCredito = Double.parseDouble(campos[3].trim());

                    RegistroCliente cliente = new RegistroCliente(id, nome, idade, limiteCredito);
                    cliente.exibirDados();
                    cadastrosSucesso++;
                } catch (IllegalArgumentException ex) {
                    System.out.println("[LINHA " + numeroLinha + " DESCARTADA] Causa: " + ex.getMessage());
                }
            }
        } catch (Exception ex) {
            System.err.println("Falha estrutural na leitura do lote: " + ex.getMessage());
        } finally {
            System.out.printf("%n--- RELATORIO CONSOLIDADO ---%n");
            System.out.printf("Total de linhas lidas: %d%n", totalLinhas);
            System.out.printf("Cadastros processados com sucesso: %d%n", cadastrosSucesso);
            System.out.printf("Linhas descartadas: %d%n", (totalLinhas - cadastrosSucesso));
        }
    }
}