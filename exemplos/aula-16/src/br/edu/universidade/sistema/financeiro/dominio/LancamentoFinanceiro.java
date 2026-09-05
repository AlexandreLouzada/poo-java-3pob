package br.edu.universidade.sistema.financeiro.dominio;

import java.time.LocalDate;
import java.util.Objects;

// 1. Entidade de Domínio representando um Lançamento Contábil
public class LancamentoFinanceiro {
    private final Long id;
    private final String descricao;
    private final String centroCusto;
    private final double valor;
    private final LocalDate dataLancamento;

    public LancamentoFinanceiro(Long id, String descricao, String centroCusto, double valor, LocalDate data) {
        if (id == null || valor <= 0.0) {
            throw new IllegalArgumentException("Parâmetros do lançamento inválidos.");
        }
        this.id = id;
        this.descricao = descricao;
        this.centroCusto = centroCusto.toUpperCase();
        this.valor = valor;
        this.dataLancamento = data;
    }

    public Long getId() { return id; }
    public String getDescricao() { return descricao; }
    public String getCentroCusto() { return centroCusto; }
    public double getValor() { return valor; }
    public LocalDate getDataLancamento() { return dataLancamento; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LancamentoFinanceiro that = (LancamentoFinanceiro) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("[#%04d] %-20s | C. Custo: %-12s | Valor: R$ %10.2f | Data: %s",
                id, descricao, centroCusto, valor, dataLancamento);
    }
}
