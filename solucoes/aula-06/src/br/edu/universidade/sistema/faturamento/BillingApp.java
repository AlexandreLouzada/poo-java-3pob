package br.edu.universidade.sistema.faturamento;

public class BillingApp {
    public static void main(String[] args) {
        FaturamentoEngine engine = new FaturamentoEngine();

        System.out.println("--- Sobrecarga 1: Somente Mensalidade Base ---");
        for (NivelAssinatura nivel : NivelAssinatura.values()) {
            System.out.printf("%s -> Base: R$ %.2f | Limite incluso: %d usuarios%n",
                    nivel.name(), nivel.getMensalidadeBase(), nivel.getLimiteUsuariosInclusos());
        }

        System.out.println("\n--- Sobrecarga 2: Mensalidade Base + Usuarios Excedentes ---");
        System.out.printf("OURO com 30 usuarios: R$ %.2f (5 excedentes x R$ 15,00)%n",
                engine.calcularMensalidade(NivelAssinatura.OURO, 30));
        System.out.printf("PRATA com 8 usuarios: R$ %.2f (sem excedentes)%n",
                engine.calcularMensalidade(NivelAssinatura.PRATA, 8));
        System.out.printf("BRONZE com 3 usuarios: R$ %.2f (dentro do limite)%n",
                engine.calcularMensalidade(NivelAssinatura.BRONZE, 3));

        System.out.println("\n--- Sobrecarga 3: Varargs com Departamentos ---");
        double fatura = engine.calcularMensalidade(NivelAssinatura.OURO, 8, 10, 9);
        System.out.printf("OURO com departamentos [8, 10, 9] = 27 usuarios: R$ %.2f%n", fatura);
        double faturaVazia = engine.calcularMensalidade(NivelAssinatura.OURO);
        System.out.printf("OURO com departamentos vazios (varargs omisso): R$ %.2f%n", faturaVazia);
        double semExcedentes = engine.calcularMensalidade(NivelAssinatura.PRATA, 2, 3, 4);
        System.out.printf("PRATA com departamentos [2, 3, 4] = 9 usuarios: R$ %.2f (sem excedentes)%n", semExcedentes);
    }
}