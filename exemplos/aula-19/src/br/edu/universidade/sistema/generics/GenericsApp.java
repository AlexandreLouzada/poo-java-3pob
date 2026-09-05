package br.edu.universidade.sistema.generics;

import br.edu.universidade.sistema.generics.dominio.Cliente;
import br.edu.universidade.sistema.generics.dominio.Produto;
import br.edu.universidade.sistema.generics.repositorio.RepositorioGenerico;
import br.edu.universidade.sistema.generics.service.ProcessadorLotesService;
import java.util.ArrayList;
import java.util.List;

// 6. Aplicação Executável demonstrando Generics, Repositórios e PECS
public class GenericsApp {
    public static void main(String[] args) {
        System.out.println("--- 1. Repositório Genérico Parametrizado ---");

        // Instanciação com tipos estritos: Repositório de Produto com ID Long
        RepositorioGenerico<Produto, Long> repoProdutos = new RepositorioGenerico<>();
        repoProdutos.salvar(new Produto(101L, "Notebook Gamer", 5500.00));
        repoProdutos.salvar(new Produto(102L, "Monitor Ultrawide", 1800.00));

        System.out.printf("Total de produtos cadastrados: %d%n", repoProdutos.totalRegistros());
        repoProdutos.listarTodos().forEach(System.out::println);

        // Reuso completo da mesma estrutura para Clientes com ID String (CPF)
        RepositorioGenerico<Cliente, String> repoClientes = new RepositorioGenerico<>();
        repoClientes.salvar(new Cliente("111.222.333-44", "Beatriz Costa"));
        repoClientes.salvar(new Cliente("555.666.777-88", "Carlos Eduardo"));

        System.out.printf("%nTotal de clientes cadastrados: %d%n", repoClientes.totalRegistros());
        repoClientes.listarTodos().forEach(System.out::println);

        System.out.println("\n--- 2. Demonstração do Princípio PECS ---");
        List<Produto> loteNovosProdutos = List.of(
                new Produto(103L, "Teclado Mecanico", 350.00),
                new Produto(104L, "Mouse Sem Fio", 180.00)
        );

        // Lista de destino tipada como superclasse direta (Object)
        List<Object> relatorioGeral = new ArrayList<>();

        // PECS em ação: List<Produto> é Produtora (extends) -> List<Object> é Consumidora (super)
        ProcessadorLotesService.transferirElementos(loteNovosProdutos, relatorioGeral);

        System.out.printf("Itens transferidos para a lista geral (%d itens):%n", relatorioGeral.size());
        relatorioGeral.forEach(item -> System.out.println("Relatório Item: " + item));

        System.out.println("\n--- 3. Bounded Wildcard em Operações Aritméticas ---");
        double precoMedio = ProcessadorLotesService.calcularPrecoMedio(loteNovosProdutos);
        System.out.printf("Preço médio do lote de produtos: R$ %.2f%n", precoMedio);
    }
}
