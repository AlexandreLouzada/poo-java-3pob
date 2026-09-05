package br.edu.universidade.sistema.frota;

public class FrotaApp {
    public static void main(String[] args) {
        Veiculo c1 = new Veiculo("9BWZZZ377VT004251", "Gol Trendline", 69000.00);
        Veiculo c2 = new Veiculo("8AGGM455WST009812", "Pulse Audace", 96500.00);
        Veiculo c3 = new Veiculo("7HDNM226PPR003417", "HR-V EX", 138000.00);

        System.out.println("--- Auditoria Estatica via Classe (Metaspace) ---");
        System.out.printf("Total de veiculos cadastrados: %d%n", Veiculo.getTotalVeiculosCadastrados());
        System.out.printf("Patrimonio total da frota   : R$ %.2f%n", Veiculo.getPatrimonioTotalFrota());

        System.out.println("\n--- Dados Individuais ---");
        c1.exibirDados();
        c2.exibirDados();
        c3.exibirDados();

        System.out.println("\n--- Desconto Valido (15% no Gol) ---");
        boolean descGol = c1.concederDesconto(0.15);
        System.out.printf("Concedido? %s | Novo valor do Gol: R$ %.2f%n", descGol, c1.getValorComercial());

        System.out.println("\n--- Desconto Abusivo Rejeitado (30% no Pulse) ---");
        boolean descPulse = c2.concederDesconto(0.30);
        System.out.printf("Concedido? %s | Valor do Pulse intacto: R$ %.2f%n", descPulse, c2.getValorComercial());

        System.out.println("\n--- Patrimonio Global Auditavel (integro) ---");
        System.out.printf("Patrimonio total da frota   : R$ %.2f%n", Veiculo.getPatrimonioTotalFrota());
        double esperado = 0.15 * 69000.00;
        System.out.printf("Esperado (somente -15%% do Gol): R$ %.2f%n", 303500.00 - esperado);
    }
}