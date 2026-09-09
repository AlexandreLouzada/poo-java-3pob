# Explicação da Aula 19 — Tipos Genéricos (Java Generics), Limites e o Princípio PECS

| | |
|---|---|
| **Disciplina** | Programação Orientada a Objetos |
| **Curso** | Análise e Desenvolvimento de Sistemas (3º Período) |
| **Tema** | Tipos Parametrizados (Generics), Segurança em Tempo de Compilação (*Type-Safety*), Classes e Métodos Genéricos, Curingas (*Wildcards* `?`), Princípio PECS (*Producer Extends, Consumer Super*) e Apagamento de Tipos (*Type Erasure*) |
| **Carga Horária** | 100 minutos (2 horas-aula) |
| **Professor** | Alexandre Neves Louzada |
| **Tutorial** | `aulas/TutorialAula19.md` |
| **Estudo de Caso** | `exemplos/aula-19/` |
| **Exercícios Resolvidos** | `solucoes/aula-19/` |

## 1. Objetivos de Aprendizagem

- **Conceitual:** Compreender a necessidade histórica dos tipos genéricos na evolução do Java 5, eliminando a dependência do tipo universal `Object` e das conversões explícitas (casting) inseguras; assimilar o princípio da segurança de tipos em tempo de compilação (*Compile-Time Type-Safety*); entender a invariância inerente aos genéricos.
- **Técnico:** Projetar classes, interfaces e métodos genéricos parametrizados por tipos abstratos (`<T>`, `<K, V>`, `<E>`); declarar restrições de tipos limitados (*Bounded Type Parameters* como `<T extends Number>`); utilizar curingas desconhecidos (`<?>`), curingas com limitador superior (*Upper Bounded Wildcards* `<? extends T>`) e limitadores inferiores (*Lower Bounded Wildcards* `<? super T>`).
- **Arquitetural:** Dominar o princípio PECS (*Producer Extends, Consumer Super*), estabelecendo a assinatura correta para métodos de coleções genéricas em APIs públicas; compreender o funcionamento e as limitações do Apagamento de Tipos (*Type Erasure*) na Máquina Virtual Java (JVM).
- **Prático:** Implementar um repositório genérico em memória desacoplado para entidades de domínio e um motor de processamento e consolidação de documentos fiscais e cadastrais.

## 2. Conteúdo Teórico Detalhado

### 2.1 O Contexto Histórico: O Problema das Coleções Pré-Java 5

Antes do Java 5, as estruturas de dados do Java Collections Framework armazenavam exclusivamente referências do tipo genérico universal `java.lang.Object`. Isso gerava dois problemas críticos de engenharia: a falta de verificação estática (o compilador não impedia que tipos incompatíveis fossem inseridos na mesma coleção) e a proliferação de castings (exigia a conversão explícita manual a cada leitura, transferindo a detecção de erros de tipo para o ambiente de produção).

```java
// CÓDIGO LEGADO (Inseguro e frágil):
List nomes = new ArrayList();
nomes.add("Carlos");
nomes.add(Integer.valueOf(100)); // Compila sem qualquer erro!

// Em outro ponto do sistema:
for (int i = 0; i < nomes.size(); i++) {
    String s = (String) nomes.get(i); // Dispara ClassCastException na posição 1!
}
```

Com a introdução dos Generics, o compilador assumiu a responsabilidade de verificar a homogeneidade dos dados em tempo de compilação:

```java
// CÓDIGO MODERNO COM GENERICS:
List<String> nomes = new ArrayList<>();
nomes.add("Carlos");
// nomes.add(100); // ERRO DE COMPILAÇÃO IMEDIATO!
String s = nomes.get(0); // Dispensa casting manual
```

### 2.2 Convenções de Nomenclatura para Parâmetros de Tipo

Por convenção internacional, utilizam-se letras maiúsculas e únicas para representar parâmetros genéricos:

| Parâmetro | Significado | Exemplo de Uso |
|---|---|---|
| **T** | *Type* (Tipo geral) | `Caixa<T>` |
| **E** | *Element* (Elemento de coleção) | `List<E>`, `Set<E>` |
| **K** | *Key* (Chave de mapeamento) | `Map<K, V>` |
| **V** | *Value* (Valor associado) | `Map<K, V>` |
| **N** | *Number* (Representação numérica) | Restrição numérica |
| **R** | *Result* (Tipo de retorno) | Métodos funcionais |

### 2.3 Invariância dos Tipos Genéricos

Embora `Integer` seja uma subclasse de `Number` (`Integer extends Number`), uma lista parametrizada como `List<Integer>` **nao** e um subtipo de `List<Number>`. Essa restricao e chamada de **invariancia generica**.

Se o Java permitisse que `List<Number> lista = new ArrayList<Integer>();` compilasse, seria possivel executar `lista.add(Double.valueOf(3.14));`, corrompendo a lista interna de inteiros. Para contornar essa restricao sem abrir maos do polimorfismo, a linguagem introduziu os **Curingas (Wildcards)**.

### 2.4 Curingas (Wildcards) e o Principio PECS

A interrogacao (`?`) representa um tipo desconhecido em tempo de compilacao. Existem tres categorias de wildcards:

- **Curinga Irrestrito (`<?>`):** Representa qualquer tipo desconhecido (`List<?>`). Pode ser consultado como `Object`, mas nao aceita insercoes (exceto `null`).
- **Curinga com Limitador Superior (`<? extends T>`):** Aceita qualquer tipo que seja `T` ou subclasse de `T`. O limite superior e `T`. A colecao e somente leitura (o compilador proibe a insercao de novos elementos).
- **Curinga com Limitador Inferior (`<? super T>`):** Aceita qualquer tipo que seja `T` ou ancestral/superclasse de `T`. O limite inferior e `T`. E permitido inserir elementos do tipo `T` com total seguranca de tipo.

O **Principio PECS** (formalizado por Joshua Bloch) define qual wildcard escolher:

- **Producer Extends (`? extends T`):** Se a colecao for utilizada para fornecer/ler dados (a colecao atua como **produtora**), utilize `extends`.
- **Consumer Super (`? super T`):** Se a colecao for utilizada para armazenar/receber dados (a colecao atua como **consumidora**), utilize `super`.

### 2.5 A Mecanica Interna: O Apagamento de Tipos (Type Erasure)

Para manter a compatibilidade reversa com arquivos binarios de versoes legadas do Java, os genericos foram implementados via **Apagamento de Tipos (Type Erasure)**:

- Em tempo de compilacao, o compilador verifica todas as regras de tipo e injeta os casts automaticos no Bytecode.
- Em seguida, o compilador remove toda a informacao dos tipos genericos dos arquivos `.class`.
- Na JVM em tempo de execucao, um `List<String>` e um `List<Integer>` sao identicos: ambos existem na memoria simplesmente como `List` bruto contendo instancias de `Object`.

Consequencias praticas do Type Erasure:

- Nao e possivel instanciar um tipo generico diretamente: `new T()` e proibido.
- Nao e possivel instanciar arrays genericos: `new T[10]` e proibido.
- Nao e possivel usar tipos primitivos em generics: `List<int>` deve ser `List<Integer>`.
- Nao e possivel usar `instanceof` com tipos parametrizados: `if (x instanceof List<String>)` e ilegal.

## 3. Estudo de Caso Aplicado

O projeto do estudo de caso implementa uma arquitetura corporativa composta por uma classe base de dominio, um repositorio generico reaproveitavel com restricao `<T extends EntidadeBase>` e um servico que aplica a regra PECS para transferencia e auditoria de lotes.

**Estrutura de pacotes (exemplos/aula-19/src):**

```
br.edu.universidade.sistema.generics
  |-- dominio/
  |     |-- EntidadeBase.java       (classe abstrata generica com ID)
  |     |-- Produto.java            (entidade concreta com ID Long)
  |     |-- Cliente.java            (entidade concreta com ID String/CPF)
  |-- repositorio/
  |     |-- RepositorioGenerico.java (repositorio em memoria com <T extends EntidadeBase<ID>, ID>)
  |-- service/
  |     |-- ProcessadorLotesService.java (metodos genericos com PECS)
  |-- GenericsApp.java              (aplicacao executavel principal)
```

A classe `EntidadeBase<ID>` e uma abstracao generica que garante toda entidade de dominio possua um identificador imutavel. As entidades `Produto` e `Cliente` estendem essa base, demonstrando que o tipo do ID pode variar (`Long` para produtos, `String` para CPFs de clientes).

O `RepositorioGenerico<T extends EntidadeBase<ID>, ID>` e a joya arquitetural deste exemplo: um unico repositorio reutilizavel para qualquer entidade que implemente `EntidadeBase`, usando um `Map<ID, T>` como armazenamento em memoria. A restricao `<T extends EntidadeBase<ID>>` garante que apenas entidades de dominio validas possam ser persistidas.

O servico `ProcessadorLotesService` demonstra o principio PECS em plena acao:

```java
public static <T> void transferirElementos(
        List<? extends T> origem,  // Producer Extends: leitura segura
        List<? super T> destino    // Consumer Super: insercao segura
) {
    for (T elemento : origem) {
        destino.add(elemento);
    }
}
```

O metodo `calcularPrecoMedio` usa `List<? extends Produto>` para aceitar qualquer lista que contenha `Produto` ou suas subclasses, mantendo a seguranca de tipos.

No `GenericsApp`, demonstra-se a instanciacao do repositorio para `Produto` (com `Long`) e para `Cliente` (com `String`), provando o reuso completo. Tambem se demonstra o PECS copiando uma `List<Produto>` para uma `List<Object>`.

## 4. Exercicios Propostos e Solucao

O exercicio de fixacao propoe a construcao de um componente corporativo reutilizavel de auditoria aplicando classes genericas, limites de tipo e PECS.

**Solucao completa (solucoes/aula-19/src):**

A interface generica `Auditor<T>` e o ponto de partida:

```java
package br.edu.universidade.sistema.generics.auditoria;

public interface Auditor<T> {
    boolean auditar(T objeto);
}
```

A classe `AuditoriaService` implementa dois metodos genericos que aplicam rigorosamente o PECS:

```java
package br.edu.universidade.sistema.generics.auditoria;

import java.util.List;

public final class AuditoriaService {

    public static <T> int contarAprovados(List<? extends T> lista, Auditor<? super T> auditor) {
        int aprovados = 0;
        for (T item : lista) {
            if (auditor.auditar(item)) {
                aprovados++;
            }
        }
        return aprovados;
    }

    public static <T> void mesclarColecoes(List<? extends T> fonte, List<? super T> destino) {
        for (T item : fonte) {
            destino.add(item);
        }
    }

    private AuditoriaService() {}
}
```

Note como `contarAprovados` recebe `List<? extends T>` (produtora: leitura) e `Auditor<? super T>` (consumidora: recebe o item para auditar). O metodo `mesclarColecoes` copia seguramente de uma fonte para um destino usando o mesmo principio.

A classe `Produto` e simples e direta:

```java
package br.edu.universidade.sistema.generics.auditoria;

public class Produto {
    private String nome;
    private double preco;

    public Produto(String nome, double preco) {
        this.nome = nome;
        this.preco = preco;
    }

    public String getNome() { return nome; }
    public double getPreco() { return preco; }

    @Override
    public String toString() {
        return String.format("Produto[nome=%s, preco=%.2f]", nome, preco);
    }
}
```

A aplicacao executavel `AuditoriaGenericsApp` valida os cenarios:

```java
package br.edu.universidade.sistema.generics.auditoria;

import java.util.ArrayList;
import java.util.List;

public class AuditoriaGenericsApp {
    public static void main(String[] args) {
        List<Produto> produtos = new ArrayList<>();
        produtos.add(new Produto("Servidor Rack", 8500.00));
        produtos.add(new Produto("Workstation", 4200.00));
        produtos.add(new Produto("Teclado", 120.00));
        produtos.add(new Produto("Monitor 24", 780.00));

        Auditor<Object> auditorGenerico = obj -> obj.toString().length() > 10;
        Auditor<Produto> auditorEspecifico = p -> p.getPreco() > 1000.00;

        System.out.println("--- Auditor Generico (Object): toString com mais de 10 caracteres ---");
        int aprovadosGenerico = AuditoriaService.contarAprovados(produtos, auditorGenerico);
        System.out.println("Produtos aprovados: " + aprovadosGenerico + " de " + produtos.size());

        System.out.println("\n--- Auditor Especifico (Produto): preco acima de R$ 1.000,00 ---");
        int aprovadosEspecifico = AuditoriaService.contarAprovados(produtos, auditorEspecifico);
        System.out.println("Produtos aprovados: " + aprovadosEspecifico + " de " + produtos.size());

        System.out.println("\n--- Reuso do Auditor<? super T> com validadores de superclasses ---");
        int aprovadosGenericoComoSuper = AuditoriaService.contarAprovados(produtos, (Auditor<Object>) auditorGenerico);
        System.out.println("Auditor<Object> aplicado em List<Produto>: " + aprovadosGenericoComoSuper + " aprovados.");

        System.out.println("\n--- PECS: Mesclagem Segura (Produto -> ? super Produto) ---");
        List<Object> destino = new ArrayList<>();
        AuditoriaService.mesclarColecoes(produtos, destino);
        System.out.println("Elementos copiados para o destino (List<Object>): " + destino.size());

        System.out.println("Mesclagem inversa (Object -> Produto) e bloqueada pelo compilador (PECS).");
    }
}
```

A saida esperada mostra que:
- O `Auditor<Object>` aprova 3 dos 4 produtos (todos cujo `toString()` tem mais de 10 caracteres).
- O `Auditor<Produto>` aprova 2 produtos (aqueles com preco superior a R$ 1.000,00).
- A mesclagem de `List<Produto>` para `List<Object>` funciona graças ao `Auditor<? super T>`.

## 5. Perguntas de Revisao

1. Por que `List<Integer>` nao e subtipo de `List<Number>` mesmo que `Integer extends Number`?
2. Qual a diferenca entre `List<?>`, `List<? extends Number>` e `List<? super Number>`?
3. Enuncie o principio PECS e explique cada palavra do acronimo.
4. O que acontece com os tipos genericos quando o codigo e compilado? Explique o Type Erasure.
5. Por que nao e possivel escrever `new T()` dentro de uma classe generica?
6. Qual a restricao que o `RepositorioGenerico` impoe aos seus tipos e por que essa restricao e necessaria?
7. Se um metodo recebe `List<? extends Animal>`, e possivel adicionar elementos nela? Por que?

## 6. Resumo / Pontos-Chave

- **Generics** introduziram seguranca de tipos em tempo de compilacao ao substituir o uso generic de `Object` por tipos parametrizados.
- **Invariancia** significa que `List<Subtipo>` nao e subtipo de `List<Supertipo`, mesmo que `Subtipo extends Supertipo`.
- **Wildcards** contornam a invariancia: `? extends T` para leitura (produtor), `? super T` para escrita (consumidor).
- **PECS** (Producer Extends, Consumer Super) e o guia definitivo para assinaturas de metodos com wildcards em APIs genericas.
- **Type Erasure** remove as informacoes de tipo em tempo de execucao por razoes de compatibilidade, impondo restricoes como a impossibilidade de instanciar `new T()`.
- O **RepositorioGenerico** demonstra como classes genericas com limites (`<T extends EntidadeBase<ID>>`) criam componentes reutilizaveis e tipados seguros.
- O exercicio de **Auditoria** consolida todos os conceitos: interface generica, metodos genericos com PECS e reuso polimorfico de auditores de superclasses.
