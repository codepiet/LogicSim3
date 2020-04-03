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
		setNumInputs(16);
		setNumOutputs(16);
	}

	@Override
	protected void drawLabel(Graphics2D g2, String lbl, Font font) {
		g2.setFont(bigFont);
		int sw = g2.getFontMetrics().stringWidth(label);
		drawRotate(g2, getX() + 36, getY() + height / 2 + sw / 2, -90, label);
	}

	@Override
	public void simulate() {
		super.simulate();

		for (int i = 0; i < getNumInputs(); i++) {
			setOutputLevel(i, getInputLevel(i));
		}
	}

}