package br.edu.universidade.sistema.logistica;

public class LogisticaApp {
    public static void main(String[] args) {
        CalculadoraFrete calc = new CalculadoraFrete();

        System.out.println("--- Teste 1: Sobrecarga com Pacote Único (Padrão Sudeste) ---");
        double frete1 = calc.calcular(10.0);
        System.out.printf("Valor do frete (10 kg): R$ %.2f%n", frete1);

        System.out.println("\n--- Teste 2: Sobrecarga com Região Específica ---");
        double frete2 = calc.calcular(10.0, RegiaoEntrega.NORTE);
        System.out.printf("Valor do frete (10 kg - Norte): R$ %.2f%n", frete2);

        System.out.println("\n--- Teste 3: Sobrecarga com Varargs (Múltiplos Pacotes) ---");
        // Passagem flexível de itens separados por vírgula
        double frete3 = calc.calcular(RegiaoEntrega.NORDESTE, 2.5, 4.0, 1.5, 3.0);
        System.out.printf("Valor do frete lote (11 kg - Nordeste): R$ %.2f%n", frete3);

        System.out.println("\n--- Teste 4: Avaliação de Prazos via Switch Expression ---");
        for (RegiaoEntrega regiao : RegiaoEntrega.values()) {
            System.out.printf("%-15s -> %s%n", regiao.name(), calc.emitirPrevisaoEntrega(regiao));
        }
    }
}
