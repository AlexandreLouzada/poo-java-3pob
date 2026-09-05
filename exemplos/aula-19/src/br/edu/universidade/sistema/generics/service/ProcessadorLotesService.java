package br.edu.universidade.sistema.generics.service;

import br.edu.universidade.sistema.generics.dominio.Produto;
import java.util.List;

// 5. Serviço com Métodos Genéricos aplicando o Princípio PECS
public class ProcessadorLotesService {

    /**
     * Aplica o princípio PECS:
     * - 'origem' é PRODUTORA (Producer Extends): nós lemos elementos dela.
     * - 'destino' é CONSUMIDORA (Consumer Super): nós inserimos elementos nela.
     */
    public static <T> void transferirElementos(
            List<? extends T> origem,  // Producer Extends: aceita T ou qualquer subclasse
            List<? super T> destino    // Consumer Super: aceita T ou qualquer superclasse
    ) {
        for (T elemento : origem) {
            destino.add(elemento); // Operação segura garantida pelo compilador
        }
    }

    // Método com Bounded Wildcard para calcular somas numéricas polimórficas
    public static double calcularPrecoMedio(List<? extends Produto> produtos) {
        if (produtos == null || produtos.isEmpty()) {
            return 0.0;
        }
        double soma = 0.0;
        for (Produto p : produtos) { // Seguro ler como Produto
            soma += p.getPreco();
        }
        return soma / produtos.size();
    }
}
