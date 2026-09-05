package br.edu.universidade.sistema.rh.dominio;

// 1. Entidade de Domínio Funcional
public class Colaborador {
    private final Long id;
    private final String nome;
    private final String departamento;
    private double salarioBase;
    private final int tempoServicoAnos;

    public Colaborador(Long id, String nome, String departamento, double salarioBase, int tempoServicoAnos) {
        this.id = id;
        this.nome = nome;
        this.departamento = departamento;
        this.salarioBase = salarioBase;
        this.tempoServicoAnos = tempoServicoAnos;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getDepartamento() { return departamento; }
    public double getSalarioBase() { return salarioBase; }
    public int getTempoServicoAnos() { return tempoServicoAnos; }

    public void aplicarAumento(double valorAdicional) {
        if (valorAdicional > 0) {
            this.salarioBase += valorAdicional;
        }
    }

    @Override
    public String toString() {
        return String.format("[%d] %-15s | Depto: %-10s | Salário: R$ %8.2f | Anos: %d",
                id, nome, departamento, salarioBase, tempoServicoAnos);
    }
}
