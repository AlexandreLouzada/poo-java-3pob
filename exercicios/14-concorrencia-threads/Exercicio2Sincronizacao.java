import java.util.concurrent.atomic.AtomicInteger;

public class Exercicio2Sincronizacao {
    public static void main(String[] args) throws InterruptedException {
        ContadorConcorrente contador = new ContadorConcorrente();
        int totalThreads = 5;
        int incrementosPorThread = 1000;

        Thread[] threads = new Thread[totalThreads];

        for (int i = 0; i < totalThreads; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < incrementosPorThread; j++) {
                    contador.incrementarSincronizado();
                    contador.incrementarAtomico();
                }
            });
            threads[i].start();
        }

        for (Thread t : threads) {
            t.join();
        }

        System.out.println("Total esperado: " + (totalThreads * incrementosPorThread));
        System.out.println("Resultado com synchronized: " + contador.getContadorSincronizado());
        System.out.println("Resultado com AtomicInteger: " + contador.getContadorAtomico());
    }
}
