package br.edu.universidade.sistema.faturamento;

public enum NivelAssinatura {
    BRONZE(80.00, 3),
    PRATA(150.00, 10),
    OURO(300.00, 25);

    private final double mensalidadeBase;
    private final int limiteUsuariosInclusos;

    NivelAssinatura(double mensalidadeBase, int limiteUsuariosInclusos) {
        this.mensalidadeBase = mensalidadeBase;
        this.limiteUsuariosInclusos = limiteUsuariosInclusos;
    }

    public double getMensalidadeBase() {
        return mensalidadeBase;
    }

    public int getLimiteUsuariosInclusos() {
        return limiteUsuariosInclusos;
    }
}