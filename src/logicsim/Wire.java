package logicsim;

/**
 * Wire represention
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.util.Vector;

public class Wire extends CircuitPart implements Cloneable {
	static final long serialVersionUID = -7554728800898882892L;

	/**
	 * connector from which this wire is originating
	 */
	public Pin fromConn;

	/**
	 * data structure to hold the wire points
	 */
	Vector<WirePoint> points = new Vector<WirePoint>();

	private Point tempPoint = null;

	/**
	 * connector to which this wire is targeting
	 */
	public Pin toConn;

	/**
	 * constructor specifying the gates and connector numbers
	 * 
	 * @param g
	 * @param fromOutput
	 */
	public Wire(Pin fromConn, Pin toConn) {
		this(0, 0);
		this.fromConn = fromConn;
		this.toConn = toConn;
		active = true;
	}

	public Wire(int x, int y) {
		super(x, y);
	}

	public void addPoint(int x, int y) {
		points.add(new WirePoint(x, y, false));
	}

	@SuppressWarnings("unchecked")
	public Object clone() {
		Wire clone = null;
		try {
			clone = (Wire) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
		// Kopie von poly & nodes anlegen, Gate bleibt die selbe Referenz wie beim
		// Original
		clone.points = (Vector<WirePoint>) points.clone();
		return clone;
	}

	public void connect(Pin conn) {
		if (conn.isInput()) {
			if (toConn != null)
				throw new RuntimeException("wire is already connected - " + toConn.getId());
			toConn = conn;
			toConn.addWire(this);
		} else {
			if (fromConn != null)
				throw new RuntimeException("wire is already connected - " + fromConn.getId());
			fromConn = conn;
			fromConn.addWire(this);
		}
	}

	private Path2D convertPointsToPath() {
		Path2D path = new Path2D.Float();
		WirePoint first = getPointFrom();
		path.moveTo(first.getX(), first.getY());

		for (int i = 0; i < points.size(); i++) {
			WirePoint point = points.get(i);
			path.lineTo(point.getX(), point.getY());
		}

		if (toConn != null) {
			path.lineTo(toConn.getX(), toConn.getY());
		} else if (tempPoint != null) {
			path.lineTo(tempPoint.x, tempPoint.y);
		}
		return path;
	}

	@Override
	public void clear() {
		fromConn = null;
		toConn = null;
		tempPoint = null;
		points.clear();
	}

	@Override
	public void draw(Graphics2D g2) {
		super.draw(g2);
		if (getLevel()) {
			g2.setColor(Color.red);
		} else {
			g2.setColor(Color.black);
		}

		if (active) {
			g2.setStroke(new BasicStroke(3));
		} else {
			g2.setStroke(new BasicStroke(2));
		}

		g2.draw(convertPointsToPath());

		// draw points
		if (points.size() > 0) {
			for (WirePoint point : points) {
				if (active || point.active || point.forceDraw) {
					point.draw(g2);
				}
			}
		}

		if (toConn == null && tempPoint != null) {
			// add a small red circle
			g2.setPaint(Color.red);
			g2.drawOval(tempPoint.x - 1, tempPoint.y - 1, 3, 3);
		}

		g2.setColor(Color.black);
	}

	public CircuitPart findPartAt(int x, int y) {
		int rx = round(x);
		int ry = round(y);

		Vector<WirePoint> ps = getAllPoints();
		for (int i = 0; i < ps.size() - 1; i++) {
			// set current and next wirepoint
			WirePoint c = ps.get(i);
			WirePoint n = ps.get(i + 1);
			if (n.isAt(rx, ry))
				return n;
			Line2D l = new Line2D.Float((float) c.getX(), (float) c.getY(), (float) n.getX(), (float) n.getY());
			double dist = l.ptSegDist((double) x, (double) y);
			if (dist < 4.5f)
				return this;
		}
		return null;
	}

	private Vector<WirePoint> getAllPoints() {
		Vector<WirePoint> ps = new Vector<WirePoint>();
		ps.add(getPointFrom());
		ps.addAll(points);
		if (toConn != null)
			ps.add(getPointTo());
		return ps;
	}

	@Override
	public Rectangle getBoundingBox() {
		Path2D path = convertPointsToPath();
		Rectangle rect = path.getBounds();
		return rect;
	}

	WirePoint getLastPoint() {
		if (toConn != null) {
			return new WirePoint(toConn.getX(), toConn.getY(), false);
		} else if (points.size() > 0) {
			return points.get(points.size() - 1);
		} else if (fromConn != null) {
			return new WirePoint(fromConn.getX(), fromConn.getY(), false);
		}
		throw new RuntimeException("Wire is empty!");
	}

	public boolean getLevel() {
		if (fromConn != null)
			return fromConn.getLevel();
		else
			return false;
	}

	/**
	 * checks if given point is near polygon node, except first and last
	 * 
	 * @param mx
	 * @param my
	 * @return -1 if no point is near to given position, else number of node
	 */
	public int getNodeIndexAt(int mx, int my) {
		if (points.size() == 0)
			return -1;

		for (int i = 1; i < points.size() - 1; i++) {
			WirePoint p = points.get(i);
			if (mx > p.getX() - 3 && mx < p.getX() + 3 && my > p.getY() - 3 && my < p.getY() + 3)
				return i;
		}
		return -1;
	}

	public WirePoint getPointAt(int x, int y) {
		if (points.size() == 0)
			return null;
		for (WirePoint point : points) {
			if (point.isAt(x, y))
				return point;
		}
		return null;
	}

	WirePoint getPointFrom() {
		return new WirePoint(fromConn.getX(), fromConn.getY(), false);
	}

	private WirePoint getPointTo() {
		return new WirePoint(toConn.getX(), toConn.getY(), false);
	}

	public void insertPointAfter(int n, int mx, int my) {
		points.insertElementAt(new WirePoint(mx, my, false), n);
	}

	public void insertPointAfterStart(int x, int y) {
		WirePoint wp = new WirePoint(x, y, false);
		points.insertElementAt(wp, 0);
	}

	/**
	 * check if the wire is near given coordinates
	 * 
	 * if the distance between the clicked point and the wire is small enough the
	 * point number is returned
	 * 
	 * @param x
	 * @param y
	 * @return the number of the point from which the line segment is starting
	 */
	public int isAt(int x, int y) {
		Vector<WirePoint> ps = getAllPoints();
		for (int i = 0; i < ps.size() - 1; i++) {
			// set current and next wirepoint
			WirePoint c = ps.get(i);
			WirePoint n = ps.get(i + 1);
			Line2D l = new Line2D.Float((float) c.getX(), (float) c.getY(), (float) n.getX(), (float) n.getY());
			if (l.ptSegDist((double) x, (double) y) < 4.0f)
				return i;
		}
		return -1;
	}

	public boolean isNotFinished() {
		return toConn == null;
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

	@Override
	public void mousePressed(LSMouseEvent e) {
		super.mousePressed(e);

		if (Simulation.getInstance().isRunning())
			return;

		int mx = e.getX();
		int my = e.getY();

		if (e.lsAction == LSPanel.ACTION_ADDPOINT) {
			Log.getInstance().print("adding point");
			int p = isAt(mx, my);
			if (p > -1) {
				insertPointAfter(p, round(mx), round(my));
				activate();
				notifyChanged();
			}
			notifyMessage("");
			notifyAction(0);
		} else if (e.lsAction == LSPanel.ACTION_DELPOINT) {
			Log.getInstance().print("DELPOINT on wire?");
			if (removePointAt(e.getX(), e.getY())) {
				activate();
				notifyChanged();
			}
		} else {
			activate();
			// call listener of fromGate
			fromConn.notifyRepaint();
		}
//		// clicked CTRL on a Wire -> insert node
//		if (e.isControlDown()) {
//			int pointNumberOfSegment = w.isAt(x, y);
//
//			if (pointNumberOfSegment > -1) {
//				if (pointNumberOfSegment == 0)
//					w.insertPointAfterStart(x, y);
//				else
//					w.insertPointAfter(pointNumberOfSegment, x, y);
//				currentPart = w;
//				w.activate();
//				repaint();
//				notifyChangeListener("");
//				notifyChangeListener();
//				return true;
//			}
//		}
	}

	@Override
	public void moveBy(int dx, int dy) {
		// move wirepoints
		for (WirePoint wp : points) {
			wp.moveBy(dx, dy);
		}
	}

	/**
	 * remove the last point of the wire
	 * 
	 * @return points left in wire
	 */
	public int removeLastPoint() {
		// wire is connected to gate, remove the connection and return number of
		// remaining points
		if (toConn != null) {
			toConn.delWire(this);
			toConn = null;
			// points + first point
			return points.size() + 1;
		} else if (points.size() == 0) {
			if (fromConn == null)
				throw new RuntimeException("wire is completely empty, may not be");
			fromConn.delWire(this);
			fromConn = null;
			// wire can be released
			return 0;
		} else {
			points.remove(points.size() - 1);
			return points.size() + 1;
		}
	}

	public WirePoint removePoint(int n) {
		if (points.size() == 0)
			return null;
		return points.remove(n);
	}

	public boolean removePointAt(int x, int y) {
		WirePoint wp = getPointAt(x, y);
		if (wp != null) {
			return points.remove(wp);
		}
		return false;
	}

	public void setNodeIsDrawn(int i) {
		WirePoint p = points.get(i);
		p.forceDraw = true;
	}

	public void setPointAt(int n, int mx, int my) {
		points.set(n, new WirePoint(mx, my, false));
	}

	public void setTempPoint(Point point) {
		this.tempPoint = point;
	}

	@Override
	public String toString() {
		String s = "";
		s += (fromConn != null ? fromConn.getId() : "-");
		s += " <- ";
		for (int i = 0; i < points.size(); i++) {
			WirePoint p = points.get(i);
			s += p.toString();
		}
		s += " -> ";
		s += (toConn != null ? toConn.getId() : "-");
		return s;
	}

	/**
	 * wenn an (mx,my) ein Punkt des Wires liegt, wird dieser als Node markiert
	 */
	public boolean trySetNode(int mx, int my) {
		int p = getNodeIndexAt(mx, my);
		if (p > 0) {
			setNodeIsDrawn(p);
			return true;
		} else
			return false;
	}

	/**
	 * disconnect wire from connectors
	 * 
	 * if this method is called from a connector, the calling connector has to be
	 * given as parameter and must remove the wire from itself.
	 * 
	 * @param connector
	 */
	public void disconnect(Pin connector) {
		if (connector == fromConn) {
			toConn.wires.remove(this);
		} else if (connector == toConn) {
			fromConn.wires.remove(this);
		} else if (connector == null) {
			toConn.wires.remove(this);
			fromConn.wires.remove(this);
		}
		this.clear();
	}

}