# Tutorial de Java — Aula 13: Mapas Associativos Chave-Valor com a Interface Map, Estrutura Interna de HashMap e Operações de Agrupamento

| | |
|---|---|
| **Disciplina** | Programação Orientada a Objetos |
| **Curso** | Análise e Desenvolvimento de Sistemas (3º Período) |
| **Tema** | A Interface java.util.Map, Implementações Concretas (HashMap, LinkedHashMap, TreeMap), Navegação por Conjuntos de Entradas (Map.Entry) e Agrupamentos em Memória |
| **Carga Horária** | 100 minutos (2 horas-aula) |
| **Professor Responsável** | Alexandre Neves Louzada |
| **Arquivo de Referência** | `TutorialAula13.md` |

## 1. Objetivos de Aprendizagem

- **Conceitual:** Compreender a semântica de estruturas associativas chave-valor (dicionários/tabelas de símbolos); assimilar que a interface `Map` não estende `java.util.Collection`, constituindo um ramo independente e fundamental do Java Collections Framework; entender a exigência crítica de imutabilidade e integridade de chaves.
- **Técnico:** Dominar as operações essenciais de um mapa (`put`, `get`, `getOrDefault`, `containsKey`, `containsValue`, `remove`, `replace`, `size` e `keySet`/`values`/`entrySet`); utilizar construções modernas da API como `computeIfAbsent`, `merge` e `putIfAbsent` para escrita de código conciso e livre de verificações manuais de nulidade.
- **Arquitetural:** Avaliar critérios de escolha entre as três principais implementações da interface `Map`: `HashMap` (busca de alto desempenho em tempo constante médio sem garantia de ordem), `LinkedHashMap` (preservação da ordem de inserção ou de acesso) e `TreeMap` (ordenação estrita pelas chaves via árvore rubro-negra balanceada).
- **Prático:** Implementar um motor de controle de estoque multidepósito e um analisador de contagem de frequência de palavras/tokens textuais, aplicando mapas com coleções aninhadas como valores (`Map<K, List<V>>`).

## 2. Fundamentação Teórica

### A Abstração Chave-Valor no Java Collections Framework

Diferente de `List` (sequências indexadas por inteiros) e de `Set` (conjuntos de elementos individuais únicos), a interface `java.util.Map<K, V>` modela uma estrutura de mapeamento associativo:

```plaintext
Chave Única (K) ───────────────────────────► Valor Associado (V)
[ "111.222.333-01" ]                         Cliente("Beatriz Costa")
[ "111.222.333-02" ]                         Cliente("Carlos Eduardo")
```

As regras fundamentais que regem a interface `Map` são:

- **Unicidade das Chaves:** Cada chave pode mapear para, no máximo, um único valor. Não existem chaves duplicadas. Inserir uma chave já existente com um novo valor via `.put(chave, novoValor)` substitui silenciosamente o valor antigo e retorna o valor anterior.
- **Duplicação Permitida de Valores:** Diferentes chaves podem apontar para valores idênticos (relação muitos-para-um).
- **Independência de Collection:** `Map` não implementa `java.lang.Iterable` diretamente. Para iterar sobre um mapa, deve-se extrair uma de suas visualizações em coleção: o conjunto de chaves (`keySet()`), a coleção de valores (`values()`) ou o conjunto de nós chave-valor (`entrySet()`).

### As Três Implementações Concretas de Map

| Implementação | Estrutura de Dados Subjacente | Ordenação das Chaves | Complexidade Média (put, get) |
|---|---|---|---|
| `HashMap` | Tabela Hash (Hash Table) | Sem qualquer garantia de ordem | O(1) (Tempo constante) |
| `LinkedHashMap` | Tabela Hash + Lista Duplamente Encadeada | Preserva a ordem de inserção (ou ordem de acesso/LRU) | O(1) (Tempo constante com custo adicional de ponteiros) |
| `TreeMap` | Árvore Rubro-Negra balanceada (Red-Black Tree) | Ordenação natural das chaves (`Comparable`) ou via `Comparator` | O(log n) (Tempo logarítmico) |

### A Mecânica Interna da Classe HashMap<K, V>

O `HashMap` gerencia internamente um array de nós (`Node<K, V>[] table`):

- **Cálculo de Posição:** A JVM obtém o código hash da chave (`chave.hashCode()`), aplica uma função de espalhamento complementar para distribuir os bits e calcula o índice do balde correspondente na tabela:

  > Índice = (Tamanho da Tabela − 1) & hash

- **Nó de Armazenamento:** Cada elemento no balde armazena o hash pré-calculado, a chave original K, o valor associado V e um ponteiro para o próximo nó da lista encadeada (para tratar eventuais colisões).
- **Treeificação de Baldes:** Desde o Java 8, se um balde individual acumular 8 ou mais elementos em colisão e a tabela tiver capacidade total de ao menos 64 baldes, a lista encadeada daquele balde é convertida automaticamente em uma árvore binária rubro-negra (`TreeNode`), reduzindo a complexidade de busca no pior cenário de O(n) para O(log n).

**Regra Crítica de Engenharia:** Objetos utilizados como chave em um `HashMap` DEVEM ser estritamente imutáveis e possuir implementações coesas de `equals()` e `hashCode()`. Se os atributos usados no hash de uma chave sofrerem mutação com ela já inserida no mapa, o valor associado ficará irrecuperável. Tipos como `String`, `Integer` e `Long` são escolhas ideais para chaves.

### Técnicas de Navegação em Mapas

A forma mais performática e profissional de percorrer um mapa é através da interface aninhada `Map.Entry<K, V>`, que evita buscas duplicadas na tabela hash:

```java
// ANTIPADRÃO LENTO (Duas operações de hash por iteração):
for (String chave : mapa.keySet()) {
    Cliente valor = mapa.get(chave); // Custo extra de reprocessar o hash da chave!
}

// PADRÃO DE ALTA PERFORMANCE (Acesso direto ao nó em memória):
for (Map.Entry<String, Cliente> entrada : mapa.entrySet()) {
    String chave = entrada.getKey();
    Cliente valor = entrada.getValue();
}
```

## 3. Estudo de Caso Integrado: Gestão de Estoque Multidepósito e Contagem de Frequência

O projeto prático abaixo consolida o uso de mapas associativos, operações modernas da API (`getOrDefault`, `computeIfAbsent`) e agrupamentos com listas dinâmicas em memória:

```java
package br.edu.universidade.sistema.estoque.dominio;

import java.util.Objects;

// 1. Entidade de Domínio representando um Produto em Estoque
public class Produto {
    private final String sku; // Stock Keeping Unit (Chave de Negócio)
    private final String nome;
    private final String categoria;
    private final double precoUnitario;

    public Produto(String sku, String nome, String categoria, double precoUnitario) {
        if (sku == null || sku.isBlank() || precoUnitario < 0.0) {
            throw new IllegalArgumentException("Dados de produto inválidos para catalogação.");
        }
        this.sku = sku.trim().toUpperCase();
        this.nome = nome.trim();
        this.categoria = categoria.trim().toUpperCase();
        this.precoUnitario = precoUnitario;
    }

    public String getSku() { return sku; }
    public String getNome() { return nome; }
    public String getCategoria() { return categoria; }
    public double getPrecoUnitario() { return precoUnitario; }

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

    @Override
    public String toString() {
        return String.format("[%s] %-20s (Cat: %-10s) | R$ %8.2f",
                sku, nome, categoria, precoUnitario);
    }
}
```

```java
package br.edu.universidade.sistema.estoque.service;

import br.edu.universidade.sistema.estoque.dominio.Produto;
import java.util.*;

// 2. Serviço de Gerenciamento com Mapeamentos Simples e Agrupamentos Aninhados
public class ControleEstoqueService {

    // Mapa 1: SKU -> Quantidade em Estoque (Mapeamento Direto)
    private final Map<String, Integer> saldoEstoque = new HashMap<>();

    // Mapa 2: SKU -> Entidade Produto (Catálogo Global)
    private final Map<String, Produto> catalogoProdutos = new HashMap<>();

    // Mapa 3: Categoria -> Lista de Produtos daquela categoria (Agrupamento 1:N)
    private final Map<String, List<Produto>> produtosPorCategoria = new HashMap<>();

    public void cadastrarProduto(Produto produto, int saldoInicial) {
        catalogoProdutos.put(produto.getSku(), produto);
        saldoEstoque.put(produto.getSku(), Math.max(0, saldoInicial));

        // computeIfAbsent: se a categoria não existe, instancia o ArrayList; em seguida, adiciona o produto
        produtosPorCategoria
                .computeIfAbsent(produto.getCategoria(), k -> new ArrayList<>())
                .add(produto);
    }

    public void registrarMovimentacao(String sku, int quantidade) {
        String chave = sku.trim().toUpperCase();
        if (!catalogoProdutos.containsKey(chave)) {
            throw new NoSuchElementException("Produto não localizado para o SKU informado: " + chave);
        }

        // getOrDefault evita NullPointerException caso a chave não exista no mapa
        int saldoAtual = saldoEstoque.getOrDefault(chave, 0);
        int novoSaldo = saldoAtual + quantidade;

        if (novoSaldo < 0) {
            throw new IllegalStateException(
                String.format("Estoque insuficiente para o produto %s. Atual: %d, Tentativa de baixa: %d",
                        chave, saldoAtual, Math.abs(quantidade))
            );
        }

        saldoEstoque.put(chave, novoSaldo);
    }

    public int consultarSaldo(String sku) {
        return saldoEstoque.getOrDefault(sku.trim().toUpperCase(), 0);
    }

    public List<Produto> listarPorCategoria(String categoria) {
        // Retorna a lista da categoria ou uma lista imutável vazia caso não exista
        return produtosPorCategoria.getOrDefault(categoria.trim().toUpperCase(), Collections.emptyList());
    }

    public void exibirRelatorioEstoqueConsolidado() {
        System.out.println("========== POSIÇÃO CONSOLIDADA DE ESTOQUE ==========");
        // Itera via entrySet() para máxima eficiência operacional
        for (Map.Entry<String, Produto> entrada : catalogoProdutos.entrySet()) {
            String sku = entrada.getKey();
            Produto p = entrada.getValue();
            int saldo = saldoEstoque.getOrDefault(sku, 0);
            double valorTotalImobilizado = saldo * p.getPrecoUnitario();

            System.out.printf("%s | Saldo: %4d unid. | Valor em Estoque: R$ %10.2f%n",
                    p, saldo, valorTotalImobilizado);
        }
        System.out.println("=====================================================");
    }
}
```

```java
package br.edu.universidade.sistema.estoque;

import br.edu.universidade.sistema.estoque.dominio.Produto;
import br.edu.universidade.sistema.estoque.service.ControleEstoqueService;
import java.util.*;

// 3. Aplicação Executável demonstrando uso de Map e Contagem de Frequência de Texto
public class EstoqueMapApp {
    public static void main(String[] args) {
        ControleEstoqueService service = new ControleEstoqueService();

        Produto p1 = new Produto("SKU-101", "Teclado Mecanico", "PERIFERICOS", 250.00);
        Produto p2 = new Produto("SKU-102", "Mouse Sem Fio", "PERIFERICOS", 120.00);
        Produto p3 = new Produto("SKU-201", "Monitor 27 Pol", "MONITORES", 1400.00);
        Produto p4 = new Produto("SKU-301", "Cabo HDMI 2.1", "CABOS", 45.00);

        service.cadastrarProduto(p1, 20);
        service.cadastrarProduto(p2, 35);
        service.cadastrarProduto(p3, 8);
        service.cadastrarProduto(p4, 100);

        System.out.println("--- 1. Movimentações de Entrada e Saída ---");
        service.registrarMovimentacao("SKU-101", -5);  // Venda de 5 teclados
        service.registrarMovimentacao("SKU-201", 2);   // Recebimento de 2 monitores
        System.out.printf("Saldo atual SKU-101: %d unidades%n", service.consultarSaldo("SKU-101"));

        System.out.println("\n--- 2. Consulta por Agrupamento (Categoria PERIFERICOS) ---");
        List<Produto> perifericos = service.listarPorCategoria("PERIFERICOS");
        perifericos.forEach(System.out::println);

        System.out.println("\n--- 3. Relatório Geral Consolidado ---");
        service.exibirRelatorioEstoqueConsolidado();

        System.out.println("\n--- 4. Algoritmo de Frequência de Palavras com Map ---");
        String textoLogs = "erro timeout conexao erro falha timeout erro fatal memoria falha";
        Map<String, Integer> frequenciaTermos = contarFrequencia(textoLogs);

        // Exibição ordenada alfabeticamente usando TreeMap
        Map<String, Integer> frequenciaOrdenada = new TreeMap<>(frequenciaTermos);
        frequenciaOrdenada.forEach((termo, contagem) ->
            System.out.printf("Termo: %-10s | Ocorrências: %d%n", termo, contagem)
        );
    }

    // Algoritmo clássico de contagem de frequência de termos utilizando Map.merge()
    private static Map<String, Integer> contarFrequencia(String texto) {
        Map<String, Integer> mapa = new HashMap<>();
        String[] palavras = texto.split("\\s+");

        for (String p : palavras) {
            // Se a chave não existir, atribui 1; se existir, soma 1 com o valor prévio
            mapa.merge(p.toLowerCase(), 1, Integer::sum);
        }
        return mapa;
    }
}
```

## 4. Diagnóstico de Erros Comuns e Armadilhas

### Armadilha 1: NullPointerException ao Fazer Unboxing de Valores Inexistentes

**Código Problemático:**

```java
Map<String, Integer> pontos = new HashMap<>();
pontos.put("usuarioA", 100);

int pontuacao = pontos.get("usuarioB"); // DISPARO DE EXCEÇÃO!
```

- **Diagnóstico da JVM:** `java.lang.NullPointerException: Cannot invoke "java.lang.Integer.intValue()" because the return value of "java.util.Map.get(Object)" is null.`
- **Causa & Correção:** Se a chave não existe no mapa, o método `.get()` retorna `null`. Ao tentar atribuir o retorno para o tipo primitivo `int`, o compilador executa o unboxing implícito (`.intValue()`), resultando em exceção. Utilize sempre `pontos.getOrDefault("usuarioB", 0)` para assegurar um valor padrão seguro caso a chave não esteja presente.

### Armadilha 2: Modificar a Chave Após a Inserção no Mapa

**Código Problemático:**

```java
class Identificador {
    public String codigo;
    public Identificador(String c) { this.codigo = c; }
    @Override public boolean equals(Object o) { ... }
    @Override public int hashCode() { return Objects.hash(codigo); }
}

Map<Identificador, String> sessoes = new HashMap<>();
Identificador id = new Identificador("SESSION-1");
sessoes.put(id, "DadosUsuario");

id.codigo = "SESSION-MODIFICADA"; // MUTAÇÃO DA CHAVE!
String dados = sessoes.get(id);   // RETORNA NULL!
```

- **Diagnóstico Técnico:** Ao alterar o campo `codigo`, o retorno de `hashCode()` muda. Quando você invoca `.get(id)`, a JVM procura no balde do novo hash e não encontra nada. A entrada original permanece presa no balde antigo, gerando vazamento de memória e inconsistência lógica.
- **Correção:** Declare todos os campos de classes usadas como chaves com os modificadores `private final`.

### Armadilha 3: Tentar Modificar o Mapa Durante a Iteração no keySet()

**Código Problemático:**

```java
Map<String, Integer> saldos = new HashMap<>(Map.of("A", 10, "B", 0, "C", 5));
for (String k : saldos.keySet()) {
    if (saldos.get(k) == 0) {
        saldos.remove(k); // MODIFICAÇÃO CONCORRENTE INDEVIDA!
    }
}
```

- **Diagnóstico da JVM:** `java.util.ConcurrentModificationException`.
- **Causa & Correção:** Remover chaves diretamente pelo mapa durante uma iteração baseada em cursor corrompe o ponteiro estrutural. Para remover de forma segura, utilize a API funcional nativa: `saldos.entrySet().removeIf(entry -> entry.getValue() == 0);`.

## 5. Roteiro Prático de Depuração: Inspecionando Nós e Baldes de HashMap na IDE

Para auditar o empacotamento dos nós chave-valor no depurador da sua IDE (IntelliJ IDEA, Eclipse ou VS Code):

1. No método `main` de `EstoqueMapApp`, posicione um ponto de interrupção (*breakpoint*) logo após a linha do cadastro de `p4`.
2. Inicie a execução em modo de depuração (*Debug*).
3. Na janela de variáveis (*Variables*), expanda o objeto `service` e localize o atributo `catalogoProdutos`:
   - Observe o vetor interno `table` (a tabela de baldes com tamanho inicial `16`).
   - Expanda os índices populados: veja que cada balde contém uma instância de `Node` com os campos `hash`, `key` (`"SKU-101"`), `val` (a referência ao objeto `Produto`) e `next` (apontando para o próximo nó em caso de colisão).
4. Avance a execução (*Step Over* — F8) até a linha que executa `contarFrequencia` e inspecione o objeto `frequenciaTermos`:
   - Observe como o método `merge()` gerencia a transição de contagem de cada palavra sem instanciar listas intermediárias nem disparar checagens manuais de `null`.

## 6. Exercício de Fixação Prática: Central de Atendimento e SAC Corporativo

Implemente um sistema de triagem e histórico de chamados de suporte técnico aplicando mapas associativos:

1. **Construa a Classe `ChamadoSuporte`:**
   - Atributos privados: `protocolo` (`String`), `cliente` (`String`), `descricao` (`String`), `status` (`String` — `"ABERTO"` ou `"RESOLVIDO"`).
   - Construtor parametrizado completo rejeitando protocolos em branco via `IllegalArgumentException`.
   - Métodos acessores (*getters*) e método `void resolverChamado()` que altera o `status` para `"RESOLVIDO"`.
   - Método descritivo `toString()`.

2. **Construa o Serviço `CentralAtendimentoService`:**
   - Declare dois mapas internos:
     ```java
     private final Map<String, ChamadoSuporte> chamadosPorProtocolo = new HashMap<>(); // Busca direta O(1)
     private final Map<String, List<ChamadoSuporte>> chamadosPorCliente = new HashMap<>(); // Agrupamento 1:N
     ```
   - Método `void abrirChamado(ChamadoSuporte chamado)`: cadastra o chamado no mapa de protocolos e adiciona à lista do cliente correspondente utilizando obrigatoriamente `computeIfAbsent()`.
   - Método `ChamadoSuporte consultarPorProtocolo(String protocolo)`: retorna o chamado utilizando busca direta por chave, lançando `NoSuchElementException` se o protocolo não existir.
   - Método `List<ChamadoSuporte> listarHistoricoCliente(String cliente)`: retorna a lista de chamados daquele cliente ou uma lista vazia imutável caso ele nunca tenha aberto chamados.
   - Método `Map<String, Integer> calcularTotalChamadosPorStatus()`: itera sobre todos os chamados registrados e produz um novo mapa contendo a contagem consolidada de chamados agrupados pelo status (`"ABERTO"` → total, `"RESOLVIDO"` → total) utilizando `Map.merge()`.

3. **Construa a Classe Executável `SacApp`:**
   - Cadastre quatro chamados distribuídos entre três clientes diferentes.
   - Resolva dois dos chamados via protocolo.
   - Exiba o histórico completo de um dos clientes.
   - Imprima o mapa de status consolidado comprovando a contagem de quantos chamados estão abertos e quantos foram resolvidos.