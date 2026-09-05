# Tutorial de Java — Aula 15: Streams API, Pipelines de Processamento e Operações Intermediárias vs. Terminais

| | |
|---|---|
| **Disciplina** | Programação Orientada a Objetos |
| **Curso** | Análise e Desenvolvimento de Sistemas (3º Período) |
| **Tema** | A Streams API (java.util.stream.Stream), Iteração Externa vs. Interna, Imutabilidade, Operações Intermediárias (filter, map, sorted, distinct, limit) e Operações Terminais (forEach, collect, count, anyMatch) |
| **Carga Horária** | 100 minutos (2 horas-aula) |
| **Professor Responsável** | Alexandre Neves Louzada |
| **Arquivo de Referência** | `TutorialAula15.md` |

## 1. Objetivos de Aprendizagem

- **Conceitual:** Compreender o modelo de processamento de dados via fluxos declarativos (*Streams*); diferenciar o controle imperativo por iteração externa (laços `for` e `while`) da iteração interna delegada à JVM; assimilar o princípio da imutabilidade da fonte de dados de origem.
- **Técnico:** Construir pipelines de processamento encadeados dividindo claramente operações intermediárias (transformações preguiçosas / *lazy*) de operações terminais (gatilhos de execução ávida / *eager*); dominar as operações fundamentais `filter`, `map`, `sorted`, `distinct`, `limit`, `skip`, `toList`, `collect` e reduções de consulta (`anyMatch`, `allMatch`, `count`).
- **Arquitetural:** Compreender o ciclo de vida de uma Stream (criação → etapas intermediárias → fechamento terminal), reconhecendo que uma Stream é um fluxo de uso único que é consumido e não pode ser reutilizado após o disparo da operação terminal.
- **Prático:** Implementar um motor de inteligência analítica e auditoria de compras de um portal de comércio eletrônico, filtrando transações suspeitas, transformando modelos de dados e gerando listas sumarizadas de auditoria.

## 2. Fundamentação Teórica

### O Que é a Streams API?

Introduzida no Java 8 com o pacote `java.util.stream`, a Streams API representa uma sequência de elementos que suporta diferentes métodos de agregação e transformação executados de forma sequencial ou paralela.

Existe uma distinção arquitetural indispensável entre Coleções e Streams:

```plaintext
┌─────────────────────────────────────────────────────────────────────────────┐
│                      COLEÇÃO vs. STREAM                                     │
├──────────────────────────────────────┬──────────────────────────────────────┤
│ Coleções (List, Set, Map)            │ Streams (Stream<T>)                  │
├──────────────────────────────────────┼──────────────────────────────────────┤
│ • Estrutura de dados em memória      │ • Estrutura de computação / fluxo    │
│ • Foco no ARMAZENAMENTO dos itens    │ • Foco no PROCESSAMENTO dos itens    │
│ • Iteração externa manual (for/while)│ • Iteração interna (JVM orquestra)   │
│ • Mutável ou imutável                │ • Não altera a fonte original        │
│ • Pode ser percorrida várias vezes   │ • Consumida só uma vez (descartável) │
└──────────────────────────────────────┴──────────────────────────────────────┘
```

### Iteração Externa vs. Iteração Interna

No modelo tradicional imperativo (iteração externa), o programador é responsável por gerenciar a navegação, os contadores e as estruturas temporárias de acumulação:

```java
// ITERAÇÃO EXTERNA (Imperativa, verbosa e propensa a efeitos colaterais):
List<String> aprovados = new ArrayList<>();
for (Aluno a : turma) {
    if (a.getNota() >= 7.0) {
        aprovados.add(a.getNome().toUpperCase());
    }
}
```

Na Streams API, utiliza-se iteração interna (paradigma declarativo): a aplicação apenas descreve o objetivo da operação, transferindo o controle do laço físico para o motor interno da JVM:

```java
// ITERAÇÃO INTERNA (Declarativa, concisa e autoexplicativa):
List<String> aprovados = turma.stream()
        .filter(a -> a.getNota() >= 7.0)
        .map(a -> a.getNome().toUpperCase())
        .toList();
```

### Anatomia de um Pipeline de Stream

Um pipeline de processamento é composto estritamente por três fases encadeadas:

> 1. Fonte de Dados → 2. Operações Intermediárias (0 ou mais) → 3. Operação Terminal (Obrigatória)

```plaintext
[ List<Pedido> ] ───► .stream()
                           │
                           ▼
                    .filter(p -> p.isPago())      ◄── Operação Intermediária (Lazy)
                           │
                           ▼
                    .map(Pedido::getValor)        ◄── Operação Intermediária (Lazy)
                           │
                           ▼
                    .sorted()                     ◄── Operação Intermediária (Lazy)
                           │
                           ▼
                    .toList()                     ◄── Operação Terminal (Dispara o Pipeline)
```

- **Fonte de Dados:** Uma coleção (`lista.stream()`), um array primitivo (`Arrays.stream(array)`), valores estáticos (`Stream.of("A", "B")`) ou arquivos em disco (`Files.lines(path)`).
- **Operações Intermediárias (Avaliação Preguiçosa / Lazy Evaluation):** Métodos que recebem uma Stream e retornam uma nova Stream modificada. Elas não processam nenhum dado imediatamente. Elas apenas registram a regra de transformação em um plano de execução.
- **Operação Terminal (Execução Ávida / Eager Evaluation):** Método final que dispara o processamento dos dados através do encadeamento registrado, produzindo um resultado concreto (uma nova `List`, um número primitivo, um booleano ou um efeito colateral de impressão) e fechando a Stream.

### Catálogo das Operações Intermediárias e Terminais Mais Frequentes

| Categoria | Método | Assinatura Funcional | Finalidade / Efeito |
|---|---|---|---|
| Intermediária | `filter(Predicate<T>)` | `Predicate<T>` | Descarta elementos que não atendem à condição booleana. |
| Intermediária | `map(Function<T, R>)` | `Function<T, R>` | Transforma cada elemento do tipo T projetando-o no tipo R. |
| Intermediária | `sorted()` | `Comparator<T>` | Ordena os elementos segundo a ordem natural (`Comparable`) ou customizada. |
| Intermediária | `distinct()` | Baseada em `equals`/`hashCode` | Elimina elementos duplicados do fluxo com base na identidade de negócio. |
| Intermediária | `limit(long n)` | Corte posicional | Trunca o fluxo permitindo passar no máximo `n` elementos. |
| Intermediária | `skip(long n)` | Descarte inicial | Descarta os primeiros `n` elementos do fluxo. |
| Terminal | `toList()` | Java 16+ | Coleta os elementos resultantes em uma `List` imutável. |
| Terminal | `collect(Collector)` | `java.util.stream.Collectors` | Coleta em coleções específicas (`Collectors.toCollection(ArrayList::new)`). |
| Terminal | `forEach(Consumer<T>)` | `Consumer<T>` | Aplica uma ação/efeito colateral a cada item processado. |
| Terminal | `count()` | Nenhuma | Retorna o total (`long`) de elementos que atingiram o final do fluxo. |
| Terminal | `anyMatch(Predicate<T>)` | `Predicate<T>` | Retorna `true` se ao menos um elemento satisfizer o predicado (com curto-circuito). |

## 3. Estudo de Caso Integrado: Motor de Auditoria e Faturamento E-Commerce

O código a seguir implementa uma esteira de processamento de compras eletrônicas, aplicando filtros, transformações e ordenações através de pipelines declarativos:

```java
package br.edu.universidade.sistema.ecommerce.dominio;

import java.util.Objects;

// 1. Entidade de Domínio representando o Pedido
public class PedidoCompra {
    private final Long id;
    private final String cliente;
    private final String categoria;
    private final double valorTotal;
    private final boolean pago;

    public PedidoCompra(Long id, String cliente, String categoria, double valorTotal, boolean pago) {
        this.id = id;
        this.cliente = cliente;
        this.categoria = categoria;
        this.valorTotal = valorTotal;
        this.pago = pago;
    }

    public Long getId() { return id; }
    public String getCliente() { return cliente; }
    public String getCategoria() { return categoria; }
    public double getValorTotal() { return valorTotal; }
    public boolean isPago() { return pago; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PedidoCompra that = (PedidoCompra) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Pedido #%03d | Cliente: %-15s | Categoria: %-10s | Total: R$ %8.2f | Pago: %s",
                id, cliente, categoria, valorTotal, pago ? "SIM" : "NÃO");
    }
}
```

```java
package br.edu.universidade.sistema.ecommerce.service;

import br.edu.universidade.sistema.ecommerce.dominio.PedidoCompra;
import java.util.Comparator;
import java.util.List;

// 2. Serviço de Auditoria e Análise usando Pipelines da Streams API
public class AuditoriaPedidosService {

    // Pipeline 1: Filtra apenas pedidos pagos de uma categoria, ordenando do maior para o menor valor
    public List<PedidoCompra> obterPedidosPagosOrdenadosPorValor(List<PedidoCompra> pedidos, String categoria) {
        return pedidos.stream()
                .filter(PedidoCompra::isPago) // Operação Intermediária: apenas confirmados
                .filter(p -> p.getCategoria().equalsIgnoreCase(categoria)) // Operação Intermediária
                .sorted(Comparator.comparing(PedidoCompra::getValorTotal).reversed()) // Intermediária: decrescente
                .toList(); // Operação Terminal (Java 16+)
    }

    // Pipeline 2: Extrai uma lista com os nomes únicos de clientes que fizeram pedidos de alto valor (> R$ 1.000)
    public List<String> obterNomesClientesVip(List<PedidoCompra> pedidos) {
        return pedidos.stream()
                .filter(p -> p.getValorTotal() >= 1000.0) // Filtro de valor mínimo
                .map(PedidoCompra::getCliente) // Projeta PedidoCompra -> String (Nome)
                .map(String::toUpperCase) // Transforma o texto para maiúsculo
                .distinct() // Elimina nomes duplicados
                .sorted() // Ordena alfabeticamente
                .toList(); // Operação Terminal
    }

    // Pipeline 3: Consulta de verificação se existe algum pedido pendente com valor crítico (> R$ 5.000)
    public boolean existeRiscoFraudePendente(List<PedidoCompra> pedidos) {
        return pedidos.stream()
                .filter(p -> !p.isPago()) // Pedidos ainda não compensados
                .anyMatch(p -> p.getValorTotal() > 5000.00); // Terminal com curto-circuito
    }

    // Pipeline 4: Retorna os TOP 3 maiores pedidos registrados na base
    public List<PedidoCompra> obterTop3MaioresPedidos(List<PedidoCompra> pedidos) {
        return pedidos.stream()
                .sorted(Comparator.comparing(PedidoCompra::getValorTotal).reversed())
                .limit(3) // Corta o fluxo após os 3 primeiros
                .toList();
    }
}
```

```java
package br.edu.universidade.sistema.ecommerce;

import br.edu.universidade.sistema.ecommerce.dominio.PedidoCompra;
import br.edu.universidade.sistema.ecommerce.service.AuditoriaPedidosService;
import java.util.List;

// 3. Aplicação Executável demonstrando a execução das Streams
public class StreamsApp {
    public static void main(String[] args) {
        List<PedidoCompra> basePedidos = List.of(
                new PedidoCompra(101L, "Carlos Eduardo", "ELETRONICOS", 1200.00, true),
                new PedidoCompra(102L, "Beatriz Costa", "LIVROS", 150.00, true),
                new PedidoCompra(103L, "Carlos Eduardo", "ELETRONICOS", 3500.00, true),
                new PedidoCompra(104L, "Ana Clara", "ELETRONICOS", 850.00, false),
                new PedidoCompra(105L, "Lucas Mendes", "MOVEIS", 2200.00, true),
                new PedidoCompra(106L, "Mariana Silva", "ELETRONICOS", 6200.00, false),
                new PedidoCompra(107L, "Beatriz Costa", "ELETRONICOS", 1800.00, true)
        );

        AuditoriaPedidosService service = new AuditoriaPedidosService();

        System.out.println("--- 1. Pedidos Pagos da Categoria ELETRONICOS (Decrescente por Valor) ---");
        List<PedidoCompra> eletronicosPagos = service.obterPedidosPagosOrdenadosPorValor(basePedidos, "ELETRONICOS");
        eletronicosPagos.forEach(System.out::println);

        System.out.println("\n--- 2. Lista de Clientes VIP Sem Repetições (Compras >= R$ 1.000) ---");
        List<String> clientesVip = service.obterNomesClientesVip(basePedidos);
        clientesVip.forEach(nome -> System.out.println("VIP: " + nome));

        System.out.println("\n--- 3. Auditoria de Segurança contra Fraude ---");
        boolean alertaFraude = service.existeRiscoFraudePendente(basePedidos);
        System.out.println("Existe pedido pendente com valor de risco crítico (> R$ 5.000)? " + alertaFraude);

        System.out.println("\n--- 4. TOP 3 Maiores Compras do Portal ---");
        List<PedidoCompra> top3 = service.obterTop3MaioresPedidos(basePedidos);
        top3.forEach(System.out::println);

        System.out.println("\n--- 5. Comprovação da Imutabilidade da Fonte Original ---");
        System.out.printf("Total de registros originais na lista intacta: %d%n", basePedidos.size());
    }
}
```

## 4. Diagnóstico de Erros Comuns e Armadilhas

### Armadilha 1: Tentar Reutilizar uma Stream Já Consumida

**Código Problemático:**

```java
Stream<String> fluxoNomes = List.of("Ana", "Carlos", "Beatriz").stream();
long total = fluxoNomes.count(); // Operação terminal disparada: FECHOU O FLUXO!

fluxoNomes.forEach(System.out::println); // ERRO DE EXECUÇÃO!
```

- **Diagnóstico da JVM:** `java.lang.IllegalStateException: stream has already been operated upon or closed` lançada em tempo de execução.
- **Causa & Correção:** Uma Stream possui ciclo de vida descartável. Uma vez invocada qualquer operação terminal, o pipeline é consumido e finalizado. Para realizar um novo processamento, abra uma nova Stream invocando `.stream()` novamente sobre a coleção de origem.

### Armadilha 2: Construir Pipelines Intermediários sem Nenhuma Operação Terminal

**Código Problemático:**

```java
// O desenvolvedor esperava que as mensagens fossem filtradas e impressas
lista.stream()
     .filter(item -> {
         System.out.println("Avaliando: " + item);
         return item.startsWith("A");
     }); // NENHUMA OPERAÇÃO TERMINAL FOI DECLARADA!
```

- **Diagnóstico Técnico:** O programa compila normalmente e roda sem erros, mas nada é impresso no console. Como as operações intermediárias são estritamente *lazy* (preguiçosas), a JVM só inicia o laço se houver uma operação terminal solicitando um resultado. Adicione `.toList()`, `.count()` ou `.forEach(...)` para acionar a execução do pipeline.

### Armadilha 3: Alterar o Estado da Coleção Fonte Dentro do Pipeline

**Código Problemático:**

```java
List<String> itens = new ArrayList<>(List.of("A", "B", "C"));
itens.stream()
     .filter(i -> {
         if (i.equals("B")) itens.remove(i); // EFEITO COLATERAL DESTRUTIVO!
         return true;
     })
     .forEach(System.out::println);
```

- **Diagnóstico da JVM:** `java.util.ConcurrentModificationException` lançada em tempo de execução.
- **Causa & Correção:** As operações de uma Stream devem ser puras e sem efeitos colaterais na estrutura da coleção fonte durante a travessia. As remoções e filtragens devem ser feitas retornando uma nova lista com `.toList()`.

## 5. Roteiro Prático de Depuração: Visualizando a Execução do Pipeline na IDE

Para inspecionar passo a passo os dados percorrendo as etapas intermediárias na sua IDE (utilizando o *Stream Trace* do IntelliJ IDEA ou equivalente):

1. No método `main` de `StreamsApp`, coloque um ponto de interrupção (*breakpoint*) na linha do pipeline: `List<PedidoCompra> eletronicosPagos = service.obterPedidosPagosOrdenadosPorValor(...)`.
2. Inicie a execução em modo de depuração (*Debug*).
3. Na barra de ferramentas da janela de *Debug*, clique no ícone *Trace Current Stream Chain* (ou utilize o plugin de visualização de streams da sua IDE):
   - A IDE abrirá uma tela gráfica dividida em abas correspondentes a cada método: `basePedidos` → `filter (isPago)` → `filter (categoria)` → `sorted` → `toList`.
   - Clique na aba do primeiro `filter`: observe os elementos não pagos (como os pedidos #104 e #106) sendo destacados e eliminados da esteira.
   - Clique na aba do `sorted`: visualize como a ordem dos objetos é reorganizada para o critério decrescente antes da entrega final no `.toList()`.

## 6. Exercício de Fixação Prática: Central de Monitoramento de Tráfego de Rede

Implemente um motor de auditoria de pacotes e acessos de rede utilizando a Streams API:

1. **Construa a Classe `RegistroAcessoRede`:**
   - Atributos privados: `idConexao` (`Long`), `ipOrigem` (`String`), `portaDestino` (`int`), `megabytesTransferidos` (`double`), `bloqueadoFirewall` (`boolean`).
   - Construtor parametrizado completo rejeitando endereços IP nulos via `IllegalArgumentException`.
   - Métodos acessores (*getters*) e método descritivo `toString()` formatando os megabytes com `%.2f`.

2. **Construa o Serviço `AuditoriaFirewallService`:**
   - **Método `List<RegistroAcessoRede> filtrarTrafegoSuspeito(List<RegistroAcessoRede> conexoes)`:**
     - Utiliza Stream para filtrar conexões que não foram bloqueadas pelo firewall, mas que transferiram mais de 500 MB em portas vulneráveis (portas menores que 1024).
     - Ordena o resultado do maior consumo de megabytes para o menor.
     - Retorna a lista final coletada com `.toList()`.
   - **Método `List<String> extrairIpsBloqueadosUnicos(List<RegistroAcessoRede> conexoes)`:**
     - Filtra apenas conexões onde `bloqueadoFirewall == true`.
     - Mapeia para a String do IP (`RegistroAcessoRede::getIpOrigem`).
     - Remove duplicatas usando `.distinct()`.
     - Ordena alfabeticamente com `.sorted()`.
     - Retorna a lista final.
   - **Método `boolean alertaAtaqueDDoS(List<RegistroAcessoRede> conexoes)`:**
     - Utiliza `.anyMatch()` para retornar verdadeiro se houver qualquer conexão individual que tenha transferido mais de 10.000 MB (10 GB).

3. **Construa a Classe Executável `MonitoramentoRedeApp`:**
   - Crie uma lista com cinco conexões contendo diferentes IPs, portas e volumes de tráfego.
   - Execute os três métodos do serviço e exiba os resultados no console com `.forEach(System.out::println)`.
   - Comprove que a lista original de conexões manteve todos os seus registros intactos e inalterados.
