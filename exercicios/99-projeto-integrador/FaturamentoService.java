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

class FaturamentoService {
    private final GenericDAO<Fatura, Long> faturaDAO;
    private final GatewayPagamento gateway;
    private final ExecutorService executor;

    public FaturamentoService(GenericDAO<Fatura, Long> faturaDAO, GatewayPagamento gateway) {
        this.faturaDAO = faturaDAO;
        this.gateway = gateway;
        this.executor = Executors.newFixedThreadPool(4);
    }

    public CompletableFuture<Fatura> faturarClienteAsync(Cliente cliente, TelemetriaConsumo consumo) {
        return CompletableFuture.supplyAsync(() -> {
            double valorCobrado = cliente.getPlano().calcularFatura(consumo.usuarios(), consumo.gbArmazenados());
            Fatura fatura = new Fatura(cliente.getId(), valorCobrado, StatusFatura.PENDENTE);

            try (Connection conn = ConnectionFactory.getConnection()) {
                conn.setAutoCommit(false);
                try {
                    boolean aprovado = gateway.processarCobranca(cliente, valorCobrado);
                    if (!aprovado) {
                        throw new FalhaCobrancaException("Pagamento recusado para o cliente: " + cliente.getRazaoSocial());
                    }

                    fatura.setStatus(StatusFatura.PAGA);
                    faturaDAO.salvar(fatura, conn);
                    conn.commit();
                } catch (Exception e) {
                    conn.rollback();
                    fatura.setStatus(StatusFatura.RECUSADA);
                    throw new RuntimeException("Transação revertida: " + e.getMessage(), e);
                } finally {
                    conn.setAutoCommit(true);
                }
            } catch (SQLException e) {
                throw new RuntimeException("Erro de conexão com banco de dados.", e);
            }
            return fatura;
        }, executor);
    }

    public void encerrarExecutores() {
        executor.shutdown();
    }
}
