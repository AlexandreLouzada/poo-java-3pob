import java.sql.*;
import java.util.Optional;

class ClienteService {
    private final ClienteDAO clienteDAO;

    public ClienteService(ClienteDAO clienteDAO) {
        this.clienteDAO = clienteDAO;
    }

    public void cadastrarCliente(Cliente cliente) {
        if (cliente.getEmail() == null || cliente.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Regra de Negócio: O e-mail do cliente é obrigatório.");
        }
        if (clienteDAO.buscarPorCpf(cliente.getCpf()).isPresent()) {
            throw new IllegalStateException("Regra de Negócio: Já existe um cliente cadastrado com o CPF: " + cliente.getCpf());
        }
        clienteDAO.salvar(cliente);
        System.out.println("Cliente cadastrado com sucesso! ID: " + cliente.getId());
    }
}
