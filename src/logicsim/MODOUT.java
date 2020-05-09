package logicsim;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

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
	private static final String INPUT_LABEL = "inputlabel";
	private int pincount;
	private static final String PINCOUNT = "pincount";
	private static final int PINCOUNT_DEFAULT = 16;

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
	public void draw(Graphics2D g2) {
		super.draw(g2);
		g2.setColor(Color.GREEN);
		int numberOfInputs = getInputs().size();
		// draw click areas if pin is connected
		for (Pin p : getOutputs()) {
			Pin pin = getPin(p.number - numberOfInputs);
			if (pin.isConnected() && p.getProperty(TEXT) == null)
				g2.fill(new Rectangle(getX() + width - CONN_SIZE - 9, p.getY() - 4, 8, 8));
		}
	}

	@Override
	protected void drawLabel(Graphics2D g2, String lbl, Font font) {
		g2.setFont(bigFont);
		WidgetHelper.drawStringRotated(g2, label, xc, yc, WidgetHelper.ALIGN_CENTER, -90);
	}

	@Override
	public void changedLevel(LSLevelEvent e) {
		Pin p = (Pin) e.source;
		int target = p.number + getNumInputs();
		LSLevelEvent evt = new LSLevelEvent(this, p.getLevel());
		getPin(target).changedLevel(evt);
	}

	@Override
	protected void loadProperties() {
		pincount = getPropertyIntWithDefault(PINCOUNT, PINCOUNT_DEFAULT);
		int inputcount = getInputs().size();
		if (pincount != inputcount) {
			if (pincount < inputcount) {
				// disconnect wires
				for (int i = inputcount; i > pincount; i--) {
					// disconnect all parts from input and output pin
					getPin(i + inputcount).disconnect();
					getPin(i).disconnect();
				}
			}
		}
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
	public void loadLanguage() {
		I18N.addGate(I18N.ALL, type, I18N.TITLE, "Outputs");
		I18N.addGate(I18N.ALL, type, I18N.DESCRIPTION, "Output Gate for Modules");
		I18N.addGate(I18N.ALL, type, INPUT_LABEL, "Label");
		I18N.addGate("de", type, I18N.TITLE, "Modulausgänge");
		I18N.addGate("de", type, I18N.DESCRIPTION, "Ausgangsgatter für Module");
	}

}