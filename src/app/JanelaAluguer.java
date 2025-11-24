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
import java.util.HashMap;
import java.util.LinkedHashMap;

import pds.util.GeradorCodigos;
import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;

import aluguer.BESTAuto;
import aluguer.Categoria;
import aluguer.Estacao;
import aluguer.Modelo;
import aluguer.Viatura;
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

    private Estacao estacaoSelecionada;
    private HashMap<String, Estacao> estacoes;
    private HashMap<Modelo, Viatura> viaturasParaAluguer = new HashMap<>();
    private HashMap<Modelo, Boolean> eDaCentral = new HashMap<>();

    /**
     * Cria uma janela de aluguer
     */
    public JanelaAluguer(BESTAuto a) {
        bestAuto = a;
        setTitle("Aluguer - bEST Auto - A melhor experiência em aluguer de automóveis");

        Vector<String> nomes;
        try {
            estacoes = a.getEstacoes();

            estacoes = estacoes.entrySet().stream()
                    .sorted(Map.Entry.comparingByValue(Comparator.comparing(Estacao::getNome)))
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (e1, e2) -> e1,
                            LinkedHashMap::new));

            nomes = new Vector<>(estacoes.values().stream()
                    .map(Estacao::getNome)
                    .collect(Collectors.toList()));

        } catch (Exception e) {
            nomes = new Vector<>();
            nomes.add("Alcains");
            nomes.add("Castelo Branco");
            Collections.sort(nomes);
        }
        setupJanela(nomes);

    }

    /**
     * Método chamado quando o utilizador muda de estação
     * * @param selecionadaIndex o índice da estação selecionada
     */
    private void escolherEstacao(int selecionadaIndex) {
        // Selecionar a estação adequada
        if (selecionadaIndex >= 0 && selecionadaIndex < estacoes.size()) {
            this.estacaoSelecionada = estacoes.values().toArray(new Estacao[0])[selecionadaIndex];
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
        // ir buscar o horário da estação atual em vez de vazio
        if (this.estacaoSelecionada == null) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma estação.");
            return;
        }

        HorarioSemanal hs = estacaoSelecionada.getHorario();

        apresentarHorario(hs);
    }

    // =====================
    // MÉTODOS AUXILIARES
    // =====================

    private IntervaloTempo criarValidarIntervaloTempo() {
        LocalDateTime inicio = LocalDateTime.of(dataInicio, horasInicio);
        LocalDateTime fim = LocalDateTime.of(dataFim, horasFim);

        // Validação contra datas passadas
        if (inicio.isBefore(LocalDateTime.now())) {
            JOptionPane.showMessageDialog(null,
                    "A data e hora de recolha não podem ser anteriores ao momento atual.");
            return null;
        }

        // Validação fim > inicio
        if (!inicio.isBefore(fim) || inicio.isEqual(fim)) {
            JOptionPane.showMessageDialog(null,
                    "A data de fim tem de ser superior a data de início.");
            return null;
        }

        return IntervaloTempo.entre(inicio, fim);
    }

    /**
     * Procura viaturas disponíveis na estação selecionada e, se aplicável, na
     * central,
     * para a categoria e intervalo de tempo selecionados.
     * Popula os mapas viaturasParaAluguer e eDaCentral.
     */

    private void procuraViaturasDisponiveis(Categoria categoriaSel, Estacao localEstacao, Estacao central,
            IntervaloTempo intervalo) {

        viaturasParaAluguer.clear();
        eDaCentral.clear();

        // Procurar na estação local
        for (Viatura v : bestAuto.getViaturas()) {
            Modelo m = v.getModelo();
            if (v.getEstacao().equals(localEstacao) &&
                    m.getCategoria().equals(categoriaSel) &&
                    v.isDisponivel(intervalo)) {

                if (!viaturasParaAluguer.containsKey(m)) {
                    viaturasParaAluguer.put(m, v);
                    eDaCentral.put(m, false);
                }
            }
        }

        // Procurar na estação central (só se não encontrou um modelo local)
        if (central != null) {
            for (Viatura v : bestAuto.getViaturas()) {
                Modelo m = v.getModelo();
                if (v.getEstacao().equals(central) &&
                        m.getCategoria().equals(categoriaSel) &&
                        v.isDisponivel(intervalo) &&
                        !viaturasParaAluguer.containsKey(m)) {

                    viaturasParaAluguer.put(m, v);
                    eDaCentral.put(m, true);
                }
            }
        }
    }

    /**
     * Calcula o preço total final para um Modelo, incluindo sobretaxa da central e
     * custos de extensão.
     */

    private long calculaPrecoFinal(Modelo m, long diasBaseAluguer, boolean isFromCentral, Estacao localEstacao,
            LocalDateTime inicio, LocalDateTime fim) {
        long precoDiario = m.getPreco(); // Preço em cêntimos
        long precoTotal = diasBaseAluguer * precoDiario; // A) Preço Base

        // B) Sobretaxa da Central
        if (isFromCentral) {
            // Adicionar 2 dias extras (Pág 2, Passos 2)
            precoTotal += 2 * precoDiario;
        }

        // C) Custo por Extensão de Horário
        long custoRecolha = calcularCustoExtensao(inicio, localEstacao, m);
        precoTotal += custoRecolha;

        long custoDevolucao = calcularCustoExtensao(fim, localEstacao, m);
        precoTotal += custoDevolucao;

        return precoTotal;
    }

    // =======================
    // MÉTODOS Do Professor
    // ========================

    /**
     * Método chamado quando o utilizador pressiona o botão de pesquisar
     */

    private void pesquisar() {
        limparPesquisa();

        intervaloSel = criarValidarIntervaloTempo();
        if (intervaloSel == null) {
            return;
        }

        if (this.estacaoSelecionada == null) {
            JOptionPane.showMessageDialog(this, "Nenhuma estação selecionada.");
            return;
        }

        // Ver data de aluguer
        if (!estaEmHorarioNormalOuExtra(intervaloSel.getInicio(), estacaoSelecionada)) {
            JOptionPane.showMessageDialog(null, "A estação não está aberta no horário de recolha.");
            return;
        }
        // Ver data de entrega
        if (!estaEmHorarioNormalOuExtra(intervaloSel.getFim(), estacaoSelecionada)) {
            JOptionPane.showMessageDialog(null, "A estação não está aberta no horário de entrega.");
            return;
        }

        Categoria categoriaSel = (Categoria) categCb.getSelectedItem();
        Estacao central = this.estacaoSelecionada.getCentral();

        procuraViaturasDisponiveis(categoriaSel, estacaoSelecionada, central, intervaloSel);

        long diasBaseAluguer = calcularDiasAluguer(intervaloSel);

        // Resultados e calcular o preço final

        if (viaturasParaAluguer.isEmpty()) {
            alugueres.add(new JLabel("-- SEM RESULTADOS --", JLabel.CENTER));
        } else {

            // Ordenar os Modelos por nome antes de iterar
            List<Modelo> modelosOrdenados = viaturasParaAluguer.keySet().stream()
                    .sorted(Comparator.comparing(Modelo::getModeloString))
                    .collect(Collectors.toList());

            for (Modelo m : modelosOrdenados) {
                boolean isFromCentral = eDaCentral.get(m);
                long precoTotal = calculaPrecoFinal(m, diasBaseAluguer, isFromCentral,
                        estacaoSelecionada, intervaloSel.getInicio(),
                        intervaloSel.getFim());

                PainelAluguer pa = new PainelAluguer(
                        m.getModeloString(),
                        m.getLotacao(),
                        m.getBagagem(),
                        precoTotal,
                        m);
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
       
        Modelo modeloAlugado = (Modelo) valor;
        Viatura viaturaAlugada = viaturasParaAluguer.get(modeloAlugado);
        Boolean isCentralWrapper = eDaCentral.get(modeloAlugado);

        if (viaturaAlugada == null || isCentralWrapper == null) {
            JOptionPane.showMessageDialog(this, "Erro ao processar aluguer. Tente pesquisar novamente.");
            return;
        }
        
        //Gerar código de Aluguer e obter matrículas/datas
        String code = GeradorCodigos.gerarCodigo(8);
        String motivoAluguer = "Aluguer " + code;
        String matricula = viaturaAlugada.getMatricula();
        LocalDateTime inicioReserva = intervaloSel.getInicio();
        LocalDateTime fimReserva = intervaloSel.getFim();

        //Adicionar indisponibilidade
        viaturaAlugada.adicionarIndisponibilidade(
                IntervaloTempo.entre(inicioReserva, fimReserva),
                motivoAluguer);

        
        //Apresentar confirmação
        JOptionPane.showMessageDialog(this,
                "<html>Obrigado por usar os nossos serviços!<br>Aluguer " + code + ", carro será " + matricula
                        + "</html>");

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
     * 
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
        // O null check é agora redundante, mas foi mantido por segurança no caso de
        // reatribuição.
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

    // =========================================================================
    // MÉTODOS DE CÁLCULO DE PREÇO (Inalterados, usados nos novos métodos)
    // =========================================================================

    /**
     * Calcula o número de dias (blocos de 24 horas) para efeitos de preço.
     * Mesmo um bloco incompleto conta como um dia completo.
     * 
     * @param intervalo O intervalo de tempo.
     * @return O número de dias a pagar.
     */
    private long calcularDiasAluguer(IntervaloTempo intervalo) {
        // 24h = 86400 segundos
        final long segundosPorDia = 86400;
        long duracaoSegundos = intervalo.duracao().getSeconds();

        // Número de dias completos
        long dias = duracaoSegundos / segundosPorDia;

        // Se houver resto (mesmo que 1 segundo), conta como mais um dia.
        if (duracaoSegundos % segundosPorDia > 0) {
            dias++;
        }

        return dias;
    }

    /**
     * Verifica se um momento (data/hora) está no horário normal OU no horário extra
     * de uma estação.
     * Necessário para a validação inicial do aluguer.
     * 
     * @param time    O momento a verificar.
     * @param estacao A estação.
     * @return true se o momento está coberto pelo horário normal ou extra, false se
     *         a estação está fechada.
     */
    private boolean estaEmHorarioNormalOuExtra(LocalDateTime time, Estacao estacao) {
        HorarioSemanal hs = estacao.getHorario();

        // Verfica horario normal
        if (hs.estaDentroHorario(time)) {
            return true;
        }

        // Verifica horario extra
        String tipoExtensao = estacao.getTipoExtensao();

        if (tipoExtensao == null) {
            return false; // Sem extensão definida
        }

        if (tipoExtensao.equals("total")) {
            return true; // Está sempre disponível se houver extensão total
        }

        if (tipoExtensao.equals("horas")) {
            int maxHoras = estacao.getMaxHorasExtensao();

            // Obter o HorarioDiario para o dia
            HorarioDiario hd = hs.getHorarioDia(time.getDayOfWeek());

            // Se tiver fechado e nao tiver tipo de horario "total" - return false
            if (hd.eVazio()) {
                return false;
            }

            // Horas de início/fim do horário normal para o dia.
            LocalTime hora = LocalTime.from(time);
            LocalTime inicioNormal = hd.getInicio();
            LocalTime fimNormal = hd.getFim();

            // Antes da abertura
            if (hora.isBefore(inicioNormal)) {
                LocalTime limiteInicio = inicioNormal.minusHours(maxHoras);

                // Verficar limite de abertura antecipada
                if (hora.isAfter(limiteInicio) || hora.equals(limiteInicio)) {
                    return true;
                }
            }

            // Depois do fecho
            if (hora.isAfter(fimNormal)) {
                LocalTime limiteFim = fimNormal.plusHours(maxHoras);

                // Verifica limite de fecho alargado
                if (hora.isBefore(limiteFim) || hora.equals(limiteFim)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Calcula o custo de Extensão para uma hora específica.
     * 
     * @param time    O momento (recolha/devolução) a verificar.
     * @param estacao A estação local.
     * @param modelo  O modelo da viatura (necessário para o cálculo "variavel").
     * @return O custo extra em cêntimos (long), ou 0 se não houver custo extra.
     */
    private long calcularCustoExtensao(LocalDateTime time, Estacao estacao, Modelo modelo) {

        // Verfica horario normal
        if (estacao.getHorario().estaDentroHorario(time)) {
            return 0;
        }

        // Verifica horario extra

        if (!estaEmHorarioNormalOuExtra(time, estacao)) {
            return 0; // Fora do horário normal E fora do período de extensão = Fechado
        }

        // Calcular custo de extensão
        String tipoPreco = estacao.getTipoPrecoExtensao();

        if ("taxa".equals(tipoPreco)) {
            // Custo fixo
            return estacao.getPrecoTaxaExtensao();
        } else if ("variavel".equals(tipoPreco)) {
            // Custo Variável (50% do preço diário do modelo)
            return modelo.getPreco() / 2;
        }

        return 0;
    }

    // =========================================================================
    // CLASSE INTERNA - PainelAluguer (Inalterada)
    // =========================================================================

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

            // O preço está em cêntimos, divide-se por 100.0f para ter Euros
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