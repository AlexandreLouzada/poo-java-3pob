import java.sql.*;
import java.util.List;

class PedidoDAOImpl implements PedidoDAO {
    @Override
    public long criarPedido(Connection conn, Pedido pedido) throws SQLException {
        String sql = "INSERT INTO pedidos_venda (cliente) VALUES (?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, pedido.getCliente());
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) return rs.getLong(1);
            }
        }
        throw new SQLException("Falha ao recuperar ID do pedido.");
    }
}
