import java.util.concurrent.atomic.AtomicInteger;

class ContadorConcorrente {
    private int contadorSincronizado = 0;
    private final AtomicInteger contadorAtomico = new AtomicInteger(0);

    // Abordagem 1: Método sincronizado (bloqueio por monitor)
    public synchronized void incrementarSincronizado() {
        contadorSincronizado++;
    }

    // Abordagem 2: Operação atômica sem lock (CAS - Compare-And-Swap)
    public void incrementarAtomico() {
        contadorAtomico.incrementAndGet();
    }

    public int getContadorSincronizado() {
        return contadorSincronizado;
    }

    public int getContadorAtomico() {
        return contadorAtomico.get();
    }
}
