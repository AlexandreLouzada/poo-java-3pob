import java.sql.*;
import java.util.List;

public class Exercicio5TransacaoMultiplosDAOsApp {
    public static void main(String[] args) {
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE pedidos_venda (id BIGINT AUTO_INCREMENT PRIMARY KEY, cliente VARCHAR(100))");
            stmt.execute("CREATE TABLE itens_pedido (id BIGINT AUTO_INCREMENT PRIMARY KEY, pedido_id BIGINT, produto VARCHAR(100), quantidade INT, preco DOUBLE)");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        PedidoDAO pedidoDAO = new PedidoDAOImpl();
        ItemPedidoDAO itemPedidoDAO = new ItemPedidoDAOImpl();
        VendaService service = new VendaService(pedidoDAO, itemPedidoDAO);

        Pedido pedido = new Pedido("Lucas Mendes", List.of(
                new ItemPedido("Teclado Mecânico", 1, 250.00),
                new ItemPedido("Mousepad Gamer", 2, 45.00)
        ));

        service.registrarVenda(pedido);
    }
}
