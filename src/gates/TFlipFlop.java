package gates;

import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import logicsim.Gate;

/**
 * T-FlipFlop for LogicSim
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class TFlipFlop extends Gate {

	public TFlipFlop() {
		super("flipflops");
		type = "tff";
		createInputs(1);
		createOutputs(2);
		reset();
	}

//	public void createModule() {
//		Gate h = new HIGH();
//		Gate clk = new Dummy(getInputWire(0));
//		Gate jk1 = new JKCFlipFlop();
//		jk1.setInputWire(0, new Wire(h, 0));
//		jk1.setInputWire(1, new Wire(clk, 0));
//		jk1.setInputWire(2, new Wire(h, 0));
//		Gate not = new NOT();
//		not.setInputWire(0, new Wire(clk, 0));
//		Gate jk2 = new JKCFlipFlop();
//		jk2.setInputWire(0, new Wire(jk1, 0));
//		jk2.setInputWire(1, new Wire(not, 0));
//		jk2.setInputWire(2, new Wire(jk1, 1));
//
//		gates.addGate(h);
//		gates.addGate(clk);
//		gates.addGate(jk1);
//		gates.addGate(not);
//		gates.addGate(jk2);
//
//		// Eingang 0 dieses Moduls auf Eingang 0 des Gatters clk setzen
//		inputGates.setElementAt(clk, 0);
//		inputNums.setElementAt(0, 0);
//
//		// Ausgang 0 dieses Moduls auf Ausgang 0 des Gatters jk2 setzen
//		outputGates.setElementAt(jk2, 0);
//		outputNums.setElementAt(0, 0);
//		// Ausgang 1 dieses Moduls auf Ausgang 1 des Gatters jk2 setzen
//		outputGates.setElementAt(jk2, 1);
//		outputNums.setElementAt(1, 1);
//	}

	@Override
	public void draw(Graphics2D g2) {
		super.draw(g2);
		Path2D p = new Path2D.Double();
		int sp = 10;
		p.moveTo(getX() + width - 2 * sp, getY() + height - 2 * sp);
		p.lineTo(getX() + width - sp, getY() + height - 2 * sp);
		p.lineTo(getX() + width - sp, getY() + height - sp);

		p.moveTo(getX() + width - 2 * sp, getY() + sp);
		p.lineTo(getX() + width - sp, getY() + sp);
		p.lineTo(getX() + width - sp, getY() + 2 * sp);
		g2.draw(p);
	}

	@Override
	public void reset() {
		getPin(1).setLevel(true);
		getPin(2).setLevel(true);
	}
	@Override
	public void loadLanguage() {
//		gate.tff.title=T FlipFlop

//				tff=FlipFlop T
		
		//	fr	GATE_TFF=Bascule T

	}
}