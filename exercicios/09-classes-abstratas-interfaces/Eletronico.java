import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class Eletronico extends Item implements Tributavel {
    public Eletronico(int codigo, double precoBase) {
        super(codigo, precoBase);
    }

    @Override
    public double calcularTributo() {
        return getPrecoBase() * 0.15; // 15% de imposto
    }
}
