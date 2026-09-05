package br.edu.universidade.sistema.generics.auditoria;

public interface Auditor<T> {
    boolean auditar(T objeto);
}