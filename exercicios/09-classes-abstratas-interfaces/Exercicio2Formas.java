import java.util.Locale;

public class Exercicio2Formas {
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);

        Forma ret = new Retangulo("Azul", 4.0, 5.0);
        Forma circ = new Circulo("Vermelho", 3.0);

        ret.exibirCor();
        System.out.printf("Área do Retângulo: %.2f%n", ret.calcularArea());

        circ.exibirCor();
        System.out.printf("Área do Círculo: %.2f%n", circ.calcularArea());
    }
}
