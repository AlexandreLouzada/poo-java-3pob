import java.util.Optional;

// Classe de Teste
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private UsuarioService service;

    @Test
    void deveAtualizarEmailESalvarUsuario() {
        UsuarioEntidade usuarioExistente = new UsuarioEntidade(1L, "antigo@teste.com");
        when(repository.buscarPorId(1L)).thenReturn(Optional.of(usuarioExistente));

        service.atualizarEmail(1L, "novo@teste.com");

        assertEquals("novo@teste.com", usuarioExistente.getEmail());
        verify(repository, times(1)).buscarPorId(1L);
        verify(repository, times(1)).salvar(any(UsuarioEntidade.class));
    }
}
