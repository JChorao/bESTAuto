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
// IMPORTS ADICIONADOS
import java.util.HashSet;
import java.util.HashMap;
import pds.util.GeradorCodigos;
// FIM DOS IMPORTS ADICIONADOS

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

    // --- CAMPOS ATUALIZADOS ---
    // A estação atualmente selecionada
    private Estacao estacaoSelecionada;
    private Vector<Estacao> estacoesOrdenadas;

    // Guarda o resultado da pesquisa: Para um Modelo, qual Viatura específica está disponível
    private HashMap<Modelo, Viatura> viaturasParaAluguer;
    // Guarda a origem da viatura (true se for da central)
    private HashMap<Modelo, Boolean> eDaCentral;
    // --- FIM DA ATUALIZAÇÃO ---

    /**
     * Cria uma janela de aluguer
     */
    public JanelaAluguer(BESTAuto a) {
        bestAuto = a;
        setTitle("bEST Auto - A melhor experiência em aluguer de automóveis");

        // 1. Buscar estações e ordená-las por nome
        estacoesOrdenadas = a.getEstacoes(); // getEstacoes() retorna um Vector<Estacao>
        // Ordena o Vector de Estações pelo nome
        Collections.sort(estacoesOrdenadas, (e1, e2) -> e1.getNome().compareTo(e2.getNome()));

        // 2. Criar o vetor de nomes a partir da lista ordenada
        Vector<String> nomes = new Vector<>();
        for(Estacao e : estacoesOrdenadas){ 
                nomes.add(e.getNome());
        }
        
        setupJanela(nomes);

        // 3. Definir a estação inicial (índice 0)
        if (!estacoesOrdenadas.isEmpty()) {
            // Isto vai chamar o escolherEstacao(0) automaticamente
            // porque o 'setupEscolhaEstacao' define o 'setSelectedIndex(0)'
            escolherEstacao(0); 
        }
    }

    /**
     * Método chamado quando o utilizador muda de estação
     * * @param selecionadaIndex o índice da estação selecionada
     */
    private void escolherEstacao(int selecionadaIndex) {
        // Apanha a estação correta da nossa lista ordenada
        if (selecionadaIndex >= 0 && selecionadaIndex < estacoesOrdenadas.size()) {
            this.estacaoSelecionada = estacoesOrdenadas.get(selecionadaIndex);
        } else {
            this.estacaoSelecionada = null;
        }

        // limpar a pesquisa
        limparPesquisa();
    }

    /**
     * método chamado quando o utilizador pressiona o botão de apresentar horário
     */
    private void apresentarHorario() {
        // TODO ir buscar o horário da estação atual, em vez de usar um vazio
        // Deverá ser algo como:
        // HorarioSemanal hs = estacaoSelecionada.getHorario();
        HorarioSemanal hs = HorarioSemanal.sempreFechado(); // Placeholder

        apresentarHorario(hs);
    }

    /**
     * Método chamado quando o utilizador pressiona o botão de pesquisar
     */
    private void pesquisar() {
        limparPesquisa();

        // --- Validação das datas (como antes) ---
        LocalDateTime inicio = LocalDateTime.of(dataInicio, horasInicio);
        LocalDateTime fim = LocalDateTime.of(dataFim, horasFim);
        if (!inicio.isBefore(fim) || !dataInicio.isBefore(dataFim)) {
            JOptionPane.showMessageDialog(null,
                    "A data de fim tem de ser superior em 1 dia, pelo menos, à data de início");
            return;
        }
        intervaloSel = IntervaloTempo.entre(inicio, fim);

        // --- Início da Lógica de Pesquisa ---
        if (this.estacaoSelecionada == null) {
            JOptionPane.showMessageDialog(this, "Nenhuma estação selecionada.");
            return;
        }
        Categoria categoriaSel = (Categoria) categCb.getSelectedItem();
        Estacao central = this.estacaoSelecionada.getCentral();

        // 1. Inicializar os mapas de resultados
        viaturasParaAluguer = new HashMap<>();
        eDaCentral = new HashMap<>();

        // 2. Procurar na estação local
        for (Viatura v : bestAuto.getViaturas()) {
            Modelo m = v.getModelo();
            // Se a viatura pertence à estação, é da categoria certa E ESTÁ DISPONÍVEL
            if (v.getEstacao().equals(this.estacaoSelecionada) &&
                m.getCategoria().equals(categoriaSel) &&
                v.isDisponivel(intervaloSel)) { // <-- VERIFICAÇÃO CHAVE
                
                // Adiciona o primeiro carro encontrado desse modelo
                if (!viaturasParaAluguer.containsKey(m)) {
                    viaturasParaAluguer.put(m, v);
                    eDaCentral.put(m, false); // 'false' = é local
                }
            }
        }

        // 3. Procurar na estação central (se existir)
        if (central != null) {
            for (Viatura v : bestAuto.getViaturas()) {
                Modelo m = v.getModelo();
                // Se é da central, da categoria certa, ESTÁ DISPONÍVEL E AINDA NÃO FOI ENCONTRADO
                if (v.getEstacao().equals(central) &&
                    m.getCategoria().equals(categoriaSel) &&
                    v.isDisponivel(intervaloSel) && // <-- VERIFICAÇÃO CHAVE
                    !viaturasParaAluguer.containsKey(m)) { // <-- Não adicionar se já temos local

                    viaturasParaAluguer.put(m, v);
                    eDaCentral.put(m, true); // 'true' = é da central
                }
            }
        }

        // 4. Apresentar os resultados
        if (viaturasParaAluguer.isEmpty()) {
            alugueres.add(new JLabel("-- SEM RESULTADOS --", JLabel.CENTER));
        } else {
            // Itera sobre os Modelos encontrados
            for (Modelo m : viaturasParaAluguer.keySet()) {
                
                // TODO: Calcular o preço total do aluguer (pág 2 e 3 do PDF)
                // De momento, vou usar o preço diário como placeholder.
                long precoCalculado = m.getPreco();
                // Se for da central, adicionar custo (2 dias extra)
                if (eDaCentral.get(m)) {
                    // Este cálculo é mais complexo, por agora apenas adicionamos o valor
                    // precoCalculado += m.getPreco() * 2;
                }

                PainelAluguer pa = new PainelAluguer(
                    m.getModelo(),
                    m.getLotacao(),
                    m.getBagagem(),
                    precoCalculado, // Usar o preço (aqui deve ser o preço TOTAL calculado)
                    m               // Passar o Modelo como 'valor' para o 'alugar'
                );
                alugueres.add(pa);
            }
        }

        alugueres.revalidate();
        alugueres.repaint();
    }


    /**
     * Método chamado quando o utilizador pressiona o botão de alugar.
     * * @param valor o objeto selecionado. Este valor foi o usado
     * quando se criou o painel de aluguer
     */
    private void alugar(Object valor) {
        // 1. Obter o Modelo e a Viatura específica que foi reservada
        Modelo modeloAlugado = (Modelo) valor;
        Viatura viaturaAlugada = viaturasParaAluguer.get(modeloAlugado);
        Boolean isCentralWrapper = eDaCentral.get(modeloAlugado);

        // Se por algum motivo não encontrar a viatura ou o booleano, parar.
        if (viaturaAlugada == null || isCentralWrapper == null) {
            JOptionPane.showMessageDialog(this, "Erro ao processar aluguer. Tente pesquisar novamente.");
            return;
        }
        boolean isCentral = isCentralWrapper; // Converter de Boolean para boolean

        // 2. Gerar código de Aluguer
        String code = GeradorCodigos.gerarCodigo(8); // "GeradorCodigos" de pds.util
        String motivoAluguer = "Aluguer " + code;
        
        // Encontra a matrícula da viatura (necessário para o pop-up)
        String matricula = bestAuto.viaturas.entrySet().stream()
                .filter(entry -> entry.getValue().equals(viaturaAlugada))
                .map(java.util.Map.Entry::getKey)
                .findFirst().orElse("??-??-??");

        // 3. Obter datas da reserva (guardadas em 'intervaloSel' pelo 'pesquisar')
        LocalDateTime inicioReserva = intervaloSel.getInicio();
        LocalDateTime fimReserva = intervaloSel.getFim();

        // 4. Adicionar indisponibilidade PRINCIPAL
        // Esta é a indisponibilidade do aluguer em si
        viaturaAlugada.adicionarIndisponibilidade(
            IntervaloTempo.entre(inicioReserva, fimReserva), 
            motivoAluguer
        );

        // 5. Adicionar indisponibilidades de TRÂNSITO (se for da central)
        // (Lógica da pág 6 do PDF)
        if (isCentral) {
            // "desde as 17:00 do dia anterior"
            LocalDateTime inicioTransito = inicioReserva.toLocalDate().minusDays(1).atTime(17, 0);
            viaturaAlugada.adicionarIndisponibilidade(
                IntervaloTempo.entre(inicioTransito, inicioReserva), 
                "Deslocar para " + estacaoSelecionada.getNome() // Motivo
            );

            // "até às 9:30 do dia seguinte"
            LocalDateTime fimTransito = fimReserva.toLocalDate().plusDays(1).atTime(9, 30);
            viaturaAlugada.adicionarIndisponibilidade(
                IntervaloTempo.entre(fimReserva, fimTransito), 
                "Retornar a " + viaturaAlugada.getEstacao().getNome() // Motivo (Estação de origem)
            );
        }

        // 6. Apresentar confirmação
        JOptionPane.showMessageDialog(this,
                "<html>Obrigado por usar os nossos serviços!<br>Aluguer " + code + ", carro será " + matricula
                        + "</html>");
        
        // 7. Limpar e pesquisar de novo
        // Isto cumpre o seu pedido: a pesquisa é re-executada
        // e a viatura que acabou de ser alugada já não vai aparecer.
        limparPesquisa();
        pesquisar(); 
    }

    /**
     * Cria e configura a janela
     * * @param nomes nomes das estações a usar
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
     * * @return o painel configurado
     */
    private JPanel setupEscolhaTempos() {
        String horas[] = new String[48];
        for (int h = 0; h < 24; h++) {
            horas[h * 2] = String.format("%02d:00", h);
            horas[h * 2 + 1] = String.format("%02d:30", h);
        }
        LocalTime t = LocalTime.now();
        int indiceHora = t.getHour() * 2 + (t.getMinute() >= 30 ? 1 : 0); // Correção: >30 para >=30

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
        horasInicio = LocalTime.of(horasIniCb.getSelectedIndex() / 2, 30 * (horasIniCb.getSelectedIndex() % 2)); // Inicializar
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
        horasFim = LocalTime.of(horasFimCb.getSelectedIndex() / 2, 30 * (horasFimCb.getSelectedIndex() % 2)); // Inicializar
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
     * * @param nomes os nomes das estações
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
     * * @param h o horário da estação
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
        // Limpar também os resultados da pesquisa anterior
        if (viaturasParaAluguer != null) {
            viaturasParaAluguer.clear();
        }
        if (eDaCentral != null) {
            eDaCentral.clear();
        }
        // Forçar a atualização visual imediata
        alugueres.revalidate();
        alugueres.repaint();
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