import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Exercicio1InsertId {

    public static long inserirCliente(Connection conn, String nome, String email) throws SQLException {
        String sql = "INSERT INTO clientes (nome, email) VALUES (?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, nome);
            pstmt.setString(2, email);

            int linhasAfetadas = pstmt.executeUpdate();

            if (linhasAfetadas > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        long idGerado = rs.getLong(1);
                        System.out.printf("Cliente '%s' inserido com sucesso! ID gerado: %d%n", nome, idGerado);
                        return idGerado;
                    }
                }
            }
            throw new SQLException("Falha na criação do cliente, nenhum ID obtido.");
        }
    }

    public static void main(String[] args) {
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE clientes (id BIGINT AUTO_INCREMENT PRIMARY KEY, nome VARCHAR(100), email VARCHAR(100))");

            inserirCliente(conn, "Lucas Mendes", "lucas@email.com");
            inserirCliente(conn, "Mariana Silva", "mariana@email.com");

        } catch (SQLException e) {
            System.err.println("Erro de banco de dados: " + e.getMessage());
        }
    }
}
