import java.util.Scanner;

public class Exercicio2ConversaoVetor {
    public static void main(String[] args) {
        String[] valores = {"10", "25", "abc", "50"};
        Scanner sc = new Scanner(System.in);

        System.out.println("Vetor disponível com 4 posições (índices 0 a 3).");
        System.out.print("Informe o índice que deseja converter para inteiro: ");

        try {
            int indice = sc.nextInt();
            String valorTexto = valores[indice];
            int valorNumerico = Integer.parseInt(valorTexto);

            System.out.println("Valor convertido com sucesso: " + valorNumerico);

        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Erro de Índice: O índice informado está fora dos limites do vetor (0 a 3).");
        } catch (NumberFormatException e) {
            System.out.println("Erro de Formato: O valor selecionado não contém uma representação inteira válida.");
        } finally {
            sc.close();
        }
    }
}
