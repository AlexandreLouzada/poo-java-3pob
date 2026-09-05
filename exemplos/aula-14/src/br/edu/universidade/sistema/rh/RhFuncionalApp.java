package br.edu.universidade.sistema.rh;

import br.edu.universidade.sistema.rh.dominio.Colaborador;
import br.edu.universidade.sistema.rh.service.AuditoriaRhService;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

// 3. Aplicação Executável demonstrando Composição Funcional
public class RhFuncionalApp {
    public static void main(String[] args) {
        List<Colaborador> equipe = List.of(
                new Colaborador(101L, "Mariana Silva", "TI", 8500.00, 6),
                new Colaborador(102L, "Lucas Mendes", "TI", 4200.00, 2),
                new Colaborador(103L, "Carlos Prado", "FINANCEIRO", 6100.00, 8),
                new Colaborador(104L, "Beatriz Souza", "RH", 3800.00, 1),
                new Colaborador(105L, "Renata Lima", "FINANCEIRO", 9200.00, 10)
        );

        AuditoriaRhService service = new AuditoriaRhService();

        System.out.println("--- 1. Filtragem com Predicate Simples e Encadeado ---");
        // Predicados atômicos e reutilizáveis
        Predicate<Colaborador> ehDeTi = c -> c.getDepartamento().equalsIgnoreCase("TI");
        Predicate<Colaborador> ehSenior = c -> c.getTempoServicoAnos() >= 5;

        // Composição booleana funcional usando o método default 'and()'
        List<Colaborador> tiSeniors = service.filtrar(equipe, ehDeTi.and(ehSenior));
        tiSeniors.forEach(c -> System.out.println(c));

        System.out.println("\n--- 2. Transformação e Projeção com Function ---");
        // Extrai apenas os nomes em letras maiúsculas
        Function<Colaborador, String> crachaFormatter = c ->
                String.format("CRACHÁ: %s (%s)", c.getNome().toUpperCase(), c.getDepartamento());

        List<String> crachas = service.mapear(equipe, crachaFormatter);
        crachas.forEach(s -> System.out.println(s));

        System.out.println("\n--- 3. Aplicação de Efeito Colateral com Consumer ---");
        // Regra de Bonificação: Concede bônus de R$ 500 para colaboradores com mais de 7 anos
        Predicate<Colaborador> aptosBonus = c -> c.getTempoServicoAnos() >= 7;
        Consumer<Colaborador> concederBonus = c -> {
            c.aplicarAumento(500.00);
            System.out.printf("Bônus aplicado para %s! Novo Salário: R$ %.2f%n",
                    c.getNome(), c.getSalarioBase());
        };

        List<Colaborador> veteranos = service.filtrar(equipe, aptosBonus);
        service.executarAcao(veteranos, concederBonus);
    }
}
