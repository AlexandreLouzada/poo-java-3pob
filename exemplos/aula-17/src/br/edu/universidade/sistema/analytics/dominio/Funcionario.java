package br.edu.universidade.sistema.analytics.dominio;

import java.util.Objects;

// 1. Entidade de Domínio representando o Profissional
public class Funcionario {
    private final Long id;
    private final String nome;
    private final String departamento;
    private final String cargo;
    private final double salarioMensal;

    public Funcionario(Long id, String nome, String departamento, String cargo, double salarioMensal) {
        if (id == null || salarioMensal < 0.0) {
            throw new IllegalArgumentException("Dados de identificação salarial inválidos.");
        }
        this.id = id;
        this.nome = nome.trim();
        this.departamento = departamento.trim().toUpperCase();
        this.cargo = cargo.trim().toUpperCase();
        this.salarioMensal = salarioMensal;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getDepartamento() { return departamento; }
    public String getCargo() { return cargo; }
    public double getSalarioMensal() { return salarioMensal; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Funcionario that = (Funcionario) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("[%d] %-15s | Depto: %-12s | Cargo: %-15s | R$ %8.2f",
                id, nome, departamento, cargo, salarioMensal);
    }
}
