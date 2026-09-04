
public class Exercicio3Notificacoes {
    public static void processarEnvio(Notificacao notificacao, String texto) {
        notificacao.enviar(texto);
    }

    public static void main(String[] args) {
        Notificacao n1 = new EmailNotificacao("aluno@universidade.edu.br");
        Notificacao n2 = new SmsNotificacao("+55 (21) 99999-8888");
        Notificacao n3 = new PushNotificacao("DEVICE-TOKEN-XYZ-2026");

        processarEnvio(n1, "Sua nota da prova foi lançada no portal.");
        processarEnvio(n2, "Seu código de acesso é: 482910.");
        processarEnvio(n3, "Você tem uma nova mensagem não lida.");
    }
}
