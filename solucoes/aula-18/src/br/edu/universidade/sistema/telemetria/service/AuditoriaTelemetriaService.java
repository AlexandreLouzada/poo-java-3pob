package br.edu.universidade.sistema.telemetria.service;

import br.edu.universidade.sistema.telemetria.dominio.SinalSensor;
import java.util.List;

public class AuditoriaTelemetriaService {

    public double consolidarMediaTermicaSequencial(List<SinalSensor> sinais) {
        return sinais.stream()
                .filter(SinalSensor::isStatusCritico)
                .mapToDouble(SinalSensor::processarNormalizacao)
                .average()
                .orElse(0.0);
    }

    public double consolidarMediaTermicaParalelo(List<SinalSensor> sinais) {
        return sinais.parallelStream()
                .filter(SinalSensor::isStatusCritico)
                .mapToDouble(SinalSensor::processarNormalizacao)
                .average()
                .orElse(0.0);
    }

    public List<String> coletarEnderecosMacSuspeitosParalelo(List<SinalSensor> sinais, double tetoTemperatura) {
        return sinais.parallelStream()
                .filter(s -> s.processarNormalizacao() > tetoTemperatura)
                .map(s -> s.getMacAddress().toUpperCase())
                .distinct()
                .toList();
    }
}