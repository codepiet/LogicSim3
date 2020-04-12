package gates;

import logicsim.I18N;
import logicsim.Pin;

/**
 * NAND Gate for LogicSim
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class NAND extends AND {
	static final long serialVersionUID = -8148143070926953439L;

	public NAND() {
		super();
		type = "nand";
		getPin(0).setLevelType(Pin.INVERTED);
	}

	@Override
	public void loadLanguage() {
		I18N.addGate(I18N.ALL, type, I18N.TITLE, "NAND");
		I18N.addGate(I18N.ALL, type, I18N.DESCRIPTION, "NAND Gate (variable Inputcount)");
		I18N.addGate("de", type, I18N.DESCRIPTION, "NAND Gatter mit einstellbarer Eingangsanzahl");
		I18N.addGate("es", type, I18N.TITLE, "NAND (NO-Y)");
		I18N.addGate("fr", type, I18N.TITLE, "Non Et (NAND)");
	}
}