
public class Exercicio4UncheckedException {
    public static void main(String[] args) {
        try {
            Eleitor e1 = new Eleitor("Mariana Silva", 22);
            Eleitor e2 = new Eleitor("João Pereira", -5); // Lança RuntimeException
        } catch (IdadeInvalidaException e) {
            System.out.println("Falha no cadastro de Eleitor: " + e.getMessage());
        }
    }
}
