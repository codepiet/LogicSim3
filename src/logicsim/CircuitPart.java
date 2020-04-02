package logicsim;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

public abstract class CircuitPart {
	public static final int BOUNDING_SPACE = 6;

	/**
	 * if part is currently being edited
	 */
	protected boolean active = false;

	private int x;
	private int y;

	protected Point mousePos;

	private CircuitChangedListener changeListener = null;

	public String getId() {
		return getX() + ":" + getY();
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void clear() {
	}

	public final void activate() {
		active = true;
	}

	public final void deactivate() {
		active = false;
	}

	public CircuitPart(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public abstract Rectangle getBoundingBox();

	public void mousePressed(LSMouseEvent e) {
		// compute position in part relative to its origin
		mousePos = new Point(e.getX(), e.getY());
	}

	protected void drawActiveFrame(Graphics2D g2) {
		if (active) {
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

	public void draw(Graphics2D g2) {
		drawActiveFrame(g2);
	}

	public void moveTo(int x, int y) {
		checkXY(x, y);

		this.x = x;
		this.y = y;
	}

	private void checkXY(int x2, int y2) {
		if (x2 % 10 != 0)
			throw new RuntimeException("only move by 10s! tried x=" + x2);
		if (y2 % 10 != 0)
			throw new RuntimeException("only move by 10s! tried y=" + y2);
	}

	public void moveBy(int dx, int dy) {
		if (dx == 0 && dy == 0)
			return;
		checkXY(x + dx, y + dy);
		x = x + dx;
		y = y + dy;
	}

	protected static String indent(String string, int indentation) {
		String s = "";
		for (int i = 0; i < indentation; i++)
			s += " ";
		return s + string.replaceAll("\n", "\n" + s);
	}

	public void setChangeListener(CircuitChangedListener changeListener) {
		this.changeListener = changeListener;
	}

	protected void notifyChanged() {
		if (changeListener != null)
			changeListener.changedCircuit();
	}

	protected void notifyMessage(String msg) {
		if (changeListener != null)
			changeListener.changedStatusText(msg);
	}

	protected void notifyActive(CircuitPart activePart) {
		if (changeListener != null)
			changeListener.changedActivePart(activePart);
	}

	protected void notifyAction(int action) {
		if (changeListener != null)
			changeListener.setAction(action);
	}

	/**
	 * if this part is dragged
	 * 
	 */
	public void mouseDragged(MouseEvent e) {
		if (mousePos == null)
			mousePos = new Point(e.getX(), e.getY());
	}

	/**
	 * wird aufgerufen, wenn über dem Teil die Maus losgelassen wird
	 */
	public void mouseReleased(int mx, int my) {
		mousePos = null;
	}

	protected void notifyRepaint() {
		if (changeListener != null)
			changeListener.needsRepaint(this);
	}

	public static int round(int num) {
		int x = num;
		int rest = x % 10;
		if (rest < 5)
			x = x / 10 * 10;
		else
			x = x / 10 * 10 + 10;
		return x;
	}

	/**
	 * all Circuitparts can be resetted: maybe set back inputs or outputs and so on
	 */
	public void reset() {
	}

}
