package gates;

import java.awt.Graphics2D;

import logicsim.Connector;
import logicsim.Gate;
import logicsim.Wire;

/**
 * SR-Latch for LogicSim
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class SRLatch extends Gate {
	static final long serialVersionUID = 1049162074522360589L;

	Gate nor1 = new NOR();
	Gate nor2 = new NOR();
	// Circuit c = new Circuit();

	public SRLatch() {
		super("flipflop");
		type = "srl";
		setNumInputs(2);
		setNumOutputs(2);

		getInput(0).label = "S";
		getInput(1).label = "R";

		getOutput(0).label = "Q";
		getOutput(1).label = "/Q";

		getInput(0).moveBy(0, 10);
		getInput(1).moveBy(0, -10);
		getOutput(0).moveBy(0, 10);
		getOutput(1).moveBy(0, -10);

		reset();

		// build gate
		// https://www.elektronik-kompendium.de/sites/dig/0209302.htm

		Wire nor1_nor2a = new Wire(nor1.getOutput(0), nor2.getInput(0));
		nor1.getOutput(0).addWire(nor1_nor2a);
		nor2.getInput(0).addWire(nor1_nor2a);

		Wire nor2_nor1b = new Wire(nor2.getOutput(0), nor1.getInput(1));
		nor2.getOutput(0).addWire(nor2_nor1b);
		nor1.getInput(1).addWire(nor2_nor1b);

		simulate();
	}

	@Override
	public void draw(Graphics2D g2) {
		super.draw(g2);
		drawLabel(g2, "SRL", Connector.smallFont);
	}

	@Override
	public void simulate() {
		super.simulate();

		nor1.getInput(0).setLevel(getInputLevel(0));
		nor2.getInput(1).setLevel(getInputLevel(1));

		nor1.simulate();
		nor2.simulate();

		setOutputLevel(0, !nor1.getOutputLevel(0));
		setOutputLevel(1, !nor2.getOutputLevel(0));
	}

	@Override
	public void reset() {
		setOutputLevel(0, !nor1.getOutputLevel(0));
		setOutputLevel(1, !nor2.getOutputLevel(0));
	}
}