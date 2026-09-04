import java.sql.*;
import java.util.Optional;

class ClienteDAOImpl implements ClienteDAO {
    @Override
    public void salvar(Cliente cliente) {
        String sql = "INSERT INTO clientes_service (nome, email, cpf) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, cliente.getNome());
            pstmt.setString(2, cliente.getEmail());
            pstmt.setString(3, cliente.getCpf());
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) cliente.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao persistir cliente: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<Cliente> buscarPorCpf(String cpf) {
        String sql = "SELECT id, nome, email, cpf FROM clientes_service WHERE cpf = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, cpf);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Cliente c = new Cliente(rs.getString("nome"), rs.getString("email"), rs.getString("cpf"));
                    c.setId(rs.getLong("id"));
                    return Optional.of(c);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar CPF: " + e.getMessage(), e);
        }
        return Optional.empty();
    }
}
