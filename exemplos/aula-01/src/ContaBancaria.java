// Entidade autônoma e responsável pela sua própria consistência
class ContaBancaria {
    // Atributo privado residente no Heap: inacessível diretamente por código externo
    private double saldo;

    public ContaBancaria(double saldoInicial) {
        if (saldoInicial >= 0) {
            this.saldo = saldoInicial;
        } else {
            this.saldo = 0.0;
        }
    }

    public boolean sacar(double valor) {
        // A própria classe rejeita a operação e protege sua invariante
        if (valor > 0 && this.saldo >= valor) {
            this.saldo -= valor;
            return true;
        }
        return false;
    }

    public void depositar(double valor) {
        if (valor > 0) {
            this.saldo += valor;
        }
    }

    public double getSaldo() {
        return this.saldo;
    }
}
