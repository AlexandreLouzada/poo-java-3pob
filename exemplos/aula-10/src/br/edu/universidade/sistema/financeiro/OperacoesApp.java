package br.edu.universidade.sistema.financeiro;

import br.edu.universidade.sistema.financeiro.dominio.ContaCorrente;
import br.edu.universidade.sistema.financeiro.service.TransferenciaService;

// 5. Execução demonstrando os diferentes cenários de captura
public class OperacoesApp {
    public static void main(String[] args) {
        ContaCorrente c1 = new ContaCorrente("001-A", 500.00);
        ContaCorrente c2 = new ContaCorrente("002-B", 100.00);
        TransferenciaService service = new TransferenciaService();

        System.out.println("--- Cenário 1: Transferência Válida ---");
        service.transferir(c1, c2, 200.00);

        System.out.println("\n--- Cenário 2: Saldo Insuficiente (Checked Exception) ---");
        service.transferir(c1, c2, 400.00);

        System.out.println("\n--- Cenário 3: Conta Inativa (Unchecked Exception) ---");
        c2.bloquearConta();
        service.transferir(c1, c2, 50.00);

        System.out.println("\nO sistema permaneceu em execução contínua sem quebras de processo.");
    }
}
