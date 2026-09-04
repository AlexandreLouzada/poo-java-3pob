import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

interface GenericDAO<T, ID> {
    void salvar(T entidade, Connection conn) throws SQLException;

    Optional<T> buscarPorId(ID id, Connection conn) throws SQLException;

    List<T> listarTodos(Connection conn) throws SQLException;
}