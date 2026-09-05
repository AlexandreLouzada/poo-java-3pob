package br.edu.universidade.util;

/**
 * Fornece rotinas utilitárias para validação de documentos fiscais.
 * <p>Esta classe não pode ser instanciada.</p>
 *
 * @author Prof. Dr.
 * @version 1.0.0
 * @since 1.0.0
 */
public final class ValidadorDocumento {

    /**
     * Construtor privado para impedir a instanciação da classe utilitária.
     */
    private ValidadorDocumento() {}

    /**
     * Valida o formato e os dígitos de uma cadeia de CPF.
     *
     * @param cpf texto contendo os dígitos do CPF (com ou sem máscara).
     * @return {@code true} se for válido; {@code false} caso contrário ou se nulo.
     * @throws IllegalArgumentException se contiver letras não permitidas.
     * @see #sanitizar(String)
     */
    public static boolean isCpfValido(String cpf) {
        if (cpf == null) return false;
        String limpo = sanitizar(cpf);
        return limpo.length() == 11;
    }

    /**
     * Remove quaisquer caracteres não numéricos de uma cadeia de texto.
     *
     * @param texto cadeia de caracteres de entrada a ser filtrada.
     * @return texto contendo unicamente dígitos decimais, ou cadeia vazia se nulo.
     */
    public static String sanitizar(String texto) {
        return texto == null ? "" : texto.replaceAll("\\D", "");
    }
}
