package br.edu.universidade.financeiro;

/**
 * Fornece rotinas utilitárias de cálculo financeiro para a gestão de operações
 * de crédito e parcelamentos empresariais.
 * <p>Esta classe não pode ser instanciada: todos os serviços são expostos por
 * métodos estáticos autossuficientes.</p>
 *
 * @author Prof. Dr.
 * @version 1.0.0
 * @since 1.0.0
 */
public final class CalculadoraFinanceira {

    /**
     * Construtor privado para impedir a instanciação da classe utilitária.
     */
    private CalculadoraFinanceira() {}

    /**
     * Calcula o montante final de uma aplicação sob regime de juros compostos.
     * A fórmula utilizada é: M = C × (1 + i)^n, onde C é o capital, i a taxa
     * mensal e n o número de meses.
     *
     * @param capital    valor do capital principal aplicado; deve ser maior ou igual a zero.
     * @param taxaMensal taxa de juros mensal em representação decimal (ex.: 0,02 = 2%); deve estar entre 0.0 e 1.0.
     * @param meses      prazo da aplicação em meses; deve ser no mínimo 1.
     * @return montante acumulado ao final do período, já incluindo o capital e os juros.
     * @throws IllegalArgumentException se o capital for menor que zero, a taxa estiver fora do intervalo [0.0, 1.0] ou o prazo for inferior a 1 mês.
     */
    public static double calcularJurosCompostos(double capital, double taxaMensal, int meses) {
        if (capital < 0.0) {
            throw new IllegalArgumentException("Capital não pode ser menor que zero: " + capital);
        }
        if (taxaMensal < 0.0 || taxaMensal > 1.0) {
            throw new IllegalArgumentException("Taxa mensal deve estar entre 0.0 e 1.0: " + taxaMensal);
        }
        if (meses < 1) {
            throw new IllegalArgumentException("Prazo deve ser de no mínimo 1 mês: " + meses);
        }
        return capital * Math.pow(1.0 + taxaMensal, meses);
    }

    /**
     * Calcula a cota fixa de amortização sob o sistema de amortização constante (SAC).
     * O valor é obtido dividindo-se o saldo devedor atual pelo prazo restante em meses.
     *
     * @param saldoDevedor saldo principal ainda devido; deve ser maior ou igual a zero.
     * @param prazoMeses   prazo total do financiamento em meses; deve ser estritamente positivo.
     * @return valor da cota fixa de amortização que incide sobre o principal.
     * @throws IllegalArgumentException se o saldo devedor for negativo ou o prazo não for positivo.
     */
    public static double calcularAmortizacaoConstante(double saldoDevedor, int prazoMeses) {
        if (saldoDevedor < 0.0) {
            throw new IllegalArgumentException("Saldo devedor não pode ser negativo: " + saldoDevedor);
        }
        if (prazoMeses <= 0) {
            throw new IllegalArgumentException("Prazo deve ser positivo em meses: " + prazoMeses);
        }
        return saldoDevedor / prazoMeses;
    }
}