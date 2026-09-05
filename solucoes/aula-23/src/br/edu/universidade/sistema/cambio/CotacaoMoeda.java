package br.edu.universidade.sistema.cambio;

public class CotacaoMoeda {
    private final String provedor;
    private final String moeda;
    private final double valorTaxa;
    private final long tempoRespostaMs;

    public CotacaoMoeda(String provedor, String moeda, double valorTaxa, long tempoRespostaMs) {
        if (provedor == null || provedor.trim().isEmpty()) {
            throw new IllegalArgumentException("Provedor de cotacao nao pode estar em branco.");
        }
        if (moeda == null || moeda.trim().isEmpty()) {
            throw new IllegalArgumentException("Moeda nao pode estar em branco.");
        }
        if (valorTaxa <= 0.0) {
            throw new IllegalArgumentException("Valor da taxa deve ser maior que zero: " + valorTaxa);
        }
        if (tempoRespostaMs < 0) {
            throw new IllegalArgumentException("Tempo de resposta nao pode ser negativo: " + tempoRespostaMs);
        }
        this.provedor = provedor;
        this.moeda = moeda;
        this.valorTaxa = valorTaxa;
        this.tempoRespostaMs = tempoRespostaMs;
    }

    public String getProvedor() { return provedor; }
    public String getMoeda() { return moeda; }
    public double getValorTaxa() { return valorTaxa; }
    public long getTempoRespostaMs() { return tempoRespostaMs; }

    @Override
    public String toString() {
        return String.format("Cotacao [Provedor: %s | Moeda: %s | Taxa: %.4f | Tempo: %d ms]",
                provedor, moeda, valorTaxa, tempoRespostaMs);
    }
}