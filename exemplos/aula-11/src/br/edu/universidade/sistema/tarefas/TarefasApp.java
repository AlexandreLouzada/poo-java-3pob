package br.edu.universidade.sistema.tarefas;

import br.edu.universidade.sistema.tarefas.dominio.Tarefa;
import br.edu.universidade.sistema.tarefas.service.GerenciadorTarefasService;

// 3. Aplicação Executável demonstrando o ciclo de operações em List
public class TarefasApp {
    public static void main(String[] args) {
        GerenciadorTarefasService service = new GerenciadorTarefasService();

        System.out.println("--- 1. Inserção de Elementos Dinâmicos ---");
        service.adicionarTarefa(new Tarefa(103L, "Implementar autenticação JWT"));
        service.adicionarTarefa(new Tarefa(101L, "Configurar pool de conexões"));
        service.adicionarTarefa(new Tarefa(102L, "Atualizar documentação Javadoc"));

        System.out.printf("Total de tarefas ativas: %d%n", service.getTotalTarefas());
        service.listarTodas().forEach(System.out::println);

        System.out.println("\n--- 2. Acesso Direto e Atualização por Índice ---");
        Tarefa primeira = service.buscarPorIndice(0);
        System.out.println("Tarefa no índice 0: " + primeira);
        primeira.marcarComoConcluida();

        System.out.println("\n--- 3. Exclusão Dinâmica de Tarefa ---");
        boolean removida = service.removerTarefaPorId(101L);
        System.out.println("Tarefa #101 removida com sucesso? " + removida);
        System.out.printf("Total de tarefas remanescentes: %d%n", service.getTotalTarefas());

        System.out.println("\n--- 4. Ordenação Alfabética via Collections.sort ---");
        service.ordenarPorTitulo();
        service.listarTodas().forEach(System.out::println);
    }
}
