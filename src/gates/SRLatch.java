package gates;

import java.awt.Graphics2D;

import logicsim.Pin;
import logicsim.Gate;
import logicsim.I18N;
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
		super("flipflops");
		type = "srl";
		createInputs(2);
		createOutputs(2);

		getPin(0).label = "S";
		getPin(1).label = "R";

		getPin(2).label = "Q";
		getPin(3).label = "/Q";

		getPin(0).moveBy(0, 10);
		getPin(1).moveBy(0, -10);
		getPin(2).moveBy(0, 10);
		getPin(3).moveBy(0, -10);

		reset();

		// build gate
		// https://www.elektronik-kompendium.de/sites/dig/0209302.htm

		Wire nor1_nor2a = new Wire(nor1.getPin(0), nor2.getPin(1));
		nor1.getPin(0).connect(nor1_nor2a);
		nor2.getPin(1).connect(nor1_nor2a);

		Wire nor2_nor1b = new Wire(nor2.getPin(0), nor1.getPin(2));
		nor2.getPin(0).connect(nor2_nor1b);
		nor1.getPin(2).connect(nor2_nor1b);

		simulate();
	}

	@Override
	public void draw(Graphics2D g2) {
		super.draw(g2);
		drawLabel(g2, "SRL", Pin.smallFont);
	}

	@Override
	public void simulate() {
		super.simulate();

		nor1.getPin(1).setLevel(getPin(0).getLevel());
		nor2.getPin(2).setLevel(getPin(1).getLevel());

		nor1.simulate();
		nor2.simulate();

		getPin(2).setLevel(!nor1.getPin(0).getLevel());
		getPin(3).setLevel(!nor2.getPin(0).getLevel());
	}

	@Override
	public void reset() {
		getPin(2).setLevel(!nor1.getPin(0).getLevel());
		getPin(3).setLevel(!nor2.getPin(0).getLevel());
	}

	@Override
	public void loadLanguage() {
		I18N.addGate(I18N.ALL, type, I18N.TITLE, "SR-Latch");
	}
}