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

	int content = 0;

	public ALU8() {
		super("cpu");
		type = "alu8";
		height = 110;
		width = 110;
		createOutputs(8);
		createInputs(18);

		// word 1 from pin 8-15
		// word 2 from pin 16-23
		// enable output is 24
		// subtract signal is 25

		for (int i = 0; i < 8; i++) {
			getPin(i).paintDirection = Pin.RIGHT;
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
			int b = content & pot;
			Color fillColor = (b == pot) ? Color.red : Color.LIGHT_GRAY;
			g2.setColor(fillColor);
			g2.fillOval(getX() + 50, getY() + 20 + i * 10 + 6, 8, 8);
			g2.setColor(Color.black);
			g2.drawOval(getX() + 50, getY() + 20 + i * 10 + 6, 8, 8);
		}
	}

	@Override
	public void changedLevel(LSLevelEvent e) {
		super.changedLevel(e);
		int newContent = content;

		// rising edge detection
		if (e.source.equals(getPin(10)) && e.level == HIGH) {
			// only set register if load is low
			if (getPin(9).getLevel() == LOW) {
				int b1 = getPin(0).getLevel() ? 1 : 0;
				int b2 = getPin(1).getLevel() ? 1 : 0;
				int b4 = getPin(2).getLevel() ? 1 : 0;
				int b8 = getPin(3).getLevel() ? 1 : 0;
				int b16 = getPin(4).getLevel() ? 1 : 0;
				int b32 = getPin(5).getLevel() ? 1 : 0;
				int b64 = getPin(6).getLevel() ? 1 : 0;
				int b128 = getPin(7).getLevel() ? 1 : 0;
				newContent = b1 + (b2 << 1) + (b4 << 2) + (b8 << 3) + (b16 << 4) + (b32 << 5) + (b64 << 6)
						+ (b128 << 7);
				content = newContent;
				for (int i = 0; i < 8; i++) {
					int pot = (int) Math.pow(2, i);
					LSLevelEvent evt = new LSLevelEvent(this, (content & pot) == pot);
					getPin(i + 19).changedLevel(evt);
				}
			}
		}
		// if output enable is low, send content to bus
		if (getPin(8).getLevel() == LOW) {
			for (int i = 0; i < 8; i++) {
				int pot = (int) Math.pow(2, i);
				LSLevelEvent evt = new LSLevelEvent(this, (content & pot) == pot);
				getPin(i + 11).changedLevel(evt);
			}
		}
		if (getPin(8).getLevel() == HIGH) {
			for (int i = 0; i < 8; i++) {
				LSLevelEvent evt = new LSLevelEvent(this, LOW);
				getPin(i + 11).changedLevel(evt);
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