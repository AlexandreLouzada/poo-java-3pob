import java.sql.*;
import java.util.Optional;

interface ClienteDAO {
    void salvar(Cliente cliente);
    Optional<Cliente> buscarPorCpf(String cpf);
}
