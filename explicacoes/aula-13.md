# Explicação da Aula 13 — Conjuntos e Unicidade: A Interface Set, HashSet, TreeSet e o Contrato equals/hashCode

| | |
|---|---|
| **Disciplina** | Programação Orientada a Objetos |
| **Curso** | Análise e Desenvolvimento de Sistemas (3o Período) |
| **Tema** | Conjuntos e Unicidade: A Interface Set, HashSet, TreeSet e o Contrato equals/hashCode |
| **Carga Horária** | 100 minutos (2 horas-aula) |
| **Professor** | Alexandre Neves Louzada |
| **Plano de Aula** | `Aulas 3pob/Aula 13.md` |
| **Tutorial** | `aulas/TutorialAula13.md` |
| **Estudo de Caso** | `exemplos/aula-13/` |
| **Exercícios Resolvidos** | `solucoes/aula-13/` |

---

## 1. Objetivos de Aprendizagem

**Conceitual:** Compreender a abstração de conjuntos no Java Collections Framework; entender o contrato de unicidade da interface `java.util.Set<E>` (ausência de duplicatas e inexistência de acesso por índice posicional); dominar a relação matemática indissociável entre os métodos `equals()` e `hashCode()` da classe `java.lang.Object`.

**Técnico:** Diferenciar a arquitetura interna do `HashSet` (tabelas de espalhamento com complexidade amortizada O(1)) da estrutura do `TreeSet` (Árvores Rubro-Negras auto-balanceadas com ordem natural/customizada O(log n)); implementar sobrescritas rigorosas de `equals()` e `hashCode()` utilizando a classe utilitária `java.util.Objects`.

**Arquitetural:** Prevenir falhas críticas de integridade de dados e vazamentos silenciosos em coleções de memória causados pela violação do contrato do hash; selecionar a implementação de `Set` adequada para requisitos de alta performance vs. ordenação dinâmica.

**Prático:** Implementar um sistema corporativo de credenciamento e controle de presença em eventos (`ControleCredenciamento`), utilizando `HashSet` para garantia de unicidade por CPF em tempo constante e `TreeSet` para geração da lista de chamada em ordem alfabética.

---

## 2. Conteúdo Teórico Detalhado

### 2.1. Abertura: O Problema da Duplicação de Dados em Memória

Nas aulas anteriores (Aula 11 e 12), estudamos o Collections Framework, Generics e a interface `List`. Agora, entramos no estudo específico da família de estruturas que garantem **unicidade** dos dados. Em bancos de dados relacionais, usamos restrições `UNIQUE` e `PRIMARY KEY`. Em memória, o contrato equivalente é representado pela interface `Set`.

O problema concreto que a interface `Set` resolve: o que acontece se tentarmos cadastrar o mesmo CPF duas vezes em um sistema de eventos? Como a JVM decide se dois objetos são "iguais"? A resposta envolve o contrato matemático entre `equals()` e `hashCode()`.

### 2.2. O Contrato da Interface java.util.Set\<E\>

A interface `Set<E>` é uma coleção que **não permite elementos duplicados**. Suas características fundamentais são:

- **Sem Elementos Duplicados:** Rejeita inserções de itens considerados equivalentes pelo critério de igualdade (`equals()`).
- **Sem Acesso Posicional por Índice:** Métodos como `get(int index)` **não existem** no `Set`. Não é possível acessar um elemento pela sua posição.
- **Validação por Retorno Booleano:** O método `add(E e)` retorna `true` se o elemento foi inserido com sucesso e `false` se já existia no conjunto.

**Operações de Teoria dos Conjuntos:**

| Operação Java | Notação Matemática | Descrição |
|---|---|---|
| `setA.addAll(setB)` | A U B | União: combina todos os elementos de ambos os conjuntos |
| `setA.retainAll(setB)` | A inter B | Interseção: mantém apenas elementos presentes em ambos |
| `setA.removeAll(setB)` | A - B | Diferença: mantém apenas elementos exclusivos de A |

**Elegância do Retorno Booleano:** Em vez de fazer uma verificação `if (!conjunto.contains(item))` antes de inserir (que custa duas operações), basta fazer `if (conjunto.add(item))` para tentar inserir e saber imediatamente se o elemento era novo.

### 2.3. Arquitetura Interna do HashSet\<E\>

O `HashSet` é a implementação mais rápida da interface `Set`. Funciona da seguinte forma:

**Mecanismo:** Internamente, envolve um `HashMap` onde os elementos do conjunto atuam como chaves (com um valor dummy associado).

**Tabela de Espalhamento (Hash Table):**

O `HashSet` mantém um array de baldes (*buckets*). A posição de cada elemento é calculada por:

```
Índice = hashCode(e) % Tamanho_da_Tabela
```

**Funcionamento em Duas Etapas:**

1. Calcula o `hashCode()` para ir direto ao balde correspondente (operação O(1)).
2. Se o balde tiver mais de um item (colisão), executa o `equals()` apenas nos itens daquele balde.

**Ordem dos Elementos:** Totalmente indeterminada e imprevisível. A ordem de iteração do `HashSet` **não reflete** a ordem de inserção. Se você precisar manter a ordem de inserção, use `LinkedHashSet`.

**Complexidade Assintótica:** Inserção (`add`), remoção (`remove`) e consulta (`contains`) em **O(1) amortizado**.

### 2.4. O Contrato Obrigatório: equals() e hashCode()

O contrato entre `equals()` e `hashCode()` é a regra mais importante que qualquer programador Java precisa dominar ao usar coleções. A Especificação da JDK estabelece:

**A Regra de Ouro:**

```
Se a.equals(b) == true  ==>  a.hashCode() == b.hashCode()
```

Isso é OBRIGATÓRIO. Se dois objetos são considerados iguais pelo `equals`, eles **devem** ter o mesmo `hashCode`.

**A Recíproca NÃO é Verdadeira:**

```
Se a.hashCode() == b.hashCode()  ==>  Pode ser uma colisão!
```

Dois objetos podem ter o mesmo `hashCode` sem serem iguais. Isso é chamado de **colisão** e é tratado internamente pelo `HashSet` através do `equals`.

**O que acontece se o contrato for quebrado?**

Se uma classe sobrescreve `equals()` mas não sobrescreve `hashCode()`, a JVM usa o `hashCode` padrão da classe `Object` (baseado no endereço de memória). Resultado:

- Objetos "iguais" (segundo `equals`) geram hashes diferentes.
- São enviados para baldes diferentes na Tabela Hash.
- O `HashSet` aceita duplicatas silenciosamente.
- O método `contains()` falha em encontrar elementos existentes.

**Desenho conceitual:**

```
// Sem hashCode personalizado:
// Objeto com CPF "111.222.333-01" -> Endereço 0x7A3B -> Balde 3
// Objeto com CPF "111.222.333-01" -> Endereço 0x5F1C -> Balde 7
// Resultado: HashSet aceita AMBOS (duplicata silenciosa!)

// Com hashCode baseado no CPF:
// Objeto com CPF "111.222.333-01" -> hashCode baseado no CPF -> Balde 5
// Objeto com CPF "111.222.333-01" -> hashCode baseado no CPF -> Balde 5
// Resultado: HashSet detecta duplicata via equals() no mesmo balde
```

### 2.5. Implementando equals() e hashCode() Corretamente

A implementação profissional baseia-se em chave de negócio imutável. Utilizaremos a classe `java.util.Objects` como auxiliar:

```java
public class Participante {
    private final String cpf;  // Chave de identidade natural imutável
    private String nome;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;                                    // 1. Reflexividade
        if (obj == null || getClass() != obj.getClass()) return false;   // 2. Checagem de tipo
        Participante outro = (Participante) obj;
        return Objects.equals(this.cpf, outro.cpf);                     // 3. Comparação de chave
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.cpf);  // Gera hash baseado no mesmo atributo do equals!
    }
}
```

**Os três passos do `equals` padrão:**

1. **Reflexividade:** Um objeto é igual a si mesmo (`this == obj`).
2. **Checagem de tipo:** Verifica se o outro objeto é da mesma classe (usando `getClass()`, não `instanceof`, para preservar simetria estrita).
3. **Comparação de atributos:** Compara apenas os atributos que definem a identidade do objeto.

**Método `Objects.equals(a, b)`:** Trata o caso onde ambos são `null`, evitando `NullPointerException`.

**Método `Objects.hash(atributos...)`:** Gera um hash combinado dos atributos fornecidos. É a forma padrão e segura de implementar `hashCode`.

### 2.6. Arquitetura Interna do TreeSet\<E\>

O `TreeSet` é uma implementação de `Set` que mantém os elementos **ordenados**. Internamente, ele é baseado em uma **Árvore Rubro-Negra** (Red-Black Tree), uma estrutura de dados auto-balanceada.

**Características do TreeSet:**

- Mantém os elementos em ordem natural (via `Comparable`) ou ordem personalizada (via `Comparator`).
- **Complexidade Assintótica:** Inserção, remoção e busca em O(log n).
- **Não permite elementos nulos** (não implementa `Comparable` e não pode comparar com `null`).
- **Ordem garantida:** Diferente do `HashSet`, a iteração sempre retorna os elementos na mesma ordem.

**Quando usar TreeSet:**

- Quando é necessário manter os elementos ordenados enquanto insere.
- Para gerar listas de chamada, rankings ou relatórios em ordem.
- Quando a performance O(log n) é aceitável para o volume de dados.

### 2.7. HashSet vs. TreeSet: Critérios de Decisão

| Critério | HashSet | TreeSet |
|---|---|---|
| **Ordem de iteração** | Indeterminada | Ordenada (natural ou customizada) |
| **Complexidade (add/remove/contains)** | O(1) amortizado | O(log n) |
| **Permite null** | Sim (1 elemento null) | Não |
| **Desempenho** | Mais rápido | Mais lento (sobrecarga de árvore) |
| **Uso recomendado** | Quando a ordem não importa | Quando a ordem é necessária |

### 2.8. LinkedHashSet: A Opção Intermediária

O `LinkedHashSet` é uma implementação que mantém a **ordem de inserção** dos elementos, utilizando uma lista duplamente encadeada interna que conecta os elementos na ordem em que foram adicionados. É uma opção intermediária entre o `HashSet` (sem ordem) e o `TreeSet` (ordenado por Comparable/Comparator).

**Características:**

- Mantém a ordem de inserção.
- Complexidade O(1) para add/remove/contains (mesmo que `HashSet`).
- Mais memória que `HashSet` (por causa dos ponteiros da lista encadeada).
- Útil para logs, auditorias e históricos onde a cronologia importa.

---

## 3. Estudo de Caso Aplicado

O estudo de caso desta aula encontra-se em `exemplos/aula-13/`, no pacote `br.edu.universidade.sistema.estoque`. O sistema demonstra uso de `Map` com `HashMap` para gerenciamento de estoque e agrupamentos por categoria.

### 3.1. Entidade de Domínio: Produto

O arquivo `Produto.java` (`exemplos/aula-13/src/br/edu/universidade/sistema/estoque/dominio/Produto.java`) define a entidade com SKU como chave de negócio:

```java
public class Produto {
    private final String sku;
    private final String nome;
    private final String categoria;
    private final double precoUnitario;

    public Produto(String sku, String nome, String categoria, double precoUnitario) {
        if (sku == null || sku.isBlank() || precoUnitario < 0.0) {
            throw new IllegalArgumentException("Dados de produto inválidos.");
        }
        this.sku = sku.trim().toUpperCase();
        this.nome = nome.trim();
        this.categoria = categoria.trim().toUpperCase();
        this.precoUnitario = precoUnitario;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Produto produto = (Produto) o;
        return Objects.equals(sku, produto.sku);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sku);
    }
}
```

O SKU é normalizado em maiúsculo e sem espaços no construtor, garantindo consistência nas chaves.

### 3.2. Serviço: ControleEstoqueService

O serviço (`exemplos/aula-13/src/br/edu/universidade/sistema/estoque/service/ControleEstoqueService.java`) demonstra uso avançado de `Map` com múltiplos mapeamentos:

```java
public class ControleEstoqueService {
    private final Map<String, Integer> saldoEstoque = new HashMap<>();
    private final Map<String, Produto> catalogoProdutos = new HashMap<>();
    private final Map<String, List<Produto>> produtosPorCategoria = new HashMap<>();

    public void cadastrarProduto(Produto produto, int saldoInicial) {
        catalogoProdutos.put(produto.getSku(), produto);
        saldoEstoque.put(produto.getSku(), Math.max(0, saldoInicial));

        produtosPorCategoria
            .computeIfAbsent(produto.getCategoria(), k -> new ArrayList<>())
            .add(produto);
    }

    public void registrarMovimentacao(String sku, int quantidade) {
        String chave = sku.trim().toUpperCase();
        if (!catalogoProdutos.containsKey(chave)) {
            throw new NoSuchElementException(
                "Produto não localizado: " + chave);
        }

        int saldoAtual = saldoEstoque.getOrDefault(chave, 0);
        int novoSaldo = saldoAtual + quantidade;

        if (novoSaldo < 0) {
            throw new IllegalStateException(
                "Estoque insuficiente para: " + chave);
        }

        saldoEstoque.put(chave, novoSaldo);
    }

    public List<Produto> listarPorCategoria(String categoria) {
        return produtosPorCategoria.getOrDefault(
            categoria.trim().toUpperCase(), Collections.emptyList());
    }

    public void exibirRelatorioEstoqueConsolidado() {
        for (Map.Entry<String, Produto> entrada : catalogoProdutos.entrySet()) {
            String sku = entrada.getKey();
            Produto p = entrada.getValue();
            int saldo = saldoEstoque.getOrDefault(sku, 0);
            double valorTotalImobilizado = saldo * p.getPrecoUnitario();

            System.out.printf("%s | Saldo: %4d unid. | Valor: R$ %10.2f%n",
                p, saldo, valorTotalImobilizado);
        }
    }
}
```

**Métodos-chave demonstrados:**

- `computeIfAbsent(chave, função)`: se a chave não existe, executa a função lambda para criar o valor e insere no mapa. É atômico e eficiente.
- `getOrDefault(chave, padrão)`: retorna o valor associado ou um padrão seguro, evitando `NullPointerException`.
- `entrySet()`: retorna um `Set<Map.Entry<K,V>>` com os pares completos, permitindo iteração eficiente.

### 3.3. Aplicação Executável: EstoqueMapApp

A classe principal demonstra o ciclo de operações de estoque e também o algoritmo de contagem de frequência de palavras:

```java
public class EstoqueMapApp {
    public static void main(String[] args) {
        ControleEstoqueService service = new ControleEstoqueService();

        Produto p1 = new Produto("SKU-101", "Teclado Mecânico", "PERIFÉRICOS", 250.00);
        Produto p2 = new Produto("SKU-102", "Mouse Sem Fio", "PERIFÉRICOS", 120.00);
        Produto p3 = new Produto("SKU-201", "Monitor 27 Pol", "MONITORES", 1400.00);
        Produto p4 = new Produto("SKU-301", "Cabo HDMI 2.1", "CABOS", 45.00);

        service.cadastrarProduto(p1, 20);
        service.cadastrarProduto(p2, 35);
        service.cadastrarProduto(p3, 8);
        service.cadastrarProduto(p4, 100);

        service.registrarMovimentacao("SKU-101", -5);
        service.registrarMovimentacao("SKU-201", 2);

        service.exibirRelatorioEstoqueConsolidado();

        // Algoritmo de frequência de palavras com Map.merge()
        String textoLogs = "erro timeout conexão erro falha timeout erro fatal memória falha";
        Map<String, Integer> frequenciaTermos = contarFrequencia(textoLogs);

        Map<String, Integer> frequenciaOrdenada = new TreeMap<>(frequenciaTermos);
        frequenciaOrdenada.forEach((termo, contagem) ->
            System.out.printf("Termo: %-10s | Ocorrências: %d%n", termo, contagem));
    }

    private static Map<String, Integer> contarFrequencia(String texto) {
        Map<String, Integer> mapa = new HashMap<>();
        String[] palavras = texto.split("\\s+");
        for (String p : palavras) {
            mapa.merge(p.toLowerCase(), 1, Integer::sum);
        }
        return mapa;
    }
}
```

O método `contarFrequencia` demonstra o uso elegante de `Map.merge()`: se a chave não existir, atribui 1; se existir, soma 1 com o valor prévio. Isso substitui toda uma estrutura `if/else` ou `containsKey` em uma única linha.

---

## 4. Exercícios Propostos e Solução

### Exercício: Central de Atendimento ao Cliente (SAC)

**Enunciado:** Implemente um sistema de atendimento ao cliente que gerencie chamados de suporte, mantendo histórico por cliente e contagem de status por protocolo.

**Pacote da Solução:** `br.edu.universidade.sistema.sac` em `solucoes/aula-13/`.

**Entidade ChamadoSuporte:**

```java
public class ChamadoSuporte {
    private String protocolo;
    private String cliente;
    private String descricao;
    private String status;

    public ChamadoSuporte(String protocolo, String cliente, String descricao) {
        if (protocolo == null || protocolo.trim().isEmpty()) {
            throw new IllegalArgumentException("Protocolo não pode estar em branco.");
        }
        if (cliente == null || cliente.trim().isEmpty()) {
            throw new IllegalArgumentException("Cliente não pode estar em branco.");
        }
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new IllegalArgumentException("Descrição não pode estar em branco.");
        }
        this.protocolo = protocolo;
        this.cliente = cliente;
        this.descricao = descricao;
        this.status = "ABERTO";
    }

    public void resolverChamado() {
        this.status = "RESOLVIDO";
    }

    public String getProtocolo() { return protocolo; }
    public String getCliente() { return cliente; }
    public String getDescricao() { return descricao; }
    public String getStatus() { return status; }
}
```

**Serviço CentralAtendimentoService:**

```java
public class CentralAtendimentoService {
    private final Map<String, ChamadoSuporte> chamadosPorProtocolo = new HashMap<>();
    private final Map<String, List<ChamadoSuporte>> chamadosPorCliente = new HashMap<>();

    public void abrirChamado(ChamadoSuporte chamado) {
        if (chamado == null) {
            throw new IllegalArgumentException("Chamado nulo não pode ser aberto.");
        }
        if (chamadosPorProtocolo.containsKey(chamado.getProtocolo())) {
            throw new IllegalArgumentException(
                "Protocolo já cadastrado: " + chamado.getProtocolo());
        }
        chamadosPorProtocolo.put(chamado.getProtocolo(), chamado);
        chamadosPorCliente
            .computeIfAbsent(chamado.getCliente(), k -> new ArrayList<>())
            .add(chamado);
    }

    public ChamadoSuporte consultarPorProtocolo(String protocolo) {
        ChamadoSuporte chamado = chamadosPorProtocolo.get(protocolo);
        if (chamado == null) {
            throw new NoSuchElementException(
                "Protocolo não localizado: " + protocolo);
        }
        return chamado;
    }

    public List<ChamadoSuporte> listarHistoricoCliente(String cliente) {
        List<ChamadoSuporte> historico = chamadosPorCliente.get(cliente);
        return (historico == null) ? List.of() : historico;
    }

    public Map<String, Integer> calcularTotalChamadosPorStatus() {
        Map<String, Integer> contagem = new HashMap<>();
        for (ChamadoSuporte chamado : chamadosPorProtocolo.values()) {
            contagem.merge(chamado.getStatus(), 1, Integer::sum);
        }
        return contagem;
    }
}
```

**Aplicação SacApp:**

```java
public class SacApp {
    public static void main(String[] args) {
        CentralAtendimentoService service = new CentralAtendimentoService();

        service.abrirChamado(new ChamadoSuporte(
            "SAC-1001", "Empresa X", "Problema de login no portal"));
        service.abrirChamado(new ChamadoSuporte(
            "SAC-1002", "Empresa Y", "Erro 500 no checkout"));
        service.abrirChamado(new ChamadoSuporte(
            "SAC-1003", "Empresa X", "Faturamento duplicado"));
        service.abrirChamado(new ChamadoSuporte(
            "SAC-1004", "Empresa Z", "Integração de API indisponível"));

        service.consultarPorProtocolo("SAC-1002").resolverChamado();
        service.consultarPorProtocolo("SAC-1003").resolverChamado();

        List<ChamadoSuporte> historicoX = service.listarHistoricoCliente("Empresa X");
        historicoX.forEach(System.out::println);

        Map<String, Integer> status = service.calcularTotalChamadosPorStatus();
        status.forEach((s, total) ->
            System.out.printf("Status: %-9s | Total: %d%n", s, total));
    }
}
```

**Observações sobre a solução:**

- Dois mapas são usados: um para acesso direto por protocolo e outro para agrupamento por cliente.
- `computeIfAbsent` é usado para criar a lista de histórico do cliente de forma atômica e segura.
- `Map.merge` é utilizado para contar chamados por status, somando 1 a cada ocorrência.
- `List.of()` retorna uma lista imutável vazia quando não há histórico para o cliente.

---

## 5. Perguntas de Revisão

1. Qual é a diferença fundamental entre a interface `List` e a interface `Set`?

2. O que é uma colisão em uma tabela Hash e como o `HashSet` a resolve?

3. Por que o `hashCode()` de dois objetos iguais **deve** ser idêntico? O que acontece se não for?

4. Qual a diferença entre `HashSet` e `TreeSet` em termos de ordenação e performance?

5. Explique o método `Objects.hash()` e por que ele é preferível a implementar `hashCode` manualmente.

6. O que é o método `computeIfAbsent` e em que situações ele é superior a um `containsKey` + `put`?

7. Por que chaves de mapas e conjuntos **devem** ser imutáveis?

8. O que é o `LinkedHashSet` e quando ele é a melhor opção?

---

## 6. Resumo / Pontos-Chave

- **Interface Set\<E\>:** garante unicidade de elementos, sem acesso posicional por índice.
- **Operações de conjuntos:** `addAll` (união), `retainAll` (interseção), `removeAll` (diferença).
- **HashSet:** implementação baseada em tabela hash, com O(1) amortizado para add/remove/contains. Ordem de iteração indeterminada.
- **TreeSet:** implementação baseada em Árvore Rubro-Negra, com O(log n) e ordenação garantida.
- **Contrato equals/hashCode:** se `a.equals(b)` é verdadeiro, então `a.hashCode() == b.hashCode()` é obrigatório.
- **Implementação padrão:** usar `Objects.equals()` no `equals` e `Objects.hash()` no `hashCode`, ambos baseados na mesma chave de identidade.
- **Obrigação:** a chave de identidade do objeto deve ser imutável e incluída tanto no `equals` quanto no `hashCode`.
- **computeIfAbsent:** método atômico e eficiente para criar valores no mapa quando a chave não existe.
- **Map.merge:** método elegante para agregação e contagem de frequência em mapas.
- **Visões do Map:** `keySet()` (chaves), `values()` (valores), `entrySet()` (pares chave-valor).
