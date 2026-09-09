# Explicação da Aula 15 — Iteração Segura e Ordenação de Coleções: O Padrão Iterator, Mecanismo Fail-Fast, Comparable e Comparator

| | |
|---|---|
| **Disciplina** | Programação Orientada a Objetos |
| **Curso** | Análise e Desenvolvimento de Sistemas (3º Período) |
| **Tema** | Iteração Segura e Ordenação de Coleções: O Padrão Iterator, Mecanismo Fail-Fast, Comparable e Comparator |
| **Carga Horária** | 100 minutos (2 horas-aula) |
| **Professor** | Alexandre Neves Louzada |
| **Plano de Aula** | `Aulas 3pob/Aula 15.md` |
| **Tutorial** | `aulas/TutorialAula15.md` |
| **Estudo de Caso** | `exemplos/aula-15/` |
| **Exercícios Resolvidos** | `solucoes/aula-15/` |

---

## 1. Objetivos de Aprendizagem

**Conceitual:** Compreender o padrão comportamental *Iterator* do GoF e sua implementação na JDK; entender a causa raiz da exceção `ConcurrentModificationException` e a mecânica do contador de modificações estruturais (`modCount`) que aciona o comportamento *Fail-Fast*; diferenciar a Ordem Natural de uma entidade (interface `java.lang.Comparable<T>`) de Ordenações Customizadas/Multicritério (interface `java.util.Comparator<T>`).

**Técnico:** Manipular ponteiros de iteração com os métodos `hasNext()`, `next()` e `remove()` do `Iterator<E>`; efetuar expurgos e remoções de elementos em coleções durante a travessia de forma segura; implementar o método `compareTo()` respeitando as propriedades matematicamente reflexivas, anti-simétricas e transitivas; criar comparadores independentes com a sintaxe clássica e métodos estáticos fluentes (`Comparator.comparing`).

**Arquitetural:** Projetar modelos de domínio onde a regra de comparação primária reflete a identidade natural do registro, enquanto relatórios e visões analíticas utilizam comparadores desacoplados e compostos.

**Prático:** Implementar o módulo de relatórios e auditoria de vendas de e-commerce (`CentralVendasApp`), aplicando expurgo seguro de registros zerados/inválidos via `Iterator.remove()` e ordenações dinâmicas por ID (ordem natural), valor total decrescente e nome do cliente.

---

## 2. Conteúdo Teórico Detalhado

### 2.1. Abertura: Navegação e Ordenação Estruturada de Coleções

Nas Aulas 12, 13 e 14 estudamos as três grandes famílias de coleções: Listas (`List`), Conjuntos (`Set`) e Mapas (`Map`). Na Aula 15, aprendemos a **percorrer** essas estruturas com segurança contra corrupção e a **ordená-las** sob múltiplos critérios de negócio.

Tabelas corporativas, dashboards e relatórios exigem ordenação dinamicamente configurável pelos usuários do sistema. Um relatório pode ser ordenado por data, por valor, por nome ou por qualquer outro campo. Compreender a diferença entre `Comparable` (ordem natural, imutável) e `Comparator` (ordens customizadas, flexíveis) é essencial para projetar soluções escaláveis.

### 2.2. O Padrão de Projeto Iterator

O Iterator é um padrão de projeto comportamental do GoF (Gang of Four) que permite percorrer todos os elementos de uma coleção sem expor sua representação interna. Independentemente de a coleção ser um `ArrayList`, `LinkedList` ou `HashSet`, o Iterator fornece uma API unificada para navegação.

**Contrato da Interface Iterator\<E\>:**

| Método | Descrição |
|---|---|
| `hasNext()` | Retorna `true` se houver mais elementos na travessia |
| `next()` | Avança o ponteiro e retorna o próximo elemento |
| `remove()` | Remove da coleção subjacente o último elemento retornado por `next()` |

**A Interface Iterable\<T\>:** Interface raiz que exige o método `iterator()`, permitindo o suporte nativo ao laço `for-each`. Qualquer classe que implementa `Iterable` pode ser percorrida com `for (T elemento : colecao) { ... }`.

Usando `Iterator`, o código cliente pode percorrer qualquer coleção sem se importar se ela é um `ArrayList`, `LinkedList` ou `HashSet`. Isso é um exemplo clássico de Programação Orientada a Objetos: programar para a interface, não para a implementação.

### 2.3. A Armadilha da Modificação Concorrente

Um dos erros mais comuns em programação Java é tentar modificar uma coleção diretamente durante uma iteração. Esse erro gera a temida `ConcurrentModificationException`.

**Antipadrão de Remoção (NÃO FAZER):**

```java
// FALHA GRAVE: Tentar modificar a lista dentro do for-each
for (Venda v : listaVendas) {
    if (v.getValorTotal() <= 0) {
        listaVendas.remove(v);  // DISPARA ConcurrentModificationException!
    }
}
```

**Por que isso falha?**

O laço `for-each` é implementado internamente pelo compilador como um Iterator. Quando você modifica a coleção diretamente (via `listaVendas.remove()`), o contador de modificações estruturais (`modCount`) da coleção incrementa. O Iterator, ao chamar `next()`, verifica se o `modCount` atual é igual ao `modCount` que ele capturou quando foi criado. Se forem diferentes, significa que a coleção foi modificada de forma não autorizada, e o Iterator lança `ConcurrentModificationException`.

**Comportamento Fail-Fast:** Esse mecanismo se chama *Fail-Fast*: a operação é interrompida imediatamente quando uma inconsistência é detectada, evitando corrupção silenciosa dos dados.

### 2.4. Iteração Segura com Iterator.remove()

A forma correta de remover elementos durante uma iteração é usando o próprio método `remove()` do Iterator:

```java
// REMOÇÃO SEGURA: Usando o Iterator.remove()
Iterator<Venda> iterador = listaVendas.iterator();
while (iterador.hasNext()) {
    Venda v = iterador.next();
    if (v.getValorTotal() <= 0) {
        iterador.remove();  // Remove da coleção subjacente de forma segura
    }
}
```

O `Iterator.remove()` remove o último elemento retornado por `next()` e atualiza o `modCount` de forma sincronizada, evitando a `ConcurrentModificationException`. Após a remoção, o Iterator continua funcionando normalmente.

### 2.5. Streams API como Alternativa Moderna

A partir do Java 8, a Streams API oferece uma forma declarativa e funcional de filtrar, transformar e processar coleções, eliminando a necessidade de iteradores manuais para a maioria dos cenários:

```java
// Filtragem, transformação e ordenação em uma única expressão
List<Venda> vendasValidas = listaVendas.stream()
    .filter(v -> v.getValorTotal() > 0)        // Filtrar apenas válidas
    .sorted(Comparator.comparing(Venda::getValorTotal).reversed())  // Ordenar decrescente
    .toList();  // Terminal: coleta em lista imutável
```

**Características das Streams:**

- **Não modifica a fonte original:** A operação cria uma nova estrutura de dados.
- **Operações intermediárias são lazy:** Só executam quando uma operação terminal é chamada.
- **Pipelines declarativos:** Facilitam leitura e manutenção do código.
- **Método `toList()`** (Java 16+): coleta em lista imutável.

### 2.6. Comparable\<T\>: A Ordem Natural

A interface `java.lang.Comparable<T>` define a **ordem natural** de uma entidade. Essa é a ordenação padrão, intrínseca ao objeto, que faz sentido sem nenhum critério externo.

**Método compareTo():**

```java
public int compareTo(T outro);
```

O método deve retornar:
- **Valor negativo** se `this` é menor que `outro`.
- **Zero** se `this` é igual a `outro`.
- **Valor positivo** se `this` é maior que `outro`.

**Propriedades Matemáticas Obrigatórias:**

1. **Reflexividade:** `x.compareTo(x) == 0` (todo objeto é igual a si mesmo).
2. **Anti-simetria:** Se `x.compareTo(y)` tem um certo sinal, então `y.compareTo(x)` tem o sinal oposto.
3. **Transitividade:** Se `x.compareTo(y) > 0` e `y.compareTo(z) > 0`, então `x.compareTo(z) > 0`.
4. **Consistência:** `x.compareTo(y)` sempre retorna o mesmo resultado (enquanto os objetos não mudarem).

**Exemplo prático no contexto do curso:**

```java
public class Tarefa implements Comparable<Tarefa> {
    @Override
    public int compareTo(Tarefa outra) {
        return this.titulo.compareToIgnoreCase(outra.titulo);
    }
}
```

**Onde é usada:** `Collections.sort(lista)`, `TreeSet`, `TreeMap`, entre outros.

### 2.7. Comparator\<T\>: Ordenações Customizadas

A interface `java.util.Comparator<T>` permite criar critérios de ordenação **externos, independentes e compostos**. Diferente do `Comparable`, que define uma única ordem natural, o `Comparator` pode ser criado em qualquer classe e combinar múltiplos critérios.

**Sintaxe Clássica:**

```java
Comparator<Produto> porNomeCrescente = new Comparator<Produto>() {
    @Override
    public int compare(Produto p1, Produto p2) {
        return p1.getNome().compareToIgnoreCase(p2.getNome());
    }
};
```

**Sintaxe Moderna com Lambda:**

```java
Comparator<Produto> porNomeCrescente =
    (p1, p2) -> p1.getNome().compareToIgnoreCase(p2.getNome());
```

**Métodos Estáticos Fluentes (Java 8+):**

```java
// Comparação por um único campo
Comparator<Produto> porNome =
    Comparator.comparing(Produto::getNome);

// Comparação decrescente
Comparator<Produto> porPrecoDecrescente =
    Comparator.comparing(Produto::getPreco).reversed();

// Comparação encadeada (multicritério)
Comparator<Produto> porCategoriaDepreco =
    Comparator.comparing(Produto::getCategoria)
              .thenComparing(Comparator.comparing(Produto::getPreco).reversed());
```

**Cadeia de Métodos Disponíveis:**

| Método | Descrição |
|---|---|
| `Comparator.comparing(fn)` | Cria comparador a partir de uma função de extração |
| `Comparator.reverseOrder()` | Inverte a ordenação natural |
| `Comparator.naturalOrder()` | Usa a ordenação natural do Comparable |
| `Comparator.nullsFirst(comp)` | Coloca nulos no início |
| `Comparator.nullsLast(comp)` | Coloca nulos no final |
| `comp.reversed()` | Inverte o comparador |
| `comp.thenComparing(comp2)` | Adiciona critério secundário |

### 2.8. Comparable vs. Comparator: Quando Usar Cada Um

| Critério | Comparable\<T\> | Comparator\<T\> |
|---|---|---|
| **Pacote** | `java.lang` | `java.util` |
| **Método** | `compareTo(T)` | `compare(T, T)` |
| **Definição** | Dentro da própria classe | Em qualquer classe/locus externo |
| **Critérios** | Um único (ordem natural) | Múltiplos (comparadores compostos) |
| **Acoplamento** | Acopla a entidade a um critério | Desacoplado da entidade |
| **Uso recomendado** | Quando a ordenação é intrínseca | Quando a ordenação depende de contexto |

**Regra prática:** Use `Comparable` para a identidade natural (ex.: ordenar por ID, por nome se for uma pessoa) e `Comparator` para ordenações de relatório (ex.: por preço, por data, por valor).

---

## 3. Estudo de Caso Aplicado

O estudo de caso desta aula encontra-se em `exemplos/aula-15/`, no pacote `br.edu.universidade.sistema.ecommerce`. O sistema demonstra uso de `Comparator` com `Stream` para auditoria e análise de pedidos de e-commerce.

### 3.1. Entidade de Domínio: PedidoCompra

O arquivo `PedidoCompra.java` (`exemplos/aula-15/src/br/edu/universidade/sistema/ecommerce/dominio/PedidoCompra.java`) define um pedido imutável:

```java
public class PedidoCompra {
    private final Long id;
    private final String cliente;
    private final String categoria;
    private final double valorTotal;
    private final boolean pago;

    public PedidoCompra(Long id, String cliente, String categoria,
                        double valorTotal, boolean pago) {
        this.id = id;
        this.cliente = cliente;
        this.categoria = categoria;
        this.valorTotal = valorTotal;
        this.pago = pago;
    }

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

    public Long getId() { return id; }
    public String getCliente() { return cliente; }
    public String getCategoria() { return categoria; }
    public double getValorTotal() { return valorTotal; }
    public boolean isPago() { return pago; }
}
```

Todos os campos são `final`, tornando a entidade imutável. O `equals` e `hashCode` são baseados no ID.

### 3.2. Serviço: AuditoriaPedidosService

O serviço (`exemplos/aula-15/src/br/edu/universidade/sistema/ecommerce/service/AuditoriaPedidosService.java`) demonstra pipelines completos da Streams API com `Comparator.comparing`:

```java
public class AuditoriaPedidosService {

    // Pipeline 1: Filtra pedidos pagos de uma categoria, ordenando por valor decrescente
    public List<PedidoCompra> obterPedidosPagosOrdenadosPorValor(
            List<PedidoCompra> pedidos, String categoria) {
        return pedidos.stream()
            .filter(PedidoCompra::isPago)
            .filter(p -> p.getCategoria().equalsIgnoreCase(categoria))
            .sorted(Comparator.comparing(PedidoCompra::getValorTotal).reversed())
            .toList();
    }

    // Pipeline 2: Lista de clientes VIP sem repetições (compras >= R$ 1.000)
    public List<String> obterNomesClientesVip(List<PedidoCompra> pedidos) {
        return pedidos.stream()
            .filter(p -> p.getValorTotal() >= 1000.0)
            .map(PedidoCompra::getCliente)
            .map(String::toUpperCase)
            .distinct()
            .sorted()
            .toList();
    }

    // Pipeline 3: Verificação de risco de fraude (pendentes > R$ 5.000)
    public boolean existeRiscoFraudePendente(List<PedidoCompra> pedidos) {
        return pedidos.stream()
            .filter(p -> !p.isPago())
            .anyMatch(p -> p.getValorTotal() > 5000.00);
    }

    // Pipeline 4: TOP 3 maiores pedidos
    public List<PedidoCompra> obterTop3MaioresPedidos(List<PedidoCompra> pedidos) {
        return pedidos.stream()
            .sorted(Comparator.comparing(PedidoCompra::getValorTotal).reversed())
            .limit(3)
            .toList();
    }
}
```

**Operações demonstradas:**

- **Filtros encadeados:** `filter()` aceita predicados e pode ser encadeado para refinar a seleção.
- **Comparator.comparing + reversed:** ordenação decrescente por valor.
- **Method references:** `PedidoCompra::isPago` é uma abreviação de `p -> p.isPago()`.
- **anyMatch:** operação terminal com curto-circuito que para ao encontrar o primeiro elemento.
- **limit(n):** corta o fluxo após os N primeiros elementos.

### 3.3. Aplicação Executável: StreamsApp

A classe principal demonstra a execução dos pipelines:

```java
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

        System.out.println("--- 1. Pedidos Pagos da Categoria ELETRONICOS ---");
        List<PedidoCompra> eletronicosPagos =
            service.obterPedidosPagosOrdenadosPorValor(basePedidos, "ELETRONICOS");
        eletronicosPagos.forEach(System.out::println);

        System.out.println("\n--- 2. Lista de Clientes VIP ---");
        List<String> clientesVip = service.obterNomesClientesVip(basePedidos);
        clientesVip.forEach(nome -> System.out.println("VIP: " + nome));

        System.out.println("\n--- 3. Auditoria de Segurança contra Fraude ---");
        boolean alertaFraude = service.existeRiscoFraudePendente(basePedidos);
        System.out.println("Alerta de fraude: " + alertaFraude);

        System.out.println("\n--- 4. TOP 3 Maiores Compras ---");
        List<PedidoCompra> top3 = service.obterTop3MaioresPedidos(basePedidos);
        top3.forEach(System.out::println);

        System.out.println("\n--- 5. Integridade da Fonte Original ---");
        System.out.printf("Total original: %d registros%n", basePedidos.size());
    }
}
```

O último trecho demonstra a **imutabilidade** da fonte original: após todos os pipelines de filtragem e ordenação, a lista `basePedidos` continua intacta com todos os 7 registros. Streams não modificam a fonte.

---

## 4. Exercícios Propostos e Solução

### Exercício: Auditoria de Firewall e Monitoramento de Rede

**Enunciado:** Implemente um sistema de auditoria de firewall que filtre conexões de rede suspeitas, extraia IPs bloqueados únicos e detecte possíveis ataques DDoS.

**Pacote da Solução:** `br.edu.universidade.sistema.rede` em `solucoes/aula-15/`.

**Entidade RegistroAcessoRede:**

```java
public class RegistroAcessoRede {
    private Long idConexao;
    private String ipOrigem;
    private int portaDestino;
    private double megabytesTransferidos;
    private boolean bloqueadoFirewall;

    public RegistroAcessoRede(Long idConexao, String ipOrigem,
                               int portaDestino, double megabytesTransferidos,
                               boolean bloqueadoFirewall) {
        if (idConexao == null) {
            throw new IllegalArgumentException("ID da conexão não pode ser nulo.");
        }
        if (ipOrigem == null) {
            throw new IllegalArgumentException("IP de origem não pode ser nulo.");
        }
        this.idConexao = idConexao;
        this.ipOrigem = ipOrigem;
        this.portaDestino = portaDestino;
        this.megabytesTransferidos = megabytesTransferidos;
        this.bloqueadoFirewall = bloqueadoFirewall;
    }

    public Long getIdConexao() { return idConexao; }
    public String getIpOrigem() { return ipOrigem; }
    public int getPortaDestino() { return portaDestino; }
    public double getMegabytesTransferidos() { return megabytesTransferidos; }
    public boolean isBloqueadoFirewall() { return bloqueadoFirewall; }
}
```

**Serviço AuditoriaFirewallService:**

```java
public class AuditoriaFirewallService {

    public List<RegistroAcessoRede> filtrarTrafegoSuspeito(
            List<RegistroAcessoRede> conexoes) {
        return conexoes.stream()
            .filter(c -> !c.isBloqueadoFirewall())
            .filter(c -> c.getMegabytesTransferidos() > 500.0)
            .filter(c -> c.getPortaDestino() < 1024)
            .sorted((a, b) -> Double.compare(
                b.getMegabytesTransferidos(), a.getMegabytesTransferidos()))
            .toList();
    }

    public List<String> extrairIpsBloqueadosUnicos(
            List<RegistroAcessoRede> conexoes) {
        return conexoes.stream()
            .filter(c -> c.isBloqueadoFirewall())
            .map(RegistroAcessoRede::getIpOrigem)
            .distinct()
            .sorted()
            .toList();
    }

    public boolean alertaAtaqueDDoS(List<RegistroAcessoRede> conexoes) {
        return conexoes.stream()
            .anyMatch(c -> c.getMegabytesTransferidos() > 10000.0);
    }
}
```

**Aplicação MonitoramentoRedeApp:**

```java
public class MonitoramentoRedeApp {
    public static void main(String[] args) {
        AuditoriaFirewallService service = new AuditoriaFirewallService();

        List<RegistroAcessoRede> conexoes = List.of(
            new RegistroAcessoRede(1L, "192.168.0.10", 22, 12000.00, false),
            new RegistroAcessoRede(2L, "192.168.0.20", 443, 80.00, true),
            new RegistroAcessoRede(3L, "172.16.5.11", 445, 980.00, false),
            new RegistroAcessoRede(4L, "10.0.0.33", 1433, 700.00, true),
            new RegistroAcessoRede(5L, "172.16.9.77", 8080, 40.00, true)
        );

        System.out.println("--- Tráfego Suspeito ---");
        List<RegistroAcessoRede> suspeitos =
            service.filtrarTrafegoSuspeito(conexoes);
        suspeitos.forEach(System.out::println);

        System.out.println("\n--- IPs Bloqueados Únicos ---");
        List<String> ipsBloqueados =
            service.extrairIpsBloqueadosUnicos(conexoes);
        ipsBloqueados.forEach(System.out::println);

        System.out.println("\n--- Alerta DDoS ---");
        boolean ddos = service.alertaAtaqueDDoS(conexoes);
        System.out.println("Alerta DDoS: " + ddos);

        System.out.println("\n--- Integridade da Lista Original ---");
        System.out.printf("Registros preservados: %d%n", conexoes.size());
    }
}
```

**Observações sobre a solução:**

- O filtro de tráfego suspeito combina três critérios: não bloqueado, mais de 500 MB e porta abaixo de 1024 (portas privilegiadas do sistema operacional).
- A detecção de DDoS é feita por `anyMatch`, que para imediatamente ao encontrar a primeira conexão com mais de 10 GB.
- A lista original é imutável (`List.of`) e preserva todos os registros após as operações de filtragem.
- A ordenação do tráfego suspeito usa comparator explícito `(a, b) -> Double.compare(b.getMegabytesTransferidos(), a.getMegabytesTransferidos())` para ordem decrescente.

---

## 5. Perguntas de Revisão

1. Qual é a diferença entre `Comparable` e `Comparator`? Quando usar cada um?

2. O que é a `ConcurrentModificationException` e por que ela ocorre?

3. Explique o mecanismo de `modCount` e como ele previne corrupção silenciosa de dados.

4. Qual é a forma segura de remover elementos de uma lista durante uma iteração manual?

5. Por que o método `compareTo()` deve ser reflexivo, anti-simétrico e transitivo?

6. Crie um `Comparator` que ordene por dois campos: nome (crescente) e salário (decrescente) usando `Comparator.comparing`.

7. Qual é a diferença entre uma Stream e uma Collection?

8. Explique o que é uma operação terminal e uma operação intermediária no contexto de Streams.

---

## 6. Resumo / Pontos-Chave

- **Padrão Iterator:** percorre coleções sem expor a representação interna. Interface `Iterator<E>` com `hasNext()`, `next()` e `remove()`.
- **ConcurrentModificationException:** falha causada por modificação estrutural de uma coleção durante uma iteração não autorizada.
- **ModCount:** contador interno que controla a integridade da iteração. Fail-Fast: erro imediato ao detectar inconsistência.
- **Remoção segura:** usar `Iterator.remove()` durante a travessia manual, ou `removeIf()` / Streams para operações declarativas.
- **Comparable\<T\>:** define ordem natural (intrínseca). Método `compareTo()` deve ser reflexivo, anti-simétrico e transitivo.
- **Comparator\<T\>:** define ordenações externas, flexíveis e compostas. Métodos estáticos `Comparator.comparing()`, `thenComparing()`, `reversed()`.
- **Streams API:** forma declarativa e funcional de processar coleções. Fonte imutável, operações intermediárias lazy, operações terminais.
- **Imutabilidade:** Streams não modificam a fonte original. Use `toList()` para coletar em lista imutável (Java 16+).
- **anyMatch / allMatch / noneMatch:** operações terminais de curto-circuito que retornam `boolean`.
- **Comparable vs. Comparator:** Comparable para identidade natural; Comparator para critérios de relatório e ordenações contextualizadas.
