import java.sql.*;
import java.util.List;

class Pedido {
    private String cliente;
    private List<ItemPedido> itens;

    public Pedido(String cliente, List<ItemPedido> itens) {
        this.cliente = cliente;
        this.itens = itens;
    }

    public String getCliente() { return cliente; }
    public List<ItemPedido> getItens() { return itens; }
}
