
public class Exercicio3InterfacesMultiplas {
    public static void main(String[] args) {
        Usuario user = new Usuario("joao", "123");
        Administrador admin = new Administrador("admin", "admin123", 1);

        System.out.println("Usuário autenticado? " + user.autenticar("123"));
        System.out.println("Admin autenticado? " + admin.autenticar("errada"));

        // user.exportarJSON(); // Erro de compilação: Usuario não implementa ExportavelJSON
        System.out.println("JSON do Admin: " + admin.exportarJSON());
    }
}
