package gates;

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

//		gate.nand.description=NAND Gate
//				gate.nand.title=NAND

//		gate.nand.description=NAND Gatter mit einstellbarer Eingangsanzahl

//		nand=NAND (NO-Y)

		
//		GATE_NAND=Non Et (NAND)

		
	}
}