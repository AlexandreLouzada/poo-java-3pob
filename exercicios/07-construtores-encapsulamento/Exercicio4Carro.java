
public class Exercicio4Carro {
    public static void main(String[] args) {
        Carro meuCarro = new Carro("Sedan Turbo", 2024);

        System.out.println("Em movimento? " + meuCarro.isEmMovimento());

        meuCarro.acelerar(60);
        System.out.println("Em movimento? " + meuCarro.isEmMovimento());

        meuCarro.frear(80);
        System.out.println("Em movimento? " + meuCarro.isEmMovimento());
    }
}
