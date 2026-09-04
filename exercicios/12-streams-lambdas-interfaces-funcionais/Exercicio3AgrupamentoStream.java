import java.util.stream.Stream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Exercicio3AgrupamentoStream {
    public static void main(String[] args) {
        List<AlunoStream> alunos = Arrays.asList(
                new AlunoStream("Lucas", "Sistemas de Informação", 8.5),
                new AlunoStream("Ana", "Ciência da Computação", 6.0),
                new AlunoStream("Beatriz", "Sistemas de Informação", 5.5),
                new AlunoStream("Carlos", "Engenharia de Software", 9.0),
                new AlunoStream("Mariana", "Ciência da Computação", 7.5),
                new AlunoStream("Rafael", "Engenharia de Software", 4.0)
        );

        // 1. Agrupamento por Curso
        Map<String, List<AlunoStream>> porCurso = alunos.stream()
                .collect(Collectors.groupingBy(AlunoStream::getCurso));

        System.out.println("--- Alunos por Curso ---");
        porCurso.forEach((curso, lista) -> System.out.println(curso + ": " + lista));

        // 2. Particionamento por Aprovação (nota >= 7.0)
        Map<Boolean, List<AlunoStream>> porAprovacao = alunos.stream()
                .collect(Collectors.partitioningBy(a -> a.getNotaFinal() >= 7.0));

        System.out.println("\n--- Particionamento por Situação ---");
        System.out.println("Aprovados (>= 7.0): " + porAprovacao.get(true));
        System.out.println("Reprovados (< 7.0): " + porAprovacao.get(false));
    }
}

