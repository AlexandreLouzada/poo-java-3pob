public class ItemEstoque {
    private String codigo;
    private String nome;
    private int quantidade;
    private double valorUnitario;

    public ItemEstoque(String codigo, String nome, int quantidadeInicial, double valorUnitario) {
        this.codigo = codigo;
        this.nome = nome;
        this.quantidade = (quantidadeInicial < 0) ? 0 : quantidadeInicial;
        this.valorUnitario = (valorUnitario > 0.0) ? valorUnitario : 0.0;
    }

    public boolean adicionarEstoque(int qtd) {
        if (qtd > 0) {
            this.quantidade += qtd;
            return true;
        }
        return false;
    }

    public boolean removerEstoque(int qtd) {
        if (qtd > 0 && this.quantidade >= qtd) {
            this.quantidade -= qtd;
            return true;
        }
        return false;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public double getValorUnitario() {
        return valorUnitario;
    }

    public void exibirFicha() {
        System.out.printf("Item: %-6s | Nome: %-20s | Qtd: %4d | Vr.Unit: R$ %8.2f | Total: R$ %10.2f%n",
                codigo, nome, quantidade, valorUnitario, (quantidade * valorUnitario));
    }
}