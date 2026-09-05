package br.edu.universidade.sistema.varejo.dominio;

public class ItemMercadoria {
    private String sku;
    private String nome;
    private String setor;
    private int quantidade;
    private double precoUnitario;

    public ItemMercadoria(String sku, String nome, String setor, int quantidade, double precoUnitario) {
        if (sku == null || sku.trim().isEmpty()) {
            throw new IllegalArgumentException("SKU nao pode ser vazio.");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do item nao pode ser vazio.");
        }
        if (setor == null || setor.trim().isEmpty()) {
            throw new IllegalArgumentException("Setor nao pode ser vazio.");
        }
        if (quantidade < 0) {
            throw new IllegalArgumentException("Quantidade nao pode ser negativa: " + quantidade);
        }
        if (precoUnitario <= 0.0) {
            throw new IllegalArgumentException("Preco unitario deve ser positivo: " + precoUnitario);
        }
        this.sku = sku;
        this.nome = nome;
        this.setor = setor;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    public String getSku() { return sku; }
    public String getNome() { return nome; }
    public String getSetor() { return setor; }
    public int getQuantidade() { return quantidade; }
    public double getPrecoUnitario() { return precoUnitario; }

    @Override
    public String toString() {
        return String.format("Item [SKU: %s | Nome: %s | Setor: %s | Qtd: %d | Preco: R$ %.2f]",
                sku, nome, setor, quantidade, precoUnitario);
    }
}