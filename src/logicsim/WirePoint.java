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

	protected boolean show = false;

	public WirePoint(int x, int y) {
		super(x, y);
	}

	public WirePoint(int x, int y, boolean draw) {
		this(x, y);
		this.show = draw;
	}

	@Override
	public String toString() {
		return "(" + getX() + "," + getY() + "-" + (show ? "w" : "f") + ")";
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
		notifyMessage("WIREPOINT_CLICKED");
		// Auf Punkt eines Wires geklickt ?
		if (!e.isShiftDown()) {
			select();
		} else {
			select();
			// Wire newWire = ((Wire) currentPart).clone();
			// newWire.activate();
		}
		notifyRepaint();
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
