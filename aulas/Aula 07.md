# Plano de Aula e Roteiro de Slides: Aula 07

**Disciplina:** Programação Orientada a Objetos  
**Curso:** Análise e Desenvolvimento de Sistemas (3º Período)  
**Tema:** Herança (`extends`), Polimorfismo Dinâmico, Cadeia de Construtores (`super`), Classes Abstratas (`abstract`) e Interfaces (`implements`)  
**Carga Horária:** 100 minutos (2 horas-aula)  
**Professor:** Alexandre Neves Louzada

---

## 1. Plano de Ensino da Aula

### 1.1. Objetivos de Aprendizagem
* **Conceitual:** Compreender o mecanismo de herança como especialização de tipo e reuso estrutural (*relação É-UM*); dominar o conceito de **Polimorfismo Dinâmico** (*Dynamic Method Dispatch* / *Late Binding*) e sua relação com o princípio Aberto/Fechado (OCP do SOLID).
* **Técnico:** Projetar hierarquias extensíveis utilizando a palavra-chave `extends`; invocar construtores e métodos da superclasse através de `super`; aplicar a anotação `@Override` com rigor; projetar classes base incompletas com `abstract class` e métodos `abstract`; definir contratos desacoplados com `interface` e `implements`.
* **Arquitetural:** Dominar a matriz de decisão entre Classes Abstratas (compartilhamento de estado estrutural e código comum em hierarquias fortemente acopladas) versus Interfaces (contratos comportamentais puros, desacoplados e de múltipla implementação).
* **Prático:** Implementar um motor de checkout de pagamentos eletrônicos em lote (Cartão de Crédito, Pix, Boleto), onde a camada consumidora opera exclusivamente sobre abstrações polimórficas sem conhecer as classes concretas.

### 1.2. Metodologia Ativa
* **Jigsaw Classroom (Especialização em Grupos):** A turma é dividida em grupos focados no projeto de abstrações estruturais (classes base abstratas) e contratos de comportamento (interfaces), convergindo para a montagem de uma arquitetura limpa de checkout.
* **Demonstração Forense de Polimorfismo:** Inspecionar no depurador da IDE a tabela de métodos virtuais (*vtable*) da JVM resolvendo em tempo de execução o método da subclasse correta através de uma variável de referência da interface genérica.

---

## 2. Roteiro Detalhado de Slides

---

### Slide 1: Abertura e Visão Geral
* **Título do Slide:** Herança, Polimorfismo Dinâmico, Classes Abstratas e Interfaces
* **Tópicos Visuais:**
  * Reuso estrutural vs. Extensibilidade comportamental.
  * Herança em Java (`extends`) e a relação "É-UM" (*Is-A*).
  * Polimorfismo Dinâmico: despachando métodos em tempo de execução (*Late Binding*).
  * Classes Abstratas (`abstract`) vs. Contratos Puros de Comportamento (`interface`).
* **Notas Pedagógicas do Professor:**
  * Apresentar o fechamento do Módulo 2: esta aula consolida todos os conceitos fundamentais da Orientação a Objetos.
  * Enfatizar a máxima do Design de Software corporativo (Gang of Four): *"Programe para interfaces/abstrações, nunca para implementações concretas"*.

---

### Slide 2: Herança em Java (`extends`) e o Operador `super`
* **Título do Slide:** Especialização de Classes e a Cadeia de Construtores
* **Tópicos Visuais:**
  * **Herança Simples:** Em Java, uma classe herda diretamente de apenas **uma única superclasse** (raiz universal: `java.lang.Object`).
  * **Modificador `protected`:** Permite acesso direto aos membros por subclasses (mesmo em outros pacotes) e classes do mesmo pacote.
  * **A Palavra-Chave `super`:**
    * `super(...)`: Invoca o construtor da superclasse (**deve ser a 1ª linha do construtor filho**).
    * `super.metodo()`: Invoca a versão original do método da superclasse.
* **Notas Pedagógicas do Professor:**
  * Explicar a mecânica de instanciação: ao instanciar uma subclasse, a JVM executa primeiro o construtor de `Object`, depois da superclasse e, por último, o da classe filha.

---

### Slide 3: Sobrescrita de Métodos (*Override*) vs. Sobrecarga (*Overload*)
* **Título do Slide:** Redefinindo Comportamentos: A Anotação `@Override`
* **Tópicos Visuais:**
  * Tabela Comparativa:

| Critério | Sobrecarga (*Overloading* - Aula 06) | Sobrescrita (*Overriding* - Aula 07) |
| :--- | :--- | :--- |
| **Escopo** | Na mesma classe | Entre Superclasse e Subclasse |
| **Assinatura** | **Tipos/quantidades de parâmetros diferentes** | **Assinatura rigorosamente idêntica** |
| **Retorno** | Pode ser diferente | Idêntico ou subtipo covariante |
| **Momento de Resolução** | **Tempo de Compilação** (*Static Binding*) | **Tempo de Execução** (*Dynamic Binding*) |

* **Notas Pedagógicas do Professor:**
  * Reforçar a regra de boas práticas: a anotação `@Override` não é apenas decorativa; ela instrui o compilador a validar se o método realmente existe na superclasse, evitando bugs silenciosos por erros de digitação na assinatura.

---

### Slide 4: Polimorfismo Dinâmico (*Dynamic Method Dispatch*)
* **Título do Slide:** Polimorfismo: Uma Interface, Múltiplos Comportamentos
* **Tópicos Visuais:**
  ```java
  // Referência genérica apontando para instâncias concretas distintas
  MeioPagamento pagamento1 = new CartaoCredito(2.5, 1500.00);
  MeioPagamento pagamento2 = new PagamentoPix("chave@empresa.com");

  // A JVM decide em tempo de execução qual método executar!
  pagamento1.autorizar(200.00); // Executa a lógica de limite do CartaoCredito
  pagamento2.autorizar(200.00); // Executa a lógica de saldo/chave do Pix