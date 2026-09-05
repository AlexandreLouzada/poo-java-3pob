package br.edu.universidade.sistema.vendas.dominio;

public class VendaRepresentante {
    private Long idVenda;
    private String nomeRepresentante;
    private String regiao;
    private double valorVenda;
    private double comissaoPaga;

    public VendaRepresentante(Long idVenda, String nomeRepresentante, String regiao,
                              double valorVenda, double comissaoPaga) {
        if (idVenda == null) {
            throw new IllegalArgumentException("ID da venda nao pode ser nulo.");
        }
        if (comissaoPaga < 0.0) {
            throw new IllegalArgumentException("Comissao nao pode ser negativa: " + comissaoPaga);
        }
        if (valorVenda <= 0.0) {
            throw new IllegalArgumentException("Valor da venda deve ser maior que zero: " + valorVenda);
        }
        this.idVenda = idVenda;
        this.nomeRepresentante = nomeRepresentante;
        this.regiao = regiao;
        this.valorVenda = valorVenda;
        this.comissaoPaga = comissaoPaga;
    }

    public Long getIdVenda() { return idVenda; }
    public String getNomeRepresentante() { return nomeRepresentante; }
    public String getRegiao() { return regiao; }
    public double getValorVenda() { return valorVenda; }
    public double getComissaoPaga() { return comissaoPaga; }

    @Override
    public String toString() {
        return String.format("Venda [ID: %d | Repr: %s | Regiao: %s | Valor: R$ %.2f | Comissao: R$ %.2f]",
                idVenda, nomeRepresentante, regiao, valorVenda, comissaoPaga);
    }
}