package br.edu.universidade.sistema.cadastro;

public class RegistroCliente {
    private Long id;
    private String nome;
    private int idade;
    private double limiteCredito;

    public RegistroCliente(Long id, String nome, int idade, double limiteCredito) {
        if (id == null) {
            throw new IllegalArgumentException("ID do cliente nao pode ser nulo.");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do cliente nao pode ser vazio.");
        }
        if (idade < 0) {
            throw new IllegalArgumentException("Idade do cliente nao pode ser negativa: " + idade);
        }
        if (limiteCredito < 0.0) {
            throw new IllegalArgumentException("Limite de credito nao pode ser negativo: " + limiteCredito);
        }
        this.id = id;
        this.nome = nome;
        this.idade = idade;
        this.limiteCredito = limiteCredito;
    }

    public void exibirDados() {
        System.out.printf("Cliente [ID: %d | Nome: %s | Idade: %d | Limite: R$ %.2f]%n",
                id, nome, idade, limiteCredito);
    }
}