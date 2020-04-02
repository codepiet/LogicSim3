package logicsim.gates;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import logicsim.Gate;

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