package gates;

import java.awt.Color;
import java.awt.Graphics2D;

import logicsim.Gate;
import logicsim.I18N;
import logicsim.LSLevelEvent;
import logicsim.Pin;

/**
 * 8bit Register
 * 
 * The register has 8 binary inputs and 8 outputs to connect to a bus. Clock,
 * output enable and load inputs ensure control of the register.
 * 
 * @author Peter Gabriel
 * @version 1.0
 */
public class Register8 extends Gate {

	int content = 0;

	public Register8() {
		super("cpu");
		type = "register8";
		height = 130;
		width = 110;
		createInputs(11);
		createOutputs(16);

		for (int i = 0; i < 8; i++) {
			getPin(i).setProperty(TEXT, "I" + i);
			getPin(i).setY(getY() + (i + 2) * 10);
		}
		for (int i = 8; i < 11; i++) {
			getPin(i).setY(getY() + (i + 2) * 10);
		}
		getPin(8).setProperty(TEXT, "/OE");
		getPin(9).setProperty(TEXT, "/LD");
		getPin(10).setProperty(TEXT, Pin.POS_EDGE_TRIG);
		for (int i = 11; i < 19; i++) {
			getPin(i).setProperty(TEXT, "O" + (i - 11));
			getPin(i).setY(getY() + (i - 9) * 10);
		}

		// internal outputs (e.g. for ALU)
		for (int i = 19; i < 27; i++) {
			getPin(i).setDirection(Pin.DOWN);
			getPin(i).setY(getY());
			getPin(i).setX(getX() + (10 * (i - 17)));
			getPin(i).setProperty(TEXT, String.valueOf(i - 19));
		}

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
		I18N.addGate(I18N.ALL, type, I18N.TITLE, "Register 8bit");
		I18N.addGate(I18N.ALL, type, I18N.DESCRIPTION, "8 bit register");
		I18N.addGate("de", type, I18N.TITLE, "Register 8bit");
		I18N.addGate("de", type, I18N.DESCRIPTION, "8 bit Register");
	}
}