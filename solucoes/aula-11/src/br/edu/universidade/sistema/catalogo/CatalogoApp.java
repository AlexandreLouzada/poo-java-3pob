package br.edu.universidade.sistema.catalogo;

import br.edu.universidade.sistema.catalogo.dominio.Produto;
import br.edu.universidade.sistema.catalogo.service.CatalogoService;

public class CatalogoApp {
    public static void main(String[] args) {
        CatalogoService service = new CatalogoService();

        service.cadastrar(new Produto("SKU-003", "Monitor 27 Pol", 1400.00, 8));
        service.cadastrar(new Produto("SKU-001", "Teclado Mecanico", 290.90, 20));
        service.cadastrar(new Produto("SKU-004", "Cabo HDMI 2.1", 45.00, 100));
        service.cadastrar(new Produto("SKU-002", "Mouse Sem Fio", 120.50, 15));

        System.out.println("--- Catalogo Apos Cadastro (Ordem de Insercao) ---");
        service.listarTodos().forEach(System.out::println);
        System.out.printf("Total de produtos: %d%n", service.getTotalProdutos());

        System.out.println("\n--- Exclusao do Produto Intermediario (SKU-002) ---");
        boolean removido = service.excluirPorCodigo("SKU-002");
        System.out.println("Produto removido com sucesso? " + removido);
        System.out.printf("Total de produtos apos exclusao: %d%n", service.getTotalProdutos());

        System.out.println("\n--- Acesso por Posicao (indice 0 apos exclusao, reindexado) ---");
        System.out.println("Indice 0: " + service.obterPorPosicao(0));

        System.out.println("\n--- Catalogo Ordenado por Preco (Menor -> Maior) ---");
        service.ordenarPorPreco();
        int posicao = 0;
        for (Produto p : service.listarTodos()) {
            System.out.printf("Indice %d: %s%n", posicao++, p);
        }
    }
}