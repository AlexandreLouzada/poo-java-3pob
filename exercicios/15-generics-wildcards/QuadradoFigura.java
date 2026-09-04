import java.util.List;
import java.util.Locale;

class QuadradoFigura extends Figura {
    private final double lado;

    public QuadradoFigura(double lado) {
        this.lado = lado;
    }

    @Override
    public double calcularArea() {
        return lado * lado;
    }
}
