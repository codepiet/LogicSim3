package logicsim;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;

import javax.swing.JOptionPane;

public abstract class CircuitPart implements LSLevelListener {
	public static final int BOUNDING_SPACE = 6;
	public static final boolean HIGH = true;
	public static final boolean LOW = false;
	private Collection<LSLevelListener> listeners;
	private LSRepaintListener repListener;

	protected static String indent(String string, int indentation) {
		String s = "";
		for (int i = 0; i < indentation; i++)
			s += " ";
		return s + string.replaceAll("\n", "\n" + s);
	}

	protected boolean busted = false;

	public static int round(int num) {
		int x = num;
		int rest = x % 10;
		if (rest < 5)
			x = x / 10 * 10;
		else
			x = x / 10 * 10 + 10;
		return x;
	}

	private CircuitChangedListener changeListener = null;

	protected Point mousePos;

	/**
	 * if part is currently being edited
	 */
	protected boolean selected = false;

	private int x;

	private int y;

	public static final String TEXT = "text";

	public String TEXT_DEFAULT = "";

	public String text;

	public CircuitPart(int x, int y) {
		this.x = x;
		this.y = y;
		this.listeners = new ArrayList<LSLevelListener>();
	}

	protected Properties properties = new Properties();

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

	protected void loadProperties() {
		text = getPropertyWithDefault(TEXT, TEXT_DEFAULT);
	}

	public boolean hasPropertiesUI() {
		return true;
	}

	public boolean showPropertiesUI(Component frame) {
		String h = (String) JOptionPane.showInputDialog(frame, I18N.tr(Lang.TEXT), I18N.tr(Lang.PROPERTIES),
				JOptionPane.QUESTION_MESSAGE, null, null, text);
		if (h != null && h.length() > 0) {
			text = h;
			setProperty(TEXT, text);
		}
		return true;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
		loadProperties();
	}

	public void setProperty(String key, String value) {
		properties.setProperty(key, value);
		if (TEXT.equals(key))
			text = value;
	}

	protected void setPropertyInt(String key, int value) {
		setProperty(key, String.valueOf(value));
	}

	private void checkXY(int x2, int y2) {
		if (x2 % 10 != 0)
			throw new RuntimeException("only move by 10s! tried x=" + x2);
		if (y2 % 10 != 0)
			throw new RuntimeException("only move by 10s! tried y=" + y2);
	}

	public void addLevelListener(LSLevelListener l) {
		if (getListeners() == null)
			return;
		if (getListeners().contains(l))
			return;
		getListeners().add(l);
	}

	public void setRepaintListener(LSRepaintListener l) {
		repListener = l;
	}

	public void removeLevelListener(LSLevelListener l) {
		if (getListeners() != null)
			getListeners().remove(l);
	}

	public void clear() {
	}

	public void deselect() {
		selected = false;
	}

	public void draw(Graphics2D g2) {
		drawActiveFrame(g2);
	}

	protected void drawActiveFrame(Graphics2D g2) {
		if (selected) {
			Rectangle rect = getBoundingBox();

			int r = rect.x + rect.width;
			int b = rect.y + rect.height;
			g2.setStroke(new BasicStroke(2));
			g2.setColor(Color.blue);

			// oben links
			g2.drawLine(rect.x - BOUNDING_SPACE, rect.y - BOUNDING_SPACE, rect.x - BOUNDING_SPACE, rect.y);
			g2.drawLine(rect.x - BOUNDING_SPACE, rect.y - BOUNDING_SPACE, rect.x, rect.y - BOUNDING_SPACE);
			// unten links
			g2.drawLine(rect.x - BOUNDING_SPACE, b + BOUNDING_SPACE, rect.x - BOUNDING_SPACE, b);
			g2.drawLine(rect.x - BOUNDING_SPACE, b + BOUNDING_SPACE, rect.x, b + BOUNDING_SPACE);
			// oben rechts
			g2.drawLine(r + BOUNDING_SPACE, rect.y - BOUNDING_SPACE, r + BOUNDING_SPACE, rect.y);
			g2.drawLine(r + BOUNDING_SPACE, rect.y - BOUNDING_SPACE, r, rect.y - BOUNDING_SPACE);
			// unten rechts
			g2.drawLine(r + BOUNDING_SPACE, b + BOUNDING_SPACE, r + BOUNDING_SPACE, b);
			g2.drawLine(r + BOUNDING_SPACE, b + BOUNDING_SPACE, r, b + BOUNDING_SPACE);
		}
	}

	protected void drawBounds(Graphics2D g2) {
		int cd = 3;
		int co = cd / 2;
		Rectangle rect = getBoundingBox();
		g2.setPaint(Color.red);
		g2.fillOval(rect.x - co, rect.y - co, cd, cd);
		g2.fillOval(rect.x - co + rect.width, y - co, cd, cd);
		g2.fillOval(rect.x - co, rect.y - co + rect.height, cd, cd);
		g2.fillOval(rect.x - co + rect.width, y - co + rect.height, cd, cd);
	}

	public abstract Rectangle getBoundingBox();

	public String getId() {
		return getX() + ":" + getY();
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public boolean isSelected() {
		return selected;
	}

	/**
	 * if this part is dragged
	 * 
	 */

	public void loadLanguage() {
	}

	public void mouseDragged(MouseEvent e) {
		if (mousePos == null) {
			mousePos = new Point(e.getX(), e.getY());
		}
	}

	public void mousePressed(LSMouseEvent e) {
		if (Simulation.getInstance().isRunning())
			mousePressedSim(e);
		else {
			select();
			notifyRepaint();
		}

		if (e.isAltDown()) {
			this.showPropertiesUI(null);
		}
	}

	public void mousePressedSim(LSMouseEvent e) {
	}

	/**
	 * wird aufgerufen, wenn über dem Teil die Maus losgelassen wird
	 */
	public void mouseReleased(int mx, int my) {
		mousePos = null;
	}

	public void moveBy(int dx, int dy) {
		if (dx == 0 && dy == 0)
			return;
		checkXY(x + dx, y + dy);
		x = x + dx;
		y = y + dy;
	}

	public void moveTo(int x, int y) {
		checkXY(x, y);

		this.x = x;
		this.y = y;
	}

	protected void notifyAction(int action) {
		if (changeListener != null)
			changeListener.setAction(action);
	}

	protected void notifyChanged() {
		if (changeListener != null)
			changeListener.changedCircuit();
	}

	protected void notifyMessage(String msg) {
		if (changeListener != null)
			changeListener.changedStatusText(msg);
	}

	protected void notifyRepaint() {
		if (changeListener != null)
			changeListener.needsRepaint(this);
	}

	/**
	 * all Circuitparts can be resetted: maybe set back inputs or outputs and so on
	 */
	public void reset() {
		busted = false;
	}

	public final void select() {
		selected = true;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	@Override
	public void changedLevel(LSLevelEvent e) {
	}

	public void connect(CircuitPart part) {
	}

	protected void fireChangedLevel(LSLevelEvent e) {
		// Log.getInstance().print("fireChangedLevel " + e);
		// the event can have a different source (not itself)
		// if so, just forward the event to the others except to the origin
		if (e.source != this) {
			LSLevelEvent evt = new LSLevelEvent(this, e.level);
			for (LSLevelListener l : getListeners()) {
				if (e.source != l)
					l.changedLevel(evt);
			}
		} else {
			for (LSLevelListener l : getListeners()) {
				l.changedLevel(e);
			}
		}
	}

	protected void fireRepaint() {
		if (repListener != null)
			repListener.needsRepaint(this);
	}

	public boolean isConnected() {
		return getListeners().size() > 0;
	}

	public Collection<LSLevelListener> getListeners() {
		return listeners;
	}

}
