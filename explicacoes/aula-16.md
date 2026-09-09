# Explicação da Aula 16 — Introdução a Metadados com Anotações

| | |
|---|---|
| **Disciplina** | Programação Orientada a Objetos |
| **Curso** | Análise e Desenvolvimento de Sistemas (3º Período) |
| **Tema** | Introdução a Metadados com Anotações (`@Override`, `@Deprecated`, `@SuppressWarnings` e Anotações Customizadas) |
| **Carga Horária** | 100 minutos (2 horas-aula) |
| **Professor** | Alexandre Neves Louzada |
| **Plano de Aula** | `Aulas 3pob/Aula 16.md` |
| **Tutorial** | `aulas/TutorialAula16.md` |
| **Estudo de Caso** | `exemplos/aula-16/` |
| **Exercícios Resolvidos** | `solucoes/aula-16/` |

---

## 1. Objetivos de Aprendizagem

**Conceitual:** Compreender o papel dos metadados no desenvolvimento de software moderno; entender a evolução histórica do ecossistema Java, especialmente a migração da verbosidade de arquivos XML para anotações declarativas no código; compreender o ciclo de vida dos metadados e as políticas de retenção na JVM.

**Técnico:** Aplicar anotações nativas do compilador para governança e segurança de código (`@Override`, `@Deprecated`, `@SuppressWarnings`); utilizar meta-anotações (`@Target`, `@Retention`, `@Documented`) para estruturar anotações customizadas; inspecionar programaticamente metadados em tempo de execução utilizando a API de *Reflection*.

**Arquitetural:** Projetar contratos de governança e segurança declarativa utilizando anotações de domínio de negócio; reconhecer a arquitetura declarativa utilizada por frameworks corporativos do mercado (Spring Boot, Jakarta EE, Hibernate/JPA e JUnit 5).

**Prático:** Refatorar uma API fiscal legada marcando métodos obsoletos com `@Deprecated(forRemoval = true)`, suprimir alertas de compilação justificados e criar uma anotação customizada de controle de acesso (`@PerfilAcesso`) processada dinamicamente em tempo de execução.

---

## 2. Conteúdo Teórico Detalhado

### 2.1. O Conceito de Metadados e a Sintaxe `@`

Metadados são definidos como "dados sobre dados". No contexto da plataforma Java, as anotações são marcadores estruturados que adicionam informações semânticas a elementos do código — classes, métodos, atributos, parâmetros ou pacotes — precedidos pelo símbolo `@`.

Um ponto fundamental é que anotações **não alteram diretamente a lógica de execução** do método ou classe onde estão colocadas. Elas servem como etiquetas semânticas para o compilador, ferramentas de análise estática (como o SonarQube) ou para o container do framework em tempo de execução.

Uma metáfora didática para entender o conceito: imagine uma etiqueta de remessa em uma caixa de mudança. A etiqueta "FRÁGIL" não muda o conteúdo dentro da caixa, mas orienta o transportador sobre como manuseá-la. Da mesma forma, uma anotação não altera o código, mas fornece instruções adicionais a quem vai processá-lo.

### 2.2. A Revolução do Java 5: De XML para Anotações

Antes do Java 5 (2004), a configuração de frameworks corporativos era feita predominantemente em arquivos XML extensos e verbosos. O Java 5 introduziu as anotações como forma declarativa de metadados diretamente no código-fonte, reduzindo drasticamente a complexidade de configuração e inaugurando o que se convencionou chamar de "metaprogramação declarativa".

Essa evolução é crucial para entender por que frameworks modernos como Spring Boot, Quarkus, Hibernate e JUnit 5 funcionam quase inteiramente baseados em anotações. Entender como elas operam por baixo dos panos demistifica a "mágica" desses frameworks.

### 2.3. Anotação Nativa: `@Override` — Blindagem Contra Erros de Sobrescrita

O `@Override` informa ao compilador que o método marcado pretende redefinir um método da superclasse ou interface. Seu uso é uma prática de segurança de código essencial.

Sem `@Override`, ao errar a digitação de um método (por exemplo, escrever `equals(Cliente outro)` em vez de `equals(Object outro)`), o compilador cria uma **sobrecarga acidental** em vez de uma sobrescrita, gerando um bug silencioso que pode passar despercebido por muito tempo.

```java
// SEM @Override: Se errar a digitação, cria uma SOBRECARGA acidental!
public boolean equals(Cliente outro) { ... } // Bug silencioso!

// COM @Override: O compilador valida a assinatura exata
@Override
public boolean equals(Object outro) { ... } // Se errar, O COMPILADOR TRAVA!
```

No estudo de caso da aula, observamos o uso correto do `@Override` na classe `LancamentoFinanceiro` para os métodos `equals`, `hashCode` e `toString`:

```java
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    LancamentoFinanceiro that = (LancamentoFinanceiro) o;
    return Objects.equals(id, that.id);
}

@Override
public int hashCode() {
    return Objects.hash(id);
}

@Override
public String toString() {
    return String.format("[#%04d] %-20s | C. Custo: %-12s | Valor: R$ %10.2f | Data: %s",
            id, descricao, centroCusto, valor, dataLancamento);
}
```

### 2.4. Anotação Nativa: `@Deprecated` — Descontinuação Controlada de API

O `@Deprecated` marca um elemento (classe, método ou atributo) como obsoleto. Quando um desenvolvedor tenta usar um elemento marcado com essa anotação, o compilador gera um aviso. A partir do Java 9, o atributo `forRemoval = true` indica que o elemento será removido em uma versão futura da API.

| Atributo | Descrição |
|---|---|
| `since` | Indica a versão em que o elemento foi descontinuado |
| `forRemoval` | Quando `true`, indica que o elemento será removido em versão futura |

### 2.5. Anotação Nativa: `@SuppressWarnings` — Controle de Alertas do Compilador

O `@SuppressWarnings` suprime avisos gerados pelo compilador durante a compilação. É útil em situações onde o alerta é conhecido e justificado, evitando a poluição visual de dezenas de warnings no projeto.

| Valor Comum | Descrição |
|---|---|
| `"unchecked"` | Suprime alertas de operações com generics não verificadas em tempo de compilação |
| `"deprecation"` | Suprime alertas sobre o uso de APIs marcadas como `@Deprecated` |
| `"unused"` | Suprime alertas sobre variáveis, parâmetros ou métodos não utilizados |
| `"all"` | Suprime todos os alertas (uso desencorajado por esconder problemas reais) |

### 2.6. Anotações Customizadas: Meta-anotações e Criação Própria

Uma anotação customizada é criada definindo uma interface com a anotação `@interface` e utilizando meta-anotações para configurar seu comportamento:

| Meta-anotação | Função | Valores Principais |
|---|---|---|
| `@Target` | Define onde a anotação pode ser aplicada | `TYPE`, `METHOD`, `FIELD`, `PARAMETER`, `CONSTRUCTOR`, `PACKAGE` |
| `@Retention` | Define quando a anotação está disponível | `SOURCE` (compilador), `RUNTIME` (reflexão em execução), `CLASS` (bytecode, padrão) |
| `@Documented` | Inclui a anotação na documentação Javadoc | Sem valores — apenas sinaliza |

Exemplo de uma anotação customizada de controle de acesso:

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PerfilAcesso {
    String valor();
}
```

### 2.7. Inspecionamento via Reflection

A API de reflexão (`java.lang.reflect`) permite inspecionar e manipular metadados em tempo de execução. É o mecanismo que frameworks como Spring utilizam para detectar anotações e tomar decisões de execução.

```java
Method metodo = clazz.getMethod("nomeDoMetodo");
if (metodo.isAnnotationPresent(PerfilAcesso.class)) {
    PerfilAcesso anotacao = metodo.getAnnotation(PerfilAcesso.class);
    String perfil = anotacao.valor();
    // Lógica de autorização baseada no perfil
}
```

---

## 3. Estudo de Caso Aplicado

O estudo de caso da aula 16 é o **Sistema Financeiro Contábil** da universidade, situado no pacote `br.edu.universidade.sistema.financeiro`. Ele demonstra o uso integrado de anotações (`@Override`), tipos opcionais (`Optional`, `OptionalDouble`) e Streams primitivas (`DoubleStream`) em um contexto real de fechamento contábil.

### 3.1. Entidade de Domínio: `LancamentoFinanceiro`

Arquivo: `exemplos/aula-16/src/br/edu/universidade/sistema/financeiro/dominio/LancamentoFinanceiro.java`

A classe representa um lançamento contábil com campos imutáveis (todos `final`): `id`, `descricao`, `centroCusto`, `valor` e `dataLancamento`. O construtor realiza validação de integridade, rejeitando valores nulos no ID ou valores iguais/menores que zero para o campo `valor`.

```java
public class LancamentoFinanceiro {
    private final Long id;
    private final String descricao;
    private final String centroCusto;
    private final double valor;
    private final LocalDate dataLancamento;

    public LancamentoFinanceiro(Long id, String descricao, String centroCusto, double valor, LocalDate data) {
        if (id == null || valor <= 0.0) {
            throw new IllegalArgumentException("Parametros do lancamento invalidos.");
        }
        this.id = id;
        this.descricao = descricao;
        this.centroCusto = centroCusto.toUpperCase();
        this.valor = valor;
        this.dataLancamento = data;
    }
}
```

Os métodos `equals` e `hashCode` são sobrescritos com `@Override`, utilizando `Objects.equals` e `Objects.hash` para comparação baseada no `id`. O `toString` gera uma representação formatada em tabela para impressão legível no console.

### 3.2. Serviço: `FechamentoContabilService`

Arquivo: `exemplos/aula-16/src/br/edu/universidade/sistema/financeiro/service/FechamentoContabilService.java`

Este serviço demonstra cinco operações financeiras típicas utilizando Streams e tipos opcionais:

| Método | Retorno | Descrição |
|---|---|---|
| `calcularVolumeTotalPorCentroCusto(List, String)` | `double` | Usa `DoubleStream` para somar valores filtrados por centro de custo |
| `calcularTicketMedio(List)` | `OptionalDouble` | Retorna a média aritmética, segura para listas vazias |
| `buscarMaiorLancamento(List)` | `Optional<LancamentoFinanceiro>` | Encontra o maior lançamento com `max(Comparator.comparingDouble(...))` |
| `buscarPorId(List, Long)` | `Optional<LancamentoFinanceiro>` | Busca pontual por ID usando `findFirst()` |
| `extrairMetricasGlobais(List)` | `DoubleSummaryStatistics` | Gera relatório estatístico consolidado (count, sum, min, max, average) |

Destaque para a conversão via `mapToDouble(LancamentoFinanceiro::getValor)` que produz um `DoubleStream`, eliminando o autoboxing para `Double` e otimizando o uso de memória em pipelines de grande volume.

### 3.3. Aplicação Executável: `ContabilidadeApp`

Arquivo: `exemplos/aula-16/src/br/edu/universidade/sistema/financeiro/ContabilidadeApp.java`

A aplicação principal demonstra o consumo defensivo do `Optional` com cinco cenários:

1. **Volume total via DoubleStream:** Soma de todos os gastos do centro "TECNOLOGIA".
2. **Média via OptionalDouble:** Cálculo seguro com verificação `isPresent()` antes de acessar o valor.
3. **Maior lançamento com `ifPresent`:** Consumo funcional do `Optional` sem bloco `if` explícito.
4. **Busca com fallback:** Dois casos — sucesso com `orElseThrow()` (lança `NoSuchElementException`) e ausência tratada com `orElse()` usando um lançamento sentinela padrão.
5. **Métricas consolidadas:** Uso de `DoubleSummaryStatistics` para extrair count, sum, min, max e average em uma única passada.

---

## 4. Exercícios Propostos e Solução

### Exercício: Sistema de Auditoria de Vendas

O exercício proposto na aula consiste em aplicar os mesmos conceitos (Streams, Optional, tipos primitivos e anotações) em um domínio diferente: o de vendas por representante comercial.

### 4.1. Entidade: `VendaRepresentante`

Arquivo: `solucoes/aula-16/src/br/edu/universidade/sistema/vendas/dominio/VendaRepresentante.java`

```java
public class VendaRepresentante {
    private Long idVenda;
    private String nomeRepresentante;
    private String regiao;
    private double valorVenda;
    private double comissaoPaga;

    public VendaRepresentante(Long idVenda, String nomeRepresentante, String regiao,
                              double valorVenda, double comissaoPaga) {
        if (idVenda == null) {
            throw new IllegalArgumentException("ID da venda nao pode ser nulo.");
        }
        if (comissaoPaga < 0.0) {
            throw new IllegalArgumentException("Comissao nao pode ser negativa: " + comissaoPaga);
        }
        if (valorVenda <= 0.0) {
            throw new IllegalArgumentException("Valor da venda deve ser maior que zero: " + valorVenda);
        }
        this.idVenda = idVenda;
        this.nomeRepresentante = nomeRepresentante;
        this.regiao = regiao;
        this.valorVenda = valorVenda;
        this.comissaoPaga = comissaoPaga;
    }

    public Long getIdVenda() { return idVenda; }
    public String getNomeRepresentante() { return nomeRepresentante; }
    public String getRegiao() { return regiao; }
    public double getValorVenda() { return valorVenda; }
    public double getComissaoPaga() { return comissaoPaga; }

    @Override
    public String toString() {
        return String.format("Venda [ID: %d | Repr: %s | Regiao: %s | Valor: R$ %.2f | Comissao: R$ %.2f]",
                idVenda, nomeRepresentante, regiao, valorVenda, comissaoPaga);
    }
}
```

### 4.2. Serviço: `AuditoriaVendasService`

Arquivo: `solucoes/aula-16/src/br/edu/universidade/sistema/vendas/service/AuditoriaVendasService.java`

| Método | Retorno | Descrição |
|---|---|---|
| `calcularFaturamentoTotalPorRegiao(List, String)` | `double` | Soma de vendas filtradas por região (ignora maiúsculas/minúsculas) |
| `calcularMediaComissoesPagas(List)` | `OptionalDouble` | Média de comissões pagas, segura para listas vazias |
| `buscarMaiorVendaPorRepresentante(List, String)` | `Optional<VendaRepresentante>` | Maior venda de um representante específico |
| `obterVendaComGarantia(List, Long)` | `VendaRepresentante` | Busca por ID com `orElseThrow()` — lança exceção se não encontrar |

```java
public double calcularFaturamentoTotalPorRegiao(List<VendaRepresentante> vendas, String regiao) {
    return vendas.stream()
            .filter(v -> v.getRegiao().equalsIgnoreCase(regiao))
            .mapToDouble(VendaRepresentante::getValorVenda)
            .sum();
}

public Optional<VendaRepresentante> buscarMaiorVendaPorRepresentante(
        List<VendaRepresentante> vendas, String nome) {
    return vendas.stream()
            .filter(v -> v.getNomeRepresentante().equalsIgnoreCase(nome))
            .max(Comparator.comparingDouble(VendaRepresentante::getValorVenda));
}
```

### 4.3. Aplicação: `VendasAnalyticsApp`

Arquivo: `solucoes/aula-16/src/br/edu/universidade/sistema/vendas/VendasAnalyticsApp.java`

```java
public class VendasAnalyticsApp {
    public static void main(String[] args) {
        AuditoriaVendasService service = new AuditoriaVendasService();

        List<VendaRepresentante> vendas = List.of(
                new VendaRepresentante(1L, "Carlos Prado", "SUL", 8500.00, 700.00),
                new VendaRepresentante(2L, "Beatriz Costa", "SUDESTE", 12000.00, 960.00),
                new VendaRepresentante(3L, "Carlos Prado", "SUL", 4500.00, 350.00),
                new VendaRepresentante(4L, "Lucas Mendes", "NORTE", 3200.00, 210.00)
        );

        System.out.printf("Regiao SUL     : R$ %.2f%n",
                service.calcularFaturamentoTotalPorRegiao(vendas, "SUL"));

        OptionalDouble media = service.calcularMediaComissoesPagas(vendas);
        System.out.printf("Ticket medio de comissoes: R$ %.2f%n", media.orElse(0.0));

        service.buscarMaiorVendaPorRepresentante(vendas, "Carlos Prado")
                .ifPresent(System.out::println);

        Optional<VendaRepresentante> inexistente =
                service.buscarMaiorVendaPorRepresentante(vendas, "Ana Clara");
        VendaRepresentante resultado = inexistente.orElseGet(() -> {
            System.out.println("[AVISO] Nenhuma venda registrada para 'Ana Clara'.");
            return null;
        });

        var garantia = service.obterVendaComGarantia(vendas, 3L);
        System.out.println("Garantia: " + garantia);
    }
}
```

---

## 5. Perguntas de Revisão

1. Qual a diferença fundamental entre dados e metadados no contexto de programação Java?
2. Por que o `@Override` é considerado uma ferramenta de segurança de código e não apenas estilística?
3. Em que situações o uso de `@SuppressWarnings("unchecked")` é justificado e quando ele pode esconder bugs reais?
4. Qual a diferença entre os níveis de retenção `SOURCE`, `CLASS` e `RUNTIME` na meta-anotação `@Retention`?
5. Por que o construtor da classe `LancamentoFinanceiro` valida os parâmetros antes de atribuí-los aos campos?
6. Qual a vantagem de usar `DoubleStream` em vez de `Stream<Double>` para operações de soma em listas de valores financeiros?
7. Explique por que o método `buscarPorId` retorna `Optional<LancamentoFinanceiro>` em vez de `null`.
8. O que acontece quando o método `orElseThrow()` é invocado em um `Optional` vazio?
9. Como o método `mapToDouble` elimina o autoboxing e quais são as vantagens de performance resultantes?
10. Por que a entidade `LancamentoFinanceiro` sobrescreve `equals` e `hashCode` juntos?

---

## 6. Resumo / Pontos-Chave

- **Metadados** são informações semânticas adicionadas ao código-fonte que não alteram diretamente a lógica de execução, mas fornecem instruções para compiladores, ferramentas e frameworks.
- **`@Override`** garante que um método seja efetivamente uma sobrescrita, prevenindo sobrecargas acidentais e bugs silenciosos.
- **`@Deprecated`** sinaliza elementos obsoletos com suporte ao atributo `forRemoval` para descontinuação planejada.
- **`@SuppressWarnings`** controla alertas do compilador, sendo útil quando o warning é conhecido e aceitável.
- **Anotações customizadas** são criadas com `@interface` e configuradas via meta-anotações `@Target`, `@Retention` e `@Documented`.
- **Reflection** permite inspecionar metadados em tempo de execução, sendo o mecanismo fundamental por trás de frameworks como Spring e JUnit.
- **`Optional<T>`** e **`OptionalDouble`** eliminam o risco de `NullPointerException` em operações que podem não ter resultado.
- **`DoubleStream`** e **`mapToDouble`** otimizam operações numéricas ao evitar autoboxing entre tipos primitivos eWrapper Classes.
- O estudo de caso **FechamentoContabilService** integra anotações, Streams primitivas e tipos opcionais em um contexto real de contabilidade corporativa.
