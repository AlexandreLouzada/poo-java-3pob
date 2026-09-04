class Caixa<T> {
    private T conteudo;

    public void guardar(T elemento) {
        this.conteudo = elemento;
    }

    public T recuperar() {
        return this.conteudo;
    }

    public boolean isVazia() {
        return this.conteudo == null;
    }

    public void limpar() {
        this.conteudo = null;
    }
}
