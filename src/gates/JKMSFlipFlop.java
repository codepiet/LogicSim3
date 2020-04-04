package gates;

import logicsim.Connector;
import logicsim.Gate;

/**
 * JKMS-Flipflop for LogicSim
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class JKMSFlipFlop extends Gate {
	static final long serialVersionUID = 6562388223937836948L;

	public JKMSFlipFlop() {
		super("flipflop");
		type = "jkmsff";
		setNumInputs(3);
		setNumOutputs(2);

		getInput(0).label = "J";
		getInput(1).label = Connector.POS_EDGE_TRIG;
		getInput(2).label = "K";

		getOutput(0).label = "Q";
		getOutput(1).label = "/Q";

		getOutput(0).moveBy(0, 10);
		getOutput(1).moveBy(0, -10);

		reset();
	}

	public void createModule() {
//		Gate clk = new Dummy(getInputWire(1));
//		Gate jk1 = new JKCFlipFlop();
//		jk1.setInputWire(0, getInputWire(0));
//		jk1.setInputWire(1, new Wire(clk, 0));
//		jk1.setInputWire(2, getInputWire(2));
//		Gate not = new NOT();
//		not.setInputWire(0, new Wire(clk, 0));
//		Gate jk2 = new JKCFlipFlop();
//		jk2.setInputWire(0, new Wire(jk1, 0));
//		jk2.setInputWire(1, new Wire(not, 0));
//		jk2.setInputWire(2, new Wire(jk1, 1));
//
//		gates.addGate(clk);
//		gates.addGate(jk1);
//		gates.addGate(not);
//		gates.addGate(jk2);
//
//		// Eingang 0 dieses Moduls auf Eingang 0 des Gatters jk1 setzen
//		inputGates.setElementAt(jk1, 0);
//		inputNums.setElementAt(0, 0);
//		// Eingang 1 dieses Moduls auf Eingang 0 des Gatters clk setzen
//		inputGates.setElementAt(clk, 1);
//		inputNums.setElementAt(0, 1);
//		// Eingang 2 dieses Moduls auf Eingang 2 des Gatters jk1 setzen
//		inputGates.setElementAt(jk1, 2);
//		inputNums.setElementAt(2, 2);
//
//		// Ausgang 0 dieses Moduls auf Ausgang 0 des Gatters jk2 setzen
//		outputGates.setElementAt(jk2, 0);
//		outputNums.setElementAt(0, 0);
//		// Ausgang 1 dieses Moduls auf Ausgang 1 des Gatters jk2 setzen
//		outputGates.setElementAt(jk2, 1);
//		outputNums.setElementAt(1, 1);
	}
}