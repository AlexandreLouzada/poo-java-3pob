package br.edu.universidade.concessionaria;

public class ConcessionariaApp {
    public static void main(String[] args) {
        Veiculo v1 = new Veiculo("9BWZZZ377VT004251", "Volkswagen", "Gol Trendline", 2022, 69000.00);
        Veiculo v2 = new Veiculo("8AGGM455WST009812", "Fiat", "Pulse Audace", 2023, 96500.00);
        Veiculo v3 = new Veiculo("7HDNM226PPR003417", "Honda", "HR-V EX");

        System.out.println("--- Fichas Tecnicas Iniciais ---");
        v1.exibirFichaTecnica();
        v2.exibirFichaTecnica();
        v3.exibirFichaTecnica();

        System.out.println("\n--- Aplicacao de Desconto Valido (10% no Gol; 15% no Pulse) ---");
        v1.aplicarDesconto(0.10);
        v2.aplicarDesconto(0.15);
        System.out.printf("Novo preco do Gol  : R$ %.2f%n", v1.getPrecoBase());
        System.out.printf("Novo preco do Pulse: R$ %.2f%n", v2.getPrecoBase());

        System.out.println("\n--- Tentativa de Desconto Abusivo (40% no Pulse) ---");
        v2.aplicarDesconto(0.40);
        System.out.printf("Preco do Pulse preservado pela regra de guarda: R$ %.2f%n", v2.getPrecoBase());

        System.out.println("\n--- Desconto Invalido (percentual negativo/nulo) ---");
        v1.aplicarDesconto(-0.05);
        v1.aplicarDesconto(0.0);
        System.out.printf("Preco do Gol intacto apos descontos invalidos: R$ %.2f%n", v1.getPrecoBase());
    }
}