# Explicação da Aula 04 — Abstração, Classes, Atributos, Métodos, Instanciação e Construtores

| | |
|---|---|
| **Disciplina** | Programação Orientada a Objetos |
| **Curso** | Análise e Desenvolvimento de Sistemas (3º Período) |
| **Tema** | Abstração, Classes, Atributos, Métodos, Instanciação e Construtores |
| **Carga Horária** | 100 minutos (2 horas-aula) |
| **Professor** | Alexandre Neves Louzada |
| **Plano de Aula** | `Aulas 3pob/Aula 04.md` |
| **Tutorial** | `aulas/TutorialAula04.md` |
| **Estudo de Caso** | `exemplos/aula-04/` |
| **Exercícios Resolvidos** | `solucoes/aula-04/` |

## 1. Objetivos de Aprendizagem

- **Conceitual:** Compreender o princípio da abstração no projeto orientado a objetos; diferenciar a classe (modelo/especificação) de um objeto (entidade concreta instanciada em memória); entender a semântica de estado e comportamento.
- **Arquitetural:** Mapear o ciclo de vida dos objetos em memória com o operador `new`, diferenciando o armazenamento de variáveis de referência na Stack dos dados dinâmicos do objeto no Heap; compreender os efeitos de cópia de referências (*aliasing*).
- **Técnico:** Projetar classes com atributos de instância e métodos com regras de guarda; dominar a criação de construtores padrão e sobrecarregados; utilizar a palavra-chave `this` para desambiguação de escopo e encadeamento de construtores (`this(...)`).
- **Prático:** Implementar uma modelagem orientada a domínio para gestão de veículos de concessionária, validando invariantes no momento da instanciação.

## 2. Conteúdo Teórico Detalhado

### 2.1 Transição de Modelo Mental — O Núcleo da POO

Esta aula marca a abertura do Módulo 2 da disciplina: o Núcleo da Orientação a Objetos. Nas aulas anteriores (Aulas 01 a 03), utilizamos métodos utilitários estáticos e classes sem instanciação. Agora abandonamos o uso indiscriminado de `static` para focar em instâncias dinâmicas — objetos que vivem no Heap e possuem estado próprio.

Para o estudante de ADS, cada classe modelada deve refletir uma entidade do modelo de negócio: `Veiculo`, `Cliente`, `Conta`, `ItemPedido`. A transição é de uma visão procedural ("o que o sistema faz") para uma visão orientada a objetos ("quem é responsável pelo quê").

### 2.2 O Princípio da Abstração no Projeto de Software

**Definição:** Abstração é a capacidade de selecionar apenas as propriedades e ações **relevantes** de um elemento real para o escopo do software em desenvolvimento. A abstração é contextual — não existe uma modelagem "universal e perfeita" para qualquer entidade.

**Estudo de caso — A Entidade "Pessoa" em Diferentes Domínios:**

| Domínio | Atributos Relevantes |
|:---|:---|
| Detran (trânsito) | CNH, categoria, pontuação na carteira, vencimento do exame |
| Hospital (saúde) | Tipo sanguíneo, histórico cirúrgico, prontuário, alergias |
| Universidade (educação) | Matrícula, curso, coeficiente de rendimento (CR) |

A pergunta fundamental é: *"Existe uma modelagem universal e perfeita para uma classe `Carro`?"*. A resposta é não. Uma locadora precisa de placa e quilometragem; um jogo de corrida precisa de torque, aerodinâmica e atrito dos pneus. A abstração depende do **contexto do negócio**.

### 2.3 Anatomia de uma Classe em Java

Uma classe em Java é a especificação que define o estado (atributos) e o comportamento (métodos) de um tipo de objeto. A estrutura é:

```java
public class ContaBancaria {
    // 1. Atributos (Estado do Objeto)
    String numero;
    String titular;
    double saldo;

    // 2. Métodos (Comportamento e Regras de Guarda)
    void depositar(double valor) {
        if (valor > 0) {
            saldo += valor;
        }
    }

    boolean sacar(double valor) {
        if (valor > 0 && saldo >= valor) {
            saldo -= valor;
            return true;
        }
        return false;
    }
}
```

**Componentes fundamentais:**
- **Atributos de instância:** Variáveis que definem o estado de cada objeto. Cada instância possui sua própria cópia dessas variáveis no Heap.
- **Métodos:** Funções que definem o comportamento da classe. Métodos com regras de guarda (validações internas) protegem a integridade do estado.
- **Construtor:** Método especial chamado no momento da instanciação (com `new`), responsável pela inicialização do estado do objeto.

### 2.4 A Construção do Objeto: `new` e Alocação em Memória

Quando o operador `new` é utilizado, três etapas ocorrem:
1. **Alocação no Heap:** O JVM reserva espaço na memória dinâmica para os atributos do objeto.
2. **Inicialização:** O construtor é invocado, atribuindo valores iniciais aos atributos.
3. **Retorno da Referência:** Um ponteiro (referência) para o objeto recém-criado é retornado e armazenado em uma variável na Stack.

```
Stack                          Heap
+----------------+        +----------------+
| referencia --> | ------>| saldo: 100.0   |
+----------------+        | titular: "Joao" |
                          | numero: "001"   |
                          +----------------+
```

**Aliasing (Cópia de Referências):** Quando duas variáveis de referência apontam para o mesmo objeto no Heap, modificações feitas por uma referência são visíveis pela outra. Isso pode ser intencional (compartilhamento de estado) ou um bug sutil (efeitos colaterais inesperados).

### 2.5 Construtores: Padrão, Sobrecarga e Encadeamento

**Construtor:** É um método com o mesmo nome da classe, sem tipo de retorno (nem `void`). É chamado automaticamente quando o operador `new` é utilizado.

**Construtor Padrão (Default):** Se nenhuma declaração de construtor existir, o Java fornece um construtor sem parâmetros. Se **qualquer** construtor for declarado, o padrão deixa de ser fornecido automaticamente.

**Sobrecarga de Construtores:** Uma classe pode ter múltiplos construtores com diferentes assinaturas (diferente número ou tipos de parâmetros). Isso permite criar objetos com diferentes níveis de inicialização.

**Encadeamento de Construtores com `this(...)`:** Um construtor pode delegar a inicialização para outro construtor da mesma classe usando `this(...)`. Isso elimina duplicação de código e garante uma única fonte de verdade para a lógica de inicialização.

Exemplo prático da Aula 04 (concessionária):

```java
public class ContaBancaria {
    String numero;
    String titular;
    double saldo;

    // Construtor simplificado: delega para o completo
    public ContaBancaria(String numero, String titular) {
        this(numero, titular, 0.0); // Delega para o construtor completo
    }

    // Construtor completo: lógica de inicialização centralizada
    public ContaBancaria(String numero, String titular, double saldoInicial) {
        this.numero = numero;
        this.titular = titular;
        // Aplicação de regra de guarda na inicialização
        if (saldoInicial >= 0.0) {
            this.saldo = saldoInicial;
        } else {
            this.saldo = 0.0;
        }
    }
}
```

**A palavra-chave `this`:** Serve para dois propósitos:
1. **Desambiguação de escopo:** Quando o parâmetro tem o mesmo nome que o atributo (ex.: `this.numero = numero`).
2. **Encadeamento de construtores:** `this(...)` chama outro construtor da mesma classe como primeira instrução.

### 2.6 Métodos com Regras de Guarda

Métodos de negócio devem proteger a integridade do estado do objeto através de **regras de guarda** — condições que devem ser satisfeitas para que a operação seja executada:

```java
public void depositar(double valor) {
    if (valor > 0) {           // Regra de guarda: valor deve ser positivo
        this.saldo += valor;
    }
}

public boolean sacar(double valor) {
    if (valor > 0 && this.saldo >= valor) {  // Regra de guarda composta
        this.saldo -= valor;
        return true;   // Operação bem-sucedida
    }
    return false;      // Operação rejeitada
}
```

O método `sacar()` retorna `boolean` para indicar sucesso ou falha, permitindo que o chamador tome decisões com base no resultado. O método `depositar()` é `void` porque a validação é interna e silenciosa.

### 2.7 Exibição de Estado com `printf`

O método `exibirExtrato()` demonstra formatação profissional de saída:

```java
public void exibirExtrato() {
    System.out.printf("Conta: %-8s | Titular: %-15s | Saldo: R$ %8.2f%n",
            this.numero, this.titular, this.saldo);
}
```

Os especificadores `%-8s` e `%-15s` alinham o texto à esquerda em campos de largura fixa, criando uma tabela visualmente alinhada. O `%8.2f` exibe o saldo com 8 caracteres de largura total e 2 casas decimais.

## 3. Estudo de Caso Aplicado

O estudo de caso da Aula 04 está em `exemplos/aula-04/src/br/edu/universidade/dominio/` e contém a classe:

- **`ContaBancaria.java`** — Classe no pacote `br.edu.universidade.dominio` que modela uma conta bancária com dois construtores sobrecarregados (simplificado e completo), atributos de instância (`numero`, `titular`, `saldo`), métodos de negócio (`depositar()`, `sacar()`) com regras de guarda, e método de exibição (`exibirExtrato()`). O construtor completo aplica validação no saldo inicial (não permite saldo negativo). O construtor simplificado delega para o completo via `this(...)`, usando saldo zero como padrão.

O problema de negócio apresentado é o de inconsistência cadastral em sistemas de concessionárias: sem construtores com validação, seria possível criar veículos com dados inválidos (preço negativo, ano impossível, etc.). A modelagem com construtores protetivos elimina esse risco.

## 4. Exercícios Propostos e Solução

A solução está em `solucoes/aula-04/src/br/edu/universidade/concessionaria/` e contém duas classes:

- **`Veiculo.java`** — Classe que modela um veículo de concessionária com atributos privados (`chassi`, `marca`, `modelo`, `anoFabricacao`, `precoBase`). Dois construtores sobrecarregados:
  - Completo: recebe todos os parâmetros e aplica validações (ano de fabricação deve ser posterior a 1886 — ano da invenção do automóvel; preço base não pode ser negativo).
  - Simplificado: recebe apenas chassi, marca e modelo, delegando para o completo com ano atual (`Year.now().getValue()`) e preço zero.

  Métodos:
  - `aplicarDesconto(double percentual)`: Aplica desconto com regra de guarda (percentual deve ser > 0 e <= 0.25, ou seja, máximo 25%).
  - `exibirFichaTecnica()`: Exibe dados formatados com `printf`.
  - Getters públicos para consulta segura dos atributos.

- **`ConcessionariaApp.java`** — Classe executável que demonstra a criação de veículos com diferentes construtores, aplicação de descontos válidos e rejeição de descontos abusivos (40%, -5%, 0%), verificando a cada passo que a integridade dos dados é preservada pelas regras de guarda.

## 5. Perguntas de Revisão

1. **Qual a diferença entre classe e objeto?**
   - Classe é o modelo/蓝图 (especificação) que define atributos e métodos. Objeto é a instância concreta criada a partir da classe, com estado próprio alocado no Heap.

2. **O que acontece na memória quando executamos `ContaBancaria c = new ContaBancaria("001", "João", 500.0);`?**
   - Uma referência `c` é criada na Stack apontando para um bloco no Heap que contém os atributos `numero = "001"`, `titular = "João"` e `saldo = 500.0`.

3. **Para que serve o encadeamento de construtores com `this(...)`?**
   - Para delegar a inicialização de um construtor mais simples para um construtor mais completo, eliminando duplicação de código de inicialização e centralizando a lógica de validação.

4. **O que é aliasing e quais riscos ele apresenta?**
   - Aliasing ocorre quando duas variáveis de referência apontam para o mesmo objeto no Heap. Modificações feitas por uma referência são visíveis pela outra, o que pode causar efeitos colaterais inesperados se não for gerenciado adequadamente.

## 6. Resumo / Pontos-Chave

- Abstração é contextual: os atributos relevantes de uma entidade dependem do domínio do negócio.
- Classe é a especificação (modelo); objeto é a instância concreta (entidade em memória).
- O operador `new` aloca o objeto no Heap e retorna uma referência armazenada na Stack.
- Construtores inicializam o estado do objeto e podem ser sobrecarregados para oferecer diferentes formas de criação.
- `this(...)` permite encadeamento de construtores, evitando duplicação de código.
- Métodos de negócio devem implementar regras de guarda que protegem a invariante do objeto.
- A modelagem orientada a domínio cada classe deve refletir uma entidade real do sistema corporativo.
- Referências compartilhadas (aliasing) exigem atenção para evitar efeitos colaterais.
