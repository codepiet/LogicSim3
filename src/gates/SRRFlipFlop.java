package gates;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import logicsim.Gate;
import logicsim.I18N;
import logicsim.LSLevelEvent;
import logicsim.Pin;

/**
 * SRR-FlipFlop for LogicSim
 * 
 * @author Peter Gabriel
 * @version 1.0
 */
public class SRRFlipFlop extends Gate {
	static final long serialVersionUID = 1049162074522360589L;

	Color bg = Color.white;

	public SRRFlipFlop() {
		super("flipflops");
		type = "srrff";
		createInputs(4);
		createOutputs(2);

		getPin(0).setProperty(TEXT, "S");
		getPin(1).setProperty(TEXT, "R");
		getPin(2).setProperty(TEXT, Pin.POS_EDGE_TRIG);
		getPin(3).setProperty(TEXT, "R");

		getPin(4).setProperty(TEXT, "Q");
		getPin(4).moveBy(0, 10);

		getPin(5).setProperty(TEXT, "/Q");
		getPin(5).setLevelType(Pin.INVERTED);
		getPin(5).moveBy(0, -10);
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
		// mr: pin3 - master reset
		if (e.source.equals(getPin(2)) && e.level == HIGH) {
			// clock rising edge detection
			boolean s = getPin(0).getLevel();
			boolean r = getPin(1).getLevel();
			boolean mr = getPin(3).getLevel();
			if (r || mr) {
				LSLevelEvent evt = new LSLevelEvent(this, LOW);
				getPin(4).changedLevel(evt);
				getPin(5).changedLevel(evt);
			} else if (s) {
				LSLevelEvent evt = new LSLevelEvent(this, HIGH);
				getPin(4).changedLevel(evt);
				getPin(5).changedLevel(evt);
			}
		}
	}

	@Override
	public void loadLanguage() {
		I18N.addGate(I18N.ALL, type, I18N.TITLE, "SRR Flip-flop");
		I18N.addGate(I18N.ALL, type, I18N.DESCRIPTION, "SRR Flip-flop");
		I18N.addGate("de", type, I18N.TITLE, "SRR Flipflop");
		I18N.addGate("de", type, I18N.DESCRIPTION, "SRR Flipflop");
		I18N.addGate("es", type, I18N.TITLE, "FlipFlop RRS");
		I18N.addGate("es", type, I18N.DESCRIPTION, "FlipFlop RRS");
		I18N.addGate("fr", type, I18N.TITLE, "Bascule RRS");
		I18N.addGate("fr", type, I18N.DESCRIPTION, "Bascule RRS");
	}
}