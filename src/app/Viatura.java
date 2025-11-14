package app;

// 1. IMPORTS ADICIONADOS
import java.util.List;
import java.util.ArrayList;
import pds.tempo.IntervaloTempo;
import aluguer.Categoria; // Importar Categoria se Modelo.java precisar

public class Viatura {

    private Modelo modelo;
    private Estacao estacao;

    // 2. CLASSE INTERNA E LISTA DE INDISPONIBILIDADES
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

    // Lista de todas as reservas para esta viatura
    private List<Indisponibilidade> indisponibilidades = new ArrayList<>();
    // --- FIM DA ADIÇÃO ---


    public Viatura( Modelo modelo, Estacao estacao) {
        this.modelo = modelo;
        this.estacao = estacao;
    }

    @Override
    public String toString() {
        return "Viatura [modelo=" + modelo.getModelo() + ", estacao=" + estacao.getNome() + "]";
    }

    // 3. GETTERS E MÉTODOS DE GESTÃO DE DISPONIBILIDADE
    
    public Modelo getModelo() {
        return modelo;
    }

    public Estacao getEstacao() {
        return estacao;
    }

    /**
     * Retorna a lista de indisponibilidades (para a JanelaEstacoes).
     */
    public List<Indisponibilidade> getIndisponibilidades() {
        return indisponibilidades;
    }
    
    /**
     * Adiciona uma nova reserva (indisponibilidade) a esta viatura.
     * @param intervalo O período de tempo
     * @param motivo A descrição (ex: "Aluguer XX1234")
     */
    public void adicionarIndisponibilidade(IntervaloTempo intervalo, String motivo) {
        this.indisponibilidades.add(new Indisponibilidade(intervalo, motivo));
    }

    /**
     * Verifica se a viatura está disponível num dado intervalo.
     * @param searchInterval O intervalo de pesquisa do cliente.
     * @return true se estiver disponível, false se tiver um aluguer sobreposto.
     */
    public boolean isDisponivel(IntervaloTempo searchInterval) {
        // Itera por todas as reservas desta viatura
        for (Indisponibilidade ind : indisponibilidades) {
            
            // Se o intervalo de pesquisa (searchInterval) intersetar
            // com alguma reserva já existente, a viatura NÃO está disponível.
            if (ind.intervalo.interseta(searchInterval)) {
                return false;
            }
        }
        
        // Se o loop terminar, não encontrou conflitos, logo está disponível
        return true; 
    }
}