import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class Livro {
    private Long id;
    private String titulo;
    private String autor;

    public Livro(Long id, String titulo, String autor) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
    }

    @Override
    public String toString() {
        return String.format("[%d] '%s' - %s", id, titulo, autor);
    }
}
