import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class LivroDAOImpl implements LivroDAO {
    @Override
    public List<Livro> buscarPaginado(int pagina, int tamanhoPagina) {
        int offset = (pagina - 1) * tamanhoPagina;
        String sql = "SELECT id, titulo, autor FROM livros ORDER BY id LIMIT ? OFFSET ?";
        List<Livro> lista = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tamanhoPagina);
            pstmt.setInt(2, offset);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Livro(
                            rs.getLong("id"),
                            rs.getString("titulo"),
                            rs.getString("autor")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro na paginação de livros: " + e.getMessage(), e);
        }
        return lista;
    }
}
