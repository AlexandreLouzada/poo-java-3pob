import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

class ProdutoEntidade {
    private Long id;
    private String descricao;
    private double precoUnitario;
    private int quantidadeEstoque;

    public ProdutoEntidade(String descricao, double precoUnitario, int quantidadeEstoque) {
        this.descricao = descricao;
        this.precoUnitario = precoUnitario;
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public ProdutoEntidade(Long id, String descricao, double precoUnitario, int quantidadeEstoque) {
        this.id = id;
        this.descricao = descricao;
        this.precoUnitario = precoUnitario;
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDescricao() { return descricao; }
    public double getPrecoUnitario() { return precoUnitario; }
    public int getQuantidadeEstoque() { return quantidadeEstoque; }
    public void setPrecoUnitario(double precoUnitario) { this.precoUnitario = precoUnitario; }

    @Override
    public String toString() {
        return String.format("Produto [ID=%d, Descricao='%s', Preco=R$ %.2f, Estoque=%d]",
                id, descricao, precoUnitario, quantidadeEstoque);
    }
}
