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

abstract class Plano {
    protected String nome;
    protected double mensalidadeBase;

    public Plano(String nome, double mensalidadeBase) {
        this.nome = nome;
        this.mensalidadeBase = mensalidadeBase;
    }

    public String getNome() { return nome; }
    public double getMensalidadeBase() { return mensalidadeBase; }

    public abstract double calcularFatura(int usuariosAtivos, long gbArmazenados);
}
