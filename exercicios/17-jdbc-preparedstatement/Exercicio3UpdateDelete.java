import java.util.Set;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

public class Exercicio3UpdateDelete {

    public static void atualizarSalario(Connection conn, Long id, double novoSalario) throws SQLException {
        String sql = "UPDATE funcionarios SET salario = ? WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, novoSalario);
            pstmt.setLong(2, id);

            int linhasAfetadas = pstmt.executeUpdate();
            if (linhasAfetadas > 0) {
                System.out.printf("Sucesso: Salário do funcionário ID %d atualizado para R$ %.2f.%n", id, novoSalario);
            } else {
                System.out.printf("Aviso: Nenhum funcionário encontrado com o ID %d para atualização.%n", id);
            }
        }
    }

    public static void deletarPorId(Connection conn, Long id) throws SQLException {
        String sql = "DELETE FROM funcionarios WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);

            int linhasAfetadas = pstmt.executeUpdate();
            if (linhasAfetadas > 0) {
                System.out.printf("Sucesso: Funcionário ID %d excluído com sucesso.%n", id);
            } else {
                System.out.printf("Aviso: Nenhum funcionário encontrado com o ID %d para exclusão.%n", id);
            }
        }
    }

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE funcionarios (id BIGINT PRIMARY KEY, nome VARCHAR(100), salario DOUBLE)");
            stmt.execute("INSERT INTO funcionarios VALUES (1, 'Beatriz', 5000.00), (2, 'Carlos', 3500.00)");

            atualizarSalario(conn, 1L, 6200.00);
            atualizarSalario(conn, 99L, 4000.00); // ID inexistente

            deletarPorId(conn, 2L);
            deletarPorId(conn, 88L); // ID inexistente

        } catch (SQLException e) {
            System.err.println("Erro na operação: " + e.getMessage());
        }
    }
}

