import java.sql.*;
import java.util.ArrayList;
import java.util.List;

class Aluno {
    private Long id;
    private String nome;
    private String matricula;

    public Aluno(String nome, String matricula) {
        this.nome = nome;
        this.matricula = matricula;
    }

    public Aluno(Long id, String nome, String matricula) {
        this.id = id;
        this.nome = nome;
        this.matricula = matricula;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public String getMatricula() { return matricula; }

    @Override
    public String toString() {
        return String.format("Aluno [ID=%d, Nome='%s', Matrícula='%s']", id, nome, matricula);
    }
}
