
public class Exercicio1Veiculos {
    public static void main(String[] args) {
        Veiculo carro = new Carro("Toyota", "Corolla", 4);
        Veiculo moto = new Moto("Honda", "CB 500", 500);

        carro.exibirDetalhes();
        moto.exibirDetalhes();
    }
}
