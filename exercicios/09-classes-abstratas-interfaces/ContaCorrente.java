import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class ContaCorrente extends ContaBancaria {
    public ContaCorrente(String numero, double saldoInicial) {
        super(numero, saldoInicial);
    }

    @Override
    public void cobrarTaxaMensal() {
        double taxa = 15.00;
        deduzirSaldo(taxa);
        System.out.printf("Taxa Fixa (R$ %.2f) cobrada na Conta Corrente %s.%n", taxa, getNumero());
    }
}
