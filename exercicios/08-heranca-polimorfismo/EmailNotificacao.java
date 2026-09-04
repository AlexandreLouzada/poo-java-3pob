
class EmailNotificacao extends Notificacao {
    public EmailNotificacao(String email) {
        super(email);
    }

    @Override
    public void enviar(String mensagem) {
        System.out.println("Enviando E-mail para [" + getDestinatario() + "]: " + mensagem);
    }
}
