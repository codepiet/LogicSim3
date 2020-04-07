package gates;

import java.awt.Font;
import java.awt.Graphics2D;

import logicsim.Pin;
import logicsim.Gate;

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
			getPin(i).label = "I" + (i + 1);
			getPin(i).setDirection(0xa0 + i);
		}
		getPin(0).moveTo(getX(), getY() + offset);
		getPin(1).moveTo(getX() + getWidth() - offset, getY());
		getPin(2).moveTo(getX() + getWidth(), getY() + getHeight() - offset);
		getPin(3).moveTo(getX() + offset, getY() + getHeight());

		for (int i = 0; i < 4; i++) {
			getPin(i + 4).label = "O" + (i + 1);
			getPin(i + 4).setLevelType(Pin.INVERTED);
			getPin(i + 4).setDirection(0xa0 + i);
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
	public void simulate() {
		for (int i = 0; i < getNumInputs(); i++)
			getPin(i + 4).setLevel(getPin(i).getLevel());
	}

	@Override
	public void reset() {
		for (int i = 0; i < getNumInputs(); i++)
			getPin(i + 4).setLevel(getPin(i).getLevel());
	}
}