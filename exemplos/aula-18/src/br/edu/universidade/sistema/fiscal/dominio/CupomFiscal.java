package br.edu.universidade.sistema.fiscal.dominio;

// 1. Entidade de Domínio Imutável representando o Cupom Fiscal
public class CupomFiscal {
    private final Long id;
    private final String chaveNfe;
    private final double valorTotal;
    private final boolean cancelado;

    public CupomFiscal(Long id, String chaveNfe, double valorTotal, boolean cancelado) {
        this.id = id;
        this.chaveNfe = chaveNfe;
        this.valorTotal = valorTotal;
        this.cancelado = cancelado;
    }

    public Long getId() { return id; }
    public String getChaveNfe() { return chaveNfe; }
    public double getValorTotal() { return valorTotal; }
    public boolean isCancelado() { return cancelado; }

    // Simulação de custo de CPU (Q) por elemento: cálculo de validação criptográfica
    public double calcularImpostoComplexo() {
        double imposto = 0.0;
        for (int i = 0; i < 50; i++) {
            imposto += Math.sin(valorTotal) * Math.cos(i);
        }
        return Math.max(0.0, valorTotal * 0.12 + (imposto * 0.001));
    }
}
