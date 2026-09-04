import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Exercicio3BoundedType {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        // Operando com Inteiros
        CalculadoraEstatistica<Integer> calcInt = new CalculadoraEstatistica<>();
        calcInt.adicionar(10);
        calcInt.adicionar(25);
        calcInt.adicionar(35);
        System.out.printf("Inteiros -> Média: %.2f | Maior: %.2f%n", 
                          calcInt.calcularMedia(), calcInt.calcularMaior());

        // Operando com Números Decimais
        CalculadoraEstatistica<Double> calcDouble = new CalculadoraEstatistica<>();
        calcDouble.adicionar(4.5);
        calcDouble.adicionar(9.2);
        calcDouble.adicionar(7.8);
        System.out.printf("Decimais -> Média: %.2f | Maior: %.2f%n", 
                          calcDouble.calcularMedia(), calcDouble.calcularMaior());

        // CalculadoraEstatistica<String> erro = new CalculadoraEstatistica<>(); 
        // Erro de compilação: String não estende Number
    }
}
