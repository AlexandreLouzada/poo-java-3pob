package br.edu.universidade.sistema.vendas.service;

import br.edu.universidade.sistema.vendas.dominio.VendaRepresentante;
import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.OptionalDouble;

public class AuditoriaVendasService {

    public double calcularFaturamentoTotalPorRegiao(List<VendaRepresentante> vendas, String regiao) {
        return vendas.stream()
                .filter(v -> v.getRegiao().equalsIgnoreCase(regiao))
                .mapToDouble(VendaRepresentante::getValorVenda)
                .sum();
    }

    public OptionalDouble calcularMediaComissoesPagas(List<VendaRepresentante> vendas) {
        return vendas.stream()
                .mapToDouble(VendaRepresentante::getComissaoPaga)
                .average();
    }

    public Optional<VendaRepresentante> buscarMaiorVendaPorRepresentante(
            List<VendaRepresentante> vendas, String nome) {
        return vendas.stream()
                .filter(v -> v.getNomeRepresentante().equalsIgnoreCase(nome))
                .max(Comparator.comparingDouble(VendaRepresentante::getValorVenda));
    }

    public VendaRepresentante obterVendaComGarantia(List<VendaRepresentante> vendas, Long id) {
        return vendas.stream()
                .filter(v -> v.getIdVenda().equals(id))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Venda nao localizada para auditoria: ID " + id));
    }
}