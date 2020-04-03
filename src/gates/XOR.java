package gates;

import logicsim.Gate;

/**
 * XOR gate for LogicSim
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class XOR extends Gate {

	public XOR() {
		super("basic");
		label = "=1";
		type = "xor";
		setNumInputs(2);
		setNumOutputs(1);
		variableInputCountSupported = true;
	}

	@Override
	public void simulate() {
		super.simulate();
		int n = 0;
		for (int i = 0; i < getNumInputs() && getInputWire(i) != null; i++)
			if (getInputLevel(i))
				n++;
		setOutputLevel(0, (n % 2 > 0)); // ungerade ??
	}
}