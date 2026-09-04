
class PedidoEntidade {
    private StatusPedido status = StatusPedido.PENDENTE;
    private final double total;

    public PedidoEntidade(double total) {
        this.total = total;
    }

    public double getTotal() { return total; }
    public StatusPedido getStatus() { return status; }
    public void setStatus(StatusPedido status) { this.status = status; }
}
