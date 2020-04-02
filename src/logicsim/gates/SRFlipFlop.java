package logicsim.gates;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

import logicsim.Connector;
import logicsim.Gate;

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
		super("flipflop");
		type = "srff";
		setNumInputs(3);
		setNumOutputs(2);

		getInput(0).label = "S";
		getInput(1).label = "R";
		getInput(2).label = "Cl";

		getOutput(0).label = "Q";
		getOutput(1).label = "/Q";
		getOutput(1).setLevelType(Connector.INVERTED);
		getOutput(0).moveBy(0, 10);
		getOutput(1).moveBy(0, -10);

		reset();
	}

	@Override
	protected void drawLabel(Graphics2D g2, String lbl, Font font) {
		super.drawLabel(g2, "SRFF", Connector.smallFont);
	}

	@Override
	public void simulate() {
		super.simulate();
		boolean s = getInputLevel(0);
		boolean r = getInputLevel(1);
		boolean clk = getInputLevel(2);

		if (clk && s && r) {
			bg = Color.yellow;
			setOutputLevel(0, false);
			setOutputLevel(1, false);
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
		setOutputLevel(0, out0);
		setOutputLevel(1, out0);
	}

	@Override
	public void reset() {
		out0 = false;
		setOutputLevel(0, out0);
		setOutputLevel(1, out0);
	}
}