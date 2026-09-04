import java.util.HashMap;
import java.util.Map;

class ControleEstoque {
    private Map<String, Integer> inventario = new HashMap<>();

    public void cadastrarOuAdicionar(String codigo, int quantidade) {
        int saldoAtual = inventario.getOrDefault(codigo, 0);
        inventario.put(codigo, saldoAtual + quantidade);
        System.out.printf("Produto %s atualizado. Saldo: %d unidades.%n", codigo, inventario.get(codigo));
    }

    public void darBaixa(String codigo, int quantidade) {
        if (!inventario.containsKey(codigo)) {
            System.out.printf("Erro: Produto %s não encontrado no estoque.%n", codigo);
            return;
        }

        int saldoAtual = inventario.get(codigo);
        if (saldoAtual >= quantidade) {
            inventario.put(codigo, saldoAtual - quantidade);
            System.out.printf("Baixa de %d unidades efetuada para %s. Novo saldo: %d.%n", 
                              quantidade, codigo, inventario.get(codigo));
        } else {
            System.out.printf("Erro: Saldo insuficiente para %s. Disponível: %d, Solicitado: %d.%n", 
                              codigo, saldoAtual, quantidade);
        }
    }

    public void relatorioEstoqueBaixo(int limiteAlerta) {
        System.out.println("\n--- Alerta: Produtos com estoque <= " + limiteAlerta + " ---");
        for (Map.Entry<String, Integer> entry : inventario.entrySet()) {
            if (entry.getValue() <= limiteAlerta) {
                System.out.printf("Código: %s | Quantidade: %d%n", entry.getKey(), entry.getValue());
            }
        }
    }
}
