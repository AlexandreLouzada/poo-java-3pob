package br.edu.universidade.sistema.checkout;

// 5. Camada de Serviço desacoplada: opera exclusivamente sobre abstrações
public class CheckoutService {

    public void processarLote(MeioPagamento[] pagamentos, double valorCobranca) {
        System.out.println("========== INICIANDO PROCESSAMENTO DE CHECKOUT ==========");

        for (MeioPagamento pgto : pagamentos) {
            // Despacho dinâmico: a JVM localiza o método concreto no Heap
            boolean sucesso = pgto.autorizar(valorCobranca);

            if (sucesso) {
                System.out.println("[SUCESSO] " + pgto.emitirComprovante());
            } else {
                System.out.printf("[RECUSADO] TX [%s] - Saldo/Limite insuficiente para valor R$ %.2f%n",
                        pgto.getCodigoTransacao(), valorCobranca);
            }
        }

        System.out.println("=========================================================");
    }
}
