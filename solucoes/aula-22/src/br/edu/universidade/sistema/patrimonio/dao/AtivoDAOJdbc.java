package br.edu.universidade.sistema.patrimonio.dao;

import br.edu.universidade.sistema.patrimonio.dominio.AtivoPatrimonial;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AtivoDAOJdbc implements AtivoDAO {

    private final Connection conexao;

    public AtivoDAOJdbc(Connection conexao) {
        this.conexao = conexao;
    }

    @Override
    public void salvar(AtivoPatrimonial ativo) throws SQLException {
        String sql = "INSERT INTO ativos (tombo, descricao, valor_aquisicao, status) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, ativo.getTombo());
            stmt.setString(2, ativo.getDescricao());
            stmt.setDouble(3, ativo.getValorAquisicao());
            stmt.setString(4, ativo.getStatus());
            stmt.executeUpdate();

            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                if (chaves.next()) {
                    ativo.setId(chaves.getLong(1));
                }
            }
        }
    }

    @Override
    public Optional<AtivoPatrimonial> buscarPorTombo(String tombo) throws SQLException {
        String sql = "SELECT id, tombo, descricao, valor_aquisicao, status FROM ativos WHERE tombo = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, tombo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(montarAtivo(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public void atualizarStatus(Long id, String novoStatus) throws SQLException {
        String sql = "UPDATE ativos SET status = ? WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, novoStatus);
            stmt.setLong(2, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public List<AtivoPatrimonial> listarAtivosEmOperacao() throws SQLException {
        String sql = "SELECT id, tombo, descricao, valor_aquisicao, status FROM ativos WHERE status = 'ATIVO'";
        List<AtivoPatrimonial> ativos = new ArrayList<>();
        try (PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                ativos.add(montarAtivo(rs));
            }
        }
        return ativos;
    }

    private AtivoPatrimonial montarAtivo(ResultSet rs) throws SQLException {
        return new AtivoPatrimonial(
                rs.getLong("id"),
                rs.getString("tombo"),
                rs.getString("descricao"),
                rs.getDouble("valor_aquisicao"),
                rs.getString("status")
        );
    }
}