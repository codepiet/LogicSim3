package logicsim.gates;

import java.awt.Graphics2D;

import logicsim.Gate;

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
		setNumInputs(4);
		setNumOutputs(7);

		getInput(0).label = "b1";
		getInput(1).label = "b2";
		getInput(2).label = "b4";
		getInput(3).label = "b8";

		getInput(0).setY(getY() + 10);
		getInput(1).setY(getY() + 20);
		getInput(2).setY(getY() + 30);
		getInput(3).setY(getY() + 40);

		getOutput(0).label = "a";
		getOutput(1).label = "b";
		getOutput(2).label = "c";
		getOutput(3).label = "d";
		getOutput(4).label = "e";
		getOutput(5).label = "f";
		getOutput(6).label = "g";

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
		int b1 = getInputLevel(0) ? 1 : 0;
		int b2 = getInputLevel(1) ? 1 : 0;
		int b4 = getInputLevel(2) ? 1 : 0;
		int b8 = getInputLevel(3) ? 1 : 0;
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
		case 10: {
			out = new int[] { 1, 1, 1, 0, 1, 1, 1 };
			break;
		}
		case 11: {
			out = new int[] { 0, 0, 1, 1, 1, 1, 1 };
			break;
		}
		case 12: {
			out = new int[] { 1, 0, 0, 1, 1, 1, 0 };
			break;
		}
		case 13: {
			out = new int[] { 0, 1, 1, 1, 1, 0, 1 };
			break;
		}
		case 14: {
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

	private void setOutputs() {
		for (int i = 0; i < 7; i++) {
			setOutputLevel(i, (out[i] == 1));
		}
	}

	@Override
	public void reset() {
		simulate();
	}
}