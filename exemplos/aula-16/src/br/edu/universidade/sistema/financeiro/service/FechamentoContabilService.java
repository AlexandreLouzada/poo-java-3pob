package br.edu.universidade.sistema.financeiro.service;

import br.edu.universidade.sistema.financeiro.dominio.LancamentoFinanceiro;
import java.util.*;

// 2. Serviço Contábil operando sobre Streams Primitivas e Optional
public class FechamentoContabilService {

    // 1. Totalização via DoubleStream nativo (elimina Autoboxing e otimiza memória)
    public double calcularVolumeTotalPorCentroCusto(List<LancamentoFinanceiro> lancamentos, String centroCusto) {
        return lancamentos.stream()
                .filter(l -> l.getCentroCusto().equalsIgnoreCase(centroCusto))
                .mapToDouble(LancamentoFinanceiro::getValor) // Conversão para DoubleStream primitivo
                .sum(); // Redução nativa em nível de hardware
    }

    // 2. Média aritmética retornando OptionalDouble defensivo
    public OptionalDouble calcularTicketMedio(List<LancamentoFinanceiro> lancamentos) {
        return lancamentos.stream()
                .mapToDouble(LancamentoFinanceiro::getValor)
                .average(); // Retorna OptionalDouble vazio caso a lista esteja vazia
    }

    // 3. Localização do maior lançamento individual registrado
    public Optional<LancamentoFinanceiro> buscarMaiorLancamento(List<LancamentoFinanceiro> lancamentos) {
        return lancamentos.stream()
                .max(Comparator.comparingDouble(LancamentoFinanceiro::getValor));
    }

    // 4. Busca pontual de lançamento por ID sem risco de retorno nulo
    public Optional<LancamentoFinanceiro> buscarPorId(List<LancamentoFinanceiro> lancamentos, Long id) {
        return lancamentos.stream()
                .filter(l -> l.getId().equals(id))
                .findFirst(); // Retorna o primeiro elemento encontrado ou Optional.empty()
    }

    // 5. Relatório estatístico consolidado gerado em única passada
    public DoubleSummaryStatistics extrairMetricasGlobais(List<LancamentoFinanceiro> lancamentos) {
        return lancamentos.stream()
                .mapToDouble(LancamentoFinanceiro::getValor)
                .summaryStatistics();
    }
}
