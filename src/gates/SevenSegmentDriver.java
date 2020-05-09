package gates;

import java.awt.Graphics2D;

import logicsim.Gate;
import logicsim.I18N;
import logicsim.LSLevelEvent;
import logicsim.WidgetHelper;

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
	boolean force = false;

	public SevenSegmentDriver() {
		super("output");
		type = "sevendrv";
		height = 80;
		createInputs(4);
		createOutputs(7);

		for (int i = 0; i < 4; i++) {
			getPin(i).setProperty(TEXT, "I" + i);
			getPin(i).setY(getY() + (i + 1) * 10);
		}
		getPin(4).setProperty(TEXT, "a");
		getPin(5).setProperty(TEXT, "b");
		getPin(6).setProperty(TEXT, "c");
		getPin(7).setProperty(TEXT, "d");
		getPin(8).setProperty(TEXT, "e");
		getPin(9).setProperty(TEXT, "f");
		getPin(10).setProperty(TEXT, "g");

		reset();
	}

	@Override
	public void draw(Graphics2D g2) {
		super.draw(g2);
		String lbl = "7S-D";
		int sw = g2.getFontMetrics().stringWidth(lbl);
		WidgetHelper.drawStringRotated(g2, lbl, getX() + getWidth() / 2 - 3, getY() + getHeight() / 2 - sw / 2,
				WidgetHelper.ALIGN_CENTER, 90);
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
			LSLevelEvent evt = new LSLevelEvent(this, out[i] == 1, force);
			getPin(i + 4).changedLevel(evt);
		}
	}

	@Override
	public void reset() {
		force = true;
		simulate();
		force = false;
	}

	@Override
	public void loadLanguage() {
		I18N.addGate(I18N.ALL, type, I18N.TITLE, "7-Segment-driver");
		I18N.addGate(I18N.ALL, type, I18N.DESCRIPTION, "binary to 7-Segment-display driver");
		I18N.addGate("de", type, I18N.TITLE, "7-Segment-Treiber");
		I18N.addGate("de", type, I18N.DESCRIPTION, "Binärzahl-zu-7Segment-Anzeige Konverter");
	}
}