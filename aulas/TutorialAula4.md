# Tutorial de Java — Aula 04: Abstração, Classes, Atributos, Métodos, Instanciação e Construtores

| | |
|---|---|
| **Disciplina** | Programação Orientada a Objetos |
| **Curso** | Análise e Desenvolvimento de Sistemas (3º Período) |
| **Tema** | Abstração, Classes, Atributos, Métodos, Instanciação e Construtores |
| **Carga Horária** | 100 minutos (2 horas-aula) |
| **Professor Responsável** | Alexandre Neves Louzada |
| **Arquivo de Referência** | `TutorialAula4.md` |

## 1. Objetivos de Aprendizagem

- **Conceitual:** Compreender o princípio da abstração no projeto orientado a objetos; diferenciar a classe (especificação/modelo) de um objeto (entidade concreta alocada no Heap); assimilar o significado de estado e comportamento em entidades de software.
- **Arquitetural:** Mapear o ciclo de vida dos objetos em memória com o operador `new`, diferenciando o armazenamento de variáveis de referência na Stack dos dados do objeto no Heap, compreendendo também os efeitos do compartilhamento de ponteiros (*aliasing*).
- **Técnico:** Projetar classes com atributos de instância e métodos contendo regras de guarda; dominar a criação de construtores padrão e sobrecarregados; utilizar a palavra-chave `this` para desambiguação de escopo e encadeamento interno de construtores (`this(...)`).
- **Prático:** Implementar uma modelagem orientada a domínio para gestão de veículos de concessionária, aplicando validações de invariantes no instante da instanciação.

## 2. Fundamentação Teórica

### O Princípio da Abstração no Projeto de Software

A abstração orientada a objetos consiste na capacidade de isolar e selecionar exclusivamente as características (atributos) e ações (métodos) relevantes de um elemento do mundo real para o contexto de negócio da aplicação.

Não existe uma modelagem universal ou absoluta para uma entidade. O escopo do sistema determina o que deve ser representado:

- **Contexto de um Hospital:** A entidade `Pessoa` precisa de tipo sanguíneo, prontuário médico, histórico cirúrgico e alergias.
- **Contexto Acadêmico:** A entidade `Pessoa` é abstraída com número de matrícula, curso matriculado e coeficiente de rendimento.
- **Contexto de Trânsito/DETRAN:** A mesma entidade requer número da CNH, pontuação acumulada e categoria de habilitação.

### Classe vs. Objeto: Da Especificação à Instanciação

- **Classe:** Funciona como a planta arquitetônica, molde ou especificação estrutural. Define quais campos o objeto possuirá e quais operações ele estará apto a executar. Ela reside no disco e, quando carregada pela JVM, compõe os metadados da aplicação.
- **Objeto:** É a instância materializada em memória criada a partir do molde da classe. O operador `new` aloca dinamicamente um bloco contíguo de memória no Heap para guardar os valores específicos daquela ocorrência.

```plaintext
┌─────────────────────────────────────────┐
│           CLASSE: ContaBancaria         │ (Planta / Especificação no disco)
│  - Atributos: numero, titular, saldo    │
│  - Métodos: depositar(), sacar()        │
└────────────────────┬────────────────────┘
                     │
         Instanciação via operador 'new'
                     │
                     ▼
┌─────────────────────────────────────────┐
│     OBJETO INSTANCIADO NO HEAP          │
│  [numero: "1001-X", saldo: 1500.0]      │ (Ocorrência real em memória dinâmica)
└─────────────────────────────────────────┘
```

### Anatomia da Memória: Variáveis de Referência e o Efeito Aliasing

Quando declaramos uma variável do tipo de uma classe, a variável na Stack não armazena o objeto em si, mas sim um endereço de memória de 32 ou 64 bits (ponteiro de referência) que aponta para o endereço físico do objeto alocado no Heap:

```java
ContaBancaria c1 = new ContaBancaria("1001-X", 500.0);
ContaBancaria c2 = c1; // Cópia de referência (Aliasing)
```

No cenário acima, `c2` recebe o mesmo endereço de memória que `c1`. Ambas as variáveis na Stack apontam para a mesma instância física no Heap. Consequentemente, qualquer mutação efetuada por `c2` (ex.: `c2.sacar(100.0)`) altera o mesmo bloco compartilhado, tornando o novo saldo visível imediatamente através de `c1`.

### Construtores, Regras de Guarda e a Palavra-Chave `this`

- **Construtor Padrão:** Quando nenhuma declaração de construtor é escrita, o compilador `javac` injeta silenciosamente um construtor público sem argumentos, inicializando números com zero, booleanos com `false` e referências com `null`.
- **Construtores Parametrizados:** Garantem que um objeto nunca nasça em estado inconsistente ou inválido no sistema.
- **Desambiguação com `this`:** A palavra-chave `this` referencia a própria instância atual. É empregada para distinguir os atributos do objeto dos parâmetros homônimos passados no construtor ou método:
  ```java
  public ContaBancaria(String titular, double saldo) {
      this.titular = titular; // 'this.titular' é o campo; 'titular' é o parâmetro
      this.saldo = saldo;
  }
  ```
- **Sobrecarga de Construtores (`this(...)`):** Permite que um construtor com poucos parâmetros delegue sua execução para outro construtor mais completo da mesma classe, reduzindo a duplicação de código. A instrução `this(...)` deve ser, obrigatoriamente, a primeira linha do bloco construtor.

## 3. Estudo de Caso Integrado: Modelagem da Entidade ContaBancaria

O código abaixo exemplifica uma classe de domínio com métodos providos de regras de guarda para proteção de invariantes de negócio:

```java
package br.edu.universidade.dominio;

public class ContaBancaria {
    // 1. Atributos de Instância (Estado do Objeto)
    String numero;
    String titular;
    double saldo;

    // 2. Construtor com Sobrecarga e Encadeamento
    public ContaBancaria(String numero, String titular) {
        this(numero, titular, 0.0); // Delega para o construtor completo
    }

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

    // 3. Métodos (Comportamento e Regras de Guarda)
    public void depositar(double valor) {
        if (valor > 0) {
            this.saldo += valor;
        }
    }

    public boolean sacar(double valor) {
        if (valor > 0 && this.saldo >= valor) {
            this.saldo -= valor;
            return true;
        }
        return false;
    }

    public void exibirExtrato() {
        System.out.printf("Conta: %-8s | Titular: %-15s | Saldo: R$ %8.2f%n",
                this.numero, this.titular, this.saldo);
    }
}
```

## 4. Diagnóstico de Erros Comuns e Armadilhas de Instanciação

### Armadilha 1: Perda Silenciosa do Construtor Padrão

- **Cenário:** O desenvolvedor define um construtor parametrizado `public ContaBancaria(String titular, double saldo)` e tenta executar `ContaBancaria c = new ContaBancaria();` em outra classe.
- **Mensagem da JVM/Compilador:** `constructor ContaBancaria in class ContaBancaria cannot be applied to given types; required: String, double; found: no arguments.`
- **Causa & Correção:** Ao declarar qualquer construtor explicitamente, o Java cancela a geração do construtor sem argumentos. Se o sistema precisar da versão sem parâmetros, ela deve ser implementada explicitamente.

### Armadilha 2: Falha de Inicialização por Omissão do `this`

**Código Problemático:**

```java
public class Veiculo {
    String modelo;

    public Veiculo(String modelo) {
        modelo = modelo; // Atribui o parâmetro a ele mesmo!
    }
}
```

- **Impacto:** O atributo `this.modelo` da instância permanece `null` no Heap, gerando potenciais falhas de ponteiro nulo (`NullPointerException`) mais adiante na execução.
- **Correção:** Use a desambiguação explícita: `this.modelo = modelo;`.

### Armadilha 3: Invocação Tardia de `this(...)`

**Código Problemático:**

```java
public ContaBancaria(String numero, String titular) {
    System.out.println("Inicializando conta..."); // ERRO DE COMPILAÇÃO!
    this(numero, titular, 0.0);
}
```

- **Mensagem da JVM:** `call to this must be first statement in constructor.`
- **Causa & Correção:** Assim como na chamada de construtores de superclasses, a delegação via `this(...)` deve ser obrigatoriamente a primeira instrução dentro do bloco do construtor.

## 5. Roteiro Prático de Depuração: Inspecionando o Heap na IDE

Para visualizar a criação de instâncias dinâmicas e o comportamento de cópia de referências (*aliasing*), execute este procedimento no depurador da sua IDE:

1. Crie uma classe executável instanciando `c1` e atribuindo `ContaBancaria c2 = c1;`.
2. Posicione um ponto de interrupção (*breakpoint*) logo após a linha da atribuição.
3. Inicie o programa em modo de depuração (*Debug*).
4. Abra a aba *Variables*: observe que tanto a variável `c1` quanto a variável `c2` compartilham exatamente o mesmo identificador de instância no Heap (exemplo: `@65b` ou `id=42`).
5. Execute uma linha que altere o saldo usando `c2.sacar(...)` e observe o atributo `saldo` ser atualizado simultaneamente para a inspeção de `c1`, comprovando a existência de um único objeto compartilhado na memória física.

## 6. Exercício de Fixação Prática: Gestão de Veículos de Concessionária

Implemente uma solução orientada a domínio para catalogação de veículos em uma concessionária, respeitando os seguintes requisitos arquiteturais:

1. **Construa a Classe `Veiculo` no Pacote `br.edu.universidade.concessionaria`:**
   - Atributos de instância: `chassi` (`String`), `marca` (`String`), `modelo` (`String`), `anoFabricacao` (`int`) e `precoBase` (`double`).
   - **Construtor Completo:** Inicializa todos os atributos, aplicando regras de guarda: o ano de fabricação deve ser maior que `1886` (ano do primeiro automóvel moderno) e o preço base não pode ser negativo (se for menor que zero, atribuir `0.0`).
   - **Construtor Sobrecarregado:** Recebe apenas `chassi`, `marca` e `modelo`, utilizando `this(...)` para repassar o ano corrente e o valor base zerado para o construtor principal.
   - **Método `void aplicarDesconto(double percentual)`:** Reduz o preço base daquele veículo apenas se o percentual for válido (maior que `0.0` e menor ou igual a `0.25` — teto de 25% de desconto).
   - **Método `void exibirFichaTecnica()`:** Imprime no console a listagem alinhada de dados do veículo, formatando o preço com `%.2f`.

2. **Construa a Classe Executável `ConcessionariaApp`:**
   - Instancie três veículos distintos no Heap.
   - Demonstre o encadeamento de construtores instanciando um dos veículos sem declarar o valor inicial.
   - Aplique descontos válidos e tente aplicar um desconto abusivo de 40%, comprovando no console que a regra de guarda do método protege o preço do veículo contra mutações indevidas.