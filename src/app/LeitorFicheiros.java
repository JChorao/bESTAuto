package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Classe responsável pela leitura dos ficheiros do sistema
 */
public class LeitorFicheiros {

    /**
     * Lê um ficheiro. O ficheiro assume-se como tendo uma informação de um elemento
     * numa linha com separação por tabulações o no formato chave=valor[opções]
     * Cada linha será codificada num bloco de informação
     * 
     * @param ficheiro o nome do ficheiro a ler
     * @return a lista de blocos de informação
     * @throws IOException se houver algum problema ler o ficheiro
     */
    public static List<Bloco> lerFicheiro(String ficheiro) throws IOException {
        ArrayList<Bloco> blocos = new ArrayList<>();
        try (BufferedReader fin = Files.newBufferedReader(Paths.get(ficheiro))) {
            String linha;
            while ((linha = fin.readLine()) != null) {
                if (linha.isEmpty())
                    continue;
                Bloco b = readLinha(linha);
                blocos.add(b);
            }
        }
        return blocos;
    }

    /**
     * Lê uma linha de informação, processando cada par chave=valor
     * 
     * @param linha a linha a processar
     * @return o bloco com todos os pares chave:valor lidos
     */
    private static Bloco readLinha(String linha) {
        Bloco props = new Bloco();

        String blocos[] = linha.split("\t");
        for (String b : blocos) {
            String dados[] = b.split("=");
            String campo = dados[0];
            String valor = dados[1];
            int opcoesIdx = valor.indexOf('[');
            if (opcoesIdx == -1) {
                Campo v = new Campo(valor, new String[0]);
                props.addCampo(campo, v);
            } else {
                valor = valor.substring(0, opcoesIdx);
                String opDados = dados[1].substring(opcoesIdx + 1, dados[1].length() - 1);
                String opcoes[] = opDados.split(",");
                Campo v = new Campo(valor, opcoes);
                props.addCampo(campo, v);
            }
        }
        return props;
    }

    /**
     * Classe que representa um bloco de informação dentro do ficheiro
     * Um bloco corresponde a todos os pares chave=valor de uma linha do ficheiro
     */
    public static final class Bloco {
        private final HashMap<String, Campo> propriedades = new HashMap<>();

        /**
         * Método chamado pelo leitor para adicionar um novo campo ao bloco
         * 
         * @param propriedade o chave que identifica a propriedade
         * @param cmp         o campo que contém o valor e as opções
         */
        protected void addCampo(String propriedade, Campo cmp) {
            propriedades.put(propriedade, cmp);
        }

        /**
         * Retorna o valor associado a uma chave
         * 
         * @param chave a chave a pesquisar
         * @return o valor associado a essa chave
         */
        public String getValor(String chave) {
            Campo c = propriedades.get(chave);
            return c == null ? null : c.getValor();
        }

        /**
         * Retorna as opções associadas a uma chave
         * 
         * @param chave a chave a pesquisar
         * @return as opções associadas à chave, ou null se não houver opções
         */
        public String[] getOpcoes(String chave) {
            Campo c = propriedades.get(chave);
            return c == null ? null : c.getOpcoes();
        }
    }

    /**
     * Classe interna que representa um campo. Um campo é um conjunto de valor e
     * um array de opções
     */
    private static final class Campo {
        private final String valor;
        private final String[] opcoes;

        public Campo(String valor, String[] opcoes) {
            this.valor = valor;
            this.opcoes = opcoes;
        }

        public String[] getOpcoes() {
            return opcoes;
        }

        public String getValor() {
            return valor;
        }
    }
}
