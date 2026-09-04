import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Exercicio2SelectResultSet {

    public static List<ProdutoDTO> buscarPorFaixaPreco(Connection conn, double min, double max) throws SQLException {
        String sql = "SELECT id, nome, preco, estoque FROM produtos WHERE preco BETWEEN ? AND ? ORDER BY preco ASC";
        List<ProdutoDTO> lista = new ArrayList<>();

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, min);
            pstmt.setDouble(2, max);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    long id = rs.getLong("id");
                    String nome = rs.getString("nome");
                    double preco = rs.getDouble("preco");
                    int estoque = rs.getInt("estoque");

                    lista.add(new ProdutoDTO(id, nome, preco, estoque));
                }
            }
        }
        return lista;
    }

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE produtos (id BIGINT AUTO_INCREMENT PRIMARY KEY, nome VARCHAR(100), preco DOUBLE, estoque INT)");
            stmt.execute("INSERT INTO produtos (nome, preco, estoque) VALUES ('Teclado', 150.00, 10), ('Mouse', 75.00, 25), ('Monitor', 1100.00, 5), ('Cabo USB', 25.00, 50)");

            List<ProdutoDTO> produtosFiltrados = buscarPorFaixaPreco(conn, 50.0, 200.0);

            System.out.println("--- Produtos na faixa de R$ 50.00 a R$ 200.00 ---");
            produtosFiltrados.forEach(System.out::println);

        } catch (SQLException e) {
            System.err.println("Erro na consulta: " + e.getMessage());
        }
    }
}
