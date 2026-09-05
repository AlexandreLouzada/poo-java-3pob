package br.edu.universidade.sistema.acesso;

import br.edu.universidade.sistema.acesso.dominio.CrachaFuncionario;
import br.edu.universidade.sistema.acesso.service.ControleAcessoPredialService;

public class CatracaApp {
    public static void main(String[] args) {
        ControleAcessoPredialService service = new ControleAcessoPredialService();

        CrachaFuncionario c1 = new CrachaFuncionario("CRT-001", "Ana Clara", "TI");
        CrachaFuncionario c2 = new CrachaFuncionario("CRT-002", "Carlos Prado", "TI");
        CrachaFuncionario c3 = new CrachaFuncionario("CRT-003", "Beatriz Costa", "RH");
        CrachaFuncionario c4 = new CrachaFuncionario("CRT-004", "Lucas Mendes", "ADMINISTRATIVO");

        System.out.println("--- Cadastro dos 4 Colaboradores (Ordem de Cadastro Preservada) ---");
        service.cadastrarColaborador(c1);
        service.cadastrarColaborador(c2);
        service.cadastrarColaborador(c3);
        service.cadastrarColaborador(c4);
        System.out.printf("Total cadastrados: %d%n", service.getTotalCadastrados());

        System.out.println("\n--- Entradas na Catraca ---");
        System.out.printf("Entrada %s : %s%n", c1.getNome(), service.registrarEntrada(c1));
        System.out.printf("Entrada %s : %s%n", c2.getNome(), service.registrarEntrada(c2));
        System.out.printf("Entrada %s : %s%n", c3.getNome(), service.registrarEntrada(c3));

        System.out.println("\n--- Tentativa Fraudulenta de Reuso do Mesmo Cartao (CRT-001) ---");
        boolean reuso = service.registrarEntrada(c1);
        System.out.printf("Reentrada de %s autorizada? %s (barrada pela unicidade do Set)%n", c1.getNome(), reuso);

        System.out.println("\n--- Colaboradores Presentes no Edificio (Ordenados por Departamento e Nome) ---");
        service.obterPresentesOrdenados().forEach(System.out::println);
        System.out.printf("Total presentes: %d%n", service.getTotalPresentes());

        System.out.println("\n--- Colaboradores Ausentes (cadastrados - presentes) ---");
        service.obterAusentes().forEach(System.out::println);
    }
}