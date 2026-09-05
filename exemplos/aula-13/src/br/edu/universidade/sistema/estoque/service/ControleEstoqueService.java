package br.edu.universidade.sistema.estoque.service;

import br.edu.universidade.sistema.estoque.dominio.Produto;
import java.util.*;

// 2. Serviço de Gerenciamento com Mapeamentos Simples e Agrupamentos Aninhados
public class ControleEstoqueService {

    // Mapa 1: SKU -> Quantidade em Estoque (Mapeamento Direto)
    private final Map<String, Integer> saldoEstoque = new HashMap<>();

    // Mapa 2: SKU -> Entidade Produto (Catálogo Global)
    private final Map<String, Produto> catalogoProdutos = new HashMap<>();

    // Mapa 3: Categoria -> Lista de Produtos daquela categoria (Agrupamento 1:N)
    private final Map<String, List<Produto>> produtosPorCategoria = new HashMap<>();

    public void cadastrarProduto(Produto produto, int saldoInicial) {
        catalogoProdutos.put(produto.getSku(), produto);
        saldoEstoque.put(produto.getSku(), Math.max(0, saldoInicial));

        // computeIfAbsent: se a categoria não existe, instancia o ArrayList; em seguida, adiciona o produto
        produtosPorCategoria
                .computeIfAbsent(produto.getCategoria(), k -> new ArrayList<>())
                .add(produto);
    }

    public void registrarMovimentacao(String sku, int quantidade) {
        String chave = sku.trim().toUpperCase();
        if (!catalogoProdutos.containsKey(chave)) {
            throw new NoSuchElementException("Produto não localizado para o SKU informado: " + chave);
        }

        // getOrDefault evita NullPointerException caso a chave não exista no mapa
        int saldoAtual = saldoEstoque.getOrDefault(chave, 0);
        int novoSaldo = saldoAtual + quantidade;

        if (novoSaldo < 0) {
            throw new IllegalStateException(
                String.format("Estoque insuficiente para o produto %s. Atual: %d, Tentativa de baixa: %d",
                        chave, saldoAtual, Math.abs(quantidade))
            );
        }

        saldoEstoque.put(chave, novoSaldo);
    }

    public int consultarSaldo(String sku) {
        return saldoEstoque.getOrDefault(sku.trim().toUpperCase(), 0);
    }

    public List<Produto> listarPorCategoria(String categoria) {
        // Retorna a lista da categoria ou uma lista imutável vazia caso não exista
        return produtosPorCategoria.getOrDefault(categoria.trim().toUpperCase(), Collections.emptyList());
    }

    public void exibirRelatorioEstoqueConsolidado() {
        System.out.println("========== POSIÇÃO CONSOLIDADA DE ESTOQUE ==========");
        // Itera via entrySet() para máxima eficiência operacional
        for (Map.Entry<String, Produto> entrada : catalogoProdutos.entrySet()) {
            String sku = entrada.getKey();
            Produto p = entrada.getValue();
            int saldo = saldoEstoque.getOrDefault(sku, 0);
            double valorTotalImobilizado = saldo * p.getPrecoUnitario();

            System.out.printf("%s | Saldo: %4d unid. | Valor em Estoque: R$ %10.2f%n",
                    p, saldo, valorTotalImobilizado);
        }
        System.out.println("=====================================================");
    }
}
