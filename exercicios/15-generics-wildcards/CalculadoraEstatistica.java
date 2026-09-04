import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class CalculadoraEstatistica<T extends Number> {
    private final List<T> numeros = new ArrayList<>();

    public void adicionar(T numero) {
        if (numero != null) {
            numeros.add(numero);
        }
    }

    public double calcularMedia() {
        if (numeros.isEmpty()) return 0.0;
        double soma = 0.0;
        for (T n : numeros) {
            soma += n.doubleValue(); // Método garantido por Number
        }
        return soma / numeros.size();
    }

    public double calcularMaior() {
        if (numeros.isEmpty()) return 0.0;
        double maior = numeros.get(0).doubleValue();
        for (T n : numeros) {
            if (n.doubleValue() > maior) {
                maior = n.doubleValue();
            }
        }
        return maior;
    }
}
