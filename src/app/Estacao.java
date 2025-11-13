package app;

import pds.tempo.HorarioSemanal;

public class Estacao {

    public String nome;
    public HorarioSemanal horario;
    public String etensao;
    public Estacao central;
    
    public Estacao(String nome, HorarioSemanal processarHorario , Object o, Object o1) {
        this.nome = nome;
        this.horario = processarHorario;
	}

    public void adicionarCentral(Estacao e) {
        this.central = e;
    }

    public String getNome() {
        return nome;
    }

    public String toString() {
        return "Estacao [nome=" + nome + ", horario=" + horario + ", etensao=" + etensao + ", central=" + central + "]";
    }
}
