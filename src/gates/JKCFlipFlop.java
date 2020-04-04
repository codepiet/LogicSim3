package gates;

import logicsim.Connector;
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
		super("flipflop");
		type = "jkcff";
		label = "JKC";
		setNumInputs(3);
		setNumOutputs(2);
		
		getInput(0).label = "J";
		getInput(1).label = Connector.POS_EDGE_TRIG;
		getInput(2).label = "K";

		getOutput(0).label = "Q";
		getOutput(1).label = "/Q";

		getOutput(0).moveBy(0, 10);
		getOutput(1).moveBy(0, -10);

		reset();
	}

	public void simulate() {
		out0 = getOutputLevel(0);
		out1 = getOutputLevel(1);
		j = getInputLevel(0);
		clk = getInputLevel(1);
		k = getInputLevel(2);

		if (j && !lastClk && k && out0) {
			out0 = true;
			out1 = false;
		} else if (k && !lastClk && clk && out0) {
			out0 = false;
			out1 = true;
		}
		lastClk = clk;

		setOutputLevel(0, out0);
		setOutputLevel(0, out1);
	}

	public boolean isOutputPositive(int n) {
		return (n == 0);
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