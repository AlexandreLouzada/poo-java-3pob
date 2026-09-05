package br.edu.universidade.sistema.generics.dominio;

// 3. Entidade de Domínio Concreta 2: Cliente
public class Cliente extends EntidadeBase<String> {
    private final String nome;

    public Cliente(String cpf, String nome) {
        super(cpf); // ID é a String do CPF
        this.nome = nome;
    }

    public String getNome() { return nome; }

    @Override
    public String toString() {
        return String.format("Cliente [CPF: %s | Nome: %s]", getId(), nome);
    }
}
