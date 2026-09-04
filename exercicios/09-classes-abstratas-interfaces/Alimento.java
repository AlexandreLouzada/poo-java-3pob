import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class Alimento extends Item {
    public Alimento(int codigo, double precoBase) {
        super(codigo, precoBase);
    }
    // Não implementa Tributavel, pois alimentos são isentos nesta regra
}
