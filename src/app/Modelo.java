package app;

import aluguer.Categoria;

public class Modelo {

    private String modelo;
    private Categoria categoria;
    private String marca;
    private int lotacao;
    private int bagagem;
    private long preco;

    public Modelo(String modelo, Categoria categoria, String marca, int lotacao, int bagagem, long preco) {
        this.modelo = modelo;
        this.categoria = categoria;
        this.marca = marca;
        this.lotacao = lotacao;
        this.bagagem = bagagem;
        this.preco = preco;
    }


    public String getModelo() {
        return modelo;
    }
    @Override
    public String toString() {
        return "Modelo [modelo=" + modelo + ", categoria=" + categoria + ", marca=" + marca + ", lotacao=" + lotacao
                + ", bagagem=" + bagagem + ", preco=" + preco + "]";
    }
    
    public Categoria getCategoria() {
        return categoria;
    }

    public int getLotacao() {
        return lotacao;
    }

    public int getBagagem() {
        return bagagem;
    }

    public long getPreco() {
        return preco;
    }

}
