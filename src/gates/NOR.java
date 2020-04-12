package gates;

import logicsim.Pin;

/**
 * NOR Gate for LogicSim
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class NOR extends OR {
	static final long serialVersionUID = -6728388521484380234L;

	public NOR() {
		super();
		type = "nor";
		getPin(0).setLevelType(Pin.INVERTED);
	}

	@Override
	public void loadLanguage() {

//	gate.nor.description=NOR Gate
//			gate.nor.title=NOR

//		gate.nor.description=NOR Gatter mit einstellbarer Eingangsanzahl

		
//	nor=NOR (NO-O)
		
		
//		GATE_NOR=Non Ou (NOR)

		
	}
}