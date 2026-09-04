import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Exercicio5Tributos {
    public static void calcularTotalImpostos(List<Tributavel> itensTributaveis) {
        double total = 0.0;
        for (Tributavel t : itensTributaveis) {
            total += t.calcularTributo();
        }
        System.out.printf("Total de impostos a recolher: R$ %.2f%n", total);
    }

    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        Eletronico tv = new Eletronico(1, 2000.0);
        Eletronico celular = new Eletronico(2, 1000.0);
        Alimento maca = new Alimento(3, 5.0); // Instanciado, mas isento

        List<Tributavel> itensParaTributar = new ArrayList<>();
        itensParaTributar.add(tv);
        itensParaTributar.add(celular);
        // itensParaTributar.add(maca); // Erro de compilação: Alimento não é Tributavel

        calcularTotalImpostos(itensParaTributar);
    }
}
