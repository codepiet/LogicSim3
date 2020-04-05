package logicsim;

import java.awt.event.MouseEvent;
import java.util.Vector;

/**
 * Title: LogicSim Description: digital logic circuit simulator Copyright:
 * Copyright (c) 2001 Company:
 * 
 * @author Andreas Tetzl
 * @version 1.0
 */

public class Circuit implements CircuitChangedListener {
	static final long serialVersionUID = 3458986578856078326L;

	Vector<Gate> gates;

	private CircuitChangedListener changeListener;

	public Circuit() {
		gates = new Vector<Gate>();
	}

	public void clear() {
		gates.clear();
	}

	public void addGate(Gate gate) {
		gates.addElement(gate);
		gate.setChangeListener(this);
	}

	public Vector<Gate> getGates() {
		return gates;
	}

	public Vector<Wire> getWires() {
		Vector<Wire> wires = new Vector<Wire>();
		for (Gate g : gates)
			for (Connector conn : g.getOutputs())
				if (conn.isConnected())
					for (Wire wire : conn.wires)
						wires.add(wire);
		return wires;
	}

	public void simulate() {
		for (int j = 0; j < 2; j++) {
			for (int i = 0; i < gates.size(); i++) {
				Gate g = (Gate) gates.get(i);
				g.simulate();
			}
		}
	}

	public void setChangeListener(CircuitChangedListener changeListener) {
		this.changeListener = changeListener;
	}

	// Alle Gatter und zugehörige Wires deaktivieren
	public void deactivateAll() {
		for (Gate g : gates) {
			g.deactivate();
			g.deactivateWires();
		}
		// for (Wire w : wires)
		// w.deactivate();
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

	public Vector<CircuitPart> findActiveObjects() {
		Vector<CircuitPart> parts = new Vector<CircuitPart>();
		for (Gate g : gates) {
			if (g.active)
				parts.add(g);

			for (Connector conn : g.getOutputs())
				if (conn.isConnected()) {
					for (Wire wire : conn.wires)
						if (wire.active)
							parts.add(wire);
				}
		}
		return parts;
	}

	public boolean removeActiveObjects() {
		boolean found = false;
		Vector<CircuitPart> parts = findActiveObjects();
		found = parts.size() > 0;
		for (CircuitPart part : parts) {
			if (part instanceof Gate) {
				CircuitPart g = (CircuitPart) part;
				// remove outgoing wires
				g.clear();
				gates.remove(g);
			} else if (part instanceof Wire) {
				((Wire) part).disconnect(null);
			}
		}
		return found;
	}

	public boolean removeGate(Gate g) {
		if (g == null)
			throw new RuntimeException("cannot remove a non-gate gate is null");
		if (g.type.equals("modin") || g.type.equals("modout"))
			return false;
		// 1. delete wires
		for (Connector c : g.getConnectors()) {
			if (c.isConnected()) {
				for (Wire w : c.wires) {
					c.clear();
					c.delWire(w);
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

	public void mousePressed(MouseEvent e) {

	}

	@Override
	public void changedCircuit() {
		if (changeListener != null)
			changeListener.changedCircuit();
	}

	@Override
	public void changedStatusText(String text) {
		// just transfer to parent
		if (changeListener != null)
			changeListener.changedStatusText(text);
	}

	@Override
	public void changedCoordinates(String text) {
	}

	@Override
	public void changedActivePart(CircuitPart activePart) {
		deactivateAll();
		activePart.activate();
		if (changeListener != null)
			changeListener.changedActivePart(activePart);
	}

	@Override
	public void needsRepaint(CircuitPart circuitPart) {
		if (changeListener != null)
			changeListener.needsRepaint(circuitPart);
	}

	@Override
	public void setAction(int action) {
	}

	public void setGates(Vector<Gate> gates) {
		for (Gate gate : gates) {
			gate.setChangeListener(this);
			this.gates.add(gate);
		}
		if (changeListener != null)
			changeListener.needsRepaint(null);
	}

	@Override
	public String toString() {
		String s = "";
		for (Gate g : gates) {
			s += "\n" + g;
		}
		return s = "Circuit:" + CircuitPart.indent(s, 3);
	}

}