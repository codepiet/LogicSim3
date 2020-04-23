package logicsim;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.Iterator;
import java.util.Properties;

import javax.swing.JOptionPane;

public class Pin extends CircuitPart {

	public static final int INPUT = 1;
	public static final int OUTPUT = 2;

	public static final int NORMAL = 10;
	public static final int INVERTED = 11;
	public static final int HIGH = 12;
	public static final int LOW = 13;

	public static final int RIGHT = 0xa0;
	public static final int DOWN = 0xa1;
	public static final int LEFT = 0xa2;
	public static final int UP = 0xa3;

	public static final int BOUNDING_SPACE = 5;
	public static final int CONN_SIZE = 5;
	public static final String POS_EDGE_TRIG = "PosEdgeTrig";
	public static final String NEG_EDGE_TRIG = "NegEdgeTrig";

	public final int number;

	protected boolean level = false;
	public int paintDirection = RIGHT;

	public static Font smallFont = new Font(Font.SANS_SERIF, Font.PLAIN, 10);

	public int ioType = INPUT;
	/**
	 * type can be HIGH, LOW, INVERTED or NORMAL
	 */
	int levelType = NORMAL;
	
	public String label = null;
	public Gate gate;

	public Pin(int x, int y, Gate gate, int number) {
		super(x, y);
		this.type = "pin";
		this.gate = gate;
		this.number = number;
		loadProperties();
	}
	
	/*@Override
	protected void loadProperties() {
		text = getPropertyWithDefault(TEXT, TEXT_DEFAULT);
	}
	
	@Override
	public boolean hasPropertiesUI() {
		return true;
	}
	
	@Override
	public boolean showPropertiesUI(Component frame) {
		String h = (String) JOptionPane.showInputDialog(frame, I18N.getString(type, "ui.text"),
				I18N.getString(type, "ui.title"), JOptionPane.QUESTION_MESSAGE, null, null, text);
		if (h != null && h.length() > 0) {
			text = h;
			setProperty(TEXT, text);
		}
		return true;
	}

	@Override
	public Properties getProperties() {
		return this.properties;
	}*/

	/**
	 * A connector can handle this event if there is one activePart and that part is
	 * a wire in other cases we can start a wire if this is an output
	 * 
	 */
	@Override
	public void mousePressed(LSMouseEvent e) {
		if (e.isAltDown()) {
			this.showPropertiesUI(null);
		}
		else {
			super.mousePressed(e);
		}
	}

	/**
	 * draw connector label (inside gate frame)
	 * 
	 * @param g2
	 */
	private void drawLabel(Graphics2D g2) {
		int x = getX();
		int y = getY();

		g2.setFont(smallFont);
		if (label != null) {
			int lw = g2.getFontMetrics().stringWidth(label);
			if (paintDirection == RIGHT) {
				if (POS_EDGE_TRIG.equals(label)) {
					Polygon tr = new Polygon();
					tr.addPoint(x + 1 + CONN_SIZE, y - 4);
					tr.addPoint(x + 1 + CONN_SIZE, y + 4);
					tr.addPoint(x + 1 + CONN_SIZE + 8, y);
					g2.draw(tr);
				} else
					g2.drawString(label, x + CONN_SIZE + 3, y + 5);
			} else if (paintDirection == LEFT) {
				if (POS_EDGE_TRIG.equals(label)) {
					Polygon tr = new Polygon();
					tr.addPoint(x - 1 - CONN_SIZE, y - 4);
					tr.addPoint(x - 1 - CONN_SIZE, y + 4);
					tr.addPoint(x - 1 - CONN_SIZE - 8, y);
					g2.draw(tr);
				} else
					g2.drawString(label, x - CONN_SIZE - lw - 2, y + 5);
			} else if (paintDirection == UP) {
				if (POS_EDGE_TRIG.equals(label)) {
					Polygon tr = new Polygon();
					tr.addPoint(x - 4, y - 1 - CONN_SIZE);
					tr.addPoint(x + 4, y - 1 - CONN_SIZE);
					tr.addPoint(x, y - 1 - CONN_SIZE - 8);
					g2.draw(tr);
				} else
					g2.drawString(label, x - lw / 2, y - CONN_SIZE - 3);
			} else if (paintDirection == DOWN) {
				if (POS_EDGE_TRIG.equals(label)) {
					Polygon tr = new Polygon();
					tr.addPoint(x - 4, y + 1 + CONN_SIZE);
					tr.addPoint(x + 4, y + 1 + CONN_SIZE);
					tr.addPoint(x, y + 1 + CONN_SIZE + 8);
					g2.draw(tr);
				} else
					g2.drawString(label, x - lw / 2, y + CONN_SIZE + 12);
			}
		}
	}

	@Override
	public void draw(Graphics2D g2) {
		super.draw(g2);
		int x = getX();
		int y = getY();

		g2.setStroke(new BasicStroke(1));
		g2.setColor(Color.BLACK);

		drawLabel(g2);

		int offset = 0;

		// g2.setPaint(getLevel() ? Color.red : Color.black);
		if (levelType == INVERTED) {
			int ovalSize = CONN_SIZE;
			// g2.setPaint(!getLevel() ? Color.red : Color.black);
			if (paintDirection == LEFT)
				g2.drawOval(x + offset - ovalSize - 1, y - 1 - ovalSize / 2, ovalSize + 1, ovalSize + 1);
			else if (paintDirection == RIGHT)
				g2.drawOval(x + offset, y - 1 - ovalSize / 2, ovalSize + 1, ovalSize + 1);
			else if (paintDirection == DOWN)
				g2.drawOval(x + offset + 2 - ovalSize, y + 2 - ovalSize / 2, ovalSize + 1, ovalSize + 1);
			else // UP
				g2.drawOval(x + offset + 2 - ovalSize, y - 4 - ovalSize / 2, ovalSize + 1, ovalSize + 1);
		} else if (levelType == HIGH) {
			if (ioType == OUTPUT)
				throw new RuntimeException("OUTPUT may not be set HIGH");
			WidgetHelper.drawStringCentered(g2, "1", x + 1, y - 2);
		} else if (levelType == LOW) {
			if (ioType == OUTPUT)
				throw new RuntimeException("OUTPUT may not be set LOW");
			WidgetHelper.drawStringCentered(g2, "0", x + 2, y - 2);
		} else {
			// normal
			g2.setStroke(new BasicStroke(1));
			if (paintDirection == LEFT || paintDirection == RIGHT) {
				if (paintDirection == LEFT) {
					offset = -CONN_SIZE - 1;
				} else {
					offset = -1;
				}
				g2.fillRect(x + offset + 1, y - 1, CONN_SIZE + 1, 3);
			} else {
				if (paintDirection == UP) {
					offset = -CONN_SIZE;
				}
				g2.fillRect(x - 1, y + offset, 3, CONN_SIZE + 1);
			}
		}
		if (text != "<Label>") {
			int tw = g2.getFontMetrics().stringWidth(text);
			if (paintDirection == DOWN || paintDirection == RIGHT) {
			    g2.drawString(text, getX() - tw - 1, getY());
			}
			else if (paintDirection == UP) {
				g2.drawString(text, getX() + 3, getY() + 5);
			}
			else {
				g2.drawString(text, getX() + 3, getY());
			}
			
		}
	}

	public void setLevelType(int levelType) {
		// TODO if we set a level type of type HIGH or LOW we have to remove the wire
		// completely
		this.levelType = levelType;
	}

	public boolean getLevel() {
		if (levelType == NORMAL)
			return level;
		if (levelType == INVERTED)
			return !level;
		if (levelType == HIGH)
			return true;
		// low
		return false;
	}

	@Override
	public Rectangle getBoundingBox() {
		return new Rectangle(getX() - BOUNDING_SPACE / 2, getY() - BOUNDING_SPACE / 2, BOUNDING_SPACE, BOUNDING_SPACE);
	}

	public boolean isAt(int atX, int atY) {
		return (atX > getX() - 5 && atX < getX() + 5 && atY > getY() - 5 && atY < getY() + 5);
	}

	public void setDirection(int dir) {
		paintDirection = dir;
	}

	public boolean isInput() {
		return ioType == Pin.INPUT;
	}

	public boolean isOutput() {
		return ioType == Pin.OUTPUT;
	}

	@Override
	public String toString() {
		String s = getId() + " - " + (getLevel() ? "HIGH" : "LOW");
//		if (getListeners().size() > 0) {
//			s += "\n send updates to\n";
//			for (LSLevelListener l : getListeners())
//				s += indent(((CircuitPart) l).getId(), 3) + "\n";
//		}
//		s += "-----------------";
		return s;
	}

	public void setLevel(boolean b) {
		level = b;
	}

	public static String actionToString(int id) {
		if (id == HIGH)
			return "HIGH";
		if (id == LOW)
			return "LOW";
		if (id == INVERTED)
			return "INVERTED";
		if (id == NORMAL)
			return "NORMAL";

		return null;
	}

	@Override
	public String getId() {
		String it = "I";
		if (ioType == Pin.OUTPUT)
			it = "O";
		String lt = "N";
		if (levelType == Pin.HIGH)
			lt = "H";
		else if (levelType == Pin.LOW)
			lt = "L";
		else if (levelType == Pin.INVERTED)
			lt = "I";
		return it + number + it + "-" + lt + "-" + (label == null ? "" : "\"" + label + "\"") + getX() + ":" + getY()
				+ "@" + gate.getId();
	}

	@Override
	public void changedLevel(LSLevelEvent e) {
		// source has to be a Gate or a Wire
		if (e.source instanceof Gate) {
			if (isOutput()) {
				if (level != e.level || e.force) {
					level = e.level;
					// propagate this to the outside
					fireChangedLevel(new LSLevelEvent(this, getLevel(), e.force));
				}
			}
		} else if (e.source instanceof Wire) {
			// signal is from outside, propagate this to gate
			// call gate directly
			if (level != e.level || e.force) {
				level = e.level;
				gate.changedLevel(new LSLevelEvent(this, getLevel(), e.force));

				// and call all other wires which are connected to the pin
				fireChangedLevel(e);
			}
		} else
			throw new RuntimeException("pins communicate with gates or wires only! source is " + e.source.getId()
					+ ", target is " + getId());
	}

	public boolean getInternalLevel() {
		return level;
	}

	@Override
	public void connect(CircuitPart part) {
		// pins connect to wires only
		if (!(part instanceof Wire))
			throw new RuntimeException("part is not a wire! cannot connect to pin");
		Wire wire = (Wire) part;
		wire.addLevelListener(this);
		this.addLevelListener(wire);
		if (isInput()) {
			changedLevel(new LSLevelEvent(wire, wire.getLevel(), true));
		} else {
			LSLevelEvent evt = new LSLevelEvent(this, getLevel(), true);
			fireChangedLevel(evt);
		}
	}

	public void disconnect() {
		for (Iterator<LSLevelListener> iterator = getListeners().iterator(); iterator.hasNext();) {
			if (iterator instanceof Wire) {
				Wire w = (Wire) iterator;
				w.disconnect(null);
				removeLevelListener(w);
				iterator.remove();
			}
		}
	}

}
