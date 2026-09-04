import java.util.Set;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

public class Exercicio5TransacaoBancaria {

    public static void transferirFundos(Connection conn, Long idOrigem, Long idDestino, double valor) throws SQLException {
        String sqlVerificaSaldo = "SELECT saldo FROM contas WHERE id = ?";
        String sqlDebito = "UPDATE contas SET saldo = saldo - ? WHERE id = ?";
        String sqlCredito = "UPDATE contas SET saldo = saldo + ? WHERE id = ?";

        // Desativa a confirmação automática
        conn.setAutoCommit(false);

        try {
            // 1. Verificação de saldo
            double saldoOrigem = 0.0;
            try (PreparedStatement psVerifica = conn.prepareStatement(sqlVerificaSaldo)) {
                psVerifica.setLong(1, idOrigem);
                try (ResultSet rs = psVerifica.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("Conta de origem não encontrada.");
                    }
                    saldoOrigem = rs.getDouble("saldo");
                }
            }

            if (saldoOrigem < valor) {
                throw new SQLException("Saldo insuficiente para transferência. Saldo atual: R$ " + saldoOrigem);
            }

            // 2. Débito
            try (PreparedStatement psDebito = conn.prepareStatement(sqlDebito)) {
                psDebito.setDouble(1, valor);
                psDebito.setLong(2, idOrigem);
                psDebito.executeUpdate();
            }

            // Simulação condicional de falha (se idDestino == 999 força erro)
            if (idDestino == 999L) {
                throw new SQLException("Falha na rede durante crédito na conta destino.");
            }

            // 3. Crédito
            try (PreparedStatement psCredito = conn.prepareStatement(sqlCredito)) {
                psCredito.setDouble(1, valor);
                psCredito.setLong(2, idDestino);
                int afetadas = psCredito.executeUpdate();
                if (afetadas == 0) {
                    throw new SQLException("Conta de destino não encontrada.");
                }
            }

            // 4. Se tudo deu certo, efetiva as alterações
            conn.commit();
            System.out.printf("Transação concluída com sucesso! Transferido R$ %.2f de Conta %d para Conta %d.%n", 
                              valor, idOrigem, idDestino);

        } catch (SQLException e) {
            // Em caso de erro, desfaz qualquer operação parcial
            conn.rollback();
            System.err.printf("Transação revertida (Rollback)! Motivo: %s%n", e.getMessage());
        } finally {
            // Restaura o estado padrão da conexão
            conn.setAutoCommit(true);
        }
    }

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute("CREATE TABLE contas (id BIGINT PRIMARY KEY, saldo DOUBLE)");
            stmt.execute("INSERT INTO contas VALUES (101, 1000.00), (202, 500.00)");

            System.out.println("--- Cenário 1: Transferência Válida ---");
            transferirFundos(conn, 101L, 202L, 300.00);

            System.out.println("\n--- Cenário 2: Transferência com Falha / Rollback ---");
            transferirFundos(conn, 101L, 999L, 200.00); // Força erro para acionar rollback

        } catch (SQLException e) {
            System.err.println("Erro de conexão: " + e.getMessage());
        }
    }
}

