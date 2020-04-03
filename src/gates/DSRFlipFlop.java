package gates;

import java.awt.Graphics2D;

import logicsim.Gate;
import logicsim.Connector;

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
		super("flipflop");
		type = "dsrff";
		setNumInputs(4);
		setNumOutputs(2);
		out0 = false;
		out1 = true;

		getInput(0).label = "D";
		getInput(1).label = "S";
		getInput(2).label = "R";
		getInput(3).label = "Cl";

		getOutput(0).label = "Q";
		getOutput(1).label = "/Q";
		getOutput(1).setLevelType(Connector.INVERTED);

		getOutput(0).moveBy(0, 10);
		getOutput(1).moveBy(0, -10);

		reset();
	}

	@Override
	public void simulate() {
		boolean d = getInputLevel(0);
		boolean s = getInputLevel(1);
		boolean r = getInputLevel(2);
		boolean clk = getInputLevel(3);

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
			setOutputLevel(0, out0);
			setOutputLevel(1, !out1);
		}
		lastClk = clk;
	}

	@Override
	public void draw(Graphics2D g2) {
		super.draw(g2);
		drawLabel(g2, "DSR", Connector.smallFont);
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