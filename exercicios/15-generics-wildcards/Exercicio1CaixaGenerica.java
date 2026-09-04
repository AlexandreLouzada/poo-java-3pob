
public class Exercicio1CaixaGenerica {
    public static void main(String[] args) {
        // Caixa para String
        Caixa<String> caixaTexto = new Caixa<>();
        caixaTexto.guardar("Java 21");
        String texto = caixaTexto.recuperar(); // Sem cast manual
        System.out.println("Conteúdo Texto: " + texto);

        // Caixa para Integer
        Caixa<Integer> caixaNumero = new Caixa<>();
        caixaNumero.guardar(2026);
        System.out.println("Conteúdo Número: " + caixaNumero.recuperar());

        // Caixa para Tipo Personalizado
        Caixa<ItemProduto> caixaProduto = new Caixa<>();
        caixaProduto.guardar(new ItemProduto("Teclado Mecânico"));
        System.out.println("Conteúdo Objeto: " + caixaProduto.recuperar());
        
        caixaProduto.limpar();
        System.out.println("Caixa produto está vazia? " + caixaProduto.isVazia());
    }
}
