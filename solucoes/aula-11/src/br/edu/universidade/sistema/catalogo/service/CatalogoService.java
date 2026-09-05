package br.edu.universidade.sistema.catalogo.service;

import br.edu.universidade.sistema.catalogo.dominio.Produto;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CatalogoService {

    private final List<Produto> produtos = new ArrayList<>();

    public void cadastrar(Produto p) {
        if (p == null) {
            throw new IllegalArgumentException("Produto nulo nao pode ser cadastrado.");
        }
        if (produtos.contains(p)) {
            throw new IllegalArgumentException("Codigo duplicado: " + p.getCodigo());
        }
        produtos.add(p);
    }

    public Produto obterPorPosicao(int indice) {
        if (indice < 0 || indice >= produtos.size()) {
            throw new IndexOutOfBoundsException("Indice fora dos limites do catalogo: " + indice);
        }
        return produtos.get(indice);
    }

    public boolean excluirPorCodigo(String codigo) {
        for (Produto p : produtos) {
            if (p.getCodigo().equals(codigo)) {
                produtos.remove(p);
                return true;
            }
        }
        return false;
    }

    public void ordenarPorPreco() {
        Collections.sort(produtos);
    }

    public List<Produto> listarTodos() {
        return new ArrayList<>(produtos);
    }

    public int getTotalProdutos() {
        return produtos.size();
    }
}