package br.edu.universidade.sistema.catalogo.dominio;

import java.util.Objects;

public class Produto implements Comparable<Produto> {
    private String codigo;
    private String descricao;
    private double preco;
    private int quantidade;

    public Produto(String codigo, String descricao, double preco, int quantidade) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("Codigo do produto nao pode ser vazio.");
        }
        if (preco < 0.0) {
            throw new IllegalArgumentException("Preco do produto nao pode ser negativo: " + preco);
        }
        if (quantidade < 0) {
            throw new IllegalArgumentException("Quantidade do produto nao pode ser negativa: " + quantidade);
        }
        this.codigo = codigo;
        this.descricao = descricao;
        this.preco = preco;
        this.quantidade = quantidade;
    }

    @Override
    public int compareTo(Produto outro) {
        return Double.compare(this.preco, outro.preco);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Produto)) {
            return false;
        }
        Produto outro = (Produto) o;
        return Objects.equals(this.codigo, outro.codigo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.codigo);
    }

    @Override
    public String toString() {
        return String.format("Produto [codigo: %s | descricao: %s | preco: R$ %.2f | qtd: %d]",
                codigo, descricao, preco, quantidade);
    }

    public String getCodigo() { return codigo; }
    public String getDescricao() { return descricao; }
    public double getPreco() { return preco; }
    public int getQuantidade() { return quantidade; }
}