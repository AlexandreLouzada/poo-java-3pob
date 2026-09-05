package br.edu.universidade.sistema.persistencia.dao;

import br.edu.universidade.sistema.persistencia.dominio.ContaBancaria;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

// 2. Contrato da Camada de Acesso a Dados (DAO Interface)
public interface ContaDAO {
    void salvar(ContaBancaria conta) throws SQLException;
    Optional<ContaBancaria> buscarPorId(Long id) throws SQLException;
    Optional<ContaBancaria> buscarPorNumero(String numero) throws SQLException;
    void atualizarSaldo(ContaBancaria conta) throws SQLException;
    List<ContaBancaria> listarTodas() throws SQLException;
}
