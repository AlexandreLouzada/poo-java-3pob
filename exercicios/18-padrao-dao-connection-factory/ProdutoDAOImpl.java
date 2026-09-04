import java.util.Set;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

class ProdutoDAOImpl implements GenericDAO<ProdutoEntidade, Long> {
    @Override
    public void salvar(ProdutoEntidade p) {
        String sql = "INSERT INTO produtos (descricao, preco_unitario, estoque) VALUES (?, ?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, p.getDescricao());
            pstmt.setDouble(2, p.getPrecoUnitario());
            pstmt.setInt(3, p.getQuantidadeEstoque());
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) p.setId(rs.getLong(1));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar produto: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<ProdutoEntidade> buscarPorId(Long id) {
        String sql = "SELECT id, descricao, preco_unitario, estoque FROM produtos WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new ProdutoEntidade(
                            rs.getLong("id"),
                            rs.getString("descricao"),
                            rs.getDouble("preco_unitario"),
                            rs.getInt("estoque")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar produto: " + e.getMessage(), e);
        }
        return Optional.empty();
    }

    @Override
    public List<ProdutoEntidade> listarTodos() {
        String sql = "SELECT id, descricao, preco_unitario, estoque FROM produtos";
        List<ProdutoEntidade> lista = new ArrayList<>();
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                lista.add(new ProdutoEntidade(
                        rs.getLong("id"),
                        rs.getString("descricao"),
                        rs.getDouble("preco_unitario"),
                        rs.getInt("estoque")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar produtos: " + e.getMessage(), e);
        }
        return lista;
    }

    @Override
    public void atualizar(ProdutoEntidade p) {
        String sql = "UPDATE produtos SET descricao = ?, preco_unitario = ?, estoque = ? WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, p.getDescricao());
            pstmt.setDouble(2, p.getPrecoUnitario());
            pstmt.setInt(3, p.getQuantidadeEstoque());
            pstmt.setLong(4, p.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar produto: " + e.getMessage(), e);
        }
    }

    @Override
    public void deletarPorId(Long id) {
        String sql = "DELETE FROM produtos WHERE id = ?";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao deletar produto: " + e.getMessage(), e);
        }
    }
}

