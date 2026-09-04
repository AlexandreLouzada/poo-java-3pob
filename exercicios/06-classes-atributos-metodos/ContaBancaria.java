import java.util.Locale;

class ContaBancaria {
    String titular;
    String numeroConta;
    double saldo = 0.0;

    void depositar(double valor) {
        if (valor > 0) {
            saldo += valor;
            System.out.printf("Depósito de R$ %.2f realizado com sucesso.%n", valor);
        } else {
            System.out.println("Valor de depósito inválido.");
        }
    }

    void sacar(double valor) {
        if (valor > 0 && saldo >= valor) {
            saldo -= valor;
            System.out.printf("Saque de R$ %.2f realizado com sucesso.%n", valor);
        } else {
            System.out.printf("Falha no saque de R$ %.2f: Saldo insuficiente ou valor inválido.%n", valor);
        }
    }

    void consultarSaldo() {
        System.out.printf("Titular: %s | Conta: %s | Saldo Atual: R$ %.2f%n", titular, numeroConta, saldo);
    }
}
