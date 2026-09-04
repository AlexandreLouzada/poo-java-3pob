# UNIDADE 6 ⭐ — Uso de Arquivos em Java

**Status:** 🌟 **AULA EXTRA** — este tópico **não** faz parte das 18 aulas da ementa. Foi criado porque a prova cobra 3 questões (Q9, Q10, Q11 = 15 pontos) sobre manipulação de arquivos.  
**Aula da ementa relacionada:** Aula 09 (usa `IOException` e `try-with-resources` — conceitos pré-requisito)  
**Questões da prova que esta unidade resolve:** Q9, Q10, Q11 (**15 pontos**)  
**Tempo estimado de estudo:** 1h30

---

## 🎯 Objetivos

Ao final desta unidade, você será capaz de:

1. Usar `File`, `FileReader`/`BufferedReader` para leitura e `FileWriter`/`BufferedWriter` para escrita;
2. **Persistir dados** em arquivos de texto (ex.: CSV) e recriar objetos ao ler;
3. Aplicar `try-with-resources` para garantir o fechamento automático de recursos;
4. Entender o modo *append* (`FileWriter` com `true`) para acrescentar conteúdo;
5. Usar `Scanner` para leitura simples e as classes `Files`/`Paths` do pacote `java.nio.file`.

---

## 🗺️ Mapa da Unidade

| Questão da Prova | Habilidade testada | Conceito-chave |
| :--- | :--- | :--- |
| **Q9** | Lecher linha a linha e dividir campos | `BufferedReader` + `split(";")` |
| **Q10** | Fechamento automático de recursos | `try-with-resources` |
| **Q11** | Adicionar conteúdo sem apagar | `FileWriter(arquivo, true)` |

---

## 📖 Revisão Teórica

### 1. Por que manipular arquivos?

Aplicações precisam **persistir dados** (guardar além da execução). Em Java, as classes de arquivo ficam principalmente em `java.io` e `java.nio.file`.

### 2. A classe `File` — representação de arquivo/diretório

```java
File arquivo = new File("dados.txt");
if (arquivo.exists()) {
    System.out.println("Arquivo existe: " + arquivo.getAbsolutePath());
} else {
    System.out.println("Arquivo não encontrado.");
}
```

- Representa um caminho do sistema de arquivos;
- Permite verificar existência, criar diretórios, obter caminho absoluto.

### 3. Leitura com `FileReader` + `BufferedReader`

- `FileReader`: lê **caracteres** de um arquivo;
- `BufferedReader`: envolve um `Reader` e usa **buffer** → mais eficiente + `readLine()`.

```java
try (BufferedReader reader = new BufferedReader(new FileReader("produtos.csv"))) {
    String linha;
    while ((linha = reader.readLine()) != null) {
        System.out.println(linha);
    }
} catch (IOException e) {
    System.out.println("Erro ao ler o arquivo: " + e.getMessage());
}
```

### 4. Escrita com `FileWriter` + `BufferedWriter`

- `FileWriter`: grava **caracteres** em um arquivo;
- `BufferedWriter`: bufferiza + métodos `write(...)` e `newLine()`.

```java
try (BufferedWriter writer = new BufferedWriter(new FileWriter("dados.txt"))) {
    writer.write("Linha 1");
    writer.newLine();
    writer.write("Linha 2");
} catch (IOException e) {
    System.out.println("Erro ao escrever no arquivo: " + e.getMessage());
}
```

### 5. Modo *append* — não apagar o conteúdo existente 🔑

```java
// Sobrescreve (padrão):
new FileWriter("log.txt");
// false == mesmo comportamento

// Acrescenta ao final (preserva conteúdo):
new FileWriter("log.txt", true);   // <-- MODO APPEND
```

> ⚠️ O segundo parâmetro booleano `true` do `FileWriter` é a chave da **Questão 11**.

### 6. `try-with-resources` — a forma moderna (chave da Q10)

O `try-with-resources` (Java 7+) declara recursos que implementam `AutoCloseable` e os fecha **automaticamente** ao final do bloco — mesmo que ocorra exceção.

```java
try (BufferedWriter writer = new BufferedWriter(new FileWriter("produtos.txt"))) {
    writer.write("Produto 1;10.0");
} catch (IOException e) {
    System.out.println("Erro: " + e.getMessage());
}
```

> 💡 **Não** é necessário `finally` para fechar o recurso — isso é feito pela JVM. O `catch` continua opcional.

### 7. Leitura com `Scanner`

```java
try (Scanner scanner = new Scanner(new File("dados.txt"))) {
    while (scanner.hasNextLine()) {
        System.out.println(scanner.nextLine());
    }
} catch (FileNotFoundException e) {
    System.out.println("Arquivo não encontrado: " + e.getMessage());
}
```

### 8. O pacote moderno `java.nio.file` (Java 7+)

```java
String conteudo = Files.readString(Paths.get("dados.txt"));
```

- `Files`: operações de leitura/escrita e atributos;
- `Paths`: criação de caminhos.

### 9. Dados estruturados (CSV) e reconstrução de objetos

Padrão usado nos projetos: **gravar** com separador e **reconstruir** objetos ao ler com `split`.

```java
public class Produto {
    private int id;
    private String nome;
    private double preco;

    public Produto(int id, String nome, double preco) {
        this.id = id; this.nome = nome; this.preco = preco;
    }

    public String toFileString() {
        return id + ";" + nome + ";" + preco;   // grava no arquivo
    }

    public static Produto fromFileString(String linha) {
        String[] partes = linha.split(";");       // lê do arquivo
        return new Produto(Integer.parseInt(partes[0]),
                           partes[1],
                           Double.parseDouble(partes[2]));
    }
}
```

> 💡 **Lembre para a Q9:** `split(";")` produz um array; `campos[0]` é o primeiro campo, `campos[1]` o segundo, etc.

### 10. Boas práticas

1. **Sempre** use `try-with-resources` (evita vazamento de recursos);
2. **Trate** `IOException`/`FileNotFoundException`;
3. Use `FileWriter(..., true)` para **acrescentar** (append);
4. Use `BufferedReader`/`BufferedWriter` para **desempenho** em arquivos grandes;
5. **Valide** os dados lidos antes de usá-los (ex.: `NumberFormatException` no `parseInt`);
6. Prefira formatos estruturados: CSV, JSON, XML.

---

## ✍️ Questões da Prova Resolvidas

### Questão 9 — Leitura de CSV com `BufferedReader` (5 pts)

**ENUNCIADO:** Considerando que "produtos.csv" contém `1;Notebook;2500.00` e `2;Mouse;50.00`, qual será a saída?

```java
try (BufferedReader reader = new BufferedReader(new FileReader("produtos.csv"))) {
    String linha;
    while ((linha = reader.readLine()) != null) {
        String[] campos = linha.split(";");
        System.out.println(campos[0] + " - " + campos[1]);
    }
} catch (IOException e) {
    System.out.println("Erro: " + e.getMessage());
}
```

A) **`1 - Notebook` e `2 - Mouse`**  ✅  
B) `Notebook - 2500.00` e `Mouse - 50.00`  
C) `1 - Notebook - 2500.00` e `2 - Mouse - 50.00`  
D) `1;Notebook` e `2;Mouse`  
E) O programa lança exceção  

**PASSO A PASSO:**
1. `readLine()` retorna cada linha: `"1;Notebook;2500.00"` e `"2;Mouse;50.00"`;
2. `split(";")` divide em: `["1", "Notebook", "2500.00"]` e `["2", "Mouse", "50.00"]`;
3. Imprime `campos[0] + " - " + campos[1]` → `1 - Notebook` e `2 - Mouse`;
4. `try-with-resources` + `catch` → nenhuma exceção é lançada.

**GABARITO: letra A.**

---

### Questão 10 — `try-with-resources` (5 pts)

**ENUNCIADO:** Qual afirmação sobre o `try-with-resources` está **CORRETA**?

```java
public static void escreverArquivo(String dados) {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter("dados.txt"))) {
        writer.write(dados);
    } catch (IOException e) {
        System.out.println("Erro: " + e.getMessage());
    }
}
```

A) **O `BufferedWriter` será fechado automaticamente ao final do `try`**  ✅  
B) É obrigatório usar um `finally` para fechar o `BufferedWriter`  
C) O `catch` não pode ser usado com `try-with-resources`  
D) O `BufferedWriter` precisa ser declarado antes do `try`  
E) Apenas recursos que lançam `IOException` podem ser usados  

**PASSO A PASSO:**
1. Recursos declarados **dentro** dos parênteses do `try` implementam `AutoCloseable` e são fechados pela JVM (A ✅);
2. B: ❌ o `finally` explícito **não** é necessário;
3. C: ❌ `catch` é **opcional** e pode coexistir;
4. D: ❌ a declaração é feita **dentro** do `try`;
5. E: ❌ qualquer recurso `AutoCloseable` serve (não só os de IO).

**GABARITO: letra A.**

---

### Questão 11 — `FileWriter` modo append (5 pts)

**ENUNCIADO:** Qual construtor de `FileWriter` deve ser usado para **adicionar** conteúdo ao final de um arquivo existente sem apagar os dados anteriores?

A) `new FileWriter("log.txt")`  
B) **`new FileWriter("log.txt", true)`**  ✅  
C) `new FileWriter("log.txt", "append")`  
D) `new FileWriter("log.txt", false)`  
E) `new FileWriter("log.txt", 1024)`  

**PASSO A PASSO:**
1. A assinatura é `FileWriter(String nome, boolean append)`;
2. `append = true` → **acrescenta** ao final;
3. `false` (ou sem o parâmetro, como em A) → **sobrescreve**;
4. C e E são inválidos (parâmetros de tipo errado).

**GABARITO: letra B.**

---

## 🧪 Exercícios de Fixação

**Exercício 1.** Qual método de `BufferedReader` lê uma linha inteira e retorna `null` no fim do arquivo?
**R:** `readLine()`

**Exercício 2.** Para não perder o conteúdo de "log.txt" ao gravar novos logs, qual código usar?
```java
new BufferedWriter(new FileWriter("log.txt", true));
```
**R:** Verdadeiro / true -> modo append.

**Exercício 3.** Dada a linha `"10;Notebook;2500.00"`, quais são `partes[0]`, `partes[1]` e `partes[2]` após `linha.split(";")`?
**R:** `"10"`, `"Notebook"`, `"2500.00"`

**Exercício 4.** Quais exceções (checked) são típicas ao manipular arquivos?
**R:** `IOException` e `FileNotFoundException`

**Exercício 5.** Qual interface os recursos de arquivo implementam para funcionar com `try-with-resources`?
**R:** `AutoCloseable` (e `Closeable`, que a estende)

---

## ✅ Checklist de autoavaliação

- [ ] Sei ler arquivos linha a linha com `BufferedReader` (`readLine()` retorna `null` no fim)
- [ ] Sei usar `split(";")` para processar campos de um CSV
- [ ] Sei que `try-with-resources` fecha recursos automaticamente (sem `finally`)
- [ ] Sei usar `FileWriter("arquivo", true)` para append
- [ ] Sei gravar objetos no formato estruturado e reconstruí-los ao ler
- [ ] Sei tratar `IOException`/`FileNotFoundException`