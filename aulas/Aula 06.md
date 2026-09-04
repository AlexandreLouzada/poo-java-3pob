# Plano de Aula e Roteiro de Slides: Aula 06

**Disciplina:** Programação Orientada a Objetos  
**Curso:** Análise e Desenvolvimento de Sistemas (3º Período)  
**Tema:** Sobrecarga de Métodos (*Overloading*), Argumentos Variáveis (*Varargs*) e Tipos Enumerados (*Enums* Avançados)  
**Carga Horária:** 100 minutos (2 horas-aula)  
**Professor:** Alexandre Neves Louzada

---

## 1. Plano de Ensino da Aula

### 1.1. Objetivos de Aprendizagem
* **Conceitual:** Compreender o polimorfismo estático em tempo de compilação (*Static / Early Binding*) através da sobrecarga de métodos; reconhecer as limitações e riscos de integridade de constantes inteiras soltas (*Magic Numbers*); entender que tipos enumerados (`enum`) em Java são tipos orientados a objetos completos.
* **Técnico:** Projetar métodos sobrecarregados baseando-se na assinatura reconhecida pela JVM (nome + lista ordenada de tipos de parâmetros); utilizar a sintaxe de *Varargs* (`Tipo...`), respeitando a restrição de unicidade e posicionamento final na assinatura; implementar enums avançados com atributos imutáveis (`final`), construtores parametrizados privados e métodos de cálculo encapsulados.
* **Arquitetural:** Integrar enums com estruturas condicionais modernas (*Switch Expressions*), garantindo cobertura exaustiva de cenários pelo compilador e eliminando o antipadrão *fall-through*.
* **Prático:** Implementar um motor de cálculo de frete e logística para e-commerce, combinando tabelas tarifárias baseadas em enums ricos e métodos sobrecarregados com *varargs* para pesagem individual e em lotes.

### 1.2. Metodologia Ativa
* **Coding Dojo (Randori):** Desenvolvimento coletivo de uma rotina de cálculo logístico onde a assinatura dos métodos evolui de parâmetros individuais rígidos para sobrecargas elegantes com *varargs* e enums tipados.
* **Análise de Código Legado vs. Moderno:** Comparação de um módulo financeiro antigo estruturado com inteiros mágicos (`int STATUS_CRIADO = 1`) versus a versão moderna com `enum` tipado e validado em tempo de compilação.

---

## 2. Roteiro Detalhado de Slides

---

### Slide 1: Abertura e Visão Geral
* **Título do Slide:** Flexibilidade e Segurança de Tipos: Sobrecarga, Varargs e Enums
* **Tópicos Visuais:**
  * Sobrecarga (*Method Overloading*): polimorfismo estático em tempo de compilação.
  * *Varargs* (`Tipo...`): flexibilidade para listas dinâmicas de argumentos.
  * O perigo das constantes soltas (*Magic Numbers* e *Magic Strings*).
  * Tipos Enumerados (`enum`) no Java: classes completas com estado e regras de negócio.
* **Notas Pedagógicas do Professor:**
  * Contextualizar: nas Aulas 04 e 05 aprendemos a modelar e encapsular o estado. Agora aprenderemos como tornar nossos métodos mais flexíveis e como representar domínios discretos de negócio com 100% de segurança de tipos.
  * Ressaltar a diferença de Java para C/C++: em Java, `enum` não é apenas um apelido para inteiros sequenciais (`0, 1, 2...`), mas uma classe rica gerenciada pela JVM.

---

### Slide 2: Sobrecarga de Métodos (*Method Overloading*)
* **Título do Slide:** Sobrecarga: Múltiplas Formas do Mesmo Método
* **Tópicos Visuais:**
  * **Conceito:** Dois ou mais métodos na mesma classe que compartilham o mesmo nome, mas possuem listas de parâmetros diferentes.
  * **Assinatura de Método na JVM:**
    $$\text{Assinatura} = \text{Nome do Método} + \text{Lista Ordenada dos Tipos dos Parâmetros}$$
  * **Regra de Ouro do Compilador:**
    * O tipo de retorno **NÃO** diferencia sobrecargas!
    * O nome dos parâmetros **NÃO** diferencia sobrecargas!
    * Modificadores de acesso (`public`, `private`) **NÃO** diferenciam sobrecargas!
* **Notas Pedagógicas do Professor:**
  * Demonstrar na IDE o erro clássico: tentar criar `double calcular(int x)` e `int calcular(int x)` na mesma classe. O compilador acusa *"method is already defined"*.
  * Explicar o termo técnico: resolução estática antecipada (*Early Binding* ou *Compile-time Polymorphism*).

---

### Slide 3: Argumentos Variáveis (*Varargs*)
* **Título do Slide:** Métodos com Quantidade Flexível de Argumentos: `Tipo...`
* **Tópicos Visuais:**
  * **Problema:** Criar sobrecargas manuais para 1, 2, 3 ou 4 parâmetros polui a classe.
  * **Sintaxe:** `public double somar(double... valores)`
  * **Como a JVM enxerga:** O parâmetro `Tipo...` é sintetizado internamente como um array primitivo (`Tipo[]`).
  * **Restrições Estritas do Compilador:**
    1. Apenas **um único** parâmetro *varargs* por método.
    2. O *varargs* deve ser **obrigatoriamente o último** parâmetro da assinatura.
* **Notas Pedagógicas do Professor:**
  * Mostrar que o chamador pode invocar `somar()`, `somar(10.5)`, `somar(10.5, 20.0, 30.2)` ou até passar um array pré-alocado `somar(new double[]{1, 2, 3})` com total transparência.
  * Alertar sobre o risco de `NullPointerException` caso alguém passe `null` explícito para um parâmetro *varargs*.

---

### Slide 4: Estudo de Caso — Sobrecarga e Varargs na Prática
* **Título do Slide:** Implementação de Calculadora de Descontos e Fretes
* **Tópicos Visuais:**
  ```java
  public class CalculadoraFrete {

      // Sobrecarga 1: Pacote único por peso
      public double calcular(double pesoKg) {
          return pesoKg * 5.0;
      }

      // Sobrecarga 2: Múltiplos pacotes com varargs
      public double calcular(double... pesosItens) {
          if (pesosItens == null || pesosItens.length == 0) {
              return 0.0;
          }
          double pesoTotal = 0.0;
          for (double p : pesosItens) {
              if (p > 0) pesoTotal += p;
          }
          return calcular(pesoTotal); // Delegação de responsabilidade!
      }
  }