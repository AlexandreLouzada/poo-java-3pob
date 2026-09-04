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

class Fatura {
    private Long id;
    private Long clienteId;
    private double valorTotal;
    private StatusFatura status;
    private LocalDateTime dataFaturamento;

    public Fatura(Long clienteId, double valorTotal, StatusFatura status) {
        this.clienteId = clienteId;
        this.valorTotal = valorTotal;
        this.status = status;
        this.dataFaturamento = LocalDateTime.now();
    }

    public Fatura(Long id, Long clienteId, double valorTotal, StatusFatura status, LocalDateTime data) {
        this.id = id;
        this.clienteId = clienteId;
        this.valorTotal = valorTotal;
        this.status = status;
        this.dataFaturamento = data;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getClienteId() { return clienteId; }
    public double getValorTotal() { return valorTotal; }
    public StatusFatura getStatus() { return status; }
    public void setStatus(StatusFatura status) { this.status = status; }
    public LocalDateTime getDataFaturamento() { return dataFaturamento; }

    @Override
    public String toString() {
        return String.format("Fatura #%d | Cliente ID: %d | Total: R$ %.2f | Status: %s",
                id, clienteId, valorTotal, status);
    }
}
