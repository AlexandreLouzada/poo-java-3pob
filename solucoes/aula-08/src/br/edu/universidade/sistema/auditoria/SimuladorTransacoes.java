package br.edu.universidade.sistema.auditoria;

public class SimuladorTransacoes {

    public static void efetuarDivisaoLucros(int totalLucro, int totalSocios) {
        int quociente = totalLucro / totalSocios;
        System.out.printf("Divisao de lucros: R$ %d para %d sócios -> R$ %d por sócio.%n",
                totalLucro, totalSocios, quociente);
    }

    public static void converterChaveAcesso(String chave) {
        int codigoNumerico = Integer.parseInt(chave);
        System.out.println("Chave de acesso convertida para inteiro: " + codigoNumerico);
    }

    public static void validarAssinaturaDigital(String assinatura) {
        String normalizada = assinatura.toUpperCase();
        System.out.println("Assinatura digital normalizada: " + normalizada);
    }
}