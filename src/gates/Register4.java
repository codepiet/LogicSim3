package gates;

import java.awt.Color;
import java.awt.Graphics2D;

import logicsim.Gate;
import logicsim.I18N;
import logicsim.LSLevelEvent;
import logicsim.LSMouseEvent;
import logicsim.Pin;

/**
 * 4bit Register
 * 
 * The register has 4 binary tri state outputs. Clock, output enable and load
 * inputs ensure control of the register.
 * 
 * @author Peter Gabriel
 * @version 1.0
 */
public class Register4 extends Gate {

	private static final String STATE = "state";

	int content = 0;
	private static final int DATA = 0;
	private static final int OE = 4;
	private static final int LOAD = 5;
	private static final int CLOCK = 6;
	private static final int CLEAR = 7;
	private static final int INTOUT = 8;

	public Register4() {
		super("cpu");
		type = "register4";
		height = 70;
		width = 70;
		createInputs(8);
		createOutputs(4);

		for (int i = DATA; i < DATA + 4; i++) {
			getPin(i).setIoType(Pin.HIGHIMP);
			getPin(i).setProperty(TEXT, String.valueOf(i));
			getPin(i).setX(getX() + (4 - i + 1) * 10);
			getPin(i).setY(getY());
			getPin(i).paintDirection = Pin.DOWN;
		}
		for (int i = OE; i < INTOUT; i++) {
			getPin(i).paintDirection = Pin.RIGHT;
			getPin(i).setX(getX());
			getPin(i).setY(getY() + (i - OE) * 10 + 30);
		}
		getPin(OE).setProperty(TEXT, "/OE");
		getPin(LOAD).setProperty(TEXT, "/LD");
		getPin(CLEAR).setProperty(TEXT, "CL");
		getPin(CLOCK).setProperty(TEXT, Pin.POS_EDGE_TRIG);

		// internal outputs
		for (int i = INTOUT; i < INTOUT + 4; i++) {
			getPin(i).setDirection(Pin.UP);
			getPin(i).setY(getY() + height);
			getPin(i).setX(getX() + (10 * (4 - (i - INTOUT) + 1)));
			getPin(i).setProperty(TEXT, "i" + String.valueOf(i - INTOUT));
		}

	}

	@Override
	public void draw(Graphics2D g2) {
		super.draw(g2);
		for (int i = 0; i < 4; i++) {
			int pot = (int) Math.pow(2, i);
			int b = content & pot;
			Color fillColor = (b == pot) ? Color.red : Color.LIGHT_GRAY;
			g2.setColor(fillColor);
			int OVAL_SIZE = 9;
			g2.fillOval(getX() + 16 + (3 - i) * 10, yc - OVAL_SIZE / 2, OVAL_SIZE, OVAL_SIZE);
			g2.setColor(Color.black);
			g2.drawOval(getX() + 16 + (3 - i) * 10, yc - OVAL_SIZE / 2, OVAL_SIZE, OVAL_SIZE);
		}
	}

	@Override
	protected void loadProperties() {
		content = getPropertyIntWithDefault(STATE, 0);
		updateInternalOutputs();
	}

	private void updateInternalOutputs() {
		for (int i = 0; i < 4; i++) {
			int pow = (int) Math.pow(2, i);
			boolean b = (content & pow) == pow;
			LSLevelEvent evt = new LSLevelEvent(this, b, force);
			getPin(i + INTOUT).changedLevel(evt);
		}
	}

	@Override
	public void reset() {
		force = true;
		updateInternalOutputs();
		force = false;
	}

	@Override
	public void mousePressedSim(LSMouseEvent e) {
		super.mousePressedSim(e);
		int mx = e.getX();
		int my = e.getY();
		if (my > yc - 5 && my < yc + 5) {
			mx = (mx - getX() - 16) / 10;
			mx = 3 - mx;
			if (mx >= 0 && mx <= 3) {
				int pow = (int) Math.pow(2, mx);
				if ((pow & content) > 0) {
					content -= pow;
				} else {
					content += pow;
				}
				updateInternalOutputs();
			}
		}
	}

	@Override
	public void changedLevel(LSLevelEvent e) {
		super.changedLevel(e);

		// LOAD edge detection
		if (e.source.equals(getPin(LOAD))) {
			if (e.level == LOW) {
				for (int i = DATA; i < DATA + 4; i++) {
					getPin(i).setIoType(Pin.INPUT);
				}
			} else {
				for (int i = DATA; i < DATA + 4; i++) {
					getPin(i).setIoType((getPin(OE).getLevel() == LOW) ? Pin.OUTPUT : Pin.HIGHIMP);
				}
			}
		}
		if (e.source.equals(getPin(OE)) && getPin(LOAD).getLevel() == HIGH) {
			if (e.level == LOW) {
				for (int i = DATA; i < DATA + 4; i++) {
					getPin(i).setIoType(Pin.OUTPUT);
				}
			} else {
				for (int i = DATA; i < DATA + 4; i++) {
					getPin(i).setIoType(Pin.HIGHIMP);
				}
			}
		}

		// clk rising edge detection
		if (e.source.equals(getPin(CLOCK)) && e.level == HIGH) {
			// only set register if load is low
			if (getPin(LOAD).getLevel() == LOW) {
				content = 0;
				for (int i = 0; i < 4; i++) {
					boolean b = getPin(DATA + i).getLevel();
					if (b) {
						int pow = (int) Math.pow(2, i + DATA);
						content = content + pow;
					}
					LSLevelEvent evt = new LSLevelEvent(this, b);
					getPin(i + DATA + INTOUT).changedLevel(evt);
				}
				setPropertyInt(STATE, content);
				fireRepaint();
			}
		}
		// if output enable is low, send content to bus
		if (getPin(OE).getLevel() == LOW && getPin(LOAD).getLevel() == HIGH) {
			for (int i = 0; i < 4; i++) {
				LSLevelEvent evt = null;
				int pot = (int) Math.pow(2, i);
				evt = new LSLevelEvent(this, (content & pot) == pot);
				getPin(i + DATA).changedLevel(evt);
			}
		}

		if (e.source.equals(getPin(CLEAR)) && e.level == HIGH) {
			content = 0;
			for (int i = 0; i < 4; i++) {
				LSLevelEvent evt = new LSLevelEvent(this, LOW);
				getPin(i + INTOUT).changedLevel(evt);
			}
			if (getPin(OE).getLevel() == LOW)
				for (int i = 0; i < 4; i++) {
					LSLevelEvent evt = new LSLevelEvent(this, LOW);
					getPin(i + DATA).changedLevel(evt);
				}
		}

	}

	@Override
	public void loadLanguage() {
		I18N.add(I18N.ALL, "cpu", "CPU");
		I18N.addGate(I18N.ALL, type, I18N.TITLE, "Register 4bit");
		I18N.addGate(I18N.ALL, type, I18N.DESCRIPTION, "4 bit register");
		I18N.addGate("de", type, I18N.TITLE, "Register 4bit");
		I18N.addGate("de", type, I18N.DESCRIPTION, "4 bit Register");
	}
}