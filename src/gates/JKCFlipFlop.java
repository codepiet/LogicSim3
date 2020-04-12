package gates;

import logicsim.Pin;
import logicsim.Gate;

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
}