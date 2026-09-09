# Explicação da Aula 01 — Paradigmas de Programação (Procedural vs. OO) e o Ecossistema Java

| | |
|---|---|
| **Disciplina** | Programação Orientada a Objetos |
| **Curso** | Análise e Desenvolvimento de Sistemas (3º Período) |
| **Tema** | Paradigmas de Programação (Procedural vs. OO) e o Ecossistema Java |
| **Carga Horária** | 100 minutos (2 horas-aula) |
| **Professor** | Alexandre Neves Louzada |
| **Plano de Aula** | `Aulas 3pob/Aula 01.md` |
| **Tutorial** | `aulas/TutorialAula01.md` |
| **Estudo de Caso** | `exemplos/aula-01/` |
| **Exercícios Resolvidos** | `solucoes/aula-01/` |

## 1. Objetivos de Aprendizagem

- **Conceitual:** Diferenciar acoplamento e separação entre dados e funções (modelo procedural/estruturado) do encapsulamento coeso de estado e comportamento (modelo orientado a objetos).
- **Técnico:** Compreender o pipeline de compilação e execução da plataforma Java (JDK, JRE, JVM, Bytecode e compilação JIT).
- **Operacional:** Executar manualmente o ciclo de compilação (`javac`), execução (`java`) e inspeção de instruções de pilha (`javap -c`) no terminal sem dependência de IDEs.
- **Arquitetural:** Compreender a organização da memória de tempo de execução da JVM (separação entre frames da Stack e alocação dinâmica no Heap) e o papel do Garbage Collector (GC).

## 2. Conteúdo Teórico Detalhado

### 2.1 Visão Geral da Aula e Objetivo Pedagógico

Esta aula inaugural estabelece o fundamento teórico e prático de toda a disciplina. O ponto de partida é a transição estruturada do pensamento algorítmico básico — adquirido em linguagens como C ou Python estruturado — para a engenharia de software orientada a objetos em Java. O perfil do estudante de Análise e Desenvolvimento de Sistemas demanda algo além de "fazer o código compilar": é necessário projetar software manutenível, coeso e preparado para regras corporativas complexas.

A aula é dividida em dois grandes blocos conceituais: (1) a comparação entre o paradigma procedural e o paradigma orientado a objetos, com foco em por que a POO surgiu como resposta aos problemas de manutenção de sistemas corporativos; e (2) o ecossistema da plataforma Java, incluindo o pipeline de compilação, a organização de memória da JVM e a compilação JIT.

### 2.2 O Paradigma Procedural / Estruturado

O paradigma procedural é regido pela famosa equação de Niklaus Wirth: *Programa = Algoritmos + Estruturas de Dados*. Nesse modelo, existe uma separação estrita entre **dados** e **comportamento**. Os dados são representados por `structs` (em C), variáveis globais ou locais, enquanto o comportamento reside em funções e procedimentos que recebem essas estruturas como parâmetros e as manipulam livremente.

O fluxo de execução é tipicamente descrito por diagramas de fluxo sequencial: chamadas encadeadas de sub-rotinas que operam sobre estruturas passadas de mão em mão. O modelo mental do desenvolvedor procedural pergunta: *"O que o sistema precisa fazer passo a passo?"*.

Esse paradigma possui forças claras: é ideal para drivers de baixo nível, scripts lineares e processamento puramente matemático. A maioria dos alunos de ADS já teve contato com C, Pascal ou Python estruturado e reconhece esse padrão de organização do código.

### 2.3 O Ponto de Ruptura do Modelo Procedural

O gargalo de manutenção em sistemas corporativos se manifesta quando múltiplas funções passam a acessar e modificar diretamente os mesmos registros de dados sem qualquer barreira de proteção. Isso gera três problemas fundamentais:

- **Efeito Colateral Indesejado:** Como os dados ficam expostos, qualquer função pode alterá-los, criando efeitos colaterais imprevisíveis em outras partes do sistema.
- **Alto Acoplamento:** Alterar o layout de uma estrutura (`struct`) quebra dezenas de funções dependentes espalhadas pela base de código, tornando a manutenção um pesadelo.
- **Falta de Invariantes de Negócio:** Dados em estado inconsistente trafegam livremente pelo sistema. Um saldo bancário pode se tornar negativo, um cadastro pode ficar sem documento — nenhuma regra de validação é imposta automaticamente.

**Exemplo prático:** Imagine um sistema bancário com `struct Conta { double saldo; }`. Qualquer programador pode escrever `conta.saldo = -50000;` em qualquer parte do sistema, sem que nenhuma validação de saldo seja acionada. Em bases de código com centenas de milhares de linhas, a depuração se torna caótica.

### 2.4 O Paradigma Orientado a Objetos (POO)

A POO resolve os problemas do paradigma procedural unindo estado e comportamento em uma única entidade: o **objeto**. Um objeto é uma entidade de software que agrupa atributos (estado) e métodos (comportamento) de forma coesa. A chave do paradigma é o **encapsulamento**: o estado interno do objeto é blindado com o modificador `private`, e a manipulação ocorre exclusivamente através de mensagens/métodos públicos.

A mudança de pergunta é fundamental: em vez de *"Como calcular o juro?"*, o desenvolvedor pergunta: *"Qual classe do domínio tem a responsabilidade de gerenciar as suas próprias regras de juros?"*. A resposta define a localização da lógica dentro do objeto, promovendo um design modular e desacoplado.

A metáfora da "caixa-preta" ajuda a entender o conceito: o consumidor do objeto conhece apenas a interface pública (os métodos), mas não precisa se preocupar com os detalhes internos de implementação.

A tabela comparativa abaixo resume as diferenças entre os dois paradigmas:

| Aspecto | Paradigma Procedural | Paradigma Orientado a Objetos |
| :--- | :--- | :--- |
| **Foco Central** | Verbo / Ação (Como processar) | Substantivo / Entidade (Quem é responsável) |
| **Organização** | Funções e procedimentos soltos | Classes e Objetos encapsulados |
| **Segurança dos Dados** | Baixa (dados expostos à mutação direta) | Alta (encapsulamento e visibilidade) |
| **Escalabilidade** | Complexa e propensa a efeitos colaterais | Modular, desacoplada e extensível |

### 2.5 Estudo de Caso Comparativo de Código

O estudo de caso comparativo demonstra a diferença prática entre as duas abordagens usando o controle de integridade de saldo bancário:

**Abordagem Estruturada (Dados Expostos):**

```java
// Dados soltos sem proteção contra mutação indevida
double saldo = 100.0;

// Função utilitária externa
saldo = sacar(saldo, 150.0); // Onde está a garantia contra saldo negativo?
```

Nessa abordagem, a variável `saldo` é apenas um número solto na Stack. A função `sacar()` tenta validar internamente, mas nada impede que qualquer outra parte do código escreva diretamente `saldo = -50000.0`. Não existe encapsulamento.

**Abordagem Orientada a Objetos (Estado Blindado):**

```java
// Objeto autônomo e responsável pela sua própria consistência
ContaBancaria conta = new ContaBancaria(100.0);
conta.sacar(150.0); // A própria classe rejeita a operação e protege sua invariante
```

Nessa abordagem, a classe `ContaBancaria` encapsula o atributo `saldo` como `private` e controla todo acesso através de métodos públicos (`sacar()`, `depositar()`, `getSaldo()`). A tentativa de saque de R$ 150,00 com saldo de R$ 100,00 é simplesmente rejeitada pelo próprio objeto. A tentativa de atribuição direta como `conta.saldo = -50000.0` nem compilaria, pois o atributo é privado.

O dinâmica de *Peer Instruction* proposta é perguntar: o que acontece no modelo procedural se um desenvolvedor desatento criar uma função `aplicarTarifa()` sem checar se o saldo é suficiente? A resposta evidencia que a POO transfere o controle de integridade da mão do chamador para a própria entidade.

### 2.6 A Arquitetura da Plataforma Java

Java é conhecida pela filosofia *Write Once, Run Anywhere* (WORA) — escreva uma vez, execute em qualquer lugar. Essa capacidade é possível graças ao pipeline de compilação e execução da plataforma:

$$\text{Código-Fonte (.java)} \xrightarrow[\text{javac}]{Compilador} \text{Bytecode (.class)} \xrightarrow[\text{JVM}]{Interpretação / JIT} \text{Código Nativo (SO/Hardware)}$$

Os três componentes principais são:

- **JDK (Java Development Kit):** Contém o compilador (`javac`), utilitários (`javadoc`, `javap`) e todo o ambiente de desenvolvimento necessário para escrever, compilar e depurar código Java.
- **JRE (Java Runtime Environment):** Contém as bibliotecas de classes padrão e a JVM. É voltado para ambientes de execução em servidores de produção, sem ferramentas de desenvolvimento.
- **JVM (Java Virtual Machine):** A máquina virtual que isola a aplicação das particularidades do sistema operacional. É a JVM que interpreta o bytecode e o converte em instruções nativas do hardware.

O **Bytecode** é um conjunto padronizado de instruções de baixo nível que não dependem do conjunto de instruções do processador físico (x86, ARM, RISC-V). Isso permite que um mesmo arquivo `.class` seja executado em qualquer plataforma que possua uma JVM compatível.

O compilador **JIT (Just-In-Time)** é uma otimização em tempo de execução da JVM. Ele monitora em tempo real os trechos mais executados da aplicação — chamados de *hot spots* — e os compila dinamicamente para código nativo ultraotimizado, combinando a portabilidade do bytecode com o desempenho de código compilado nativamente.

### 2.7 Gerenciamento de Memória: Stack, Heap e o Garbage Collector

A JVM organiza a memória de tempo de execução em três áreas fundamentais:

**Stack (Pilha de Execução):**
- Armazena os quadros de chamada de métodos (*Stack Frames*). Cada vez que um método é invocado, um novo frame é empilhado; quando o método retorna, o frame é desempilhado.
- Guarda variáveis locais de tipos primitivos (`int`, `double`, `boolean`, etc.) e ponteiros de referência (variáveis que apontam para objetos no Heap).
- Alocação e desalocação são automáticas e extremamente rápidas, pois seguem o princípio LIFO (Last In, First Out).

**Heap (Área Dinâmica):**
- Espaço onde residem todas as instâncias de objetos criados com o operador `new`.
- Diferente da Stack, a alocação no Heap é gerenciada dinamicamente e os objetos permanecem enquanto forem acessíveis (referenciados) por pelo menos uma variável na Stack.
- O Heap é compartilhado entre todas as threads da aplicação.

**Garbage Collector (GC):**
- Processo em segundo plano que rastreia e desaloca automaticamente objetos inalcançáveis — ou seja, objetos que não possuem mais nenhuma referência ativa na Stack.
- Elimina a necessidade de gerenciar memória manualmente como em C/C++ (`malloc`/`free`, `delete`), prevenindo ponteiros pendentes (*dangling pointers*) e vazamentos de memória estruturais.
- Ressalva importante de engenharia: o GC gerencia a desalocação, mas não impede más práticas, como acumular objetos desnecessários em coleções estáticas. A responsabilidade de eficiência continua com o desenvolvedor.

### 2.8 Estrutura Mínima de um Programa Java e Compilação Manual

A estrutura mínima de um programa Java consiste em uma classe pública contendo o método `main`:

```java
public class OlaMundo {
    public static void main(String[] args) {
        System.out.println("Fundamentos de POO e Plataforma Java!");
    }
}
```

Elementos obrigatórios:
- **`public class OlaMundo`**: O nome da classe deve ser idêntico ao nome do arquivo (`OlaMundo.java`). A classe deve ser `public` para que o `javac` a reconheça como ponto de entrada.
- **`public static void main(String[] args)`**: Assinatura fixa do método de entrada. `public` permite acesso externo pela JVM; `static` permite chamada sem instanciar a classe; `void` indica ausência de retorno; `String[] args` recebe argumentos de linha de comando.

O ciclo de compilação e execução manual no terminal é:

1. **Compilação:** `javac OlaMundo.java` — O compilador Java (`javac`) analisa o código-fonte, verifica erros de sintaxe e tipagem, e gera o arquivo `OlaMundo.class` contendo bytecode.
2. **Execução:** `java OlaMundo` — A JVM (`java`) carrega o arquivo `.class`, verifica a integridade do bytecode via *verificador de classes*, e executa o método `main`.
3. **Inspeção:** `javap -c OlaMundo` — O utilitário `javap` (*Java Class File Disassembler*) exibe as instruções de bytecode geradas, permitindo entender o que a JVM realmente executa sob o código de alto nível.

A desmistificação da "mágica" das IDEs é um objetivo pedagógico desta aula: compilar e inspecionar bytecode diretamente no terminal mostra ao estudante que não existe magia, apenas um pipeline bem definido de ferramentas. IDEs como IntelliJ IDEA, Eclipse e VS Code executam exatamente esses comandos em segundo plano, mas o desenvolvedor que compreende o pipeline tem controle total sobre o processo de build.

### 2.9 Metodologia Ativa da Aula

A aula utiliza duas metodologias ativas para fixação dos conceitos:

**Peer Instruction:** O professor apresenta um caso de violação de regra de negócio em código procedural (ex.: `conta.saldo = -50000;`) e os alunos discutem em pares como o encapsulamento transfere a responsabilidade da integridade para a própria entidade. O objetivo é que os próprios alunos articulem a vantagem da POO antes de receberem a explicação formal.

**Laboratório Hands-on / CLI:** Os alunos compilam, executam e inspecionam bytecode diretamente no terminal, sem dependência de IDEs. Essa experiência prática desmistifica a camada de abstração das ferramentas e consolida a compreensão do pipeline de compilação e execução.

## 3. Estudo de Caso Aplicado

O estudo de caso da Aula 01 está na pasta `exemplos/aula-01/src/` e contém quatro classes que ilustram a progressão do paradigma procedural para o paradigma orientado a objetos:

- **`OlaMundo.java`** — Programa mínimo sem pacote declarado. Demonstra a estrutura básica de um programa Java: classe pública com método `main`. Serve como ponto de partida para a compilação manual via `javac`/`java`/`javap`.

- **`BancoProceduralApp.java`** — Implementa o cenário bancário no paradigma procedural. A variável `saldo` é um `double` solto na memória. A função `sacar()` recebe o saldo como parâmetro e retorna o novo valor, mas nada impede que qualquer linha do programa atribua diretamente `saldo = -50000.0`, violando a regra de negócio. O código demonstra explicitamente esse "ponto de ruptura".

- **`ContaBancaria.java`** — Implementa a versão OO da conta bancária. O atributo `saldo` é `private` e só pode ser acessado através dos métodos públicos `sacar()`, `depositar()` e `getSaldo()`. O construtor valida o saldo inicial. A própria classe rejeita operações que violem a invariante.

- **`BancoOOApp.java`** — Classe principal que demonstra a criação de um objeto `ContaBancaria` via `new`, a tentativa de saque que excede o saldo (rejeitada pelo objeto), e a impossibilidade de atribuição direta ao saldo (causaria erro de compilação).

A organização em pacote padrão (default package) facilita a compilação manual sem necessidade de diretórios de pacote.

## 4. Exercícios Propostos e Solução

A seção de exercícios da aula propõe a implementação de um sistema de controle de estoque, exercitando os conceitos de classes, atributos privados, construtores com validação e métodos de negócio com regras de guarda.

A solução está em `solucoes/aula-01/src/` e contém duas classes:

- **`ItemEstoque.java`** — Classe que modela um item de estoque com atributos privados (`codigo`, `nome`, `quantidade`, `valorUnitario`). O construtor valida quantidades negativas (corrigindo para zero) e valores unitários inválidos (corrigindo para zero). Os métodos `adicionarEstoque()` e `removerEstoque()` implementam regras de guarda: só permitem operações com quantidades positivas e o método de remoção verifica se há estoque suficiente. Getters públicos permitem consulta segura. O método `exibirFicha()` formata a saída com `printf`.

- **`EstoqueApp.java`** — Classe executável que cria instâncias de `ItemEstoque`, demonstra movimentações legítimas (entrada e saída), tentativas de violação de invariante (retirada excessiva, entrada negativa, inicialização com quantidade negativa), verificando a cada passo que a integridade dos dados é preservada pelo próprio objeto.

O desafio CLI ao final do exercício instrui o estudante a compilar manualmente (`javac ItemEstoque.java EstoqueApp.java`), executar (`java EstoqueApp`) e inspecionar bytecode (`javap -c ItemEstoque`).

## 5. Perguntas de Revisão

1. **Qual a principal diferença entre o paradigma procedural e o paradigma orientado a objetos quanto ao tratamento dos dados?**
   - No procedural, dados e funções estão separados e os dados ficam expostos à mutação direta. Na POO, dados e comportamento estão unidos em objetos encapsulados, onde o acesso aos dados é controlado por métodos.

2. **O que é Bytecode e por que ele é fundamental para a filosofia WORA?**
   - Bytecode é um conjunto padronizado de instruções intermediárias geradas pelo compilador Java (`javac`). Por não depender de arquitetura de hardware específica, o mesmo bytecode pode ser executado em qualquer plataforma que possua uma JVM compatível.

3. **Qual a diferença entre Stack e Heap na memória da JVM?**
   - A Stack armazena frames de chamada de métodos e variáveis locais de tipos primitivos e referências. O Heap armazena as instâncias de objetos criados com `new`. A Stack é gerenciada automaticamente por frame; o Heap é gerenciado pelo Garbage Collector.

4. **Qual o papel do Garbage Collector (GC) e quais problemas ele resolve?**
   - O GC desaloca automaticamente objetos que não possuem mais referências ativas, eliminando a necessidade de gerenciamento manual de memória e prevenindo vazamentos de memória e ponteiros pendentes.

## 6. Resumo / Pontos-Chave

- O paradigma procedural separa dados e funções, levando a alto acoplamento e dificuldade de manutenção em sistemas corporativos.
- A POO une estado e comportamento em objetos encapsulados, transferindo a responsabilidade da integridade dos dados para a própria entidade.
- A plataforma Java segue o modelo WORA: código-fonte é compilado para Bytecode (`.class`), que é executado pela JVM em qualquer plataforma.
- O pipeline de compilação envolve `javac` (compilador), JVM (intérprete/compilador JIT) e código nativo do hardware.
- A memória da JVM é dividida em Stack (quadros de chamada e variáveis locais/primitivos) e Heap (instâncias de objetos).
- O Garbage Collector gerencia automaticamente a desalocação de objetos inalcançáveis, eliminando problemas clássicos de C/C++.
- A compilação manual via terminal (`javac`, `java`, `javap`) desmistifica o funcionamento das IDEs.
