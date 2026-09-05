package br.edu.universidade.sistema.sac;

import br.edu.universidade.sistema.sac.dominio.ChamadoSuporte;
import br.edu.universidade.sistema.sac.service.CentralAtendimentoService;
import java.util.List;
import java.util.Map;

public class SacApp {
    public static void main(String[] args) {
        CentralAtendimentoService service = new CentralAtendimentoService();

        service.abrirChamado(new ChamadoSuporte("SAC-1001", "Empresa X", "Problema de login no portal"));
        service.abrirChamado(new ChamadoSuporte("SAC-1002", "Empresa Y", "Erro 500 no checkout"));
        service.abrirChamado(new ChamadoSuporte("SAC-1003", "Empresa X", "Faturamento duplicado"));
        service.abrirChamado(new ChamadoSuporte("SAC-1004", "Empresa Z", "Integracao de API indisponivel"));

        System.out.println("--- Resolucao de Chamados por Protocolo ---");
        service.consultarPorProtocolo("SAC-1002").resolverChamado();
        service.consultarPorProtocolo("SAC-1003").resolverChamado();
        System.out.println("Chamados SAC-1002 e SAC-1003 marcados como RESOLVIDO.");

        System.out.println("\n--- Historico Completo do Cliente 'Empresa X' ---");
        List<ChamadoSuporte> historicoX = service.listarHistoricoCliente("Empresa X");
        historicoX.forEach(System.out::println);

        System.out.println("\n--- Cliente Sem Historicos (lista vazia imutavel) ---");
        System.out.println("Historico de 'Empresa Inexistente': " + service.listarHistoricoCliente("Empresa Inexistente"));

        System.out.println("\n--- Mapa de Contagem Consolidado por Status (Map.merge) ---");
        Map<String, Integer> status = service.calcularTotalChamadosPorStatus();
        status.forEach((s, total) ->
                System.out.printf("Status: %-9s | Total: %d%n", s, total));
        System.out.printf("Total geral de chamados: %d%n", service.getTotalChamados());
    }
}