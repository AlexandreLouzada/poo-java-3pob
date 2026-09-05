package br.edu.universidade.sistema.tarefas.dominio;

import java.util.Objects;

// 1. Entidade de Domínio representando uma Tarefa
public class Tarefa implements Comparable<Tarefa> {
    private final Long id;
    private String titulo;
    private boolean concluida;

    public Tarefa(Long id, String titulo) {
        if (id == null || titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("Identificador e título são de fornecimento obrigatório.");
        }
        this.id = id;
        this.titulo = titulo.trim();
        this.concluida = false;
    }

    public Long getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("O título não pode ser redefinido para vazio.");
        }
        this.titulo = titulo.trim();
    }

    public boolean isConcluida() {
        return concluida;
    }

    public void marcarComoConcluida() {
        this.concluida = true;
    }

    // Critério de igualdade semântica baseado no ID
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Tarefa outra = (Tarefa) obj;
        return Objects.equals(this.id, outra.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // Regra de ordenação natural alfabética pelo título
    @Override
    public int compareTo(Tarefa outra) {
        return this.titulo.compareToIgnoreCase(outra.titulo);
    }

    @Override
    public String toString() {
        return String.format("[%s] #%03d - %s",
                concluida ? "CONCLUÍDA" : "PENDENTE ", id, titulo);
    }
}
