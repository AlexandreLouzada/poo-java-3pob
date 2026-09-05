package br.edu.universidade.sistema.cadastro;

public class CadastroApp {
    public static void main(String[] args) {
        String massaTeste = """
                101,Ana Clara Souza,28,5000.00
                102,Carlos Eduardo,35,12000.50
                103,Beatriz Costa,vinte,8000.00
                104,Mariana Silva,32,-1500.00
                105,Lucas Mendes,41,20000.00
                """;

        ImportadorCadastroService importador = new ImportadorCadastroService();
        importador.importarLote(massaTeste);
    }
}