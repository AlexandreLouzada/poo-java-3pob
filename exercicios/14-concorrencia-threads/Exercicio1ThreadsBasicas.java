public class Exercicio1ThreadsBasicas {
    public static void main(String[] args) {
        Runnable contadorCrescente = () -> {
            for (int i = 1; i <= 10; i++) {
                System.out.println("[Crescente] " + i);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };

        Runnable contadorRegressivo = () -> {
            for (int i = 10; i >= 1; i--) {
                System.out.println("  [Regressivo] " + i);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };

        Thread t1 = new Thread(contadorCrescente);
        Thread t2 = new Thread(contadorRegressivo);

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("\n>>> Contagens finalizadas!");
    }
}
