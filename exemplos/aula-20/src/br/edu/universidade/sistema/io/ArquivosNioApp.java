package br.edu.universidade.sistema.io;

import br.edu.universidade.sistema.io.dominio.MovimentacaoBancaria;
import br.edu.universidade.sistema.io.service.IngestaoArquivoService;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

// 3. Aplicação Executável demonstrando o ciclo completo de I/O moderno
public class ArquivosNioApp {
    public static void main(String[] args) {
        System.out.println("========== SISTEMA DE INGESTÃO DE ARQUIVOS NIO.2 ==========");

        try {
            // Inicializa o serviço preparando os diretórios em disco
            IngestaoArquivoService service = new IngestaoArquivoService("armazenamento_bancario");

            // Massa de dados inicial em memória
            List<MovimentacaoBancaria> lote = List.of(
                    new MovimentacaoBancaria(1001L, "001-A", "CREDITO", 4500.00, LocalDate.now()),
                    new MovimentacaoBancaria(1002L, "002-B", "DEBITO", 1200.50, LocalDate.now()),
                    new MovimentacaoBancaria(1003L, "001-A", "CREDITO", 3800.00, LocalDate.now()),
                    new MovimentacaoBancaria(1004L, "003-C", "DEBITO", 650.00, LocalDate.now())
            );

            // 1. Exportação física para arquivo .csv
            String nomeArquivoLote = "lote_diario.csv";
            Path arquivoGerado = service.exportarMovimentacoes(lote, nomeArquivoLote);
            System.out.println("Arquivo gravado com sucesso em: " + arquivoGerado.toAbsolutePath());

            // 2. Leitura sob demanda (Streaming) calculando totalização
            double totalCreditos = service.calcularVolumeTotalCreditos(arquivoGerado);
            System.out.printf("Total consolidado de créditos processados: R$ %.2f%n", totalCreditos);

            // 3. Arquivamento seguro (Movimentação física com timestamp)
            Path arquivoArquivado = service.arquivarArquivoProcessado(arquivoGerado);
            System.out.println("Arquivo movido para pasta de backup: " + arquivoArquivado.toAbsolutePath());

            System.out.println("\nProcessamento concluído com estabilidade estrutural.");

        } catch (IOException ex) {
            System.err.println("Falha operacional no sistema de arquivos: " + ex.getMessage());
            ex.printStackTrace();
        }
        System.out.println("============================================================");
    }
}
