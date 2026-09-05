package br.edu.universidade.sistema.concorrencia.dominio;

// 1. Entidade de Domínio representando a Solicitação de Pedido
public class SolicitacaoPedido {
    private final Long idPedido;
    private final String cliente;
    private final double valorTotal;

    public SolicitacaoPedido(Long idPedido, String cliente, double valorTotal) {
        this.idPedido = idPedido;
        this.cliente = cliente;
        this.valorTotal = valorTotal;
    }

    public Long getIdPedido() { return idPedido; }
    public String getCliente() { return cliente; }
    public double getValorTotal() { return valorTotal; }

    @Override
    public String toString() {
        return String.format("Pedido #%d | Cliente: %s | Total: R$ %.2f", idPedido, cliente, valorTotal);
    }
}
