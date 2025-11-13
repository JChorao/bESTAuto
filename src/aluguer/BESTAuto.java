package aluguer;

import java.util.HashMap;
import java.util.Vector;

import app.Estacao;

/**
 * Classe que representa o sistema
 */
public class BESTAuto {

    public HashMap<String, Estacao> estacoes;

    public void adicionarEstacao(String id,Estacao e) {
        if (estacoes == null) {
            estacoes = new HashMap<>();
        }
        estacoes.put(id, e);
    }
    
    public void adicionarCentral(String idEstacao, Estacao central) {
        Estacao e = estacoes.get(idEstacao);
        if (e != null) {
            e.adicionarCentral(central);
        }
    }

    public Vector<Estacao> getEstacoes() {
        
        Vector<Estacao> v = new Vector<>(estacoes.values());
        return v;
    
    }

    public String getNomeEstacao(String id) {
        Estacao e = estacoes.get(id);
        if (e != null) {
            return e.getNome();
        }
        return null;
    }
}
