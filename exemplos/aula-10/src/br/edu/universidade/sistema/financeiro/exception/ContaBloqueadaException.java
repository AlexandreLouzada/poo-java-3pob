package br.edu.universidade.sistema.financeiro.exception;

// 2. Exceção de Domínio Não-Checada (Unchecked): Violação de Integridade
public class ContaBloqueadaException extends RuntimeException {
    private final String numeroConta;

    public ContaBloqueadaException(String mensagem, String numeroConta) {
        super(mensagem);
        this.numeroConta = numeroConta;
    }

    public ContaBloqueadaException(String mensagem, String numeroConta, Throwable causa) {
        super(mensagem, causa); // Preserva o encadeamento de falha
        this.numeroConta = numeroConta;
    }

    public String getNumeroConta() {
        return numeroConta;
    }
}
