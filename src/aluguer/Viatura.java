package aluguer;

import java.util.List;
import java.util.ArrayList;
import pds.tempo.IntervaloTempo;
public class Viatura {

    private String matricula;
    private Modelo modelo;
    private Estacao estacao;

    private List<Indisponibilidade> indisponibilidades = new ArrayList<>();

    public Viatura(String matricula, Modelo modelo, Estacao estacao) {
            this.matricula = matricula;
            this.modelo = modelo;
            this.estacao = estacao;
        }

    public Viatura( Modelo modelo, Estacao estacao) {
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

    public List<Indisponibilidade> getIndisponibilidades() {
        return indisponibilidades;
    }

    public void adicionarIndisponibilidade(IntervaloTempo intervalo, String motivo) {
        this.indisponibilidades.add(new Indisponibilidade(intervalo, motivo));
    }

    public boolean isDisponivel(IntervaloTempo searchInterval) {
        for (Indisponibilidade ind : indisponibilidades) {
            if (ind.intervalo.interseta(searchInterval)) {
                return false;
            }
        }
        return true; 
    }


    @Override
    public String toString() {
        return "Viatura [matricula=" + matricula + ", modelo=" + modelo.getModeloString() + ", estacao=" + estacao.getNome() + "]";
    }

    /**
    * Classe interna para guardar uma reserva (indisponibilidade)
    */

    public static class Indisponibilidade {
        public final IntervaloTempo intervalo;
        public final String motivo;

        public Indisponibilidade(IntervaloTempo intervalo, String motivo) {
            this.intervalo = intervalo;
            this.motivo = motivo;
        }
    }

}