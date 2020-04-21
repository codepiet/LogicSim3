package logicsim;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

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
	private static final String INPUT_LABEL = "inputlabel";

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
		// forward event to the appropriate output
		int target = p.number + getNumInputs();
		LSLevelEvent evt = new LSLevelEvent(this, p.getLevel());
		getPin(target).changedLevel(evt);
	}

	@Override
	public void rotate() {
		// don't rotate
	}

	@Override
	public void mirror() {
		// don't mirror
	}

	@Override
	public void draw(Graphics2D g2) {
		super.draw(g2);
		g2.setColor(Color.GREEN);
		// draw klick areas if pin is connected
		for (Pin p : getInputs()) {
			Pin pout = getPin(p.number + 16);
			if (pout.isConnected() && p.getProperty(TEXT) == null)
				g2.fill(new Rectangle(getX() + CONN_SIZE + 1, p.getY() - 4, 8, 8));
		}
	}

	@Override
	public void loadLanguage() {
		I18N.addGate(I18N.ALL, type, I18N.TITLE, "Inputs");
		I18N.addGate(I18N.ALL, type, I18N.DESCRIPTION,
				"Input Gate for Modules - click label area to set an input pin's label");
		I18N.addGate(I18N.ALL, type, INPUT_LABEL, "Label");
		I18N.addGate("de", type, I18N.TITLE, "Moduleingänge");
		I18N.addGate("de", type, I18N.DESCRIPTION,
				"Eingangsgatter für Module - klicke den Labelbereich um einen Pin zu benennen.");
	}

}