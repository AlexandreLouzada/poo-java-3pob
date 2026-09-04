import java.util.Locale;

abstract class Forma {
    protected String cor;

    public Forma(String cor) {
        this.cor = cor;
    }

    public String getCor() {
        return cor;
    }

    public void exibirCor() {
        System.out.println("Cor da forma: " + cor);
    }

    // Método abstrato: toda classe filha é obrigada a implementar
    public abstract double calcularArea();
}
