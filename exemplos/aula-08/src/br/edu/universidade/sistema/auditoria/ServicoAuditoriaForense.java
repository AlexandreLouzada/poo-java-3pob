package br.edu.universidade.sistema.auditoria;

public class ServicoAuditoriaForense {

    public static void emitirLaudoTecnico(Throwable falha) {
        System.err.println("================ LAUDO DE INCIDENTE TÉCNICO ================");
        System.err.println("Classe da Exceção : " + falha.getClass().getName());
        System.err.println("Mensagem Descritiva: " + falha.getMessage());

        // Inspeção programática dos quadros da pilha através de StackTraceElement
        StackTraceElement[] frames = falha.getStackTrace();

        if (frames.length > 0) {
            StackTraceElement pontoFalha = frames[0]; // Topo da pilha: causa primária
            System.err.println("--- PONTO DE ORIGEM PRIMÁRIO ---");
            System.err.println("Classe Afetada   : " + pontoFalha.getClassName());
            System.err.println("Método Violado   : " + pontoFalha.getMethodName() + "()");
            System.err.println("Arquivo Fonte    : " + pontoFalha.getFileName());
            System.err.println("Linha do Disparo : " + pontoFalha.getLineNumber());
        }

        System.err.println("--- CADEIA DE RASTREIO RESUMIDA ---");
        for (int i = 0; i < Math.min(frames.length, 3); i++) {
            System.err.printf("  [Frame #%d] %s.%s() -> Linha %d%n",
                    i, frames[i].getClassName(), frames[i].getMethodName(), frames[i].getLineNumber());
        }
        System.err.println("============================================================");
    }
}
