public class BancoOOApp {
    public static void main(String[] args) {
        // Alocação da referência na Stack e da instância no Heap
        ContaBancaria conta = new ContaBancaria(100.0);

        // Tentativa de saque que viola a regra de negócio é rejeitada pelo objeto
        boolean sucesso = conta.sacar(150.0);
        System.out.println("Saque de R$ 150.00 autorizado? " + sucesso);
        System.out.printf("Saldo preservado: R$ %.2f%n", conta.getSaldo());

        // A instrução abaixo geraria erro de compilação:
        // conta.saldo = -50000.0;
    }
}
