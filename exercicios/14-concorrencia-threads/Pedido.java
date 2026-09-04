import java.util.Locale;
import java.util.concurrent.CompletableFuture;

class Pedido {
    private final String id;
    private final double subtotal;

    public Pedido(String id, double subtotal) {
        this.id = id;
        this.subtotal = subtotal;
    }

    public String getId() {
        return id;
    }

    public double getSubtotal() {
        return subtotal;
    }
}
