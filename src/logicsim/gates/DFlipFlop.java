package logicsim.gates;

import java.awt.Graphics2D;

import logicsim.Gate;
import logicsim.Connector;

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
		setNumInputs(2);
		setNumOutputs(2);

		getInput(0).label = "D";
		getInput(1).label = Connector.POS_EDGE_TRIG;

		getOutput(0).label = "Q";
		getOutput(1).label = "/Q";
		getOutput(1).setLevelType(Connector.INVERTED);

		getInput(0).moveBy(0, 10);
		getInput(1).moveBy(0, -10);
		getOutput(0).moveBy(0, 10);
		getOutput(1).moveBy(0, -10);

		reset();
	}

	@Override
	public void draw(Graphics2D g2) {
		super.draw(g2);
		drawLabel(g2, "D-FF", Connector.smallFont);
	}

	/**
	 * https://www.electronicsforu.com/resources/learn-electronics/flip-flop-rs-jk-t-d
	 */
	@Override
	public void simulate() {
		super.simulate();
		boolean d = getInputLevel(0);
		boolean clk = getInputLevel(1);
		// rising edge
		if (clk && !lastClock) {
			out0 = d;
			setOutputLevel(0, out0);
			setOutputLevel(1, out0);
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