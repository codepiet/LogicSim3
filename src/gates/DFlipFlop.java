package gates;

import java.awt.Graphics2D;

import logicsim.Gate;
import logicsim.Pin;

/**
 * D-Flipflop for LogicSim
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class DFlipFlop extends Gate {

	boolean out0 = false;
	boolean lastClock = false;

	public DFlipFlop() {
		super("flipflop");
		type = "dff";
		createInputs(2);
		createOutputs(2);

		getPin(0).label = "D";
		getPin(1).label = Pin.POS_EDGE_TRIG;

		getPin(2).label = "Q";
		getPin(3).label = "/Q";
		getPin(3).setLevelType(Pin.INVERTED);

		getPin(0).moveBy(0, 10);
		getPin(1).moveBy(0, -10);
		getPin(2).moveBy(0, 10);
		getPin(3).moveBy(0, -10);

		reset();
	}

	@Override
	public void draw(Graphics2D g2) {
		super.draw(g2);
		drawLabel(g2, "D-FF", Pin.smallFont);
	}

	/**
	 * https://www.electronicsforu.com/resources/learn-electronics/flip-flop-rs-jk-t-d
	 */
	@Override
	public void simulate() {
		super.simulate();
		boolean d = getPin(0).getLevel();
		boolean clk = getPin(1).getLevel();
		// rising edge
		if (clk && !lastClock) {
			out0 = d;
			getPin(2).setLevel(out0);
			getPin(3).setLevel(out0);
		}
		lastClock = clk;
	}

	@Override
	public void reset() {
		super.reset();
		out0 = false;
		lastClock = false;
	}
}