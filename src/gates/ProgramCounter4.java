package gates;

import java.awt.Color;
import java.awt.Graphics2D;

import logicsim.Gate;
import logicsim.I18N;
import logicsim.LSLevelEvent;
import logicsim.LSMouseEvent;
import logicsim.Pin;

/**
 * 4bit Program Counter
 * 
 * The Program Counter keeps track of the next instruction.
 * 
 * @author Peter Gabriel
 * @version 1.0
 */
public class ProgramCounter4 extends Gate {

	private static final String STATE = "state";

	int content = 0;
	private static final int DATA = 0;
	private static final int CO = 4;
	private static final int JUMP = 5;
	private static final int CLOCK = 6;
	private static final int CLEAR = 7;
	private static final int CE = 8;
	private static final int INTOUT = 9;

	public ProgramCounter4() {
		super("cpu");
		type = "pgmcnt4";
		height = 70;
		width = 70;
		createInputs(9);
		createOutputs(4);

		for (int i = DATA; i < DATA + 4; i++) {
			getPin(i).setIoType(Pin.HIGHIMP);
			getPin(i).setProperty(TEXT, String.valueOf(i));
			getPin(i).setX(getX());
			getPin(i).setY(getY() + (i + 2) * 10);
			getPin(i).paintDirection = Pin.RIGHT;
		}
		for (int i = CO; i < INTOUT; i++) {
			getPin(i).paintDirection = Pin.LEFT;
			getPin(i).setX(getX() + width);
			getPin(i).setY(getY() + (i - CO) * 10 + 20);
		}
		getPin(CO).setProperty(TEXT, "/OE");
		getPin(JUMP).setProperty(TEXT, "/J");
		getPin(CLEAR).setProperty(TEXT, "/CL");
		getPin(CE).setProperty(TEXT, "CE");
		getPin(CLOCK).setProperty(TEXT, Pin.POS_EDGE_TRIG);

		// internal outputs
		for (int i = INTOUT; i < INTOUT + 4; i++) {
			getPin(i).setDirection(Pin.DOWN);
			getPin(i).setY(getY());
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
			mx -= getX();
			mx -= 16;
			mx /= 10;
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
		if (e.source.equals(getPin(JUMP))) {
			if (e.level == LOW) {
				for (int i = DATA; i < DATA + 4; i++) {
					getPin(i).setIoType(Pin.INPUT);
				}
			} else {
				for (int i = DATA; i < DATA + 4; i++) {
					getPin(i).setIoType((getPin(CO).getLevel() == LOW) ? Pin.OUTPUT : Pin.HIGHIMP);
				}
			}
		}
		if (e.source.equals(getPin(CO)) && getPin(JUMP).getLevel() == HIGH) {
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
			if (getPin(JUMP).getLevel() == LOW) {
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
			if (getPin(CE).getLevel() == HIGH) {
				content++;
				fireRepaint();
			}
		}
		// if output enable is low, send content to bus
		if (getPin(CO).getLevel() == LOW && getPin(JUMP).getLevel() == HIGH) {
			for (int i = 0; i < 4; i++) {
				LSLevelEvent evt = null;
				int pot = (int) Math.pow(2, i);
				evt = new LSLevelEvent(this, (content & pot) == pot);
				getPin(i + DATA).changedLevel(evt);
			}
		}

		if (e.source.equals(getPin(CLEAR)) && e.level == LOW) {
			content = 0;
			LSLevelEvent evt = new LSLevelEvent(this, LOW);
			for (int i = 0; i < 4; i++) {
				getPin(i + INTOUT).changedLevel(evt);
			}
			if (getPin(CO).getLevel() == LOW) {
				for (int i = 0; i < 4; i++) {
					getPin(i + DATA).changedLevel(evt);
				}
			}
		}
	}

	@Override
	public void loadLanguage() {
		I18N.addGate(I18N.ALL, type, I18N.TITLE, "ProgramCounter 4bit");
		I18N.addGate(I18N.ALL, type, I18N.DESCRIPTION, "4 bit Program Counter");
		I18N.addGate("de", type, I18N.TITLE, "Programmzähler 4bit");
		I18N.addGate("de", type, I18N.DESCRIPTION, "4 bit Programmzähler");
	}
}