package br.edu.universidade.sistema.financeiro.dominio;

import br.edu.universidade.sistema.financeiro.exception.ContaBloqueadaException;
import br.edu.universidade.sistema.financeiro.exception.SaldoInsuficienteException;

// 3. Entidade de Domínio aplicando regras de guarda e disparando exceções
public class ContaCorrente {
    private final String numero;
    private double saldo;
    private boolean ativa;

    public ContaCorrente(String numero, double saldoInicial) {
        this.numero = numero;
        this.saldo = Math.max(0.0, saldoInicial);
        this.ativa = true;
    }

    public void bloquearConta() {
        this.ativa = false;
    }

    public void depositar(double valor) {
        if (!this.ativa) {
            throw new ContaBloqueadaException("Não é permitido depósito em conta inativa.", this.numero);
        }
        if (valor <= 0.0) {
            throw new IllegalArgumentException("O valor do depósito deve ser estritamente positivo.");
        }
        this.saldo += valor;
    }

    // Declara explicitamente a exceção checada no contrato do método
    public void sacar(double valor) throws SaldoInsuficienteException {
        if (!this.ativa) {
            throw new ContaBloqueadaException("Não é permitido saque em conta inativa.", this.numero);
        }
        if (valor <= 0.0) {
            throw new IllegalArgumentException("O valor de saque deve ser superior a zero.");
        }
        if (this.saldo < valor) {
            // Disparo com passagem de metadados para auditoria
            throw new SaldoInsuficienteException(
                String.format("Tentativa de débito de R$ %.2f excede o saldo atual de R$ %.2f.", valor, this.saldo),
                this.saldo,
                valor
            );
        }
        this.saldo -= valor;
    }

    public double getSaldo() {
        return saldo;
    }

    public String getNumero() {
        return numero;
    }
}
