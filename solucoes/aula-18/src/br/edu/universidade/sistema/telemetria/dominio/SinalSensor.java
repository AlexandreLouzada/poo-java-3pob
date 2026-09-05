package br.edu.universidade.sistema.telemetria.dominio;

public class SinalSensor {
    private Long idSensor;
    private String macAddress;
    private double leituraTemperatura;
    private boolean statusCritico;

    public SinalSensor(Long idSensor, String macAddress, double leituraTemperatura, boolean statusCritico) {
        if (idSensor == null) {
            throw new IllegalArgumentException("ID do sensor nao pode ser nulo.");
        }
        if (macAddress == null) {
            throw new IllegalArgumentException("Endereco MAC nao pode ser nulo.");
        }
        this.idSensor = idSensor;
        this.macAddress = macAddress;
        this.leituraTemperatura = leituraTemperatura;
        this.statusCritico = statusCritico;
    }

    public double processarNormalizacao() {
        double resultado = leituraTemperatura;
        for (int i = 0; i < 100; i++) {
            double angulo = i * Math.PI / 180.0;
            resultado += leituraTemperatura * Math.sqrt(Math.abs(Math.sin(angulo)))
                    + Math.cos(angulo) * 0.5;
        }
        return resultado;
    }

    public Long getIdSensor() { return idSensor; }
    public String getMacAddress() { return macAddress; }
    public double getLeituraTemperatura() { return leituraTemperatura; }
    public boolean isStatusCritico() { return statusCritico; }

    @Override
    public String toString() {
        return String.format("Sensor [ID: %d | MAC: %s | Temp: %.2f | Critico: %s]",
                idSensor, macAddress, leituraTemperatura, statusCritico);
    }
}