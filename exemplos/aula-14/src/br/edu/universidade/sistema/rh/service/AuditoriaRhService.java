package br.edu.universidade.sistema.rh.service;

import br.edu.universidade.sistema.rh.dominio.Colaborador;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

// 2. Serviço Desacoplado operando através de Comportamentos Funcionais
public class AuditoriaRhService {

    // Método de Alta Ordem: Recebe um Predicate para filtrar elementos de forma dinâmica
    public List<Colaborador> filtrar(List<Colaborador> equipe, Predicate<Colaborador> criterio) {
        List<Colaborador> resultado = new ArrayList<>();
        for (Colaborador c : equipe) {
            if (criterio.test(c)) { // Executa a função booleana injetada pela Lambda
                resultado.add(c);
            }
        }
        return resultado;
    }

    // Método de Transformação: Projeta colaboradores em qualquer outro tipo via Function
    public <R> List<R> mapear(List<Colaborador> equipe, Function<Colaborador, R> transformador) {
        List<R> resultado = new ArrayList<>();
        for (Colaborador c : equipe) {
            resultado.add(transformador.apply(c)); // Aplica a transformação funcional
        }
        return resultado;
    }

    // Método de Ação: Dispara um Consumer em cada registro filtrado
    public void executarAcao(List<Colaborador> equipe, Consumer<Colaborador> acao) {
        for (Colaborador c : equipe) {
            acao.accept(c); // Despacha a ação sem esperar retorno
        }
    }
}
