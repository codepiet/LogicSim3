package logicsim.gates;

import logicsim.Gate;

/**
 * Equivalence Gate for LogicSim
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class EQU extends Gate {
	static final long serialVersionUID = 521585027776705481L;

	public EQU() {
		super("basic");
		label = "=";
		type = "equ";
		setNumInputs(2);
		setNumOutputs(1);
		setOutputLevel(0, true);
		variableInputCountSupported = true;
	}

	public void simulate() {
		int n = 0;
		for (int i = 0; i < getNumInputs(); i++) {
			if (getInput(i).isConnected())
				if (getInput(i).wires.get(0).getLevel())
					n++;
		}
		// if n is even set true
		setOutputLevel(0, (n % 2 == 0));
	}
}