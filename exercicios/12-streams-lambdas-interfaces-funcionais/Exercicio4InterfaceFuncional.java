import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Exercicio4InterfaceFuncional {

    public static List<String> processarLista(List<String> lista, TransformadorTexto regra) {
        List<String> resultado = new ArrayList<>();
        for (String item : lista) {
            resultado.add(regra.transformar(item));
        }
        return resultado;
    }

    public static void main(String[] args) {
        List<String> termos = Arrays.asList("java", "streams", "lambda", "programação");

        // Implementação 1: Method Reference (Caixa Alta)
        List<String> maiusculas = processarLista(termos, String::toUpperCase);
        System.out.println("Caixa Alta: " + maiusculas);

        // Implementação 2: Expressão Lambda (Inverter String)
        List<String> invertidas = processarLista(termos, s -> new StringBuilder(s).reverse().toString());
        System.out.println("Invertidas: " + invertidas);

        // Implementação 3: Expressão Lambda (Adicionar delimitadores)
        List<String> delimitadas = processarLista(termos, s -> "[" + s + "]");
        System.out.println("Delimitadas: " + delimitadas);
    }
}
