import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Exercicio3PaginacaoApp {
    public static void main(String[] args) {
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE livros (id BIGINT AUTO_INCREMENT PRIMARY KEY, titulo VARCHAR(100), autor VARCHAR(100))");
            for (int i = 1; i <= 12; i++) {
                stmt.execute(String.format("INSERT INTO livros (titulo, autor) VALUES ('Livro Volume %d', 'Autor %d')", i, i));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        LivroDAO dao = new LivroDAOImpl();

        System.out.println("--- Página 1 (Tamanho: 5) ---");
        dao.buscarPaginado(1, 5).forEach(System.out::println);

        System.out.println("\n--- Página 2 (Tamanho: 5) ---");
        dao.buscarPaginado(2, 5).forEach(System.out::println);
    }
}
