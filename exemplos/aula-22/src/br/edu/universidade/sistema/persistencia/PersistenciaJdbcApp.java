package br.edu.universidade.sistema.persistencia;

import br.edu.universidade.sistema.persistencia.dao.ContaDaoJdbc;
import br.edu.universidade.sistema.persistencia.dominio.ContaBancaria;
import br.edu.universidade.sistema.persistencia.service.TransferenciaService;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

// 5. Aplicação Executável demonstrando o ciclo completo de persistência e rollback
public class PersistenciaJdbcApp {

    // Configuração de banco H2 em memória
    private static final String URL = "jdbc:h2:mem:sistemabancario;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    public static void main(String[] args) {
        System.out.println("========== PERSISTÊNCIA RELACIONAL COM JDBC E DAO ==========");

        try (Connection conexao = DriverManager.getConnection(URL, USER, PASSWORD)) {
            inicializarTabelas(conexao);

            ContaDaoJdbc contaDAO = new ContaDaoJdbc(conexao);
            TransferenciaService service = new TransferenciaService(conexao, contaDAO);

            System.out.println("\n--- 1. Inserção de Contas com Chave Auto-Incremento ---");
            ContaBancaria c1 = new ContaBancaria("001-A", "Beatriz Costa", 1000.00);
            ContaBancaria c2 = new ContaBancaria("002-B", "Carlos Eduardo", 300.00);

            contaDAO.salvar(c1);
            contaDAO.salvar(c2);

            System.out.println("Contas persistidas no banco:");
            contaDAO.listarTodas().forEach(System.out::println);

            System.out.println("\n--- 2. Execução de Transferência Válida (Commit) ---");
            service.transferir("001-A", "002-B", 400.00);

            System.out.println("Saldos pós-transferência:");
            contaDAO.listarTodas().forEach(System.out::println);

            System.out.println("\n--- 3. Tentativa de Transferência Inválida (Disparo de Rollback) ---");
            try {
                // Tentativa de transferir mais do que o saldo atual de Beatriz (Saldo: R$ 600.00)
                service.transferir("001-A", "002-B", 900.00);
            } catch (Exception e) {
                System.out.println("Exceção capturada pela aplicação com sucesso.");
            }

            System.out.println("\nSaldos após o Rollback (Comprovação da integridade dos valores):");
            contaDAO.listarTodas().forEach(System.out::println);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("\n============================================================");
    }

    private static void inicializarTabelas(Connection conn) throws Exception {
        String ddl = """
            CREATE TABLE contas (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                numero_conta VARCHAR(20) NOT NULL UNIQUE,
                titular VARCHAR(100) NOT NULL,
                saldo DOUBLE NOT NULL
            );
            """;
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(ddl);
        }
    }
}
