package br.edu.universidade.sistema.tarifacao.service;

import br.edu.universidade.sistema.tarifacao.dominio.TransacaoFinanceira;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class MotorRegrasFinanceirasService {

    public List<TransacaoFinanceira> auditarTransacoes(
            List<TransacaoFinanceira> lista, Predicate<TransacaoFinanceira> regraAuditoria) {
        List<TransacaoFinanceira> aprovadas = new ArrayList<>();
        for (TransacaoFinanceira t : lista) {
            if (regraAuditoria.test(t)) {
                aprovadas.add(t);
            }
        }
        return aprovadas;
    }

    public void aplicarTarifacao(List<TransacaoFinanceira> lista,
                                 Function<TransacaoFinanceira, Double> calculadorTarifa,
                                 Consumer<TransacaoFinanceira> auditoriaFinal) {
        for (TransacaoFinanceira t : lista) {
            double taxa = calculadorTarifa.apply(t);
            t.abaterTaxa(taxa);
            auditoriaFinal.accept(t);
        }
    }
}