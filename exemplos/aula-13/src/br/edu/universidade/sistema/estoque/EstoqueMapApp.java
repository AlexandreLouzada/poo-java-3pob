package br.edu.universidade.sistema.estoque;

import br.edu.universidade.sistema.estoque.dominio.Produto;
import br.edu.universidade.sistema.estoque.service.ControleEstoqueService;
import java.util.*;

// 3. Aplicação Executável demonstrando uso de Map e Contagem de Frequência de Texto
public class EstoqueMapApp {
    public static void main(String[] args) {
        ControleEstoqueService service = new ControleEstoqueService();

        Produto p1 = new Produto("SKU-101", "Teclado Mecanico", "PERIFERICOS", 250.00);
        Produto p2 = new Produto("SKU-102", "Mouse Sem Fio", "PERIFERICOS", 120.00);
        Produto p3 = new Produto("SKU-201", "Monitor 27 Pol", "MONITORES", 1400.00);
        Produto p4 = new Produto("SKU-301", "Cabo HDMI 2.1", "CABOS", 45.00);

        service.cadastrarProduto(p1, 20);
        service.cadastrarProduto(p2, 35);
        service.cadastrarProduto(p3, 8);
        service.cadastrarProduto(p4, 100);

        System.out.println("--- 1. Movimentações de Entrada e Saída ---");
        service.registrarMovimentacao("SKU-101", -5);  // Venda de 5 teclados
        service.registrarMovimentacao("SKU-201", 2);   // Recebimento de 2 monitores
        System.out.printf("Saldo atual SKU-101: %d unidades%n", service.consultarSaldo("SKU-101"));

        System.out.println("\n--- 2. Consulta por Agrupamento (Categoria PERIFERICOS) ---");
        List<Produto> perifericos = service.listarPorCategoria("PERIFERICOS");
        perifericos.forEach(System.out::println);

        System.out.println("\n--- 3. Relatório Geral Consolidado ---");
        service.exibirRelatorioEstoqueConsolidado();

        System.out.println("\n--- 4. Algoritmo de Frequência de Palavras com Map ---");
        String textoLogs = "erro timeout conexao erro falha timeout erro fatal memoria falha";
        Map<String, Integer> frequenciaTermos = contarFrequencia(textoLogs);

        // Exibição ordenada alfabeticamente usando TreeMap
        Map<String, Integer> frequenciaOrdenada = new TreeMap<>(frequenciaTermos);
        frequenciaOrdenada.forEach((termo, contagem) ->
            System.out.printf("Termo: %-10s | Ocorrências: %d%n", termo, contagem)
        );
    }

    // Algoritmo clássico de contagem de frequência de termos utilizando Map.merge()
    private static Map<String, Integer> contarFrequencia(String texto) {
        Map<String, Integer> mapa = new HashMap<>();
        String[] palavras = texto.split("\\s+");

        for (String p : palavras) {
            // Se a chave não existir, atribui 1; se existir, soma 1 com o valor prévio
            mapa.merge(p.toLowerCase(), 1, Integer::sum);
        }
        return mapa;
    }
}
