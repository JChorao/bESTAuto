package app;

import static javax.swing.SpringLayout.EAST;
import static javax.swing.SpringLayout.NORTH;
import static javax.swing.SpringLayout.SOUTH;
import static javax.swing.SpringLayout.WEST;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SpringLayout;
import javax.swing.table.DefaultTableModel;
import java.util.Collections;

import aluguer.BESTAuto;
import aluguer.Categoria;
import pds.tempo.HorarioDiario;
import pds.tempo.HorarioSemanal;
import pds.tempo.IntervaloTempo;
import pds.ui.PainelListador;

@SuppressWarnings("serial")
/**
 * Janela onde se podem visualizar as informações de um voo
 */
public class JanelaAluguer extends JFrame {

	/** formatador para apresentar as datas */
	private static final DateTimeFormatter dataFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	private static final DateTimeFormatter horaFormatter = DateTimeFormatter.ofPattern("HH:mm");
	// fontes e cores para a interface gráfica
	private static Font grandeFont = new Font("ROMAN", Font.BOLD, 16);
	private static Font mediaFont = new Font("ROMAN", Font.PLAIN, 13);
	private static final Color COR_RESULTADO = new Color(250, 215, 170);

	// Elementos visuais da interface
	private JComboBox<Categoria> categCb = new JComboBox<>(Categoria.values());
	private PainelListador alugueres = new PainelListador();
	private JButton deBt;
	private JButton ateBt;

	// listas e tabelas com as várias informações
	DefaultListModel<Categoria> categoriasModel = new DefaultListModel<>();
	DefaultListModel<String> modelosModel = new DefaultListModel<>();
	DefaultListModel<String> matriculasModel = new DefaultListModel<>();
	DefaultTableModel indisponibilidadesModel;

	// valores escohidos pelo utilizador para as datas
	private LocalDate dataInicio;
	private LocalDate dataFim;
	private LocalTime horasInicio;
	private LocalTime horasFim;

	// intervalo de tempo selecionado pelo utilziador
	private IntervaloTempo intervaloSel;

	// A companhia a ser usada
	private BESTAuto bestAuto;

	/**
	 * Cria uma janela de aluguer
	 */
	public JanelaAluguer(BESTAuto a) {
		bestAuto = a;
		setTitle("bEST Auto - A melhor experiência em aluguer de automóveis");

		// TODO colocar a lista de nomes das estações (ordenadas alfabeticamente) no
		// vetor nomes (o que está é apenas de exemplo)
		Vector<String> nomes = new Vector<>();
		
		for(Estacao e : a.getEstacoes()){ 
				nomes.add(e.getNome());
		}

		Collections.sort(nomes);
		
		setupJanela(nomes);
	}

	/**
	 * Método chamado quando o utilizador muda de estação
	 * 
	 * @param selecionadaIndex o índice da estação selecionada
	 */
	private void escolherEstacao(int selecionadaIndex) {
		// TODO selecionar a estação adequada

		// limpar a pesquisa
		limparPesquisa();
	}

	/**
	 * método chamado quando o utilizador pressiona o botão de apresentar horário
	 */
	private void apresentarHorario() {
		// TODO ir buscar o horário da estação atual, em vez de usar um vazio
		HorarioSemanal hs = HorarioSemanal.sempreFechado();

		apresentarHorario(hs);
	}

	/**
	 * Método chamado quando o utilizador pressiona o botão de pesquisar
	 */
	private void pesquisar() {
		limparPesquisa();

		// ver as datas de inicio e de fim
		LocalDateTime inicio = LocalDateTime.of(dataInicio, horasInicio);
		LocalDateTime fim = LocalDateTime.of(dataFim, horasFim);
		// garantir que fim é, pelo menos, um dia depois do início
		if (!inicio.isBefore(fim) || !dataInicio.isBefore(dataFim)) {
			JOptionPane.showMessageDialog(null,
					"A data de fim tem de ser superior em 1 dia, pelo menos, à data de início");
			return;
		}
		intervaloSel = IntervaloTempo.entre(inicio, fim);

		// TODO fazer a pesquisa

		// TODO para cada viatura da pesquisa criar um painel e
		// associar a informação adequada. Cada painel terá um valor (à escolha do
		// grupo) que o associará a um resultado. Esse valor será depois usado
		// para identificar qual a viatura alugada se o cliente escolher esse painel
		PainelAluguer pa1 = new PainelAluguer("Koenigsegg Gemera", 4, 1, 120000, null);
		alugueres.add(pa1);
		PainelAluguer pa2 = new PainelAluguer("Koenigsegg Jesko Attack", 2, 1, 100000, null);
		alugueres.add(pa2);

		// TODO sem resultados (alterar o teste, claro!)? Apresentar essa informação
		if (Math.abs(2) == -1)
			alugueres.add(new JLabel("-- SEM RESULTADOS --", JLabel.CENTER));
	}

	/**
	 * Método chamado quando o utilizador pressiona o botão de alugar.
	 * 
	 * @param valor o objeto selecionado. Este valor foi o usado
	 *              quando se criou o painel de aluguer
	 */
	private void alugar(Object valor) {
		// TODO fazer o aluguer

		// TODO colocar a info certa nas variáveis
		String code = "AA1122BB";
		String matricula = "ZZ-99-ZZ";

		// apresentar a info
		JOptionPane.showMessageDialog(this,
				"<html>Obrigado por usar os nossos serviços!<br>Aluguer " + code + ", carro será " + matricula
						+ "</html>");
		limparPesquisa();
	}

	/**
	 * Cria e configura a janela
	 * 
	 * @param nomes nomes das estações a usar
	 */
	private void setupJanela(Vector<String> nomes) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		SpringLayout layout = new SpringLayout();
		JPanel panel = new JPanel(layout);

		JPanel estacoes = setupEscolhaEstacao(nomes);
		JPanel tempos = setupEscolhaTempos();
		JScrollPane scrollAlugueres = new JScrollPane(alugueres);
		panel.add(estacoes);
		panel.add(tempos);
		panel.add(scrollAlugueres);

		layout.putConstraint(NORTH, estacoes, 2, NORTH, panel);
		layout.putConstraint(EAST, estacoes, 2, EAST, panel);
		layout.putConstraint(WEST, estacoes, 2, WEST, panel);

		layout.putConstraint(NORTH, tempos, 2, SOUTH, estacoes);
		layout.putConstraint(EAST, tempos, 0, EAST, estacoes);
		layout.putConstraint(WEST, tempos, 0, WEST, estacoes);
		layout.putConstraint(SOUTH, tempos, 100, NORTH, tempos);

		layout.putConstraint(NORTH, scrollAlugueres, 2, SOUTH, tempos);
		layout.putConstraint(EAST, scrollAlugueres, 0, EAST, estacoes);
		layout.putConstraint(WEST, scrollAlugueres, 0, WEST, estacoes);
		layout.putConstraint(SOUTH, scrollAlugueres, 2, SOUTH, panel);

		setContentPane(panel);
		setSize(450, 680);
	}

	/**
	 * Cria o painel para escolha dos tempos de início e de fim
	 * 
	 * @return o painel configurado
	 */
	private JPanel setupEscolhaTempos() {
		String horas[] = new String[48];
		for (int h = 0; h < 24; h++) {
			horas[h * 2] = String.format("%02d:00", h);
			horas[h * 2 + 1] = String.format("%02d:30", h);
		}
		LocalTime t = LocalTime.now();
		int indiceHora = t.getHour() * 2 + 1 + (t.getMinute() > 30 ? 1 : 0);

		JPanel painel = new JPanel(new GridLayout(0, 1));
		JPanel temposPn = new JPanel();
		painel.setBorder(BorderFactory.createTitledBorder("Escolher data de recolha e entrega"));
		temposPn.add(new JLabel("De:"));
		dataInicio = LocalDate.now();
		deBt = new JButton(dataInicio.format(dataFormatter));
		deBt.addActionListener(e -> escolherInicio());
		temposPn.add(deBt);
		JComboBox<String> horasIniCb = new JComboBox<>(horas);
		horasIniCb.addActionListener(e -> {
			horasInicio = LocalTime.of(horasIniCb.getSelectedIndex() / 2, 30 * (horasIniCb.getSelectedIndex() % 2));
			limparPesquisa();
		});
		horasIniCb.setSelectedIndex(indiceHora);
		temposPn.add(horasIniCb);
		temposPn.add(new JLabel("Até:"));
		dataFim = dataInicio.plusDays(1);
		ateBt = new JButton(dataFim.format(dataFormatter));
		ateBt.addActionListener(e -> escolherFim());
		temposPn.add(ateBt);
		JComboBox<String> horasFimCb = new JComboBox<>(horas);
		horasFimCb.addActionListener(e -> {
			horasFim = LocalTime.of(horasFimCb.getSelectedIndex() / 2, 30 * (horasFimCb.getSelectedIndex() % 2));
			limparPesquisa();
		});
		horasFimCb.setSelectedIndex(indiceHora);
		temposPn.add(horasFimCb);

		JPanel catePesquisar = new JPanel();
		catePesquisar.add(new JLabel("Categoria:"));
		categCb.addActionListener(e -> limparPesquisa());
		catePesquisar.add(categCb);

		JButton pesquisarBt = new JButton("Pesquisar");
		pesquisarBt.addActionListener(e -> pesquisar());
		catePesquisar.add(pesquisarBt);

		painel.add(temposPn);
		painel.add(catePesquisar);
		return painel;
	}

	/**
	 * método chamado quando o utilizador escolhe mudar a data de início
	 */
	private void escolherInicio() {
		CalendarDialog cd = new CalendarDialog(dataInicio);
		cd.setModal(true);
		cd.setVisible(true);
		limparPesquisa();
		if (cd.hasSelectedDate()) {
			dataInicio = cd.getSelectedDate();
			deBt.setText(dataInicio.format(dataFormatter));
		}
	}

	/**
	 * método chamado quando o utilizador escolhe mudar a data de fim
	 */
	private void escolherFim() {
		CalendarDialog cd = new CalendarDialog(dataFim);
		cd.setModal(true);
		cd.setVisible(true);
		limparPesquisa();
		if (cd.hasSelectedDate()) {
			dataFim = cd.getSelectedDate();
			ateBt.setText(dataFim.format(dataFormatter));
		}
	}

	/**
	 * Cria a zona de escolha das estações e preenche-a com os respetivos nomes
	 * 
	 * @param nomes os nomes das estações
	 * @return o painel configurado
	 */
	private JPanel setupEscolhaEstacao(Vector<String> nomes) {
		JPanel painel = new JPanel(new BorderLayout());
		painel.setBorder(BorderFactory.createTitledBorder("Escolher Estação"));

		JComboBox<String> listagem = new JComboBox<>(nomes);
		listagem.setEditable(false);
		listagem.addActionListener(e -> escolherEstacao(listagem.getSelectedIndex()));
		listagem.setSelectedIndex(0);
		painel.add(listagem, BorderLayout.CENTER);

		JButton horarioBt = new JButton("Horário");
		horarioBt.addActionListener(e -> apresentarHorario());
		painel.add(horarioBt, BorderLayout.EAST);

		return painel;
	}

	/**
	 * Método chamado quando o utilizador pressiona o botão de ver o horário da
	 * estação
	 * 
	 * @param h o horário da estação
	 */
	private void apresentarHorario(HorarioSemanal h) {
		String nomesDias[] = { "Seg.: ", "Ter.: ", "Qua.: ", "Qui.: ", "Sex.: ", "Sab.: ", "Dom.: " };
		StringBuilder str = new StringBuilder("<html>");
		int i = 0;
		for (DayOfWeek dia : DayOfWeek.values()) {
			HorarioDiario hd = h.getHorarioDia(dia);
			if (hd.eVazio())
				str.append(nomesDias[i++] + "fechado");
			else {
				str.append(nomesDias[i++] + h.getHorarioDia(dia).getInicio().format(horaFormatter) + " - ");
				str.append(h.getHorarioDia(dia).getFim().format(horaFormatter));
			}
			str.append("<br>");
		}
		str.append("</html>");
		JOptionPane.showMessageDialog(this, str, "Horário", JOptionPane.INFORMATION_MESSAGE);
	}

	/** Limpa o painel de pesquisa */
	private void limparPesquisa() {
		alugueres.removeAll();
	}

	/**
	 * Classe que representa um painel onde irão ser colcoadas as informações de um
	 * possível aluguer
	 */
	private class PainelAluguer extends JPanel {

		PainelAluguer(String modelo, int lotacao, int bagagem, long preco, Object valor) {
			SpringLayout layout = new SpringLayout();
			setLayout(layout);
			setOpaque(false);

			JLabel modeloLbl = new JLabel(modelo);
			modeloLbl.setFont(grandeFont);
			add(modeloLbl);

			JLabel portasLbl = new JLabel("lotação: " + lotacao);
			portasLbl.setFont(mediaFont);
			add(portasLbl);

			JLabel lotacaoLbl = new JLabel("malas: " + bagagem);
			lotacaoLbl.setFont(mediaFont);
			add(lotacaoLbl);

			JLabel precoLbl = new JLabel(String.format("%.2f€", preco / 100.0f));
			precoLbl.setFont(grandeFont);
			add(precoLbl);

			JButton alugarBt = new JButton("Alugar");
			alugarBt.addActionListener(e -> alugar(valor));
			add(alugarBt);

			Dimension prefDim = new Dimension(200, 60);
			setPreferredSize(prefDim);
			setMinimumSize(prefDim);
			layout.putConstraint(SpringLayout.NORTH, modeloLbl, 2, SpringLayout.NORTH, this);
			layout.putConstraint(SpringLayout.EAST, modeloLbl, -2, SpringLayout.EAST, this);
			layout.putConstraint(SpringLayout.WEST, modeloLbl, 2, SpringLayout.WEST, this);

			layout.putConstraint(SpringLayout.NORTH, portasLbl, 2, SpringLayout.SOUTH, modeloLbl);
			layout.putConstraint(SpringLayout.WEST, portasLbl, 2, SpringLayout.WEST, modeloLbl);

			layout.putConstraint(SpringLayout.NORTH, lotacaoLbl, 0, SpringLayout.NORTH, portasLbl);
			layout.putConstraint(SpringLayout.WEST, lotacaoLbl, 10, SpringLayout.EAST, portasLbl);

			layout.putConstraint(SpringLayout.NORTH, precoLbl, 2, SpringLayout.NORTH, this);
			layout.putConstraint(SpringLayout.EAST, precoLbl, -10, SpringLayout.EAST, this);

			layout.putConstraint(SpringLayout.NORTH, alugarBt, 2, SpringLayout.SOUTH, precoLbl);
			layout.putConstraint(SpringLayout.EAST, alugarBt, -10, SpringLayout.EAST, this);
		}

		@Override
		protected void paintComponent(Graphics g) {
			g.setColor(COR_RESULTADO);
			g.fillRoundRect(0, 1, getWidth(), getHeight() - 2, 16, 16);
			g.setColor(Color.GRAY);
			g.drawRoundRect(0, 1, getWidth(), getHeight() - 2, 16, 16);
			super.paintComponent(g);
		}
	}
}
