package br.edu.universidade.sistema.logistica;

// 2. Serviço com Sobrecarga de Métodos e Varargs Defensivo
public class CalculadoraFrete {

    private static final double TAXA_POR_QUILO = 5.50;

    // Sobrecarga 1: Cálculo simples para pacote único com destino padrão
    public double calcular(double pesoKg) {
        return calcular(pesoKg, RegiaoEntrega.SUDESTE); // Delegação de responsabilidade
    }

    // Sobrecarga 2: Cálculo para pacote único com região específica
    public double calcular(double pesoKg, RegiaoEntrega regiao) {
        if (pesoKg <= 0.0) {
            return 0.0;
        }
        if (regiao == null) {
            regiao = RegiaoEntrega.SUDESTE; // Fallback defensivo
        }
        double valorBase = pesoKg * TAXA_POR_QUILO;
        return regiao.ajustarValorBase(valorBase);
    }

    // Sobrecarga 3: Varargs para pesagem de múltiplos pacotes em lote
    public double calcular(RegiaoEntrega regiao, double... pesosItens) {
        if (pesosItens == null || pesosItens.length == 0) {
            return 0.0;
        }

        double pesoTotal = 0.0;
        for (double peso : pesosItens) {
            if (peso > 0.0) {
                pesoTotal += peso;
            }
        }

        return calcular(pesoTotal, regiao); // Reutiliza a regra da sobrecarga 2
    }

    // Exemplo de integração do enum com Switch Expression moderna
    public String emitirPrevisaoEntrega(RegiaoEntrega regiao) {
        return switch (regiao) {
            case SUDESTE -> "Entrega expressa regional: " + regiao.getPrazoDiasUteis() + " dia útil.";
            case SUL, CENTRO_OESTE -> "Entrega intermodal padrão: " + regiao.getPrazoDiasUteis() + " dias úteis.";
            case NORDESTE, NORTE -> "Entrega de longa distância: " + regiao.getPrazoDiasUteis() + " dias úteis.";
        };
    }
}
