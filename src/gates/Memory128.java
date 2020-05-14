package gates;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import logicsim.Gate;
import logicsim.I18N;
import logicsim.LSLevelEvent;
import logicsim.LSMouseEvent;
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
	private static final int WE = 12;
	private static final int OE = 13;
	private static final int DATA = 0;
	private static final int ADDRESS = 8;
	private static final int CLK = 14;

	public Memory128() {
		super("cpu");
		type = "memory128";
		height = 110;
		width = 110;
		// 8 tri-state in/outputs for data
		createOutputs(8);
		// 4 inputs for address encoding
		// write enable
		// output enable
		createInputs(7);

		// DATA Bus IO
		for (int i = 0; i < 8; i++) {
			getPin(i).setIoType(Pin.HIGHIMP);
			getPin(i).setProperty(TEXT, String.valueOf(i));
			getPin(i).setX(getX() + width);
			getPin(i).setY(getY() + (i + 2) * 10);
			getPin(i).paintDirection = Pin.LEFT;
		}
		// encoded address
		for (int i = 0; i < 4; i++) {
			getPin(ADDRESS + i).paintDirection = Pin.DOWN;
			getPin(ADDRESS + i).setX(getX() + (4 - i + 3) * 10);
			getPin(ADDRESS + i).setY(getY());
			getPin(ADDRESS + i).setProperty(TEXT, "a" + String.valueOf(i));
		}

		getPin(WE).setProperty(TEXT, "WE");
		getPin(WE).setX(getX());
		getPin(WE).setY(getY() + 30);
		getPin(WE).paintDirection = Pin.RIGHT;

		getPin(OE).setProperty(TEXT, "/OE");
		getPin(OE).setX(getX());
		getPin(OE).setY(getY() + 50);
		getPin(OE).paintDirection = Pin.RIGHT;

		getPin(CLK).setProperty(TEXT, Pin.POS_EDGE_TRIG);
		getPin(CLK).setX(getX());
		getPin(CLK).setY(getY() + 70);
		getPin(CLK).paintDirection = Pin.RIGHT;

	}

	private void updateOutputs() {
		address = 0;
		for (int i = 0; i < 4; i++) {
			int pow = (int) Math.pow(2, i);
			address += getPin(i + ADDRESS).getLevel() ? pow : 0;
		}
		// update output
		for (int i = 0; i < 8; i++) {
			int pow = (int) Math.pow(2, i);
			boolean b = (mem[address] & pow) == pow;
			//System.out.println(address + " " + i + ": " + b);
			getPin(DATA + i).changedLevel(new LSLevelEvent(this, b));
		}
		fireRepaint();
	}

	@Override
	public void mousePressedSim(LSMouseEvent e) {
		super.mousePressedSim(e);
		int mx = e.getX();
		int my = e.getY();
		if (mx > getX() + 34 && mx < getX() + 76) {
			if (my > getY() + 14 && my < getY() + 95) {
				mx = mx - getX() - 34;
				mx = mx / 5;
				mx = 7 - mx;
				my = my - getY() - 14;
				my = my / 5;
				my = 15 - my;
				if (my >= 0 && my <= 15) {
					int pow = (int) Math.pow(2, mx);
					if ((mem[my] & pow) == pow)
						mem[my] -= pow;
					else
						mem[my] += pow;
					setPropertyInt(STATE + my, mem[my]);
				}
			}
		}
	}

	@Override
	public void draw(Graphics2D g2) {
		super.draw(g2);
		for (int i = 0; i < 8; i++) {
			int pow = (int) Math.pow(2, i);
			for (int r = 0; r < mem.length; r++) {
				short b = (short) (mem[mem.length - 1 - r] & pow);
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
	public void reset() {
		updateOutputs();
	}

	@Override
	public void changedLevel(LSLevelEvent e) {
		super.changedLevel(e);

		if (e.source.equals(getPin(WE))) {
			// edge detection for write enable
			int ioType = e.level ? Pin.INPUT : Pin.HIGHIMP;
			for (int i = 0; i < 8; i++) {
				getPin(i + DATA).setIoType(ioType);
			}
		} else if (e.source.equals(getPin(OE))) {
			// edge detection for output enable
			updateOutputs();
			int ioType = e.level ? Pin.HIGHIMP : Pin.OUTPUT;
			for (int i = 0; i < 8; i++) {
				getPin(i + DATA).setIoType(ioType);
			}
		} else if (e.source.equals(getPin(CLK)) && e.level == HIGH) {
			if (getPin(WE).getLevel() == HIGH) {
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
		} else if (e.source.equals(getPin(ADDRESS)) || e.source.equals(getPin(ADDRESS + 1))
				|| e.source.equals(getPin(ADDRESS + 2)) || e.source.equals(getPin(ADDRESS + 3))) {
		}
		updateOutputs();
		fireRepaint();
	}

	@Override
	public void loadLanguage() {
		I18N.add(I18N.ALL, "cpu", "CPU");
		I18N.addGate(I18N.ALL, type, I18N.TITLE, "Memory 16Byte");
		I18N.addGate(I18N.ALL, type, I18N.DESCRIPTION, "Static RAM: 16 x 8 bit registers");
	}
}