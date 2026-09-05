package br.edu.universidade.sistema.patrimonio;

import br.edu.universidade.sistema.patrimonio.dao.AtivoDAO;
import br.edu.universidade.sistema.patrimonio.dao.AtivoDAOJdbc;
import br.edu.universidade.sistema.patrimonio.dominio.AtivoPatrimonial;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class PatrimonioApp {
    public static void main(String[] args) {
        String url = "jdbc:h2:mem:patrimonio;DB_CLOSE_DELAY=-1";
        try (Connection conexao = DriverManager.getConnection(url, "sa", "")) {
            criarTabela(conexao);

            AtivoDAO dao = new AtivoDAOJdbc(conexao);

            System.out.println("--- Cadastro de Ativos (INSERT com chave gerada) ---");
            AtivoPatrimonial computador = new AtivoPatrimonial("TMB-0001", "Computador Desktop", 4200.00, "ATIVO");
            AtivoPatrimonial servidor = new AtivoPatrimonial("TMB-0002", "Servidor Rack", 28500.00, "ATIVO");
            AtivoPatrimonial monitor = new AtivoPatrimonial("TMB-0003", "Monitor 27 Pol", 1450.00, "ATIVO");

            dao.salvar(computador);
            dao.salvar(servidor);
            dao.salvar(monitor);
            System.out.println("Ativos salvos com IDs gerados: "
                    + computador.getId() + ", " + servidor.getId() + ", " + monitor.getId());

            System.out.println("\n--- Busca por Tombo (PreparedStatement) ---");
            dao.buscarPorTombo("TMB-0002").ifPresent(System.out::println);

            System.out.println("\n--- Baixa do Servidor (UPDATE status = BAIXADO) ---");
            dao.atualizarStatus(servidor.getId(), "BAIXADO");
            System.out.println("Status atualizado para BAIXADO no ID " + servidor.getId() + ".");

            System.out.println("\n--- Listagem de Ativos em Operacao (WHERE status = 'ATIVO') ---");
            List<AtivoPatrimonial> emOperacao = dao.listarAtivosEmOperacao();
            emOperacao.forEach(System.out::println);
            System.out.printf("Total de ativos ativos recuperados pelo ResultSet: %d%n", emOperacao.size());
        } catch (SQLException ex) {
            System.err.println("Falha de persistencia: " + ex.getMessage());
        }
    }

    private static void criarTabela(Connection conexao) throws SQLException {
        String ddl = """
                CREATE TABLE ativos (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    tombo VARCHAR(20) UNIQUE,
                    descricao VARCHAR(100),
                    valor_aquisicao DOUBLE,
                    status VARCHAR(20)
                )
                """;
        try (Statement stmt = conexao.createStatement()) {
            stmt.execute(ddl);
        }
    }
}