package br.edu.universidade.sistema.tarifacao;

import br.edu.universidade.sistema.tarifacao.dominio.TransacaoFinanceira;
import br.edu.universidade.sistema.tarifacao.service.MotorRegrasFinanceirasService;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class MotorRegrasApp {
    public static void main(String[] args) {
        MotorRegrasFinanceirasService service = new MotorRegrasFinanceirasService();

        List<TransacaoFinanceira> transacoes = List.of(
                new TransacaoFinanceira(1L, "pix-alfa", 700.00, "CREDITO"),
                new TransacaoFinanceira(2L, "pix-beta", 120.00, "DEBITO"),
                new TransacaoFinanceira(3L, "pix-gama", 30.00, "DEBITO"),
                new TransacaoFinanceira(4L, "pix-delta", 250.00, "BOLETO")
        );

        Predicate<TransacaoFinanceira> regraAuditoria = t ->
                t.getValor() > 50.00 && !t.getTipoOperacao().equals("BOLETO");

        Function<TransacaoFinanceira, Double> calculadorTarifa = t -> {
            if (t.getTipoOperacao().equals("CREDITO")) {
                return t.getValor() * 0.02;
            }
            return 1.00;
        };

        Consumer<TransacaoFinanceira> auditoriaFinal = t ->
                System.out.printf("[TARIFADA] TX #%d - Valor Final Liquido: R$ %.2f%n",
                        t.getId(), t.getValor());

        System.out.println("--- Aprovacao pelo Predicate (valor > R$ 50 e nao BOLETO) ---");
        List<TransacaoFinanceira> aprovadas = service.auditarTransacoes(transacoes, regraAuditoria);
        aprovadas.forEach(System.out::println);
        System.out.printf("Total aprovado para tarifacao: %d de %d transacoes.%n",
                aprovadas.size(), transacoes.size());

        System.out.println("\n--- Aplicacao da Tarifacao (Funcao + Consumer) ---");
        service.aplicarTarifacao(aprovadas, calculadorTarifa, auditoriaFinal);

        System.out.println("\n--- Comprovacao dos Valores Liquidos Finais ---");
        aprovadas.forEach(System.out::println);
    }
}