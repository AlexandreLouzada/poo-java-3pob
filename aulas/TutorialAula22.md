# Tutorial de Java — Aula 22: Persistência de Dados Relacionais com JDBC e o Padrão DAO

| | |
|---|---|
| **Disciplina** | Programação Orientada a Objetos |
| **Curso** | Análise e Desenvolvimento de Sistemas (3º Período) |
| **Tema** | Java Database Connectivity (JDBC), Prevenção de SQL Injection com `PreparedStatement`, Mapeamento Objeto-Relacional Manual via `ResultSet`, Padrão Arquitetural DAO (*Data Access Object*) e Gerenciamento Transacional Atômico (`commit`/`rollback`) |
| **Carga Horária** | 100 minutos (2 horas-aula) |
| **Professor Responsável** | Alexandre Neves Louzada |
| **Arquivo de Referência** | `TutorialAula22.md` |

## 1. Objetivos de Aprendizagem

- **Conceitual:** Compreender a camada de persistência em aplicações corporativas; entender a arquitetura do driver JDBC como ponte entre o bytecode Java e o motor do Sistema Gerenciador de Banco de Dados Relacional (SGBD); assimilar o mecanismo de ataque por SQL Injection e sua contenção definitiva via pré-compilação de instruções SQL.
- **Técnico:** Estabelecer conexões de banco de dados via `java.sql.Connection` e `DriverManager`; executar comandos DDL/DML utilizando `PreparedStatement`; iterar sobre cursores de dados tabulares com `ResultSet`, convertendo registros relacionais em instâncias de objetos no Heap; recuperar chaves primárias geradas automaticamente (*Auto-Increment / Serial*) através da flag `RETURN_GENERATED_KEYS`.
- **Arquitetural:** Implementar o padrão DAO (*Data Access Object*), isolando a camada de acesso a dados da lógica de negócio e da interface com o usuário; controlar transações ACID manualmente, desativando o modo de confirmação automática (`setAutoCommit(false)`) e orquestrando comandos atômicos com `commit()` e `rollback()` em blocos defensivos.
- **Prático:** Construir uma solução corporativa completa para controle financeiro de contas e transferências bancárias, operando sobre um banco de dados relacional (H2 Database ou PostgreSQL) com suporte a reversão automática em caso de inconsistência de saldo.

## 2. Fundamentação Teórica

### A Arquitetura do Java Database Connectivity (JDBC)

O JDBC é uma API padrão do ecossistema Java (`java.sql`) que define um conjunto de interfaces abstratas para comunicação uniforme com bancos de dados relacionais:

> Aplicação Java → Interfaces java.sql → Driver JDBC (PostgreSQL, MySQL, H2) → Protocolo TCP/IP → SGBD Relacional

- **DriverManager:** Fábrica estática responsável por carregar o driver e estabelecer a conexão através de uma URL JDBC (ex.: `jdbc:h2:mem:banco_teste` ou `jdbc:postgresql://localhost:5432/producao`).
- **Connection:** Representa a sessão de comunicação aberta com o banco de dados. É o recurso que gerencia transações e cria instruções SQL.
- **PreparedStatement:** Representa uma instrução SQL pré-compilada no banco. Aceita parâmetros dinâmicos de entrada por meio de marcadores posicionais (`?`).
- **ResultSet:** Cursor de dados que aponta para as linhas retornadas por uma consulta (SELECT). Avança linha a linha através do método `.next()`.

### A Ameaça de SQL Injection e a Blindagem com PreparedStatement

O ataque por injeção de SQL ocorre quando dados de entrada fornecidos pelo usuário são concatenados diretamente em comandos SQL textuais com a interface legada `Statement`:

```java
// VULNERABILIDADE CRÍTICA (Concatenação de Strings com Statement):
String sql = "SELECT * FROM usuarios WHERE login = '" + login + "' AND senha = '" + senha + "'";
```

Se um usuário mal-intencionado fornecer no campo de login o texto: `' OR '1'='1`, a instrução enviada ao SGBD torna a cláusula `WHERE` universalmente verdadeira, permitindo acesso não autorizado sem checar a senha.

A interface `PreparedStatement` elimina esse risco em nível estrutural:

```java
// BLINDAGEM CORPORATIVA (Instrução Pré-Compilada com Marcadores ?):
String sql = "SELECT * FROM usuarios WHERE login = ? AND senha = ?";
PreparedStatement stmt = conn.prepareStatement(sql);
stmt.setString(1, login); // O driver trata as aspas e caracteres especiais como dado puro
stmt.setString(2, senha);
```

O SGBD compila a árvore sintática do comando antes de receber os valores. Qualquer comando SQL malicioso inserido no parâmetro será tratado como uma cadeia de texto literal inofensiva, impossibilitando a alteração da lógica da consulta.

### O Padrão Arquitetural DAO (Data Access Object)

Para evitar que comandos SQL e tratamentos de conexão fiquem espalhados por classes de serviço ou controladores de tela, aplica-se o padrão DAO:

```plaintext
┌──────────────────────────────┐
│  Camada de Serviço / Negócio │ (Não conhece nada de SQL, tabelas ou conexões)
└──────────────┬───────────────┘
               │ Usa a interface
               ▼
┌──────────────────────────────┐
│     ContaDAO (Interface)     │ (Contrato público das operações de persistência)
└──────────────┬───────────────┘
               │ Implementada por
               ▼
┌──────────────────────────────┐
│    ContaDaoJdbc (Classe)     │ (Centraliza conexões, PreparedStatements e ResultSets)
└──────────────┬───────────────┘
               │ Executa SQL
               ▼
┌──────────────────────────────┐
│       Banco de Dados         │
└──────────────────────────────┘
```

- **Entidade de Domínio:** Representa os dados e comportamentos de negócio puros (ex.: `ContaBancaria`).
- **Interface DAO:** Declara os contratos de operações CRUD (`salvar`, `buscarPorId`, `atualizar`, `excluir`).
- **Implementação Concreta:** Constrói as instruções SQL, injeta os parâmetros e mapeia o `ResultSet` em objetos de domínio.

### Controle Transacional ACID: Autocommit vs. Modo Transacional Manual

Por padrão, as conexões JDBC operam em modo *Auto-Commit* ativado (`conn.getAutoCommit() == true`), onde cada instrução DML (INSERT, UPDATE, DELETE) é confirmada individualmente e gravada no disco de forma irreversível.

Em operações complexas de negócio (como transferências bancárias, onde um débito e um crédito devem ocorrer juntos), o *Auto-Commit* gera falhas graves: se a energia cair ou ocorrer um erro logo após o débito, o crédito não será efetuado e o dinheiro desaparecerá.

Para garantir atomicidade (o **A** do princípio ACID), assume-se o controle transacional manualmente:

```java
try {
    conn.setAutoCommit(false); // 1. Abre a transação controlada

    // 2. Executa múltiplos comandos relacionados
    stmtDebito.executeUpdate();
    stmtCredito.executeUpdate();

    conn.commit(); // 3. Se todos passarem, confirma e grava as alterações no banco
} catch (SQLException ex) {
    conn.rollback(); // 4. Se qualquer erro ocorrer, reverte 100% das alterações!
    throw ex;
} finally {
    conn.setAutoCommit(true); // Restaura o estado padrão da conexão
}
```

## 3. Estudo de Caso Integrado: Sistema Bancário com JDBC e Padrão DAO

O projeto abaixo implementa uma solução corporativa completa para persistência de contas e processamento atômico de transferências utilizando o banco relacional H2 em memória:

```java
package br.edu.universidade.sistema.persistencia.dominio;

// 1. Entidade de Domínio
public class ContaBancaria {
    private Long id;
    private final String numeroConta;
    private final String titular;
    private double saldo;

    public ContaBancaria(Long id, String numeroConta, String titular, double saldo) {
        this.id = id;
        this.numeroConta = numeroConta;
        this.titular = titular;
        this.saldo = saldo;
    }

    public ContaBancaria(String numeroConta, String titular, double saldoInicial) {
        this(null, numeroConta, titular, saldoInicial);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNumeroConta() { return numeroConta; }
    public String getTitular() { return titular; }
    public double getSaldo() { return saldo; }

    public void debitar(double valor) {
        if (valor <= 0 || valor > this.saldo) {
            throw new IllegalArgumentException("Saldo insuficiente ou valor de débito inválido.");
        }
        this.saldo -= valor;
    }

    public void creditar(double valor) {
        if (valor <= 0) {
            throw new IllegalArgumentException("Valor de crédito inválido.");
        }
        this.saldo += valor;
    }

    @Override
    public String toString() {
        return String.format("[ID: %d] Conta: %-8s | Titular: %-15s | Saldo: R$ %8.2f",
                id, numeroConta, titular, saldo);
    }
}
```

```java
package br.edu.universidade.sistema.persistencia.dao;

import br.edu.universidade.sistema.persistencia.dominio.ContaBancaria;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

// 2. Contrato da Camada de Acesso a Dados (DAO Interface)
public interface ContaDAO {
    void salvar(ContaBancaria conta) throws SQLException;
    Optional<ContaBancaria> buscarPorId(Long id) throws SQLException;
    Optional<ContaBancaria> buscarPorNumero(String numero) throws SQLException;
    void atualizarSaldo(ContaBancaria conta) throws SQLException;
    List<ContaBancaria> listarTodas() throws SQLException;
}
```

```java
package br.edu.universidade.sistema.persistencia.dao;

import br.edu.universidade.sistema.persistencia.dominio.ContaBancaria;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// 3. Implementação Concreta do DAO via JDBC Nativo
public class ContaDaoJdbc implements ContaDAO {

    private final Connection conexao;

    public ContaDaoJdbc(Connection conexao) {
        this.conexao = conexao;
    }

    @Override
    public void salvar(ContaBancaria conta) throws SQLException {
        String sql = "INSERT INTO contas (numero_conta, titular, saldo) VALUES (?, ?, ?)";

        // RETURN_GENERATED_KEYS captura a chave primária auto-incrementada gerada pelo SGBD
        try (PreparedStatement stmt = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, conta.getNumeroConta());
            stmt.setString(2, conta.getTitular());
            stmt.setDouble(3, conta.getSaldo());
            stmt.executeUpdate();

            try (ResultSet chaves = stmt.getGeneratedKeys()) {
                if (chaves.next()) {
                    conta.setId(chaves.getLong(1)); // Atualiza o objeto no Heap com o ID do banco
                }
            }
        }
    }

    @Override
    public Optional<ContaBancaria> buscarPorId(Long id) throws SQLException {
        String sql = "SELECT id, numero_conta, titular, saldo FROM contas WHERE id = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setLong(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearLinhaParaEntidade(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<ContaBancaria> buscarPorNumero(String numero) throws SQLException {
        String sql = "SELECT id, numero_conta, titular, saldo FROM contas WHERE numero_conta = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setString(1, numero);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearLinhaParaEntidade(rs));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public void atualizarSaldo(ContaBancaria conta) throws SQLException {
        String sql = "UPDATE contas SET saldo = ? WHERE id = ?";

        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setDouble(1, conta.getSaldo());
            stmt.setLong(2, conta.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public List<ContaBancaria> listarTodas() throws SQLException {
        String sql = "SELECT id, numero_conta, titular, saldo FROM contas ORDER BY id";
        List<ContaBancaria> contas = new ArrayList<>();

        try (PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                contas.add(mapearLinhaParaEntidade(rs));
            }
        }
        return contas;
    }

    // Método utilitário privado de mapeamento Objeto-Relacional
    private ContaBancaria mapearLinhaParaEntidade(ResultSet rs) throws SQLException {
        return new ContaBancaria(
                rs.getLong("id"),
                rs.getString("numero_conta"),
                rs.getString("titular"),
                rs.getDouble("saldo")
        );
    }
}
```

```java
package br.edu.universidade.sistema.persistencia.service;

import br.edu.universidade.sistema.persistencia.dao.ContaDAO;
import br.edu.universidade.sistema.persistencia.dominio.ContaBancaria;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.NoSuchElementException;

// 4. Camada de Serviço Orquestradora de Transações
public class TransferenciaService {

    private final Connection conexao;
    private final ContaDAO contaDAO;

    public TransferenciaService(Connection conexao, ContaDAO contaDAO) {
        this.conexao = conexao;
        this.contaDAO = contaDAO;
    }

    public void transferir(String numContaOrigem, String numContaDestino, double valor) throws SQLException {
        // Desativa autocommit para iniciar a transação ACID manual
        conexao.setAutoCommit(false);

        try {
            ContaBancaria origem = contaDAO.buscarPorNumero(numContaOrigem)
                    .orElseThrow(() -> new NoSuchElementException("Conta de origem não encontrada: " + numContaOrigem));

            ContaBancaria destino = contaDAO.buscarPorNumero(numContaDestino)
                    .orElseThrow(() -> new NoSuchElementException("Conta de destino não encontrada: " + numContaDestino));

            // Executa as regras de negócio nas entidades
            origem.debitar(valor);
            destino.creditar(valor);

            // Persiste o estado atualizado de ambos os registros no banco
            contaDAO.atualizarSaldo(origem);
            contaDAO.atualizarSaldo(destino);

            // Confirma todas as operações atomicamente
            conexao.commit();
            System.out.printf("[TRANSAÇÃO CONCLUÍDA] R$ %.2f transferidos com sucesso de %s para %s.%n",
                    valor, numContaOrigem, numContaDestino);

        } catch (Exception ex) {
            // Em qualquer anomalia técnica ou violação de regra, reverte completamente
            conexao.rollback();
            System.err.printf("[TRANSAÇÃO REVERTIDA - ROLLBACK] Falha na transferência: %s%n", ex.getMessage());
            throw ex;
        } finally {
            // Restaura o autocommit para não interferir em outras operações
            conexao.setAutoCommit(true);
        }
    }
}
```

```java
package br.edu.universidade.sistema.persistencia;

import br.edu.universidade.sistema.persistencia.dao.ContaDaoJdbc;
import br.edu.universidade.sistema.persistencia.dominio.ContaBancaria;
import br.edu.universidade.sistema.persistencia.service.TransferenciaService;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

// 5. Aplicação Executável demonstrando o ciclo completo de persistência e rollback
public class PersistenciaJdbcApp {

    // Configuração de banco H2 em memória
    private static final String URL = "jdbc:h2:mem:sistemabancario;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    public static void main(String[] args) {
        System.out.println("========== PERSISTÊNCIA RELACIONAL COM JDBC E DAO ==========");

        try (Connection conexao = DriverManager.getConnection(URL, USER, PASSWORD)) {
            inicializarTabelas(conexao);

            ContaDaoJdbc contaDAO = new ContaDaoJdbc(conexao);
            TransferenciaService service = new TransferenciaService(conexao, contaDAO);

            System.out.println("\n--- 1. Inserção de Contas com Chave Auto-Incremento ---");
            ContaBancaria c1 = new ContaBancaria("001-A", "Beatriz Costa", 1000.00);
            ContaBancaria c2 = new ContaBancaria("002-B", "Carlos Eduardo", 300.00);

            contaDAO.salvar(c1);
            contaDAO.salvar(c2);

            System.out.println("Contas persistidas no banco:");
            contaDAO.listarTodas().forEach(System.out::println);

            System.out.println("\n--- 2. Execução de Transferência Válida (Commit) ---");
            service.transferir("001-A", "002-B", 400.00);

            System.out.println("Saldos pós-transferência:");
            contaDAO.listarTodas().forEach(System.out::println);

            System.out.println("\n--- 3. Tentativa de Transferência Inválida (Disparo de Rollback) ---");
            try {
                // Tentativa de transferir mais do que o saldo atual de Beatriz (Saldo: R$ 600.00)
                service.transferir("001-A", "002-B", 900.00);
            } catch (Exception e) {
                System.out.println("Exceção capturada pela aplicação com sucesso.");
            }

            System.out.println("\nSaldos após o Rollback (Comprovação da integridade dos valores):");
            contaDAO.listarTodas().forEach(System.out::println);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("\n============================================================");
    }

    private static void inicializarTabelas(Connection conn) throws Exception {
        String ddl = """
            CREATE TABLE contas (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                numero_conta VARCHAR(20) NOT NULL UNIQUE,
                titular VARCHAR(100) NOT NULL,
                saldo DOUBLE NOT NULL
            );
            """;
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(ddl);
        }
    }
}
```

## 4. Diagnóstico de Erros Comuns e Armadilhas

### Armadilha 1: Vazamento de Conexões e Descritores de Banco (Connection Leaks)

**Código Problemático:**

```java
Connection conn = DriverManager.getConnection(url, user, pass);
PreparedStatement stmt = conn.prepareStatement(sql);
ResultSet rs = stmt.executeQuery();
// Se o método lançar exceção ou esquecer de fechar, a conexão fica presa no SGBD!
```

- **Diagnóstico Técnico:** Bancos de dados possuem um teto máximo de conexões simultâneas (ex.: 100 conexões). Se as instâncias não forem fechadas, a aplicação atinge rapidamente o erro: *Too many connections* ou *Connection pool exhausted*, travando todo o sistema corporativo.
- **Correção:** Declare conexões, comandos e conjuntos de resultados estritamente dentro de blocos `try-with-resources`.

### Armadilha 2: Omissão de rs.next() Antes de Ler o Primeiro Registro

**Código Problemático:**

```java
ResultSet rs = stmt.executeQuery();
String titular = rs.getString("titular"); // ERRO DE EXECUÇÃO!
```

- **Diagnóstico da JVM:** `java.sql.SQLException: Before start of result set`.
- **Causa & Correção:** Ao ser instanciado, o cursor do `ResultSet` fica posicionado antes da primeira linha de dados. É mandatório invocar `.next()` dentro de um `if` ou `while` para avançar o cursor para a primeira linha válida antes de tentar recuperar valores.

### Armadilha 3: Erros em Transações Manuais por Não Restaurar o setAutoCommit(true)

**Código Problemático:**

```java
conn.setAutoCommit(false);
// Realiza transação com commit() ou rollback()...
// Não reativa o setAutoCommit(true)!
```

- **Diagnóstico Técnico:** Se a conexão retornar para um pool de conexões (como HikariCP) com `autoCommit == false`, as próximas consultas e atualizações simples executadas por outras rotinas do sistema não serão gravadas no banco silenciosamente.
- **Regra de Ouro:** Restaure sempre o modo automático dentro de um bloco `finally`.

## 5. Roteiro Prático de Depuração: Inspecionando a Transação e o Rollback na IDE

Para auditar o momento exato em que a reversão transacional protege os dados na sua IDE (IntelliJ IDEA, Eclipse ou VS Code):

1. No método `transferir` da classe `TransferenciaService`, coloque dois pontos de interrupção (*breakpoints*):
   - O primeiro na linha `conexao.commit();`
   - O segundo dentro do bloco `catch` na linha `conexao.rollback();`
2. Inicie a execução da aplicação em modo de depuração (*Debug*).
3. **Na primeira execução (transferência válida de R$ 400,00):**
   - O fluxo pausará no *breakpoint* do `commit()`.
   - Abra a aba *Evaluate Expression* (Alt + F8) e execute uma query de consulta: observe que as alterações ainda estão na área temporária de transação. Ao passar pela linha do `commit()`, o SGBD consolida as duas tabelas atomicamente.
4. **Na segunda execução (transferência com saldo insuficiente de R$ 900,00):**
   - O método `origem.debitar(valor)` lança a `IllegalArgumentException`.
   - O depurador desvia imediatamente o fluxo para o bloco `catch`, parando na linha do `rollback()`.
   - Execute o `rollback()` e comprove no painel de saída que nenhuma linha foi alterada no banco, atestando a integridade das invariantes financeiras.

## 6. Exercício de Fixação Prática: Módulo de Cadastro e Movimentação de Ativos Fiscais

Implemente um motor de controle patrimonial corporativo aplicando JDBC nativo e o padrão DAO:

1. **Construa a Tabela e Entidade `AtivoPatrimonial`:**
   - Tabela SQL: `ativos (id BIGINT AUTO_INCREMENT PRIMARY KEY, tombo VARCHAR(20) UNIQUE, descricao VARCHAR(100), valor_aquisicao DOUBLE, status VARCHAR(20))`
   - Atributos privados na classe: `id` (`Long`), `tombo` (`String`), `descricao` (`String`), `valorAquisicao` (`double`) e `status` (`String` — `"ATIVO"`, `"BAIXADO"`).
   - Construtores com regras de guarda, getters/setters e método descritivo `toString()`.

2. **Construa a Interface e Implementação `AtivoDAO`:**
   - **Método `void salvar(AtivoPatrimonial ativo)`:** executa INSERT com captura de chaves geradas via `RETURN_GENERATED_KEYS`.
   - **Método `Optional<AtivoPatrimonial> buscarPorTombo(String tombo)`:** consulta por código de tombo via `PreparedStatement`.
   - **Método `void atualizarStatus(Long id, String novoStatus)`:** executa UPDATE para alterar o status operacional.
   - **Método `List<AtivoPatrimonial> listarAtivosEmOperacao()`:** executa SELECT filtrando `WHERE status = 'ATIVO'`.

3. **Construa a Classe Executável `PatrimonioApp`:**
   - Estabeleça a conexão com o banco H2 em memória via `DriverManager`.
   - Crie a estrutura física da tabela via `Statement.execute(...)`.
   - Instancie o DAO e cadastre três ativos (ex.: computadores, servidores e monitores).
   - Altere o status de um dos ativos para `"BAIXADO"`.
   - Execute a listagem e comprove no console que apenas os ativos com status `"ATIVO"` são recuperados pelo cursor do `ResultSet`.