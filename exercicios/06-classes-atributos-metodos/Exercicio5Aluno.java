import java.util.Locale;

public class Exercicio5Aluno {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        Aluno aluno1 = new Aluno();
        aluno1.nome = "Carlos";
        aluno1.matricula = "2026-A";
        aluno1.nota1 = 8.0;
        aluno1.nota2 = 5.0;

        aluno1.imprimirBoletim();
    }
}
