package br.edu.universidade.sistema.acesso.service;

import br.edu.universidade.sistema.acesso.dominio.CrachaFuncionario;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

public class ControleAcessoPredialService {

    private final Set<CrachaFuncionario> presentesHoje = new HashSet<>();
    private final Set<CrachaFuncionario> cadastradosGeral = new LinkedHashSet<>();

    public void cadastrarColaborador(CrachaFuncionario c) {
        if (c == null) {
            throw new IllegalArgumentException("Colaborador nulo nao pode ser cadastrado.");
        }
        cadastradosGeral.add(c);
    }

    public boolean registrarEntrada(CrachaFuncionario c) {
        if (c == null) {
            return false;
        }
        if (cadastradosGeral.contains(c) && presentesHoje.add(c)) {
            return true;
        }
        return false;
    }

    public boolean registrarSaida(CrachaFuncionario c) {
        if (c == null) {
            return false;
        }
        return presentesHoje.remove(c);
    }

    public Set<CrachaFuncionario> obterPresentesOrdenados() {
        return new TreeSet<>(presentesHoje);
    }

    public Set<CrachaFuncionario> obterAusentes() {
        Set<CrachaFuncionario> ausentes = new HashSet<>(cadastradosGeral);
        ausentes.removeAll(presentesHoje);
        return ausentes;
    }

    public int getTotalPresentes() {
        return presentesHoje.size();
    }

    public int getTotalCadastrados() {
        return cadastradosGeral.size();
    }
}