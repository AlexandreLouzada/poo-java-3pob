package br.edu.universidade.sistema.persistencia.service;

import br.edu.universidade.sistema.persistencia.dao.ContaDAO;
import br.edu.universidade.sistema.persistencia.dominio.ContaBancaria;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.NoSuchElementException;

// 4. Camada de Serviço Orquestradora de Transações
public class TransferenciaService {

    private final Connection conexao;
    private final ContaDAO contaDAO;

    public TransferenciaService(Connection conexao, ContaDAO contaDAO) {
        this.conexao = conexao;
        this.contaDAO = contaDAO;
    }

    public void transferir(String numContaOrigem, String numContaDestino, double valor) throws SQLException {
        // Desativa autocommit para iniciar a transação ACID manual
        conexao.setAutoCommit(false);

        try {
            ContaBancaria origem = contaDAO.buscarPorNumero(numContaOrigem)
                    .orElseThrow(() -> new NoSuchElementException("Conta de origem não encontrada: " + numContaOrigem));

            ContaBancaria destino = contaDAO.buscarPorNumero(numContaDestino)
                    .orElseThrow(() -> new NoSuchElementException("Conta de destino não encontrada: " + numContaDestino));

            // Executa as regras de negócio nas entidades
            origem.debitar(valor);
            destino.creditar(valor);

            // Persiste o estado atualizado de ambos os registros no banco
            contaDAO.atualizarSaldo(origem);
            contaDAO.atualizarSaldo(destino);

            // Confirma todas as operações atomicamente
            conexao.commit();
            System.out.printf("[TRANSAÇÃO CONCLUÍDA] R$ %.2f transferidos com sucesso de %s para %s.%n",
                    valor, numContaOrigem, numContaDestino);

        } catch (Exception ex) {
            // Em qualquer anomalia técnica ou violação de regra, reverte completamente
            conexao.rollback();
            System.err.printf("[TRANSAÇÃO REVERTIDA - ROLLBACK] Falha na transferência: %s%n", ex.getMessage());
            throw ex;
        } finally {
            // Restaura o autocommit para não interferir em outras operações
            conexao.setAutoCommit(true);
        }
    }
}
