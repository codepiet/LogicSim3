package logicsim.gates;

import logicsim.Connector;
import logicsim.Gate;

/**
 * AND Gate for LogicSim
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class AND extends Gate {
	static final long serialVersionUID = 4521959944440523564L;

	public AND() {
		super("basic");
		label = "&";
		type = "and";
		setNumInputs(2);
		setNumOutputs(1);
		variableInputCountSupported = true;
		reset();
	}

	@Override
	public void simulate() {
		super.simulate();

		boolean b = true;
		for (int i = 0; i < getNumInputs(); i++) {
			b = b && getInputLevel(i);
		}
		Connector output = getOutput(0);
		output.setLevel(b);
	}

}