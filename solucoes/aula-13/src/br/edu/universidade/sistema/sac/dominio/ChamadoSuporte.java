package br.edu.universidade.sistema.sac.dominio;

public class ChamadoSuporte {
    private String protocolo;
    private String cliente;
    private String descricao;
    private String status;

    public ChamadoSuporte(String protocolo, String cliente, String descricao) {
        if (protocolo == null || protocolo.trim().isEmpty()) {
            throw new IllegalArgumentException("Protocolo nao pode estar em branco.");
        }
        if (cliente == null || cliente.trim().isEmpty()) {
            throw new IllegalArgumentException("Cliente nao pode estar em branco.");
        }
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new IllegalArgumentException("Descricao nao pode estar em branco.");
        }
        this.protocolo = protocolo;
        this.cliente = cliente;
        this.descricao = descricao;
        this.status = "ABERTO";
    }

    public void resolverChamado() {
        this.status = "RESOLVIDO";
    }

    public String getProtocolo() { return protocolo; }
    public String getCliente() { return cliente; }
    public String getDescricao() { return descricao; }
    public String getStatus() { return status; }

    @Override
    public String toString() {
        return String.format("Chamado [Protocolo: %s | Cliente: %s | Status: %s | %s]",
                protocolo, cliente, status, descricao);
    }
}