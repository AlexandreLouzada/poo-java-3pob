# Tutorial de Java — Aula 14: Expressões Lambda, Interfaces Funcionais e o Paradigma Funcional na JVM

| | |
|---|---|
| **Disciplina** | Programação Orientada a Objetos |
| **Curso** | Análise e Desenvolvimento de Sistemas (3º Período) |
| **Tema** | Expressões Lambda, a Anotação @FunctionalInterface, Interfaces Funcionais Padronizadas do Pacote java.util.function e Refatoração de Classes Anônimas |
| **Carga Horária** | 100 minutos (2 horas-aula) |
| **Professor Responsável** | Alexandre Neves Louzada |
| **Arquivo de Referência** | `TutorialAula14.md` |

## 1. Objetivos de Aprendizagem

- **Conceitual:** Compreender a transição do paradigma puramente imperativo para o modelo híbrido imperativo-funcional incorporado no Java 8; assimilar o conceito de funções como cidadãs de primeira classe (*first-class citizens*) representadas por interfaces funcionais; entender as vantagens de concisão, clareza declarativa e redução de código redundante (*boilerplate*).
- **Técnico:** Dominar a sintaxe das expressões lambda (parâmetros, operador flecha `->` e corpo da expressão/bloco); aplicar a anotação `@FunctionalInterface` com rigor semântico; dominar as quatro interfaces funcionais fundamentais da linguagem: `Predicate<T>`, `Function<T, R>`, `Consumer<T>` e `Supplier<T>`.
- **Arquitetural:** Compreender a mecânica interna de resolução das lambdas na JVM via instrução `invokedynamic` (introduzida pela JSR 292), contrastando-a com a alocação volumosa de classes anônimas internas (*anonymous inner classes*) no Heap; aplicar a regra de captura de variáveis do escopo envolvente (*effectively final*).
- **Prático:** Implementar um motor flexível de filtragem, processamento e transformação de dados de folha de pagamento e auditoria corporativa, eliminando estruturas condicionais aninhadas por meio da composição dinâmica de funções.

## 2. Fundamentação Teórica

### A Evolução da Concisão: De Classes Anônimas para Lambdas

Até o Java 7, para repassar um comportamento customizado como parâmetro para um método (por exemplo, um critério de ordenação ou um filtro de eventos), o desenvolvedor era obrigado a instanciar uma Classe Anônima Interna (*Anonymous Inner Class*).

Considere a necessidade de ordenar uma lista de funcionários por salário:

```java
// O MODELO LEGADO (Verboso e ruidoso):
Collections.sort(funcionarios, new Comparator<Funcionario>() {
    @Override
    public int compare(Funcionario f1, Funcionario f2) {
        return Double.compare(f1.getSalario(), f2.getSalario());
    }
});
```

Esse padrão apresentava três inconvenientes graves:

- **Ruído Sintático:** Cinco linhas de código para expressar uma única comparação de números.
- **Poluição de Bytecode:** Para cada classe anônima declarada, o compilador `javac` gerava um arquivo binário separado no disco (ex.: `FolhaApp$1.class`).
- **Alocação Desnecessária no Heap:** A cada execução, um novo objeto interno anônimo precisava ser instanciado e posteriormente coletado pelo Garbage Collector.

A partir do Java 8, esse mesmo comportamento passou a ser escrito de forma concisa como uma Expressão Lambda:

```java
// O MODELO MODERNO COM EXPRESSÃO LAMBDA:
funcionarios.sort((f1, f2) -> Double.compare(f1.getSalario(), f2.getSalario()));
```

### A Mecânica Interna: O Operador invokedynamic

Ao contrário do que parece à primeira vista, uma expressão lambda NÃO é apenas um atalho sintático para instanciar uma classe anônima.

- Quando o compilador encontra uma lambda, ele gera uma instrução especial de bytecode chamada `invokedynamic` em vez de criar um arquivo de classe separado.
- Em tempo de execução, a JVM utiliza a classe utilitária `LambdaMetafactory` para ligar a função dinamicamente na primeira invocação, transformando a chamada em um ponteiro ultraotimizado sem custo adicional de alocação de classes ou sobrecarga de memória no Heap.

### O Contrato das Interfaces Funcionais (@FunctionalInterface)

Uma interface funcional é definida como qualquer interface que possua exatamente um único método abstrato (SAM — *Single Abstract Method*). Ela pode conter múltiplos métodos padrão (`default`) e métodos estáticos (`static`), desde que mantenha apenas um método sem implementação de corpo.

A anotação `@FunctionalInterface` deve ser colocada no topo da interface para instruir o compilador `javac` a auditar esse contrato:

```java
@FunctionalInterface
public interface ValidadorFiscal<T> {
    // Único método abstrato (SAM): Define o contrato aceito pela Lambda
    boolean validar(T entidade);

    // Métodos default auxiliares são permitidos livremente
    default ValidadorFiscal<T> e(ValidadorFiscal<T> outro) {
        return entidade -> this.validar(entidade) && outro.validar(entidade);
    }
}
```

Caso outro desenvolvedor tente adicionar inadvertidamente um segundo método abstrato nessa interface, o compilador rejeitará a instrução na hora: "Multiple non-overriding abstract methods found".

### As Quatro Interfaces Funcionais Basilares (java.util.function)

Para evitar a proliferação desordenada de interfaces proprietárias, a API padrão consolidou quatro categorias fundamentais de funções:

| Interface Funcional | Assinatura do Método Abstrato | Analogia / Papel | Exemplo de Aplicação |
|---|---|---|---|
| `Predicate<T>` | `boolean test(T t)` | Filtro / Decisão: Recebe um dado e retorna verdadeiro ou falso. | `p -> p.getSalario() > 5000.0` |
| `Function<T, R>` | `R apply(T t)` | Transformação / Mapeador: Recebe tipo T e projeta/converte em R. | `f -> f.getNome().toUpperCase()` |
| `Consumer<T>` | `void accept(T t)` | Ação / Efeito Colateral: Recebe o dado e executa uma ação (não retorna nada). | `msg -> System.out.println(msg)` |
| `Supplier<T>` | `T get()` | Fábrica / Provedor: Não recebe parâmetros e produz uma nova instância de T. | `() -> new ArrayList<>()` |

### A Regra do Escopo Envolvente: Variáveis Effectively Final

Uma expressão lambda pode acessar variáveis declaradas fora de seu próprio corpo (no método envolvente), mas com uma restrição arquitetural rígida: a variável acessada deve ser `final` ou explicitamente "efetivamente final" (*effectively final*).

```java
double aliquotaBase = 0.15; // Variável local do método envolvente

// Compila perfeitamente: 'aliquotaBase' nunca sofreu reatribuição (é efetivamente final)
Function<Double, Double> calculador = valor -> valor * (1 + aliquotaBase);

// SE VOCÊ FIZER ISSO EM SEGUIDA:
// aliquotaBase = 0.20;
// O compilador QUEBRA a lambda acima:
// "local variables referenced from a lambda expression must be final or effectively final"
```

Essa barreira da JVM existe porque as variáveis locais primitivas residem na Stack do método. Como a lambda pode ser executada de forma assíncrona ou em outra thread quando aquele frame da Stack já foi destruído, o Java copia o valor primitivo para dentro da estrutura da função. Se a variável pudesse mudar de valor depois disso, haveria inconsistência de memória e condições de corrida entre a Stack e o Heap.

## 3. Estudo de Caso Integrado: Motor de Auditoria e Bonificação Salarial

O projeto prático abaixo consolida o uso de interfaces funcionais customizadas, encadeamento de `Predicate`, mapeamentos com `Function` e despacho de ações com `Consumer`:

```java
package br.edu.universidade.sistema.rh.dominio;

// 1. Entidade de Domínio Funcional
public class Colaborador {
    private final Long id;
    private final String nome;
    private final String departamento;
    private double salarioBase;
    private final int tempoServicoAnos;

    public Colaborador(Long id, String nome, String departamento, double salarioBase, int tempoServicoAnos) {
        this.id = id;
        this.nome = nome;
        this.departamento = departamento;
        this.salarioBase = salarioBase;
        this.tempoServicoAnos = tempoServicoAnos;
    }

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public String getDepartamento() { return departamento; }
    public double getSalarioBase() { return salarioBase; }
    public int getTempoServicoAnos() { return tempoServicoAnos; }

    public void aplicarAumento(double valorAdicional) {
        if (valorAdicional > 0) {
            this.salarioBase += valorAdicional;
        }
    }

    @Override
    public String toString() {
        return String.format("[%d] %-15s | Depto: %-10s | Salário: R$ %8.2f | Anos: %d",
                id, nome, departamento, salarioBase, tempoServicoAnos);
    }
}
```

```java
package br.edu.universidade.sistema.rh.service;

import br.edu.universidade.sistema.rh.dominio.Colaborador;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

// 2. Serviço Desacoplado operando através de Comportamentos Funcionais
public class AuditoriaRhService {

    // Método de Alta Ordem: Recebe um Predicate para filtrar elementos de forma dinâmica
    public List<Colaborador> filtrar(List<Colaborador> equipe, Predicate<Colaborador> criterio) {
        List<Colaborador> resultado = new ArrayList<>();
        for (Colaborador c : equipe) {
            if (criterio.test(c)) { // Executa a função booleana injetada pela Lambda
                resultado.add(c);
            }
        }
        return resultado;
    }

    // Método de Transformação: Projeta colaboradores em qualquer outro tipo via Function
    public <R> List<R> mapear(List<Colaborador> equipe, Function<Colaborador, R> transformador) {
        List<R> resultado = new ArrayList<>();
        for (Colaborador c : equipe) {
            resultado.add(transformador.apply(c)); // Aplica a transformação funcional
        }
        return resultado;
    }

    // Método de Ação: Dispara um Consumer em cada registro filtrado
    public void executarAcao(List<Colaborador> equipe, Consumer<Colaborador> acao) {
        for (Colaborador c : equipe) {
            acao.accept(c); // Despacha a ação sem esperar retorno
        }
    }
}
```

```java
package br.edu.universidade.sistema.rh;

import br.edu.universidade.sistema.rh.dominio.Colaborador;
import br.edu.universidade.sistema.rh.service.AuditoriaRhService;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

// 3. Aplicação Executável demonstrando Composição Funcional
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

        System.out.println("--- 1. Filtragem com Predicate Simples e Encadeado ---");
        // Predicados atômicos e reutilizáveis
        Predicate<Colaborador> ehDeTi = c -> c.getDepartamento().equalsIgnoreCase("TI");
        Predicate<Colaborador> ehSenior = c -> c.getTempoServicoAnos() >= 5;

        // Composição booleana funcional usando o método default 'and()'
        List<Colaborador> tiSeniors = service.filtrar(equipe, ehDeTi.and(ehSenior));
        tiSeniors.forEach(c -> System.out.println(c));

        System.out.println("\n--- 2. Transformação e Projeção com Function ---");
        // Extrai apenas os nomes em letras maiúsculas
        Function<Colaborador, String> crachaFormatter = c ->
                String.format("CRACHÁ: %s (%s)", c.getNome().toUpperCase(), c.getDepartamento());

        List<String> crachas = service.mapear(equipe, crachaFormatter);
        crachas.forEach(s -> System.out.println(s));

        System.out.println("\n--- 3. Aplicação de Efeito Colateral com Consumer ---");
        // Regra de Bonificação: Concede bônus de R$ 500 para colaboradores com mais de 7 anos
        Predicate<Colaborador> aptosBonus = c -> c.getTempoServicoAnos() >= 7;
        Consumer<Colaborador> concederBonus = c -> {
            c.aplicarAumento(500.00);
            System.out.printf("Bônus aplicado para %s! Novo Salário: R$ %.2f%n",
                    c.getNome(), c.getSalarioBase());
        };

        List<Colaborador> veteranos = service.filtrar(equipe, aptosBonus);
        service.executarAcao(veteranos, concederBonus);
    }
}
```

## 4. Diagnóstico de Erros Comuns e Armadilhas

### Armadilha 1: Violação de Effectively Final com Mutações Locais

**Código Problemático:**

```java
int contador = 0;
List<String> nomes = List.of("Java", "Kotlin", "Scala");

nomes.forEach(n -> {
    contador++; // ERRO DE COMPILAÇÃO!
    System.out.println(n);
});
```

- **Diagnóstico da JVM:** `local variables referenced from a lambda expression must be final or effectively final.`
- **Causa & Correção:** Primitivos locais não podem ser mutados dentro de lambdas para garantir integridade e isolamento de escopo de pilha. Se contadores acumuladores forem necessários, utilize classes atômicas apropriadas (`AtomicInteger`) ou utilize as operações nativas de redução da Streams API.

### Armadilha 2: Tentar Usar Mais de um Método Abstrato em @FunctionalInterface

**Código Problemático:**

```java
@FunctionalInterface
public interface ProcessadorTransacao {
    void executar();
    void cancelar(); // ERRO DE COMPILAÇÃO!
}
```

- **Diagnóstico da JVM:** `Unexpected @FunctionalInterface annotation: ProcessadorTransacao is not a functional interface (multiple non-overriding abstract methods found).`
- **Causa & Correção:** Expressões lambda só conseguem inferir a implementação quando há exatamente um único método abstrato. Se a interface exigir mais de uma operação contratual, ela é uma interface convencional e não pode ser instanciada diretamente via sintaxe lambda.

### Armadilha 3: Ambiguidade em Sobrecargas que Recebem Interfaces Diferentes

**Código Problemático:**

```java
public void despachar(Runnable r) { r.run(); }
public void despachar(Callable<String> c) throws Exception { c.call(); }

// No código chamador:
despachar(() -> System.out.println("Executando")); // Ambiguidade detectada!
```

- **Diagnóstico da JVM:** `reference to despachar is ambiguous; both method despachar(Runnable) and method despachar(Callable) match.`
- **Causa & Correção:** Quando duas interfaces funcionais distintas aceitam assinaturas compatíveis, o compilador não consegue inferir qual o tipo pretendido. Desambigue explicitamente fazendo um cast: `despachar((Runnable) () -> System.out.println("Executando"));`.

## 5. Roteiro Prático de Depuração: Inspecionando Lambdas e Frames na IDE

Para auditar a resolução sintética de uma expressão lambda no depurador da sua IDE (IntelliJ IDEA, Eclipse ou VS Code):

1. No método `main` de `RhFuncionalApp`, posicione um ponto de interrupção (*breakpoint*) dentro do corpo da lambda: na linha `return c.getDepartamento().equalsIgnoreCase("TI");` dentro do predicado `ehDeTi`.
2. Inicie a execução em modo de depuração (*Debug*).
3. Na janela de quadros de execução (*Frames / Call Stack*):
   - Observe que a execução está pausada dentro de um método gerado dinamicamente chamado algo como `RhFuncionalApp.lambda$main$0(Colaborador)`.
   - Note que o frame imediatamente abaixo é o laço `filtrar` da classe `AuditoriaRhService`, comprovando como a JVM despacha o método da interface através da instrução de pilha sem ter criado uma classe física anônima no disco.
4. Avance a execução (*Resume Program* — F9): comprove que o predicado é acionado independentemente a cada iteração do laço com total transparência.

## 6. Exercício de Fixação Prática: Motor de Regras e Tarifação de Transações

Implemente um componente de validação e tributação financeira utilizando exclusivamente interfaces funcionais padronizadas:

1. **Construa a Classe `TransacaoFinanceira`:**
   - Atributos privados: `id` (`Long`), `chavePix` (`String`), `valor` (`double`) e `tipoOperacao` (`String` — `"DEBITO"`, `"CREDITO"`, `"BOLETO"`).
   - Construtor completo com regras de guarda (valor maior que zero e campos de texto não nulos).
   - Métodos acessores (*getters*) e método `void abaterTaxa(double taxa)` que subtrai o custo do montante da transação.
   - Método descritivo `toString()` formatando o valor com `%.2f`.

2. **Construa o Serviço `MotorRegrasFinanceirasService`:**
   - Método `List<TransacaoFinanceira> auditarTransacoes(List<TransacaoFinanceira> lista, Predicate<TransacaoFinanceira> regraAuditoria)`: retorna apenas as transações aprovadas pelo predicado.
   - Método `void aplicarTarifacao(List<TransacaoFinanceira> lista, Function<TransacaoFinanceira, Double> calculadorTarifa, Consumer<TransacaoFinanceira> auditoriaFinal)`:
     - Para cada transação da lista, calcula a taxa correspondente via `calculadorTarifa.apply(t)`.
     - Aplica o desconto através de `t.abaterTaxa(taxa)`.
     - Dispara a ação de auditoria final via `auditoriaFinal.accept(t)`.

3. **Construa a Classe Executável `MotorRegrasApp`:**
   - Instancie quatro transações com valores e tipos distintos.
   - Crie um `Predicate` que aprove apenas operações com valor acima de R$ 50,00 e que não sejam do tipo `"BOLETO"`.
   - Crie uma `Function` que calcule uma taxa de 2% para operações de `"CREDITO"` e taxa fixa de R$ 1,00 para `"DEBITO"`.
   - Crie um `Consumer` que imprima no console: `"[TARIFADA] TX #" + t.getId() + " - Valor Final Líquido: R$ " + t.getValor()`.
   - Execute o serviço com os comportamentos criados e comprove a aprovação e tarifação correta de cada registro.