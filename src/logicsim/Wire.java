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
import java.util.Iterator;
import java.util.Vector;

public class Wire extends CircuitPart implements Cloneable {
	static final long serialVersionUID = -7554728800898882892L;

	public static float SEL_WIDTH = 3f;

	public static float WIDTH = 1.0f;

	private static Color LOW_COLOR = Color.black;

	private static Color HIGH_COLOR = Color.red;

	private static float LOW_WIDTH = 1.0f;
	private static float HIGH_WIDTH = 1.0f;

	/**
	 * Pin/Wire/WirePoint from which this wire is originating
	 */
	private CircuitPart from;

	/**
	 * data structure to hold the wire points
	 */
	private Vector<WirePoint> points = new Vector<WirePoint>();

	private Point tempPoint = null;

	/**
	 * connector to which this wire is targeting
	 */
	private CircuitPart to;

	private boolean level;

	/**
	 * constructor specifying the origin and the end
	 * 
	 * @param fromPart
	 * @param toPart
	 */
	public Wire(CircuitPart fromPart, CircuitPart toPart) {
		this(0, 0);
		this.setFrom(fromPart);
		this.setTo(toPart);
		selected = true;
		loadProperties();
		checkFromTo();
	}

	private void checkFromTo() {
		if (getFrom() instanceof WirePoint)
			((WirePoint) getFrom()).show = true;
		if (getTo() instanceof WirePoint)
			((WirePoint) getTo()).show = true;
	}

	public Wire(int x, int y) {
		super(x, y);
		loadProperties();
	}

	@Override
	protected void loadProperties() {
		text = getPropertyWithDefault(TEXT, TEXT_DEFAULT);
	}

	public void addPoint(int x, int y) {
		WirePoint wp = new WirePoint(x, y);
		addPoint(wp);
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

	private Path2D convertPointsToPath() {
		Path2D path = new Path2D.Float();
		WirePoint first = getPointFrom();
		path.moveTo(first.getX(), first.getY());

		for (int i = 0; i < points.size(); i++) {
			WirePoint point = points.get(i);
			path.lineTo(point.getX(), point.getY());
		}

		if (getTo() != null) {
			path.lineTo(getTo().getX(), getTo().getY());
		} else if (tempPoint != null) {
			path.lineTo(tempPoint.x, tempPoint.y);
		}
		return path;
	}

	@Override
	public void clear() {
		setFrom(null);
		setTo(null);
		tempPoint = null;
		points.clear();
	}

	@Override
	public void draw(Graphics2D g2) {
		super.draw(g2);
		float width = 0;
		if (getLevel()) {
			g2.setColor(HIGH_COLOR);
			width = HIGH_WIDTH;
		} else {
			g2.setColor(LOW_COLOR);
			width = LOW_WIDTH;
		}

		if (selected) {
			g2.setStroke(new BasicStroke(SEL_WIDTH, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		} else {
			g2.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		}

		g2.draw(convertPointsToPath());

		// draw points
		if (points.size() > 0) {
			for (WirePoint point : points) {
				//point.show || 
				if (selected || point.isSelected() || point.getListeners().size() > 1) {
					point.draw(g2);
				}
			}
		}
		if (getTo() instanceof WirePoint)
			getTo().draw(g2);
		if (getFrom() instanceof WirePoint)
			getFrom().draw(g2);

		if (getTo() == null && tempPoint != null) {
			// add a small red circle
			g2.setPaint(Color.red);
			g2.drawOval(tempPoint.x - 1, tempPoint.y - 1, 3, 3);
		}

		g2.setColor(Color.black);

		if (points.size() > 0) {
			g2.drawString(text, (getFrom().getX() + points.get(0).getX()) / 2,
					(getFrom().getY() + points.get(0).getY()) / 2);
		} else {
			if (getFrom() != null && getTo() == null && tempPoint != null) {
				g2.drawString(text, (getFrom().getX() + tempPoint.x) / 2, (getFrom().getY() + tempPoint.y) / 2);
			} else if (getFrom() != null && getTo() != null) {
				g2.drawString(text, (getFrom().getX() + getTo().getX()) / 2, (getFrom().getY() + getTo().getY()) / 2);
			}
		}
	}

	public CircuitPart findPartAt(int x, int y) {
		int rx = round(x);
		int ry = round(y);

		if (getFrom() instanceof WirePoint)
			if (((WirePoint) getFrom()).isAt(rx, ry))
				return getFrom();

		if (getTo() instanceof WirePoint)
			if (((WirePoint) getTo()).isAt(rx, ry))
				return getTo();

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
		if (getTo() != null)
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
		if (getTo() != null) {
			return getPointTo();
		} else if (points.size() > 0) {
			return points.get(points.size() - 1);
		} else if (getFrom() != null) {
			return getPointFrom();
		}
		throw new RuntimeException("Wire is empty!");
	}

	public boolean getLevel() {
		return level;
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

		for (int i = 0; i < points.size(); i++) {
			WirePoint p = points.get(i);
			if (mx > p.getX() - 3 && mx < p.getX() + 3 && my > p.getY() - 3 && my < p.getY() + 3)
				return i;
		}
		return -1;
	}

	WirePoint getPointFrom() {
		WirePoint wp = new WirePoint(getFrom().getX(), getFrom().getY(), false);
		return wp;
	}

	private WirePoint getPointTo() {
		WirePoint wp = new WirePoint(getTo().getX(), getTo().getY(), false);
		return wp;
	}

	public void insertPointAfter(int n, int mx, int my) {
		WirePoint wp = new WirePoint(mx, my, false);
		wp.parent = this;
		points.insertElementAt(wp, n);
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
		return getTo() == null;
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
			int p = isAt(mx, my);
			if (p > -1) {
				insertPointAfter(p, round(mx), round(my));
				select();
				notifyChanged();
			}
			notifyMessage("");
			notifyAction(0);
		} else if (e.lsAction == LSPanel.ACTION_DELPOINT) {
			if (removePointAt(e.getX(), e.getY())) {
				select();
				fireRepaint();
			}
		} else {
			select();
			// call listener of fromGate
			getFrom().notifyRepaint();
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
		// if (getFrom() instanceof WirePoint) {
		// getFrom().moveBy(dx, dy);
		// }
		// if (getTo() instanceof WirePoint) {
		// getTo().moveBy(dx, dy);
		// }
	}

	/**
	 * remove the last point of the wire
	 * 
	 * @return points left in wire
	 */
	public int removeLastPoint() {
		// wire is connected to gate, remove the connection and return number of
		// remaining points
		if (getTo() != null) {
			getTo().removeLevelListener(this);
			setTo(null);
			// points + first point
			return points.size() + 1;
		} else if (points.size() == 0) {
			if (getFrom() == null)
				throw new RuntimeException("wire is completely empty, may not be");
			getFrom().removeLevelListener(this);
			setFrom(null);
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
		for (Iterator<WirePoint> iter = points.iterator(); iter.hasNext();) {
			WirePoint wp = iter.next();
			if (wp.isAt(x, y)) {
				iter.remove();
				return true;
			}
		}
		return false;
	}

	public void setNodeIsDrawn(int i) {
		WirePoint p = points.get(i);
		p.show = true;
	}

	public void setTempPoint(Point point) {
		this.tempPoint = point;
	}

	@Override
	public String toString() {
		String s = getId();
//		if (getListeners().size() > 0) {
//			s += "\n send updates to\n";
//			for (LSLevelListener l : getListeners())
//				s += indent(((CircuitPart) l).getId(), 3) + "\n";
//		}
//		s += "-----------------";
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
	 * disconnect wire from CircuitParts
	 * 
	 * if this method is called from a connector, the calling connector has to be
	 * given as parameter and must remove the wire from itself.
	 * 
	 * @param connector
	 */
	public void disconnect(Pin connector) {
		if (getTo() != null) {
			getTo().removeLevelListener(this);
			this.removeLevelListener(getTo());
			setTo(null);
		}
		if (getFrom() != null) {
			getFrom().removeLevelListener(this);
			this.removeLevelListener(getFrom());
			setFrom(null);
		}
	}

	@Override
	public void changedLevel(LSLevelEvent e) {
		// System.out.println(getId() + ": got event " + e);
		// a wire can get a level change from a pin or another wire
		if (level != e.level || e.force) {
			level = e.level;
			// forward to other listeners, event must not get back to the origin
			fireRepaint();
			LSLevelEvent evt = new LSLevelEvent(this, e.level, e.force);
			fireChangedLevel(e);
			for (WirePoint wp : points) {
				wp.changedLevel(evt);
			}
		}
	}

	@Override
	public String getId() {
		String s = "";
		s += (getFrom() != null ? getFrom().getId() : "");
		s += "-";
		s += (getTo() != null ? getTo().getId() : "");
		return s;
	}

	public void addPointFitting(int x, int y) {
		Vector<WirePoint> ps = getAllPoints();
		for (int i = 0; i < ps.size() - 1; i++) {
			// set current and next wirepoint
			WirePoint c = ps.get(i);
			WirePoint n = ps.get(i + 1);
			if (n.isAt(x, y)) {
				n.show = true;
				return;
			}
			Line2D l = new Line2D.Float((float) c.getX(), (float) c.getY(), (float) n.getX(), (float) n.getY());
			double dist = l.ptSegDist((double) x, (double) y);
			if (dist < 4.5f) {
				// so the point should be after "c"
				insertPointAfter(i, x, y);
				return;
			}
		}
	}

	@Override
	public void deselect() {
		super.deselect();
		if (getFrom() instanceof WirePoint)
			getFrom().deselect();

		if (getTo() instanceof WirePoint)
			getTo().deselect();

		for (WirePoint wp : points) {
			wp.deselect();
		}
	}

	public void finish() {
		setTempPoint(null);
	}

	public void addPoint(WirePoint wp) {
		// check if the point is not present
		int x = wp.getX();
		int y = wp.getY();
		if (getFrom() != null && getFrom().getX() == x && getFrom().getY() == y)
			return;
		if (getTo() != null && getTo().getX() == x && getTo().getY() == y)
			return;
		int number = getNodeIndexAt(x, y);
		if (number > -1) {
			// delete every point from this node on
			for (int i = points.size() - 1; i >= number; i--) {
				points.remove(i);
			}
		}
		wp.parent = this;
		// wp.connect(this);
		points.add(wp);
	}

	public Vector<WirePoint> getPoints() {
		return points;
	}

	public CircuitPart getFrom() {
		return from;
	}

	public void setFrom(CircuitPart from) {
		this.from = from;
		checkFromTo();
	}

	public CircuitPart getTo() {
		return to;
	}

	public void setTo(CircuitPart to) {
		this.to = to;
		checkFromTo();
	}

	@Override
	public void reset() {
		super.reset();
		if (from != null) {
			if (from instanceof Wire) {
				Wire w = (Wire) from;
				from.fireChangedLevel(new LSLevelEvent(from, w.getLevel()));
			} else if (from instanceof Pin) {
				Pin p = (Pin) from;
				from.fireChangedLevel(new LSLevelEvent(from, p.getLevel()));
			} else if (from instanceof WirePoint) {
				WirePoint wp = (WirePoint) from;
				if (wp.parent != null) {
					Wire w = (Wire) wp.parent;
					wp.parent.fireChangedLevel(new LSLevelEvent(wp.parent, w.getLevel()));
				}
			}
		}
	}

	public static void setColorMode() {
		String colmode = LSProperties.getInstance().getProperty(LSProperties.COLORMODE, LSProperties.COLORMODE_ON);
		if (LSProperties.COLORMODE_OFF.equals(colmode)) {
			HIGH_COLOR = Color.black;
			LOW_COLOR = Color.black;
			LOW_WIDTH = 1f;
			HIGH_WIDTH = 3f;
		} else {
			HIGH_COLOR = Color.red;
			LOW_COLOR = Color.black;
			LOW_WIDTH = 1f;
			HIGH_WIDTH = 1f;
		}

	}
}