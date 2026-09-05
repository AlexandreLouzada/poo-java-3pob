package br.edu.universidade.sistema.checkout;

// 4. Subclasse Concreta 2: Pagamento Pix
public class PagamentoPix extends MeioPagamento {
    private final String chavePixDestino;

    public PagamentoPix(String codigoTransacao, String chavePixDestino) {
        super(codigoTransacao); // Invocação encadeada da superclasse
        this.chavePixDestino = chavePixDestino;
    }

    @Override
    public boolean autorizar(double valor) {
        if (valor > 0.0) {
            this.valorAutorizado = valor; // Pix isento de tarifa no modelo de negócio
            return true;
        }
        return false;
    }

    @Override
    public String emitirComprovante() {
        return String.format("TX [%s] - PIX: Transferido R$ %.2f para Chave [%s] instantaneamente.",
                codigoTransacao, valorAutorizado, chavePixDestino);
    }
}
