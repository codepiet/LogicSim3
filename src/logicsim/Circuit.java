package logicsim;

import java.awt.geom.Rectangle2D;
import java.util.Vector;

/**
 * Title: LogicSim Description: digital logic circuit simulator Copyright:
 * Copyright (c) 2001 Company:
 * 
 * @author Andreas Tetzl
 * @version 1.0
 */

public class Circuit implements LSRepaintListener {
	static final long serialVersionUID = 3458986578856078326L;

	Vector<Gate> gates;
	Vector<Wire> wires;

	private LSRepaintListener repaintListener;

	public Circuit() {
		gates = new Vector<Gate>();
		wires = new Vector<Wire>();
	}

	public void clear() {
		gates.clear();
		wires.clear();
	}

	public void addGate(Gate gate) {
		gates.add(gate);
		gate.setRepaintListener(this);
	}

	public boolean addWire(Wire wire) {
		// only add a wire if there is not a wire from<->to
		for (Wire w : wires) {
			if (w.to.equals(wire.to) && w.from.equals(w.from)) {
				// don't add
				return false;
			}
		}
		wires.add(wire);
		wire.setRepaintListener(this);
		return true;

	}

	private CircuitPart[] findPartsAt(Class clazz, int x, int y) {
		Vector<CircuitPart> parts = new Vector<CircuitPart>();
		for (Gate g : gates) {
			CircuitPart cp = g.findPartAt(x, y);
			if (cp != null && (clazz == null || (clazz != null && cp.getClass().equals(clazz))))
				parts.add(cp);
		}
		for (Wire w : wires) {
			CircuitPart cp = w.findPartAt(x, y);
			if (cp != null && (clazz == null || (clazz != null && cp.getClass().equals(clazz))))
				parts.add(cp);
		}
		return parts.toArray(new CircuitPart[parts.size()]);
	}

	public Vector<Gate> getGates() {
		return gates;
	}

	public Vector<Wire> getWires() {
		return wires;
	}

	public void simulate() {
//		for (int j = 0; j < 2; j++) {
//			for (int i = 0; i < gates.size(); i++) {
//				Gate g = (Gate) gates.get(i);
//				g.simulate();
//			}
//		}
	}

	public void setRepaintListener(LSRepaintListener listener) {
		this.repaintListener = listener;
	}

	public void selectAll() {
		deselectAll();
		for (Gate g : gates) {
			g.select();
		}
		for (Wire w : wires) {
			w.select();
		}
	}

	// Alle Gatter und zugehörige Wires deaktivieren
	public void deselectAll() {
		for (Gate g : gates) {
			g.deselect();
		}
		for (Wire w : wires) {
			w.deselect();
			for (WirePoint wp : w.points)
				wp.deselect();
		}
	}

	public boolean isModule() {
		for (CircuitPart g : gates) {
			if (g instanceof MODIN) {
				return true;
			}
		}
		return false;
	}

	public boolean isPartAtCoordinates(int x, int y) {
		for (Gate g : gates) {
			if (g.getX() == x && g.getY() == y)
				return true;
		}
		return false;
	}

	public CircuitPart findPartAt(int x, int y) {
		for (Gate g : gates) {
			CircuitPart cp = g.findPartAt(x, y);
			if (cp != null)
				return cp;
		}
		for (Wire w : wires) {
			CircuitPart cp = w.findPartAt(x, y);
			if (cp != null)
				return cp;
		}
		return null;
	}

	/**
	 * find all wires at this point and insert node
	 * 
	 * @param x
	 * @param y
	 */
	public void addWirePointOnAllWires(int x, int y) {
		// TODO
		// for (Gate g : gates) {
//			Wire w = input.wire;
//				if (w != null) {
//					// check if this wire is at point x,y
//					int pIdx = w.isAt(x, y);
//					// check if there is a node at point x,y
//					if (pIdx > -1 && w.getNodeIndexAt(x, y) == -1) {
//						// add node
//						w.insertPointAfter(pIdx, x, y);
//						w.setNodeIsDrawn(pIdx);
//					}
//				}
//		}
	}

	public CircuitPart[] getSelected() {
		Vector<CircuitPart> parts = new Vector<CircuitPart>();

		for (Gate g : gates) {
			if (g.selected)
				parts.add(g);
		}
		for (Wire w : wires) {
			if (w.selected)
				parts.add(w);
			for (WirePoint pt : w.points)
				if (pt.isSelected())
					parts.add(pt);
		}
		return parts.toArray(new CircuitPart[parts.size()]);
	}

	public boolean remove(CircuitPart[] parts) {
		if (parts.length == 0)
			return false;

		for (CircuitPart part : parts) {
			if (part instanceof Gate) {
				Gate g = (Gate) part;
				// remove outgoing wires
				g.clear();
				gates.remove(g);
			} else if (part instanceof Wire) {
				Wire w = (Wire) part;
				w.disconnect(null);
				wires.remove(w);
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
		for (Wire w : wires) {
			if (w.to != null && w.to instanceof Pin) {
				Pin p = (Pin) w.to;
				if (p.gate == g) {
					w.removeLevelListener(p);
					p.removeLevelListener(w);
					w.removeLevelListener(w.from);
					w.from.removeLevelListener(w);
					w.clear();
				}
			}
			if (w.from != null && w.from instanceof Pin) {
				Pin p = (Pin) w.from;
				if (p.gate == g) {
					w.removeLevelListener(p);
					p.removeLevelListener(w);
					w.removeLevelListener(w.to);
					w.to.removeLevelListener(w);
					w.clear();
				}
			}
		}
		return true;
	}

	public boolean removeGateIdx(int idx) {
		Gate g = gates.get(idx);
		return removeGate(g);
	}

	public Gate findGateById(String fromGateId) {
		for (Gate g : gates) {
			if (g.getId().equals(fromGateId))
				return g;
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
		checkWires();

		fireRepaint(null);
	}

	private void checkWires() {
		// check if there is at least one wire at any
		// WirePoint-position
		for (Wire w : wires) {
			for (WirePoint wp : w.points) {
				CircuitPart[] parts = findPartsAt(WirePoint.class, wp.getX(), wp.getY());
				if (parts.length > 1) {
					for (CircuitPart part : parts) {
						WirePoint wirepoint = (WirePoint) part;
						wirepoint.show = true;
					}
				}
				parts = findPartsAt(Wire.class, wp.getX(), wp.getY());
				if (parts.length > 0) {
					wp.show = true;
					// add a wirepoint at that position in every part of parts
					for (CircuitPart part : parts) {
						Wire wire = (Wire) part;
						wire.addPointFitting(wp.getX(), wp.getY());
					}
				}
			}
		}
	}

	private void fireRepaint(CircuitPart source) {
		if (repaintListener != null)
			repaintListener.needsRepaint(source);
	}

	@Override
	public String toString() {
		String s = "";
		for (Gate g : gates) {
			s += "\n" + g;
		}
		for (Wire w : wires) {
			s += "\n" + w;
		}

		return s = "Circuit:" + CircuitPart.indent(s, 3);
	}

	public CircuitPart[] findParts(Rectangle2D selectRect) {
		Vector<CircuitPart> parts = new Vector<CircuitPart>();
		for (Gate gate : gates) {
			if (selectRect.contains(gate.getBoundingBox())) {
				parts.add(gate);
				gate.select();
			}
		}
		for (Wire w : wires) {
			if (selectRect.contains(w.getBoundingBox())) {
				w.select();
				parts.add(w);
			}
		}
		return parts.toArray(new CircuitPart[parts.size()]);
	}

	public void reset() {
		for (Gate g : gates)
			g.reset();
	}

}