package br.edu.universidade.sistema.auditoria;

public class CheckoutApp {

    public static void main(String[] args) {
        try {
            processarTransacao(null, "1234-A");
        } catch (RuntimeException ex) {
            // Em vez de finalizar a JVM, isolamos a falha e emitimos o diagnóstico
            ServicoAuditoriaForense.emitirLaudoTecnico(ex);
        }

        System.out.println("O sistema continuou executando normalmente após o diagnóstico.");
    }

    public static void processarTransacao(String codigoItem, String contaDestino) {
        validarParametros(codigoItem);
        System.out.println("Transação autorizada com sucesso!");
    }

    public static void validarParametros(String codigo) {
        // Disparo deliberado de NullPointerException na invocação de trim()
        if (codigo.trim().isEmpty()) {
            throw new IllegalArgumentException("O código não pode estar vazio.");
        }
    }
}
