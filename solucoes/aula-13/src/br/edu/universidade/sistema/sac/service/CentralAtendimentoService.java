package br.edu.universidade.sistema.sac.service;

import br.edu.universidade.sistema.sac.dominio.ChamadoSuporte;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class CentralAtendimentoService {

    private final Map<String, ChamadoSuporte> chamadosPorProtocolo = new HashMap<>();
    private final Map<String, List<ChamadoSuporte>> chamadosPorCliente = new HashMap<>();

    public void abrirChamado(ChamadoSuporte chamado) {
        if (chamado == null) {
            throw new IllegalArgumentException("Chamado nulo nao pode ser aberto.");
        }
        if (chamadosPorProtocolo.containsKey(chamado.getProtocolo())) {
            throw new IllegalArgumentException("Protocolo ja cadastrado: " + chamado.getProtocolo());
        }
        chamadosPorProtocolo.put(chamado.getProtocolo(), chamado);
        chamadosPorCliente.computeIfAbsent(chamado.getCliente(), k -> new ArrayList<>()).add(chamado);
    }

    public ChamadoSuporte consultarPorProtocolo(String protocolo) {
        ChamadoSuporte chamado = chamadosPorProtocolo.get(protocolo);
        if (chamado == null) {
            throw new NoSuchElementException("Protocolo nao localizado: " + protocolo);
        }
        return chamado;
    }

    public List<ChamadoSuporte> listarHistoricoCliente(String cliente) {
        List<ChamadoSuporte> historico = chamadosPorCliente.get(cliente);
        return (historico == null) ? List.of() : historico;
    }

    public Map<String, Integer> calcularTotalChamadosPorStatus() {
        Map<String, Integer> contagem = new HashMap<>();
        for (ChamadoSuporte chamado : chamadosPorProtocolo.values()) {
            contagem.merge(chamado.getStatus(), 1, Integer::sum);
        }
        return contagem;
    }

    public int getTotalChamados() {
        return chamadosPorProtocolo.size();
    }
}