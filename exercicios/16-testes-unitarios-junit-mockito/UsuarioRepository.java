import java.util.Optional;

interface UsuarioRepository {
    Optional<UsuarioEntidade> buscarPorId(Long id);
    void salvar(UsuarioEntidade usuario);
}
