package br.edu.universidade.sistema.fiscal;

import br.edu.universidade.sistema.fiscal.dominio.CupomFiscal;
import br.edu.universidade.sistema.fiscal.service.AuditoriaFiscalService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

// 3. Aplicação Executável com Benchmark de Performance
public class ParalelismoFiscalApp {
    public static void main(String[] args) {
        System.out.println("========== BENCHMARK: STREAMS SEQUENCIAIS VS. PARALELAS ==========");
        System.out.printf("Núcleos de CPU Lógicos Detectados: %d%n", Runtime.getRuntime().availableProcessors());
        System.out.printf("Threads no ForkJoinPool Comum    : %d%n", ForkJoinPool.getCommonPoolParallelism());

        // Geração controlada de massa de dados (1.000.000 de registros)
        int volumeDados = 1_000_000;
        System.out.printf("Carregando massa em memória (%d cupons)...%n", volumeDados);
        List<CupomFiscal> baseCupons = new ArrayList<>(volumeDados);

        for (long i = 1; i <= volumeDados; i++) {
            baseCupons.add(new CupomFiscal(
                    i,
                    "NFE-" + i,
                    100.0 + (i % 500),
                    (i % 10 == 0) // 10% cancelados
            ));
        }

        AuditoriaFiscalService service = new AuditoriaFiscalService();

        // 1. Execução Sequencial
        System.out.println("\nIniciando processamento SEQUENCIAL...");
        long inicioSeq = System.currentTimeMillis();
        double totalImpostosSeq = service.calcularImpostoTotalSequencial(baseCupons);
        long duracaoSeq = System.currentTimeMillis() - inicioSeq;
        System.out.printf("Total Impostos (Seq) : R$ %.2f%n", totalImpostosSeq);
        System.out.printf("Tempo Gasto (Seq)    : %d ms%n", duracaoSeq);

        // 2. Execução Paralela
        System.out.println("\nIniciando processamento PARALELO...");
        long inicioPar = System.currentTimeMillis();
        double totalImpostosPar = service.calcularImpostoTotalParalelo(baseCupons);
        long duracaoPar = System.currentTimeMillis() - inicioPar;
        System.out.printf("Total Impostos (Par) : R$ %.2f%n", totalImpostosPar);
        System.out.printf("Tempo Gasto (Par)    : %d ms%n", duracaoPar);

        // Análise de Eficiência (Speedup)
        double speedup = (double) duracaoSeq / duracaoPar;
        System.out.printf("\nFator de Aceleração Real (Speedup): %.2fx mais rápido%n", speedup);

        // 3. Contagem Paralela
        long criticos = service.contarTransacoesCriticasParalelo(baseCupons, 500.0);
        System.out.printf("Total de Cupons com Valor >= R$ 500: %d%n", criticos);
        System.out.println("==================================================================");
    }
}
