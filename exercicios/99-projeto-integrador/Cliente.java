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

class Cliente {
    private Long id;
    private String razaoSocial;
    private String email;
    private Plano plano;

    public Cliente(Long id, String razaoSocial, String email, Plano plano) {
        this.id = id;
        this.razaoSocial = razaoSocial;
        this.email = email;
        this.plano = plano;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getRazaoSocial() { return razaoSocial; }
    public String getEmail() { return email; }
    public Plano getPlano() { return plano; }
}
