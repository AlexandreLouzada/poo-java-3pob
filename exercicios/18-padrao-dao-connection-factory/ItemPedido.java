import java.sql.*;
import java.util.List;

class ItemPedido {
    private String produto;
    private int quantidade;
    private double preco;

    public ItemPedido(String produto, int quantidade, double preco) {
        this.produto = produto;
        this.quantidade = quantidade;
        this.preco = preco;
    }

    public String getProduto() { return produto; }
    public int getQuantidade() { return quantidade; }
    public double getPreco() { return preco; }
}
