package br.edu.universidade.sistema.academico.service;

import br.edu.universidade.sistema.academico.dominio.Aluno;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

// 2. Serviço com Operações de Conjuntos (União, Interseção e Diferença)
public class GestaoTurmasService {

    // Operação de União: Junta os alunos de duas turmas sem duplicidade
    public Set<Aluno> consolidarTodosAlunos(Set<Aluno> turmaA, Set<Aluno> turmaB) {
        Set<Aluno> uniao = new HashSet<>(turmaA);
        uniao.addAll(turmaB); // Operação Matemática: A ∪ B
        return uniao;
    }

    // Operação de Interseção: Alunos matriculados simultaneamente em ambas
    public Set<Aluno> listarAlunosEmComum(Set<Aluno> turmaA, Set<Aluno> turmaB) {
        Set<Aluno> intersecao = new HashSet<>(turmaA);
        intersecao.retainAll(turmaB); // Operação Matemática: A ∩ B
        return intersecao;
    }

    // Operação de Diferença: Alunos exclusivos da turma A
    public Set<Aluno> listarAlunosExclusivos(Set<Aluno> turmaA, Set<Aluno> turmaB) {
        Set<Aluno> diferenca = new HashSet<>(turmaA);
        diferenca.removeAll(turmaB); // Operação Matemática: A - B
        return diferenca;
    }

    // Retorna os alunos ordenados alfabeticamente usando TreeSet
    public Set<Aluno> ordenarAlunos(Set<Aluno> alunos) {
        return new TreeSet<>(alunos); // Delega para o compareTo da classe Aluno
    }
}
