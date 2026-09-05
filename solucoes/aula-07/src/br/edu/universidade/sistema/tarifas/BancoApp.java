package br.edu.universidade.sistema.tarifas;

public class BancoApp {
    public static void main(String[] args) {
        ContaCorrente cc = new ContaCorrente("0101-CC", 1000.00);
        ContaPoupanca cp = new ContaPoupanca("0202-CP", 2000.00);

        System.out.println("--- Vetor Heterogeneo: Saques Polimorficos ---");
        Conta[] contas = { cc, cp };
        double[] valoresSaque = { 100.00, 300.00 };
        for (int i = 0; i < contas.length; i++) {
            boolean sucesso = contas[i].sacar(valoresSaque[i]);
            System.out.printf("Conta %s | Saque de R$ %.2f autorizado? %s -> ",
                    contas[i].numeroConta, valoresSaque[i], sucesso);
            contas[i].exibirDados();
        }

        System.out.println("\n--- Tabela de Impostos (Tributavel[]) ---");
        Tributavel[] tributaveis = { cc };
        double somaTributos = 0.0;
        for (Tributavel t : tributaveis) {
            double tributo = t.calcularTributo();
            somaTributos += tributo;
            System.out.printf("Tributo recolhido: R$ %.2f%n", tributo);
        }
        System.out.printf("Somatório total de tributos recolhidos: R$ %.2f%n", somaTributos);

        System.out.println("\n--- Comprovacao: poupanca nao e tributavel ---");
        System.out.println("ContaPoupanca implements Tributavel? "
                + (cp instanceof Tributavel ? "SIM (incorreto)" : "NAO (correto, sem tributo)"));
    }
}