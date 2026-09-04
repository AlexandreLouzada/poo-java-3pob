import java.util.Scanner;

public class SeparaParesImpares {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int[] original = new int[10];

        int contPares = 0;
        int contImpares = 0;

        for (int i = 0; i < original.length; i++) {
            System.out.print("Elemento [" + i + "]: ");
            original[i] = sc.nextInt();
            if (original[i] % 2 == 0) {
                contPares++;
            } else {
                contImpares++;
            }
        }

        int[] pares = new int[contPares];
        int[] impares = new int[contImpares];

        int ip = 0, ii = 0;
        for (int val : original) {
            if (val % 2 == 0) {
                pares[ip++] = val;
            } else {
                impares[ii++] = val;
            }
        }

        System.out.print("Pares: [ ");
        for (int p : pares) System.out.print(p + " ");
        System.out.println("]");

        System.out.print("Ímpares: [ ");
        for (int im : impares) System.out.print(im + " ");
        System.out.println("]");

        sc.close();
    }
}
