
class Eleitor {
    private String nome;
    private int idade;

    public Eleitor(String nome, int idade) {
        cadastrar(nome, idade);
    }

    public void cadastrar(String nome, int idade) {
        if (idade < 0 || idade > 130) {
            throw new IdadeInvalidaException(
                "Idade inválida (" + idade + "). O valor permitido é entre 0 e 130 anos."
            );
        }
        this.nome = nome;
        this.idade = idade;
        System.out.println("Eleitor " + this.nome + " cadastrado com sucesso com " + this.idade + " anos.");
    }
}
