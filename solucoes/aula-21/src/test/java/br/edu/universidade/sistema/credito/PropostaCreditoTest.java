package br.edu.universidade.sistema.credito;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Suite de Testes Unitarios - PropostaCredito")
class PropostaCreditoTest {

    private PropostaCredito propostaPadrao;

    @BeforeEach
    void setUp() {
        propostaPadrao = new PropostaCredito("111.222.333-04", 5000.00, 10000.00, 24);
    }

    @Test
    @DisplayName("Deve aprovar proposta elegivel (parcela <= 30% da renda)")
    void deveAprovarPropostaElegivel() {
        assertTrue(propostaPadrao.isAprovada(),
                "Proposta com parcela de R$ 437,50 contra renda de R$ 5.000 deveria ser aprovada");
    }

    @Test
    @DisplayName("Deve recusar proposta cuja parcela excede 30% da renda")
    void deveRecusarPropostaComComprometimentoExcessivo() {
        PropostaCredito pesada = new PropostaCredito("444.555.666-07", 3000.00, 100000.00, 36);
        assertFalse(pesada.isAprovada(),
                "Parcela acima de 30% da renda deve tornar a proposta inelegivel");
    }

    @Test
    @DisplayName("Deve lancar IllegalArgumentException para renda mensal menor ou igual a zero")
    void deveLancarExcecaoParaRendaInvalida() {
        assertThrows(IllegalArgumentException.class,
                () -> new PropostaCredito("111.222.333-04", 0.0, 10000.00, 24));
        assertThrows(IllegalArgumentException.class,
                () -> new PropostaCredito("111.222.333-04", -500.0, 10000.00, 24));
    }

    @ParameterizedTest(name = "Parcela de {0} em {1} meses deve ser {2}")
    @CsvSource({
            "12000.00, 24, 525.00",
            "6000.00, 12, 525.00",
            "10000.00, 10, 1050.00"
    })
    @DisplayName("Deve calcular a parcela mensal com 5% de encargos (delta 0.01)")
    void deveCalcularParcelaMensal(double valorSolicitado, int meses, double parcelaEsperada) {
        PropostaCredito proposta = new PropostaCredito("123.456.789-00", 8000.00, valorSolicitado, meses);
        double parcela = proposta.calcularValorParcelaMensal();
        assertEquals(parcelaEsperada, parcela, 0.01);
    }
}