
// Classe de Teste
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessadorPagamentoServiceTest {

    @Mock
    private GatewayPagamento gateway;

    @Mock
    private PedidoRepository repository;

    @InjectMocks
    private ProcessadorPagamentoService service;

    @Test
    void deveProcessarPagamentoEAtualizarPedidoQuandoGatewayAprova() {
        PedidoEntidade pedido = new PedidoEntidade(350.0);

        service.processar(pedido);

        assertEquals(StatusPedido.PAGO, pedido.getStatus());
        verify(gateway, times(1)).cobrar(350.0);
        verify(repository, times(1)).atualizar(pedido);
    }

    @Test
    void naoDeveAtualizarPedidoQuandoGatewayRecusaPagamento() {
        PedidoEntidade pedido = new PedidoEntidade(350.0);
        doThrow(new PagamentoRecusadoException("Cartão negado")).when(gateway).cobrar(anyDouble());

        assertThrows(PagamentoRecusadoException.class, () -> service.processar(pedido));

        assertEquals(StatusPedido.PENDENTE, pedido.getStatus());
        verify(repository, never()).atualizar(any(PedidoEntidade.class));
    }
}
