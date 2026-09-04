import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class Exercicio4BatchProcessing {

    public static void inserirLoteLog(Connection conn, List<String> mensagensLog) throws SQLException {
        String sql = "INSERT INTO logs_sistema (mensagem, data_registro) VALUES (?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (String msg : mensagensLog) {
                pstmt.setString(1, msg);
                pstmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                pstmt.addBatch(); // Adiciona ao buffer do lote
            }

            int[] resultados = pstmt.executeBatch(); // Executa em uma única viagem de rede
            System.out.printf("Lote processado com sucesso! Total de registros gravados: %d%n", resultados.length);
        }
    }

    public static void main(String[] args) {
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE logs_sistema (id BIGINT AUTO_INCREMENT PRIMARY KEY, mensagem VARCHAR(255), data_registro TIMESTAMP)");

            List<String> logs = Arrays.asList(
                    "[AUTH] Usuário admin efetuou login",
                    "[CACHE] Limpeza de sessão executada",
                    "[PAYMENT] Transação #4819 aprovada",
                    "[BACKUP] Rotina de integridade concluída"
            );

            inserirLoteLog(conn, logs);

        } catch (SQLException e) {
            System.err.println("Erro no envio em lote: " + e.getMessage());
        }
    }
}
