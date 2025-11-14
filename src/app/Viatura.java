package app;

public class Viatura {

    private String matricula;
    private Modelo modelo;
    private Estacao estacao;

    public Viatura(String matricula, Modelo modelo, Estacao estacao) {

        this.matricula = matricula;
        this.modelo = modelo;
        this.estacao = estacao;
    }

    public String getMatricula() {
        return matricula;
    }

    public Modelo getModelo() {
        return modelo;
    }

    public Estacao getEstacao() {
        return estacao;
    }

    @Override
    public String toString() {
        return "Viatura [matricula=" + matricula + ", modelo=" + modelo.getModelo() + ", estacao=" + estacao.getNome() + "]";
    }

    public Modelo getModelo() {
        return modelo;
    }

    public Estacao getEstacao() {
        return estacao;
    }

}
