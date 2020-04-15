package logicsim;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 * input gate for modules
 * 
 * will be created during module creation. connect inputs to define module
 * inputs.
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class MODIN extends Gate {
	static final long serialVersionUID = -2338870902247206767L;

	public MODIN() {
		super();
		type = "modin";
		label = "INPUTS";
		height = 170;
		backgroundColor = Color.LIGHT_GRAY;
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
		//forward event to the appropriate output
		int target = p.number + getNumInputs();
		LSLevelEvent evt = new LSLevelEvent(this, p.getLevel());
		getPin(target).changedLevel(evt);
	}

	@Override
	public void loadLanguage() {
		I18N.addGate(I18N.ALL, type, I18N.TITLE, "Inputs");
		I18N.addGate(I18N.ALL, type, I18N.DESCRIPTION, "Input Gate for Modules");
		I18N.addGate("de", type, I18N.TITLE, "Moduleingänge");
		I18N.addGate("de", type, I18N.DESCRIPTION, "Eingangsgatter für Module");
	}

}