import java.util.function.Consumer;
import java.util.ArrayList;
import java.util.List;

public class Exercicio5LowerBoundedWildcard {

    // Regra PECS: Producer Extends (origem), Consumer Super (destino)
    public static <T> void copiarElementos(List<? extends T> origem, List<? super T> destino) {
        for (T elemento : origem) {
            destino.add(elemento); // Escrita segura pois destino aceita T ou supertipos de T
        }
    }

    public static void main(String[] args) {
        List<Integer> listaOrigem = List.of(10, 20, 30, 40, 50);

        // Destino aceita Number (superclasse direta de Integer)
        List<Number> destinoNumeros = new ArrayList<>();
        copiarElementos(listaOrigem, destinoNumeros);
        System.out.println("Lista de Number preenchida: " + destinoNumeros);

        // Destino aceita Object (superclasse raiz de Integer)
        List<Object> destinoObjetos = new ArrayList<>();
        copiarElementos(listaOrigem, destinoObjetos);
        System.out.println("Lista de Object preenchida: " + destinoObjetos);
    }
}

