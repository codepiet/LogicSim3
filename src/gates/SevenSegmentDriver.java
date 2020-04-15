package gates;

import java.awt.Graphics2D;

import logicsim.Gate;
import logicsim.I18N;
import logicsim.LSLevelEvent;

/**
 * Segment Segment Driver for LogicSim
 * 
 * has 4 binary inputs and 8 outputs to connect to a seven segment display.
 * 
 * @author Peter Gabriel
 * @version 2.0
 */
public class SevenSegmentDriver extends Gate {

	boolean out0 = false;
	boolean lastClock = false;
	int[] out;

	public SevenSegmentDriver() {
		super("output");
		type = "sevendrv";
		height = 80;
		createInputs(4);
		createOutputs(7);

		for (int i = 0; i < 4; i++) {
			getPin(i).label = "I" + (i + 1);
			getPin(i).setY(getY() + (i + 1) * 10);
		}
		getPin(4).label = "a";
		getPin(5).label = "b";
		getPin(6).label = "c";
		getPin(7).label = "d";
		getPin(8).label = "e";
		getPin(9).label = "f";
		getPin(10).label = "g";

		reset();
	}

	@Override
	public void draw(Graphics2D g2) {
		super.draw(g2);
		String lbl = "7S-D";
		int sw = g2.getFontMetrics().stringWidth(lbl);
		drawRotate(g2, getX() + getWidth() / 2 - 3, getY() + getHeight() / 2 - sw / 2, 90, "7S-D");
	}

	/**
	 * https://www.electronicsforu.com/resources/learn-electronics/flip-flop-rs-jk-t-d
	 */
	@Override
	public void simulate() {
		super.simulate();
		int b1 = getPin(0).getLevel() ? 1 : 0;
		int b2 = getPin(1).getLevel() ? 1 : 0;
		int b4 = getPin(2).getLevel() ? 1 : 0;
		int b8 = getPin(3).getLevel() ? 1 : 0;
		int value = b1 + (b2 << 1) + (b4 << 2) + (b8 << 3);
		switch (value) {
		case 0: {
			out = new int[] { 1, 1, 1, 1, 1, 1, 0 };
			break;
		}
		case 1: {
			out = new int[] { 0, 1, 1, 0, 0, 0, 0 };
			break;
		}
		case 2: {
			out = new int[] { 1, 1, 0, 1, 1, 0, 1 };
			break;
		}
		case 3: {
			out = new int[] { 1, 1, 1, 1, 0, 0, 1 };
			break;
		}
		case 4: {
			out = new int[] { 0, 1, 1, 0, 0, 1, 1 };
			break;
		}
		case 5: {
			out = new int[] { 1, 0, 1, 1, 0, 1, 1 };
			break;
		}
		case 6: {
			out = new int[] { 1, 0, 1, 1, 1, 1, 1 };
			break;
		}
		case 7: {
			out = new int[] { 1, 1, 1, 0, 0, 0, 0 };
			break;
		}
		case 8: {
			out = new int[] { 1, 1, 1, 1, 1, 1, 1 };
			break;
		}
		case 9: {
			out = new int[] { 1, 1, 1, 1, 0, 1, 1 };
			break;
		}
		case 0xa: {
			out = new int[] { 1, 1, 1, 0, 1, 1, 1 };
			break;
		}
		case 0xb: {
			out = new int[] { 0, 0, 1, 1, 1, 1, 1 };
			break;
		}
		case 0xc: {
			out = new int[] { 1, 0, 0, 1, 1, 1, 0 };
			break;
		}
		case 0xd: {
			out = new int[] { 0, 1, 1, 1, 1, 0, 1 };
			break;
		}
		case 0xe: {
			out = new int[] { 1, 0, 0, 1, 1, 1, 1 };
			break;
		}
		default: {
			out = new int[] { 1, 0, 0, 0, 1, 1, 1 };
			break;
		}
		}
		setOutputs();
	}

	@Override
	public void changedLevel(LSLevelEvent e) {
		super.changedLevel(e);
		simulate();
	}

	private void setOutputs() {
		for (int i = 0; i < 7; i++) {
			LSLevelEvent evt = new LSLevelEvent(this, out[i] == 1);
			getPin(i + 4).changedLevel(evt);
		}
	}

	@Override
	public void reset() {
		simulate();
	}

	@Override
	public void loadLanguage() {
		I18N.addGate(I18N.ALL, type, I18N.TITLE, "7-Segment-driver");

		I18N.addGate("de", type, I18N.TITLE, "7-Segment-Treiber");
	}
}