
class ProcessadorPagamentoService {
    private final GatewayPagamento gateway;
    private final PedidoRepository repository;

    public ProcessadorPagamentoService(GatewayPagamento gateway, PedidoRepository repository) {
        this.gateway = gateway;
        this.repository = repository;
    }

    public void processar(PedidoEntidade pedido) {
        gateway.cobrar(pedido.getTotal());
        pedido.setStatus(StatusPedido.PAGO);
        repository.atualizar(pedido);
    }
}
