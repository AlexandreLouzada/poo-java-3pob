import java.util.Objects;

public class Exercicio2ParesGenericos {
    public static void main(String[] args) {
        Par<Integer, String> p1 = new Par<>(1, "Ativo");
        Par<Integer, String> p2 = new Par<>(1, "Ativo");
        Par<Integer, String> p3 = new Par<>(2, "Inativo");

        System.out.println("Par 1: " + p1);
        System.out.println("Par 2: " + p2);
        System.out.println("Par 3: " + p3);

        System.out.println("p1 é igual a p2? " + Par.saoIguais(p1, p2));
        System.out.println("p1 é igual a p3? " + Par.saoIguais(p1, p3));
    }
}
