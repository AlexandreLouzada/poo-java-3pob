
// Classe de Teste
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContaBancariaTest {

    private ContaBancariaTeste conta;

    @BeforeEach
    void setUp() {
        conta = new ContaBancariaTeste(500.0);
    }

    @Test
    @DisplayName("Deve efetuar saque e decrementar saldo quando houver fundos suficientes")
    void deveSacarComSucesso() {
        conta.sacar(200.0);
        assertEquals(300.0, conta.getSaldo(), 0.001);
    }

    @Test
    @DisplayName("Deve disparar SaldoInsuficienteException ao tentar sacar valor acima do saldo")
    void deveLancarExcecaoQuandoSaldoForInsuficiente() {
        SaldoInsuficienteException ex = assertThrows(
                SaldoInsuficienteException.class,
                () -> conta.sacar(600.0)
        );
        assertEquals("Saldo insuficiente. Saldo disponível: 500.0", ex.getMessage());
    }

    @Test
    @DisplayName("Deve disparar IllegalArgumentException ao tentar sacar valor negativo ou zero")
    void deveLancarExcecaoQuandoValorForInvalido() {
        assertThrows(IllegalArgumentException.class, () -> conta.sacar(-10.0));
        assertThrows(IllegalArgumentException.class, () -> conta.sacar(0.0));
    }
}
