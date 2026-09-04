import java.sql.*;
import java.util.List;

class ItemPedidoDAOImpl implements ItemPedidoDAO {
    @Override
    public void salvarItens(Connection conn, long pedidoId, List<ItemPedido> itens) throws SQLException {
        String sql = "INSERT INTO itens_pedido (pedido_id, produto, quantidade, preco) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (ItemPedido item : itens) {
                pstmt.setLong(1, pedidoId);
                pstmt.setString(2, item.getProduto());
                pstmt.setInt(3, item.getQuantidade());
                pstmt.setDouble(4, item.getPreco());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        }
    }
}
