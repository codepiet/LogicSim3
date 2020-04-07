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

}