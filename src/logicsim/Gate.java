package logicsim;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
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
	protected static final int CONN_SIZE = 7;

	public static final int HORIZONTAL = 0;

	static final long serialVersionUID = -6775454761569297690L;
	public static final int VERTICAL = 1;

	protected static void drawRotate(Graphics2D g2, double x, double y, int angle, String text) {
		g2.translate((float) x, (float) y);
		g2.rotate(Math.toRadians(angle));
		g2.drawString(text, 0, 0);
		g2.rotate(-Math.toRadians(angle));
		g2.translate(-(float) x, -(float) y);
	}

	protected int actionid = 0;
	protected Font bigFont = new Font(Font.SANS_SERIF, Font.PLAIN, 13);
	protected Vector<Connector> conns = new Vector<Connector>();

	protected boolean drawFrame = true;
	protected int height = 60;

	protected String label;

	protected Properties properties = new Properties();

	protected String type;

	protected boolean variableInputCountSupported = false;
	protected int width = 60;

	protected Color backgroundColor = Color.white;

	public String category;

	public Gate() {
		this(0, 0);
		active = true;
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

	private boolean connectorExists(int ioType, int number) {
		for (Connector c : conns) {
			if (c.ioType == ioType && c.number == number)
				return true;
		}
		return false;
	}

	/**
	 * disconnect all wires
	 */
	@Override
	public void clear() {
		for (Connector c : conns)
			if (c.isConnected())
				c.deleteWires();
	}

	public void draw(Graphics2D g2) {
		super.draw(g2);

		g2.setStroke(new BasicStroke(1));

		// drawBounds(g2);
		g2.setPaint(Color.black);

		if (drawFrame) {
			drawFrame(g2);
		}
		drawIO(g2);
	}

	protected void drawFrame(Graphics2D g2) {
		Rectangle2D border = new Rectangle2D.Double(getX() + CONN_SIZE, getY() + CONN_SIZE, width - 2 * CONN_SIZE,
				height - 2 * CONN_SIZE);
		g2.setPaint(backgroundColor);
		g2.fill(border);
		g2.setPaint(Color.black);
		g2.setStroke(new BasicStroke(1));
		g2.draw(border);
		drawLabel(g2, label, bigFont);
	}

	protected void drawIO(Graphics2D g2) {
		for (Connector c : conns)
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

	public Connector findConnector(int atX, int atY) {
		for (Connector c : conns) {
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
		Connector conn = findConnector(rx, ry);
		if (conn != null)
			return conn;

		// check connected outgoing wires
		for (Connector c : getOutputs()) {
			if (c.isConnected()) {
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

	public int getConnectorNumber(Connector conn) {
		for (Connector c : conns)
			if (conn == c)
				return c.number;
		return -1;
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

	public Vector<Connector> getConnectors() {
		return conns;
	}

	public int getHeight() {
		return height;
	}

	public Connector getInput(int num) {
		for (Connector c : conns) {
			if (c.isInput() && c.number == num)
				return c;
		}
		throw new RuntimeException("no input with number " + num);
	}

	/**
	 * gibt die Position des Eingangs n zurück
	 */
	public Point getInputPosition(int n) {
		Connector conn = getInput(n);
		return new Point(conn.getX(), conn.getY());
	}

	public Vector<Connector> getInputs() {
		Vector<Connector> cs = new Vector<Connector>();
		for (Connector c : conns) {
			if (c.isInput())
				cs.add(c);
		}
		return cs;
	}

	public boolean getInputLevel(int n) {
		Connector c = getInput(n);
		return c.getLevel();
	}

	public int[] getInputLevel() {
		int[] inputStates = new int[getNumInputs()];
		for (int i = 0; i < getNumInputs(); i++) {
			inputStates[i] = getInputLevel(i) ? 1 : 0;
		}
		return inputStates;
	}

	public Wire getInputWire(int number) {
		Connector c = getInput(number);
		if (c.isConnected())
			return c.wires.get(0);
		return null;
	}

	public int getNumInputs() {
		return getInputs().size();
	}

	public int getNumOutputs() {
		return getOutputs().size();
	}

	public Connector getOutput(int num) {
		for (Connector c : conns) {
			if (!c.isInput() && c.number == num)
				return c;
		}
		throw new RuntimeException("no output with number " + num);
	}

	/**
	 * gibt die Position des Ausgangs n zurück
	 */
	public Point getOutputPosition(int n) {
		Connector conn = getOutput(n);
		return new Point(conn.getX(), conn.getY());
	}

	public Vector<Connector> getOutputs() {
		Vector<Connector> cs = new Vector<Connector>();
		for (Connector c : conns) {
			if (!c.isInput())
				cs.add(c);
		}
		return cs;
	}

	public boolean getOutputLevel(int n) {
		return getOutput(n).getLevel();
	}

	public int[] getOutputLevel() {
		int[] outputStates = new int[getNumOutputs()];
		for (int i = 0; i < getNumOutputs(); i++) {
			outputStates[i] = getOutputLevel(i) ? 1 : 0;
		}
		return outputStates;
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
	 * wird aufgerufen, wenn auf das Gatter geklickt wird
	 */
	@Override
	public final void mousePressed(LSMouseEvent e) {
		super.mousePressed(e);
		notifyMessage(I18N.getString(type, "title"));

		if (Simulation.getInstance().isRunning())
			mousePressedUI(e);
		else {
			activate();
			notifyRepaint();
		}
	}

	public void mousePressedUI(LSMouseEvent e) {
	}

	@Override
	public void moveBy(int dx, int dy) {
		if (dx == 0 && dy == 0)
			return;
		super.moveBy(dx, dy);
		for (Connector conn : conns) {
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
		for (Connector c : conns)
			c.reset();
	}

	public void setInputWire(int i, Wire wire) {
		Connector c = getInput(i);
		c.addWire(wire);
	}

	public void setNumInputs(int total) {
		// 1. check if there is an input with number present, if so do nothing
		// 2. if not, create one
		for (int i = 0; i < total; i++) {
			if (!connectorExists(Connector.INPUT, i)) {
				Connector c = new Connector(getX(), getY() + getConnectorPosition(i, total, VERTICAL), this, i);
				c.paintDirection = Connector.RIGHT;
				c.ioType = Connector.INPUT;
				conns.add(c);
			} else {
				// move connector
				getInput(i).moveTo(getX(), getY() + getConnectorPosition(i, total, VERTICAL));
			}
		}
	}

	public void setNumOutputs(int n) {
		// 1. check if there is an input with number present, if so do nothing
		// 2. if not, create one
		for (int i = 0; i < n; i++) {
			if (!connectorExists(Connector.OUTPUT, i)) {
				Connector c = new Connector(getX() + getWidth(), getY() + getConnectorPosition(i, n, VERTICAL), this,
						i);
				c.paintDirection = Connector.LEFT;
				c.ioType = Connector.OUTPUT;
				conns.add(c);
			}
		}
	}

	public void setOutputLevel(int i, boolean b) {
		getOutput(i).setLevel(b);
	}

	public void setOutputWire(int i, Wire wire) {
		Connector c = getOutput(i);
		if (!c.wires.contains(wire))
			c.addWire(wire);
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
		for (Connector c : getInputs()) {
			c.getLevel();
		}
	}

	public boolean supportsVariableInputs() {
		return variableInputCountSupported;
	}

	@Override
	public String toString() {
		String s = getId() + " - I/O: " + Arrays.toString(getInputLevel()) + " / " + Arrays.toString(getOutputLevel());
		for (Connector c : conns) {
			s += "\n" + indent(c.toString(), 3);
		}
		return s;
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

	public void deactivateWires() {
		for (Connector conn : getOutputs()) {
			if (conn.isConnected()) {
				for (Wire w : conn.wires)
					w.deactivate();
			}
		}
	}

	@Override
	public String getId() {
		return type + "@" + getX() + ":" + getY();
	}

	public Vector<Wire> getWiresFromThis() {
		Vector<Wire> wires = new Vector<Wire>();
		for (Connector conn : getOutputs()) {
			if (conn.isConnected()) {
				for (Wire w : conn.wires)
					wires.add(w);
			}
		}
		return wires;
	}

}