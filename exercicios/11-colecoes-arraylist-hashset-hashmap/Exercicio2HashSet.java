import java.util.HashSet;
import java.util.Set;

public class Exercicio2HashSet {
    public static void main(String[] args) {
        Set<Integer> turmaA = new HashSet<>();
        turmaA.add(101);
        turmaA.add(102);
        turmaA.add(103);
        turmaA.add(101); // Elemento duplicado: ignorado automaticamente

        Set<Integer> turmaB = new HashSet<>();
        turmaB.add(103);
        turmaB.add(104);
        turmaB.add(105);

        System.out.println("Turma A (sem duplicatas): " + turmaA);
        System.out.println("Turma B: " + turmaB);

        // União (A ∪ B)
        Set<Integer> uniao = new HashSet<>(turmaA);
        uniao.addAll(turmaB);
        System.out.println("União (todos os alunos únicos): " + uniao);

        // Interseção (A ∩ B)
        Set<Integer> intersecao = new HashSet<>(turmaA);
        intersecao.retainAll(turmaB);
        System.out.println("Interseção (alunos em ambas as turmas): " + intersecao);
    }
}
