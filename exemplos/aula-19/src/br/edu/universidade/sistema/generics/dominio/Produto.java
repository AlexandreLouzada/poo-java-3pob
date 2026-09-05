package br.edu.universidade.sistema.generics.dominio;

// 2. Entidade de Domínio Concreta 1: Produto
public class Produto extends EntidadeBase<Long> {
    private final String descricao;
    private final double preco;

    public Produto(Long id, String descricao, double preco) {
        super(id);
        this.descricao = descricao;
        this.preco = preco;
    }

    public String getDescricao() { return descricao; }
    public double getPreco() { return preco; }

    @Override
    public String toString() {
        return String.format("Produto [#%d | %s | R$ %.2f]", getId(), descricao, preco);
    }
}
