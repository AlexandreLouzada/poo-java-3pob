package br.edu.universidade.sistema.financeiro;

import br.edu.universidade.sistema.financeiro.dominio.LancamentoFinanceiro;
import br.edu.universidade.sistema.financeiro.service.FechamentoContabilService;
import java.time.LocalDate;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.OptionalDouble;

// 3. Aplicação Executável demonstrando cálculos e o consumo defensivo do Optional
public class ContabilidadeApp {
    public static void main(String[] args) {
        List<LancamentoFinanceiro> baseLancamentos = List.of(
                new LancamentoFinanceiro(1001L, "Servidores em Nuvem", "TECNOLOGIA", 4200.50, LocalDate.now()),
                new LancamentoFinanceiro(1002L, "Licenças de Software", "TECNOLOGIA", 1850.00, LocalDate.now()),
                new LancamentoFinanceiro(1003L, "Campanha de Marketing", "MARKETING", 8500.00, LocalDate.now()),
                new LancamentoFinanceiro(1004L, "Material de Escritório", "ADMINISTRATIVO", 340.20, LocalDate.now()),
                new LancamentoFinanceiro(1005L, "Manutenção Predial", "ADMINISTRATIVO", 1200.00, LocalDate.now())
        );

        FechamentoContabilService service = new FechamentoContabilService();

        System.out.println("--- 1. Volume Total por Centro de Custo via DoubleStream ---");
        double totalTI = service.calcularVolumeTotalPorCentroCusto(baseLancamentos, "TECNOLOGIA");
        System.out.printf("Total Gasto em TECNOLOGIA: R$ %.2f%n", totalTI);

        System.out.println("\n--- 2. Cálculo Seguro de Média via OptionalDouble ---");
        OptionalDouble ticketMedio = service.calcularTicketMedio(baseLancamentos);
        if (ticketMedio.isPresent()) {
            System.out.printf("Ticket Médio Geral: R$ %.2f%n", ticketMedio.getAsDouble());
        } else {
            System.out.println("Não foi possível calcular a média: base sem registros.");
        }

        System.out.println("\n--- 3. Consumo Funcional de Optional sem IF com ifPresent ---");
        service.buscarMaiorLancamento(baseLancamentos).ifPresent(l ->
                System.out.println("Maior Gasto Detectado: " + l));

        System.out.println("\n--- 4. Busca Pontual com Fallback e Lançamento de Exceção ---");
        // Caso A: Lançamento Encontrado
        LancamentoFinanceiro l1 = service.buscarPorId(baseLancamentos, 1001L)
                .orElseThrow(() -> new NoSuchElementException("Lançamento #1001 não encontrado!"));
        System.out.println("Sucesso na Consulta: " + l1.getDescricao());

        // Caso B: Lançamento Inexistente tratado via orElse com valor sentinela padrão
        LancamentoFinanceiro lPadrao = service.buscarPorId(baseLancamentos, 9999L)
                .orElse(new LancamentoFinanceiro(0L, "Lancamento Padrão de Fallback", "GERAL", 1.0, LocalDate.now()));
        System.out.println("Resultado da Consulta Inexistente (Fallback): " + lPadrao.getDescricao());

        System.out.println("\n--- 5. Métricas Consolidadas (DoubleSummaryStatistics) ---");
        DoubleSummaryStatistics stats = service.extrairMetricasGlobais(baseLancamentos);
        System.out.printf("Registros Analisados : %d%n", stats.getCount());
        System.out.printf("Volume Consolidado   : R$ %.2f%n", stats.getSum());
        System.out.printf("Menor Lançamento     : R$ %.2f%n", stats.getMin());
        System.out.printf("Maior Lançamento     : R$ %.2f%n", stats.getMax());
        System.out.printf("Média de Desembolso  : R$ %.2f%n", stats.getAverage());
    }
}
