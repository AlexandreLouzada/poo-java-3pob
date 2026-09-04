import java.util.Locale;

// Exceção Verificada (Checked Exception)
class SaldoInsuficienteException extends Exception {
    private final double saldoAtual;
    private final double valorTentativa;

    public SaldoInsuficienteException(String mensagem, double saldoAtual, double valorTentativa) {
        super(mensagem);
        this.saldoAtual = saldoAtual;
        this.valorTentativa = valorTentativa;
    }

    public double getSaldoAtual() {
        return saldoAtual;
    }

    public double getValorTentativa() {
        return valorTentativa;
    }
}
