package br.edu.universidade.sistema.patrimonio.dao;

import br.edu.universidade.sistema.patrimonio.dominio.AtivoPatrimonial;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface AtivoDAO {

    void salvar(AtivoPatrimonial ativo) throws SQLException;

    Optional<AtivoPatrimonial> buscarPorTombo(String tombo) throws SQLException;

    void atualizarStatus(Long id, String novoStatus) throws SQLException;

    List<AtivoPatrimonial> listarAtivosEmOperacao() throws SQLException;
}