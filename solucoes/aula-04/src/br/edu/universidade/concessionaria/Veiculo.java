package br.edu.universidade.concessionaria;

import java.time.Year;

public class Veiculo {
    private String chassi;
    private String marca;
    private String modelo;
    private int anoFabricacao;
    private double precoBase;

    public Veiculo(String chassi, String marca, String modelo, int anoFabricacao, double precoBase) {
        this.chassi = chassi;
        this.marca = marca;
        this.modelo = modelo;
        this.anoFabricacao = (anoFabricacao > 1886) ? anoFabricacao : 1900;
        this.precoBase = (precoBase >= 0.0) ? precoBase : 0.0;
    }

    public Veiculo(String chassi, String marca, String modelo) {
        this(chassi, marca, modelo, Year.now().getValue(), 0.0);
    }

    public void aplicarDesconto(double percentual) {
        if (percentual > 0.0 && percentual <= 0.25) {
            this.precoBase -= this.precoBase * percentual;
        }
    }

    public void exibirFichaTecnica() {
        System.out.printf("=============== FICHA TECNICA ===============%n");
        System.out.printf("Chassi            : %s%n", chassi);
        System.out.printf("Marca             : %s%n", marca);
        System.out.printf("Modelo            : %s%n", modelo);
        System.out.printf("Ano de fabricacao : %d%n", anoFabricacao);
        System.out.printf("Preco base        : R$ %.2f%n", precoBase);
        System.out.printf("=============================================%n");
    }

    public String getChassi() { return chassi; }
    public String getMarca() { return marca; }
    public String getModelo() { return modelo; }
    public int getAnoFabricacao() { return anoFabricacao; }
    public double getPrecoBase() { return precoBase; }
}