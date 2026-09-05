package br.edu.universidade.sistema.generics.dominio;

// 1. Contrato Base de Domínio com Identificador Genérico
public abstract class EntidadeBase<ID> {
    private final ID id;

    public EntidadeBase(ID id) {
        if (id == null) {
            throw new IllegalArgumentException("O identificador não pode ser nulo.");
        }
        this.id = id;
    }

    public ID getId() {
        return id;
    }
}
