package logicsim;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

/**
 * WirePoint substructure class for Wire Objects
 * 
 * taken from https://argonrain.wordpress.com/2009/10/27/000/
 * 
 * @author Peter Gabriel
 * @version 1.0
 */
public class WirePoint extends CircuitPart {

	public static final int POINT_SIZE = 7;

	protected boolean active = false;

	protected boolean forceDraw = false;

	public WirePoint(int x, int y) {
		super(x, y);
	}

	public WirePoint(int x, int y, boolean draw) {
		this(x, y);
		this.forceDraw = draw;
	}

	@Override
	public String toString() {
		return "(" + getX() + "," + getY() + "-" + (active ? "w" : "f") + ")";
	}

	@Override
	public Rectangle getBoundingBox() {
		int c = POINT_SIZE / 2;
		return new Rectangle(getX() - c, getY() - c, POINT_SIZE, POINT_SIZE);
	}

	@Override
	public void draw(Graphics2D g2) {
		super.draw(g2);
		g2.fill(getBoundingBox());
	}

	public boolean isAt(int x, int y) {
		if (x > getX() - 4 && x < getX() + 4 && y > getY() - 4 && y < getY() + 4)
			return true;
		return false;
	}

	@Override
	public void mousePressed(LSMouseEvent e) {
		super.mousePressed(e);
		// Auf Punkt eines Wires geklickt ?
		if (!e.isShiftDown()) {
			Log.getInstance().print("auf wirepunkt geklickt - ohne shift");
			select();
			return;
		}

		// mit SHIFT auf Punkt eines Wires geklickt ?
		if (e.isShiftDown()) {
			Log.getInstance().print("auf wirepunkt geklickt - mit shift");
			forceDraw = true;
			// Wire newWire = ((Wire) currentPart).clone();
			// newWire.activate();
			return;
		}
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
}
