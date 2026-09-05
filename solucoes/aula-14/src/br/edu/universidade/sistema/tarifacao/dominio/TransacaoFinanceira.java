package br.edu.universidade.sistema.tarifacao.dominio;

public class TransacaoFinanceira {
    private Long id;
    private String chavePix;
    private double valor;
    private String tipoOperacao;

    public TransacaoFinanceira(Long id, String chavePix, double valor, String tipoOperacao) {
        if (id == null) {
            throw new IllegalArgumentException("ID da transacao nao pode ser nulo.");
        }
        if (chavePix == null || chavePix.trim().isEmpty()) {
            throw new IllegalArgumentException("Chave Pix nao pode ser nula ou vazia.");
        }
        if (valor <= 0.0) {
            throw new IllegalArgumentException("Valor da transacao deve ser maior que zero: " + valor);
        }
        if (tipoOperacao == null || tipoOperacao.trim().isEmpty()) {
            throw new IllegalArgumentException("Tipo de operacao nao pode ser nulo ou vazio.");
        }
        this.id = id;
        this.chavePix = chavePix;
        this.valor = valor;
        this.tipoOperacao = tipoOperacao;
    }

    public void abaterTaxa(double taxa) {
        if (taxa > 0.0 && taxa <= this.valor) {
            this.valor -= taxa;
        }
    }

    public Long getId() { return id; }
    public String getChavePix() { return chavePix; }
    public double getValor() { return valor; }
    public String getTipoOperacao() { return tipoOperacao; }

    @Override
    public String toString() {
        return String.format("Transacao [ID: %d | Pix: %s | Tipo: %s | Valor: R$ %.2f]",
                id, chavePix, tipoOperacao, valor);
    }
}