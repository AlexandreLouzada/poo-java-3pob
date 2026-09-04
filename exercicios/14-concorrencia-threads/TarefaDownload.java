import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

class TarefaDownload implements Callable<String> {
    private final int id;
    private static final Random RANDOM = new Random();

    public TarefaDownload(int id) {
        this.id = id;
    }

    @Override
    public String call() throws Exception {
        int tempoEspera = 500 + RANDOM.nextInt(1000);
        Thread.sleep(tempoEspera);
        return String.format("Arquivo #%d processado pela thread [%s] em %d ms",
                id, Thread.currentThread().getName(), tempoEspera);
    }
}
