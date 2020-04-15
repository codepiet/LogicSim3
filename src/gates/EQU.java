package gates;

import logicsim.Gate;
import logicsim.I18N;

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
			if (getPin(i).getLevel())
				n++;
		}
		// if n is even set true
		getPin(0).setLevel(n % 2 == 0);
	}

	@Override
	public void loadLanguage() {
		I18N.addGate(I18N.ALL, type, I18N.TITLE, "EQUIV");
		I18N.addGate(I18N.ALL, type, I18N.DESCRIPTION, "Equivalence Gate");
		I18N.addGate("de", type, I18N.DESCRIPTION, "Äquivalenz Gatter (einstellbare Eingangsanzahl)");
		I18N.addGate("es", type, I18N.TITLE, "XNOR (=)");
		I18N.addGate("fr", type, I18N.TITLE, "<-> (NXOR)");
	}
}