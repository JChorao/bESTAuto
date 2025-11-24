package aluguer;

import pds.tempo.HorarioSemanal;

public class Estacao {

    private String nome;
    private HorarioSemanal horario;
    private String tipoExtensao;        
    private int maxHorasExtensao = 0;   
    private String tipoPrecoExtensao;   
    private long precoTaxaExtensao = 0; 
    
    private Estacao central;
    
    public Estacao(String nome, HorarioSemanal horario, Estacao central) {
        this.nome = nome;
        this.horario = horario;
        this.central = central;
	}
    public Estacao(String nome, HorarioSemanal horario, Estacao central, String tipoExtensao, int maxHorasExtensao, String tipoPrecoExtensao, long precoTaxaExtensao) {
        this.nome = nome;
        this.horario = horario;
        this.central = central;
        this.tipoExtensao = tipoExtensao;
        this.maxHorasExtensao = maxHorasExtensao;
        this.tipoPrecoExtensao = tipoPrecoExtensao;
        this.precoTaxaExtensao = precoTaxaExtensao;
	}

    public void setExtensao(String tipoExtensao, int maxHoras) {
        this.tipoExtensao = tipoExtensao;
        this.maxHorasExtensao = maxHoras;
    }

    public void setPrecoExtensao(String tipoPreco, long precoTaxa) {
        this.tipoPrecoExtensao = tipoPreco;
        this.precoTaxaExtensao = precoTaxa;
    }
    
    public String getTipoExtensao() {
        return tipoExtensao;
    }
    
    public int getMaxHorasExtensao() {
        return maxHorasExtensao;
    }

    public String getTipoPrecoExtensao() {
        return tipoPrecoExtensao;
    }

    public long getPrecoTaxaExtensao() {
        return precoTaxaExtensao;
    }
    
    public HorarioSemanal getHorario() {
        return horario;
    }

    public String getNome() {
        return nome;
    }

    public Estacao getCentral() {
        return central;
    }
    
    public void adicionarCentral(Estacao e) {
        this.central = e;
    }

    public String toString() {
        return "Estacao [nome=" + nome + ", horario=" + horario + ", central=" + central + "]";
    }
}