# Tutorial de Java — Aula 06: Sobrecarga de Métodos (Overloading), Argumentos Variáveis (Varargs) e Tipos Enumerados (Enums Avançados)

| | |
|---|---|
| **Disciplina** | Programação Orientada a Objetos |
| **Curso** | Análise e Desenvolvimento de Sistemas (3º Período) |
| **Tema** | Sobrecarga de Métodos (Overloading), Argumentos Variáveis (Varargs) e Tipos Enumerados (Enums Avançados) |
| **Carga Horária** | 100 minutos (2 horas-aula) |
| **Professor Responsável** | Alexandre Neves Louzada |
| **Arquivo de Referência** | `TutorialAula6.md` |

## 1. Objetivos de Aprendizagem

- **Conceitual:** Compreender o conceito de polimorfismo estático em tempo de compilação (*Static / Early Binding*) através da sobrecarga de métodos; reconhecer os riscos de integridade associados ao uso de números e textos mágicos (*Magic Numbers* e *Magic Strings*); entender que os tipos enumerados (`enum`) em Java representam classes completas no modelo de orientação a objetos.
- **Técnico:** Projetar métodos sobrecarregados com base na assinatura reconhecida pela JVM (nome do método associado à lista ordenada de tipos de parâmetros); aplicar a sintaxe de Varargs (`Tipo...`), respeitando as regras de unicidade e posicionamento final na assinatura; implementar tipos enumerados avançados contendo atributos imutáveis (`final`), construtores parametrizados privados e métodos de cálculo encapsulados.
- **Arquitetural:** Integrar enums avançados com estruturas de seleção modernas (*Switch Expressions*), garantindo a cobertura completa dos cenários em tempo de compilação e eliminando o risco de execuções em cascata indesejadas (*fall-through*).
- **Prático:** Implementar um motor de cálculo logístico e tarifário para e-commerce, integrando tabelas de frete baseadas em enums ricos e métodos sobrecarregados com varargs para pesagens individuais e processamento em lote.

## 2. Fundamentação Teórica

### Sobrecarga de Métodos (Method Overloading)

A sobrecarga ocorre quando dois ou mais métodos dentro da mesma classe compartilham exatamente o mesmo nome, mas apresentam listas de parâmetros distintas. Esse recurso representa o polimorfismo estático em tempo de compilação (*Compile-Time Polymorphism* ou *Early Binding*), no qual o compilador `javac` determina qual método executar no instante da compilação, avaliando o número, os tipos e a ordem dos argumentos passados.

> Assinatura do Método na JVM = Nome do Método + Lista Ordenada dos Tipos de Parâmetros

```plaintext
┌─────────────────────────────────────────────────────────────────────────────┐
│                   CRITÉRIOS DE DIFERENCIAÇÃO DE ASSINATURA                  │
├──────────────────────────────────────┬──────────────────────────────────────┤
│ O que DIFERENCIA a sobrecarga:       │ O que NÃO diferencia a sobrecarga:   │
├──────────────────────────────────────┼──────────────────────────────────────┤
│ • Quantidade de parâmetros           │ • O tipo de retorno do método        │
│ • Tipos de dados dos parâmetros      │ • Os nomes dos parâmetros            │
│ • Ordem posicional dos tipos         │ • Modificadores de acesso            │
│                                      │   (public/private)                   │
└──────────────────────────────────────┴──────────────────────────────────────┘
```

Se o desenvolvedor declarar na mesma classe dois métodos com mesmo nome e mesmos tipos de parâmetros, alterando apenas o tipo de retorno (por exemplo, `double calcular(int x)` e `int calcular(int x)`), o compilador rejeitará o código com o erro de compilação: `method is already defined`.

### Argumentos Variáveis (Varargs)

Tradicionalmente, para permitir que um método receba uma quantidade variável de entradas, era necessário criar sobrecargas repetitivas (com 1, 2, 3 ou mais argumentos) ou exigir que o chamador empacotasse os dados em um vetor pré-alocado.

O recurso de Varargs simplifica essa sintaxe usando reticências logo após o tipo (`Tipo...`):

- **Funcionamento Interno na JVM:** O compilador sintetiza a instrução `Tipo...` internamente como um array convencional (`Tipo[]`).
- **Flexibilidade na Chamada:** O consumidor do método pode invocar a rotina passando múltiplos valores separados por vírgula, um array já existente ou até nenhum argumento.
- **Restrições Obrigatórias do Compilador:**
  - Cada método pode conter no máximo um único parâmetro varargs.
  - O parâmetro varargs deve ser, obrigatoriamente, o último item declarado na lista de argumentos do método.

### Tipos Enumerados Avançados (enum)

Em linguagens procedurais clássicas como C, enumerações são representadas apenas como apelidos para números inteiros sequenciais (0, 1, 2...). Nesses ambientes, constantes soltas (*Magic Numbers*) podem ser facilmente corrompidas por atribuições de valores fora da faixa permitida.

Na plataforma Java, um `enum` é um tipo de dado orientado a objetos completo:

- Todas as instâncias do enum herdam implicitamente de `java.lang.Enum`.
- O conjunto de constantes é estático, imutável e validado pelo compilador.
- Um enum pode declarar atributos próprios (`private final`), construtores parametrizados obrigatoriamente privados e métodos de negócio encapsulados.

```plaintext
┌─────────────────────────────────────────────────────────────────────────────┐
│             EVOLUÇÃO DO CONTROLE DE DOMÍNIOS DISCRETOS EM JAVA              │
├──────────────────────────────────────┬──────────────────────────────────────┤
│ Modelo Antigo (Magic Numbers)        │ Modelo Moderno (Tipos Enumerados)    │
├──────────────────────────────────────┼──────────────────────────────────────┤
│ public static final int NORTE = 1;   │ public enum RegiaoEntrega {          │
│ int rota = 99; // Compila sem erro,  │     NORTE(1.20), SUL(1.05);          │
│ gerando falha lógica em execução!   │ } // Impede dados fora do domínio!    │
└──────────────────────────────────────┴──────────────────────────────────────┘
```

## 3. Estudo de Caso Integrado: Motor de Cálculo Logístico de Fretes

O exemplo a seguir reúne a sobrecarga de métodos, o uso defensivo de varargs para pesagem e a aplicação de um enum rico associado a uma *Switch Expression* moderna:

```java
package br.edu.universidade.sistema.logistica;

// 1. Enum Avançado com Estado Imutável e Regra de Negócio Encapsulada
public enum RegiaoEntrega {
    SUDESTE(1.00, 1),
    SUL(1.15, 3),
    CENTRO_OESTE(1.25, 4),
    NORDESTE(1.40, 6),
    NORTE(1.60, 8);

    private final double multiplicadorTarifario; // Atributo imutável
    private final int prazoDiasUteis;

    // Construtor do enum: obrigatoriamente privado ou com visibilidade de pacote
    RegiaoEntrega(double multiplicadorTarifario, int prazoDiasUteis) {
        this.multiplicadorTarifario = multiplicadorTarifario;
        this.prazoDiasUteis = prazoDiasUteis;
    }

    public double getMultiplicadorTarifario() {
        return this.multiplicadorTarifario;
    }

    public int getPrazoDiasUteis() {
        return this.prazoDiasUteis;
    }

    // Método encapsulado de domínio dentro do próprio enum
    public double ajustarValorBase(double taxaBase) {
        return taxaBase * this.multiplicadorTarifario;
    }
}
```

```java
package br.edu.universidade.sistema.logistica;

// 2. Serviço com Sobrecarga de Métodos e Varargs Defensivo
public class CalculadoraFrete {

    private static final double TAXA_POR_QUILO = 5.50;

    // Sobrecarga 1: Cálculo simples para pacote único com destino padrão
    public double calcular(double pesoKg) {
        return calcular(pesoKg, RegiaoEntrega.SUDESTE); // Delegação de responsabilidade
    }

    // Sobrecarga 2: Cálculo para pacote único com região específica
    public double calcular(double pesoKg, RegiaoEntrega regiao) {
        if (pesoKg <= 0.0) {
            return 0.0;
        }
        if (regiao == null) {
            regiao = RegiaoEntrega.SUDESTE; // Fallback defensivo
        }
        double valorBase = pesoKg * TAXA_POR_QUILO;
        return regiao.ajustarValorBase(valorBase);
    }

    // Sobrecarga 3: Varargs para pesagem de múltiplos pacotes em lote
    public double calcular(RegiaoEntrega regiao, double... pesosItens) {
        if (pesosItens == null || pesosItens.length == 0) {
            return 0.0;
        }

        double pesoTotal = 0.0;
        for (double peso : pesosItens) {
            if (peso > 0.0) {
                pesoTotal += peso;
            }
        }

        return calcular(pesoTotal, regiao); // Reutiliza a regra da sobrecarga 2
    }

    // Exemplo de integração do enum com Switch Expression moderna
    public String emitirPrevisaoEntrega(RegiaoEntrega regiao) {
        return switch (regiao) {
            case SUDESTE -> "Entrega expressa regional: " + regiao.getPrazoDiasUteis() + " dia útil.";
            case SUL, CENTRO_OESTE -> "Entrega intermodal padrão: " + regiao.getPrazoDiasUteis() + " dias úteis.";
            case NORDESTE, NORTE -> "Entrega de longa distância: " + regiao.getPrazoDiasUteis() + " dias úteis.";
        };
    }
}
```

## 4. Diagnóstico de Erros Comuns e Armadilhas

### Armadilha 1: Posicionamento Inválido do Parâmetro Varargs

**Código com Falha:**

```java
public void despachar(double... pesos, String destino) { ... } // ERRO DE COMPILAÇÃO!
```

- **Diagnóstico da JVM:** `varargs parameter must be the last parameter`.
- **Causa & Correção:** A máquina virtual não tem como determinar onde termina a lista de pesos e onde começa a string de destino se o varargs vier primeiro. Mova o parâmetro variável para o final da assinatura: `despachar(String destino, double... pesos)`.

### Armadilha 2: Ambiguidade em Sobrecargas com Varargs

**Código com Falha:**

```java
public void processar(int x, int... valores) { ... }
public void processar(int... valores) { ... }

// No método chamador:
processar(10); // Qual das duas opções o compilador deve executar?
```

- **Diagnóstico da JVM:** `reference to processar is ambiguous`.
- **Causa & Correção:** Ambos os métodos atendem à chamada passando um único inteiro `10`. Evite declarar sobrecargas cuja resolução exija do compilador escolher entre chamadas diretas e argumentos variáveis que colidam na mesma quantidade de parâmetros.

### Armadilha 3: Disparo de NullPointerException ao Passar null Explícito em Varargs

**Código Problemático:**

```java
CalculadoraFrete calc = new CalculadoraFrete();
calc.calcular(RegiaoEntrega.SUL, null); // Passagem explícita de nulo
```

- **Diagnóstico Técnico:** A JVM compila a chamada repassando uma referência nula de array (`double[] = null`) para dentro do método. Ao tentar executar o laço `for (double peso : pesosItens)`, ocorre o desenrolamento da pilha com `NullPointerException`.
- **Correção:** Sempre inicie o método com uma verificação defensiva de nulidade: `if (pesosItens == null || pesosItens.length == 0) return 0.0;`.

## 5. Roteiro Prático de Execução e Testes

Para validar a resolução estática de sobrecargas e a cobertura do enum, construa e execute a classe de teste abaixo:

```java
package br.edu.universidade.sistema.logistica;

public class LogisticaApp {
    public static void main(String[] args) {
        CalculadoraFrete calc = new CalculadoraFrete();

        System.out.println("--- Teste 1: Sobrecarga com Pacote Único (Padrão Sudeste) ---");
        double frete1 = calc.calcular(10.0);
        System.out.printf("Valor do frete (10 kg): R$ %.2f%n", frete1);

        System.out.println("\n--- Teste 2: Sobrecarga com Região Específica ---");
        double frete2 = calc.calcular(10.0, RegiaoEntrega.NORTE);
        System.out.printf("Valor do frete (10 kg - Norte): R$ %.2f%n", frete2);

        System.out.println("\n--- Teste 3: Sobrecarga com Varargs (Múltiplos Pacotes) ---");
        // Passagem flexível de itens separados por vírgula
        double frete3 = calc.calcular(RegiaoEntrega.NORDESTE, 2.5, 4.0, 1.5, 3.0);
        System.out.printf("Valor do frete lote (11 kg - Nordeste): R$ %.2f%n", frete3);

        System.out.println("\n--- Teste 4: Avaliação de Prazos via Switch Expression ---");
        for (RegiaoEntrega regiao : RegiaoEntrega.values()) {
            System.out.printf("%-15s -> %s%n", regiao.name(), calc.emitirPrevisaoEntrega(regiao));
        }
    }
}
```

## 6. Exercício de Fixação Prática: Sistema de Faturamento de Assinaturas

Implemente um motor de precificação de assinaturas corporativas aplicando sobrecarga, argumentos variáveis e tipos enumerados ricos:

1. **Construa o Enum `NivelAssinatura`:**
   - Constantes: `BRONZE` (mensalidade base: R$ 80.00, limite de usuários inclusos: 3), `PRATA` (base: R$ 150.00, limite inclusos: 10) e `OURO` (base: R$ 300.00, limite inclusos: 25).
   - Atributos encapsulados: `private final double mensalidadeBase` e `private final int limiteUsuariosInclusos`.
   - Construtor parametrizado privado para associar os valores a cada constante.
   - Métodos acessores (*getters*) para ambos os campos.

2. **Construa a Classe `FaturamentoEngine`:**
   - Constante estática de classe: `public static final double TARIFA_USUARIO_EXCEDENTE = 15.00;`.
   - **Sobrecarga 1:** `public double calcularMensalidade(NivelAssinatura nivel)` — calcula a fatura cobrando apenas a mensalidade base do plano informado.
   - **Sobrecarga 2:** `public double calcularMensalidade(NivelAssinatura nivel, int totalUsuariosAtivos)` — calcula a fatura considerando a mensalidade base mais o custo de R$ 15,00 por usuário que exceder o limite estabelecido no plano.
   - **Sobrecarga 3 (com Varargs):** `public double calcularMensalidade(NivelAssinatura nivel, int... departamentos)` — recebe o plano contratado e uma lista flexível com a quantidade de usuários de cada departamento da empresa cliente; o método soma o total de usuários e reaproveita a regra da Sobrecarga 2.

3. **Construa a Classe Executável `BillingApp`:**
   - Instancie o motor de faturamento.
   - Realize simulações exibindo no console o valor faturado para cada um dos cenários de sobrecarga, comprovando a robustez dos cálculos diante de departamentos vazios ou sem excedentes.
