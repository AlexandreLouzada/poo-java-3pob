import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Exercicio1DAOApp {
    public static void main(String[] args) {
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE alunos (id BIGINT AUTO_INCREMENT PRIMARY KEY, nome VARCHAR(100), matricula VARCHAR(50))");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        AlunoDAO dao = new AlunoDAOImpl();
        Aluno a1 = new Aluno("Beatriz Souza", "MAT-2026-01");
        Aluno a2 = new Aluno("Carlos Drummond", "MAT-2026-02");

        dao.inserir(a1);
        dao.inserir(a2);

        System.out.println("Alunos cadastrados:");
        dao.listarTodos().forEach(System.out::println);
    }
}
