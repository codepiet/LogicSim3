package logicsim;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Properties;
import java.util.Vector;

/**
 * Base class for all gate components
 *
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class Gate extends CircuitPart {

	public static final int BOTH_AXES = 3;

	protected static final int CONN_SIZE = 7;

	public static final int HORIZONTAL = 0;
	public static final int NORMAL = 0;

	static final long serialVersionUID = -6775454761569297690L;
	public static final int VERTICAL = 1;
	public static final int XAXIS = 1;
	public static final int YAXIS = 2;

	protected static void drawRotate(Graphics2D g2, double x, double y, int angle, String text) {
		g2.translate((float) x, (float) y);
		g2.rotate(Math.toRadians(angle));
		g2.drawString(text, 0, 0);
		g2.rotate(-Math.toRadians(angle));
		g2.translate(-(float) x, -(float) y);
	}

	protected int actionid = 0;
	protected Color backgroundColor = Color.white;

	protected Font bigFont = new Font(Font.SANS_SERIF, Font.PLAIN, 13);

	public String category;

	protected int height = 60;

	protected String label;

	/**
	 * mirroring of part.
	 * 
	 * 0 is normal. 1 is mirrored in x-axis. 2 is mirrored in y-axis. 3 means
	 * mirroring in both axes
	 */
	public int mirror = 0;
	protected Vector<Pin> pins = new Vector<Pin>();

	protected Properties properties = new Properties();

	/**
	 * rotate in 90 degree steps clockwise (0-3).
	 * 
	 * so 0 is normal orientation, 1 is 90 degrees clockwise, 2 is 180, 3 is 270
	 * degrees clockwise
	 */
	public int rotate90 = 0;

	protected String type;

	protected boolean variableInputCountSupported = false;

	protected int width = 60;

	protected double xc;

	protected double yc;

	public Gate() {
		this(0, 0);
		selected = true;
	}

	public Gate(int x, int y) {
		super(x, y);
	}

	public Gate(String category) {
		this(0, 0);
		this.category = category;
	}

	public Gate(String type, int actionid) {
		this(0, 0);
		this.type = type;
		this.actionid = actionid;
	}

	public void addConnector(Pin conn) {
		for (Pin c : pins)
			if (c.number == conn.number)
				throw new RuntimeException("Connector number " + c.number + " is already there in gate " + type);
		// check if number is present
		pins.add(conn);
	}

	/**
	 * disconnect all wires
	 */
	@Override
	public void clear() {
		for (Pin c : pins)
			if (c.isConnected())
				c.deleteWires();
	}

	public void createDynamicInputs(int total) {
		int numinputs = getInputs().size();
		// get max number
		int num = -1;
		for (Pin c : pins)
			if (c.number > num)
				num = c.number;

		// add new connectors - total times
		for (int i = numinputs; i < total; i++) {
			num++;
			int pos = getX();
			int ioType = Pin.INPUT;
			Pin c = new Pin(pos, 0, this, num);
			c.paintDirection = ioType == Pin.INPUT ? Pin.RIGHT : Pin.LEFT;
			c.ioType = ioType;
			pins.add(c);
		}

		// reposition all inputs
		num = 0;
		for (Pin p : getInputs()) {
			p.setY(getY() + getConnectorPosition(num, total, VERTICAL));
			num++;
		}

	}

	public void createInputs(int n) {
		createPins(Pin.INPUT, n);
	}

	public void createOutputs(int n) {
		createPins(Pin.OUTPUT, n);
	}

	public void createPins(int ioType, int total) {
		// get max number
		int num = -1;
		for (Pin c : pins)
			if (c.number > num)
				num = c.number;
		// add new connectors - total times
		for (int i = 0; i < total; i++) {
			num++;
			int pos = getX();
			if (ioType == Pin.OUTPUT)
				pos += width;
			Pin c = new Pin(pos, getY() + getConnectorPosition(i, total, VERTICAL), this, num);
			c.paintDirection = ioType == Pin.INPUT ? Pin.RIGHT : Pin.LEFT;
			c.ioType = ioType;
			pins.add(c);
		}
	}

	public void draw(Graphics2D g2) {
		super.draw(g2);
		AffineTransform old = null;
		xc = getX() + width / 2;
		yc = getY() + height / 2;
		if (rotate90 != 0) {
			old = g2.getTransform();
			g2.rotate(Math.toRadians(rotate90 * 90), xc, yc);
		}
		g2.setStroke(new BasicStroke(1));
		// drawBounds(g2);
		g2.setPaint(Color.black);
		drawFrame(g2);
		if (rotate90 != 0) {
			g2.setTransform(old);
		}
		drawIO(g2);
	}

	protected void drawFrame(Graphics2D g2) {
		Rectangle2D border = new Rectangle2D.Double(getX() + CONN_SIZE - 1, getY() + CONN_SIZE - 1,
				(width + 2) - 2 * CONN_SIZE, (height + 2) - 2 * CONN_SIZE);
		g2.setPaint(busted ? Color.red : backgroundColor);
		g2.fill(border);
		g2.setPaint(Color.black);
		g2.setStroke(new BasicStroke(1));
		g2.draw(border);
		drawLabel(g2, label, bigFont);
	}

	protected void drawIO(Graphics2D g2) {
		for (Pin c : pins)
			c.draw(g2);
	}

	protected void drawLabel(Graphics2D g2, String lbl, Font font) {
		if (lbl != null) {
			g2.setFont(font);
			int sw = g2.getFontMetrics().stringWidth(lbl);
			g2.drawString(lbl, getX() + getWidth() / 2 - sw / 2,
					getY() + getHeight() / 2 + (g2.getFont().getSize() / 2) - 2);
		}
	}

	protected void drawWires(Graphics2D g2) {
		for (Pin c : pins)
			c.drawWires(g2);
	}

	public Pin findConnector(int atX, int atY) {
		for (Pin c : pins) {
			if (c.isAt(atX, atY)) {
				return c;
			}
		}
		return null;
	}

	public CircuitPart findPartAt(int x, int y) {
		int rx = round(x);
		int ry = round(y);

		// if inside frame, it could only be a gate
		if (insideFrame(x, y))
			return this;

		// check connectors
		Pin conn = findConnector(rx, ry);
		if (conn != null)
			return conn;

		// check connected outgoing wires
		for (Pin c : pins) {
			if (!c.isInput() && c.isConnected()) {
				for (Wire wire : c.wires) {
					CircuitPart part = wire.findPartAt(x, y);
					if (part != null)
						return part;
				}
			}
		}
		return null;
	}

	@Override
	public Rectangle getBoundingBox() {
		return new Rectangle(getX(), getY(), getWidth(), getHeight());
	}

	protected int getConnectorPosition(int number, int total, int direction) {
		int cp;
		if (direction == Gate.VERTICAL)
			cp = getHeight() / 2;
		else
			cp = getWidth() / 2;

		if (total == 0)
			return -1;
		if (total == 1) {
			if (cp % 10 != 0)
				cp += 5;
			return cp;
		}
		if (total == 2)
			switch (number) {
			case 0:
				return 10;
			case 1:
				return height - 10;
			}
		if (total == 3)
			switch (number) {
			case 0:
				return 10;
			case 1:
				if (cp % 10 != 0)
					cp += 5;
				return cp;
			case 2:
				return height - 10;
			}
		if (total == 4)
			switch (number) {
			case 0:
				return 10;
			case 1:
				return 20;
			case 2:
				return height - 20;
			case 3:
				return height - 10;
			}
		if (total == 5)
			switch (number) {
			case 0:
				return 10;
			case 1:
				return 20;
			case 2:
				if (cp % 10 != 0)
					cp += 5;
				return cp;
			case 3:
				return height - 20;
			case 4:
				return height - 10;
			}
		return 10 + number * 10;
	}

	public int getHeight() {
		return height;
	}

	@Override
	public String getId() {
		return type + "@" + getX() + ":" + getY();
	}

	public Vector<Pin> getInputs() {
		Vector<Pin> cs = new Vector<Pin>();
		for (Pin c : pins) {
			if (c.isInput())
				cs.add(c);
		}
		return cs;
	}

	public int getNumInputs() {
		return getInputs().size();
	}

	public int getNumOutputs() {
		return getOutputs().size();
	}

	public Vector<Pin> getOutputs() {
		Vector<Pin> cs = new Vector<Pin>();
		for (Pin c : pins) {
			if (c.isOutput())
				cs.add(c);
		}
		return cs;
	}

	public Pin getPin(int number) {
		for (Pin c : pins)
			if (c.number == number)
				return c;
		return null;
	}

	/**
	 * gibt die Position des Ausgangs n zurück
	 */
	@Deprecated
	public Point getPinPosition(int n) {
		Pin conn = getPin(n);
		return new Point(conn.getX(), conn.getY());
	}

	public Vector<Pin> getPins() {
		return pins;
	}

	public Properties getProperties() {
		return properties;
	}

	protected String getProperty(String string) {
		return properties.getProperty(string);
	}

	protected int getPropertyInt(String string) {
		return Integer.parseInt(getProperty(string));
	}

	protected String getPropertyWithDefault(String key, String sdefault) {
		String s = getProperty(key);
		if (s == null)
			return sdefault;
		return s;
	}

	public int getWidth() {
		return width;
	}

	public Vector<Wire> getWiresFromThis() {
		Vector<Wire> wires = new Vector<Wire>();
		for (Pin conn : getOutputs()) {
			if (conn.isConnected()) {
				for (Wire w : conn.wires)
					wires.add(w);
			}
		}
		return wires;
	}

	/**
	 * True zurückgeben, wenn Gatter Einstellungen hat. Wird benutzt, damit bei
	 * Gattern ohne Einstellungen der Punkt "Properties" im Context-Menü
	 * ausgeblendet wird
	 */
	public boolean hasPropertiesUI() {
		return false;
	}

	/**
	 * true, wenn Koordinaten mx,my innerhalb der gate Area liegen
	 */
	public final boolean insideArea(int mx, int my) {
		// setup tolerance
		int t = 1;
		return new Rectangle(getX() - t, getY() - t, getWidth() + 2 * t + 1, getHeight() + 2 * t + 1).contains(mx, my);
	}

	/**
	 * true, wenn Koordinaten mx,my innerhalb des Gatters liegen
	 */
	public boolean insideFrame(int mx, int my) {
		return new Rectangle(getX() + CONN_SIZE, getY() + CONN_SIZE, width - 2 * CONN_SIZE, height - 2 * CONN_SIZE)
				.contains(mx, my);
	}

	/**
	 * implement this in gates if some settings should be applied after setting
	 * properties
	 */
	protected void loadProperties() {
	}

	/**
	 * mirror the gate first in x-axis, then in y-axis, then both axes, then normal
	 * so 0 is normal, 1 x, 2 y, 3 both
	 */
	public void mirror() {
		mirror = (mirror + 1) % 4;
		for (Pin p : pins) {
			// now check the coordinates and if the current mirror-state is normal, x, y, or
			// both
			if (p.getX() == this.getX()) {
				p.setX(p.getX() + width);
				p.setDirection(Pin.LEFT);
			} else if (p.getX() == this.getX() + width) {
				p.setX(p.getX() - width);
				p.setDirection(Pin.RIGHT);
			}
			if (mirror == NORMAL || mirror == YAXIS) {
				if (p.getY() == this.getY()) {
					p.setY(p.getY() + height);
					p.setDirection(Pin.UP);
				} else if (p.getY() == this.getY() + height) {
					p.setY(p.getY() - height);
					p.setDirection(Pin.DOWN);
				}
			}
		}
	}

	public void loadLanguage() {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		super.mouseDragged(e);

		int mx = e.getX();
		int my = e.getY();
		int dx = round(mx - mousePos.x);
		int dy = round(my - mousePos.y);

		if (dx != 0 || dy != 0) {
			if (e.isShiftDown()) {
				if (dx < dy)
					dx = 0;
				else
					dy = 0;
			}
			mousePos.x = mousePos.x + dx;
			mousePos.y = mousePos.y + dy;
			moveBy(dx, dy);
		}
	}

	/**
	 * wird aufgerufen, wenn auf das Gatter geklickt wird
	 */
	@Override
	public final void mousePressed(LSMouseEvent e) {
		super.mousePressed(e);
		notifyMessage(I18N.getString(type, I18N.TITLE));

		if (Simulation.getInstance().isRunning())
			mousePressedSim(e);
		else {
			select();
			notifyRepaint();
		}
	}

	public void mousePressedSim(LSMouseEvent e) {
	}

	@Override
	public void moveBy(int dx, int dy) {
		if (dx == 0 && dy == 0)
			return;
		super.moveBy(dx, dy);
		for (Pin conn : pins) {
			conn.moveBy(dx, dy);
		}
	}

	@Override
	public void moveTo(int x, int y) {
		int dx = x - getX();
		int dy = y - getY();
		moveBy(dx, dy);
	}

	protected int pX(int percent) {
		return percent * width / 100;
	}

	protected int pY(int percent) {
		return percent * height / 100;
	}

	/**
	 * Reset Gate: default: alle Ausgaenge auf LOW manche Gates ueberschreiben diese
	 * Funktion und setzen bestimmte Ausgaenge auf HIGH
	 */
	@Override
	public void reset() {
		for (Pin c : pins)
			c.reset();
	}

	public void rotate() {
		if (height != width) {
			rotate90 += 2;
			if (rotate90 > 3)
				rotate90 = 0;

			for (Pin c : pins) {
				if (c.paintDirection == Pin.RIGHT) {
					c.paintDirection = Pin.LEFT;
					c.setY(2 * getY() + height - c.getY());
					c.setX(getX() + width);
				} else if (c.paintDirection == Pin.DOWN) {
					c.paintDirection = Pin.UP;
					c.setY(getY() + height);
					c.setX(2 * getX() + width - c.getX());
				} else if (c.paintDirection == Pin.LEFT) {
					c.paintDirection = Pin.RIGHT;
					c.setX(getX());
					c.setY(2 * getY() + height - c.getY());
				} else {
					c.paintDirection = Pin.DOWN;
					c.setY(getY());
					c.setX(2 * getX() + width - c.getX());
				}
			}
		} else {
			rotate90++;
			if (rotate90 > 3)
				rotate90 = 0;

			for (Pin c : pins) {
				if (c.paintDirection == Pin.RIGHT) {
					c.paintDirection = Pin.DOWN;
					c.setX(getX() + width - (c.getY() - getY()));
					c.setY(getY());
				} else if (c.paintDirection == Pin.DOWN) {
					c.paintDirection = Pin.LEFT;
					c.setY(getY() + (c.getX() - getX()));
					c.setX(getX() + width);
				} else if (c.paintDirection == Pin.LEFT) {
					c.paintDirection = Pin.UP;
					c.setX(getX() + width - (c.getY() - getY()));
					c.setY(getY() + height);
				} else {
					c.paintDirection = Pin.RIGHT;
					c.setY(getY() + c.getX() - getX());
					c.setX(getX());
				}
			}
		}
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
		loadProperties();
	}

	protected void setProperty(String key, String value) {
		properties.setProperty(key, value);
	}

	protected void setPropertyInt(String key, int value) {
		setProperty(key, String.valueOf(value));
	}

	/**
	 * Über Context-Menü aufgerufen
	 */
	public boolean showPropertiesUI(Component frame) {
		return false;
	}

	public void simulate() {
		for (Pin c : getInputs()) {
			c.getLevel();
		}
	}

	public boolean supportsVariableInputs() {
		return variableInputCountSupported;
	}

	@Override
	public String toString() {
		String s = getId();
		for (Pin c : pins) {
			s += "\n" + indent(c.toString(), 3);
		}
		return s;
	}

	@Override
	public void changedLevel(LSLevelEvent e) {
		// source has to be a Pin
		if (!(e.source instanceof Pin))
			throw new RuntimeException(
					"gates communicate with pins only! source is " + e.source.getId() + ", target is " + getId());
		Pin p = (Pin) e.source;
		if (p.isOutput() && e.level == HIGH) {
			// if the level change comes from an output, this will crash the part
			busted = true;
		}
	}

	/**
	 * gates can implement this method to let something happen when pressing the
	 * action key (SPACE?)
	 */
	public void interact() {
	}

}