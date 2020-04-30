package logicsim;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.JOptionPane;

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
	private int pincount;
	private static final String PINCOUNT = "pincount";
	private static final int PINCOUNT_DEFAULT = 16;

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
		int numberOfInputs = getInputs().size();
		g2.setColor(Color.GREEN);
		// draw click areas if pin is connected
		for (Pin p : getInputs()) {
			Pin pout = getPin(p.number + numberOfInputs);
			if (pout.isConnected() && p.getProperty(TEXT) == null)
				g2.fill(new Rectangle(getX() + CONN_SIZE + 1, p.getY() - 4, 8, 8));
		}
	}

	@Override
	protected void loadProperties() {
		pincount = getPropertyIntWithDefault(PINCOUNT, PINCOUNT_DEFAULT);
		int inputcount = getInputs().size();
		if (pincount != inputcount) {
			if (pincount < inputcount) {
				// disconnect wires
				for (int i = inputcount - 1; i >= pincount; i--) {
					// disconnect all parts from input and output pin
					getPin(i + inputcount).disconnect();
					getPin(i).disconnect();
				}
				for (int i = inputcount - 1; i >= pincount; i--) {
					pins.remove(i + inputcount);
				}
				for (int i = inputcount - 1; i >= pincount; i--) {
					pins.remove(i);
				}
				for (int i = 0; i < pincount; i++) {
					Pin p = pins.get(pincount + i);
					p.number = pincount + i;
				}
			} else {
				// pincount is greater than it is now
				for (int i = inputcount; i < pincount; i++) {
					Pin p = new Pin(getX(), 0, this, i);
					p.paintDirection = Pin.RIGHT;
					p.ioType = Pin.INPUT;
					pins.insertElementAt(p, i);
				}
				for (int i = pincount; i < pins.size(); i++) {
					Pin p = pins.get(i);
					p.number = i;
				}
				for (int i = inputcount + pincount; i < pincount * 2; i++) {
					Pin p = new Pin(getX() + width, 0, this, i);
					p.paintDirection = Pin.LEFT;
					p.ioType = Pin.OUTPUT;
					pins.insertElementAt(p, i);
				}
			}
			// adjust height
			if (pincount < 6)
				height = 60;
			else
				height = pincount * 10 + 10;
			// reposition all pins (only y)
			int numIn = getNumInputs();
			int numOut = getNumOutputs();
			for (Pin c : getInputs()) {
				c.setY(getY() + getConnectorPosition(c.number, numIn, Gate.VERTICAL));
			}
			for (Pin c : getOutputs()) {
				c.setY(getY() + getConnectorPosition(c.number - numIn, numOut, Gate.VERTICAL));
			}
		}
	}

	@Override
	public boolean showPropertiesUI(Component frame) {
		super.showPropertiesUI(frame);
		String h = (String) JOptionPane.showInputDialog(frame, I18N.getString(type, PINCOUNT), I18N.tr(Lang.SETTINGS),
				JOptionPane.QUESTION_MESSAGE, null, null, Integer.toString(pincount));
		if (h != null && h.length() > 0) {
			pincount = Integer.parseInt(h);
			setPropertyInt(PINCOUNT, pincount);
			loadProperties();
		}
		return true;
	}

	@Override
	public void loadLanguage() {
		I18N.addGate(I18N.ALL, type, I18N.TITLE, "Inputs");
		I18N.addGate(I18N.ALL, type, I18N.DESCRIPTION,
				"Input Gate for Modules - click label area to set an input pin's label");
		I18N.addGate(I18N.ALL, type, PINCOUNT, "Number of Input Pins");
		I18N.addGate("de", type, I18N.TITLE, "Moduleingänge");
		I18N.addGate("de", type, I18N.DESCRIPTION,
				"Eingangsgatter für Module - klicke den Labelbereich um einen Pin zu benennen.");
		I18N.addGate("de", type, PINCOUNT, "Anzahl Eingänge");
	}

}