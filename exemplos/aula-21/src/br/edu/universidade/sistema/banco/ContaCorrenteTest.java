package br.edu.universidade.sistema.banco;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

// 2. Suite de Testes Unitários Automatizados (Localizada em src/test/java)
@DisplayName("Suite de Testes Unitários - ContaCorrente")
class ContaCorrenteTest {

    private ContaCorrente conta;

    @BeforeEach
    void setUp() {
        // Arrange comum: executado antes de CADA teste para garantir isolamento
        conta = new ContaCorrente("1001-X", 500.00);
    }

    @Test
    @DisplayName("Deve inicializar conta com saldo e estado ativo válidos")
    void deveInicializarContaComSucesso() {
        // Assert agrupado com assertAll: executa todas as checagens mesmo se uma falhar
        assertAll("Validações de estado inicial da conta",
                () -> assertEquals("1001-X", conta.getNumeroConta(), "Número de conta incorreto"),
                () -> assertEquals(500.00, conta.getSaldo(), 0.001, "Saldo inicial incorreto"),
                () -> assertTrue(conta.isAtiva(), "A conta deveria iniciar com status ativo")
        );
    }

    @Test
    @DisplayName("Deve realizar depósito com sucesso atualizando o saldo")
    void deveRealizarDepositoComSucesso() {
        // Act
        conta.depositar(150.00);

        // Assert
        assertEquals(650.00, conta.getSaldo(), 0.001, "O saldo deveria ter sido acrescido de R$ 150.00");
    }

    @Test
    @DisplayName("Deve abater valor e tarifa de R$ 2.50 em saque com saldo suficiente")
    void deveRealizarSaqueComCobrancaDeTarifa() {
        // Act: Saldo inicial 500.00 - Saque 100.00 - Tarifa 2.50 = 397.50
        conta.sacar(100.00);

        // Assert
        assertEquals(397.50, conta.getSaldo(), 0.001);
    }

    @Test
    @DisplayName("Deve disparar IllegalStateException ao tentar sacar valor que excede o saldo")
    void deveLancarExcecaoQuandoSaldoInsuficiente() {
        // Act & Assert via assertThrows
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> conta.sacar(600.00),
                "Deveria ter lançado IllegalStateException para saque maior que o saldo"
        );

        // Valida se a mensagem técnica detalha o erro
        assertTrue(ex.getMessage().contains("Saldo insuficiente"));
    }

    @ParameterizedTest(name = "Depósito inválido: valor {0} deve lançar IllegalArgumentException")
    @ValueSource(doubles = { 0.0, -10.0, -500.0 })
    @DisplayName("Deve rejeitar valores não positivos de depósito")
    void deveRejeitarDepositosInvalidos(double valorInvalido) {
        assertThrows(IllegalArgumentException.class, () -> conta.depositar(valorInvalido));
    }

    @ParameterizedTest(name = "Cenário [{index}]: Saque {0} com tarifa deve deixar saldo {1}")
    @CsvSource({
            "100.00, 397.50",
            "200.00, 297.50",
            "497.50, 0.00"
    })
    @DisplayName("Deve calcular saques múltiplos com precisão via CsvSource")
    void deveCalcularDiferentesSaques(double valorSaque, double saldoEsperado) {
        conta.sacar(valorSaque);
        assertEquals(saldoEsperado, conta.getSaldo(), 0.001);
    }

    @Test
    @DisplayName("Deve impedir encerramento de conta com saldo positivo remanescente")
    void deveRejeitarEncerramentoComSaldo() {
        assertThrows(IllegalStateException.class, () -> conta.encerrarConta());
    }

    @Test
    @DisplayName("Deve encerrar conta com sucesso quando o saldo for exatamente zero")
    void deveEncerrarContaComSaldoZerado() {
        // Esvazia a conta considerando a tarifa (500 - 2.50 = 497.50)
        conta.sacar(497.50);
        assertEquals(0.00, conta.getSaldo(), 0.001);

        conta.encerrarConta();

        assertFalse(conta.isAtiva(), "A conta deveria estar inativa após encerramento");
    }
}
