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

class FaturaDAOImpl implements GenericDAO<Fatura, Long> {
    @Override
    public void salvar(Fatura fatura, Connection conn) throws SQLException {
        String sql = "INSERT INTO faturas (cliente_id, valor_total, status, data_faturamento) VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, fatura.getClienteId());
            ps.setDouble(2, fatura.getValorTotal());
            ps.setString(3, fatura.getStatus().name());
            ps.setTimestamp(4, Timestamp.valueOf(fatura.getDataFaturamento()));
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    fatura.setId(rs.getLong(1));
                }
            }
        }
    }

    @Override
    public Optional<Fatura> buscarPorId(Long id, Connection conn) throws SQLException {
        String sql = "SELECT id, cliente_id, valor_total, status, data_faturamento FROM faturas WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Fatura(
                            rs.getLong("id"),
                            rs.getLong("cliente_id"),
                            rs.getDouble("valor_total"),
                            StatusFatura.valueOf(rs.getString("status")),
                            rs.getTimestamp("data_faturamento").toLocalDateTime()
                    ));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public List<Fatura> listarTodos(Connection conn) throws SQLException {
        String sql = "SELECT id, cliente_id, valor_total, status, data_faturamento FROM faturas";
        List<Fatura> faturas = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                faturas.add(new Fatura(
                        rs.getLong("id"),
                        rs.getLong("cliente_id"),
                        rs.getDouble("valor_total"),
                        StatusFatura.valueOf(rs.getString("status")),
                        rs.getTimestamp("data_faturamento").toLocalDateTime()
                ));
            }
        }
        return faturas;
    }
}
