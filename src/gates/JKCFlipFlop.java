
package gates;

import logicsim.Pin;
import logicsim.Gate;
import logicsim.I18N;

/**
 * JK-Flipflop for LogicSim - rising edge driven
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class JKCFlipFlop extends Gate {
	static final long serialVersionUID = -5614329713407328370L;

	transient boolean lastClk;
	transient boolean clk;
	transient boolean out0;
	transient boolean out1;
	boolean j;
	boolean k;

	public JKCFlipFlop() {
		super("flipflops");
		type = "jkcff";
		label = "JKC";
		createInputs(3);
		createOutputs(2);

		getPin(0).label = "J";
		getPin(1).label = Pin.POS_EDGE_TRIG;
		getPin(2).label = "K";

		getPin(3).label = "Q";
		getPin(4).label = "/Q";

		getPin(3).moveBy(0, 10);
		getPin(4).moveBy(0, -10);

		reset();
	}

	public void simulate() {
		j = getPin(0).getLevel();
		clk = getPin(1).getLevel();
		k = getPin(2).getLevel();
		out0 = getPin(3).getLevel();
		out1 = getPin(4).getLevel();

		if (j && !lastClk && k && out0) {
			out0 = true;
			out1 = false;
		} else if (k && !lastClk && clk && out0) {
			out0 = false;
			out1 = true;
		}
		lastClk = clk;

		getPin(3).setLevel(out0);
		getPin(4).setLevel(out1);
	}

	public void reset() {
		super.reset();
		j = false;
		k = false;
		out0 = false;
		out1 = true;
		clk = false;
		lastClk = false;
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