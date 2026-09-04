
// Classe de Teste
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class ValidadorSenhaTest {

    private final ValidadorSenha validador = new ValidadorSenha();

    @ParameterizedTest
    @ValueSource(strings = {"SenhaForte1", "Admin2026!", "JavaMaster8"})
    @DisplayName("Deve validar com sucesso senhas que cumprem todos os requisitos")
    void deveAceitarSenhasFortes(String senha) {
        assertTrue(validador.isForte(senha));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"curta1", "semnumeroA", "12345678", "tudo minusculo 1"})
    @DisplayName("Deve recusar senhas nulas, vazias ou fora do padrão mínimo")
    void deveRejeitarSenhasFracas(String senha) {
        assertFalse(validador.isForte(senha));
    }
}
