import java.util.Locale;

class ContaCorrente extends Conta {
    private double limiteChequeEspecial;
    private static final double TAXA_SAQUE = 2.0;

    public ContaCorrente(String numero, double saldoInicial, double limiteChequeEspecial) {
        super(numero, saldoInicial);
        this.limiteChequeEspecial = limiteChequeEspecial;
    }

    @Override
    public boolean sacar(double valor) {
        double valorTotalDebito = valor + TAXA_SAQUE;
        if (valor > 0 && (getSaldo() + limiteChequeEspecial) >= valorTotalDebito) {
            // Reutiliza a lógica de alteração direta de estado depositando o inverso ou deduzindo do saldo base
            super.depositar(-valorTotalDebito);
            System.out.printf("Saque de R$ %.2f (Taxa: R$ %.2f) efetuado na Conta Corrente %s.%n",
                    valor, TAXA_SAQUE, getNumero());
            return true;
        } else {
            System.out.printf("Saque de R$ %.2f recusado na Conta Corrente %s: Limite insuficiente.%n",
                    valor, getNumero());
            return false;
        }
    }
}
