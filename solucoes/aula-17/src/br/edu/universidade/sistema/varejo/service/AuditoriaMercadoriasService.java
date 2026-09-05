package br.edu.universidade.sistema.varejo.service;

import br.edu.universidade.sistema.varejo.dominio.ItemMercadoria;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AuditoriaMercadoriasService {

    public Map<String, Double> calcularValorTotalImobilizadoPorSetor(List<ItemMercadoria> itens) {
        return itens.stream()
                .collect(Collectors.groupingBy(
                        ItemMercadoria::getSetor,
                        Collectors.summingDouble(i -> i.getQuantidade() * i.getPrecoUnitario())
                ));
    }

    public Map<Boolean, List<ItemMercadoria>> particionarPorRupturaEstoque(List<ItemMercadoria> itens) {
        return itens.stream()
                .collect(Collectors.partitioningBy(i -> i.getQuantidade() <= 5));
    }

    public String emitirCatalogoSetorialCsv(List<ItemMercadoria> itens, String setorAlvo) {
        return itens.stream()
                .filter(i -> i.getSetor().equalsIgnoreCase(setorAlvo))
                .map(i -> i.getNome().toUpperCase())
                .collect(Collectors.joining("; ", "SETOR " + setorAlvo + ": [", "]"));
    }

    public Set<String> listarSetores(List<ItemMercadoria> itens) {
        return itens.stream()
                .map(ItemMercadoria::getSetor)
                .collect(Collectors.toSet());
    }
}