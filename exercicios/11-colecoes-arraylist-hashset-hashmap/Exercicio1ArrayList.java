import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Exercicio1ArrayList {
    public static void main(String[] args) {
        List<String> tarefas = new ArrayList<>();

        tarefas.add("Estudar Java");
        tarefas.add("Fazer compras");
        tarefas.add("Treinar na academia");
        tarefas.add("Revisar artigo científico");
        tarefas.add("Organizar ambiente de trabalho");

        System.out.println("Lista inicial: " + tarefas);

        // Remover por nome ou por índice
        tarefas.remove("Fazer compras");
        System.out.println("Após remoção: " + tarefas);

        // Verificação de existência
        String busca = "Estudar Java";
        if (tarefas.contains(busca)) {
            System.out.println("A tarefa \"" + busca + "\" está na lista.");
        }

        // Ordenação alfabética
        Collections.sort(tarefas);
        System.out.println("Lista ordenada alfabeticamente:");
        for (int i = 0; i < tarefas.size(); i++) {
            System.out.printf("%d. %s%n", (i + 1), tarefas.get(i));
        }
    }
}
