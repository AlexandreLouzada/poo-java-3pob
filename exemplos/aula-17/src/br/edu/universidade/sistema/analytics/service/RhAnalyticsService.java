package br.edu.universidade.sistema.analytics.service;

import br.edu.universidade.sistema.analytics.dominio.Funcionario;
import java.util.*;
import java.util.stream.Collectors;

// 2. Serviço de Auditoria Analítica utilizando Coletas Avançadas
public class RhAnalyticsService {

    // 1. Agrupamento Simples 1:N -> Departamento mapeando para Lista de Funcionários
    public Map<String, List<Funcionario>> agruparPorDepartamento(List<Funcionario> funcionarios) {
        return funcionarios.stream()
                .collect(Collectors.groupingBy(Funcionario::getDepartamento));
    }

    // 2. Agrupamento com Downstream de Contagem -> Departamento mapeando para Quantidade
    public Map<String, Long> contarColaboradoresPorDepartamento(List<Funcionario> funcionarios) {
        return funcionarios.stream()
                .collect(Collectors.groupingBy(
                        Funcionario::getDepartamento,
                        Collectors.counting()
                ));
    }

    // 3. Agrupamento com Downstream Aritmético -> Departamento mapeando para Custo Total
    public Map<String, Double> somarFolhaSalarialPorDepartamento(List<Funcionario> funcionarios) {
        return funcionarios.stream()
                .collect(Collectors.groupingBy(
                        Funcionario::getDepartamento,
                        Collectors.summingDouble(Funcionario::getSalarioMensal)
                ));
    }

    // 4. Agrupamento com Transformação (Mapping) -> Departamento mapeando apenas para os Nomes
    public Map<String, List<String>> mapearApenasNomesPorDepartamento(List<Funcionario> funcionarios) {
        return funcionarios.stream()
                .collect(Collectors.groupingBy(
                        Funcionario::getDepartamento,
                        Collectors.mapping(Funcionario::getNome, Collectors.toList())
                ));
    }

    // 5. Particionamento Booleano -> Divide a folha entre quem ganha acima de R$ 7.000 e quem não ganha
    public Map<Boolean, List<Funcionario>> particionarPorTetoSalarial(List<Funcionario> funcionarios, double teto) {
        return funcionarios.stream()
                .collect(Collectors.partitioningBy(f -> f.getSalarioMensal() >= teto));
    }

    // 6. Concatenação de Nomes via joining -> Emite lista corrida para auditoria
    public String emitirListagemNomesFormatada(List<Funcionario> funcionarios) {
        return funcionarios.stream()
                .map(Funcionario::getNome)
                .collect(Collectors.joining(", ", "EQUIPE: [", "]"));
    }
}
