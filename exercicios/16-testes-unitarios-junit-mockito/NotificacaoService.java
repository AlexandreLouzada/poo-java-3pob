
class NotificacaoService {
    private final EmailSender emailSender;

    public NotificacaoService(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void notificarBoasVindas(String nome, String email) {
        String assunto = "Bem-vindo à Plataforma!";
        String corpo = String.format("Olá, %s! Seu cadastro foi concluído com sucesso.", nome);
        emailSender.enviar(email, assunto, corpo);
    }
}
