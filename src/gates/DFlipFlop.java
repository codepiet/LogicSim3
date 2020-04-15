package gates;

import java.awt.Graphics2D;

import logicsim.Gate;
import logicsim.I18N;
import logicsim.LSLevelEvent;
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
		super("flipflops");
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
	public void changedLevel(LSLevelEvent e) {
		if (e.source.equals(getPin(1)) && e.level == HIGH) {
			// rising edge happened
			boolean d = getPin(0).getLevel();
			LSLevelEvent evt = new LSLevelEvent(this, d);
			getPin(2).changedLevel(evt);
			getPin(3).changedLevel(evt);
		}
	}

	@Override
	public void reset() {
		super.reset();
		out0 = false;
		lastClock = false;
	}

	@Override
	public void loadLanguage() {
		I18N.addGate(I18N.ALL, type, I18N.TITLE, "D flipfop");
		I18N.addGate(I18N.ALL, type, I18N.DESCRIPTION, "D Flipflop");
		I18N.addGate("es", type, I18N.TITLE, "FlipFlop D");
		I18N.addGate("fr", type, I18N.TITLE, "Bascule D");
	}
}