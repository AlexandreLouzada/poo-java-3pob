
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FaturamentoServiceTest {

    @Mock
    private GenericDAO<Fatura, Long> faturaDAO;

    @Mock
    private GatewayPagamento gateway;

    @Captor
    private ArgumentCaptor<Fatura> faturaCaptor;

    @Captor
    private ArgumentCaptor<Double> valorCobrancaCaptor;

    private FaturamentoService faturamentoService;

    @BeforeEach
    void setUp() throws SQLException {
        // Inicializa a tabela no H2 em memória antes de cada teste
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
        }

        faturamentoService = new FaturamentoService(faturaDAO, gateway);
    }

    @AfterEach
    void tearDown() {
        faturamentoService.encerrarExecutores();
    }

    @Test
    @DisplayName("Deve faturar com sucesso cliente do Plano Básico, persistir no DAO e capturar argumentos corretos")
    void deveFaturarClientePlanoBasicoComSucesso() throws SQLException {
        // Arrange: 8 usuários (3 excedentes * 15 = 45) + 100 GB (* 0.50 = 50) + Base (99) = R$ 194.00
        Cliente cliente = new Cliente(1L, "Alpha Dev Ltda", "contato@alphadev.com", new PlanoBasico());
        TelemetriaConsumo consumo = new TelemetriaConsumo(1L, 8, 100L);

        when(gateway.processarCobranca(eq(cliente), anyDouble())).thenReturn(true);
        doNothing().when(faturaDAO).salvar(any(Fatura.class), any(Connection.class));

        // Act
        CompletableFuture<Fatura> futuro = faturamentoService.faturarClienteAsync(cliente, consumo);
        Fatura faturaGerada = futuro.join();

        // Assert - Retorno
        assertNotNull(faturaGerada);
        assertEquals(StatusFatura.PAGA, faturaGerada.getStatus());
        assertEquals(194.00, faturaGerada.getValorTotal(), 0.001);

        // Assert - Verificação do Gateway com ArgumentCaptor
        verify(gateway, times(1)).processarCobranca(eq(cliente), valorCobrancaCaptor.capture());
        assertEquals(194.00, valorCobrancaCaptor.getValue(), 0.001);

        // Assert - Verificação do DAO com ArgumentCaptor
        verify(faturaDAO, times(1)).salvar(faturaCaptor.capture(), any(Connection.class));
        Fatura faturaPersistida = faturaCaptor.getValue();
        assertEquals(1L, faturaPersistida.getClienteId());
        assertEquals(194.00, faturaPersistida.getValorTotal(), 0.001);
        assertEquals(StatusFatura.PAGA, faturaPersistida.getStatus());
    }

    @ParameterizedTest(name = "Consumo: {0} usuários, {1} GB -> Valor esperado: R$ {2}")
    @CsvSource({
        "5, 0, 99.00",      // Sem excedentes e sem armazenamento
        "5, 200, 199.00",   // No limite de usuários + 200 GB de armazenamento
        "10, 0, 174.00",    // 5 usuários extras (5 * 15 = 75) + 99 base
        "12, 100, 254.00"   // 7 extras (105) + 100 GB (50) + 99 base
    })
    @DisplayName("Deve calcular valores exatos de faturamento do Plano Básico de forma parametrizada")
    void deveCalcularFaturamentoPlanoBasicoParametrizado(int usuarios, long gb, double valorEsperado) {
        Cliente cliente = new Cliente(10L, "Param Test S.A.", "financeiro@param.com", new PlanoBasico());
        TelemetriaConsumo consumo = new TelemetriaConsumo(10L, usuarios, gb);

        when(gateway.processarCobranca(eq(cliente), anyDouble())).thenReturn(true);

        Fatura fatura = faturamentoService.faturarClienteAsync(cliente, consumo).join();

        assertEquals(valorEsperado, fatura.getValorTotal(), 0.001);
    }

    @Test
    @DisplayName("Deve aplicar desconto de 10% no Plano Enterprise para clientes com mais de 50 usuários")
    void deveAplicarDescontoPlanoEnterpriseComMaisDeCinquentaUsuarios() {
        // Base (499) + 60 usuários (* 8 = 480) + 500 GB (* 0.20 = 100) = 1079 * 0.90 = R$ 971.10
        Cliente cliente = new Cliente(2L, "Big Enterprise Inc", "corp@bigenterprise.com", new PlanoEnterprise());
        TelemetriaConsumo consumo = new TelemetriaConsumo(2L, 60, 500L);

        when(gateway.processarCobranca(eq(cliente), anyDouble())).thenReturn(true);

        Fatura fatura = faturamentoService.faturarClienteAsync(cliente, consumo).join();

        assertEquals(971.10, fatura.getValorTotal(), 0.001);
        verify(gateway).processarCobranca(cliente, 971.10);
    }

    @Test
    @DisplayName("Não deve persistir fatura e deve lançar exceção quando o Gateway recusar o pagamento")
    void naoDevePersistirFaturaQuandoGatewayRecusar() throws SQLException {
        Cliente cliente = new Cliente(3L, "Inadimplente Ltda", "cobranca@inadimplente.com", new PlanoBasico());
        TelemetriaConsumo consumo = new TelemetriaConsumo(3L, 5, 0L);

        when(gateway.processarCobranca(eq(cliente), anyDouble())).thenReturn(false);

        CompletionException exception = assertThrows(
                CompletionException.class,
                () -> faturamentoService.faturarClienteAsync(cliente, consumo).join()
        );

        assertTrue(exception.getCause().getMessage().contains("Transação revertida"));
        verify(faturaDAO, never()).salvar(any(Fatura.class), any(Connection.class));
    }

    @Test
    @DisplayName("Deve disparar rollback e propagar erro quando o DAO lançar SQLException")
    void deveExecutarRollbackQuandoDaoFalhar() throws SQLException {
        Cliente cliente = new Cliente(4L, "Db Error Corp", "admin@dberror.com", new PlanoBasico());
        TelemetriaConsumo consumo = new TelemetriaConsumo(4L, 5, 0L);

        when(gateway.processarCobranca(eq(cliente), anyDouble())).thenReturn(true);
        doThrow(new SQLException("Erro forçado de chave estrangeira ou banco")).when(faturaDAO).salvar(any(), any());

        CompletionException exception = assertThrows(
                CompletionException.class,
                () -> faturamentoService.faturarClienteAsync(cliente, consumo).join()
        );

        assertTrue(exception.getCause().getMessage().contains("Transação revertida"));
        verify(gateway, times(1)).processarCobranca(eq(cliente), anyDouble());
    }
}
