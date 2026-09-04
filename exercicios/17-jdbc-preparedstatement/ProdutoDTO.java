import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class ProdutoDTO {
    private final Long id;
    private final String nome;
    private final Double preco;
    private final Integer estoque;

    public ProdutoDTO(Long id, String nome, Double preco, Integer estoque) {
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.estoque = estoque;
    }

    @Override
    public String toString() {
        return String.format("ID: %d | Produto: %-15s | Preço: R$ %7.2f | Estoque: %d", id, nome, preco, estoque);
    }
}
