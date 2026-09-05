package br.edu.universidade.sistema.generics.auditoria;

public class Produto {
    private String nome;
    private double preco;

    public Produto(String nome, double preco) {
        this.nome = nome;
        this.preco = preco;
    }

    public String getNome() { return nome; }
    public double getPreco() { return preco; }

    @Override
    public String toString() {
        return String.format("Produto[nome=%s, preco=%.2f]", nome, preco);
    }
}