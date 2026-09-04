import java.sql.*;
import java.util.List;

class VendaService {
    private final PedidoDAO pedidoDAO;
    private final ItemPedidoDAO itemPedidoDAO;

    public VendaService(PedidoDAO pedidoDAO, ItemPedidoDAO itemPedidoDAO) {
        this.pedidoDAO = pedidoDAO;
        this.itemPedidoDAO = itemPedidoDAO;
    }

    public void registrarVenda(Pedido pedido) {
        try (Connection conn = ConnectionFactory.getConnection()) {
            conn.setAutoCommit(false);
            try {
                long pedidoId = pedidoDAO.criarPedido(conn, pedido);
                itemPedidoDAO.salvarItens(conn, pedidoId, pedido.getItens());

                conn.commit();
                System.out.printf("Venda #%d faturada com sucesso para %s!%n", pedidoId, pedido.getCliente());
            } catch (Exception e) {
                conn.rollback();
                System.err.println("Erro na transação. Rollback executado: " + e.getMessage());
            } finally {
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro de infraestrutura: " + e.getMessage(), e);
        }
    }
}
