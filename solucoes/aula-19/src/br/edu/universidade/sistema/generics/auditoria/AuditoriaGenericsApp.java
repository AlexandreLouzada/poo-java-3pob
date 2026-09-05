package br.edu.universidade.sistema.generics.auditoria;

import java.util.ArrayList;
import java.util.List;

public class AuditoriaGenericsApp {
    public static void main(String[] args) {
        List<Produto> produtos = new ArrayList<>();
        produtos.add(new Produto("Servidor Rack", 8500.00));
        produtos.add(new Produto("Workstation", 4200.00));
        produtos.add(new Produto("Teclado", 120.00));
        produtos.add(new Produto("Monitor 24", 780.00));

        Auditor<Object> auditorGenerico = obj -> obj.toString().length() > 10;
        Auditor<Produto> auditorEspecifico = p -> p.getPreco() > 1000.00;

        System.out.println("--- Auditor Generico (Object): toString com mais de 10 caracteres ---");
        int aprovadosGenerico = AuditoriaService.contarAprovados(produtos, auditorGenerico);
        System.out.println("Produtos aprovados: " + aprovadosGenerico + " de " + produtos.size());

        System.out.println("\n--- Auditor Especifico (Produto): preco acima de R$ 1.000,00 ---");
        int aprovadosEspecifico = AuditoriaService.contarAprovados(produtos, auditorEspecifico);
        System.out.println("Produtos aprovados: " + aprovadosEspecifico + " de " + produtos.size());

        System.out.println("\n--- Reuso do Auditor<? super T> com validadores de superclasses ---");
        int aprovadosGenericoComoSuper = AuditoriaService.contarAprovados(produtos, (Auditor<Object>) auditorGenerico);
        System.out.println("Auditor<Object> aplicado em List<Produto>: " + aprovadosGenericoComoSuper + " aprovados.");

        System.out.println("\n--- PECS: Mesclagem Segura (Produto -> ? super Produto) ---");
        List<Object> destino = new ArrayList<>();
        AuditoriaService.mesclarColecoes(produtos, destino);
        System.out.println("Elementos copiados para o destino (List<Object>): " + destino.size());

        // PECS: a linha abaixo NAO compila (intencional) -> copiar List<Object> para List<Produto>
        // violaria a seguranca de tipos: AuditoriaService.mesclarColecoes(destino, produtos);
        System.out.println("Mesclagem inversa (Object -> Produto) e bloqueada pelo compilador (PECS).");
    }
}