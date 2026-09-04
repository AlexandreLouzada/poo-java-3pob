import java.util.Locale;

class CirculoGeometrico extends FiguraGeometrica {
    private double raio;

    public CirculoGeometrico(double raio) {
        this.raio = raio;
    }

    @Override
    public double calcularArea() {
        return Math.PI * Math.pow(raio, 2);
    }
}
