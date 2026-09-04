import java.io.IOException;

// Exceção de alto nível (Camada de Negócio/Aplicação)
class ProcessamentoDadosException extends Exception {
    public ProcessamentoDadosException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
