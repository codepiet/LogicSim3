package gates;

import java.awt.Graphics2D;

import logicsim.Gate;
import logicsim.Pin;

/**
 * D-Flipflop with Set and Reset
 * 
 * @see http://cedmagic.com/tech-info/data/cd4013.pdf
 * 
 * @author Peter Gabriel
 * @version 2.0
 */
public class DSRFlipFlop extends Gate {

	boolean lastClk;
	boolean clk = false;
	boolean out0 = false;
	boolean out1 = true;

	public DSRFlipFlop() {
		super("flipflops");
		type = "dsrff";
		createInputs(4);
		createOutputs(2);
		out0 = false;
		out1 = true;

		getPin(0).label = "D";
		getPin(1).label = "S";
		getPin(2).label = "R";
		getPin(3).label = Pin.POS_EDGE_TRIG;

		getPin(4).label = "Q";
		getPin(5).label = "/Q";
		getPin(5).setLevelType(Pin.INVERTED);

		getPin(4).moveBy(0, 10);
		getPin(5).moveBy(0, -10);

		reset();
	}

	@Override
	public void simulate() {
		boolean d = getPin(0).getLevel();
		boolean s = getPin(1).getLevel();
		boolean r = getPin(2).getLevel();
		boolean clk = getPin(3).getLevel();

		boolean sthHasHappened = false;
		// rising edge
		if (clk && !lastClk && !r && !s) {
			out0 = d;
			out1 = !d;
			sthHasHappened = true;
		} else if (r && !s) {
			out0 = false;
			out1 = true;
			sthHasHappened = true;
		} else if (!r && s) {
			out0 = true;
			out1 = false;
			sthHasHappened = true;
		} else if (r && s) {
			out0 = true;
			out1 = true;
			sthHasHappened = true;
		}
		if (sthHasHappened) {
			getPin(4).setLevel(out0);
			getPin(5).setLevel(!out1);
		}
		lastClk = clk;
	}

	@Override
	public void draw(Graphics2D g2) {
		super.draw(g2);
		drawLabel(g2, "DSR", Pin.smallFont);
	}

	@Override
	public void reset() {
		super.reset();
		out0 = false;
		out1 = true;
		lastClk = false;
		clk = false;
	}
}