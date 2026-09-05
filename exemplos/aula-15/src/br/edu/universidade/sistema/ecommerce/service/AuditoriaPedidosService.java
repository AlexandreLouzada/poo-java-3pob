package br.edu.universidade.sistema.ecommerce.service;

import br.edu.universidade.sistema.ecommerce.dominio.PedidoCompra;
import java.util.Comparator;
import java.util.List;

// 2. Serviço de Auditoria e Análise usando Pipelines da Streams API
public class AuditoriaPedidosService {

    // Pipeline 1: Filtra apenas pedidos pagos de uma categoria, ordenando do maior para o menor valor
    public List<PedidoCompra> obterPedidosPagosOrdenadosPorValor(List<PedidoCompra> pedidos, String categoria) {
        return pedidos.stream()
                .filter(PedidoCompra::isPago) // Operação Intermediária: apenas confirmados
                .filter(p -> p.getCategoria().equalsIgnoreCase(categoria)) // Operação Intermediária
                .sorted(Comparator.comparing(PedidoCompra::getValorTotal).reversed()) // Intermediária: decrescente
                .toList(); // Operação Terminal (Java 16+)
    }

    // Pipeline 2: Extrai uma lista com os nomes únicos de clientes que fizeram pedidos de alto valor (> R$ 1.000)
    public List<String> obterNomesClientesVip(List<PedidoCompra> pedidos) {
        return pedidos.stream()
                .filter(p -> p.getValorTotal() >= 1000.0) // Filtro de valor mínimo
                .map(PedidoCompra::getCliente) // Projeta PedidoCompra -> String (Nome)
                .map(String::toUpperCase) // Transforma o texto para maiúsculo
                .distinct() // Elimina nomes duplicados
                .sorted() // Ordena alfabeticamente
                .toList(); // Operação Terminal
    }

    // Pipeline 3: Consulta de verificação se existe algum pedido pendente com valor crítico (> R$ 5.000)
    public boolean existeRiscoFraudePendente(List<PedidoCompra> pedidos) {
        return pedidos.stream()
                .filter(p -> !p.isPago()) // Pedidos ainda não compensados
                .anyMatch(p -> p.getValorTotal() > 5000.00); // Terminal com curto-circuito
    }

    // Pipeline 4: Retorna os TOP 3 maiores pedidos registrados na base
    public List<PedidoCompra> obterTop3MaioresPedidos(List<PedidoCompra> pedidos) {
        return pedidos.stream()
                .sorted(Comparator.comparing(PedidoCompra::getValorTotal).reversed())
                .limit(3) // Corta o fluxo após os 3 primeiros
                .toList();
    }
}
