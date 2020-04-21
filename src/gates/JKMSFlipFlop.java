package gates;

import logicsim.Pin;

import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import logicsim.Gate;
import logicsim.I18N;

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
		super("flipflops");
		type = "jkmsff";
		createInputs(3);
		createOutputs(2);

		getPin(0).label = "J";
		getPin(1).label = Pin.POS_EDGE_TRIG;
		getPin(2).label = "K";

		getPin(3).label = "Q";
		getPin(4).label = "/Q";

		getPin(3).moveBy(0, 10);
		getPin(4).moveBy(0, -10);

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
	public void loadLanguage() {
		I18N.addGate(I18N.ALL, type, I18N.TITLE, "JKMS flipfop");
		I18N.addGate(I18N.ALL, type, I18N.DESCRIPTION, "JKMS flipfop");
		I18N.addGate("es", type, I18N.TITLE, "FlipFlop JKMS");
		I18N.addGate("es", type, I18N.DESCRIPTION, "FlipFlop JKMS");
		I18N.addGate("fr", type, I18N.TITLE, "Bascule JKMS");
		I18N.addGate("fr", type, I18N.DESCRIPTION, "Bascule JKMS");
	}
}