// Código de Produção
import java.util.Optional;

class UsuarioEntidade {
    private Long id;
    private String email;

    public UsuarioEntidade(Long id, String email) {
        this.id = id;
        this.email = email;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
