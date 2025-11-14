package app;

public class Viatura {

    private Modelo modelo;
    private Estacao estacao;

    public Viatura( Modelo modelo, Estacao estacao) {

        this.modelo = modelo;
        this.estacao = estacao;
    }

    @Override
    public String toString() {
        return "Viatura [modelo=" + modelo.getModelo() + ", estacao=" + estacao.getNome() + "]";
    }
}
