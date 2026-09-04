import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

// ============================================================================
// 3. CAMADA DE SERVIÇOS (Módulos 4, 5, 6 e 7)
// ============================================================================

class IngestaoTelemetriaService {
    public List<TelemetriaConsumo> importarArquivo(Path caminhoCsv) throws TelemetriaException {
        try (Stream<String> linhas = Files.lines(caminhoCsv)) {
            return linhas
                    .skip(1)
                    .filter(linha -> !linha.isBlank())
                    .map(linha -> {
                        String[] cols = linha.split(",");
                        return new TelemetriaConsumo(
                                Long.parseLong(cols[0].trim()),
                                Integer.parseInt(cols[1].trim()),
                                Long.parseLong(cols[2].trim())
                        );
                    })
                    .toList();
        } catch (IOException | NumberFormatException e) {
            throw new TelemetriaException("Falha na ingestão do arquivo de telemetria.", e);
        }
    }
}
