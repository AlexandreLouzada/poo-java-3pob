import java.sql.*;
import java.util.ArrayList;
import java.util.List;

interface AlunoDAO {
    void inserir(Aluno aluno);
    List<Aluno> listarTodos();
}
