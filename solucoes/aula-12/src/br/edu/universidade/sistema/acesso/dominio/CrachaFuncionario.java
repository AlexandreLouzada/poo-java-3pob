package br.edu.universidade.sistema.acesso.dominio;

import java.util.Objects;

public class CrachaFuncionario implements Comparable<CrachaFuncionario> {
    private String codigoCartao;
    private String nome;
    private String departamento;

    public CrachaFuncionario(String codigoCartao, String nome, String departamento) {
        if (codigoCartao == null || codigoCartao.trim().isEmpty()) {
            throw new IllegalArgumentException("Codigo do cartao nao pode ser em branco.");
        }
        if (nome == null || nome.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do funcionario nao pode ser vazio.");
        }
        if (departamento == null || departamento.trim().isEmpty()) {
            throw new IllegalArgumentException("Departamento nao pode ser vazio.");
        }
        this.codigoCartao = codigoCartao;
        this.nome = nome;
        this.departamento = departamento;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CrachaFuncionario)) {
            return false;
        }
        CrachaFuncionario outro = (CrachaFuncionario) o;
        return Objects.equals(this.codigoCartao, outro.codigoCartao);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.codigoCartao);
    }

    @Override
    public int compareTo(CrachaFuncionario outro) {
        int porDepartamento = this.departamento.compareTo(outro.departamento);
        if (porDepartamento != 0) {
            return porDepartamento;
        }
        return this.nome.compareTo(outro.nome);
    }

    @Override
    public String toString() {
        return String.format("Cracha [Cartao: %s | Nome: %s | Depto: %s]",
                codigoCartao, nome, departamento);
    }

    public String getCodigoCartao() { return codigoCartao; }
    public String getNome() { return nome; }
    public String getDepartamento() { return departamento; }
}