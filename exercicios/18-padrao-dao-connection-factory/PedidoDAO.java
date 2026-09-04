import java.sql.*;
import java.util.List;

interface PedidoDAO {
    long criarPedido(Connection conn, Pedido pedido) throws SQLException;
}
