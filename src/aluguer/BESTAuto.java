package aluguer;

import java.util.HashMap;
import java.util.Vector;

import app.Estacao;
import app.Modelo;
import app.Viatura;

/**
 * Classe que representa o sistema
 */
public class BESTAuto {

    public HashMap<String, Estacao> estacoes;
    public HashMap<String, Modelo> modelos;
    public HashMap<String, Viatura> viaturas;


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

    public void adicionarModelo(String id, Modelo m) {
        if (modelos == null) {
            modelos = new HashMap<>();
        }
        modelos.put(id, m);
    }

    public Vector<Modelo> getModelos() {
        Vector<Modelo> v = new Vector<>(modelos.values());
        return v;
    }

    public Modelo getModelo(String id) {
        return modelos.get(id);
    }

    public void adicionarViatura(String id, Viatura v) {
        if (viaturas == null) {
            viaturas = new HashMap<>();
        }
        viaturas.put(id, v);
        System.out.println("Viatura adicionada: " + id + ", " + v.toString());
    }

    public Vector<Viatura> getViaturas() {
        Vector<Viatura> v = new Vector<>(viaturas.values());
        return v;
    }

    public Viatura getViatura(String matricula) {
        return viaturas.get(matricula);
    }
}
