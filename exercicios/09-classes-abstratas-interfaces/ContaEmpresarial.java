import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class ContaEmpresarial extends ContaBancaria {
    public ContaEmpresarial(String numero, double saldoInicial) {
        super(numero, saldoInicial);
    }

    @Override
    public void cobrarTaxaMensal() {
        double taxa = 30.00 + (getSaldo() * 0.005); // 30 fixo + 0.5% do saldo
        deduzirSaldo(taxa);
        System.out.printf("Taxa Variável (R$ %.2f) cobrada na Conta Empresarial %s.%n", taxa, getNumero());
    }
}
