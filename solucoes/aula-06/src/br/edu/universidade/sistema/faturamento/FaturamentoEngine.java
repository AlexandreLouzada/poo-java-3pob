package br.edu.universidade.sistema.faturamento;

public class FaturamentoEngine {

    public static final double TARIFA_USUARIO_EXCEDENTE = 15.00;

    public double calcularMensalidade(NivelAssinatura nivel) {
        validarNivel(nivel);
        return nivel.getMensalidadeBase();
    }

    public double calcularMensalidade(NivelAssinatura nivel, int totalUsuariosAtivos) {
        validarNivel(nivel);
        if (totalUsuariosAtivos < 0) {
            throw new IllegalArgumentException("Total de usuarios nao pode ser negativo: " + totalUsuariosAtivos);
        }
        int excedentes = totalUsuariosAtivos - nivel.getLimiteUsuariosInclusos();
        double valor = nivel.getMensalidadeBase();
        if (excedentes > 0) {
            valor += excedentes * TARIFA_USUARIO_EXCEDENTE;
        }
        return valor;
    }

    public double calcularMensalidade(NivelAssinatura nivel, int... departamentos) {
        validarNivel(nivel);
        if (departamentos == null || departamentos.length == 0) {
            return nivel.getMensalidadeBase();
        }
        int totalUsuarios = 0;
        for (int qtd : departamentos) {
            if (qtd < 0) {
                throw new IllegalArgumentException("Quantidade de departamento nao pode ser negativa: " + qtd);
            }
            totalUsuarios += qtd;
        }
        return calcularMensalidade(nivel, totalUsuarios);
    }

    private void validarNivel(NivelAssinatura nivel) {
        if (nivel == null) {
            throw new IllegalArgumentException("Nivel de assinatura nao pode ser nulo.");
        }
    }
}