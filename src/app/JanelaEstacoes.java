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
import java.util.Comparator;

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

	private BESTAuto bestAuto;

    // NOVOS CAMPOS PARA ARMAZENAR O ESTADO
    private Estacao estacaoSelecionada;
    private Vector<Estacao> estacoesOrdenadas; 

	/**
	 * Cria uma janela para apresentar informações sobre uma estação
	 */
	public JanelaEstacoes(BESTAuto a) {
		bestAuto = a;
		setTitle("bEST Auto - A melhor experiência em aluguer de automóveis");

		// 1. Buscar estações e ordená-las por nome
		Vector<String> nomes = new Vector<>();
		
		try{
            // Usar Comparator.comparing(Estacao::getNome) se o Java target for 8+
            estacoesOrdenadas = a.getEstacoes();
			Collections.sort(estacoesOrdenadas, Comparator.comparing(Estacao::getNome));

			for(Estacao e : estacoesOrdenadas){ 
				nomes.add(e.getNome());
			}

		}catch(Exception e){
			// Fallback original
			nomes.add("Alcains");
        	nomes.add("Castelo Branco");
			Collections.sort(nomes);
		}

		setupJanela(nomes);
        
        // 2. Definir a estação inicial (índice 0) e popular as listas
		if (estacoesOrdenadas != null && !estacoesOrdenadas.isEmpty()) {
			escolherEstacao(0); 
		}
	}

	/**
	 * Método chamado quando o utilizador escolhe uma nova estação
	 * * @param selecionadaIndex o índice da estação selecionada
	 */
	private void escolherEstacao(int selecionadaIndex) {
		// 1. Obter a estação selecionada
        if (estacoesOrdenadas == null || selecionadaIndex < 0 || selecionadaIndex >= estacoesOrdenadas.size()) {
			estacaoSelecionada = null;
		} else {
            this.estacaoSelecionada = estacoesOrdenadas.get(selecionadaIndex);
        }

        // Limpar todas as listas
		categoriasModel.clear();
		modelosModel.clear();
		matriculasModel.clear();
		if (indisponibilidadesModel != null)
			indisponibilidadesModel.setRowCount(0);

        // Se nenhuma estação foi selecionada ou se as viaturas não foram carregadas, sair.
        if (this.estacaoSelecionada == null || bestAuto.getViaturas() == null) {
            return;
        }

		// 2. Encontrar as Categorias de Viatura que esta estação *efetivamente* tem
		Collection<Categoria> lista = bestAuto.getViaturas().stream()
                // FILTRO: A viatura pertence à estação selecionada?
				.filter(v -> v.getEstacao().equals(this.estacaoSelecionada))
				.map(v -> v.getModelo().getCategoria())
				// Coletar categorias únicas
				.collect(Collectors.toSet()); 

		// adicionar as novas categorias à lista
		categoriasModel.addAll(lista);
	}

	/**
	 * Método chamado quando o utilizador escolhe uma nova categoria
	 * * @param c a categoria escolhida
	 */
	private void escolherCategoria(Categoria c) {
        // Garantir que a estação está selecionada
        if (this.estacaoSelecionada == null || c == null) {
            modelosModel.clear();
            matriculasModel.clear();
            indisponibilidadesModel.setRowCount(0);
            return;
        }

		// 1. Filtrar modelos que são da Categoria 'c' E que têm viaturas na 'estacaoSelecionada'.
		List<String> modelos = bestAuto.getViaturas().stream()
                // FILTRO: A viatura pertence à estação selecionada?
                .filter(v -> v.getEstacao().equals(this.estacaoSelecionada)) 
                // FILTRO: O modelo da viatura é da categoria selecionada?
				.filter(v -> v.getModelo().getCategoria() == c)
                // Mapear para o nome do modelo (String)
				.map(v -> v.getModelo().getModelo()) 
                // Coletar nomes distintos e ordenar
				.collect(Collectors.toSet()).stream()
                .sorted()
				.collect(Collectors.toList());

		// limpar as restantes listas
		modelosModel.clear();
		matriculasModel.clear();
		indisponibilidadesModel.setRowCount(0);

		// adicionar os novos modelos à lista
		modelosModel.addAll(modelos);
	}

	/**
	 * Método chamado quando o utilizador escolhe um novo modelo
	 * * @param modelo nome do modelo selecionado
	 */
	private void escolherModelo(String modelo) {
        // Garantir que a estação está selecionada
        if (this.estacaoSelecionada == null || modelo == null) {
            matriculasModel.clear();
            indisponibilidadesModel.setRowCount(0);
            return;
        }

		// 1. Filtrar as viaturas (matrículas) que pertencem ao 'modelo' E estão
		// na 'estacaoSelecionada'.
		List<String> matriculas = bestAuto.getViaturas().stream()
                // FILTRO: A viatura pertence à estação selecionada?
                .filter(v -> v.getEstacao().equals(this.estacaoSelecionada))
                // FILTRO: O modelo da viatura é o selecionado?
				.filter(v -> v.getModelo().getModelo().equals(modelo))
                // Mapear para a matrícula
                .map(Viatura::getMatricula)
                // Coletar para uma lista
				.collect(Collectors.toList());

		// limpar as restantes listas
		matriculasModel.clear();
		indisponibilidadesModel.setRowCount(0);

		// adicionar as matrículas à lista
		matriculasModel.addAll(matriculas);
        
        // Se houver matrículas, seleciona a primeira (para popular a tabela de indisponibilidades)
        if (!matriculas.isEmpty()) {
            escolherAutomovel(matriculas.get(0));
        }
	}

	/**
	 * Método chamado quando o utilizador escolhe uma nova matricula
	 * * @param matricula a matrícula escolhida
	 */
	private void escolherAutomovel(String matricula) {
		indisponibilidadesModel.setRowCount(0); // limpar a tabela
        
        // 1. Encontrar a viatura
        Viatura v = bestAuto.getViatura(matricula);
        
        if (v == null || v.getIndisponibilidades() == null) {
            return; // Se não encontrar a viatura ou não tiver indisponibilidades, sair.
        }

		// 2. Para cada indisponibilidade da viatura, adicionar uma linha à tabela.
        for (Viatura.Indisponibilidade ind : v.getIndisponibilidades()) {
            LocalDateTime inicio = ind.intervalo.getInicio();
            LocalDateTime fim = ind.intervalo.getFim();
            String motivo = ind.motivo;
            adicionarLinha(inicio, fim, motivo);
        }
        
        // Código placeholder original removido:
		/*
		adicionarLinha(LocalDateTime.now().plusDays(1).withHour(17).withMinute(0), LocalDateTime.now().plusDays(2),
				"Deslocar para ALC");
		adicionarLinha(LocalDateTime.now().plusDays(2), LocalDateTime.now().plusDays(4), "Aluguer XX1234XX");
		adicionarLinha(LocalDateTime.now().plusDays(4), LocalDateTime.now().plusDays(5).withHour(9).withMinute(30),
				"Retornar a CTB");
        */
	}

	/**
	 * Método que adiciona uma linha à tabela de indisponibilidades
	 * * @param inicio data de inicio da indisponibilidade
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
	 * * @param nomes a lista dos nomes das estações suportadas
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
	 * * @param nomes os nomes das estações
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
	 * * @return o painel configurado
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
	 * * @return o painel configurado
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
		painel.add(new JScrollPane(listagem), BorderLayout.CENTER);
		return painel;
	}

	/**
	 * Configura o painel da escolha da matricula
	 * * @return o painel configurado
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
	 * * @return o painel configurado
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