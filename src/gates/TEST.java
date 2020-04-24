package gates;

import java.awt.Font;
import java.awt.Graphics2D;

import logicsim.Pin;
import logicsim.Gate;
import logicsim.I18N;
import logicsim.LSLevelEvent;

/**
 * Test Gate for LogicSim
 * 
 * @author Peter Gabriel
 * @version 2.0
 */
public class TEST extends Gate {
	static final long serialVersionUID = 4521959944440523564L;

	public TEST() {
		super("test");
		label = "TEST";
		type = "test";
		width = 80;
		height = 80;
		createInputs(4);
		createOutputs(4);
		int offset = 30;

		for (int i = 0; i < 4; i++) {
			getPin(i).setProperty(TEXT, "I" + (i + 1));
			getPin(i).setDirection(Pin.RIGHT + i);
		}
		getPin(0).moveTo(getX(), getY() + offset);
		getPin(1).moveTo(getX() + getWidth() - offset, getY());
		getPin(2).moveTo(getX() + getWidth(), getY() + getHeight() - offset);
		getPin(3).moveTo(getX() + offset, getY() + getHeight());

		for (int i = 0; i < 4; i++) {
			getPin(i + 4).setProperty(TEXT, "O" + (i + 1));
			getPin(i + 4).setLevelType(Pin.INVERTED);
			getPin(i + 4).setDirection(Pin.RIGHT + i);
		}
		getPin(4).moveTo(getX(), getY() + getHeight() - offset);
		getPin(5).moveTo(getX() + offset, getY());
		getPin(6).moveTo(getX() + getWidth(), getY() + offset);
		getPin(7).moveTo(getX() + getWidth() - offset, getY() + getHeight());

		reset();
	}

	@Override
	protected void drawLabel(Graphics2D g2, String lbl, Font font) {
		super.drawLabel(g2, label, bigFont);
	}

	@Override
	public void changedLevel(LSLevelEvent e) {
		Pin p = (Pin) e.source;
		getPin(p.number + 4).changedLevel(new LSLevelEvent(this, e.level));
	}

	@Override
	public void loadLanguage() {
		I18N.addGate(I18N.ALL, type, I18N.TITLE, "Test");
		I18N.addGate(I18N.ALL, type, I18N.TITLE, "Test Gate for experimenting with settings");
	}
}