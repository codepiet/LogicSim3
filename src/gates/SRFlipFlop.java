package gates;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import logicsim.Gate;
import logicsim.I18N;
import logicsim.LSLevelEvent;
import logicsim.Pin;

/**
 * SR-FlipFlop for LogicSim
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class SRFlipFlop extends Gate {
	static final long serialVersionUID = 1049162074522360589L;

	Color bg = Color.white;

	public SRFlipFlop() {
		super("flipflops");
		type = "srff";
		createInputs(3);
		createOutputs(2);

		getPin(0).label = "S";
		getPin(1).label = "R";
		getPin(2).label = Pin.POS_EDGE_TRIG;

		getPin(3).label = "Q";
		getPin(3).moveBy(0, 10);

		getPin(4).label = "/Q";
		getPin(4).setLevelType(Pin.INVERTED);
		getPin(4).moveBy(0, -10);
	}

	@Override
	protected void drawLabel(Graphics2D g2, String lbl, Font font) {
		super.drawLabel(g2, "SRFF", Pin.smallFont);
	}

	@Override
	public void changedLevel(LSLevelEvent e) {
		// clock: pin2
		// s: pin0
		// r: pin1
		if (e.source.equals(getPin(2)) && e.level == HIGH) {
			// clock rising edge detection
			boolean s = getPin(0).getLevel();
			boolean r = getPin(1).getLevel();
			if (r) {
				LSLevelEvent evt = new LSLevelEvent(this, LOW);
				getPin(3).changedLevel(evt);
				getPin(4).changedLevel(evt);
			} else if (s) {
				LSLevelEvent evt = new LSLevelEvent(this, HIGH);
				getPin(3).changedLevel(evt);
				getPin(4).changedLevel(evt);
			}
		}
	}

	@Override
	public void loadLanguage() {
		I18N.addGate(I18N.ALL, type, I18N.TITLE, "SR-FlipFlop");
		I18N.addGate("es", type, I18N.TITLE, "FlipFlop RS");
		I18N.addGate("fr", type, I18N.TITLE, "Bascule RS");
	}
}