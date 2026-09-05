package br.edu.universidade.sistema.patrimonio.dominio;

public class AtivoPatrimonial {
    private Long id;
    private String tombo;
    private String descricao;
    private double valorAquisicao;
    private String status;

    public AtivoPatrimonial(String tombo, String descricao, double valorAquisicao, String status) {
        this(null, tombo, descricao, valorAquisicao, status);
    }

    public AtivoPatrimonial(Long id, String tombo, String descricao, double valorAquisicao, String status) {
        if (tombo == null || tombo.trim().isEmpty()) {
            throw new IllegalArgumentException("Tombo nao pode estar em branco.");
        }
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new IllegalArgumentException("Descricao nao pode estar em branco.");
        }
        if (valorAquisicao < 0.0) {
            throw new IllegalArgumentException("Valor de aquisicao nao pode ser negativo: " + valorAquisicao);
        }
        if (status == null || (!status.equals("ATIVO") && !status.equals("BAIXADO"))) {
            throw new IllegalArgumentException("Status deve ser ATIVO ou BAIXADO: " + status);
        }
        this.id = id;
        this.tombo = tombo;
        this.descricao = descricao;
        this.valorAquisicao = valorAquisicao;
        this.status = status;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTombo() { return tombo; }
    public void setTombo(String tombo) { this.tombo = tombo; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public double getValorAquisicao() { return valorAquisicao; }
    public void setValorAquisicao(double valorAquisicao) { this.valorAquisicao = valorAquisicao; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return String.format("Ativo [ID: %s | Tombo: %s | Descricao: %s | Valor: R$ %.2f | Status: %s]",
                (id == null ? "-" : id), tombo, descricao, valorAquisicao, status);
    }
}