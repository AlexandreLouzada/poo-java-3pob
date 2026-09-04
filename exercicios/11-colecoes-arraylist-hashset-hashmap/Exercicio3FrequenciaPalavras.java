import java.util.HashMap;
import java.util.Map;

public class Exercicio3FrequenciaPalavras {
    public static void main(String[] args) {
        String texto = "java é bom java é rápido java é tipado";

        // Converte para minúsculas e divide por um ou mais espaços em branco
        String[] palavras = texto.toLowerCase().split("\\s+");

        Map<String, Integer> frequencia = new HashMap<>();

        for (String palavra : palavras) {
            frequencia.put(palavra, frequencia.getOrDefault(palavra, 0) + 1);
        }

        System.out.println("Frequência de ocorrência das palavras:");
        for (Map.Entry<String, Integer> entry : frequencia.entrySet()) {
            System.out.printf("- %s: %d vez(es)%n", entry.getKey(), entry.getValue());
        }
    }
}
