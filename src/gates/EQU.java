package gates;

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
		createOutputs(1);
		createInputs(2);
		getPin(0).setLevel(true);
		variableInputCountSupported = true;
	}

	public void simulate() {
		int n = 0;
		for (int i = 1; i < 1 + getNumInputs(); i++) {
			if (getPin(i).isConnected())
				if (getPin(i).getLevel())
					n++;
		}
		// if n is even set true
		getPin(0).setLevel(n % 2 == 0);
	}

	@Override
	public void loadLanguage() {
		//gate.equ.description=Äquivalenz Gatter mit einstellbarer Eingangsanzahl

		// gate.equ.description=Equivalence Gate
		// gate.equ.title=EQUIV

		// equiv=XNOR (=)
		
		//fr 		GATE_EQUIVALENCE=<-> (NXOR)

	}
}