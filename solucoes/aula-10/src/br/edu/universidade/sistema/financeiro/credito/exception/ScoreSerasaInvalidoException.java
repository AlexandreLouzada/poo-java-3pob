package br.edu.universidade.sistema.financeiro.credito.exception;

public class ScoreSerasaInvalidoException extends RuntimeException {

    private final int pontuacaoInformada;

    public ScoreSerasaInvalidoException(String mensagem, int pontuacaoInformada) {
        super(mensagem);
        this.pontuacaoInformada = pontuacaoInformada;
    }

    public ScoreSerasaInvalidoException(String mensagem, Throwable causa) {
        super(mensagem, causa);
        this.pontuacaoInformada = -1;
    }

    public int getPontuacaoInformada() {
        return pontuacaoInformada;
    }
}