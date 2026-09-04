import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Exercicio4Contas {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        List<ContaBancaria> contas = new ArrayList<>();
        
        contas.add(new ContaCorrente("CC-101", 1000.0));
        contas.add(new ContaEmpresarial("CE-202", 10000.0));

        System.out.println("Processando fechamento do mês...");
        for (ContaBancaria conta : contas) {
            conta.cobrarTaxaMensal();
            conta.consultarSaldo();
        }
    }
}
