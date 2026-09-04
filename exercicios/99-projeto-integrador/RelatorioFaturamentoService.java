import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

class RelatorioFaturamentoService {
    public void gerarRelatorioConsolidado(List<Fatura> faturas) {
        System.out.println("\n========== RELATÓRIO ANALÍTICO DE FATURAMENTO ==========");

        Map<StatusFatura, List<Fatura>> faturasPorStatus = faturas.stream()
                .collect(Collectors.groupingBy(Fatura::getStatus));

        System.out.printf("Total de Faturas Pagas: %d%n", 
                faturasPorStatus.getOrDefault(StatusFatura.PAGA, Collections.emptyList()).size());
        System.out.printf("Total de Faturas Recusadas: %d%n", 
                faturasPorStatus.getOrDefault(StatusFatura.RECUSADA, Collections.emptyList()).size());

        DoubleSummaryStatistics stats = faturas.stream()
                .filter(f -> f.getStatus() == StatusFatura.PAGA)
                .mapToDouble(Fatura::getValorTotal)
                .summaryStatistics();

        System.out.printf("Receita Total Faturada: R$ %.2f%n", stats.getSum());
        System.out.printf("Ticket Médio por Fatura: R$ %.2f%n", stats.getAverage());
        System.out.printf("Maior Fatura Registrada: R$ %.2f%n", stats.getMax());
        System.out.println("========================================================");
    }
}
