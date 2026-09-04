
class Administrador extends Usuario implements ExportavelJSON {
    private int nivelAcesso;

    public Administrador(String login, String senha, int nivelAcesso) {
        super(login, senha);
        this.nivelAcesso = nivelAcesso;
    }

    @Override
    public String exportarJSON() {
        return String.format("{\"login\": \"%s\", \"nivel\": %d}", login, nivelAcesso);
    }
}
