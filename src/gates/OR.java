package gates;

import logicsim.Gate;

/**
 * OR Gate for LogicSim
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class OR extends Gate {

	public OR() {
		super("basic");
		label = "\u2265" + "1";
		type = "or";
		setNumInputs(2);
		setNumOutputs(1);
		variableInputCountSupported = true;
	}

	public void simulate() {
		boolean b = false;
		for (int i = 0; i < getNumInputs(); i++) {
			b = b || getInputLevel(i);
		}
		setOutputLevel(0, b);
	}
}