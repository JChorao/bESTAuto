package app;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.util.Collections;

import static javax.swing.SpringLayout.*;

import aluguer.BESTAuto;
import aluguer.Categoria;

@SuppressWarnings("serial")
/**
 * Janela onde se podem visualizar as informações de um voo
 */
public class JanelaEstacoes extends JFrame {

	// Modelos e listas para as várias informações a apresentar
	DefaultListModel<Categoria> categoriasModel = new DefaultListModel<>();
	DefaultListModel<String> modelosModel = new DefaultListModel<>();
	DefaultListModel<String> matriculasModel = new DefaultListModel<>();
	DefaultTableModel indisponibilidadesModel;

	/**
	 * Cria uma janela para apresentar informações sobre uma estação
	 */
	public JanelaEstacoes(BESTAuto a) {
		setTitle("bEST Auto - A melhor experiência em aluguer de automóveis");

		// TODO colocar a lista de nomes das estações (ordenadas alfabeticamente) no
		// vetor nomes (o que está é apenas de exemplo)
		
		Vector<String> nomes = new Vector<>();
		
		try{
			for(Estacao e : a.getEstacoes()){ 
				nomes.add(e.getNome());
			}

		}catch(Exception e){
			nomes.add("Alcains");
        	nomes.add("Castelo Branco");
		}

		Collections.sort(nomes);

		setupJanela(nomes);
	}

	/**
	 * Método chamado quando o utilizador escolhe uma nova estação
	 * 
	 * @param selecionadaIndex o índice da estação selecionada
	 */
	private void escolherEstacao(int selecionadaIndex) {
		// TODO escolher a estação e colocar na lista as categorias suportas por esta
		// estação (neste momento está a colocar todas)

		Collection<Categoria> lista = List.of(Categoria.values());

		// limpar as restantes listas todas
		categoriasModel.clear();
		modelosModel.clear();
		matriculasModel.clear();
		if (indisponibilidadesModel != null)
			indisponibilidadesModel.setRowCount(0);

		// adicionar as novas categorias à lista
		categoriasModel.addAll(lista);
	}

	/**
	 * Método chamado quando o utilizador escolhe uma nova categoria
	 * 
	 * @param c a categoria escolhida
	 */
	private void escolherCategoria(Categoria c) {
		// TODO colocar na lista o nome dos modelos que a estação selecionada tem nesta
		// categoria (Neste momento é apenas um exemplo)
		List<String> modelos = List.of("Koenigsegg Gemera", "Koenigsegg Jesko Attack");

		// limpar as restantes listas
		modelosModel.clear();
		matriculasModel.clear();
		indisponibilidadesModel.setRowCount(0);

		// adicionar os novos modelos à lista
		modelosModel.addAll(modelos);
	}

	/**
	 * Método chamado quando o utilizador escolhe um novo modelo
	 * 
	 * @param modelo nome do modelo selecionado
	 */
	private void escolherModelo(String modelo) {
		// TODO colocar na lista todas as matrículas das viaturas do modelo selecionado,
		// o que está é apenas um exemplo
		List<String> matriculas = List.of("ZZ-98-ZZ", "ZZ-99-ZZ");

		// limpar as restantes listas
		matriculasModel.clear();
		indisponibilidadesModel.setRowCount(0);

		// adicionar as matrículas à lista
		matriculasModel.addAll(matriculas);
	}

	/**
	 * Método chamado quando o utilizador escolhe uma nova matricula
	 * 
	 * @param matricula a matrícula escolhida
	 */
	private void escolherAutomovel(String matricula) {
		indisponibilidadesModel.setRowCount(0); // limpar a tabela

        // Obter a viatura real a partir da matrícula
        Viatura v = bestAuto.getViatura(matricula);
        if (v == null) return;

		// Iterar pelas indisponibilidades REAIS da viatura
        for (Viatura.Indisponibilidade ind : v.getIndisponibilidades()) {
            adicionarLinha(
                ind.intervalo.getInicio(), 
                ind.intervalo.getFim(), 
                ind.motivo
            );
        }
	}

	/**
	 * Método que adiciona uma linha à tabela de indisponibilidades
	 * 
	 * @param inicio data de inicio da indisponibilidade
	 * @param fim    data de fim da indisponibilidade
	 * @param motivo motivo da indisponibilidade
	 */
	private void adicionarLinha(LocalDateTime inicio, LocalDateTime fim, String motivo) {
		Object valores[] = {
				String.format("%02d/%02d/%4d", inicio.getDayOfMonth(), inicio.getMonthValue(), inicio.getYear()),
				String.format("%02d:%02d", inicio.getHour(), inicio.getMinute()),
				motivo,
				String.format("%02d/%02d/%4d", fim.getDayOfMonth(), fim.getMonthValue(), fim.getYear()),
				String.format("%02d:%02d", fim.getHour(), fim.getMinute()), };

		indisponibilidadesModel.addRow(valores);
	}

	/**
	 * Confira esta janela
	 * 
	 * @param nomes a lista dos nomes das estações suportadas
	 */
	private void setupJanela(Vector<String> nomes) {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		SpringLayout layout = new SpringLayout();
		JPanel panel = new JPanel(layout);
		JPanel estacoes = setupEscolhaEstacao(nomes);
		JPanel classesGamas = new JPanel(new GridLayout(0, 2));
		JPanel classes = setupEscolhaCategorias();
		JPanel gamas = setupEscolhaGama();
		JPanel autos = setupEscolhaMatrícula();
		JPanel autoIndisp = setupAutoIndiponibilidades();
		classesGamas.add(classes);
		classesGamas.add(gamas);

		panel.add(estacoes);
		panel.add(classesGamas);
		panel.add(autos);
		panel.add(autoIndisp);

		layout.putConstraint(NORTH, estacoes, 2, NORTH, panel);
		layout.putConstraint(EAST, estacoes, 2, EAST, panel);
		layout.putConstraint(WEST, estacoes, 2, WEST, panel);

		layout.putConstraint(NORTH, classesGamas, 2, SOUTH, estacoes);
		layout.putConstraint(EAST, classesGamas, 0, EAST, estacoes);
		layout.putConstraint(WEST, classesGamas, 0, WEST, estacoes);
		layout.putConstraint(SOUTH, classesGamas, 150, NORTH, classesGamas);

		layout.putConstraint(NORTH, autos, 2, SOUTH, classesGamas);
		layout.putConstraint(EAST, autos, 0, EAST, estacoes);
		layout.putConstraint(WEST, autos, 0, WEST, estacoes);
		layout.putConstraint(SOUTH, autos, 150, NORTH, autos);

		layout.putConstraint(NORTH, autoIndisp, 2, SOUTH, autos);
		layout.putConstraint(EAST, autoIndisp, 0, EAST, estacoes);
		layout.putConstraint(WEST, autoIndisp, 0, WEST, estacoes);
		layout.putConstraint(SOUTH, autoIndisp, 2, SOUTH, panel);

		setContentPane(panel);
		setSize(600, 680);
	}

	/**
	 * Configura o painel da listagem das estações
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
		painel.add(listagem, BorderLayout.CENTER);
		listagem.setSelectedIndex(0);
		return painel;
	}

	/**
	 * Configura o painel da escolha da categoria
	 * 
	 * @return o painel configurado
	 */
	private JPanel setupEscolhaCategorias() {
		JPanel painel = new JPanel(new BorderLayout());
		painel.setBorder(BorderFactory.createTitledBorder("Escolher Categoria"));

		JList<Categoria> listagem = new JList<>(categoriasModel);
		listagem.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listagem.addListSelectionListener(e -> {
			if (listagem.getSelectedValue() != null)
				escolherCategoria(listagem.getSelectedValue());
		});
		painel.add(new JScrollPane(listagem), BorderLayout.CENTER);
		return painel;
	}

	/**
	 * Configura o painel da escolha do modelo
	 * 
	 * @return o painel configurado
	 */
	private JPanel setupEscolhaGama() {
		JPanel painel = new JPanel(new BorderLayout());
		painel.setBorder(BorderFactory.createTitledBorder("Escolher modelo"));

		JList<String> listagem = new JList<>(modelosModel);
		listagem.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listagem.addListSelectionListener(e -> {
			if (listagem.getSelectedValue() != null)
				escolherModelo(listagem.getSelectedValue());
		});
		// listagem.addActionListener(e -> escolherClasse(listagem.getSelectedIndex()));
		painel.add(new JScrollPane(listagem), BorderLayout.CENTER);
		return painel;
	}

	/**
	 * Configura o painel da escolha da matricula
	 * 
	 * @return o painel configurado
	 */
	private JPanel setupEscolhaMatrícula() {
		JPanel painel = new JPanel(new BorderLayout());
		painel.setBorder(BorderFactory.createTitledBorder("Escolher viatura"));

		JList<String> listagem = new JList<>(matriculasModel);
		listagem.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listagem.addListSelectionListener(e -> {
			if (listagem.getSelectedValue() != null)
				escolherAutomovel(listagem.getSelectedValue());
		});
		painel.add(new JScrollPane(listagem), BorderLayout.CENTER);
		return painel;
	}

	/**
	 * Configura o painel de apresentação das indisponibilidades
	 * 
	 * @return o painel configurado
	 */
	private JPanel setupAutoIndiponibilidades() {
		JPanel painel = new JPanel(new BorderLayout());
		painel.setBorder(BorderFactory.createTitledBorder("Indisponilidades"));
		String nomeColunas[] = { "de dia", "hora", "motivo", "até dia", "hora" };
		indisponibilidadesModel = new DefaultTableModel(nomeColunas, 0);

		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);

		JTable autoTable = new JTable(indisponibilidadesModel);
		TableColumnModel cm = autoTable.getColumnModel();
		cm.getColumn(0).setMaxWidth(100);
		cm.getColumn(0).setCellRenderer(centerRenderer);
		cm.getColumn(1).setMaxWidth(60);
		cm.getColumn(1).setCellRenderer(centerRenderer);
		cm.getColumn(2).setPreferredWidth(180);
		cm.getColumn(3).setMaxWidth(100);
		cm.getColumn(3).setCellRenderer(centerRenderer);
		cm.getColumn(4).setMaxWidth(60);
		cm.getColumn(4).setCellRenderer(centerRenderer);

		painel.add(new JScrollPane(autoTable), BorderLayout.CENTER);
		return painel;
	}
}
