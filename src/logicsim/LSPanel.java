package logicsim;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

public class LSPanel extends JPanel implements Printable, MouseInputListener, CircuitChangedListener {
	private static final long serialVersionUID = -6414072156700139318L;
	CircuitChangedListener changeListener;

	public Circuit circuit = new Circuit();

	// momentane Aktion
	private int currentAction;
	// das Objekt, das gerade editiert wird
	private CircuitPart currentPart;

	// Gatter, das im Simulationsmodus zuletzt angeklickt wurde
	private CircuitPart lastClicked = null;

	private boolean paintGrid = true;

	private Dimension panelSize = new Dimension(1280, 1024);

	final static Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 9 },
			0);

	static final int NONE = 0;
	static final int ACTION_GATE = 1;
	static final int ACTION_CONNECT = 14;
	static final int ACTION_DRAWWIRE = 15;
	static final int ACTION_ADDPOINT = 16;
	static final int ACTION_DELPOINT = 17;
	static final int ACTION_MODULE = 18;

	public LSPanel() {
		circuit.setChangeListener(this);
		this.setSize(panelSize);
		this.setPreferredSize(panelSize);
		this.revalidate();
		this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				myKeyPressed(e);
			}
		});
		this.addMouseMotionListener(this);
		this.addMouseListener(this);
	}

	public void clear() {
		circuit.deactivateAll();
		currentAction = 0;
		currentPart = null;
		circuit.clear();
		repaint();
	}

	public void setAction(int actionNumber) {
		switch (actionNumber) {
		case ACTION_ADDPOINT:
			notifyChangeListener(I18N.getString(Lang.STAT_ADDPOINT));
			break;
		case ACTION_DELPOINT:
			notifyChangeListener(I18N.getString(Lang.STAT_REMOVEPOINT));
			break;
		}
		currentAction = actionNumber;
	}

	public void setAction(CircuitPart g) {
		currentAction = ACTION_GATE;
		if (g != null) {
			circuit.deactivateAll();
			currentPart = g;
			notifyChangeListener("MSG_ADD_NEW_GATE");
		}
	}

	public void setPaintGrid(boolean onoff) {
		paintGrid = onoff;
	}

	public void draw(Graphics2D g2) {
		// Log.getInstance().print("Drawing panel");
		for (CircuitPart gate : circuit.gates)
			gate.draw(g2);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		// set anti-aliasing
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

		// AffineTransform at = new AffineTransform();
		// at.scale(scaleX, scaleY);
		// at.translate(offsetX, offsetY);
		// at.scale(1, 1);
		// at.translate(0, 0);
		// g2.transform(at);

		g2.setColor(Color.white);
		g2.fillRect(0, 0, panelSize.width, panelSize.height);

		if (paintGrid) {
			g2.setColor(Color.LIGHT_GRAY);
			Path2D grid = new Path2D.Double();
			g2.setStroke(new BasicStroke(2));
			for (int x = 0; x < panelSize.width; x += 10) {
				for (int y = 0; y < panelSize.height; y += 10) {
					grid.moveTo(x, y);
					grid.lineTo(x, y);
				}
			}
			g2.draw(grid);
		}

		draw(g2);

		if (currentPart != null) {
			currentPart.draw(g2);
		}
	}

	/**
	 * check for escape, delete and space key
	 * 
	 * @param e
	 */
	protected void myKeyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		// System.err.println("key pressed");

		if (keyCode == KeyEvent.VK_ESCAPE) {
			if (currentPart instanceof Gate) {
				currentPart = null;
				circuit.deactivateAll();
			}
			if (currentPart instanceof Wire) {
				int pointsOfWire = ((Wire) currentPart).removeLastPoint();
				if (pointsOfWire == 0) {
					currentPart = null;
					circuit.deactivateAll();
				}
			}
			notifyChangeListener(I18N.getString(Lang.STAT_ABORTED));
			repaint();
			return;
		}
		if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_LEFT
				|| keyCode == KeyEvent.VK_RIGHT) {
			if (currentPart == null)
				return;
			int dx = 0;
			int dy = 0;
			if (keyCode == KeyEvent.VK_UP)
				dy -= 10;
			else if (keyCode == KeyEvent.VK_DOWN)
				dy += 10;
			else if (keyCode == KeyEvent.VK_LEFT)
				dx -= 10;
			else
				dx += 10;
			currentPart.moveBy(dx, dy);
			notifyChangeListener();
			return;

		}
		if (keyCode == KeyEvent.VK_DELETE || keyCode == KeyEvent.VK_BACK_SPACE) {
			if (circuit.removeActiveObjects()) {
				currentPart = null;
				currentAction = 0;
				notifyChangeListener();
				return;
			}
			return;
		}
//		if (keyCode == KeyEvent.VK_SPACE) {
//			setAction(currentAction);
//			notifyChangeListener();
//			repaint();
//		}

	}

	private void notifyChangeListener() {
		if (changeListener != null) {
			changeListener.changedCircuit();
		}
		repaint();
	}

	private void notifyChangeListener(String msg) {
		if (changeListener != null) {
			changeListener.changedStatusText(msg);
		}
	}

	@Override
	public int print(Graphics g, PageFormat pf, int pi) throws PrinterException {
		if (pi >= 1) {
			return Printable.NO_SUCH_PAGE;
		}
		draw((Graphics2D) g);
		return Printable.PAGE_EXISTS;
	}

	public void doPrint() {
		PrinterJob printJob = PrinterJob.getPrinterJob();
		printJob.setPrintable(this);
		if (printJob.printDialog()) {
			try {
				printJob.print();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public void setChangeListener(CircuitChangedListener changeListener) {
		this.changeListener = changeListener;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		CircuitPart cp = circuit.findPartAt(e.getX(), e.getY());
		if (cp == null) {
			// empty space has been clicked
			if (currentPart instanceof Gate && !circuit.gates.contains(currentPart)) {
				// add a new gate
				Gate gate = (Gate) currentPart;
				int x = CircuitPart.round(e.getX());
				int y = CircuitPart.round(e.getY());
				gate.moveTo(x, y);
				circuit.addGate(gate);
				lastClicked = gate;
			} else if (currentPart instanceof Wire && ((Wire) currentPart).isNotFinished()) {
				// we are drawing a wire: add a new point and change nothing else
				Wire wire = (Wire) currentPart;
				wire.addPoint(CircuitPart.round(e.getX()), CircuitPart.round(e.getY()));
				Log.getInstance().print(wire);
			} else {
				// nothing happened
				circuit.deactivateAll();
				if (currentPart != null) {
					currentPart = null;
				}
			}
			repaint();
			return;
		}
		CircuitPart[] activeParts = new CircuitPart[] { currentPart };
		int lsAction = currentAction;

		if (cp instanceof Connector) {
			Connector conn = ((Connector) cp);
			// if we are drawing a wire then this is the endpoint
			if (conn.isInput() && activeParts.length == 1 && activeParts[0] instanceof Wire) {
				Wire activeWire = (Wire) activeParts[0];
				// check for existing wire, if there is one, delete!
				if (conn.isConnected()) {
					for (Wire w : conn.wires) {
						w.clear();
					}
				}
				activeWire.connect(conn);
				activeWire.setTempPoint(null);
				activeParts = new CircuitPart[] {};
				activeWire.setChangeListener(circuit);
				repaint();
				// now we have a finished wire, the wire stays active, don't make further
				// changes
				return;
			}

			// of course we cannot edit a Connector, but we can
			// 1. set an input to inverted or high or low or revert to normal type
			if (conn.isInput()) {
				if (lsAction == Connector.HIGH || lsAction == Connector.LOW || lsAction == Connector.INVERTED
						|| lsAction == Connector.NORMAL) {
					// 1. if we clicked on an input modificator
					conn.setLevelType(lsAction);
					notifyChangeListener("MSG_INPUT_CHANGED_TO" + " " + Connector.actionToString(lsAction));
					repaint();
					return;
				}
			} else {
				// is output
				// 3. start a new wire
				// output is clicked
				Wire newWire = new Wire(null, null);
				newWire.connect(conn);
				notifyChangeListener("MSG_NEW_WIRE_STARTED");
				currentPart = newWire;
				circuit.deactivateAll();
				newWire.activate();
				notifyChangeListener();
				return;
			}
		}
		if (cp instanceof Gate) {
			circuit.deactivateAll();
			currentPart = cp;
		}
		if (cp instanceof Wire) {
			circuit.deactivateAll();
			currentPart = cp;
		}
		if (cp instanceof WirePoint) {
			circuit.deactivateAll();
			currentPart = cp;
			cp.activate();
			repaint();
		}

		if (lastClicked != null && lastClicked != cp)
			lastClicked.deactivate();
		lastClicked = cp;
		cp.mousePressed(new LSMouseEvent(e, currentAction, activeParts));
		currentAction = 0;
		repaint();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		int x = e.getPoint().x;
		int y = e.getPoint().y;
		this.requestFocusInWindow();

		// Maustaste losgelassen
		if (Simulation.getInstance().isRunning() && lastClicked != null) {
			// Das Loslassen der Maustasten an Gatter melden
			lastClicked.mouseReleased(x, y);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (!Simulation.getInstance().isRunning()) {
			if (currentPart != null) {
				currentPart.mouseDragged(e);
				repaint();
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// System.err.println("MOVED mouse");
		int rx = CircuitPart.round(e.getX());
		int ry = CircuitPart.round(e.getY());
		changeListener.changedCoordinates("(" + rx + "/" + ry + ")");

		if (currentPart instanceof Wire) {
			Wire wire = (Wire) currentPart;

			if (wire.isNotFinished()) {
				if (e.isShiftDown()) {
					// pressed SHIFT while moving and drawing wire
					WirePoint wp = wire.getLastPoint();
					int lastx = wp.getX();
					int lasty = wp.getY();
					if (Math.abs(rx - lastx) < Math.abs(ry - lasty))
						rx = lastx;
					else
						ry = lasty;
				}
				// the selected wire is unfinished - force draw
				wire.setTempPoint(new Point(rx, ry));
				repaint();
			}
		}

	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void changedCircuit() {
		if (changeListener != null)
			changeListener.changedCircuit();
		repaint();
	}

	@Override
	public void changedStatusText(String text) {
		// just transfer to parent
		changeListener.changedStatusText(text);
	}

	@Override
	public void changedCoordinates(String text) {
	}

	@Override
	public void changedActivePart(CircuitPart activePart) {
		circuit.deactivateAll();
		currentPart = activePart;
		currentPart.activate();
	}

	@Override
	public void needsRepaint(CircuitPart circuitPart) {
		repaint();
	}

}