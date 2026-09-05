package br.edu.universidade.sistema.telemetria;

import br.edu.universidade.sistema.telemetria.dominio.SinalSensor;
import br.edu.universidade.sistema.telemetria.service.AuditoriaTelemetriaService;
import java.util.ArrayList;
import java.util.List;

public class TelemetriaApp {
    public static void main(String[] args) {
        AuditoriaTelemetriaService service = new AuditoriaTelemetriaService();

        System.out.println("--- Geracao da Massa de 500.000 Sinais IoT ---");
        final int total = 500_000;
        List<SinalSensor> sinais = new ArrayList<>(total);
        for (int i = 0; i < total; i++) {
            boolean critico = (i % 3 == 0);
            double temperatura = 20.0 + (i % 40);
            sinais.add(new SinalSensor((long) i, String.format("AA:BB:CC:%02X:%02X:%02X",
                    (i >> 16) & 0xFF, (i >> 8) & 0xFF, i & 0xFF), temperatura, critico));
        }
        System.out.println("Sinais gerados: " + sinais.size());

        System.out.println("\n--- Execucao SEQUENCIAL ---");
        long inicioSeq = System.currentTimeMillis();
        double mediaSeq = service.consolidarMediaTermicaSequencial(sinais);
        long fimSeq = System.currentTimeMillis();
        long tempoSeq = fimSeq - inicioSeq;

        System.out.println("\n--- Execucao PARALELA ---");
        long inicioPar = System.currentTimeMillis();
        double mediaPar = service.consolidarMediaTermicaParalelo(sinais);
        long fimPar = System.currentTimeMillis();
        long tempoPar = fimPar - inicioPar;

        System.out.printf("%nMedia termica (sequencial): %.6f | Tempo: %d ms%n", mediaSeq, tempoSeq);
        System.out.printf("Media termica (paralela)  : %.6f | Tempo: %d ms%n", mediaPar, tempoPar);
        System.out.printf("Resultados equivalentes (tolerancia 1e-9): %b%n",
                (Math.abs(mediaSeq - mediaPar) < 1e-9));

        double speedup = (double) tempoSeq / Math.max(tempoPar, 1);
        System.out.printf("Speedup obtido: %.2fx%n", speedup);

        System.out.println("\n--- Coleta de MACs Suspeitos (paralela, distintos) ---");
        long inicioMac = System.currentTimeMillis();
        List<String> macsSuspeitos = service.coletarEnderecosMacSuspeitosParalelo(sinais, 2000.0);
        long fimMac = System.currentTimeMillis();
        System.out.printf("Quantidade de enderecos MAC coletados: %d | Tempo: %d ms%n",
                macsSuspeitos.size(), (fimMac - inicioMac));
    }
}