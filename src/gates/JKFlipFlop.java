
package gates;

import logicsim.Pin;
import logicsim.Gate;
import logicsim.I18N;
import logicsim.LSLevelEvent;

/**
 * JK-Flipflop for LogicSim - rising edge driven
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class JKFlipFlop extends Gate {
	static final long serialVersionUID = -5614329713407328370L;

	static final int CLK = 1;
	static final int J = 0;
	static final int K = 2;
	static final int ON = 3;
	static final int OI = 4;
	
	public JKFlipFlop() {
		super("flipflops");
		type = "jkff";
		label = "JK";
		createInputs(3);
		createOutputs(2);

		getPin(J).setProperty(TEXT, "J");
		getPin(CLK).setProperty(TEXT, Pin.POS_EDGE_TRIG);
		getPin(K).setProperty(TEXT, "K");

		getPin(ON).setProperty(TEXT, "Q");
		getPin(OI).setProperty(TEXT, "/Q");

		getPin(ON).moveBy(0, 10);
		getPin(OI).moveBy(0, -10);
		getPin(OI).setLevelType(Pin.INVERTED);
	}

	@Override
	public void changedLevel(LSLevelEvent e) {
		// clock: pin1
		// j: pin0
		// k: pin2
		if (e.source.equals(getPin(CLK)) && e.level == HIGH) {
			// clock rising edge detection
			boolean j = getPin(J).getLevel();
			boolean k = getPin(K).getLevel();
			if (j && k) {
				boolean out = getPin(ON).getInternalLevel();
				LSLevelEvent evt = new LSLevelEvent(this, !out);
				getPin(ON).changedLevel(evt);
				getPin(OI).changedLevel(evt);
			} else if (j) {
				LSLevelEvent evt = new LSLevelEvent(this, true);
				getPin(ON).changedLevel(evt);
				getPin(OI).changedLevel(evt);			
			} else if (k) {
				LSLevelEvent evt = new LSLevelEvent(this, false);
				getPin(ON).changedLevel(evt);
				getPin(OI).changedLevel(evt);			
			}
		}
	}

	@Override
	public void loadLanguage() {
		I18N.addGate(I18N.ALL, type, I18N.TITLE, "JK Flip-flop");
		I18N.addGate(I18N.ALL, type, I18N.DESCRIPTION, "positive edge triggered JK Flip-flop");
		I18N.addGate("de", type, I18N.TITLE, "JK Flipflop");
		I18N.addGate("de", type, I18N.DESCRIPTION, "positiv flankengesteuertes JK-Flipflop");
		I18N.addGate("es", type, I18N.TITLE, "FlipFlop JK");
		I18N.addGate("fr", type, I18N.TITLE, "Bascule JK");
		I18N.addGate("fr", type, I18N.DESCRIPTION, "Bascule JK (Front montant)");
	}
}