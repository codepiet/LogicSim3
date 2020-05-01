package logicsim;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
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

	public static final int ALIGN_CENTER = 1;
	public static final int ALIGN_LEFT = 2;
	public static final int ALIGN_RIGHT = 3;

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
	public CircuitPart parent;
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

	protected int getPropertyIntWithDefault(String string, int idefault) {
		String value = getProperty(string);
		if (value == null)
			return idefault;
		else
			return Integer.parseInt(value);
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
		if (h != null) {
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

	@Override
	public String toString() {
		return this.getId();
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
		x = x + dx;
		y = y + dy;
		checkXY(x, y);
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

	public void select() {
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
		this.addLevelListener(part);
		part.addLevelListener(this);
	}

	protected void fireChangedLevel(LSLevelEvent e) {
		// Log.getInstance().print("fireChangedLevel " + e);
		// the event can have a different source (not itself)
		// if so, just forward the event to the others except to the origin
		if (!this.equals(e.source)) {
			for (LSLevelListener l : getListeners()) {
				if (e.source != l) {
					LSLevelEvent evtL = new LSLevelEvent(this, e.level, e.force, l);
					Simulation.getInstance().putEvent(evtL);
					// l.changedLevel(evt);
				}
			}
		} else {
			for (LSLevelListener l : getListeners()) {
				LSLevelEvent evtL = new LSLevelEvent(this, e.level, e.force, l);
				Simulation.getInstance().putEvent(evtL);
				// l.changedLevel(e);
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

	public String toStringAll() {
		String s = "-----------------------------\n";
		s += toString();
		s += "PARENT : " + parent + "\n";
		s += "\n-- LISTENERS: \n";
		for (LSLevelListener l : getListeners()) {
			s += l.toString();
			s += " with parent " + ((CircuitPart) l).parent;
		}
		s += "-----------------------------\n";
		return s;
	}

	protected Rectangle textDimensions(Graphics2D g2, String text) {
		FontMetrics fm = g2.getFontMetrics();
		boolean overLine = false;
		if (text.charAt(0) == '/') {
			overLine = true;
			text = text.substring(1);
		}
		int stringWidth = fm.stringWidth(text);
		int stringHeight = fm.getHeight() + (overLine ? 2 : 0);
		return new Rectangle(0, 0, stringWidth, stringHeight);
	}

	protected void drawString(Graphics2D g2, String text, int x, int y, int mode) {
		int nx = x;
		int ny = y;
		if (text == null)
			return;
		Rectangle r = textDimensions(g2, text);
		boolean overLine = false;
		if (text.charAt(0) == '/') {
			overLine = true;
			text = text.substring(1);
		}
		if (mode == ALIGN_CENTER) {
			nx = x - r.width / 2;
			ny = y + r.height / 2;
			if (overLine) {
				g2.drawLine(nx, y - r.height / 2 + 2, nx + r.width, y - r.height / 2 + 2);
				g2.drawString(text, nx, ny - 4);
			} else {
				g2.drawString(text, nx, ny - 2);
			}
		} else if (mode == ALIGN_LEFT) {
			if (overLine) {
				g2.drawLine(nx, y - r.height + 2, nx + r.width, y - r.height + 2);
				g2.drawString(text, nx, ny - 2);
			} else {
				g2.drawString(text, nx, ny);
			}
		} else if (mode == ALIGN_RIGHT) {
			if (overLine) {
				g2.drawLine(nx - r.width, y - r.height + 3, nx, y - r.height + 3);
				g2.drawString(text, nx - r.width, ny - 2);
			} else {
				g2.drawString(text, nx - r.width, ny);
			}
		}
	}

	protected void clearListeners() {
		listeners.clear();
	}

	public boolean getLevel() {
		return false;
	}

}
