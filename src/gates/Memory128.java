package gates;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import logicsim.Gate;
import logicsim.I18N;
import logicsim.LSLevelEvent;
import logicsim.Pin;

/**
 * 128bit Memory
 * 
 * The Memory chip simulates 16 8bit registers (so it is static RAM), similar to
 * the 74LS189 IC. This adds up to a total of 128 bits. The memory chip has an
 * address decoder to select the right register number.
 * 
 * @author Peter Gabriel
 * @version 1.0
 */
public class Memory128 extends Gate {

	private static final String STATE = "state";

	byte[] mem = new byte[16];
	private int address;
	private static final int WE = 4;
	private static final int OE = 5;
	private static final int DATA = 6;
	private static final int OUT = 14;
	private static final int ADD = 0;

	public Memory128() {
		super("cpu");
		type = "memory128";
		height = 110;
		width = 110;
		// 4 inputs for address encoding
		// output enable
		// write enable
		// 8 inputs for data
		// 8 tri-state outputs for data
		createInputs(14);
		createOutputs(8);

		// outputs
		for (int i = OUT; i < OUT + 8; i++) {
			getPin(i).setIoType(Pin.HIGHIMP);
			getPin(i).setProperty(TEXT, "O" + (i - 14));
			getPin(i).setY(getY() + (i - 12) * 10);
			getPin(i).setX(getX());
			getPin(i).paintDirection = Pin.RIGHT;
		}

		// encoded address
		for (int i = ADD; i < ADD + 4; i++) {
			getPin(i).paintDirection = Pin.UP;
			getPin(i).setY(getY() + height);
			getPin(i).setX(getX() + (i + 2) * 10);
			getPin(i).setProperty(TEXT, String.valueOf(i));
		}

		getPin(WE).setProperty(TEXT, "/WE");
		getPin(WE).setX(getX() + 80);
		getPin(WE).setY(getY() + height);
		getPin(WE).paintDirection = Pin.UP;

		getPin(OE).setProperty(TEXT, "/OE");
		getPin(OE).setX(getX() + 70);
		getPin(OE).setY(getY() + height);
		getPin(OE).paintDirection = Pin.UP;

		// data inputs
		for (int i = DATA; i < DATA + 8; i++) {
			getPin(i).paintDirection = Pin.DOWN;
			getPin(i).setProperty(TEXT, "I" + (i - 6));
			getPin(i).setY(getY());
			getPin(i).setX(getX() + 10 + 80 - (i - DATA) * 10);
		}

	}

	@Override
	public void draw(Graphics2D g2) {
		super.draw(g2);
		for (int i = 0; i < 8; i++) {
			int pow = (int) Math.pow(2, i);
			for (int r = 0; r < 16; r++) {
				short b = (short) (mem[15 - r] & pow);
				Color fillColor = (b == pow) ? Color.red : Color.LIGHT_GRAY;
				g2.setColor(fillColor);
				g2.fillOval(getX() + 30 + 40 - (i * 5), getY() + 15 + r * 5, 5, 5);
				g2.setColor(Color.black);
				g2.drawOval(getX() + 30 + 40 - (i * 5), getY() + 15 + r * 5, 5, 5);
			}
		}
		// draw arrow for address line select
		g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
		g2.setColor(Color.red);
		int ay = getY() + 18 + (15 - address) * 5;
		int ax = getX() + 28;
		Path2D path = new Path2D.Double();
		path.moveTo(ax, ay);
		path.lineTo(ax + 5, ay);
		path.lineTo(ax + 4, ay - 1);
		path.lineTo(ax + 4, ay + 1);
		path.lineTo(ax + 5, ay);
		path.lineTo(ax, ay);
		g2.draw(path);
	}

	@Override
	protected void loadProperties() {
		for (int i = 0; i < mem.length; i++) {
			mem[i] = (byte) getPropertyIntWithDefault(STATE + i, 0);
		}
	}

	@Override
	public void changedLevel(LSLevelEvent e) {
		super.changedLevel(e);

		// edge detection for output enable
		if (e.source.equals(getPin(OE))) {
			int ioType = e.level ? Pin.HIGHIMP : Pin.OUTPUT;
			for (int i = OUT; i < OUT + 8; i++) {
				if (ioType == Pin.HIGHIMP) {
					LSLevelEvent evt = new LSLevelEvent(this, LOW);
					getPin(i).changedLevel(evt);
				}
				getPin(i).setIoType(ioType);
			}
		}

		address = 0;
		for (int i = ADD; i < ADD + 4; i++) {
			int pow = (int) Math.pow(2, i - ADD);
			address += (getPin(i).getLevel() ? 1 : 0) * pow;
		}

		if (getPin(WE).getLevel() == LOW) {
			// write memory
			byte value = 0;
			for (int i = DATA; i < DATA + 8; i++) {
				int pow = (int) Math.pow(2, i - DATA);
				if (getPin(i).getLevel())
					value += pow;
			}
			mem[address] = value;
			setPropertyInt(STATE + address, value);
		}

		if (getPin(OE).getLevel() == LOW) {
			for (int i = OUT; i < OUT + 8; i++) {
				int pow = (int) Math.pow(2, i - OUT);
				LSLevelEvent evt = new LSLevelEvent(this, (mem[address] & pow) == pow);
				getPin(i).changedLevel(evt);
			}
		}
	}

	@Override
	public void loadLanguage() {
		I18N.add(I18N.ALL, "cpu", "CPU");
		I18N.addGate(I18N.ALL, type, I18N.TITLE, "Memory 16Byte");
		I18N.addGate(I18N.ALL, type, I18N.DESCRIPTION, "Static RAM: 16 x 8 bit registers");
	}
}