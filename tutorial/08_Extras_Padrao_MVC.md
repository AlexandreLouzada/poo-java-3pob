# UNIDADE 8 ⭐ — Padrão MVC (Model-View-Controller)

**Status:** 🌟 **AULA EXTRA** — tópico complementar para a Questão 20 (5 pontos). Não faz parte das 18 aulas da ementa.  
**Questões da prova que esta unidade resolve:** Q20 (**5 pontos**)  
**Tempo estimado de estudo:** 45 min

---

## 🎯 Objetivos

1. Compreender a separação de responsabilidades em **Model**, **View** e **Controller**;
2. Identificar o papel de cada camada em um sistema real (ex.: gerenciamento de produtos);
3. Entender por que o padrão facilita manutenção, extensão e testes.

---

## 🗺️ Mapa da Unidade

| Questão da Prova | Habilidade testada |
| :--- | :--- |
| **Q20** | Responsabilidade principal do **Controller** no padrão MVC |

---

## 📖 Revisão Teórica

### As três camadas do MVC

```
        ┌──────────┐   1. interação    ┌──────────────┐
        │ Usuário  │ ─────────────────▶ │     View     │
        └──────────┘                    │  (Interface) │
                                        └──────┬───────┘
                                               │ 2. comando
                                               ▼
        ┌──────────────┐   4. atualiza   ┌──────────────┐
        │    Model     │ ◀────────────── │  Controller  │
        │ (Dados/Regras│                │  (Coordena)  │
        │  de negócio) │ ──────────────▶ │              │
        └──────────────┘    3. dados     └──────────────┘
```

| Camada | Responsabilidade | Exemplo |
| :--- | :--- | :--- |
| **Model** | Dados + regras de negócio | `Produto` (id, nome, valor, getters/setters) |
| **View** | Interface com o usuário | `ProdutoView` (menu, leitura de teclado, exibição) |
| **Controller** | **Intermediário** entre Model e View | `ProdutoController` (adicionar, listar, alterar, excluir) |

```java
// MODEL — dados e regras
public class Produto {
    private int id;
    private String nome;
    private double valor;
    // construtor, getters, setters...
}

// CONTROLLER — coordena as operações
public class ProdutoController {
    private List<Produto> produtos;

    public void adicionarProduto(Produto p) { produtos.add(p); }
    public List<Produto> listarProdutos() { return produtos; }
    public boolean excluirProduto(int id) {
        return produtos.removeIf(p -> p.getId() == id);
    }
}

// VIEW — interface
public class ProdutoView {
    private ProdutoController controller;
    // exibe menu, lê dados, chama controller...
}
```

### Pontos-chave que caem na prova

1. **Controller** NÃO exibe interface (isso é View) e NÃO define as regras dos dados (isso é Model);
2. **Controller** processa comandos e coordena a comunicação; 
3. **Model** guarda estado e lógica de negócio;
4. **View** captura entrada e exibe saída;
5. Benefícios: separação de responsabilidades, extensibilidade, testabilidade e reuso (o mesmo Model pode servir a diferentes Views — console, web, API).

---

## ✍️ Questão da Prova Resolvida

### Questão 20 — Papel do Controller (5 pts)

**ENUNCIADO:** Qual a responsabilidade **principal** do Controller no padrão MVC?

A) Exibir dados e capturar entradas do teclado  
B) Definir a estrutura dos dados e regras de negócio  
C) **Intermediar Model e View, processando comandos do usuário**  ✅  
D) Gerenciar a conexão com o banco de dados  
E) Renderizar a interface gráfica  

**PASSO A PASSO:**
1. A: ❌ exibir/capturar = **View**;
2. B: ❌ estrutura e regras = **Model**;
3. D: ❌ persistência pode ser responsabilidade do Model/repositório, não do Controller;
4. E: ❌ renderizar interface = **View**;
5. C: ✅ o Controller é o **intermediário/coordenador** entre Model e View.

**GABARITO: letra C.**

---

## 🧪 Exercícios de Fixação

**Exercício 1.** Em qual camada do MVC ficaria a regra "produto não pode ter valor negativo"?
**R:** No **Model** (lógica de negócio dos dados).

**Exercício 2.** Quem deve perguntar ao usuário "deseja listar produtos?"?
**R:** A **View**.

**Exercício 3.** Cite uma vantagem de separar Model e View.
**R:** Reutilização — o mesmo Model pode alimentar console, web e API sem duplicação.

---

## ✅ Checklist de autoavaliação

- [ ] Sei dividir um sistema em Model, View e Controller
- [ ] Sei que o Controller é o **intermediário** que processa comandos
- [ ] Sei que regras de negócio ficam no **Model**
- [ ] Sei que interface de usuário fica na **View**
- [ ] Sei citar as vantagens do padrão (manutenção, extensão, testes, reuso)