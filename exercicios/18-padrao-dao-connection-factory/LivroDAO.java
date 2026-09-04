import java.sql.*;
import java.util.ArrayList;
import java.util.List;

interface LivroDAO {
    List<Livro> buscarPaginado(int pagina, int tamanhoPagina);
}
