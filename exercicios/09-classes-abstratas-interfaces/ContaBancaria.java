import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

abstract class ContaBancaria {
    private String numero;
    private double saldo;

    public ContaBancaria(String numero, double saldoInicial) {
        this.numero = numero;
        this.saldo = saldoInicial;
    }

    public String getNumero() {
        return numero;
    }

    public double getSaldo() {
        return saldo;
    }

    public void depositar(double valor) {
        if (valor > 0) this.saldo += valor;
    }

    // Método protegido para permitir que subclasses modifiquem o saldo internamente
    protected void deduzirSaldo(double valor) {
        this.saldo -= valor;
    }

    public void consultarSaldo() {
        System.out.printf("Conta: %s | Saldo Atual: R$ %.2f%n", numero, saldo);
    }

    public abstract void cobrarTaxaMensal();
}
