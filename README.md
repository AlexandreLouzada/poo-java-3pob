# POO Java — 3POB FAETERJ

Material de referência da disciplina **Programação Orientada a Objetos (Java)** — FAETERJ.

## Estrutura

```
aulas/        Aulas 01 a 18 (Markdown) + Roteiro de Tratamento de Exceções
tutorial/     Tutorial completo de revisão (9 partes, Markdown)
exemplos/     Estudos de caso (seção 3) dos 23 tutoriais, extraídos com pacotes
              preservados, um subdiretório por aula (aula-NN/src + out/ compilado)
solucoes/     Soluções dos exercícios (seção 6) dos 23 tutoriais, um subdiretório
              por aula (aula-NN/src + out/ compilado)
exercicios/   ~90 exercícios resolvidos da Lista 3POB FAETERJ, um arquivo .java por classe,
              organizados por tópico (NN-topico/)
  01-estrutura-sequencial
  02-estruturas-condicionais
  03-estruturas-de-repeticao
  04-vetores-arrays
  05-matrizes
  06-classes-atributos-metodos
  07-construtores-encapsulamento
  08-heranca-polimorfismo
  09-classes-abstratas-interfaces
  10-tratamento-de-excecoes
  11-colecoes-arraylist-hashset-hashmap
  12-streams-lambdas-interfaces-funcionais
  13-manipulacao-de-arquivos
  14-concorrencia-threads
  15-generics-wildcards
  16-testes-unitarios-junit-mockito       (JUnit 5 + Mockito; requer jars — ver pom-exemplo.xml)
  17-jdbc-preparedstatement
  18-padrao-dao-connection-factory
  99-projeto-integrador                   (Projeto Integrador SaaS de Faturamento + testes)
```

## Tutoriais (aulas 01 a 23)

Cada `aulas/TutorialAulaNN.md` traz um estudo de caso (seção 3) e exercícios
(seção 6). O código dos estudos de caso foi extraído para `exemplos/aula-NN/`
(uma aplicação com `main` por aula) e os exercícios têm solução em
`solucoes/aula-NN/`, sempre em arquivos `.java` com pacote. Quando o enunciado
não define pacote, adota-se a convenção `br.edu.universidade.<dominio>`,
seguindo o pacote do estudo de caso da própria aula.

Para compilar tudo (exemplos + soluções) e rodar os testes JUnit da aula 21:

```
powershell -ExecutionPolicy Bypass -File scripts/compilar_tutoriais.ps1
```

O script baixa automaticamente os jars necessários (JUnit 5 e H2) para uma
pasta local `lib/`, se estiverem ausentes. Requisitos especiais:

- Aula 08 (solução): reutiliza `ServicoAuditoriaForense` do estudo de caso —
  compila com `exemplos/aula-08/out` no classpath (feito pelo script).
- Aula 21 (solução): estrutura Maven `src/main/java` + `src/test/java` com
  testes JUnit 5 (`proposta-credito-test`).
- Aula 22: usa JDBC + banco H2 em memória (`h2-2.2.224.jar`).

Para rodar uma aplicação de exemplo ou solução individualmente:

```
java -cp "exemplos/aula-NN/out;<jars se a aula exigir>" <classe.com.Main>
```

## Compilar e executar

Cada tópico é um conjunto independente de arquivos `.java` no pacote padrão.
A partir de um tópico, por exemplo:

```
cd exercicios/01-estrutura-sequencial
javac *.java          # gera os .class na pasta
java NomeDaClasse     # executa o main de cada exercício
```

Os tópicos `16` e `99` usam **JUnit 5 + Mockito**: adicione os jars ao `-cp`
(versões em `16-testes-unitarios-junit-mockito/pom-exemplo.xml`):

```
javac -cp "junit-jupiter-api-5.10.2.jar;mockito-core-5.16.0.jar;h2-2.2.224.jar;..." *.java
```

Para rodar os testes, use o `junit-platform-console-standalone`:

```
java -jar junit-platform-console-standalone-1.10.2.jar execute \
     --class-path "jars...;pasta_dos_class" \
     --scan-class-path
```

Há também o helper `scripts/compilar_todos.ps1` que valida todos os tópicos e
executa os testes de `16` e `99` (baixa os jars automaticamente para uma pasta
`lib/`).

## Notas

- `pom-exemplo.xml` (tópico 16) mostra as dependências Maven equivalentes
  (JUnit Jupiter 5.10.2, Mockito 5.11.0, H2) para quem preferir Maven.
- Código extraído da **Lista 3POB FAETERJ.docx**; as soluções foram separadas
  automaticamente em um arquivo por classe.