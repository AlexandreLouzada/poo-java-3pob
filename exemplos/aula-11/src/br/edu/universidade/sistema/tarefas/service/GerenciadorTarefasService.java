package br.edu.universidade.sistema.tarefas.service;

import br.edu.universidade.sistema.tarefas.dominio.Tarefa;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// 2. Serviço de Gerenciamento desacoplado operando sobre a interface List
public class GerenciadorTarefasService {
    // Declarado como List (Interface) e instanciado como ArrayList
    private final List<Tarefa> repositorioTarefas;

    public GerenciadorTarefasService() {
        this.repositorioTarefas = new ArrayList<>();
    }

    public void adicionarTarefa(Tarefa tarefa) {
        if (tarefa == null) {
            throw new IllegalArgumentException("Não é permitido inserir registros nulos.");
        }
        if (repositorioTarefas.contains(tarefa)) {
            throw new IllegalStateException("Já existe uma tarefa cadastrada com o ID: " + tarefa.getId());
        }
        repositorioTarefas.add(tarefa);
    }

    public Tarefa buscarPorIndice(int indice) {
        if (indice < 0 || indice >= repositorioTarefas.size()) {
            throw new IndexOutOfBoundsException("Posição solicitada fora do intervalo: " + indice);
        }
        return repositorioTarefas.get(indice);
    }

    public boolean removerTarefaPorId(Long id) {
        // Remove utilizando correspondência pelo equals da classe de domínio
        return repositorioTarefas.removeIf(t -> t.getId().equals(id));
    }

    public void ordenarPorTitulo() {
        // Algoritmo nativo da API que delega para o compareTo da Tarefa
        Collections.sort(repositorioTarefas);
    }

    public List<Tarefa> listarTodas() {
        // Retorna uma cópia defensiva para proteger a lista interna contra mutações externas
        return new ArrayList<>(repositorioTarefas);
    }

    public int getTotalTarefas() {
        return repositorioTarefas.size();
    }
}
