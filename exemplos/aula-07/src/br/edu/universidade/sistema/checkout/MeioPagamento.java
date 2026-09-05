package br.edu.universidade.sistema.checkout;

// 2. Classe Base Abstrata (Compartilhamento de Estado e Reuso de Código)
public abstract class MeioPagamento implements Auditavel {
    protected final String codigoTransacao;
    protected double valorAutorizado;

    public MeioPagamento(String codigoTransacao) {
        this.codigoTransacao = codigoTransacao;
    }

    public String getCodigoTransacao() {
        return this.codigoTransacao;
    }

    public double getValorAutorizado() {
        return this.valorAutorizado;
    }

    // Método abstrato: a subclasse concreta é obrigada a implementar a regra
    public abstract boolean autorizar(double valor);
}
