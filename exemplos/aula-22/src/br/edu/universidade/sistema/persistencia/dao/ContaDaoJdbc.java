package br.edu.universidade.sistema.persistencia.dao;

import br.edu.universidade.sistema.persistencia.dominio.ContaBancaria;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// 3. Implementação Concreta do DAO via JDBC Nativo
public class ContaDaoJdbc implements ContaDAO {

    private final Connection conexao;

    public ContaDaoJdbc(Connection conexao) {
        this.conexao = conexao;
    }

    @Override
    public void salvar(ContaBancaria conta) throws SQLException {
        String sql = "INSERT INTO contas (numero_conta, titular, saldo) VALUES (?, ?, ?)";

        // RETURN_GENERATED_KEYS captura a chave primária auto-incrementada gerada pelo SGBD
        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, conta.getNumeroConta());
            stmt.setString(2, conta.getTitular());
            stmt.setDouble(3, conta.getSaldo());
            stmt.executeUpdate();

            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                if (chaves.next()) {
                    conta.setId(chaves.getLong(1)); // Atualiza o objeto no Heap com o ID do banco
                }
            }
        }
    }

    @Override
    public Optional<ContaBancaria> buscarPorId(Long id) throws SQLException {
        String sql = "SELECT id, numero_conta, titular, saldo FROM contas WHERE id = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearLinhaParaEntidade(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<ContaBancaria> buscarPorNumero(String numero) throws SQLException {
        String sql = "SELECT id, numero_conta, titular, saldo FROM contas WHERE numero_conta = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, numero);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearLinhaParaEntidade(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public void atualizarSaldo(ContaBancaria conta) throws SQLException {
        String sql = "UPDATE contas SET saldo = ? WHERE id = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setDouble(1, conta.getSaldo());
            stmt.setLong(2, conta.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public List<ContaBancaria> listarTodas() throws SQLException {
        String sql = "SELECT id, numero_conta, titular, saldo FROM contas ORDER BY id";
        List<ContaBancaria> contas = new ArrayList<>();

        try (PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                contas.add(mapearLinhaParaEntidade(rs));
            }
        }
        return contas;
    }

    // Método utilitário privado de mapeamento Objeto-Relacional
    private ContaBancaria mapearLinhaParaEntidade(ResultSet rs) throws SQLException {
        return new ContaBancaria(
                rs.getLong("id"),
                rs.getString("numero_conta"),
                rs.getString("titular"),
                rs.getDouble("saldo")
        );
    }
}
