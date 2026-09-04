import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class AlunoDAOImpl implements AlunoDAO {
    @Override
    public void inserir(Aluno aluno) {
        String sql = "INSERT INTO alunos (nome, matricula) VALUES (?, ?)";
        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, aluno.getNome());
            pstmt.setString(2, aluno.getMatricula());
            pstmt.executeUpdate();

            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) {
                    aluno.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao persistir aluno: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Aluno> listarTodos() {
        String sql = "SELECT id, nome, matricula FROM alunos ORDER BY id";
        List<Aluno> lista = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                lista.add(new Aluno(
                    rs.getLong("id"),
                    rs.getString("nome"),
                    rs.getString("matricula")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao consultar alunos: " + e.getMessage(), e);
        }
        return lista;
    }
}
