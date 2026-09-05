package br.edu.universidade.sistema.ecommerce.dominio;

import java.util.Objects;

// 1. Entidade de Domínio representando o Pedido
public class PedidoCompra {
    private final Long id;
    private final String cliente;
    private final String categoria;
    private final double valorTotal;
    private final boolean pago;

    public PedidoCompra(Long id, String cliente, String categoria, double valorTotal, boolean pago) {
        this.id = id;
        this.cliente = cliente;
        this.categoria = categoria;
        this.valorTotal = valorTotal;
        this.pago = pago;
    }

    public Long getId() { return id; }
    public String getCliente() { return cliente; }
    public String getCategoria() { return categoria; }
    public double getValorTotal() { return valorTotal; }
    public boolean isPago() { return pago; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PedidoCompra that = (PedidoCompra) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Pedido #%03d | Cliente: %-15s | Categoria: %-10s | Total: R$ %8.2f | Pago: %s",
                id, cliente, categoria, valorTotal, pago ? "SIM" : "NÃO");
    }
}
