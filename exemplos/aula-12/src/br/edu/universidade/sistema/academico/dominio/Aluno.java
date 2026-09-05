package br.edu.universidade.sistema.academico.dominio;

import java.util.Objects;

// 1. Entidade de Domínio respeitando o contrato de integridade do Set
public class Aluno implements Comparable<Aluno> {
    private final String matricula;
    private final String cpf;
    private String nome;

    public Aluno(String matricula, String cpf, String nome) {
        if (matricula == null || cpf == null || nome == null) {
            throw new IllegalArgumentException("Campos de identificação não podem ser nulos.");
        }
        this.matricula = matricula.trim();
        this.cpf = cpf.replaceAll("\\D", ""); // Sanitização básica
        this.nome = nome.trim();
    }

    public String getMatricula() {
        return matricula;
    }

    public String getCpf() {
        return cpf;
    }

    public String getNome() {
        return nome;
    }

    // Regra de igualdade: Dois alunos são iguais se possuírem o mesmo CPF
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Aluno outro = (Aluno) obj;
        return Objects.equals(this.cpf, outro.cpf);
    }

    // Regra de espalhamento: Hash calculado exclusivamente sobre o mesmo atributo do equals
    @Override
    public int hashCode() {
        return Objects.hash(this.cpf);
    }

    // Ordenação natural de suporte a TreeSet (por nome alfabético)
    @Override
    public int compareTo(Aluno outro) {
        return this.nome.compareToIgnoreCase(outro.nome);
    }

    @Override
    public String toString() {
        return String.format("Aluno [Matrícula: %s | CPF: %s | Nome: %s]", matricula, cpf, nome);
    }
}
