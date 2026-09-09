# Explicação da Aula 06 -- Sobrecarga de Métodos, Varargs e Enums Avançados

| | |
|---|---|
| **Disciplina** | Programação Orientada a Objetos |
| **Curso** | Análise e Desenvolvimento de Sistemas (3º Período) |
| **Tema** | Sobrecarga de Métodos (*Overloading*), Argumentos Variáveis (*Varargs*) e Tipos Enumerados (*Enums* Avançados) |
| **Carga Horária** | 100 minutos (2 horas-aula) |
| **Professor** | Alexandre Neves Louzada |
| **Plano de Aula** | `Aulas 3pob/Aula 06.md` |
| **Tutorial** | `aulas/TutorialAula6.md` |
| **Estudo de Caso** | `exemplos/aula-06/` |
| **Exercícios Resolvidos** | `solucoes/aula-06/` |

## 1. Objetivos de Aprendizagem

Esta aula introduz três pilares complementares da flexibilidade e segurança de tipos em Java: a sobrecarga de métodos, os argumentos variáveis (varargs) e os tipos enumerados (enums) com estado e comportamento próprio.

**Objetivos conceituais:** Compreender o polimorfismo estático em tempo de compilação (*Static / Early Binding*) por meio da sobrecarga de métodos; reconhecer as limitações e riscos de integridade de constantes inteiras soltas (*Magic Numbers*); entender que tipos enumerados (`enum`) em Java são tipos orientados a objetos completos, com construtores, atributos e métodos próprios.

**Objetivos técnicos:** Projetar métodos sobrecarregados baseando-se na assinatura reconhecida pela JVM (nome + lista ordenada de tipos de parâmetros); utilizar a sintaxe de *Varargs* (`Tipo...`), respeitando a restrição de unicidade e posicionamento final na assinatura; implementar enums avançados com atributos imutáveis (`final`), construtores parametrizados privados e métodos de cálculo encapsulados.

**Objetivos arquiteturais:** Integrar enums com estruturas condicionais modernas (*Switch Expressions*), garantindo cobertura exaustiva de cenários pelo compilador e eliminando o antipadrão *fall-through*.

**Objetivos práticos:** Implementar um motor de cálculo de frete e logística para e-commerce, combinando tabelas tarifárias baseadas em enums ricos e métodos sobrecarregados com *varargs* para pesagem individual e em lotes.

## 2. Conteúdo Teórico Detalhado

### 2.1 Sobrecarga de Métodos (*Method Overloading*)

Sobrecarga de métodos é um mecanismo que permite que uma mesma classe possua dois ou mais métodos com **exatamente o mesmo nome**, desde que suas listas de parâmetros sejam diferentes. Isso é um exemplo de **polimorfismo estático**, também chamado de polimorfismo em tempo de compilação (*Compile-time Polymorphism*) ou *Early Binding*, porque é o compilador quem decide qual versão do método será chamada, antes mesmo de o programa ser executado.

A **assinatura de método** como reconhecida pela JVM é definida pela seguinte fórmula:

> Assinatura = Nome do Método + Lista Ordenada dos Tipos dos Parâmetros

Essa definição traz implicações importantes. O compilador Java **não** diferencia sobrecargas com base em:

- **Tipo de retorno:** Dois métodos com o mesmo nome e os mesmos parâmetros, mas com tipos de retorno distintos, não constituem sobrecarga -- causam erro de compilação.
- **Nomes dos parâmetros:** Alterar apenas o nome dos parâmetros, mantendo o tipo e a ordem, não cria uma sobrecarga.
- **Modificadores de acesso:** Tornar um método `public` e outro `private` com a mesma assinatura de parâmetros também não é sobrecarga.

A regra de ouro é que o compilador utiliza apenas o **nome do método** e a **lista ordenada dos tipos** dos parâmetros para decidir qual método invocar.

### 2.2 Argumentos Variáveis (*Varargs*)

A sintaxe de *varargs* (`Tipo...`) permite que um método aceite **quantidade flexível** de argumentos, eliminando a necessidade de criar sobrecargas manuais para cada quantidade possível de parâmetros. A JVM interpreta internamente o parâmetro *varargs* como um array primitivo (`Tipo[]`).

Exemplo de sintaxe:

```java
public double calcular(double... pesosItens) {
    double total = 0.0;
    for (double p : pesosItens) {
        if (p > 0) total += p;
    }
    return total;
}
```

O chamador pode invocar `calcular()`, `calcular(10.5)`, `calcular(10.5, 20.0, 30.2)` ou até passar um array pré-alocado `calcular(new double[]{1, 2, 3})`.

O compilador impõe duas restrições estritas:

1. Apenas **um único** parâmetro *varargs* é permitido por método.
2. O *varargs* deve ser **obrigatoriamente o último** parâmetro da assinatura.

Um ponto de atenção é o risco de `NullPointerException` caso alguém passe `null` explícito para um parâmetro *varargs*, o que resultaria em uma falha em tempo de execução.

### 2.3 Tipos Enumerados (*Enums*) Avançados em Java

Em Java, um `enum` não é apenas um alias para inteiros sequenciais como em C/C++. É uma **classe completa** gerenciada pela JVM. Cada constante enumerada é, na verdade, uma instância de classe com estado imutável, construtor e métodos próprios.

A estrutura de um enum avançado inclui:

- **Constantes enumeradas** com argumentos no construtor.
- **Atributos finais** (imutáveis) que armazenam estado.
- **Construtor privado** (ou de visibilidade de pacote) que é chamado na declaração de cada constante.
- **Métodos de domínio** encapsulados que implementam regras de negócio diretamente no enum.

Exemplo conceitual (a estrutura do estudo de caso):

```java
public enum RegiaoEntrega {
    SUDESTE(1.00, 1),
    SUL(1.15, 3),
    CENTRO_OESTE(1.25, 4),
    NORDESTE(1.40, 6),
    NORTE(1.60, 8);

    private final double multiplicadorTarifario;
    private final int prazoDiasUteis;

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

    public double ajustarValorBase(double taxaBase) {
        return taxaBase * this.multiplicadorTarifario;
    }
}
```

Cada constante (`SUDESTE`, `SUL`, etc.) carrega seu próprio `multiplicadorTarifario` e `prazoDiasUteis`. O método `ajustarValorBase` encapsula a regra de ajuste tarifário, mantendo toda a lógica de domínio dentro do próprio enum.

### 2.4 *Switch Expressions* com Enums

A partir do Java 14, é possível usar *Switch Expressions* com enums, garantindo **cobertura exaustiva** (o compilador exige que todas as constantes sejam tratadas) e eliminando o antipadrão `fall-through`:

```java
public String emitirPrevisaoEntrega(RegiaoEntrega regiao) {
    return switch (regiao) {
        case SUDESTE -> "Entrega expressa regional: " + regiao.getPrazoDiasUteis() + " dia útil.";
        case SUL, CENTRO_OESTE -> "Entrega intermodal padrão: " + regiao.getPrazoDiasUteis() + " dias úteis.";
        case NORDESTE, NORTE -> "Entrega de longa distância: " + regiao.getPrazoDiasUteis() + " dias úteis.";
    };
}
```

Se uma nova constante for adicionada ao enum e não for tratada no *switch*, o compilador gera erro imediato, o que é uma grande vantagem em segurança de tipos.

### 2.5 Delegação de Responsabilidade entre Sobrecargas

Um padrão importante na sobrecarga de métodos é a **delegação**: uma sobrecarga mais genérica delega a responsabilidade para outra sobrecarga mais específica, evitando duplicação de código e centralizando a lógica principal em um único ponto.

No estudo de caso, a sobrecarga que recebe apenas `pesoKg` delega para a sobrecarga que recebe `pesoKg + RegiaoEntrega`, usando `RegiaoEntrega.SUDESTE` como padrão:

```java
public double calcular(double pesoKg) {
    return calcular(pesoKg, RegiaoEntrega.SUDESTE);
}

public double calcular(double pesoKg, RegiaoEntrega regiao) {
    // lógica central
}
```

E a sobrecarga com *varargs* soma todos os pesos e delega para a sobrecarga com soma total:

```java
public double calcular(RegiaoEntrega regiao, double... pesosItens) {
    double pesoTotal = 0.0;
    for (double peso : pesosItens) {
        if (peso > 0.0) pesoTotal += peso;
    }
    return calcular(pesoTotal, regiao);
}
```

## 3. Estudo de Caso Aplicado

O estudo de caso desta aula é um **motor de logística e-commerce** localizado em `exemplos/aula-06/src/br/edu/universidade/sistema/logistica/`. Ele é composto por três classes:

### 3.1 RegiaoEntrega.java -- Enum Avançado

O enum `RegiaoEntrega` define cinco regiões do Brasil, cada uma com um multiplicador tarifário e um prazo em dias úteis. Ele demonstra:

- Construtor privado que recebe os dois atributos imutáveis.
- Métodos *getter* para expor os dados.
- Método de domínio `ajustarValorBase()` que aplica o multiplicador ao valor base do frete.

```java
package br.edu.universidade.sistema.logistica;

public enum RegiaoEntrega {
    SUDESTE(1.00, 1),
    SUL(1.15, 3),
    CENTRO_OESTE(1.25, 4),
    NORDESTE(1.40, 6),
    NORTE(1.60, 8);

    private final double multiplicadorTarifario;
    private final int prazoDiasUteis;

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

    public double ajustarValorBase(double taxaBase) {
        return taxaBase * this.multiplicadorTarifario;
    }
}
```

### 3.2 CalculadoraFrete.java -- Sobrecarga e Varargs

A classe `CalculadoraFrete` demonstra três níveis de sobrecarga, partindo do simples para o complexo, e integrando o enum com *Switch Expression*:

```java
package br.edu.universidade.sistema.logistica;

public class CalculadoraFrete {

    private static final double TAXA_POR_QUILO = 5.50;

    // Sobrecarga 1: Pacote único com destino padrão (Sudeste)
    public double calcular(double pesoKg) {
        return calcular(pesoKg, RegiaoEntrega.SUDESTE);
    }

    // Sobrecarga 2: Pacote único com região específica
    public double calcular(double pesoKg, RegiaoEntrega regiao) {
        if (pesoKg <= 0.0) {
            return 0.0;
        }
        if (regiao == null) {
            regiao = RegiaoEntrega.SUDESTE;
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
        return calcular(pesoTotal, regiao);
    }

    // Integração do enum com Switch Expression moderna
    public String emitirPrevisaoEntrega(RegiaoEntrega regiao) {
        return switch (regiao) {
            case SUDESTE -> "Entrega expressa regional: " + regiao.getPrazoDiasUteis() + " dia útil.";
            case SUL, CENTRO_OESTE -> "Entrega intermodal padrão: " + regiao.getPrazoDiasUteis() + " dias úteis.";
            case NORDESTE, NORTE -> "Entrega de longa distância: " + regiao.getPrazoDiasUteis() + " dias úteis.";
        };
    }
}
```

Pontos-chave desta implementação:

- A sobrecarga 1 delega para a sobrecarga 2 com região padrão.
- A sobrecarga 2 é a central de lógica: valida os parâmetros, calcula o valor base e delega o ajuste para o enum.
- A sobrecarga 3 aceita *varargs*, soma os pesos válidos e delega para a sobrecarga 2.
- O método `emitirPrevisaoEntrega` usa *Switch Expression*, garantindo cobertura exaustiva de todas as constantes do enum.

### 3.3 LogisticaApp.java -- Demonstração

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
        double frete3 = calc.calcular(RegiaoEntrega.NORDESTE, 2.5, 4.0, 1.5, 3.0);
        System.out.printf("Valor do frete lote (11 kg - Nordeste): R$ %.2f%n", frete3);

        System.out.println("\n--- Teste 4: Avaliação de Prazos via Switch Expression ---");
        for (RegiaoEntrega regiao : RegiaoEntrega.values()) {
            System.out.printf("%-15s -> %s%n", regiao.name(), calc.emitirPrevisaoEntrega(regiao));
        }
    }
}
```

## 4. Exercícios Propostos e Solução

O exercício proposto na aula envolve criar um **motor de faturamento por assinatura** que aplica sobrecarga e *varargs* para calcular mensalidades. A solução completa está em `solucoes/aula-06/src/br/edu/universidade/sistema/faturamento/`.

### 4.1 NivelAssinatura.java -- Enum com Mensalidade e Limite

```java
package br.edu.universidade.sistema.faturamento;

public enum NivelAssinatura {
    BRONZE(80.00, 3),
    PRATA(150.00, 10),
    OURO(300.00, 25);

    private final double mensalidadeBase;
    private final int limiteUsuariosInclusos;

    NivelAssinatura(double mensalidadeBase, int limiteUsuariosInclusos) {
        this.mensalidadeBase = mensalidadeBase;
        this.limiteUsuariosInclusos = limiteUsuariosInclusos;
    }

    public double getMensalidadeBase() {
        return mensalidadeBase;
    }

    public int getLimiteUsuariosInclusos() {
        return limiteUsuariosInclusos;
    }
}
```

Cada nível de assinatura carrega a mensalidade base e o limite de usuários inclusos. O enum é imutável e fornece acesso aos dados por meio de *getters*.

### 4.2 FaturamentoEngine.java -- Três Sobrecargas

```java
package br.edu.universidade.sistema.faturamento;

public class FaturamentoEngine {

    public static final double TARIFA_USUARIO_EXCEDENTE = 15.00;

    // Sobrecarga 1: Apenas mensalidade base do plano
    public double calcularMensalidade(NivelAssinatura nivel) {
        validarNivel(nivel);
        return nivel.getMensalidadeBase();
    }

    // Sobrecarga 2: Base + excedente por usuário
    public double calcularMensalidade(NivelAssinatura nivel, int totalUsuariosAtivos) {
        validarNivel(nivel);
        if (totalUsuariosAtivos < 0) {
            throw new IllegalArgumentException("Total de usuários não pode ser negativo: " + totalUsuariosAtivos);
        }
        int excedentes = totalUsuariosAtivos - nivel.getLimiteUsuariosInclusos();
        double valor = nivel.getMensalidadeBase();
        if (excedentes > 0) {
            valor += excedentes * TARIFA_USUARIO_EXCEDENTE;
        }
        return valor;
    }

    // Sobrecarga 3: Varargs com quantidade de usuários por departamento
    public double calcularMensalidade(NivelAssinatura nivel, int... departamentos) {
        validarNivel(nivel);
        if (departamentos == null || departamentos.length == 0) {
            return nivel.getMensalidadeBase();
        }
        int totalUsuarios = 0;
        for (int qtd : departamentos) {
            if (qtd < 0) {
                throw new IllegalArgumentException("Quantidade de departamento não pode ser negativa: " + qtd);
            }
            totalUsuarios += qtd;
        }
        return calcularMensalidade(nivel, totalUsuarios);
    }

    private void validarNivel(NivelAssinatura nivel) {
        if (nivel == null) {
            throw new IllegalArgumentException("Nível de assinatura não pode ser nulo.");
        }
    }
}
```

A sobrecarga 3 com *varargs* soma os usuários de todos os departamentos e delega para a sobrecarga 2, mantendo a lógica centralizada.

### 4.3 BillingApp.java -- Demonstração

```java
package br.edu.universidade.sistema.faturamento;

public class BillingApp {
    public static void main(String[] args) {
        FaturamentoEngine engine = new FaturamentoEngine();

        System.out.println("--- Sobrecarga 1: Somente Mensalidade Base ---");
        for (NivelAssinatura nivel : NivelAssinatura.values()) {
            System.out.printf("%s -> Base: R$ %.2f | Limite incluso: %d usuários%n",
                    nivel.name(), nivel.getMensalidadeBase(), nivel.getLimiteUsuariosInclusos());
        }

        System.out.println("\n--- Sobrecarga 2: Mensalidade Base + Usuários Excedentes ---");
        System.out.printf("OURO com 30 usuários: R$ %.2f (5 excedentes x R$ 15,00)%n",
                engine.calcularMensalidade(NivelAssinatura.OURO, 30));
        System.out.printf("PRATA com 8 usuários: R$ %.2f (sem excedentes)%n",
                engine.calcularMensalidade(NivelAssinatura.PRATA, 8));
        System.out.printf("BRONZE com 3 usuários: R$ %.2f (dentro do limite)%n",
                engine.calcularMensalidade(NivelAssinatura.BRONZE, 3));

        System.out.println("\n--- Sobrecarga 3: Varargs com Departamentos ---");
        double fatura = engine.calcularMensalidade(NivelAssinatura.OURO, 8, 10, 9);
        System.out.printf("OURO com departamentos [8, 10, 9] = 27 usuários: R$ %.2f%n", fatura);
        double faturaVazia = engine.calcularMensalidade(NivelAssinatura.OURO);
        System.out.printf("OURO com departamentos vazios (varargs omissso): R$ %.2f%n", faturaVazia);
        double semExcedentes = engine.calcularMensalidade(NivelAssinatura.PRATA, 2, 3, 4);
        System.out.printf("PRATA com departamentos [2, 3, 4] = 9 usuários: R$ %.2f (sem excedentes)%n", semExcedentes);
    }
}
```

## 5. Perguntas de Revisão

1. Qual é a assinatura de método reconhecida pela JVM e por que ela é diferente da assinatura visível ao programador?
2. Por que o tipo de retorno não pode ser usado para diferenciar sobrecargas de métodos?
3. Quais são as duas restrições impostas pelo compilador ao uso de *varargs*?
4. Por que é considerado uma boa prática usar delegação entre sobrecargas em vez de duplicar lógica em cada versão?
5. Qual a diferença fundamental entre um `enum` em Java e um conjunto de constantes inteiras (`int`) em C?
6. Por que o construtor de um *enum* em Java é obrigatoriamente privado?
7. Qual a vantagem de usar *Switch Expression* com enums em relação ao *switch* tradicional?
8. Quando é apropriado usar *varargs* e quando é melhor criar sobrecargas explícitas?
9. O que acontece se você criar dois métodos na mesma classe com nomes iguais, parâmetros iguais e tipos de retorno diferentes?
10. Por que o enum `RegiaoEntrega` guarda seu próprio método `ajustarValorBase` em vez de colocar essa lógica na classe `CalculadoraFrete`?

## 6. Resumo / Pontos-Chave

- **Sobrecarga** é polimorfismo estático: o compilador resolve qual método chamar com base no nome e na lista ordenada dos tipos de parâmetros. Tipo de retorno, nomes de parâmetros e modificadores de acesso não contam.
- **Varargs** (`Tipo...`) oferece flexibilidade para listas dinâmicas de argumentos, mas está sujeito a duas restrições: apenas um por método e deve ser o último parâmetro.
- **Delegação** entre sobrecargas evita duplicação de código e mantém a lógica centralizada em um único método "raiz".
- **Enums em Java** são classes completas com construtor, atributos imutáveis e métodos de domínio. Eles substituem seguramente constantes inteiras soltas (*Magic Numbers*).
- **Switch Expressions** com enums garantem cobertura exaustiva pelo compilador e eliminam o antipadrão *fall-through*.
- O estudo de caso combina os três conceitos em um motor de logística: enum `RegiaoEntrega` com multiplicador e prazo, `CalculadoraFrete` com três sobrecargas (incluindo *varargs*), e `LogisticaApp` como ponto de entrada.
