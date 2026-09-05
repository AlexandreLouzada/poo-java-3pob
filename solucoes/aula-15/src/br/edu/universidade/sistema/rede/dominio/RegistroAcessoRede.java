package br.edu.universidade.sistema.rede.dominio;

public class RegistroAcessoRede {
    private Long idConexao;
    private String ipOrigem;
    private int portaDestino;
    private double megabytesTransferidos;
    private boolean bloqueadoFirewall;

    public RegistroAcessoRede(Long idConexao, String ipOrigem, int portaDestino,
                              double megabytesTransferidos, boolean bloqueadoFirewall) {
        if (idConexao == null) {
            throw new IllegalArgumentException("ID da conexao nao pode ser nulo.");
        }
        if (ipOrigem == null) {
            throw new IllegalArgumentException("Endereco IP de origem nao pode ser nulo.");
        }
        this.idConexao = idConexao;
        this.ipOrigem = ipOrigem;
        this.portaDestino = portaDestino;
        this.megabytesTransferidos = megabytesTransferidos;
        this.bloqueadoFirewall = bloqueadoFirewall;
    }

    public Long getIdConexao() { return idConexao; }
    public String getIpOrigem() { return ipOrigem; }
    public int getPortaDestino() { return portaDestino; }
    public double getMegabytesTransferidos() { return megabytesTransferidos; }
    public boolean isBloqueadoFirewall() { return bloqueadoFirewall; }

    @Override
    public String toString() {
        return String.format("Conexao [ID: %d | IP: %s | Porta: %d | Volume: %.2f MB | Bloqueada: %s]",
                idConexao, ipOrigem, portaDestino, megabytesTransferidos, bloqueadoFirewall);
    }
}