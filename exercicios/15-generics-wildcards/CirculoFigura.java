import java.util.List;
import java.util.Locale;

class CirculoFigura extends Figura {
    private final double raio;

    public CirculoFigura(double raio) {
        this.raio = raio;
    }

    @Override
    public double calcularArea() {
        return Math.PI * Math.pow(raio, 2);
    }
}
