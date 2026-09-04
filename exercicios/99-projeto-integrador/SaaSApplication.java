import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

// ============================================================================
// 4. EXECUÇÃO INTEGRADA (Main)
// ============================================================================

public class SaaSApplication {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        // 1. Inicialização do schema em memória
        try (Connection conn = ConnectionFactory.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS faturas (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    cliente_id BIGINT NOT NULL,
                    valor_total DOUBLE NOT NULL,
                    status VARCHAR(20) NOT NULL,
                    data_faturamento TIMESTAMP NOT NULL
                )
            """);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        // 2. Mock do Gateway de Pagamento
        GatewayPagamento gateway = (cliente, valor) -> {
            System.out.printf("[Gateway] Cobrança de R$ %.2f aprovada para %s%n", valor, cliente.getRazaoSocial());
            return true;
        };

        // 3. Mock do Arquivo de Telemetria (NIO.2)
        Path arquivoTelemetria = Path.of("telemetria_consumo.csv");
        try {
            String csvInicial = """
                    clienteId,usuarios,gbArmazenados
                    1,8,120
                    2,65,500
                    """;
            Files.writeString(arquivoTelemetria, csvInicial, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        // 4. Ingestão e Processamento
        IngestaoTelemetriaService ingestaoService = new IngestaoTelemetriaService();
        GenericDAO<Fatura, Long> faturaDAO = new FaturaDAOImpl();
        FaturamentoService faturamentoService = new FaturamentoService(faturaDAO, gateway);
        RelatorioFaturamentoService relatorioService = new RelatorioFaturamentoService();

        try {
            List<TelemetriaConsumo> consumos = ingestaoService.importarArquivo(arquivoTelemetria);

            Map<Long, Cliente> baseClientes = Map.of(
                    1L, new Cliente(1L, "TechCorp Ltda", "contato@techcorp.com", new PlanoBasico()),
                    2L, new Cliente(2L, "Global Logistics S.A.", "financeiro@globallog.com", new PlanoEnterprise())
            );

            List<CompletableFuture<Fatura>> futuros = consumos.stream()
                    .map(consumo -> {
                        Cliente cliente = baseClientes.get(consumo.clienteId());
                        return faturamentoService.faturarClienteAsync(cliente, consumo);
                    })
                    .toList();

            List<Fatura> faturasFinais = futuros.stream()
                    .map(CompletableFuture::join)
                    .toList();

            faturasFinais.forEach(System.out::println);
            relatorioService.gerarRelatorioConsolidado(faturasFinais);

        } catch (TelemetriaException e) {
            System.err.println("Erro na telemetria: " + e.getMessage());
        } finally {
            faturamentoService.encerrarExecutores();
            try {
                Files.deleteIfExists(arquivoTelemetria);
            } catch (IOException ignored) {}
        }
    }
}
