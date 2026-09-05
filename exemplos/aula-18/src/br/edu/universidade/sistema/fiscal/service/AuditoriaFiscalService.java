package br.edu.universidade.sistema.fiscal.service;

import br.edu.universidade.sistema.fiscal.dominio.CupomFiscal;
import java.util.List;

// 2. Serviço de Auditoria Comparativo (Sequencial vs. Paralelo)
public class AuditoriaFiscalService {

    // Processamento com Stream Sequencial convencional
    public double calcularImpostoTotalSequencial(List<CupomFiscal> cupons) {
        return cupons.stream()
                .filter(c -> !c.isCancelado())
                .mapToDouble(CupomFiscal::calcularImpostoComplexo)
                .sum();
    }

    // Processamento com Stream Paralela (Distribuída no ForkJoinPool)
    public double calcularImpostoTotalParalelo(List<CupomFiscal> cupons) {
        return cupons.parallelStream() // Habilita o pipeline multi-core
                .filter(c -> !c.isCancelado())
                .mapToDouble(CupomFiscal::calcularImpostoComplexo)
                .sum(); // Redução nativa paralela thread-safe
    }

    // Identificação paralela de cupons válidos com valor crítico (> R$ 10.000)
    public long contarTransacoesCriticasParalelo(List<CupomFiscal> cupons, double corte) {
        return cupons.parallelStream()
                .filter(c -> !c.isCancelado())
                .filter(c -> c.getValorTotal() >= corte)
                .count();
    }
}
