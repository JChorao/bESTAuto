package app;

import pds.tempo.HorarioSemanal;

public class Estacao {

    private String nome;
    private HorarioSemanal horario;
    // Novos campos para Extensão e Preço
    private String tipoExtensao;        
    private int maxHorasExtensao = 0;   
    private String tipoPrecoExtensao;   
    private long precoTaxaExtensao = 0; 
    
    private Estacao central;
    
    // Novo construtor que o Main.java utilizará
    public Estacao(String nome, HorarioSemanal horario, Estacao central) {
        this.nome = nome;
        this.horario = horario;
        this.central = central;
	}

    // Setters e Getters para Extensão e Preço
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
    
    // Métodos existentes
    public void adicionarCentral(Estacao e) {
        this.central = e;
    }

    public String getNome() {
        return nome;
    }

    public String toString() {
        return "Estacao [nome=" + nome + ", horario=" + horario + ", central=" + central + "]";
    }

    public Estacao getCentral() {
        return central;
    }
}