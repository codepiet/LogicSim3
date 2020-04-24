package gates;

import java.awt.Graphics2D;

import logicsim.Gate;
import logicsim.I18N;
import logicsim.LSLevelEvent;
import logicsim.Pin;

/**
 * D-Flipflop with Set and Reset
 * 
 * @see http://cedmagic.com/tech-info/data/cd4013.pdf
 * 
 * @author Peter Gabriel
 * @version 2.0
 */
public class DSRFlipFlop extends Gate {

	boolean clk = false;
	boolean out0 = false;
	boolean out1 = true;

	public DSRFlipFlop() {
		super("flipflops");
		type = "dsrff";
		createInputs(4);
		createOutputs(2);

		getPin(0).setProperty(TEXT, "D");
		getPin(1).setProperty(TEXT, "S");
		getPin(2).setProperty(TEXT, "R");
		getPin(3).setProperty(TEXT, Pin.POS_EDGE_TRIG);

		getPin(4).setProperty(TEXT, "Q");
		getPin(5).setProperty(TEXT, "/Q");
		getPin(5).setLevelType(Pin.INVERTED);

		getPin(4).moveBy(0, 10);
		getPin(5).moveBy(0, -10);
	}

	@Override
	public void changedLevel(LSLevelEvent e) {
		// clock: pin3
		// d: pin0
		// s: pin1
		// r: pin2

		if (e.source.equals(getPin(3)) && e.level == HIGH) {
			// clock rising edge detection
			boolean d = getPin(0).getLevel();
			boolean s = getPin(1).getLevel();
			boolean r = getPin(2).getLevel();
			if (!r && !s) {
				LSLevelEvent evt = new LSLevelEvent(this, d);
				getPin(4).changedLevel(evt);
				getPin(5).changedLevel(evt);
			}
		} else if (e.source.equals(getPin(1)) && e.level == HIGH) {
			LSLevelEvent evt = new LSLevelEvent(this, HIGH);
			getPin(4).changedLevel(evt);
			getPin(5).changedLevel(evt);
		} else if (e.source.equals(getPin(2)) && e.level == HIGH) {
			LSLevelEvent evt = new LSLevelEvent(this, LOW);
			getPin(4).changedLevel(evt);
			getPin(5).changedLevel(evt);
		}
	}

	@Override
	public void draw(Graphics2D g2) {
		super.draw(g2);
		drawLabel(g2, "DSR", Pin.smallFont);
	}

	@Override
	public void loadLanguage() {
		I18N.addGate(I18N.ALL, type, I18N.TITLE, "DSR Flip-flop");
		I18N.addGate(I18N.ALL, type, I18N.DESCRIPTION, "D Flip-flop with set and reset");
		I18N.addGate("de", type, I18N.TITLE, "DSR Flipflop");
		I18N.addGate("de", type, I18N.DESCRIPTION, "D Flipflop mit Setz- und Reset-Eingang");
		I18N.addGate("es", type, I18N.TITLE, "FlipFlop DSR");
		I18N.addGate("fr", type, I18N.TITLE, "Bascule DSR");

	}
}