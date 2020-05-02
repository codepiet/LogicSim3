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

	public ALU8() {
		super("cpu");
		type = "alu8";
		height = 110;
		width = 110;
		createOutputs(8);
		createInputs(18);

		// outputs from 0-7
		// word 1 from pin 8-15
		// word 2 from pin 16-23
		// enable output is 24
		// subtract signal is 25

		for (int i = 0; i < 8; i++) {
			getPin(i).paintDirection = Pin.RIGHT;
			getPin(i).ioType = Pin.HIGHIMP;
			getPin(i).setX(getX());
			getPin(i).setY(getY() + (i + 2) * 10);
			getPin(i).setProperty(TEXT, String.valueOf(i));
		}
		for (int i = 8; i < 16; i++) {
			getPin(i).paintDirection = Pin.DOWN;
			getPin(i).setX(getX() + (i - 6) * 10);
			getPin(i).setY(getY());
			getPin(i).setProperty(TEXT, String.valueOf(i - 8));
		}
		for (int i = 16; i < 24; i++) {
			getPin(i).paintDirection = Pin.UP;
			getPin(i).setX(getX() + (i - 14) * 10);
			getPin(i).setY(getY() + height);
			getPin(i).setProperty(TEXT, String.valueOf(i - 16));
		}

		getPin(24).setProperty(TEXT, "/OE");
		getPin(24).paintDirection = Pin.LEFT;
		getPin(24).setX(getX() + width);
		getPin(24).setY(getY() + 30);

		getPin(25).setProperty(TEXT, "/SU");
		getPin(25).paintDirection = Pin.LEFT;
		getPin(25).setX(getX() + width);
		getPin(25).setY(getY() + height - 30);
	}

	@Override
	public void draw(Graphics2D g2) {
		super.draw(g2);
		for (int i = 0; i < 8; i++) {
			int pot = (int) Math.pow(2, i);
			int b = result & pot;
			Color fillColor = (b == pot) ? Color.red : Color.LIGHT_GRAY;
			g2.setColor(fillColor);
			g2.fillOval(getX() + 50, getY() + 15 + i * 9 + 6, 8, 8);
			g2.setColor(Color.black);
			g2.drawOval(getX() + 50, getY() + 15 + i * 9 + 6, 8, 8);
		}
	}

	@Override
	public void changedLevel(LSLevelEvent e) {
		super.changedLevel(e);

		// output
		if (e.source.equals(getPin(24))) {
			if (e.level == LOW) {
				// enabled
				for (int i = 0; i < 8; i++) {
					int pow = (int) Math.pow(2, i);
					LSLevelEvent evt = new LSLevelEvent(this, (result & pow) == pow);
					getPin(i).changedLevel(evt);
				}
			} else {
				// disabled
				for (int i = 0; i < 8; i++) {
					LSLevelEvent evt = new LSLevelEvent(this, LOW);
					getPin(i).changedLevel(evt);
				}
			}
		} else {
			// just compute everything when an event occurs

			// compute wordA
			wordA = 0;
			for (int i = 8; i < 16; i++) {
				int pot = (int) Math.pow(2, (i - 8));
				wordA += getPin(i).getLevel() ? pot : 0;
			}
			// compute wordB
			wordB = 0;
			for (int i = 16; i < 24; i++) {
				int pot = (int) Math.pow(2, (i - 16));
				wordB += getPin(i).getLevel() ? pot : 0;
			}

			// compute result
			result = getPin(25).getLevel() ? wordA + wordB : wordA - wordB;
			// check the result
			if (result > 255)
				result = result - 256;
			if (result < 0)
				result = result + 256;

			// output on the bus if enabled
			if (getPin(24).getLevel() == LOW) {
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