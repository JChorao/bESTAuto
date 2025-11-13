package app;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.IOException;
import java.time.LocalTime;
import java.util.List;
import aluguer.BESTAuto;
import aluguer.Categoria;
import app.LeitorFicheiros.Bloco;
import pds.tempo.HorarioDiario;
import pds.tempo.HorarioSemanal;

/**
 * Clase que arranca com o sistema
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		BESTAuto albi = new BESTAuto();
		// ler os dados
		readEstacoes(albi, "dados/estacoes.txt");
		readModelos(albi, "dados/modelos.txt");
		readViaturas(albi, "dados/carros.txt");

		// ver o tamanho do écran onde criar as janelas
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		// criar as janelas
		JanelaEstacoes je = new JanelaEstacoes(albi);
		JanelaAluguer ja = new JanelaAluguer(albi);

		// posicioná-las ao centro
		int posx1 = (screenSize.width - je.getWidth() - ja.getWidth()) / 2;
		int posx2 = posx1 + je.getWidth() + 10;
		je.setLocation(posx1, 50);
		ja.setLocation(posx2, 50);

		// torná-las visiveis
		je.setVisible(true);
		ja.setVisible(true);
	}

	/**
	 * método para ler o ficheiro com a informação das estações
	 * 
	 * @param best         a companhia
	 * @param estacoesFile o nome do ficheiro com a informação
	 */
	private static void readEstacoes(BESTAuto best, String estacoesFile) {
		try {
			List<LeitorFicheiros.Bloco> blocos = LeitorFicheiros.lerFicheiro(estacoesFile);

			for (LeitorFicheiros.Bloco b : blocos) {
				// TODO completar este método
				
				Estacao e = new Estacao(b.getValor("nome"),processarHorario(b), processarCentral(best, b), null);
				
				String id = b.getValor("id");
				String nome = b.getValor("nome");
				HorarioSemanal h = processarHorario(b);
				processarCentral(best, b);
				processarExtensao(b);
				processarPagamentoExtensao(b);
				// TODO armazenar a informação lida no sistema

				best.adicionarEstacao(b.getValor("id"), e);
				
			}

		} catch (IOException e) {
			System.out.println("Erro na leitura do ficheiro " + estacoesFile);
			e.printStackTrace();
			System.exit(0);
		}
	}

	/**
	 * Processa as informações sobre como calcular o extra por haver extensão de
	 * horário
	 * 
	 * @param b O bloco com a informação a processar
	 */
	private static void processarPagamentoExtensao(Bloco b) {
		// TODO completar este método (os return podem ter de ser eliminados)
		String tipoPrecario = b.getValor("preco_extensao");

		// se não tem esta chave é porque não tem extensão
		if (tipoPrecario == null) {
			return;
		} else if (tipoPrecario.equals("taxa")) {
			return;
		} else if (tipoPrecario.equals("variavel")) {
			return;
		}
		// nunca devia chegar aqui, mas caso alguma coisa corra mal...
		throw new UnsupportedOperationException("Campo desconhecido: " + tipoPrecario);
	}

	/**
	 * Processa as informações sobre como proceder à extensão de horário
	 * 
	 * @param b o bloco com a informação a processar
	 */
	private static void processarExtensao(Bloco b) {
		// TODO completar este método (os return podem ter de ser eliminados)
		String tipoExtensao = b.getValor("extensao");
		// se não tem esta chave é porque não tem extensão
		if (tipoExtensao == null) {
			return;
		} else if (tipoExtensao.equals("horas")) {
			return;
		} else if (tipoExtensao.equals("total")) {
			return;
		}
		// nunca devia chegar aqui
		throw new UnsupportedOperationException("Campo desconhecido: " + tipoExtensao);
	}

	/**
	 * Processa as informações sobre se tem central ou não
	 * 
	 * @param b o bloco com a informação a processar
	 */
	private static String processarCentral(BESTAuto best, Bloco b) {
		// TODO completar este método (os return podem ter de ser eliminados)
		if (b.getValor("central") != null) {
			String central = b.getValor("central");
			return central;
		} else {
			// não tem central
			return null;
		}
	}

	/**
	 * Processa as informações sobre o horário
	 * 
	 * @param b o bloco com a informação a processar
	 * @return o horário semanal
	 */
	private static HorarioSemanal processarHorario(LeitorFicheiros.Bloco info) {
		String tipoHorario = info.getValor("horario");
		if (tipoHorario.equals("total"))
			return HorarioSemanal.sempreAberto();
		String dias[] = info.getOpcoes("horario");
		HorarioDiario hds[] = new HorarioDiario[dias.length];
		for (int i = 0; i < dias.length; i++) {
			String horas[] = dias[i].split("-");
			LocalTime ini = LocalTime.parse(horas[0]);
			LocalTime fim = LocalTime.parse(horas[1]);
			hds[i] = HorarioDiario.deAte(ini, fim);
		}
		if (tipoHorario.equals("alargado"))
			return HorarioSemanal.of(hds);
		if (tipoHorario.equals("semanal"))
			return HorarioSemanal.semanaUtil(hds);

		// nunca devia chegar aqui
		throw new UnsupportedOperationException("Campo desconhecido: " + tipoHorario);
	}

	/**
	 * Lê o ficheiro com as informações sobre os modelos
	 * 
	 * @param best a companhia
	 * @param file o nome do ficheiro
	 */
	private static void readModelos(BESTAuto best, String file) {
		try {
			List<LeitorFicheiros.Bloco> blocos = LeitorFicheiros.lerFicheiro(file);
			for (LeitorFicheiros.Bloco b : blocos) {
				String id = b.getValor("id");
				String modelo = b.getValor("modelo");
				Categoria categoria = Categoria.valueOf(b.getValor("categoria"));
				String marca = b.getValor("marca");
				int lotacao = Integer.parseInt(b.getValor("lotacao"));
				int bagagem = Integer.parseInt(b.getValor("bagagem"));
				long preco = Integer.parseInt(b.getValor("preco"));

				// TODO completar o método

			}
		} catch (IOException e) {
			System.out.println("Erro na leitura do ficheiro " + file);
			e.printStackTrace();
			System.exit(0);
		}
	}

	/**
	 * Lê o ficheiro com as informações sobre as viaturas
	 * 
	 * @param best a companhia
	 * @param file o nome do ficheiro
	 */
	private static void readViaturas(BESTAuto best, String file) {
		try {
			List<LeitorFicheiros.Bloco> blocos = LeitorFicheiros.lerFicheiro(file);
			for (LeitorFicheiros.Bloco b : blocos) {
				String matricula = b.getValor("matricula");
				String modelo = b.getValor("modelo");
				String estacao = b.getValor("estacao");

				// TODO completar o método

			}
		} catch (IOException e) {
			System.out.println("Erro na leitura do ficheiro " + file);
			e.printStackTrace();
			System.exit(0);
		}
	}
}
