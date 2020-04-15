package logicsim;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 * output gate for modules
 * 
 * will be created during module creation. connect outputs to define module
 * outputs.
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class MODOUT extends Gate {
	static final long serialVersionUID = 1824440628969344103L;

	public MODOUT() {
		super();
		type = "modout";
		label = "OUTPUTS";
		backgroundColor = Color.LIGHT_GRAY;
		height = 170;
		createInputs(16);
		createOutputs(16);
	}

	@Override
	protected void drawLabel(Graphics2D g2, String lbl, Font font) {
		g2.setFont(bigFont);
		int sw = g2.getFontMetrics().stringWidth(label);
		drawRotate(g2, getX() + 36, getY() + height / 2 + sw / 2, -90, label);
	}

	@Override
	public void changedLevel(LSLevelEvent e) {
		Pin p = (Pin) e.source;
		int target = p.number + getNumInputs();
		LSLevelEvent evt = new LSLevelEvent(this, p.getLevel());
		getPin(target).changedLevel(evt);
	}

	@Override
	public void loadLanguage() {
		I18N.addGate(I18N.ALL, type, I18N.TITLE, "Outputs");
		I18N.addGate(I18N.ALL, type, I18N.DESCRIPTION, "Output Gate for Modules");
		I18N.addGate("de", type, I18N.TITLE, "Modulausgänge");
		I18N.addGate("de", type, I18N.DESCRIPTION, "Ausgangsgatter für Module");
	}

}