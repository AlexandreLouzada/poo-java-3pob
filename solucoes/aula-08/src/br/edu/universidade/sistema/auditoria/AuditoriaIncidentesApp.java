package br.edu.universidade.sistema.auditoria;

public class AuditoriaIncidentesApp {
    public static void main(String[] args) {
        System.out.println("========== AUDITORIA DE INCIDENTES EM EXECUCAO ==========");

        System.out.println("\n--- Incidente 1: Divisao por Zero (ArithmeticException) ---");
        try {
            SimuladorTransacoes.efetuarDivisaoLucros(100000, 0);
        } catch (ArithmeticException ex) {
            ServicoAuditoriaForense.emitirLaudoTecnico(ex);
        }

        System.out.println("\n--- Incidente 2: Chave Alfanumerica (NumberFormatException) ---");
        try {
            SimuladorTransacoes.converterChaveAcesso("ABCD-1234");
        } catch (NumberFormatException ex) {
            ServicoAuditoriaForense.emitirLaudoTecnico(ex);
        }

        System.out.println("\n--- Incidente 3: Assinatura Nula (NullPointerException) ---");
        try {
            SimuladorTransacoes.validarAssinaturaDigital(null);
        } catch (NullPointerException ex) {
            ServicoAuditoriaForense.emitirLaudoTecnico(ex);
        }

        System.out.println("\nA aplicacao concluiu os tres testes sequenciais sem abortar a JVM.");
    }
}