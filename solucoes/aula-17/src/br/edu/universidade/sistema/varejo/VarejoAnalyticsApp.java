package br.edu.universidade.sistema.varejo;

import br.edu.universidade.sistema.varejo.dominio.ItemMercadoria;
import br.edu.universidade.sistema.varejo.service.AuditoriaMercadoriasService;
import java.util.List;
import java.util.Map;

public class VarejoAnalyticsApp {
    public static void main(String[] args) {
        AuditoriaMercadoriasService service = new AuditoriaMercadoriasService();

        List<ItemMercadoria> itens = List.of(
                new ItemMercadoria("SKU-AL-01", "Arroz 5kg", "ALIMENTOS", 120, 24.90),
                new ItemMercadoria("SKU-AL-02", "Feijao 1kg", "ALIMENTOS", 4, 9.50),
                new ItemMercadoria("SKU-LM-01", "Detergente 500ml", "LIMPEZA", 60, 3.20),
                new ItemMercadoria("SKU-LM-02", "Sabao em po 1kg", "LIMPEZA", 2, 12.00),
                new ItemMercadoria("SKU-BE-01", "Refrigerante 2L", "BEBIDAS", 90, 7.80),
                new ItemMercadoria("SKU-BE-02", "Suco Uva 1L", "BEBIDAS", 5, 6.40)
        );

        System.out.println("--- Valor Total Imobilizado por Setor (groupingBy + summingDouble) ---");
        Map<String, Double> imobilizado = service.calcularValorTotalImobilizadoPorSetor(itens);
        imobilizado.forEach((setor, total) ->
                System.out.printf("Setor: %-10s | Capital implicado: R$ %.2f%n", setor, total));

        System.out.println("\n--- Particionamento por Ruptura de Estoque (qtd <= 5) ---");
        Map<Boolean, List<ItemMercadoria>> particionado = service.particionarPorRupturaEstoque(itens);
        System.out.println("Chave true (EM ALERTA DE RUPTURA):");
        particionado.get(true).forEach(i -> System.out.println("   " + i));
        System.out.println("Chave false (ESTOQUE NORMAL):");
        particionado.get(false).forEach(i -> System.out.println("   " + i));

        System.out.println("\n--- Catalogo Setorial CSV (Setor LIMPEZA) ---");
        String csv = service.emitirCatalogoSetorialCsv(itens, "LIMPEZA");
        System.out.println(csv);

        System.out.println("\n--- Setores Presentes no Estoque ---");
        service.listarSetores(itens).forEach(System.out::println);
    }
}