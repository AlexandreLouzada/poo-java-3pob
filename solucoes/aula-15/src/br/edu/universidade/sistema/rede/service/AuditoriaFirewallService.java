package br.edu.universidade.sistema.rede.service;

import br.edu.universidade.sistema.rede.dominio.RegistroAcessoRede;
import java.util.List;

public class AuditoriaFirewallService {

    public List<RegistroAcessoRede> filtrarTrafegoSuspeito(List<RegistroAcessoRede> conexoes) {
        return conexoes.stream()
                .filter(c -> !c.isBloqueadoFirewall())
                .filter(c -> c.getMegabytesTransferidos() > 500.0)
                .filter(c -> c.getPortaDestino() < 1024)
                .sorted((a, b) -> Double.compare(b.getMegabytesTransferidos(), a.getMegabytesTransferidos()))
                .toList();
    }

    public List<String> extrairIpsBloqueadosUnicos(List<RegistroAcessoRede> conexoes) {
        return conexoes.stream()
                .filter(c -> c.isBloqueadoFirewall())
                .map(RegistroAcessoRede::getIpOrigem)
                .distinct()
                .sorted()
                .toList();
    }

    public boolean alertaAtaqueDDoS(List<RegistroAcessoRede> conexoes) {
        return conexoes.stream()
                .anyMatch(c -> c.getMegabytesTransferidos() > 10000.0);
    }
}