package br.edu.universidade.sistema.rede;

import br.edu.universidade.sistema.rede.dominio.RegistroAcessoRede;
import br.edu.universidade.sistema.rede.service.AuditoriaFirewallService;
import java.util.List;

public class MonitoramentoRedeApp {
    public static void main(String[] args) {
        AuditoriaFirewallService service = new AuditoriaFirewallService();

        List<RegistroAcessoRede> conexoes = List.of(
                new RegistroAcessoRede(1L, "192.168.0.10", 22,  12000.00, false),
                new RegistroAcessoRede(2L, "192.168.0.20", 443,   80.00,  true),
                new RegistroAcessoRede(3L, "172.16.5.11",  445,  980.00,  false),
                new RegistroAcessoRede(4L, "10.0.0.33",    1433, 700.00,  true),
                new RegistroAcessoRede(5L, "172.16.9.77",  8080, 40.00,   true)
        );

        System.out.println("--- Trafego Suspeito (nao bloqueado, > 500 MB em porta < 1024, decrescente) ---");
        List<RegistroAcessoRede> suspeitos = service.filtrarTrafegoSuspeito(conexoes);
        suspeitos.forEach(System.out::println);

        System.out.println("\n--- IPs Bloqueados Unicos (sem repeticoes, ordem alfabetica) ---");
        List<String> ipsBloqueados = service.extrairIpsBloqueadosUnicos(conexoes);
        ipsBloqueados.forEach(System.out::println);

        System.out.println("\n--- Alerta de Ataque DDoS (conexao individual > 10.000 MB) ---");
        boolean ddos = service.alertaAtaqueDDoS(conexoes);
        System.out.println("Alerta DDoS acionado? " + ddos);

        System.out.println("\n--- Integridade da Lista Original ---");
        conexoes.forEach(System.out::println);
        System.out.printf("Total de registros preservados na fonte: %d%n", conexoes.size());
    }
}