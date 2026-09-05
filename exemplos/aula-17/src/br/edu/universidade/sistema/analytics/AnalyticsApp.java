package br.edu.universidade.sistema.analytics;

import br.edu.universidade.sistema.analytics.dominio.Funcionario;
import br.edu.universidade.sistema.analytics.service.RhAnalyticsService;
import java.util.List;
import java.util.Map;

// 3. Aplicação Executável demonstrando os relatórios de saída
public class AnalyticsApp {
    public static void main(String[] args) {
        List<Funcionario> quadroColaboradores = List.of(
                new Funcionario(101L, "Mariana Silva", "TECNOLOGIA", "ARQUITETA", 9500.00),
                new Funcionario(102L, "Lucas Mendes", "TECNOLOGIA", "DESENVOLVEDOR", 4800.00),
                new Funcionario(103L, "Carlos Prado", "FINANCEIRO", "ANALISTA", 6200.00),
                new Funcionario(104L, "Beatriz Costa", "RH", "COORDENADORA", 7100.00),
                new Funcionario(105L, "Renata Lima", "FINANCEIRO", "DIRETORA", 12500.00),
                new Funcionario(106L, "Thiago Rocha", "TECNOLOGIA", "DESENVOLVEDOR", 5200.00)
        );

        RhAnalyticsService service = new RhAnalyticsService();

        System.out.println("--- 1. Quantidade de Colaboradores por Departamento (counting) ---");
        Map<String, Long> contagem = service.contarColaboradoresPorDepartamento(quadroColaboradores);
        contagem.forEach((depto, total) -> System.out.printf("Depto: %-12s | Total: %d colaboradores%n", depto, total));

        System.out.println("\n--- 2. Custo Total de Folha Salarial por Departamento (summingDouble) ---");
        Map<String, Double> custoPorDepto = service.somarFolhaSalarialPorDepartamento(quadroColaboradores);
        custoPorDepto.forEach((depto, soma) -> System.out.printf("Depto: %-12s | Folha: R$ %10.2f%n", depto, soma));

        System.out.println("\n--- 3. Mapeamento Direto de Nomes por Setor (mapping) ---");
        Map<String, List<String>> nomesPorDepto = service.mapearApenasNomesPorDepartamento(quadroColaboradores);
        nomesPorDepto.forEach((depto, nomes) -> System.out.printf("Depto: %-12s | Membros: %s%n", depto, nomes));

        System.out.println("\n--- 4. Particionamento Salarial Binário (>= R$ 7.000) ---");
        Map<Boolean, List<Funcionario>> particionamento = service.particionarPorTetoSalarial(quadroColaboradores, 7000.00);
        System.out.println(">> Colaboradores com Salário >= R$ 7.000 (Teto Sênior/Liderança):");
        particionamento.get(true).forEach(System.out::println);
        System.out.println(">> Demais Colaboradores:");
        particionamento.get(false).forEach(System.out::println);

        System.out.println("\n--- 5. Concatenação de Nomes via joining ---");
        String relatorioTexto = service.emitirListagemNomesFormatada(quadroColaboradores);
        System.out.println(relatorioTexto);
    }
}
