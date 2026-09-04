
public class Exercicio1Livro {
    public static void main(String[] args) {
        Livro livro1 = new Livro();
        livro1.titulo = "O Programador Pragmático";
        livro1.autor = "Andy Hunt";
        livro1.numeroPaginas = 352;

        Livro livro2 = new Livro();
        livro2.titulo = "Código Limpo";
        livro2.autor = "Robert C. Martin";
        livro2.numeroPaginas = 425;

        livro1.exibirInformacoes();
        livro2.exibirInformacoes();
    }
}
