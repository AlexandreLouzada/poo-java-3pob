import java.sql.*;
import java.util.List;

interface ItemPedidoDAO {
    void salvarItens(Connection conn, long pedidoId, List<ItemPedido> itens) throws SQLException;
}
