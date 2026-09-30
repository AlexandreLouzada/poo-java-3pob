# Tutorial de Java — Aula 05: Encapsulamento, Modificadores de Acesso, Escopo de Variáveis e Membros Estáticos (static)

| | |
|---|---|
| **Disciplina** | Programação Orientada a Objetos |
| **Curso** | Análise e Desenvolvimento de Sistemas (3º Período) |
| **Tema** | Encapsulamento, Modificadores de Acesso, Escopo de Variáveis e Membros Estáticos (static) |
| **Carga Horária** | 100 minutos (2 horas-aula) |
| **Professor Responsável** | Alexandre Neves Louzada |
| **Arquivo de Referência** | `TutorialAula5.md` |

## 1. Objetivos de Aprendizagem

- **Conceitual:** Compreender o princípio do Encapsulamento (Information Hiding) como mecanismo indispensável de proteção de estado, validação de invariantes e redução de acoplamento em sistemas corporativos; entender a diferença semântica e física entre estado de instância (alocado no Heap) e estado de classe (alocado no Metaspace).
- **Técnico:** Dominar a matriz de visibilidade da plataforma Java (`private`, `default`/`package-private`, `protected` e `public`); implementar métodos acessores (*getters*) e modificadores (*setters*) defensivos com validações de negócio; utilizar variáveis e métodos estáticos (`static`), constantes globais (`static final`) e blocos estáticos de inicialização.
- **Arquitetural:** Diferenciar com precisão os três escopos de variáveis na JVM (instância, local/parâmetro e classe/estática), mapeando seus ciclos de vida, momentos de liberação e localização física na memória.
- **Prático:** Refatorar modelos de domínio abertos para classes completamente encapsuladas e implementar uma rotina centralizada de auditoria e contagem de operações bancárias com membros estáticos.

## 2. Fundamentação Teórica

### O Princípio do Encapsulamento (Information Hiding)

Nas aulas anteriores, modelamos classes e instanciamos objetos no Heap, contudo os campos internos frequentemente ficavam expostos. Em sistemas corporativos reais, a manipulação direta de variáveis de negócio por códigos externos representa uma fragilidade de segurança e manutenção:

- Se os atributos de uma entidade puderem ser modificados livremente sem barreira de proteção, valores absurdos ou fraudulentos podem ser inseridos (por exemplo: `funcionario.salarioBase = -999.0;`).
- Caso a representação interna de um dado mude, todas as classes que acessavam aquele atributo diretamente sofrerão quebras de compilação.

O Encapsulamento estabelece que o estado interno de um objeto deve ser rigorosamente ocultado do mundo exterior. O acesso e as mutações ocorrem estritamente por meio de uma interface pública (métodos controlados), tornando o próprio objeto responsável pela integridade de suas regras de negócio.

### A Matriz de Modificadores de Acesso da JVM

A linguagem Java define quatro níveis de visibilidade controlados por palavras-chave:

| Modificador | Própria Classe | Mesmo Pacote | Subclasses (Outro Pacote) | Qualquer Lugar (World) |
|---|---|---|---|---|
| `private` | Sim | Não | Não | Não |
| `default` / package | Sim | Sim | Não | Não |
| `protected` | Sim | Sim | Sim | Não |
| `public` | Sim | Sim | Sim | Sim |

- **Princípio do Menor Privilégio (*Least Privilege Principle*):** Todo membro deve ser declarado tão restrito quanto possível.
- **Regra Prática de POO:** Atributos de classe devem ser declarados como `private`. Métodos públicos formam a interface de comunicação externa permitida com o objeto.
- **Visibilidade Padrão (*package-private*):** Ocorre na omissão de modificador explícito. Permite acesso irrestrito para qualquer classe pertencente ao mesmo pacote físico, o que pode causar acoplamentos indesejados.

### Métodos Acessores (Getters) e Modificadores (Setters) Responsáveis

Criar *getters* e *setters* automáticos para todos os atributos sem nenhum critério equivale a deixar os atributos públicos. Métodos modificadores corporativos devem conter regras de guarda para blindar as regras de domínio:

```java
public class Funcionario {
    private double salarioBase; // Blindado contra mutação indevida externa

    public double getSalarioBase() {
        return this.salarioBase;
    }

    public void setSalarioBase(double novoSalario) {
        // Regra de guarda corporativa: Piso Nacional legal
        if (novoSalario >= 1412.00) {
            this.salarioBase = novoSalario;
        } else {
            System.err.println("Erro: Salário inferior ao piso legal!");
        }
    }
}
```

### Membros de Instância vs. Membros Estáticos (static)

A palavra-chave `static` altera fundamentalmente o ciclo de vida e a localização em memória de atributos e métodos:

```plaintext
┌─────────────────────────────────────────────────────────────────────────────┐
│                      ORGANIZAÇÃO FÍSICA NA JVM                              │
├──────────────────────────────────────┬──────────────────────────────────────┤
│ Metaspace (Memória de Metadados)     │ Heap (Memória Dinâmica de Objetos)   │
├──────────────────────────────────────┼──────────────────────────────────────┤
│ Classe ContaBancaria:                │ Instância 1: [saldo: 500.0]          │
│  - totalContasCriadas: 2 (ÚNICO)     │ Instância 2: [saldo: 1200.0]         │
│  - taxaBacen: 0.05 (COMPARTILHADO)   │ (Cada objeto tem sua cópia isolada)  │
└──────────────────────────────────────┴──────────────────────────────────────┘
```

- **Membros de Instância:** Pertencem ao objeto alocado no Heap via operador `new`. Cada nova instância mantém sua própria cópia independente desses campos.
- **Membros de Classe (`static`):** Pertencem à classe como um todo e residem no Metaspace. Existe apenas uma única ocorrência compartilhada por todas as instâncias existentes. Podem ser acessados diretamente pelo nome da classe (ex.: `ContaBancaria.getTotalContasCriadas()`), sem necessidade de instanciar nenhum objeto.
- **Constantes Globais (`static final`):** Valores imutáveis compartilhados em toda a aplicação (por convenção, escritos em caixa alta com separadores sublinhados, ex.: `public static final double PISO_SALARIAL = 1412.00;`).
- **Métodos Estáticos:** Operam exclusivamente sobre parâmetros recebidos ou membros estáticos. Métodos estáticos não possuem acesso à palavra-chave `this`, pois não estão vinculados a nenhuma instância específica no Heap.

### Os Três Escopos de Variáveis na JVM

Compreender o escopo e o tempo de vida das variáveis é essencial para evitar vazamentos de memória e erros de concorrência:

- **Variáveis de Instância (Campos/Atributos):** Declaradas no corpo da classe fora de qualquer método. Nascem no Heap quando o objeto é instanciado e morrem quando o objeto é recolhido pelo Garbage Collector.
- **Variáveis Locais e Parâmetros:** Declaradas dentro de blocos de métodos ou construtores. Nascem no instante em que o *frame* do método é empilhado na Stack e são destruídas imediatamente assim que o método finaliza.
- **Variáveis de Classe (Estáticas):** Declaradas com a palavra-chave `static`. Nascem no Metaspace no momento em que a classe é carregada pela JVM e permanecem vivas durante toda a execução da aplicação.

## 3. Estudo de Caso Integrado: Sistema de Auditoria Bancária

O exemplo abaixo reúne encapsulamento estrito, regras de guarda nos modificadores e controle centralizado de auditoria via membros estáticos:

```java
package br.edu.universidade.sistema.banco;

public class ContaBancaria {
    // 1. Membros Estáticos (Compartilhados por todas as contas no Metaspace)
    private static int totalContas = 0;
    private static double volumeTotalCustodiado = 0.0;
    public static final double TAXA_MANUTENCAO_PADRAO = 15.00;

    // 2. Membros de Instância Encapsulados (Isolados no Heap)
    private final String numeroConta;
    private String titular;
    private double saldo;

    // 3. Construtor com Auditoria Global
    public ContaBancaria(String numeroConta, String titular, double saldoInicial) {
        this.numeroConta = numeroConta;
        this.setTitular(titular);

        if (saldoInicial >= 0.0) {
            this.saldo = saldoInicial;
        } else {
            this.saldo = 0.0;
        }

        // Atualização dos acumuladores compartilhados de classe
        totalContas++;
        volumeTotalCustodiado += this.saldo;
    }

    // 4. Getters e Setters Defensivos
    public String getNumeroConta() {
        return this.numeroConta;
    }

    public String getTitular() {
        return this.titular;
    }

    public void setTitular(String novoTitular) {
        if (novoTitular != null && novoTitular.trim().length() >= 3) {
            this.titular = novoTitular.trim();
        } else {
            System.err.println("Erro: Nome de titular inválido!");
        }
    }

    public double getSaldo() {
        return this.saldo;
    }

    // 5. Métodos de Negócio de Instância
    public void depositar(double valor) {
        if (valor > 0.0) {
            this.saldo += valor;
            volumeTotalCustodiado += valor; // Atualiza a custódia geral
        }
    }

    public boolean sacar(double valor) {
        if (valor > 0.0 && this.saldo >= valor) {
            this.saldo -= valor;
            volumeTotalCustodiado -= valor;
            return true;
        }
        return false;
    }

    // 6. Métodos Estáticos (Operam sobre o estado da classe)
    public static int getTotalContas() {
        return totalContas;
    }

    public static double getVolumeTotalCustodiado() {
        return volumeTotalCustodiado;
    }
}
```

## 4. Diagnóstico de Erros Comuns e Armadilhas de Escopo

### Armadilha 1: Modificar Atributos Estáticos Através de Referências de Instância

**Código Problemático:**

```java
ContaBancaria c1 = new ContaBancaria("101", "Lucas", 100.0);
c1.totalContas = 50; // Alerta de má prática pelo compilador!
```

- **Diagnóstico Técnico:** Membros estáticos pertencem à classe e não ao objeto individual. Acessá-los via referência de instância (`c1.totalContas`) gera código confuso que sugere falsamente que cada conta tem seu próprio total.
- **Resolução:** Acesse membros estáticos sempre através do nome da própria classe: `ContaBancaria.totalContas`.

### Armadilha 2: Tentar Acessar `this` ou Membros de Instância Dentro de Métodos Estáticos

**Código Problemático:**

```java
public static void exibirDados() {
    System.out.println("Saldo da conta: " + this.saldo); // ERRO DE COMPILAÇÃO!
}
```

- **Mensagem da JVM:** `non-static variable saldo cannot be referenced from a static context` ou `cannot use this in a static context`.
- **Diagnóstico Técnico:** Métodos estáticos existem no Metaspace e são executados sem vínculo com nenhuma instância específica alocada no Heap. Como não há nenhum objeto apontado, o ponteiro `this` e os atributos de instância não existem naquele contexto.

### Armadilha 3: Ocultação Inadvertida de Atributos (Variable Shadowing)

**Código Problemático:**

```java
public class Produto {
    private double preco;

    public void atualizarPreco(double preco) {
        preco = preco; // Ambiguidade: o parâmetro sobrescreve o atributo localmente!
    }
}
```

- **Impacto:** A atribuição é feita entre o parâmetro local e ele mesmo na Stack. O campo de instância `preco` no Heap permanece intacto ou com valor zero, gerando falhas lógicas silenciosas.
- **Resolução:** Use a palavra-chave `this` para desambiguar explicitamente o escopo do objeto: `this.preco = preco;`.

## 5. Roteiro Prático de Depuração: Inspecionando o Metaspace e o Heap

Para visualizar a segregação física entre memória estática e dinâmica na sua IDE (IntelliJ IDEA, Eclipse ou VS Code):

1. Construa uma classe principal `AppAuditoria` e instancie três contas bancárias distintas.
2. Posicione um ponto de interrupção (*breakpoint*) imediatamente após a terceira instanciação.
3. Execute o programa em modo de depuração (*Debug*).
4. Abra a janela de variáveis (*Variables / Watches*):
   - Expanda a referência `c1`: observe os atributos de instância alocados isoladamente no Heap (`numeroConta`, `titular`, `saldo`).
   - Observe a seção de variáveis estáticas da classe `ContaBancaria`: note que `totalContas` possui valor `3` e é exibido em uma região compartilhada de classe (Metaspace), independente dos nós individuais de `c1`, `c2` e `c3`.
5. Execute uma linha contendo um depósito em `c1`: comprove que o atributo `c1.saldo` é atualizado no Heap enquanto a variável estática `volumeTotalCustodiado` é atualizada simultaneamente na área estática compartilhada.

## 6. Exercício de Fixação Prática: Módulo de Gestão de Concessionária com Auditoria Central

Refatore e expanda a modelagem de veículos para incorporar encapsulamento rígido e controle de auditoria de frota:

1. **Construa a Classe `Veiculo` no Pacote `br.edu.universidade.sistema.frota`:**
   - Atributos de classe estáticos privados: `totalVeiculosCadastrados` (`int`) e `patrimonioTotalFrota` (`double`).
   - Constante pública de classe: `public static final double TETO_DESCONTO_PERMITIDO = 0.20;` (limite máximo de 20% de desconto).
   - Atributos de instância encapsulados (`private`): `chassi` (`String`), `modelo` (`String`) e `valorComercial` (`double`).
   - Construtor parametrizado: Inicializa o veículo com validação defensiva (o valor comercial não pode ser negativo; se for informado valor menor que zero, atribuir `0.0`). Atualiza automaticamente o contador estático e a somatória do patrimônio global no Metaspace.

2. **Métodos de Acesso e Negócio:**
   - Crie *getters* para todos os atributos de instância.
   - Crie o método modificador defensivo `setValorComercial(double novoValor)`: só permite alteração caso o novo valor seja estritamente maior que zero, reajustando proporcionalmente a diferença na variável estática `patrimonioTotalFrota`.
   - Método de negócio `boolean concederDesconto(double percentual)`: valida se o percentual solicitado está entre `0.0` e o limite constante `TETO_DESCONTO_PERMITIDO`. Se for válido, reduz o valor comercial e atualiza o patrimônio global, retornando `true`; caso contrário, rejeita a operação e retorna `false`.
   - Métodos estáticos `public static int getTotalVeiculosCadastrados()` e `public static double getPatrimonioTotalFrota()` para consulta das métricas globais.

3. **Construa a Classe Executável `FrotaApp`:**
   - Instancie três veículos com valores comerciais distintos.
   - Demonstre a consulta dos dados estáticos de auditoria chamando os métodos diretamente pela classe `Veiculo`.
   - Aplique descontos válidos em um veículo e tente aplicar um desconto abusivo de 30% em outro, comprovando que o encapsulamento e as regras de guarda rejeitam a transação e mantêm o patrimônio global íntegro e auditável.