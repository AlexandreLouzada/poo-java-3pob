# Explicação da Aula 05 — Encapsulamento, Modificadores de Acesso, Escopo de Variáveis e Membros Estáticos (`static`)

| | |
|---|---|
| **Disciplina** | Programação Orientada a Objetos |
| **Curso** | Análise e Desenvolvimento de Sistemas (3º Período) |
| **Tema** | Encapsulamento, Modificadores de Acesso, Escopo de Variáveis e Membros Estáticos (`static`) |
| **Carga Horária** | 100 minutos (2 horas-aula) |
| **Professor** | Alexandre Neves Louzada |
| **Plano de Aula** | `Aulas 3pob/Aula 05.md` |
| **Tutorial** | `aulas/TutorialAula05.md` |
| **Estudo de Caso** | `exemplos/aula-05/` |
| **Exercícios Resolvidos** | `solucoes/aula-05/` |

## 1. Objetivos de Aprendizagem

- **Conceitual:** Compreender o princípio do Encapsulamento (*Information Hiding*) como mecanismo de proteção de estado e redução de acoplamento; entender a diferença semântica entre estado de instância (Heap) e estado de classe (Metaspace).
- **Técnico:** Dominar a matriz de visibilidade do Java (`private`, *default/package-private*, `protected`, `public`); implementar métodos acessores (*getters*) e modificadores (*setters*) com regras de negócio; manipular atributos e métodos estáticos (`static`), constantes globais (`static final`) e blocos estáticos de inicialização.
- **Arquitetural:** Diferenciar os três escopos de variáveis na JVM (instância, local/parâmetro e classe/estática), mapeando seu tempo de vida e localização física na memória.
- **Prático:** Refatorar modelos de dados abertos para entidades encapsuladas e implementar controle de auditoria centralizado com membros estáticos em um sistema bancário.

## 2. Conteúdo Teórico Detalhado

### 2.1 A Fragilidade do Estado Exposto

Na Aula 04, aprendemos a criar classes e instanciar objetos no Heap, mas nossos atributos ainda estavam abertos — qualquer código externo podia escrever diretamente `veiculo.precoBase = -999.0;`. Em sistemas corporativos, **nenhum atributo de entidade deve ser público**.

O encapsulamento é o mecanismo que garante que o objeto seja o único responsável pela integridade de suas regras de negócio. Sem ele, valores inconsistentes podem ser injetados externamente, quebrando invariantes e causando comportamentos imprevisíveis em todo o sistema.

O princípio da **Ocultação de Informação (Information Hiding)** estabelece que os detalhes internos de implementação de uma classe devem ser escondidos dos consumidores externos. O que é exposto é apenas a interface pública — os métodos — que são os únicos caminhos legítimos para interagir com o estado do objeto.

### 2.2 A Matriz de Modificadores de Acesso

Java oferece quatro modificadores de visibilidade que controlam o acesso a atributos, métodos e construtores:

| Modificador | Própria Classe | Mesmo Pacote | Subclasses (Outro Pacote) | Qualquer Lugar (*World*) |
| :--- | :---: | :---: | :---: | :---: |
| `private` | **Sim** | Não | Não | Não |
| *(default / package)* | **Sim** | **Sim** | Não | Não |
| `protected` | **Sim** | **Sim** | **Sim** | Não |
| `public` | **Sim** | **Sim** | **Sim** | **Sim** |

**Detalhamento de cada modificador:**

- **`private`:** Acesso restrito à própria classe. É o modificador mais restritivo. Deve ser o padrão para atributos de instância.
- **Default (package-private):** Quando nenhum modificador é especificado, o acesso é restrito às classes do mesmo pacote. Pode causar vazamento inadvertido de dados em pacotes com muitas classes.
- **`protected`:** Acesso para classes do mesmo pacote e para subclasses em outros pacotes. É especialmente relevante em projetos com herança e pacotes separados.
- **`public`:** Acesso irrestrito. Deve ser usado apenas na interface pública da classe (métodos de negócio e, quando necessário, constantes).

**Princípio do Menor Privilegio (Least Privilege Principle):** Torne cada membro tão restrito quanto possível. Comece com `private` e amplie o acesso apenas quando necessário.

**Regra de ouro da POO em Java:** Atributos são `private`; métodos públicos formam a interface de comunicação da classe.

### 2.3 Getters e Setters com Responsabilidade

Métodos acessores (getters) e modificadores (setters) são a ponte entre o estado privado e o mundo externo. Não são métodos triviais — devem implementar regras de negócio:

```java
public class Funcionario {
    private double salarioBase; // Blindado contra alteração externa direta

    public double getSalarioBase() {
        return this.salarioBase;
    }

    public void setSalarioBase(double novoSalario) {
        if (novoSalario >= 1412.00) { // Regra de negócio (Piso Nacional)
            this.salarioBase = novoSalario;
        } else {
            System.err.println("Erro: Salário inferior ao piso legal!");
        }
    }
}
```

Elementos importantes:
- O atributo `salarioBase` é `private`, inacessível diretamente de fora da classe.
- O getter `getSalarioBase()` retorna o valor sem permitir modificação — é somente leitura.
- O setter `setSalarioBase()` valida a regra de negócio (salário >= piso legal de R$ 1.412,00) antes de permitir a alteração. Se a regra for violada, o valor não é alterado e uma mensagem de erro é emitida.
- Setters que não validam nada são antipadrões: se o valor pode ser definido livremente, o atributo poderia ser público. O setter existe justamente para validar.

**Padrões de getters/setters:**
- `getAtributo()` / `setAtributo(valor)` — padrão para atributos não booleanos.
- `isAtributo()` / `setAtributo(valor)` — padrão para atributos booleanos (ex.: `isAtivo()`, `isDisponivel()`).
- Algumas propriedades são somente leitura: apenas getter, sem setter (ex.: `getNumeroConta()`, `getChassi()`).

### 2.4 Membros Estáticos (`static`) — Estado de Classe

Membros estáticos são compartilhados por **todas** as instâncias da classe. Enquanto atributos de instância vivem no Heap (um para cada objeto), atributos estáticos vivem no **Metaspace** (antes Java 8: *PermGen*) — uma única cópia compartilhada.

**Atributos estáticos:**
- Declarados com a palavra-chave `static`.
- Exemplo clássico: um contador de instâncias criadas. Cada vez que `new` é chamado, o contador estático é incrementado.
- Podem ser acessados diretamente pela classe: `ContaBancaria.getTotalContas()` (sem precisar criar uma instância).

**Constantes globais (`static final`):**
- Atributos estáticos e finais são imutáveis e compartilhados por todas as instâncias.
- Convenção de nomenclatura: `CAIXA_ALTA_COM_SUBTRACO` (ex.: `TAXA_MANUTENCAO_PADRAO`, `TETO_DESCONTO_PERMITIDO`).
- Podem ser acessados diretamente pela classe.

**Métodos estáticos:**
- Operam sobre o estado da classe (estáticos), não sobre o estado de instância.
- Não podem acessar atributos de instância nem chamar métodos de instância diretamente (pois não possuem `this`).
- Exemplos: `getTotalContas()`, `getVolumeTotalCustodiado()`.

**Blocos estáticos de inicialização:**
- Bloco de código que é executado uma única vez quando a classe é carregada peloClassLoader, antes de qualquer instanciação.
- Útil para inicialização complexa de constantes estáticas ou configuração de recursos compartilhados.

### 2.5 Os Três Escopos de Variáveis na JVM

| Escopo | Localização na Memória | Tempo de Vida | Exemplo |
|:---|:---|:---|:---|
| **Instância** | Heap (junto com o objeto) | Durante a vida do objeto | `private double saldo;` |
| **Local / Parâmetro** | Stack (frame do método) | Durante a execução do método | `double valor` (parâmetro) |
| **Classe / Estática** | Metaspace (única cópia) | Enquanto a classe estiver carregada | `private static int totalContas;` |

- **Variáveis de instância:** Uma para cada objeto, criadas pelo construtor, armazenadas no Heap junto com os outros dados do objeto. Sobrevivem enquanto o objeto existir.
- **Variáveis locais e parâmetros:** Criadas no frame do método na Stack quando o método é chamado, destruídas quando o método retorna. São temporárias.
- **Variáveis de classe (estáticas):** Uma única cópia compartilhada por todas as instâncias, armazenada no Metaspace. Sobrevivem enquanto a classe estiver carregada na JVM.

### 2.6 Estudo de Caso — ContaBancaria com Membros Estáticos

A classe `ContaBancaria` da aula demonstra a combinação de todos os conceitos:

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

        totalContas++;
        volumeTotalCustodiado += this.saldo;
    }

    // 4. Getters e Setters Defensivos
    public String getNumeroConta() { return this.numeroConta; }

    public String getTitular() { return this.titular; }

    public void setTitular(String novoTitular) {
        if (novoTitular != null && novoTitular.trim().length() >= 3) {
            this.titular = novoTitular.trim();
        } else {
            System.err.println("Erro: Nome de titular inválido!");
        }
    }

    public double getSaldo() { return this.saldo; }

    // 5. Métodos de Negócio de Instância
    public void depositar(double valor) {
        if (valor > 0.0) {
            this.saldo += valor;
            volumeTotalCustodiado += valor;
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
    public static int getTotalContas() { return totalContas; }
    public static double getVolumeTotalCustodiado() { return volumeTotalCustodiado; }
}
```

Elementos-chave:
- **`totalContas` (static):** Incrementado no construtor — conta automaticamente quantas instâncias foram criadas.
- **`volumeTotalCustodiado` (static):** Acumula o saldo de todas as contas, atualizado a cada `depositar()` ou `sacar()`.
- **`TAXA_MANUTENCAO_PADRAO` (static final):** Constante global acessível via `ContaBancaria.TAXA_MANUTENCAO_PADRAO`.
- **`numeroConta` (final):** Atributo de instância imutável — uma vez definido no construtor, não pode ser alterado.
- **`setTitular()` com validação:** Rejeita nomes nulos ou com menos de 3 caracteres.

## 3. Estudo de Caso Aplicado

O estudo de caso da Aula 05 está em `exemplos/aula-05/src/br/edu/universidade/sistema/banco/` e contém a classe:

- **`ContaBancaria.java`** — Classe que implementa todos os conceitos da aula em um modelo bancário completo. Demonstrada acima na íntegra. A classe combina: atributos de instância encapsulados (`private`), membros estáticos de auditoria (`totalContas`, `volumeTotalCustodiado`), constante global (`TAXA_MANUTENCAO_PADRAO`), construtor com inicialização de acumuladores estáticos, getters/setters com validação de regras de negócio (nome do titular com mínimo de 3 caracteres), métodos de negócio que atualizam tanto o estado de instância quanto o acumulador estático, e método `final` no atributo `numeroConta` (imutabilidade).

## 4. Exercícios Propostos e Solução

A solução está em `solucoes/aula-05/src/br/edu/universidade/sistema/frota/` e contém duas classes:

- **`Veiculo.java`** — Classe que modela um veículo com atributos estáticos de auditoria e atributos de instância encapsulados:

  - **Membros estáticos:**
    - `totalVeiculosCadastrados` (private static): Contador de instâncias criadas.
    - `patrimonioTotalFrota` (private static): Acumulador do valor comercial total da frota.
    - `TETO_DESCONTO_PERMITIDO` (public static final): Constante que define o limite máximo de desconto (20%).

  - **Atributos de instância (private):** `chassi`, `modelo`, `valorComercial`.

  - **Construtor:** Inicializa os atributos, aplica validação no valor comercial (>= 0), e atualiza os acumuladores estáticos (`totalVeiculosCadastrados++` e `patrimonioTotalFrota += valor`).

  - **Métodos:**
    - `concederDesconto(double percentual)`: Retorna `boolean` — concede o desconto apenas se o percentual estiver dentro do limite estático `TETO_DESCONTO_PERMITIDO`, atualizando tanto o valor do veículo quanto o patrimônio global.
    - `setValorComercial(double novoValor)`: Setter defensivo que recalcula a diferença no acumulador estático.
    - Getters públicos: `getChassi()`, `getModelo()`, `getValorComercial()`.
    - `exibirDados()`: Formatação de saída com `printf`.
    - Métodos estáticos: `getTotalVeiculosCadastrados()` e `getPatrimonioTotalFrota()` — acessíveis diretamente pela classe, sem instanciar.

- **`FrotaApp.java`** — Classe executável que demonstra o funcionamento completo:
  1. Cria três veículos e verifica que o contador estático e o patrimônio global foram corretamente atualizados (acesso via `Veiculo.getTotalVeiculosCadastrados()` e `Veiculo.getPatrimonioTotalFrota()`).
  2. Exibe os dados individuais de cada veículo.
  3. Aplica desconto válido de 15% no Gol (dentro do teto de 20%).
  4. Tenta aplicar desconto abusivo de 30% no Pulse (rejeitado pelo método).
  5. Verifica que o patrimônio global permanece íntegro — apenas o desconto do Gol foi descontado.

## 5. Perguntas de Revisão

1. **Qual a diferença entre `private`, *default*, `protected` e `public`?**
   - `private` restringe à própria classe; *default* (sem modificador) restringe ao mesmo pacote; `protected` permite acesso em subclasses e no mesmo pacote; `public` permite acesso irrestrito.

2. **Por que atributos devem ser sempre `private`?**
   - Para garantir o encapsulamento e proteger a integridade do estado. Acesso direto permite que valores inválidos sejam injetados externamente, quebrando invariantes de negócio.

3. **Qual a diferença entre um atributo de instância e um atributo estático?**
   - Atributo de instância: uma cópia para cada objeto, armazenada no Heap. Atributo estático: uma única cópia compartilhada por todas as instâncias, armazenada no Metaspace.

4. **Por que um método estático não pode acessar atributos de instância?**
   - Porque métodos estáticos não possuem contexto de instância (`this`). Eles são chamados pela classe, não por um objeto específico, e portanto não possuem referência a nenhum bloco particular do Heap.

5. **O que é a constante `static final` e qual sua convenção de nomenclatura?**
   - É um valor imutável compartilhado por todas as instâncias. A convenção é usar caixa alta com sublinhados: `TAXA_MANUTENCAO_PADRAO`, `TETO_DESCONTO_PERMITIDO`.

## 6. Resumo / Pontos-Chave

- Encapsulamento (*Information Hiding*) protege o estado interno do objeto, restringindo acesso via modificadores de visibilidade.
- Atributos devem ser sempre `private`; a interface pública é formada por métodos (getters, setters, métodos de negócio).
- A matriz de visibilidade (`private` → `public`) define quatro níveis de acesso, do mais restritivo ao mais aberto.
- Getters e setters não são triviais — devem implementar regras de negócio e validações.
- Membros `static` (atributos, métodos, constantes) são compartilhados por todas as instâncias e vivem no Metaspace.
- Constantes globais seguem o padrão `static final` com nomenclatura `CAIXA_ALTA_SUBTRACO`.
- A JVM possui três escopos de variáveis: instância (Heap), local/parâmetro (Stack) e classe/estática (Metaspace).
- Métodos estáticos não podem acessar estado de instância, pois não possuem referência `this`.
