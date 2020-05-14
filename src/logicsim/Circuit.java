package logicsim;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.Vector;

/**
 * all parts that belong to the circuit
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */

public class Circuit implements LSRepaintListener {
	static final long serialVersionUID = 3458986578856078326L;

	Vector<CircuitPart> parts;

	private LSRepaintListener repaintListener;

	public Circuit() {
		parts = new Vector<CircuitPart>();
	}

	public void clear() {
		parts.clear();
	}

	public void addGate(Gate gate) {
		parts.add(gate);
		gate.setRepaintListener(this);
	}

	public boolean addWire(Wire newWire) {
		// only add a wire if there is not a wire from<->to
		for (CircuitPart part : parts) {
			if (part instanceof Wire) {
				Wire w = (Wire) part;
				if (w.getTo().equals(newWire.getTo()) && w.getFrom().equals(newWire.getFrom())) {
					// don't add
					return false;
				}
			}
		}
		parts.add(newWire);
		newWire.setRepaintListener(this);
		return true;
	}

//	private CircuitPart[] findPartsAt(Class<?> clazz, int x, int y) {
//		Vector<CircuitPart> findParts = new Vector<CircuitPart>();
//		for (CircuitPart part : parts) {
//			if (part instanceof Gate) {
//				Gate g = (Gate) part;
//				CircuitPart cp = g.findPartAt(x, y);
//				if (cp != null && (clazz == null || (clazz != null && cp.getClass().equals(clazz))))
//					findParts.add(cp);
//			}
//			if (part instanceof Wire) {
//				Wire w = (Wire) part;
//				CircuitPart cp = w.findPartAt(x, y);
//				if (cp != null && (clazz == null || (clazz != null && cp.getClass().equals(clazz))))
//					findParts.add(cp);
//			}
//		}
//		return findParts.toArray(new CircuitPart[findParts.size()]);
//	}

	public Vector<CircuitPart> getParts() {
		return parts;
	}

	public Vector<Gate> getGates() {
		Vector<Gate> gates = new Vector<Gate>();
		for (CircuitPart part : parts) {
			if (part instanceof Gate) {
				gates.add((Gate) part);
			}
		}
		return gates;
	}

	public Vector<Wire> getWires() {
		Vector<Wire> wires = new Vector<Wire>();
		for (CircuitPart part : parts) {
			if (part instanceof Wire) {
				wires.add((Wire) part);
			}
		}
		return wires;
	}

	public void simulate() {
	}

	public void setRepaintListener(LSRepaintListener listener) {
		this.repaintListener = listener;
	}

	public void selectAll() {
		deselectAll();
		for (CircuitPart p : parts) {
			p.select();
		}
	}

	// Alle Gatter und zugehörige Wires deaktivieren
	public void deselectAll() {
		for (CircuitPart p : parts) {
			p.deselect();
		}
	}

	public boolean isModule() {
		for (CircuitPart g : parts) {
			if (g instanceof MODIN) {
				return true;
			}
		}
		return false;
	}

	public boolean isPartAtCoordinates(int x, int y) {
		for (CircuitPart p : parts) {
			if (p.getX() == x && p.getY() == y)
				return true;
		}
		return false;
	}

	public CircuitPart findPartAt(int x, int y) {
		for (Gate g : getGates()) {
			CircuitPart cp = g.findPartAt(x, y);
			if (cp != null)
				return cp;
		}
		for (Wire w : getWires()) {
			CircuitPart cp = w.findPartAt(x, y);
			if (cp != null)
				return cp;
		}
		return null;
	}

	public CircuitPart[] getSelected() {
		Vector<CircuitPart> selParts = new Vector<CircuitPart>();
		for (Gate g : getGates()) {
			if (g.selected && !selParts.contains(g))
				selParts.add(g);
		}
		for (Wire w : getWires()) {
			if (w.selected && !selParts.contains(w))
				selParts.add(w);
			if (w.getFrom() instanceof WirePoint)
				if (w.getFrom().isSelected() && !selParts.contains(w.getFrom()))
					selParts.add(w.getFrom());
			if (w.getTo() instanceof WirePoint)
				if (w.getTo().isSelected() && !selParts.contains(w.getTo()))
					selParts.add(w.getTo());
			for (WirePoint wp : w.getPoints())
				if (wp.isSelected() && !selParts.contains(wp))
					selParts.add(wp);
		}
		return selParts.toArray(new CircuitPart[selParts.size()]);
	}

	public boolean remove(CircuitPart[] parts) {
		if (parts.length == 0)
			return false;

		for (CircuitPart part : parts) {
			if (part instanceof Gate) {
				Gate g = (Gate) part;
				removeGate(g);
			} else if (part instanceof Wire) {
				Wire w = (Wire) part;
				w.disconnect(null);
				this.parts.remove(part);
			}
		}
		return true;
	}

	public boolean removeGate(Gate g) {
		if (g == null)
			throw new RuntimeException("cannot remove a non-gate gate is null");
		if (g.type.equals("modin") || g.type.equals("modout"))
			return false;
		// 1. check all wires if they are connected to that gate
//		for (Pin p : g.pins) {
//			if (p.isConnected()) {
//				for (LSLevelListener l : p.getListeners()) {
//					// must be a wire
//					if (l instanceof Wire) {
//						// 2. delete wire OR put a wirepoint at its end
//						Wire w = (Wire) l;
//						WirePoint wp = new WirePoint(p.getX(), p.getY());
//						p.removeLevelListener(w);
//						w.removeLevelListener(p);
//						if (p.equals(w.getTo())) {
//							w.setTo(wp);
//						} else {
//							w.setFrom(wp);
//						}
//						w.connect(wp);
//						// parts.remove(w);
//
//					}
//				}
//			}
//		}
		for (Iterator<CircuitPart> iter = parts.iterator(); iter.hasNext();) {
			CircuitPart part = iter.next();
			if (!(part instanceof Wire))
				continue;
			Wire w = (Wire) part;
			if (w.getTo() != null && w.getTo() instanceof Pin) {
				Pin p = (Pin) w.getTo();
				if (p.parent == g) {
					w.removeLevelListener(p);
					p.removeLevelListener(w);
					// w.disconnect(null);
					// iter.remove();
					WirePoint wp = new WirePoint(p.getX(), p.getY());
					w.setTo(wp);
					w.connect(wp);
				}
			}
			if (w.getFrom() != null && w.getFrom() instanceof Pin) {
				Pin p = (Pin) w.getFrom();
				if (p.parent == g) {
					w.removeLevelListener(p);
					p.removeLevelListener(w);
					// w.disconnect(null);
					// iter.remove();
					WirePoint wp = new WirePoint(p.getX(), p.getY());
					w.setFrom(wp);
					w.connect(wp);
				}
			}
		}
		// checkWires();
		parts.remove(g);
		return true;
	}

	public boolean removeGateIdx(int idx) {
		Gate g = (Gate) parts.get(idx);
		return removeGate(g);
	}

	public Gate findGateById(String fromGateId) {
		for (CircuitPart p : parts) {
			if (p.getId().equals(fromGateId))
				return (Gate) p;
		}
		return null;
	}

	@Override
	public void needsRepaint(CircuitPart circuitPart) {
		// forward
		if (repaintListener != null)
			repaintListener.needsRepaint(circuitPart);
	}

	public void setGates(Vector<Gate> gates) {
		for (Gate gate : gates) {
			addGate(gate);
			gate.setRepaintListener(this);
		}
		fireRepaint(null);
	}

	public void setWires(Vector<Wire> wires) {
		for (Wire wire : wires) {
			addWire(wire);
		}
		// checkWires();

		fireRepaint(null);
	}

	/**
	 * don't know for what that was...
	 */
//	private void checkWires() {
//		// check if there is at least one wire at any
//		// WirePoint-position
//		for (Wire w : getWires()) {
//			for (WirePoint wp : w.getPoints()) {
//				wp.show = false;
//			}
//		}
//
//		for (Wire w : getWires()) {
//			for (WirePoint wp : w.getPoints()) {
//				CircuitPart[] parts = findPartsAt(WirePoint.class, wp.getX(), wp.getY());
//				if (parts.length > 1) {
//					for (CircuitPart part : parts) {
//						WirePoint wirepoint = (WirePoint) part;
//						wirepoint.show = true;
//					}
//				}
//				parts = findPartsAt(Wire.class, wp.getX(), wp.getY());
//				if (parts.length > 0) {
//					wp.show = true;
//					// add a wirepoint at that position in every part of parts
//					for (CircuitPart part : parts) {
//						Wire wire = (Wire) part;
//						wire.addPointFitting(wp.getX(), wp.getY());
//					}
//				}
//			}
//		}
//		this.needsRepaint(null);
//	}

	private void fireRepaint(CircuitPart source) {
		if (repaintListener != null)
			repaintListener.needsRepaint(source);		
	}

	@Override
	public String toString() {
		String s = "";
		for (Gate g : getGates()) {
			s += "\n" + g;
		}
		for (Wire w : getWires()) {
			s += "\n" + w;
		}
		return s = "Circuit:" + CircuitPart.indent(s, 3);
	}

	public CircuitPart[] findParts(Rectangle2D selectRect) {
		Vector<CircuitPart> findParts = new Vector<CircuitPart>();
		for (CircuitPart p : parts) {
			if (selectRect.contains(p.getBoundingBox())) {
				p.select();
				findParts.add(p);
			}
		}
		return findParts.toArray(new CircuitPart[findParts.size()]);
	}

	public void reset() {
		for (CircuitPart p : getGates())
			p.reset();
		fireRepaint(null);
	}

	public void remove(CircuitPart part) {
		parts.remove(part);
	}

	public Wire getUnfinishedWire() {
		for (CircuitPart part : parts) {
			if (part instanceof Wire && ((Wire) part).isNotFinished())
				return (Wire) part;
		}
		return null;
	}

	/**
	 * replace a wirepoint by a pin
	 * 
	 * @param wp
	 */
	public void checkWirePoint(WirePoint wp) {
		// check for Pin
		int px = wp.getX();
		int py = wp.getY();
		CircuitPart cp = findPartAt(px, py);
		if (cp instanceof Pin) {
			Pin p = (Pin) cp;
			// wirepoint will be replaced by pin
			// get corresponding wire
			for (Wire w : getWires()) {
				if (wp.equals(w.getFrom())) {
					// found wire
					wp.removeLevelListener(w);
					w.removeLevelListener(wp);
					w.setFrom(p);
					w.connect(p);
					return;
				}
				if (wp.equals(w.getTo())) {
					// found wire
					wp.removeLevelListener(w);
					w.removeLevelListener(wp);
					w.setTo(p);
					w.connect(p);
					return;
				}
			}
		}
	}

	public boolean isEmpty() {
		return parts.size() == 0;
	}

	public Rectangle getBoundingBox() {
		Rectangle2D r2d = null;
		for (CircuitPart part : parts) {
			Rectangle r2 = part.getBoundingBox();
			Rectangle2D r22d = new Rectangle2D.Double(r2.x, r2.y, r2.width, r2.height);
			if (r2d == null)
				r2d = (Rectangle2D) r22d.clone();
			r2d.add(r22d);
		}
		return new Rectangle((int) r2d.getX(), (int) r2d.getY(), (int) r2d.getWidth(), (int) r2d.getHeight());
	}

}