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

class PlanoBasico extends Plano {
    private static final int LIMITE_USUARIOS_INCLUSOS = 5;
    private static final double VALOR_USUARIO_EXTRA = 15.0;

    public PlanoBasico() {
        super("Básico", 99.00);
    }

    @Override
    public double calcularFatura(int usuariosAtivos, long gbArmazenados) {
        double extraUsuarios = Math.max(0, usuariosAtivos - LIMITE_USUARIOS_INCLUSOS) * VALOR_USUARIO_EXTRA;
        double custoArmazenamento = gbArmazenados * 0.50;
        return mensalidadeBase + extraUsuarios + custoArmazenamento;
    }
}
