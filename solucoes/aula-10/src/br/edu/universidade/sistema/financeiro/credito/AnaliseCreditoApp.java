package br.edu.universidade.sistema.financeiro.credito;

import br.edu.universidade.sistema.financeiro.credito.dominio.PropostaFinanciamento;
import br.edu.universidade.sistema.financeiro.credito.exception.LimiteCreditoExcedidoException;
import br.edu.universidade.sistema.financeiro.credito.exception.ScoreSerasaInvalidoException;

public class AnaliseCreditoApp {
    public static void main(String[] args) {
        System.out.println("========== SISTEMA DE ANALISE DE CREDITO ==========");

        System.out.println("\n--- Caso 1: Comprometimento de Renda Excessivo ---");
        // Parcela pretendida de R$ 2.000,00 para renda de R$ 4.000,00
        PropostaFinanciamento proposta1 = new PropostaFinanciamento("111.222.333-01", 4000.00, 100000.00, 55);
        try {
            proposta1.validarAprovacao(750);
            System.out.println("Proposta 1 aprovada (inesperado).");
        } catch (LimiteCreditoExcedidoException ex) {
            System.out.println("[LAUDO DE AUDITORIA] " + ex.getMessage());
            System.out.printf("Renda mensal........: R$ %.2f%n", ex.getRendaMensal());
            System.out.printf("Parcela pretendida..: R$ %.2f%n", ex.getValorParcelaPretendida());
            System.out.printf("Comprometimento.....: %.1f%% da renda (teto: 30%%)%n",
                    (ex.getPercentualComprometimento() * 100));
        } catch (ScoreSerasaInvalidoException ex) {
            System.out.println("[NEGADO] " + ex.getMessage() + " (score: " + ex.getPontuacaoInformada() + ")");
        }

        System.out.println("\n--- Caso 2: Score Serasa Negativo (-50) ---");
        PropostaFinanciamento proposta2 = new PropostaFinanciamento("444.555.666-02", 8000.00, 30000.00, 24);
        try {
            proposta2.validarAprovacao(-50);
            System.out.println("Proposta 2 aprovada (inesperado).");
        } catch (LimiteCreditoExcedidoException ex) {
            System.out.println("[NEGADO] " + ex.getMessage());
        } catch (ScoreSerasaInvalidoException ex) {
            System.out.println("[RESILIENTE] " + ex.getMessage() + " | Score informado: "
                    + ex.getPontuacaoInformada());
        }

        System.out.println("\nO sistema permaneceu em execucao contínua sem quebras de processo.");
    }
}