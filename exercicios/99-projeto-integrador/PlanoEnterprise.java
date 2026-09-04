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

class PlanoEnterprise extends Plano {
    public PlanoEnterprise() {
        super("Enterprise", 499.00);
    }

    @Override
    public double calcularFatura(int usuariosAtivos, long gbArmazenados) {
        double descontoProgressivo = (usuariosAtivos > 50) ? 0.90 : 1.0;
        double custoPorUsuario = usuariosAtivos * 8.0;
        double custoArmazenamento = gbArmazenados * 0.20;
        return (mensalidadeBase + custoPorUsuario + custoArmazenamento) * descontoProgressivo;
    }
}
