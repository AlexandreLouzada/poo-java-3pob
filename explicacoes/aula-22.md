# Explicação da Aula 22 — Persistência de Dados Relacionais com JDBC e o Padrão DAO

| | |
|---|---|
| **Disciplina** | Programação Orientada a Objetos |
| **Curso** | Análise e Desenvolvimento de Sistemas (3º Período) |
| **Tema** | Java Database Connectivity (JDBC), Prevenção de SQL Injection com `PreparedStatement`, Mapeamento Objeto-Relacional Manual via `ResultSet`, Padrão Arquitetural DAO (*Data Access Object*) e Gerenciamento Transacional Atômico (`commit`/`rollback`) |
| **Carga Horária** | 100 minutos (2 horas-aula) |
| **Professor** | Alexandre Neves Louzada |
| **Tutorial** | `aulas/TutorialAula22.md` |
| **Estudo de Caso** | `exemplos/aula-22/` |
| **Exercícios Resolvidos** | `solucoes/aula-22/` |

## 1. Objetivos de Aprendizagem

- **Conceitual:** Compreender a camada de persistência em aplicações corporativas; entender a arquitetura do driver JDBC como ponte entre o bytecode Java e o motor do SGBD; assimilar o mecanismo de ataque por SQL Injection e sua contenção definitiva via pré-compilação de instruções SQL.
- **Técnico:** Estabelecer conexões via `java.sql.Connection` e `DriverManager`; executar comandos DDL/DML utilizando `PreparedStatement`; iterar sobre cursores tabulares com `ResultSet`, convertendo registros relacionais em objetos no Heap; recuperar chaves primárias geradas automaticamente via flag `RETURN_GENERATED_KEYS`.
- **Arquitetural:** Implementar o padrão DAO (*Data Access Object*), isolando a camada de acesso a dados da lógica de negócio; controlar transações ACID manualmente com `setAutoCommit(false)`, `commit()` e `rollback()`.
- **Prático:** Construir uma solução corporativa completa para controle financeiro de contas e transferências bancárias, operando sobre um banco relacional (H2 Database ou PostgreSQL) com suporte a reversão automática em caso de inconsistência de saldo.

## 2. Conteúdo Teórico Detalhado

### 2.1 A Arquitetura do Java Database Connectivity (JDBC)

O JDBC é uma API padrão do ecossistema Java (`java.sql`) que define interfaces abstratas para comunicação uniforme com bancos de dados relacionais:

- **DriverManager:** Fábrica estática responsável por carregar o driver e estabelecer a conexão através de uma URL JDBC (ex.: `jdbc:h2:mem:banco_teste` ou `jdbc:postgresql://localhost:5432/producao`).
- **Connection:** Representa a sessão aberta com o banco. Gerencia transações e cria instruções SQL.
- **PreparedStatement:** Instrução SQL pré-compilada no banco. Aceita parâmetros dinâmicos por marcadores posicionais (`?`).
- **ResultSet:** Cursor de dados que aponta para as linhas retornadas por um SELECT. Avança linha a linha via `.next()`.

```
Aplicação Java -> Interfaces java.sql -> Driver JDBC -> Protocolo TCP/IP -> SGBD Relacional
```

### 2.2 SQL Injection e a Blindagem com PreparedStatement

O ataque por injeção de SQL ocorre quando dados de entrada do usuário são concatenados diretamente em comandos SQL com a interface legada `Statement`:

```java
// VULNERABILIDADE CRÍTICA (Concatenação de Strings com Statement):
String sql = "SELECT * FROM usuarios WHERE login = '" + login + "' AND senha = '" + senha + "'";
```

Se o usuário fornecer no campo de login o texto `' OR '1'='1`, a cláusula `WHERE` torna-se universalmente verdadeira, permitindo acesso não autorizado.

A interface `PreparedStatement` elimina esse risco em nível estrutural:

```java
// BLINDAGEM CORPORATIVA (Instrução Pré-Compilada com Marcadores ?):
String sql = "SELECT * FROM usuarios WHERE login = ? AND senha = ?";
PreparedStatement stmt = conn.prepareStatement(sql);
stmt.setString(1, login); // O driver trata aspas e caracteres especiais como dado puro
stmt.setString(2, senha);
```

O SGBD compila a árvore sintática do comando antes de receber os valores. Qualquer comando SQL malicioso inserido no parâmetro será tratado como texto literal inofensivo.

### 2.3 O Padrão Arquitetural DAO (Data Access Object)

Para evitar que comandos SQL fiquem espalhados por classes de serviço, aplica-se o DAO:

- **Entidade de Domínio:** Representa dados e comportamentos de negócio puros (ex.: `ContaBancaria`).
- **Interface DAO:** Declara os contratos de operações CRUD (`salvar`, `buscarPorId`, `atualizar`, `excluir`).
- **Implementação Concreta:** Constrói as instruções SQL, injeta parâmetros e mapeia `ResultSet` em objetos de domínio.

A camada de serviço/negócio não conhece nada de SQL; ela apenas usa a interface DAO, que é implementada por classes como `ContaDaoJdbc`.

### 2.4 Controle Transacional ACID: Autocommit vs. Modo Manual

Por padrão, conexões JDBC operam em modo Auto-Commit, onde cada DML é confirmada individualmente. Em operações complexas (transferências bancárias, onde débito e crédito devem ocorrer juntos), o Auto-Commit gera falhas graves.

Para garantir atomicidade (o A do ACID), assume-se o controle manual:

```java
try {
    conn.setAutoCommit(false); // 1. Abre a transação controlada
    stmtDebito.executeUpdate();    // 2. Executa múltiplos comandos
    stmtCredito.executeUpdate();
    conn.commit();                 // 3. Confirma tudo atomicamente
} catch (SQLException ex) {
    conn.rollback();               // 4. Reverte 100% das alterações
    throw ex;
} finally {
    conn.setAutoCommit(true);      // Restaura o estado padrão
}
```

### 2.5 Diagnóstico de Erros Comuns e Armadilhas

**Armadilha 1: Vazamento de Conexões e Descritores de Banco (Connection Leaks)**

```java
Connection conn = DriverManager.getConnection(url, user, pass);
PreparedStatement stmt = conn.prepareStatement(sql);
ResultSet rs = stmt.executeQuery();
// Se o método lançar exceção ou esquecer de fechar, a conexão fica presa no SGBD!
```

Diagnóstico: bancos de dados possuem teto máximo de conexões simultâneas (ex.: 100). Se as instâncias não forem fechadas, a aplicação atinge rapidamente *Too many connections* ou *Connection pool exhausted*, travando todo o sistema. Correção: declarar conexões, comandos e resultados estritamente dentro de blocos `try-with-resources`.

**Armadilha 2: Omissão de rs.next() Antes de Ler o Primeiro Registro**

```java
ResultSet rs = stmt.executeQuery();
String titular = rs.getString("titular"); // ERRO DE EXECUÇÃO!
```

Diagnóstico: `java.sql.SQLException: Before start of result set`. Ao ser instanciado, o cursor do `ResultSet` fica posicionado antes da primeira linha de dados. É mandatório invocar `.next()` dentro de um `if` ou `while` para avançar o cursor antes de recuperar valores.

**Armadilha 3: Erros em Transações Manuais por Não Restaurar o setAutoCommit(true)**

```java
conn.setAutoCommit(false);
// Realiza transação com commit() ou rollback()...
// Não reativa o setAutoCommit(true)!
```

Diagnóstico: se a conexão retornar para um pool (como HikariCP) com `autoCommit == false`, as próximas consultas e atualizações executadas por outras rotinas não serão gravadas no banco silenciosamente. Regra de ouro: restaurar sempre o modo automático dentro de um bloco `finally`.

### 2.6 Roteiro Prático de Depuração na IDE

Para auditar o momento exato da reversão transacional na IDE:

1. No método `transferir` da classe `TransferenciaService`, coloque dois breakpoints: um na linha `conexao.commit();` e outro dentro do `catch` na linha `conexao.rollback();`.
2. Inicie em modo Debug.
3. Na primeira execução (transferência válida de R$ 400,00), o fluxo pausará no `commit()`. Use Evaluate Expression (Alt + F8) para consultar os dados ainda na área temporária da transação; ao passar pelo `commit()`, o SGBD consolida as alterações atomicamente.
4. Na segunda execução (saldo insuficiente de R$ 900,00), o `origem.debitar(valor)` lança `IllegalArgumentException`; o fluxo desvia para o `catch` e para no `rollback()`, comprovando que nenhuma linha foi alterada no banco.

## 3. Estudo de Caso Aplicado

O projeto implementa uma solução completa para persistência de contas e processamento atômico de transferências usando o H2 em memória.

**Estrutura de pacotes (exemplos/aula-22/src):**

```
br.edu.universidade.sistema.persistencia
  |-- dominio/
  |     |-- ContaBancaria.java
  |-- dao/
  |     |-- ContaDAO.java        (interface)
  |     |-- ContaDaoJdbc.java    (implementação JDBC)
  |-- service/
  |     |-- TransferenciaService.java
  |-- PersistenciaJdbcApp.java
```

A entidade `ContaBancaria` encapsula dados e comportamento de negócio com métodos `debitar()` e `creditar()` que validam valores (saldo insuficiente ou valor inválido lançam `IllegalArgumentException`).

A interface `ContaDAO` define o contrato CRUD. A implementação `ContaDaoJdbc` centraliza conexões, `PreparedStatement` e `ResultSet`. O método `salvar` usa a flag `Statement.RETURN_GENERATED_KEYS` para capturar a chave auto-incrementada:

```java
try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
    stmt.setString(1, conta.getNumeroConta());
    stmt.setString(2, conta.getTitular());
    stmt.setDouble(3, conta.getSaldo());
    stmt.executeUpdate();

    try (ResultSet chaves = stmt.getGeneratedKeys()) {
        if (chaves.next()) {
            conta.setId(chaves.getLong(1)); // Atualiza o objeto no Heap
        }
    }
}
```

O `TransferenciaService` orquestra a transação: desativa auto-commit, busca origem e destino, aplica débito/crédito nas entidades, persiste os dois registros com `atualizarSaldo`, confirma com `commit()`; em qualquer falha, reverte com `rollback()` e restaura o autocommit no `finally`.

No `PersistenciaJdbcApp`, conecta-se ao H2, cria a tabela `contas`, insere duas contas, executa uma transferência válida de R$ 400,00 (commit) e uma inválida de R$ 900,00 (rollback), comprovando a integridade dos saldos.

## 4. Exercícios Propostos e Solução

O exercício propõe um motor de controle patrimonial com JDBC nativo e padrão DAO.

**Estrutura da solução (solucoes/aula-22/src):**

```
br.edu.universidade.sistema.patrimonio
  |-- dominio/
  |     |-- AtivoPatrimonial.java
  |-- dao/
  |     |-- AtivoDAO.java       (interface)
  |     |-- AtivoDAOJdbc.java   (implementação)
  |-- PatrimonioApp.java
```

Tabela SQL criada pelo app:

```sql
CREATE TABLE ativos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tombo VARCHAR(20) UNIQUE,
    descricao VARCHAR(100),
    valor_aquisicao DOUBLE,
    status VARCHAR(20)
);
```

A classe `AtivoPatrimonial` valida tombo, descrição, valor de aquisição e status (somente `"ATIVO"` ou `"BAIXADO"`) e fornece getters/setters. A interface `AtivoDAO` declara quatro operações e a implementação `AtivoDAOJdbc` as concretiza:

```java
@Override
public void salvar(AtivoPatrimonial ativo) throws SQLException {
    String sql = "INSERT INTO ativos (tombo, descricao, valor_aquisicao, status) VALUES (?, ?, ?, ?)";
    try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        stmt.setString(1, ativo.getTombo());
        stmt.setString(2, ativo.getDescricao());
        stmt.setDouble(3, ativo.getValorAquisicao());
        stmt.setString(4, ativo.getStatus());
        stmt.executeUpdate();

        try (ResultSet chaves = stmt.getGeneratedKeys()) {
            if (chaves.next()) {
                ativo.setId(chaves.getLong(1));
            }
        }
    }
}

@Override
public Optional<AtivoPatrimonial> buscarPorTombo(String tombo) throws SQLException {
    String sql = "SELECT id, tombo, descricao, valor_aquisicao, status FROM ativos WHERE tombo = ?";
    try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
        stmt.setString(1, tombo);
        try (ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return Optional.of(montarAtivo(rs));
            }
        }
    }
    return Optional.empty();
}

@Override
public List<AtivoPatrimonial> listarAtivosEmOperacao() throws SQLException {
    String sql = "SELECT id, tombo, descricao, valor_aquisicao, status FROM ativos WHERE status = 'ATIVO'";
    List<AtivoPatrimonial> ativos = new ArrayList<>();
    try (PreparedStatement stmt = conexao.prepareStatement(sql);
         ResultSet rs = stmt.executeQuery()) {
        while (rs.next()) {
            ativos.add(montarAtivo(rs));
        }
    }
    return ativos;
}

private AtivoPatrimonial montarAtivo(ResultSet rs) throws SQLException {
    return new AtivoPatrimonial(
            rs.getLong("id"),
            rs.getString("tombo"),
            rs.getString("descricao"),
            rs.getDouble("valor_aquisicao"),
            rs.getString("status")
    );
}
```

Métodos da solução:

- `salvar`: INSERT com `RETURN_GENERATED_KEYS` para capturar a chave gerada.
- `buscarPorTombo`: SELECT com `PreparedStatement` retornando `Optional`.
- `atualizarStatus`: UPDATE de status por ID.
- `listarAtivosEmOperacao`: SELECT com `WHERE status = 'ATIVO'`.
- Método privado `montarAtivo(ResultSet)` faz o mapeamento objeto-relacional.

O `PatrimonioApp` conecta via `DriverManager` (`jdbc:h2:mem:patrimonio;DB_CLOSE_DELAY=-1`), cria a tabela, cadastra três ativos (Computador Desktop, Servidor Rack, Monitor 27 Pol), faz a baixa do servidor para `"BAIXADO"` e lista apenas os ativos em operação. A saída esperada lista 2 ativos com status `ATIVO`, comprovando o filtro do `ResultSet`:

```
Ativos salvos com IDs gerados: 1, 2, 3
--- Busca por Tombo (PreparedStatement) ---
Ativo [ID: 2 | Tombo: TMB-0002 | Descricao: Servidor Rack | Valor: R$ 28500.00 | Status: ATIVO]
--- Baixa do Servidor (UPDATE status = BAIXADO) ---
--- Listagem de Ativos em Operacao (WHERE status = 'ATIVO') ---
Ativo [ID: 1 | Tombo: TMB-0001 | Descricao: Computador Desktop | Valor: R$ 4200.00 | Status: ATIVO]
Ativo [ID: 3 | Tombo: TMB-0003 | Descricao: Monitor 27 Pol | Valor: R$ 1450.00 | Status: ATIVO]
Total de ativos ativos recuperados pelo ResultSet: 2
```

## 5. Perguntas de Revisão

1. Quais são os quatro componentes principais da API JDBC e seus papéis?
2. Explique como `PreparedStatement` previne SQL Injection em relação ao `Statement` concatenado.
3. Qual a finalidade da flag `Statement.RETURN_GENERATED_KEYS`? Como o objeto no Heap é atualizado?
4. O que acontece se o cursor do `ResultSet` for lido sem chamar `.next()` primeiro?
5. Por que o Auto-Commit padrão é perigoso em operações como transferências bancárias?
6. Descreva a sequência `setAutoCommit(false)` -> operações -> `commit()`/`rollback()` -> `setAutoCommit(true)` e a ordem correta de cada bloco.
7. Qual a vantagem do padrão DAO em relação a espalhar SQL pelas classes de serviço?

## 6. Resumo / Pontos-Chave

- **JDBC** é a ponte padrão entre Java e bancos relacionais, baseada em `DriverManager`, `Connection`, `PreparedStatement` e `ResultSet`.
- **`PreparedStatement`** elimina SQL Injection por pré-compilação e marcadores posicionais `?`, tratando entradas do usuário como dados puros.
- O padrão **DAO** isola (entidade/domínio -> interface DAO -> implementação JDBC), mantendo SQL longe das camadas de negócio.
- **`RETURN_GENERATED_KEYS`** captura chaves auto-incrementadas e atualiza o objeto de domínio no Heap.
- **Transações ACAID** manuais garantem atomicidade: `setAutoCommit(false)`, operações, `commit()`, e `rollback()` no `catch`, restaurando `setAutoCommit(true)` no `finally`.
- **`try-with-resources`** é essencial para evitar vazamento de conexões e descritores (Connection leaks).
- O estudo de caso e o exercício consolidam persistência corporativa com débito/crédito atômico e cadastro patrimonial com filtro de status.