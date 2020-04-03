package gates;

import logicsim.Connector;
import logicsim.Gate;

/**
 * NOT Gate for LogicSim
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class NOT extends Gate {
	static final long serialVersionUID = 3351085067064933298L;

	public NOT() {
		super("basic");
		label = "1";
		type = "not";
		setNumInputs(1);
		setNumOutputs(1);
		getOutput(0).setLevelType(Connector.INVERTED);
	}

	public void simulate() {
		setOutputLevel(0, getInputLevel(0));
	}
}