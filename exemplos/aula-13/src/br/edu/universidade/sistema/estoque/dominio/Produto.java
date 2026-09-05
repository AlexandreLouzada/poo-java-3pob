package br.edu.universidade.sistema.estoque.dominio;

import java.util.Objects;

// 1. Entidade de Domínio representando um Produto em Estoque
public class Produto {
    private final String sku; // Stock Keeping Unit (Chave de Negócio)
    private final String nome;
    private final String categoria;
    private final double precoUnitario;

    public Produto(String sku, String nome, String categoria, double precoUnitario) {
        if (sku == null || sku.isBlank() || precoUnitario < 0.0) {
            throw new IllegalArgumentException("Dados de produto inválidos para catalogação.");
        }
        this.sku = sku.trim().toUpperCase();
        this.nome = nome.trim();
        this.categoria = categoria.trim().toUpperCase();
        this.precoUnitario = precoUnitario;
    }

    public String getSku() { return sku; }
    public String getNome() { return nome; }
    public String getCategoria() { return categoria; }
    public double getPrecoUnitario() { return precoUnitario; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Produto produto = (Produto) o;
        return Objects.equals(sku, produto.sku);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sku);
    }

    @Override
    public String toString() {
        return String.format("[%s] %-20s (Cat: %-10s) | R$ %8.2f",
                sku, nome, categoria, precoUnitario);
    }
}
