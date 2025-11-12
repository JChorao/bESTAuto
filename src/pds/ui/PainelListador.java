package pds.ui;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.JPanel;

@SuppressWarnings("serial")
/**
 * Classe auxiliar que gere um painel onde são listadas várias informações
 */
public class PainelListador extends JPanel {

	private JPanel conteudo;

	public PainelListador() {
		super();
		setLayout(new GridBagLayout());
		conteudo = new JPanel(new GridLayout(0, 1));
		JPanel espaco = new JPanel();
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.gridx = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add(conteudo, gbc);
		gbc.weightx = 1;
		gbc.weighty = 1;
		add(espaco, gbc);
	}

	@Override
	public Component add(Component comp) {
		conteudo.add(comp);
		if (getTopLevelAncestor() != null)
			getTopLevelAncestor().validate();
		return comp;
	}

	@Override
	public void removeAll() {
		conteudo.removeAll();
		if (getTopLevelAncestor() != null)
			getTopLevelAncestor().validate();
	}
}
