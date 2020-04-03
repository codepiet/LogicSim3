package gates;

import logicsim.Connector;
import logicsim.Gate;

/**
 * NAND Gate for LogicSim
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class NAND extends Gate {
	static final long serialVersionUID = -8148143070926953439L;

	public NAND() {
		super("basic");
		label = "&";
		type = "nand";
		setNumInputs(2);
		setNumOutputs(1);
		getOutput(0).setLevelType(Connector.INVERTED);
		variableInputCountSupported = true;
		reset();
	}

	public void simulate() {
		boolean b = true;
		for (int i = 0; i < getNumInputs(); i++) {
			b = b && getInputLevel(i);
		}
		setOutputLevel(0, b);
	}

}