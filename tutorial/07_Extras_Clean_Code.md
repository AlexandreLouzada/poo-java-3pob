# UNIDADE 7 ⭐ — Clean Code (Código Limpo)

**Status:** 🌟 **AULA EXTRA** — tópico complementar para a Questão 19 (5 pontos). Conteúdo relacionado nas Aulas 03 (Javadoc/documentação) e 17-18 (código expressivo).  
**Questões da prova que esta unidade resolve:** Q19 (**5 pontos**)  
**Tempo estimado de estudo:** 45 min

---

## 🎯 Objetivos

1. Compreender os princípios de **código limpo** popularizados por Robert C. Martin (Uncle Bob);
2. Reconhecer violações clássicas (nomes ruins, métodos longos, comentários desnecessários);
3. Aplicar nomes significativos, responsabilidade única e métodos pequenos.

---

## 🗺️ Mapa da Unidade

| Questão da Prova | Habilidade testada |
| :--- | :--- |
| **Q19** | Reconhecer nomes não significativos (violação de Clean Code) |

---

## 📖 Revisão Teórica

### Princípios fundamentais

**1. Nomes significativos** 🔑 *(foco da Q19)*
- Variáveis, métodos e classes devem **revelar a intenção**;
- Evite `calc(a, b)`, `x`, `dados1` — prefira `calcularDesconto(valor, percentual)`.

```java
// ❌ ERRADO:
public double calc(double a, double b) {
    return a * b / 100;
}

// ✅ CERTO:
public double calcularDesconto(double valor, double percentual) {
    return valor * (percentual / 100.0);
}
```

**2. Responsabilidade Única (SRP)**
- Cada classe/método faz **apenas uma coisa**;
- Ex.: `ProdutoController` cuida de dados; `ProdutoView` cuida de interface.

**3. Métodos pequenos**
- Métodos curtos executando uma única tarefa;
- Mais fáceis de testar e manter.

**4. Evitar comentários desnecessários**
- Código autoexplicativo dispensa comentários;
- Comentário só quando agrega valor (lógica complexa).

**5. Tratamento de exceções significativo**
- Exceções específicas com mensagens claras (Unidade 3).

**6. Uso de interface/abstrações**
- Dependa de contratos, não de implementações concretas (Unidade 2).

---

## ✍️ Questão da Prova Resolvida

### Questão 19 — Violação de Clean Code (5 pts)

**ENUNCIADO:** Qual princípio de *Clean Code* está sendo violado?

```java
public double calc(double a, double b) {
    return a * b / 100;
}
```

A) Princípio da Responsabilidade Única (SRP)  
B) **Nomes significativos**  ✅  
C) Tratamento adequado de exceções  
D) Métodos pequenos  
E) Uso adequado de parâmetros  

**PASSO A PASSO:**
1. `calc` não revela o propósito do método; `a` e `b` também são genéricos;
2. A violação é de **nomes significativos** (Reveals intention);
3. SRP (A) se refere a "uma responsabilidade" — não é o problema aqui;
4. O método é pequeno (D) e não envolve exceções (C);
5. Correção: `calcularDesconto(double valor, double percentual)`.

**GABARITO: letra B.**

---

## 🧪 Exercícios de Fixação

**Exercício 1.** Renomeie para nomes significativos: `public int m(int n) { return (n * 9 / 5) + 32; }`
**R:** `public int celsiusParaFahrenheit(int celsius)` — expressa exatamente o que faz.

**Exercício 2.** Qual o benefício de métodos pequenos?
**R:** Legibilidade, testabilidade e manutenção mais fáceis (uma tarefa por método).

**Exercício 3.** Comentários extensos são automática e sempre bons? Por quê?
**R:** Não. Código legível dispensa comentários; comentários devem explicar o "porquê", não repetir o "como".

---

## ✅ Checklist de autoavaliação

- [ ] Uso nomes que revelam a intenção (métodos, classes, variáveis)
- [ ] Evito variáveis genéricas (`a`, `b`, `x`, `dados`)
- [ ] Mantenho métodos curtos com uma única responsabilidade
- [ ] Só comento quando o comentário agrega valor
- [ ] Uso exceções específicas e mensagens claras