# 📚 Tutorial de Revisão — Avaliação 3POB

**Disciplina:** Programação Orientada a Objetos  
**Curso:** Análise e Desenvolvimento de Sistemas (3º Período)  
**Material de apoio:** Revisão completa para a avaliação de 100 pontos (20 questões estilo ENADE)

---

## 1. Como usar este tutorial

Este tutorial foi construído como um **guia de revisão para a avaliação**. Cada unidade:

1. **Revisa o conteúdo da ementa** (adaptado das 18 aulas ministradas);
2. **Resolve passo a passo as questões da prova** relacionadas ao tema;
3. **Oferece exercícios de fixação** extras com gabarito comentado.

> 🎯 **Dica:** se você quer revisar apenas o que cai na prova, siga o mapa da Seção 2 e estude apenas as unidades indicadas para cada questão. Se quer revisar toda a ementa, estude as unidades em ordem.

---

## 2. Mapa: Questão da Prova → Unidade do Tutorial → Aula da Ementa

| Questão da Prova | Tema | Unidade do Tutorial | Aulas da Ementa de Origem |
| :--- | :--- | :--- | :--- |
| **Q1** | `set()` em ArrayList | Unidade 4 | Aula 12 |
| **Q2** | `remove(Object)` em ArrayList | Unidade 4 | Aula 12 |
| **Q3** | Generics / Raw Types | Unidade 4 | Aula 11 |
| **Q4** | Índices e `IndexOutOfBoundsException` | Unidade 4 | Aula 12 |
| **Q5** | Checked Exceptions (IO) | Unidade 3 | Aulas 08 e 09 |
| **Q6** | `try-catch-finally` | Unidade 3 | Aula 09 |
| **Q7** | Exceções customizadas | Unidade 3 | Aula 10 |
| **Q8** | `InputMismatchException` | Unidade 3 | Aula 09 |
| **Q9** | Leitura de arquivos (`BufferedReader`) | Unidade 6 ⭐ | **Aula extra** |
| **Q10** | `try-with-resources` | Unidade 6 ⭐ | Aula 09 (parcial) |
| **Q11** | `FileWriter` modo append | Unidade 6 ⭐ | **Aula extra** |
| **Q12** | `super(...)` em construtores | Unidade 2 | Aula 07 |
| **Q13** | Classes abstratas | Unidade 2 | Aula 07 |
| **Q14** | Atributos `private` vs `protected` | Unidade 2 | Aulas 05 e 07 |
| **Q15** | Polimorfismo dinâmico | Unidade 2 | Aula 07 |
| **Q16** | Sobrescrita (overriding) | Unidade 2 | Aula 07 |
| **Q17** | Interfaces como contrato | Unidade 2 | Aula 07 |
| **Q18** | Herança múltipla via interfaces | Unidade 2 | Aula 07 |
| **Q19** | Clean Code — nomes significativos | Unidade 7 ⭐ | **Aula extra** |
| **Q20** | Padrão MVC — papel do Controller | Unidade 8 ⭐ | **Aula extra** |

> ⭐ = Tópicos que **não** fazem parte das 18 aulas da ementa — matérias complementares criadas especificamente para a prova.

---

## 3. Estrutura das Unidades

| Unidade | Conteúdo | Aulas da Ementa | Questões |
| :--- | :--- | :--- | :--- |
| **Unidade 1** — Fundamentos, Modelagem e Encapsulamento | Sintaxe, tipos, controle de fluxo, classes, construtores, encapsulamento, `static`, Javadoc | Aulas 01–05 | Base (Q12, Q14, Q19) |
| **Unidade 2** — Herança, Abstração, Polimorfismo e Interfaces | `extends`, `super`, `abstract`, `@Override`, polimorfismo dinâmico, `implements` | Aulas 06 e 07 | **Q12–Q18** |
| **Unidade 3** — Tratamento de Exceções | Hierarquia `Throwable`, Stack Trace, `try-catch-finally`, Checked vs Unchecked, `throw`/`throws`, exceções customizadas | Aulas 08–10 | **Q5–Q8** |
| **Unidade 4** — Collections Framework e Generics | Generics, `List`, `ArrayList` vs `LinkedList`, `Set`, `Map`, `Iterator`, `Comparable`/`Comparator` | Aulas 11–15 | **Q1–Q4** |
| **Unidade 5** — Recursos Modernos | Anotações, Interfaces Funcionais, Lambdas, Method References | Aulas 16–18 | Revisão geral |
| **Unidade 6** ⭐ — Uso de Arquivos em Java | `File`, `FileReader`/`BufferedReader`, `FileWriter`/`BufferedWriter`, `Scanner`, `try-with-resources`, `java.nio.file` | **Aulas extras** | **Q9–Q11** |
| **Unidade 7** ⭐ — Clean Code | Princípios de código limpo, nomes significativos, responsabilidade única, MVC | **Aula extra** | **Q19** |
| **Unidade 8** ⭐ — Padrão MVC | Model, View, Controller e suas responsabilidades | **Aula extra** | **Q20** |

---

## 4. Plano de Estudo Sugerido (7 dias, ~2h/dia)

| Dia | Foco | Unidades | Questões dominadas |
| :--- | :--- | :--- | :--- |
| **1** | POO Core | Unidade 2 | Q12, Q13, Q14, Q15, Q16, Q17, Q18 |
| **2** | Collections | Unidade 4 (parte List) | Q1, Q2, Q3, Q4 |
| **3** | Exceções | Unidade 3 | Q5, Q6, Q7, Q8 |
| **4** | Arquivos | Unidade 6 | Q9, Q10, Q11 |
| **5** | Fundamentos + Bancada | Unidade 1 + revisão | Q19 (nomes) |
| **6** | Clean Code + MVC | Unidades 7 e 8 | Q19, Q20 |
| **7** | Simulado completo | Todas as 20 questões | Revisão geral |

---

## 5. Como cada unidade está organizada

Cada unidade do tutorial segue o mesmo padrão:

```
UNIDADE X — <Título>
├── 🎯 Objetivos
├── 🗺️ Mapa (quais Questões da prova e quais Aulas da ementa)
├── 📖 Revisão teórica adaptada (conteúdo da ementa em linguagem de estudo)
├── ✍️ Questões da prova resolvidas passo a passo (com gabarito comentado)
└── 🧪 Exercícios de fixação (com respostas no rodapé)
```

---

## 6. Resumo dos temas da prova

A prova vale **100 pontos** — cada uma das 20 questões vale **5 pontos**. A distribuição por tema:

| Tema | Questões | Pontos |
| :--- | :--- | :--- |
| ArrayList | Q1–Q4 | 20 pts |
| Tratamento de Exceções | Q5–Q8 | 20 pts |
| Uso de Arquivos | Q9–Q11 | 15 pts |
| Herança e Abstração | Q12–Q14 | 15 pts |
| Polimorfismo | Q15–Q16 | 10 pts |
| Interfaces | Q17–Q18 | 10 pts |
| Clean Code | Q19 | 5 pts |
| MVC | Q20 | 5 pts |

> Pesos relativos: POO Core (herança/polimorfismo/interfaces) = **40 pts** · Coleções = **20 pts** · Exceções = **20 pts** · Arquivos = **15 pts** · Clean Code + MVC = **10 pts**