package br.edu.universidade.sistema.banco;

public class ContaBancaria {
    // 1. Membros Estáticos (Compartilhados por todas as contas no Metaspace)
    private static int totalContas = 0;
    private static double volumeTotalCustodiado = 0.0;
    public static final double TAXA_MANUTENCAO_PADRAO = 15.00;

    // 2. Membros de Instância Encapsulados (Isolados no Heap)
    private final String numeroConta;
    private String titular;
    private double saldo;

    // 3. Construtor com Auditoria Global
    public ContaBancaria(String numeroConta, String titular, double saldoInicial) {
        this.numeroConta = numeroConta;
        this.setTitular(titular);

        if (saldoInicial >= 0.0) {
            this.saldo = saldoInicial;
        } else {
            this.saldo = 0.0;
        }

        // Atualização dos acumuladores compartilhados de classe
        totalContas++;
        volumeTotalCustodiado += this.saldo;
    }

    // 4. Getters e Setters Defensivos
    public String getNumeroConta() {
        return this.numeroConta;
    }

    public String getTitular() {
        return this.titular;
    }

    public void setTitular(String novoTitular) {
        if (novoTitular != null && novoTitular.trim().length() >= 3) {
            this.titular = novoTitular.trim();
        } else {
            System.err.println("Erro: Nome de titular inválido!");
        }
    }

    public double getSaldo() {
        return this.saldo;
    }

    // 5. Métodos de Negócio de Instância
    public void depositar(double valor) {
        if (valor > 0.0) {
            this.saldo += valor;
            volumeTotalCustodiado += valor; // Atualiza a custódia geral
        }
    }

    public boolean sacar(double valor) {
        if (valor > 0.0 && this.saldo >= valor) {
            this.saldo -= valor;
            volumeTotalCustodiado -= valor;
            return true;
        }
        return false;
    }

    // 6. Métodos Estáticos (Operam sobre o estado da classe)
    public static int getTotalContas() {
        return totalContas;
    }

    public static double getVolumeTotalCustodiado() {
        return volumeTotalCustodiado;
    }
}
