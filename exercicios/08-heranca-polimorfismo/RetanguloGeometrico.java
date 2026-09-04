import java.util.Locale;

class RetanguloGeometrico extends FiguraGeometrica {
    private double largura;
    private double altura;

    public RetanguloGeometrico(double largura, double altura) {
        this.largura = largura;
        this.altura = altura;
    }

    @Override
    public double calcularArea() {
        return largura * altura;
    }
}
