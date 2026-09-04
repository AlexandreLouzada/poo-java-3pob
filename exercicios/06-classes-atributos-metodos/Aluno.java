import java.util.Locale;

class Aluno {
    String nome;
    String matricula;
    double nota1;
    double nota2;

    double calcularMedia() {
        return (nota1 + nota2) / 2.0;
    }

    String verificarAprovacao() {
        return (calcularMedia() >= 7.0) ? "Aprovado" : "Reprovado";
    }

    void imprimirBoletim() {
        double mediaFinal = calcularMedia();
        String situacao = verificarAprovacao();

        System.out.printf("Aluno: %s | Matrícula: %s | Média: %.2f | Situação: %s%n",
                nome, matricula, mediaFinal, situacao);
    }
}
