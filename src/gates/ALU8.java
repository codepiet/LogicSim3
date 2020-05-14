package gates;

import java.awt.Color;
import java.awt.Graphics2D;

import logicsim.Gate;
import logicsim.I18N;
import logicsim.LSLevelEvent;
import logicsim.Pin;

/**
 * 8bit Algorithmic Unit
 * 
 * Following Ben Eater's Youtube Channel the ALU has inputs for two 8bit Words,
 * a signal for subtracting/adding the numbers and an output enable signal.
 * 
 * There are 8 outputs for the result of the subtraction/addition. The
 * computation is instantaneous so there is no clock input.
 * 
 * @author Peter Gabriel
 * @version 1.0
 */
public class ALU8 extends Gate {

	int result = 0;
	int wordA = 0;
	int wordB = 0;
	private static final int WORD1 = 8;
	private static final int WORD2 = 16;
	private static final int OE = 24;
	private static final int SU = 25;
	private static final int CL = 26;
	private static final int CF = 27;
	private static final int ZF = 28;

	public ALU8() {
		super("cpu");
		type = "alu8";
		height = 110;
		width = 110;
		createOutputs(8);
		createInputs(19);
		createOutputs(2);
		// outputs from 0-7
		// word 1 from pin 8-15
		// word 2 from pin 16-23
		// enable output is 24
		// subtract signal is 25

		for (int i = 0; i < 8; i++) {
			getPin(i).paintDirection = Pin.RIGHT;
			getPin(i).setIoType(Pin.HIGHIMP);
			getPin(i).setX(getX());
			getPin(i).setY(getY() + (i + 2) * 10);
			getPin(i).setProperty(TEXT, String.valueOf(i));

			getPin(i + WORD1).paintDirection = Pin.DOWN;
			getPin(i + WORD1).setX(getX() + (7 - i + 2) * 10);
			getPin(i + WORD1).setY(getY());
			getPin(i + WORD1).setProperty(TEXT, String.valueOf(i));

			getPin(i + WORD2).paintDirection = Pin.UP;
			getPin(i + WORD2).setX(getX() + (7 - i + 2) * 10);
			getPin(i + WORD2).setY(getY() + height);
			getPin(i + WORD2).setProperty(TEXT, String.valueOf(i));
		}
		getPin(OE).setProperty(TEXT, "/OE");
		getPin(OE).paintDirection = Pin.LEFT;
		getPin(OE).setX(getX() + width);
		getPin(OE).setY(getY() + 20);

		getPin(SU).setProperty(TEXT, "SU");
		getPin(SU).paintDirection = Pin.LEFT;
		getPin(SU).setX(getX() + width);
		getPin(SU).setY(getY() + 40);

		getPin(CL).setProperty(TEXT, "CL");
		getPin(CL).paintDirection = Pin.LEFT;
		getPin(CL).setX(getX() + width);
		getPin(CL).setY(getY() + 50);

		getPin(CF).setIoType(Pin.OUTPUT);
		getPin(CF).setProperty(TEXT, "CF");
		getPin(CF).paintDirection = Pin.LEFT;
		getPin(CF).setX(getX() + width);
		getPin(CF).setY(getY() + height - 40);

		getPin(ZF).setIoType(Pin.OUTPUT);
		getPin(ZF).setProperty(TEXT, "ZF");
		getPin(ZF).paintDirection = Pin.LEFT;
		getPin(ZF).setX(getX() + width);
		getPin(ZF).setY(getY() + height - 20);

	}

	@Override
	public void draw(Graphics2D g2) {
		super.draw(g2);
		for (int i = 0; i < 8; i++) {
			int pot = (int) Math.pow(2, i);
			int b = result & pot;
			Color fillColor = (b == pot) ? Color.red : Color.LIGHT_GRAY;
			g2.setColor(fillColor);
			g2.fillOval(getX() + 21 + (7 - i) * 9, yc, 8, 8);
			g2.setColor(Color.black);
			g2.drawOval(getX() + 21 + (7 - i) * 9, yc, 8, 8);
		}
	}

	@Override
	public void changedLevel(LSLevelEvent e) {
		super.changedLevel(e);

		// output
		if (e.source.equals(getPin(OE))) {
			if (e.level == LOW) {
				// enabled
				for (int i = 0; i < 8; i++) {
					int pow = (int) Math.pow(2, i);
					getPin(i).setIoType(Pin.OUTPUT);
					LSLevelEvent evt = new LSLevelEvent(this, (result & pow) == pow);
					getPin(i).changedLevel(evt);
				}
			} else {
				// disabled
				for (int i = 0; i < 8; i++) {
					getPin(i).setIoType(Pin.HIGHIMP);
					LSLevelEvent evt = new LSLevelEvent(this, LOW);
					getPin(i).changedLevel(evt);
				}
			}
			fireRepaint();
		} else {
			// just compute everything when an event occurs

			// compute wordA
			wordA = 0;
			for (int i = WORD1; i < WORD1 + 8; i++) {
				int pot = (int) Math.pow(2, (i - 8));
				wordA += getPin(i).getLevel() ? pot : 0;
			}
			// compute wordB
			wordB = 0;
			for (int i = WORD2; i < WORD2 + 8; i++) {
				int pot = (int) Math.pow(2, (i - 16));
				wordB += getPin(i).getLevel() ? pot : 0;
			}

			// compute result
			if (getPin(SU).getLevel()) {
				wordB = 256 - wordB;
			}
			result = wordA + wordB;

			// check the result
			boolean carry = false;
			if (result > 255) {
				result = result - 256;
				carry = true;
			}
			if (result < 0) {
				result = result + 256;
				carry = true;
			}
			getPin(CF).changedLevel(new LSLevelEvent(this, carry));
			getPin(ZF).changedLevel(new LSLevelEvent(this, result == 0));

			// output on the bus if enabled
			if (getPin(OE).getLevel() == LOW) {
				// enabled
				for (int i = 0; i < 8; i++) {
					int pow = (int) Math.pow(2, i);
					LSLevelEvent evt = new LSLevelEvent(this, (result & pow) == pow);
					getPin(i).changedLevel(evt);
				}
			}
		}
	}

	@Override
	public void loadLanguage() {
		I18N.add(I18N.ALL, "cpu", "CPU");
		I18N.addGate(I18N.ALL, type, I18N.TITLE, "ALU 8bit");
		I18N.addGate(I18N.ALL, type, I18N.DESCRIPTION, "8 bit Algorithmic Logic Unit");
	}
}