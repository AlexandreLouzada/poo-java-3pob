import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class Exercicio2GenericDAOApp {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE produtos (id BIGINT AUTO_INCREMENT PRIMARY KEY, descricao VARCHAR(100), preco_unitario DOUBLE, estoque INT)");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        GenericDAO<ProdutoEntidade, Long> dao = new ProdutoDAOImpl();

        ProdutoEntidade p1 = new ProdutoEntidade("Monitor UltraWide", 1350.00, 10);
        dao.salvar(p1);
        System.out.println("Inserido: " + p1);

        p1.setPrecoUnitario(1299.90);
        dao.atualizar(p1);

        dao.buscarPorId(p1.getId()).ifPresent(p -> System.out.println("Buscado após atualização: " + p));

        dao.deletarPorId(p1.getId());
        System.out.println("Existe após deleção? " + dao.buscarPorId(p1.getId()).isPresent());
    }
}
