package br.edu.universidade.sistema.io.dominio;

import java.time.LocalDate;
import java.util.Objects;

// 1. Entidade de Domínio representando o Registro de Movimentação
public class MovimentacaoBancaria {
    private final Long id;
    private final String contaOrigem;
    private final String tipo; // DEBITO ou CREDITO
    private final double valor;
    private final LocalDate data;

    public MovimentacaoBancaria(Long id, String contaOrigem, String tipo, double valor, LocalDate data) {
        if (id == null || valor <= 0.0) {
            throw new IllegalArgumentException("Dados de movimentação inconsistentes.");
        }
        this.id = id;
        this.contaOrigem = contaOrigem;
        this.tipo = tipo.toUpperCase();
        this.valor = valor;
        this.data = data;
    }

    public Long getId() { return id; }
    public String getContaOrigem() { return contaOrigem; }
    public String getTipo() { return tipo; }
    public double getValor() { return valor; }
    public LocalDate getData() { return data; }

    // Converte a entidade para formato tabular CSV delimitado por ponto e vírgula
    public String toCsv() {
        return String.format("%d;%s;%s;%.2f;%s", id, contaOrigem, tipo, valor, data);
    }

    // Fábrica estática para criar a entidade a partir de uma linha de texto do CSV
    public static MovimentacaoBancaria fromCsv(String linha) {
        String[] partes = linha.split(";");
        return new MovimentacaoBancaria(
                Long.parseLong(partes[0].trim()),
                partes[1].trim(),
                partes[2].trim(),
                Double.parseDouble(partes[3].trim().replace(",", ".")),
                LocalDate.parse(partes[4].trim())
        );
    }

    @Override
    public String toString() {
        return String.format("[#%04d] Conta: %-8s | Tipo: %-7s | Valor: R$ %8.2f | Data: %s",
                id, contaOrigem, tipo, valor, data);
    }
}
