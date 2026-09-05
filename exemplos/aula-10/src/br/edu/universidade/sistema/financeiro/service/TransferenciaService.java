package br.edu.universidade.sistema.financeiro.service;

import br.edu.universidade.sistema.financeiro.dominio.ContaCorrente;
import br.edu.universidade.sistema.financeiro.exception.ContaBloqueadaException;
import br.edu.universidade.sistema.financeiro.exception.SaldoInsuficienteException;

// 4. Camada de Serviço orquestrando transferências de forma resiliente
public class TransferenciaService {

    public void transferir(ContaCorrente origem, ContaCorrente destino, double valor) {
        System.out.printf("Iniciando transferência de R$ %.2f [Conta %s -> Conta %s]...%n",
                valor, origem.getNumero(), destino.getNumero());

        try {
            origem.sacar(valor);
            destino.depositar(valor);
            System.out.printf("[SUCESSO] Transferência concluída! Saldo atualizado da origem: R$ %.2f%n",
                    origem.getSaldo());

        } catch (SaldoInsuficienteException ex) {
            // Tratamento especializado acessando os atributos da exceção própria
            System.err.println("[FALHA DE SALDO] " + ex.getMessage());
            System.err.printf("Auditoria: Saldo em conta: R$ %.2f | Valor solicitado: R$ %.2f | Déficit: R$ %.2f%n",
                    ex.getSaldoDisponivel(), ex.getValorTentado(), (ex.getValorTentado() - ex.getSaldoDisponivel()));

        } catch (ContaBloqueadaException ex) {
            // Tratamento para conta inativa
            System.err.println("[SEGURANÇA] Operação cancelada. " + ex.getMessage());
            System.err.println("Conta sinalizada para o departamento de compliance: " + ex.getNumeroConta());

        } catch (IllegalArgumentException ex) {
            // Tratamento de parâmetros ilegais
            System.err.println("[VALOR INVÁLIDO] " + ex.getMessage());
        }
    }
}
