package br.edu.universidade.sistema.logistica;

// 1. Enum Avançado com Estado Imutável e Regra de Negócio Encapsulada
public enum RegiaoEntrega {
    SUDESTE(1.00, 1),
    SUL(1.15, 3),
    CENTRO_OESTE(1.25, 4),
    NORDESTE(1.40, 6),
    NORTE(1.60, 8);

    private final double multiplicadorTarifario; // Atributo imutável
    private final int prazoDiasUteis;

    // Construtor do enum: obrigatoriamente privado ou com visibilidade de pacote
    RegiaoEntrega(double multiplicadorTarifario, int prazoDiasUteis) {
        this.multiplicadorTarifario = multiplicadorTarifario;
        this.prazoDiasUteis = prazoDiasUteis;
    }

    public double getMultiplicadorTarifario() {
        return this.multiplicadorTarifario;
    }

    public int getPrazoDiasUteis() {
        return this.prazoDiasUteis;
    }

    // Método encapsulado de domínio dentro do próprio enum
    public double ajustarValorBase(double taxaBase) {
        return taxaBase * this.multiplicadorTarifario;
    }
}
