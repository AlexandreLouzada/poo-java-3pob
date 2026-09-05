package br.edu.universidade.sistema.academico;

import br.edu.universidade.sistema.academico.dominio.Aluno;
import br.edu.universidade.sistema.academico.service.GestaoTurmasService;
import java.util.HashSet;
import java.util.Set;

// 3. Aplicação Executável demonstrando a barreira de unicidade e as operações
public class AcademicoSetApp {
    public static void main(String[] args) {
        GestaoTurmasService service = new GestaoTurmasService();

        // Criando turmas utilizando a interface Set declarativa
        Set<Aluno> turmaPOO = new HashSet<>();
        Set<Aluno> turmaBancoDados = new HashSet<>();

        Aluno a1 = new Aluno("M01", "111.222.333-01", "Beatriz Costa");
        Aluno a2 = new Aluno("M02", "111.222.333-02", "Carlos Eduardo");
        Aluno a3 = new Aluno("M03", "111.222.333-03", "Ana Clara");

        // Aluno duplicado com matrículas diferentes mas MESMO CPF de Beatriz
        Aluno a1Duplicado = new Aluno("M99", "111.222.333-01", "Beatriz Outra Matricula");

        turmaPOO.add(a1);
        turmaPOO.add(a2);
        boolean inseriuDuplicado = turmaPOO.add(a1Duplicado); // Rejeitado!

        System.out.println("--- 1. Teste de Unicidade no Set ---");
        System.out.println("Tentativa de inserir CPF duplicado teve sucesso? " + inseriuDuplicado);
        System.out.printf("Total de inscritos em POO: %d (Duplicata barrada)%n", turmaPOO.size());

        // Montando a segunda turma
        turmaBancoDados.add(a2); // Carlos também faz Banco de Dados
        turmaBancoDados.add(a3); // Ana Clara só faz Banco de Dados

        System.out.println("\n--- 2. Operação de Interseção (Alunos em Ambas as Disciplinas) ---");
        Set<Aluno> emComum = service.listarAlunosEmComum(turmaPOO, turmaBancoDados);
        emComum.forEach(System.out::println);

        System.out.println("\n--- 3. Operação de Diferença (Alunos Exclusivos de POO) ---");
        Set<Aluno> exclusivosPOO = service.listarAlunosExclusivos(turmaPOO, turmaBancoDados);
        exclusivosPOO.forEach(System.out::println);

        System.out.println("\n--- 4. Operação de União com Ordenação via TreeSet ---");
        Set<Aluno> todos = service.consolidarTodosAlunos(turmaPOO, turmaBancoDados);
        Set<Aluno> todosOrdenados = service.ordenarAlunos(todos);
        todosOrdenados.forEach(System.out::println);
    }
}
