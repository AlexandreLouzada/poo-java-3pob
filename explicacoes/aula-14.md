# Explicação da Aula 14 — Mapeamentos Chave-Valor: A Interface Map, HashMap, Hashtable e Estratégias de Iteração

| | |
|---|---|
| **Disciplina** | Programação Orientada a Objetos |
| **Curso** | Análise e Desenvolvimento de Sistemas (3º Período) |
| **Tema** | Mapeamentos Chave-Valor: A Interface Map, HashMap, Hashtable e Estratégias de Iteração |
| **Carga Horária** | 100 minutos (2 horas-aula) |
| **Professor** | Alexandre Neves Louzada |
| **Plano de Aula** | `Aulas 3pob/Aula 14.md` |
| **Tutorial** | `aulas/TutorialAula14.md` |
| **Estudo de Caso** | `exemplos/aula-14/` |
| **Exercícios Resolvidos** | `solucoes/aula-14/` |

---

## 1. Objetivos de Aprendizagem

**Conceitual:** Compreender o paradigma de armazenamento associativo Chave -> Valor (K -> V); entender por que a interface `java.util.Map<K, V>` não estende `Collection<T>`; analisar o comportamento de unicidade de chaves vs. duplicidade de valores; entender o mecanismo interno do `HashMap` (baldes, fator de carga, rehashing e conversão automática para Árvores Rubro-Negras quando ultrapassado o limiar de 8 elementos por balde); compreender a obsolescência da classe sincronizada `Hashtable`.

**Técnico:** Dominar os métodos essenciais de manipulação de dicionários (`put`, `get`, `getOrDefault`, `putIfAbsent`, `computeIfAbsent`, `merge`, `containsKey`, `containsValue`, `remove`); manipular com alta performance as três projeções/vistas de coleção do mapa (`keySet()`, `values()` e `entrySet()`).

**Arquitetural:** Projetar estruturas de dados em memória para buscas diretas por chave de negócio em tempo constante O(1); evitar o antipadrão de iteração por `keySet()` acompanhado de `get(k)` no corpo do laço, priorizando o uso de `Map.Entry<K, V>`.

**Prático:** Implementar um módulo de catálogo e controle de estoque de produtos (`CatalogoEstoque`), mapeando entidades por SKU em `HashMap`, gerenciando saldos acumulados e emitindo relatórios de valorização total via `entrySet()`.

---

## 2. Conteúdo Teórico Detalhado

### 2.1. Abertura: Dicionários em Memória e Mapeamentos Associativos

Nas aulas 12 e 13, estudamos listas (`List`) e conjuntos (`Set`). Na Aula 14, aprendemos a associar um identificador exclusivo (chave) a um objeto complexo (valor). O paradigma de mapeamento associativo (Chave -> Valor) é a base de índices de bancos de dados, caches de aplicações, configurações de sistemas e muitas outras estruturas fundamentais da engenharia de software.

A interface `java.util.Map<K, V>` não estende `Collection<T>` porque coleções operam sobre elementos individuais (E), enquanto mapas operam sobre pares associados de entidades (K -> V). Essa diferença conceitual é fundamental para entender por que `Map` possui uma API própria e métodos de iteração diferentes.

### 2.2. O Contrato da Interface java.util.Map\<K, V\>

A interface `Map<K, V>` define um modelo mental de chave-valor com as seguintes regras:

- **Chaves Únicas (K):** Cada chave pode estar mapeada para no máximo um valor. Inserir uma chave existente **sobrescreve** o valor anterior.
- **Valores Duplicados (V):** Múltiplas chaves diferentes podem apontar para valores idênticos.

**Operações Essenciais:**

| Método | Descrição |
|---|---|
| `put(K key, V value)` | Insere ou atualiza a associação |
| `get(Object key)` | Retorna o valor associado ou `null` se não existir |
| `getOrDefault(Object key, V defaultValue)` | Retorna o valor ou um padrão seguro |
| `putIfAbsent(K key, V value)` | Insere apenas se a chave não existir |
| `computeIfAbsent(K key, Function<K,V> f)` | Cria e insere o valor se a chave não existir |
| `merge(K key, V value, BiFunction<V,V,V> f)` | Combina valores existentes com novos |
| `containsKey(Object key)` | Verifica se a chave existe |
| `containsValue(Object value)` | Verifica se o valor existe |
| `remove(Object key)` | Remove a associação pela chave |
| `size()` | Retorna o número de entradas |
| `isEmpty()` | Verifica se o mapa está vazio |

**Alerta Crítico sobre Chaves:** Chaves de mapas **devem ser imutáveis** (ex.: `String`, `Integer`, `UUID` ou records). Alterar o estado interno de um objeto usado como chave após sua inserção no mapa corrompe o cálculo do hash e torna o valor inalcançável.

### 2.3. Arquitetura Interna do HashMap\<K, V\>

O `HashMap` é a implementação mais utilizada da interface `Map`. Sua arquitetura interna é sofisticada e eficiente:

**Array de Baldes (Buckets):** Tabela interna armazena nós da interface `Map.Entry<K, V>`.

**Cálculo de Posição:**

```
Índice = hashCode(key) % Tamanho_do_Array
```

**Tratamento de Colisões no Java Moderno (Java 8+):**

Inicialmente, colisões no mesmo balde formam uma Lista Encadeada. Quando um balde ultrapassa o limiar de **8 elementos**, a lista encadeada é convertida automaticamente em uma **Árvore Rubro-Negra**, reduzindo a busca no balde de O(n) para O(log n).

**Treeificação:** Esse mecanismo é uma das genialidades da engenharia da JDK. A conversão para árvore previne ataques do tipo *HashDoS* (onde dados maliciosos provocam colisões deliberadas para derrubar o servidor por degradação algorítmica).

**Fator de Carga (Load Factor 0.75):** Ocorre *rehashing* (dobro do tamanho da tabela) quando 75% da capacidade é atingida. Isso garante que os baldes não fiquem muito cheios, mantendo a performance O(1) amortizada.

### 2.4. HashMap vs. Hashtable: Análise Comparativa

A `Hashtable` é uma classe legada do Java 1.0 que foi substituída pelo `HashMap` a partir do Java 1.2. A tabela a seguir apresenta as diferenças:

| Característica | HashMap\<K, V\> | Hashtable\<K, V\> |
|---|---|---|
| **Origem na Linguagem** | Java 1.2 (Moderno - Collections) | Java 1.0 (Legado / Obsoleto) |
| **Sincronização de Threads** | Não Sincronizado (Alta Performance) | Sincronizado (`synchronized` / Lento) |
| **Permite Chave `null`** | **Sim** (exatamente 1 chave nula) | **Não** (dispara `NullPointerException`) |
| **Permite Valor `null`** | **Sim** (múltiplos) | **Não** (dispara `NullPointerException`) |
| **Substituto Moderno Multithread** | N/A | `java.util.concurrent.ConcurrentHashMap` |

**Por que Hashtable é obsoleta?** A sincronização global de todos os seus métodos gera contenção severa de CPU em ambientes concorrentes. Em cenários multithread modernos, usa-se `ConcurrentHashMap`, que implementa sincronização em nível de Segmento (granularidade por região da tabela), permitindo acesso concorrente de alta performance.

### 2.5. As Três Vistas de Coleção de um Map

Um mapa não implementa `Iterable` diretamente (porque não é uma coleção de elementos individuais), mas oferece três projeções que permitem iteração:

1. **`map.keySet()`** -- Retorna um `Set<K>` com todas as chaves únicas. Não contém valores.
2. **`map.values()`** -- Retorna uma `Collection<V>` com todos os valores (com duplicatas possíveis).
3. **`map.entrySet()`** -- Retorna um `Set<Map.Entry<K, V>>` com os pares completos de chave-valor.

**Observação Crítica:** As visões retornadas são **ligadas diretamente** ao mapa original. Remover um elemento de `map.keySet()` remove automaticamente a entrada do mapa subjacente!

### 2.6. Padrões de Iteração e Performance

A forma como iteramos sobre um mapa afeta significativamente a performance. O ponto crítico é evitar operações redundantes de busca.

**Antipadrão de Performance (NÃO FAZER):**

```java
// LENTO: Faz duas buscas de hash por elemento (keySet + get)
for (String chave : mapa.keySet()) {
    Produto p = mapa.get(chave);  // Busca desnecessária repetida n vezes
}
```

Nesse caso, para cada chave, o código faz uma busca adicional com `get(chave)`, resultando em **duas operações de hash** por elemento.

**Abordagem Correta com entrySet():**

```java
// RÁPIDO: Acesso direto ao par chave-valor com apenas 1 busca de hash
for (Map.Entry<String, Produto> entrada : mapa.entrySet()) {
    String chave = entrada.getKey();
    Produto p = entrada.getValue();  // Acesso direto, sem nova busca
}
```

O uso de `entrySet()` é sempre recomendado quando se precisa de tanto a chave quanto o valor durante a iteração.

**Abordagem com forEach e Lambda (Java 8+):**

```java
mapa.forEach((chave, valor) -> {
    System.out.println(chave + " -> " + valor);
});
```

A forma mais limpa e expressiva de iterar sobre mapas, dispensando a necessidade de verificar se é `keySet` ou `entrySet`.

### 2.7. Métodos Avançados do Map

**computeIfAbsent:** Cria um valor se a chave não existe, de forma atomicamente segura:

```java
Map<String, List<Produto>> porCategoria = new HashMap<>();
porCategoria
    .computeIfAbsent("PERIFERICOS", k -> new ArrayList<>())
    .add(new Produto("SKU-101", "Teclado", 250.0));
```

**merge:** Combina um novo valor com o existente:

```java
Map<String, Integer> contagem = new HashMap<>();
contagem.merge("erro", 1, Integer::sum);  // Se não existe: 1; Se existe: soma 1
```

**putIfAbsent:** Insere apenas se a chave não estiver mapeada:

```java
mapa.putIfAbsent("chave", valor);
// Equivalente a: if (!mapa.containsKey("chave")) mapa.put("chave", valor);
```

---

## 3. Estudo de Caso Aplicado

O estudo de caso desta aula encontra-se em `exemplos/aula-14/`, no pacote `br.edu.universidade.sistema.rh`. O sistema demonstra uso de `Map` com `HashMap` para gerenciamento de colaboradores e operações funcionais com `Predicate`, `Function` e `Consumer`.

### 3.1. Entidade de Domínio: Colaborador

O arquivo `Colaborador.java` (`exemplos/aula-14/src/br/edu/universidade/sistema/rh/dominio/Colaborador.java`) define a entidade com campos imutáveis e mutáveis:

```java
public class Colaborador {
    private final Long id;
    private final String nome;
    private final String departamento;
    private double salarioBase;
    private final int tempoServicoAnos;

    public Colaborador(Long id, String nome, String departamento,
                       double salarioBase, int tempoServicoAnos) {
        this.id = id;
        this.nome = nome;
        this.departamento = departamento;
        this.salarioBase = salarioBase;
        this.tempoServicoAnos = tempoServicoAnos;
    }

    public void aplicarAumento(double valorAdicional) {
        if (valorAdicional > 0) {
            this.salarioBase += valorAdicional;
        }
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getDepartamento() { return departamento; }
    public double getSalarioBase() { return salarioBase; }
    public int getTempoServicoAnos() { return tempoServicoAnos; }
}
```

### 3.2. Serviço: AuditoriaRhService

O serviço (`exemplos/aula-14/src/br/edu/universidade/sistema/rh/service/AuditoriaRhService.java`) implementa métodos de alta ordem que recebem comportamentos funcionais:

```java
public class AuditoriaRhService {

    // Método de Alta Ordem: Recebe um Predicate para filtrar
    public List<Colaborador> filtrar(List<Colaborador> equipe,
                                      Predicate<Colaborador> criterio) {
        List<Colaborador> resultado = new ArrayList<>();
        for (Colaborador c : equipe) {
            if (criterio.test(c)) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    // Método de Transformação: Projeta colaboradores em qualquer outro tipo
    public <R> List<R> mapear(List<Colaborador> equipe,
                               Function<Colaborador, R> transformador) {
        List<R> resultado = new ArrayList<>();
        for (Colaborador c : equipe) {
            resultado.add(transformador.apply(c));
        }
        return resultado;
    }

    // Método de Ação: Dispara um Consumer em cada registro
    public void executarAcao(List<Colaborador> equipe,
                              Consumer<Colaborador> acao) {
        for (Colaborador c : equipe) {
            acao.accept(c);
        }
    }
}
```

**Interfaces Funcionais utilizadas:**

- **`Predicate<T>`**: recebe um `T` e retorna `boolean`. Usado para filtragem.
- **`Function<T, R>`**: recebe um `T` e retorna um `R`. Usado para transformação/projeção.
- **`Consumer<T>`**: recebe um `T` e retorna `void`. Usado para ações com efeito colateral.

### 3.3. Aplicação Executável: RhFuncionalApp

A classe principal demonstra composição funcional e uso de lambdas:

```java
public class RhFuncionalApp {
    public static void main(String[] args) {
        List<Colaborador> equipe = List.of(
            new Colaborador(101L, "Mariana Silva", "TI", 8500.00, 6),
            new Colaborador(102L, "Lucas Mendes", "TI", 4200.00, 2),
            new Colaborador(103L, "Carlos Prado", "FINANCEIRO", 6100.00, 8),
            new Colaborador(104L, "Beatriz Souza", "RH", 3800.00, 1),
            new Colaborador(105L, "Renata Lima", "FINANCEIRO", 9200.00, 10)
        );

        AuditoriaRhService service = new AuditoriaRhService();

        // Filtragem com Predicate Simples e Encadeado
        Predicate<Colaborador> ehDeTi = c ->
            c.getDepartamento().equalsIgnoreCase("TI");
        Predicate<Colaborador> ehSenior = c ->
            c.getTempoServicoAnos() >= 5;

        // Composição booleana funcional usando o método default 'and()'
        List<Colaborador> tiSeniors = service.filtrar(equipe, ehDeTi.and(ehSenior));
        tiSeniors.forEach(System.out::println);

        // Transformação e Projeção com Function
        Function<Colaborador, String> crachaFormatter = c ->
            String.format("CRACHA: %s (%s)",
                c.getNome().toUpperCase(), c.getDepartamento());

        List<String> crachas = service.mapear(equipe, crachaFormatter);
        crachas.forEach(System.out::println);

        // Aplicação de Efeito Colateral com Consumer
        Predicate<Colaborador> aptosBonus = c ->
            c.getTempoServicoAnos() >= 7;
        Consumer<Colaborador> concederBonus = c -> {
            c.aplicarAumento(500.00);
            System.out.printf("Bônus para %s! Novo Salário: R$ %.2f%n",
                c.getNome(), c.getSalarioBase());
        };

        List<Colaborador> veteranos = service.filtrar(equipe, aptosBonus);
        service.executarAcao(veteranos, concederBonus);
    }
}
```

**Demonstração de conceitos:**

- **Composição de Predicados:** `ehDeTi.and(ehSenior)` combina dois critérios de filtragem. Também há os métodos `or()` e `negate()`.
- **Transformação com Function:** O método `mapear` transforma uma lista de `Colaborador` em uma lista de `String`, demonstrando a versatilidade dos generics.
- **Efeito Colateral com Consumer:** O `concederBonus` modifica o estado do objeto (aplica aumento) e imprime na tela, retornando `void`.
- **List.of()** cria uma lista imutável de colaboradores, útil para demonstrações.

---

## 4. Exercícios Propostos e Solução

### Exercício: Motor de Regras Financeiras

**Enunciado:** Implemente um motor de regras financeiras que audite transações com chaves Pix e aplique tarifações dinamicamente, utilizando interfaces funcionais.

**Pacote da Solução:** `br.edu.universidade.sistema.tarifacao` em `solucoes/aula-14/`.

**Entidade TransacaoFinanceira:**

```java
public class TransacaoFinanceira {
    private Long id;
    private String chavePix;
    private double valor;
    private String tipoOperacao;

    public TransacaoFinanceira(Long id, String chavePix,
                               double valor, String tipoOperacao) {
        if (id == null) {
            throw new IllegalArgumentException("ID não pode ser nulo.");
        }
        if (chavePix == null || chavePix.trim().isEmpty()) {
            throw new IllegalArgumentException("Chave Pix não pode ser vazia.");
        }
        if (valor <= 0.0) {
            throw new IllegalArgumentException("Valor deve ser > 0: " + valor);
        }
        if (tipoOperacao == null || tipoOperacao.trim().isEmpty()) {
            throw new IllegalArgumentException("Tipo de operação não pode ser vazio.");
        }
        this.id = id;
        this.chavePix = chavePix;
        this.valor = valor;
        this.tipoOperacao = tipoOperacao;
    }

    public void abaterTaxa(double taxa) {
        if (taxa > 0.0 && taxa <= this.valor) {
            this.valor -= taxa;
        }
    }

    public Long getId() { return id; }
    public String getChavePix() { return chavePix; }
    public double getValor() { return valor; }
    public String getTipoOperacao() { return tipoOperacao; }
}
```

**Serviço MotorRegrasFinanceirasService:**

```java
public class MotorRegrasFinanceirasService {

    public List<TransacaoFinanceira> auditarTransacoes(
            List<TransacaoFinanceira> lista,
            Predicate<TransacaoFinanceira> regraAuditoria) {
        List<TransacaoFinanceira> aprovadas = new ArrayList<>();
        for (TransacaoFinanceira t : lista) {
            if (regraAuditoria.test(t)) {
                aprovadas.add(t);
            }
        }
        return aprovadas;
    }

    public void aplicarTarifacao(
            List<TransacaoFinanceira> lista,
            Function<TransacaoFinanceira, Double> calculadorTarifa,
            Consumer<TransacaoFinanceira> auditoriaFinal) {
        for (TransacaoFinanceira t : lista) {
            double taxa = calculadorTarifa.apply(t);
            t.abaterTaxa(taxa);
            auditoriaFinal.accept(t);
        }
    }
}
```

**Aplicação MotorRegrasApp:**

```java
public class MotorRegrasApp {
    public static void main(String[] args) {
        MotorRegrasFinanceirasService service =
            new MotorRegrasFinanceirasService();

        List<TransacaoFinanceira> transacoes = List.of(
            new TransacaoFinanceira(1L, "pix-alfa", 700.00, "CREDITO"),
            new TransacaoFinanceira(2L, "pix-beta", 120.00, "DEBITO"),
            new TransacaoFinanceira(3L, "pix-gama", 30.00, "DEBITO"),
            new TransacaoFinanceira(4L, "pix-delta", 250.00, "BOLETO")
        );

        // Predicate: regra de auditoria (valor > 50 e não é BOLETO)
        Predicate<TransacaoFinanceira> regraAuditoria = t ->
            t.getValor() > 50.00
            && !t.getTipoOperacao().equals("BOLETO");

        // Function: calcula tarifa conforme tipo de operação
        Function<TransacaoFinanceira, Double> calculadorTarifa = t -> {
            if (t.getTipoOperacao().equals("CREDITO")) {
                return t.getValor() * 0.02;
            }
            return 1.00;
        };

        // Consumer: auditoria final (impressão do resultado)
        Consumer<TransacaoFinanceira> auditoriaFinal = t ->
            System.out.printf("[TARIFADA] TX #%d - Valor Final: R$ %.2f%n",
                t.getId(), t.getValor());

        // Pipeline: Filtrar -> Tarifar -> Auditar
        List<TransacaoFinanceira> aprovadas =
            service.auditarTransacoes(transacoes, regraAuditoria);
        aprovadas.forEach(System.out::println);

        service.aplicarTarifacao(aprovadas, calculadorTarifa, auditoriaFinal);

        aprovadas.forEach(System.out::println);
    }
}
```

**Observações sobre a solução:**

- A regra de auditoria é representada por um `Predicate` que filtra transações com valor superior a R$ 50,00 e que não sejam do tipo BOLETO.
- A função de tarifação retorna 2% para operações de CREDITO e R$ 1,00 fixo para DEBITO.
- O `Consumer` é responsável pela auditoria final, imprimindo o resultado sem retornar valor.
- O serviço é completamente genérico: a regra, a função e a ação são injetadas como comportamento, permitindo reutilização total.

---

## 5. Perguntas de Revisão

1. Por que a interface `Map<K, V>` não estende `Collection<T>`?

2. O que acontece quando você insere uma chave existente em um `HashMap` com `put`?

3. Qual é a diferença entre `getOrDefault` e `get` no que diz respeito a chaves inexistentes?

4. Explique o que é treeificação no `HashMap` e qual é o limiar de elementos por balde que a aciona.

5. Por que é considerado antipadrão iterar por `keySet()` e chamar `get(k)` dentro do laço?

6. Qual é a diferença entre `computeIfAbsent` e `putIfAbsent`?

7. Por que a `Hashtable` é considerada obsoleta e qual é o seu substituto moderno?

8. Como funciona o `merge` em um `Map` e por que ele é útil para contagem de frequência?

---

## 6. Resumo / Pontos-Chave

- **Map\<K, V\>:** estrutura de dados associativa que mapeia chaves únicas a valores. Não estende `Collection` porque manipula pares (K, V), não elementos individuais.
- **Chaves devem ser imutáveis:** String, Integer, UUID, records. Alterar uma chave corrompe o hash e torna o valor inalcançável.
- **HashMap:** implementação moderna e eficiente, com O(1) amortizado. Chaves null permitidas. Treeificação em 8+ elementos por balde.
- **Hashtable:** classe legada, sincronizada e obsoleta. Substituída por `ConcurrentHashMap` em cenários multithread.
- **Três vistas do Map:** `keySet()`, `values()`, `entrySet()`. Todas são visões ligadas ao mapa original.
- **Iteração eficiente:** sempre usar `entrySet()` quando se precisa de chave e valor, ou `forEach()`. Evitar `keySet()` + `get()`.
- **Métodos avançados:** `computeIfAbsent` (cria valor se chave não existe), `merge` (combina valores), `putIfAbsent` (insere se chave não existe).
- **Interfaces funcionais:** `Predicate` (filtragem), `Function` (transformação), `Consumer` (ação). Combinadas com lambdas, permitem construção de pipelines expressivos.
