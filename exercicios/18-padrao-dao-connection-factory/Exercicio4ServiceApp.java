import java.sql.*;
import java.util.Optional;

public class Exercicio4ServiceApp {
    public static void main(String[] args) {
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE clientes_service (id BIGINT AUTO_INCREMENT PRIMARY KEY, nome VARCHAR(100), email VARCHAR(100), cpf VARCHAR(14))");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ClienteDAO dao = new ClienteDAOImpl();
        ClienteService service = new ClienteService(dao);

        Cliente c1 = new Cliente("Mariana", "mariana@email.com", "111.222.333-44");
        service.cadastrarCliente(c1);

        try {
            Cliente cDuplicado = new Cliente("Outro", "outro@email.com", "111.222.333-44");
            service.cadastrarCliente(cDuplicado);
        } catch (IllegalStateException e) {
            System.err.println("Validação capturada com sucesso: " + e.getMessage());
        }
    }
}
