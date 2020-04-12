package gates;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import logicsim.Gate;
import logicsim.Pin;

/**
 * SR-FlipFlop for LogicSim
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class SRFlipFlop extends Gate {
	static final long serialVersionUID = 1049162074522360589L;

	Color bg = Color.white;
	boolean out0 = false;

	public SRFlipFlop() {
		super("flipflops");
		type = "srff";
		createInputs(3);
		createOutputs(2);

		getPin(0).label = "S";
		getPin(1).label = "R";
		getPin(2).label = Pin.POS_EDGE_TRIG;

		getPin(3).label = "Q";
		getPin(3).moveBy(0, 10);

		getPin(4).label = "/Q";
		getPin(4).setLevelType(Pin.INVERTED);
		getPin(4).moveBy(0, -10);

		reset();
	}

	@Override
	protected void drawLabel(Graphics2D g2, String lbl, Font font) {
		super.drawLabel(g2, "SRFF", Pin.smallFont);
	}

	@Override
	public void simulate() {
		super.simulate();
		boolean s = getPin(0).getLevel();
		boolean r = getPin(1).getLevel();
		boolean clk = getPin(2).getLevel();

		if (clk && s && r) {
			bg = Color.yellow;
			getPin(4).setLevel(false);
			getPin(5).setLevel(false);
			return;
		} else {
			bg = Color.white;
		}

		if (clk) {
			if (!s && r)
				out0 = false;
			else if (s && !r)
				out0 = true;
		}
		getPin(3).setLevel(out0);
		getPin(4).setLevel(out0);
	}

	@Override
	public void reset() {
		out0 = false;
		getPin(3).setLevel(out0);
		getPin(4).setLevel(out0);
	}
	@Override
	public void loadLanguage() {
		
//		gate.rsff.title=RS-FlipFlop
//		gate.srff.title=SR-FlipFlop

		
		//rsff=FlipFlop RS
		
		//fr GATE_RSFF=Bascule RS

	}
}