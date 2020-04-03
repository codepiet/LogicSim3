package gates;

import logicsim.Connector;
import logicsim.Gate;

/**
 * NOR Gate for LogicSim
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class NOR extends Gate {
	static final long serialVersionUID = -6728388521484380234L;

	public NOR() {
		super("basic");
		label = "\u2265" + "1";
		type = "nor";
		setNumInputs(2);
		setNumOutputs(1);
		getOutput(0).setLevelType(Connector.INVERTED);
		variableInputCountSupported = true;
	}

	public void simulate() {
		super.simulate();

		boolean b = false;
		for (int i = 0; i < getNumInputs(); i++) {
			b = b || getInputLevel(i);
		}
		setOutputLevel(0, b);
	}
}