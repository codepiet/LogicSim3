package gates;

import logicsim.I18N;
import logicsim.Pin;

/**
 * NOT Gate for LogicSim
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class NOT extends Buffer {

	public NOT() {
		super("basic");
		label = "1";
		type = "not";
		createInputs(1);
		createOutputs(1);
		getPin(1).setLevelType(Pin.INVERTED);
	}

	@Override
	public void loadLanguage() {
		I18N.addGate(I18N.ALL, type, I18N.TITLE, "NOT");
		I18N.addGate(I18N.ALL, type, I18N.DESCRIPTION, "NOT Gate (Inverter)");
		I18N.addGate("de", type, I18N.DESCRIPTION, "NOT Gatter (Inverter/Negator)");
		I18N.addGate("es", type, I18N.TITLE, "NOT (Inversor)");
		I18N.addGate("fr", type, I18N.TITLE, "Non (NOT)");
	}
}