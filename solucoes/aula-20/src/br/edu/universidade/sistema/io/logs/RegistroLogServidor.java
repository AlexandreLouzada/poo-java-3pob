package br.edu.universidade.sistema.io.logs;

public class RegistroLogServidor {
    private String ipOrigem;
    private String nivelSeveridade;
    private String mensagem;
    private String timestamp;

    public RegistroLogServidor(String timestamp, String nivelSeveridade, String ipOrigem, String mensagem) {
        if (timestamp == null || timestamp.trim().isEmpty()) {
            throw new IllegalArgumentException("Timestamp nao pode estar em branco.");
        }
        if (nivelSeveridade == null || nivelSeveridade.trim().isEmpty()) {
            throw new IllegalArgumentException("Nivel de severidade nao pode estar em branco.");
        }
        if (ipOrigem == null || ipOrigem.trim().isEmpty()) {
            throw new IllegalArgumentException("IP de origem nao pode estar em branco.");
        }
        if (mensagem == null || mensagem.trim().isEmpty()) {
            throw new IllegalArgumentException("Mensagem do log nao pode estar em branco.");
        }
        this.timestamp = timestamp;
        this.nivelSeveridade = nivelSeveridade;
        this.ipOrigem = ipOrigem;
        this.mensagem = mensagem;
    }

    public static RegistroLogServidor fromLogLine(String linha) {
        if (linha == null || linha.trim().isEmpty()) {
            throw new IllegalArgumentException("Linha de log nao pode estar em branco.");
        }
        int aberturaColchete = linha.indexOf('[');
        int fechamentoColchete = linha.indexOf(']', aberturaColchete);
        if (aberturaColchete < 0 || fechamentoColchete < 0) {
            throw new IllegalArgumentException("Formato de log invalido (colchetes ausentes): " + linha);
        }
        String timestamp = linha.substring(0, aberturaColchete).trim();
        String nivel = linha.substring(aberturaColchete + 1, fechamentoColchete).trim();
        String restante = linha.substring(fechamentoColchete + 1).trim();

        int separador = restante.indexOf(" - ");
        if (separador < 0) {
            throw new IllegalArgumentException("Formato de log invalido (separador ' - ' ausente): " + linha);
        }
        String ip = restante.substring(0, separador).trim();
        String mensagem = restante.substring(separador + 3).trim();
        return new RegistroLogServidor(timestamp, nivel, ip, mensagem);
    }

    public String toSanitizedLine() {
        String ipSanitizado = mascararIp(ipOrigem);
        return timestamp + " [" + nivelSeveridade + "] " + ipSanitizado + " - " + mensagem;
    }

    private String mascararIp(String ip) {
        String[] octetos = ip.split("\\.");
        if (octetos.length < 2) {
            return "*.*";
        }
        for (int i = 0; i < octetos.length; i++) {
            octetos[i] = (i == octetos.length - 1 || i == octetos.length - 2) ? "*" : octetos[i];
        }
        return String.join(".", octetos);
    }

    public String getIpOrigem() { return ipOrigem; }
    public String getNivelSeveridade() { return nivelSeveridade; }
    public String getMensagem() { return mensagem; }
    public String getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return toSanitizedLine();
    }
}