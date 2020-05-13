package gates;

import java.awt.Color;
import java.awt.Graphics2D;

import logicsim.Gate;
import logicsim.I18N;
import logicsim.LSLevelEvent;
import logicsim.LSMouseEvent;
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

	private static final String STATE = "state";

	int content = 0;
	private static final int DATA = 0;
	private static final int OE = 8;
	private static final int LOAD = 9;
	private static final int CLOCK = 10;
	private static final int CLEAR = 11;
	private static final int INTOUT = 12;

	public Register8() {
		super("cpu");
		type = "register8";
		height = 110;
		width = 110;
		createInputs(12);
		createOutputs(8);

		// 0 to 7 for BUS INPUTS
		// 8 is OE-Signal (INPUT)
		// 9 is LOAD (INPUT)
		// 10 is CLOCK (INPUT)
		for (int i = DATA; i < DATA + 8; i++) {
			getPin(i).setIoType(Pin.HIGHIMP);
			getPin(i).setProperty(TEXT, String.valueOf(i));
			getPin(i).setY(getY() + (i + 2) * 10);
		}
		for (int i = OE; i < INTOUT; i++) {
			getPin(i).paintDirection = Pin.LEFT;
			getPin(i).setX(getX() + width);
			getPin(i).setY(getY() + (i - OE) * 10 + 70);
		}
		getPin(8).setProperty(TEXT, "/OE");
		getPin(9).setProperty(TEXT, "/LD");
		getPin(CLEAR).setProperty(TEXT, "CL");
		getPin(10).setProperty(TEXT, Pin.POS_EDGE_TRIG);

		// internal outputs (e.g. for ALU)
		for (int i = INTOUT; i < INTOUT + 8; i++) {
			getPin(i).setDirection(Pin.DOWN);
			getPin(i).setY(getY());
			getPin(i).setX(getX() + (10 * (7 - (i - INTOUT) + 2)));
			getPin(i).setProperty(TEXT, "i" + String.valueOf(i - INTOUT));
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
			int OVAL_SIZE = 9;
			g2.fillOval(getX() + 16 + (7 - i) * 10, yc - OVAL_SIZE / 2, OVAL_SIZE, OVAL_SIZE);
			g2.setColor(Color.black);
			g2.drawOval(getX() + 16 + (7 - i) * 10, yc - OVAL_SIZE / 2, OVAL_SIZE, OVAL_SIZE);
		}
	}

	@Override
	protected void loadProperties() {
		content = getPropertyIntWithDefault(STATE, 0);
		updateInternalOutputs();
	}

	private void updateInternalOutputs() {
		for (int i = 0; i < 8; i++) {
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
			mx -= getX();
			mx -= 16;
			mx /= 10;
			mx = 7 - mx;
			if (mx >= 0 && mx <= 7) {
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
				for (int i = DATA; i < DATA + 8; i++) {
					getPin(i).setIoType(Pin.INPUT);
				}
			} else {
				for (int i = DATA; i < DATA + 8; i++) {
					getPin(i).setIoType((getPin(OE).getLevel() == LOW) ? Pin.OUTPUT : Pin.HIGHIMP);
				}
			}
		}
		if (e.source.equals(getPin(OE)) && getPin(LOAD).getLevel() == HIGH) {
			if (e.level == LOW) {
				for (int i = DATA; i < DATA + 8; i++) {
					getPin(i).setIoType(Pin.OUTPUT);
				}
			} else {
				for (int i = DATA; i < DATA + 8; i++) {
					getPin(i).setIoType(Pin.HIGHIMP);
				}
			}
		}

		// clk rising edge detection
		if (e.source.equals(getPin(CLOCK)) && e.level == HIGH) {
			// only set register if load is low
			if (getPin(LOAD).getLevel() == LOW) {
				content = 0;
				for (int i = 0; i < 8; i++) {
					boolean b = getPin(DATA + i).getLevel();
					if (b) {
						int pow = (int) Math.pow(2, i + DATA);
						content = content + pow;
					}
					LSLevelEvent evt = new LSLevelEvent(this, b);
					getPin(i + INTOUT).changedLevel(evt);
				}
				setPropertyInt(STATE, content);
				fireRepaint();
			}
		}
		// if output enable is low, send content to bus
		if (getPin(OE).getLevel() == LOW && getPin(LOAD).getLevel() == HIGH) {
			for (int i = 0; i < 8; i++) {
				LSLevelEvent evt = null;
				int pot = (int) Math.pow(2, i);
				evt = new LSLevelEvent(this, (content & pot) == pot);
				getPin(i + DATA).changedLevel(evt);
			}
		}

		if (e.source.equals(getPin(CLEAR)) && e.level == HIGH) {
			content = 0;
			for (int i = 0; i < 8; i++) {
				LSLevelEvent evt = new LSLevelEvent(this, LOW);
				getPin(i + INTOUT).changedLevel(evt);
			}
			if (getPin(OE).getLevel() == LOW)
				for (int i = 0; i < 8; i++) {
					LSLevelEvent evt = new LSLevelEvent(this, LOW);
					getPin(i + DATA).changedLevel(evt);
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