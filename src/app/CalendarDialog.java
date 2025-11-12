package app;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Dialog que apresenta um calendário e permite a escolha de um dia pelo
 * utilizador. Não permite escolher datas anteriores à data atual.
 */
public class CalendarDialog extends JDialog {
    // variáveis para a interface
    private YearMonth currentYearMonth;
    private JLabel monthLabel;
    private JButton prevButton;
    private JButton nextButton;
    private JTable calendarTable;

    private int diaSel = -1; // qual o dia escolhido
    private LocalDate selectedDate; // a data escolhida quando se pressiona ok
    private LocalDate currentSelDate; // a data atualmente escolhida

    /**
     * Cria uma janela com a data atual como sendo a data inicialmente escolhida
     */
    public CalendarDialog() {
        this(LocalDate.now());
    }

    /**
     * Cria uma janela, com uma data atual inicialmente escolhida
     * 
     * @param inicio a data que aparece como inicialmente escolhida
     */
    public CalendarDialog(LocalDate inicio) {
        currentYearMonth = YearMonth.from(inicio);
        currentSelDate = inicio;

        setTitle("Escolha a data pretendida");
        setSize(400, 230);
        setLocationRelativeTo(null);

        monthLabel = new JLabel("", JLabel.CENTER);
        calendarTable = new JTable(6, 7) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        calendarTable.setCellSelectionEnabled(true);
        calendarTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        ListSelectionListener selecionar = e -> {
            if (e.getValueIsAdjusting() || calendarTable.getSelectedRow() == -1
                    || calendarTable.getSelectedColumn() == -1)
                return;
            selecionarData(calendarTable.getValueAt(calendarTable.getSelectedRow(), calendarTable.getSelectedColumn()));
            calendarTable.repaint();
        };

        calendarTable.getSelectionModel().addListSelectionListener(selecionar);
        calendarTable.getColumnModel().getSelectionModel().addListSelectionListener(selecionar);

        prevButton = new JButton("<");
        prevButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentYearMonth.minusMonths(1).isBefore(YearMonth.now()))
                    return;
                currentYearMonth = currentYearMonth.minusMonths(1);
                updateCalendar();
            }
        });

        nextButton = new JButton(">");
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentYearMonth = currentYearMonth.plusMonths(1);
                updateCalendar();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(prevButton);
        buttonPanel.add(monthLabel);
        buttonPanel.add(nextButton);

        updateCalendar();

        setLayout(new BorderLayout());
        add(buttonPanel, BorderLayout.NORTH);
        add(new JScrollPane(calendarTable), BorderLayout.CENTER);
        JButton okBt = new JButton("Ok");
        okBt.addActionListener(e -> testarFechar());
        add(okBt, BorderLayout.SOUTH);
    }

    /**
     * Indica se o utilizador escolheu uma data (pode ter fechado a janela sem
     * escolher data)
     * 
     * @return true, se o utilizador pressionou em Ok
     */
    public boolean hasSelectedDate() {
        return selectedDate != null;
    }

    /**
     * Retorna a data que o utilizador escolheu, se este escolheu uma data, senão
     * retorna null
     * 
     * @return a data que o utilizador escolheu, ou null se abortou a escolha
     */
    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    /** método chamado quando se pressiona o ok para ver se está tudo ok */
    private void testarFechar() {
        if (diaSel == -1)
            return;
        LocalDate t = LocalDate.of(currentYearMonth.getYear(), currentYearMonth.getMonthValue(), diaSel);
        if (t.isBefore(LocalDate.now()))
            return;
        selectedDate = t;
        setVisible(false);
    }

    /**
     * Selecionou um elemento na tabela (pode ter sido um espaço em branco)
     * 
     * @param value o valor na célula escolhida
     */
    private void selecionarData(Object value) {
        // se for um número
        if (value.getClass() == Integer.class) {
            diaSel = (int) value;
            LocalDate t = LocalDate.of(currentYearMonth.getYear(), currentYearMonth.getMonthValue(), diaSel);
            if (t.isBefore(LocalDate.now()))
                return;
            currentSelDate = t;
        } else
            diaSel = -1;
    }

    /** Atualiza o desenho do calendário */
    private void updateCalendar() {
        monthLabel.setText(currentYearMonth.getMonthValue() + " - " + currentYearMonth.getYear());

        LocalDate firstDayOfMonth = currentYearMonth.atDay(1);
        int firstDayOfWeek = firstDayOfMonth.getDayOfWeek().getValue();

        int daysInMonth = currentYearMonth.lengthOfMonth();
        int numRows = (int) Math.ceil((firstDayOfWeek + daysInMonth) / 7.0);

        String[] columnNames = { "Domingo", "Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado" };
        Object[][] data = new Object[numRows][7];

        int day = 1 - firstDayOfWeek;
        for (int i = 0; i < numRows; i++) {
            for (int j = 0; j < 7; j++) {
                if (day > 0 && day <= daysInMonth) {
                    data[i][j] = day;
                } else {
                    data[i][j] = "";
                }
                day++;
            }
        }

        calendarTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
        calendarTable.setDefaultRenderer(calendarTable.getColumnClass(0), new CalendarCellRenderer());
    }

    private class CalendarCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row,
                    column);
            label.setBackground(table.getBackground());
            if (value != null && value.getClass() == Integer.class) {
                int day = (int) value;
                // se for antes de hoje fica a conzento
                if (day < LocalDate.now().getDayOfMonth() &&
                        currentYearMonth.equals(YearMonth.now())) {
                    label.setForeground(Color.GRAY);
                } // a data de hoje fica a vermelho
                else if (day == LocalDate.now().getDayOfMonth() &&
                        currentYearMonth.equals(YearMonth.now())) {
                    label.setForeground(Color.RED);
                } else {
                    label.setForeground(table.getForeground());
                }
                // a data atualmente escolhida fica com fundo a ciano
                if (isSelected || (day == currentSelDate.getDayOfMonth()
                        && currentSelDate.getMonthValue() == currentYearMonth.getMonthValue()
                        && currentSelDate.getYear() == currentYearMonth.getYear())) {
                    label.setBackground(Color.CYAN);
                }
            }
            return label;
        }
    }
}