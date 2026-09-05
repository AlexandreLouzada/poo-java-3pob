package br.edu.universidade.sistema.generics.repositorio;

import br.edu.universidade.sistema.generics.dominio.EntidadeBase;
import java.util.*;

// 4. Repositório Genérico em Memória com Limite de Tipo (Bounded Type)
public class RepositorioGenerico<T extends EntidadeBase<ID>, ID> {
    private final Map<ID, T> bancoEmMemoria = new HashMap<>();

    public void salvar(T entidade) {
        if (entidade == null) {
            throw new IllegalArgumentException("Entidade nula não pode ser persistida.");
        }
        bancoEmMemoria.put(entidade.getId(), entidade);
    }

    public Optional<T> buscarPorId(ID id) {
        return Optional.ofNullable(bancoEmMemoria.get(id));
    }

    public List<T> listarTodos() {
        return new ArrayList<>(bancoEmMemoria.values());
    }

    public boolean excluir(ID id) {
        return bancoEmMemoria.remove(id) != null;
    }

    public int totalRegistros() {
        return bancoEmMemoria.size();
    }
}
