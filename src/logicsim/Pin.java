package logicsim;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.util.ArrayList;

public class Pin extends CircuitPart {

	public static final int INPUT = 1;
	public static final int OUTPUT = 2;
	public static final int HIGHIMP = 3;

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

	public int number;

	protected boolean level = false;
	public int paintDirection = RIGHT;

	private int ioType = INPUT;

	public int getIoType() {
		return ioType;
	}

	public void setIoType(int ioType) {
		if (ioType == Pin.HIGHIMP && this.ioType != Pin.HIGHIMP) {
			parent.changedLevel(new LSLevelEvent(this, CircuitPart.LOW));
		} else if (ioType != Pin.HIGHIMP && this.ioType == Pin.HIGHIMP) {

		}
		this.ioType = ioType;
	}

	/**
	 * type can be HIGH, LOW, INVERTED or NORMAL
	 */
	int levelType = NORMAL;

	public Pin(int x, int y, Gate gate, int number) {
		super(x, y);
		this.parent = gate;
		this.number = number;
	}

	/**
	 * A connector can handle this event if there is one activePart and that part is
	 * a wire in other cases we can start a wire if this is an output
	 * 
	 */
	@Override
	public void mousePressed(LSMouseEvent e) {
		super.mousePressed(e);
	}

	/**
	 * draw pin label (inside gate frame)
	 * 
	 * @param g2
	 */
	private void drawLabel(Graphics2D g2) {
		int x = getX();
		int y = getY();

		g2.setFont(smallFont);
		if (text != null && text.length() > 0) {
			if (paintDirection == RIGHT) {
				if (POS_EDGE_TRIG.equals(text)) {
					Polygon tr = new Polygon();
					tr.addPoint(x + 1 + CONN_SIZE, y - 4);
					tr.addPoint(x + 1 + CONN_SIZE, y + 4);
					tr.addPoint(x + 1 + CONN_SIZE + 9, y);
					g2.draw(tr);
				} else {
					WidgetHelper.drawString(g2, text, x + CONN_SIZE + 3, y + 4, WidgetHelper.ALIGN_LEFT);
				}
			} else if (paintDirection == LEFT) {
				if (POS_EDGE_TRIG.equals(text)) {
					Polygon tr = new Polygon();
					tr.addPoint(x - 1 - CONN_SIZE, y - 4);
					tr.addPoint(x - 1 - CONN_SIZE, y + 4);
					tr.addPoint(x - 1 - CONN_SIZE - 9, y);
					g2.draw(tr);
				} else {
					WidgetHelper.drawString(g2, text, x - CONN_SIZE - 2, y + 4, WidgetHelper.ALIGN_RIGHT);
				}
			} else if (paintDirection == UP) {
				if (POS_EDGE_TRIG.equals(text)) {
					Polygon tr = new Polygon();
					tr.addPoint(x - 4, y - 1 - CONN_SIZE);
					tr.addPoint(x + 4, y - 1 - CONN_SIZE);
					tr.addPoint(x, y - 1 - CONN_SIZE - 8);
					g2.draw(tr);
				} else {
					WidgetHelper.drawString(g2, text, x, y - CONN_SIZE - 3, WidgetHelper.ALIGN_CENTER);
				}
			} else if (paintDirection == DOWN) {
				if (POS_EDGE_TRIG.equals(text)) {
					Polygon tr = new Polygon();
					tr.addPoint(x - 4, y + 1 + CONN_SIZE);
					tr.addPoint(x + 4, y + 1 + CONN_SIZE);
					tr.addPoint(x, y + 1 + CONN_SIZE + 8);
					g2.draw(tr);
				} else {
					WidgetHelper.drawString(g2, text, x, y + CONN_SIZE + 10, WidgetHelper.ALIGN_CENTER);
				}
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

		if (levelType == INVERTED) {
			int ovalSize = CONN_SIZE;
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
			int xp = x + 2;
			int yp = y - 2;
			if (paintDirection == LEFT)
				WidgetHelper.drawStringCentered(g2, "1", xp + 2, yp);
			else if (paintDirection == RIGHT)
				WidgetHelper.drawStringCentered(g2, "1", xp - 5, yp);
			else if (paintDirection == DOWN)
				WidgetHelper.drawStringCentered(g2, "1", xp - 2, yp - 6);
			else // UP
				WidgetHelper.drawStringCentered(g2, "1", xp - 2, yp + 6);
		} else if (levelType == LOW) {
			if (ioType == OUTPUT)
				throw new RuntimeException("OUTPUT may not be set LOW");
			int xp = x + 2;
			int yp = y - 2;
			if (paintDirection == LEFT)
				WidgetHelper.drawStringCentered(g2, "0", xp + 3, yp);
			else if (paintDirection == RIGHT)
				WidgetHelper.drawStringCentered(g2, "0", xp - 6, yp);
			else if (paintDirection == DOWN)
				WidgetHelper.drawStringCentered(g2, "0", xp - 2, yp - 6);
			else // UP
				WidgetHelper.drawStringCentered(g2, "0", xp - 2, yp + 6);
		}
		if (levelType == HIGH || levelType == LOW || levelType == NORMAL) {
			// normal
			g2.setStroke(new BasicStroke(1));
			if (paintDirection == LEFT || paintDirection == RIGHT) {
				if (paintDirection == LEFT) {
					offset = -CONN_SIZE - 1;
				} else {
					offset = -1;
				}
				if (ioType == HIGHIMP) {
					if (paintDirection == LEFT) {
						g2.setPaint(level ? Color.red : Color.black);
						g2.fillRect(x + offset + 1 + 4, y - 1, CONN_SIZE + 1 - 4, 3);
						g2.setColor(Color.black);
						g2.fillRect(x + offset + 1, y - 1, 2, 3);
					} else {
						g2.setPaint(level ? Color.red : Color.black);
						g2.fillRect(x + offset + 1, y - 1, 2, 3);
						g2.setColor(Color.black);
						g2.fillRect(x + offset + 1 + 4, y - 1, CONN_SIZE + 1 - 4, 3);
					}
				} else {
					g2.setPaint(getLevel() ? Color.red : Color.black);
					g2.fillRect(x + offset + 1, y - 1, CONN_SIZE + 1, 3);
				}
			} else {
				if (paintDirection == UP) {
					offset = -CONN_SIZE;
				}
				if (ioType == HIGHIMP) {
					if (paintDirection == UP) {
						g2.setPaint(level ? Color.red : Color.black);
						g2.fillRect(x - 1, y + offset + 4, 3, CONN_SIZE + 1 - 4);
						g2.setColor(Color.black);
						g2.fillRect(x - 1, y + offset, 3, 2);
					} else {
						g2.setPaint(level ? Color.red : Color.black);
						g2.fillRect(x - 1, y + offset, 3, 2);
						g2.setColor(Color.black);
						g2.fillRect(x - 1, y + offset + 4, 3, CONN_SIZE + 1 - 4);
					}
				} else {
					g2.setPaint(getLevel() ? Color.red : Color.black);
					g2.fillRect(x - 1, y + offset, 3, CONN_SIZE + 1);
				}
			}
		}
	}

	public void setLevelType(int levelType) {
		// TODO if we set a level type of type HIGH or LOW we have to remove the wire
		// completely
		this.levelType = levelType;
	}

	public boolean getLevel() {
		if (ioType == HIGHIMP)
			return false;
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
		String s = number + it + lt + "-" + (text == null ? "" : "\"" + text + "\"") + getX() + ":" + getY() + "@"
				+ parent.getId();
		s += " - " + (getLevel() ? "HIGH" : "LOW");
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
		return number + "@" + parent.getId();
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
			// if the pin is in high imp state, update level but don't push it
			if (this.ioType == Pin.HIGHIMP) {
				level = e.level;
			} else {
				// signal is from outside, propagate this to gate
				// call gate directly
				if (level != e.level || e.force) {
					level = e.level;
					parent.changedLevel(new LSLevelEvent(this, getLevel(), e.force));

					// and call all other wires which are connected to the pin
					fireChangedLevel(e);
				}
			}
		} else
			throw new RuntimeException("pins communicate with gates or wires only! source is " + e.source.getId()
					+ ", target is " + getId());

	}

	public boolean getInternalLevel() {
		return level;
	}

	public void disconnect() {
		for (int i = 0; i < getListeners().size(); i++) {
			LSLevelListener l = ((ArrayList<LSLevelListener>) getListeners()).get(i);
			if (l instanceof Wire) {
				Wire w = (Wire) l;
				w.disconnect(null);
				w = null;
				i--;
			}
		}
	}

}
