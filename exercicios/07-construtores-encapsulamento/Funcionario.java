import java.util.Locale;

class Funcionario {
    private String nome;
    private final String matricula;
    private double salario;

    public Funcionario(String nome, String matricula, double salario) {
        this.nome = nome;
        this.matricula = matricula;
        this.salario = (salario > 0) ? salario : 0.0;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMatricula() {
        return matricula;
    }

    public double getSalario() {
        return salario;
    }

    public void setSalario(double novoSalario) {
        if (novoSalario > this.salario) {
            this.salario = novoSalario;
            System.out.printf("Salário atualizado com sucesso para R$ %.2f.%n", this.salario);
        } else {
            System.out.printf("Erro: O novo salário (R$ %.2f) deve ser maior que o salário atual (R$ %.2f). Alteração recusada.%n",
                    novoSalario, this.salario);
        }
    }

    public void exibirDados() {
        System.out.printf("Matrícula: %s | Funcionário: %s | Salário: R$ %.2f%n", matricula, nome, salario);
    }
}
