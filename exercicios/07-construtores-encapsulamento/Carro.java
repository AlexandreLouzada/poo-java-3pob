class Carro {
    private String modelo;
    private final int ano;
    private int velocidadeAtual;

    public Carro(String modelo, int ano) {
        this.modelo = modelo;
        this.ano = ano;
        this.velocidadeAtual = 0;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getAno() {
        return ano;
    }

    public int getVelocidadeAtual() {
        return velocidadeAtual;
    }

    public void acelerar(int incremento) {
        if (incremento > 0) {
            velocidadeAtual += incremento;
            System.out.println(modelo + " acelerou para " + velocidadeAtual + " km/h.");
        }
    }

    public void frear(int decremento) {
        if (decremento > 0) {
            velocidadeAtual = Math.max(0, velocidadeAtual - decremento);
            System.out.println(modelo + " desacelerou para " + velocidadeAtual + " km/h.");
        }
    }

    public boolean isEmMovimento() {
        return velocidadeAtual > 0;
    }
}
