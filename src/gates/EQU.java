package gates;

import javax.swing.event.ListSelectionEvent;

import logicsim.Gate;
import logicsim.I18N;
import logicsim.LSLevelEvent;
import logicsim.Pin;

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
		for (Pin p : getInputs()) {
			if (p.getLevel())
				n++;
		}
		// if n is even, set true
		LSLevelEvent evt = new LSLevelEvent(this, n % 2 == 0);
		getPin(0).changedLevel(evt);
	}

	@Override
	public void changedLevel(LSLevelEvent e) {
		super.changedLevel(e);
		if (busted)
			return;
		simulate();
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