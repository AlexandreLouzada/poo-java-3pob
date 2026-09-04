import java.util.Map;
import java.util.stream.Stream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Exercicio5InterfacesNativas {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        List<FuncionarioEmpresa> funcionarios = Arrays.asList(
                new FuncionarioEmpresa("Alice", "TI", 5000.0),
                new FuncionarioEmpresa("Bruno", "RH", 4000.0),
                new FuncionarioEmpresa("Carla", "TI", 6500.0),
                new FuncionarioEmpresa("Daniel", "Financeiro", 4800.0),
                new FuncionarioEmpresa("Eduardo", "TI", 8000.0)
        );

        // 1. Predicate: Critério de filtragem
        Predicate<FuncionarioEmpresa> apenasTI = f -> "TI".equalsIgnoreCase(f.getDepartamento());

        // 2. Function: Regra de transformação (aumento projetado de 10%)
        Function<FuncionarioEmpresa, Double> calcularAumento = f -> f.getSalario() * 1.10;

        // 3. Consumer: Ação final sobre o resultado
        Consumer<Double> imprimirSalarioProjetado = sal -> System.out.printf("Salário Reajustado: R$ %.2f%n", sal);

        System.out.println("--- Projeção Salarial (Departamento de TI) ---");
        funcionarios.stream()
                .filter(apenasTI)
                .map(calcularAumento)
                .forEach(imprimirSalarioProjetado);
    }
}

