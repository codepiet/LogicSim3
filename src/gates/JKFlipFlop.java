
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

	public JKFlipFlop() {
		super("flipflops");
		type = "jkff";
		label = "JK";
		createInputs(3);
		createOutputs(2);

		getPin(0).setProperty(TEXT, "J");
		getPin(1).setProperty(TEXT, Pin.POS_EDGE_TRIG);
		getPin(2).setProperty(TEXT, "K");

		getPin(3).setProperty(TEXT, "Q");
		getPin(4).setProperty(TEXT, "/Q");

		getPin(3).moveBy(0, 10);
		getPin(4).moveBy(0, -10);
		getPin(4).setLevelType(Pin.INVERTED);
	}

	@Override
	public void changedLevel(LSLevelEvent e) {
		// clock: pin1
		// j: pin0
		// k: pin2
		if (e.source.equals(getPin(1)) && e.level == HIGH) {
			// clock rising edge detection
			boolean j = getPin(0).getLevel();
			boolean k = getPin(2).getLevel();
			if (j && k) {
				boolean out = getPin(3).getInternalLevel();
				LSLevelEvent evt = new LSLevelEvent(this, !out);
				getPin(3).changedLevel(evt);
				getPin(4).changedLevel(evt);
			}
		}
	}

	@Override
	public void loadLanguage() {
		I18N.addGate(I18N.ALL, type, I18N.TITLE, "JK flipfop");
		I18N.addGate(I18N.ALL, type, I18N.DESCRIPTION, "positive edge triggered JK Flipflop");
		I18N.addGate("de", type, I18N.DESCRIPTION, "positiv flankengesteuertes JK-Flipflop");
		I18N.addGate("es", type, I18N.TITLE, "FlipFlop JK");
		I18N.addGate("fr", type, I18N.TITLE, "Bascule JK");
		I18N.addGate("fr", type, I18N.DESCRIPTION, "Bascule JK (Front montant)");
	}
}