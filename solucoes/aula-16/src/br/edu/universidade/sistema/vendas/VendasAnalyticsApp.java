package br.edu.universidade.sistema.vendas;

import br.edu.universidade.sistema.vendas.dominio.VendaRepresentante;
import br.edu.universidade.sistema.vendas.service.AuditoriaVendasService;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

public class VendasAnalyticsApp {
    public static void main(String[] args) {
        AuditoriaVendasService service = new AuditoriaVendasService();

        List<VendaRepresentante> vendas = List.of(
                new VendaRepresentante(1L, "Carlos Prado", "SUL", 8500.00, 700.00),
                new VendaRepresentante(2L, "Beatriz Costa", "SUDESTE", 12000.00, 960.00),
                new VendaRepresentante(3L, "Carlos Prado", "SUL", 4500.00, 350.00),
                new VendaRepresentante(4L, "Lucas Mendes", "NORTE", 3200.00, 210.00)
        );

        System.out.println("--- Faturamento Consolidado por Regiao ---");
        System.out.printf("Regiao SUL     : R$ %.2f%n", service.calcularFaturamentoTotalPorRegiao(vendas, "SUL"));
        System.out.printf("Regiao SUDESTE : R$ %.2f%n", service.calcularFaturamentoTotalPorRegiao(vendas, "SUDESTE"));

        System.out.println("\n--- Media de Comissoes Pagas (OptionalDouble) ---");
        OptionalDouble media = service.calcularMediaComissoesPagas(vendas);
        System.out.printf("Ticket medio de comissoes: R$ %.2f%n", media.orElse(0.0));

        System.out.println("\n--- Maior Venda de 'Carlos Prado' (ifPresent) ---");
        Optional<VendaRepresentante> maior = service.buscarMaiorVendaPorRepresentante(vendas, "Carlos Prado");
        maior.ifPresent(System.out::println);

        System.out.println("\n--- Maior Venda de Vendedor Sem Registros (orElseGet amigavel) ---");
        Optional<VendaRepresentante> inexistente = service.buscarMaiorVendaPorRepresentante(vendas, "Ana Clara");
        VendaRepresentante resultado = inexistente.orElseGet(() -> {
            System.out.println("[AVISO] Nenhuma venda registrada para 'Ana Clara'.");
            return null;
        });
        System.out.println("Resultado tratado: " + (resultado == null ? "ausencia de registros" : resultado));

        System.out.println("\n--- Busca Com Garantia (lanca NoSuchElementException) ---");
        var garantia = service.obterVendaComGarantia(vendas, 3L);
        System.out.println("Garantia: " + garantia);
    }
}