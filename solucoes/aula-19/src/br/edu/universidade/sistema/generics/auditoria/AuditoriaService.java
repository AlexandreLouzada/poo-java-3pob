package br.edu.universidade.sistema.generics.auditoria;

import java.util.List;

public final class AuditoriaService {

    public static <T> int contarAprovados(List<? extends T> lista, Auditor<? super T> auditor) {
        int aprovados = 0;
        for (T item : lista) {
            if (auditor.auditar(item)) {
                aprovados++;
            }
        }
        return aprovados;
    }

    public static <T> void mesclarColecoes(List<? extends T> fonte, List<? super T> destino) {
        for (T item : fonte) {
            destino.add(item);
        }
    }

    private AuditoriaService() {}
}