package br.edu.universidade.sistema.ecommerce;

import br.edu.universidade.sistema.ecommerce.dominio.PedidoCompra;
import br.edu.universidade.sistema.ecommerce.service.AuditoriaPedidosService;
import java.util.List;

// 3. Aplicação Executável demonstrando a execução das Streams
public class StreamsApp {
    public static void main(String[] args) {
        List<PedidoCompra> basePedidos = List.of(
                new PedidoCompra(101L, "Carlos Eduardo", "ELETRONICOS", 1200.00, true),
                new PedidoCompra(102L, "Beatriz Costa", "LIVROS", 150.00, true),
                new PedidoCompra(103L, "Carlos Eduardo", "ELETRONICOS", 3500.00, true),
                new PedidoCompra(104L, "Ana Clara", "ELETRONICOS", 850.00, false),
                new PedidoCompra(105L, "Lucas Mendes", "MOVEIS", 2200.00, true),
                new PedidoCompra(106L, "Mariana Silva", "ELETRONICOS", 6200.00, false),
                new PedidoCompra(107L, "Beatriz Costa", "ELETRONICOS", 1800.00, true)
        );

        AuditoriaPedidosService service = new AuditoriaPedidosService();

        System.out.println("--- 1. Pedidos Pagos da Categoria ELETRONICOS (Decrescente por Valor) ---");
        List<PedidoCompra> eletronicosPagos = service.obterPedidosPagosOrdenadosPorValor(basePedidos, "ELETRONICOS");
        eletronicosPagos.forEach(System.out::println);

        System.out.println("\n--- 2. Lista de Clientes VIP Sem Repetições (Compras >= R$ 1.000) ---");
        List<String> clientesVip = service.obterNomesClientesVip(basePedidos);
        clientesVip.forEach(nome -> System.out.println("VIP: " + nome));

        System.out.println("\n--- 3. Auditoria de Segurança contra Fraude ---");
        boolean alertaFraude = service.existeRiscoFraudePendente(basePedidos);
        System.out.println("Existe pedido pendente com valor de risco crítico (> R$ 5.000)? " + alertaFraude);

        System.out.println("\n--- 4. TOP 3 Maiores Compras do Portal ---");
        List<PedidoCompra> top3 = service.obterTop3MaioresPedidos(basePedidos);
        top3.forEach(System.out::println);

        System.out.println("\n--- 5. Comprovação da Imutabilidade da Fonte Original ---");
        System.out.printf("Total de registros originais na lista intacta: %d%n", basePedidos.size());
    }
}
