package logicsim;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
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
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.swing.event.MouseInputAdapter;

public class LSPanel extends Viewer implements Printable, CircuitChangedListener, LSRepaintListener {
	public class LogicSimPainterGraphics implements Painter {
		@Override
		public void paint(Graphics2D g2, AffineTransform at, int w, int h) {
			// set anti-aliasing
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			g2.transform(at);
			if (LSProperties.getInstance().getPropertyBoolean(LSProperties.PAINTGRID, true) && scaleX > 0.7f) {
				int startX = CircuitPart.round((int) Math.round(getTransformer().screenToWorldX(0)));
				int startY = CircuitPart.round((int) Math.round(getTransformer().screenToWorldY(0)));
				int endX = (int) getTransformer().screenToWorldX(w + 9);
				int endY = (int) getTransformer().screenToWorldY(h + 9);
				g2.setColor(gridColor);
				Path2D grid = new Path2D.Double();
				g2.setStroke(new BasicStroke(1));
				for (int x = startX; x < endX; x += 10) {
					for (int y = startY; y < endY; y += 10) {
						grid.moveTo(x, y);
						grid.lineTo(x, y);
					}
				}
				g2.draw(grid);
			}

			draw(g2);

			// TODO redraw selected parts?

			if (currentAction == ACTION_SELECT) {
				g2.setStroke(dashed);
				g2.setColor(Color.blue);
				if (selectRect != null)
					g2.draw(selectRect);
			}

		}
	}

	/**
	 * A class summarizing the mouse interaction for this viewer.
	 */
	private class MouseControl extends MouseInputAdapter
			implements MouseListener, MouseMotionListener, MouseWheelListener {

		@Override
		public void mouseDragged(MouseEvent e) {
			e = convertToWorld(e);
			if (currentAction == ACTION_SELECT) {
				notifyZoomPos(scaleX, new Point(e.getX(), e.getY()));
				// previousPoint is the start point of the selection box
				Point currentMouse = new Point(e.getX(), e.getY());
				if (currentMouse.x < previousPoint.x || currentMouse.y < previousPoint.y)
					selectRect.setFrameFromDiagonal(currentMouse, previousPoint);
				else
					selectRect.setFrameFromDiagonal(previousPoint, currentMouse);
				repaint();
				return;
			}

			CircuitPart[] parts = circuit.getSelected();
			if (parts.length == 0) {
				// drag world
				int dx = e.getX() - previousPoint.x;
				int dy = e.getY() - previousPoint.y;
				translate(dx, dy);
				return;
			} else {
				// don't drag in simulation mode
				if (Simulation.getInstance().isRunning())
					return;

				// drag parts
				notifyZoomPos(scaleX, new Point(e.getX(), e.getY()));
				for (CircuitPart part : parts) {
					part.mouseDragged(e);

					if (LSProperties.getInstance().getPropertyBoolean(LSProperties.AUTOWIRE, true)) {
						// check if currentpart is a gate and if any output touches another part's input
						// pin
						if (part instanceof Gate) {
							Gate gate = (Gate) part;
							for (Pin pin : gate.pins) {
								// autowire unconnected pins only
								if (!pin.isConnected()) {
									int x = pin.getX();
									int y = pin.getY();
									for (Gate g : circuit.getGates()) {
										CircuitPart cp = g.findPartAt(x, y);
										if (cp instanceof Pin) {
											Pin p = (Pin) cp;
											if (pin.isInput() == p.isOutput()) {
												// put new wire between pin and p
												Wire w = null;
												if (pin.isOutput())
													w = new Wire(pin, p);
												else
													w = new Wire(p, pin);
												w.deselect();
												if (circuit.addWire(w)) {
													p.connect(w);
													pin.connect(w);
												}
											}
										}
									}
								}
							}
						}
					}
				}
				fireCircuitChanged();
			}
		}

		private void setPoint(int x, int y) {
			previousPoint.setLocation(x, y);
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			e = convertToWorld(e);

			int rx = CircuitPart.round(e.getX());
			int ry = CircuitPart.round(e.getY());
			setPoint(rx, ry);
			notifyZoomPos(scaleX, new Point(rx, ry));

			CircuitPart[] parts = circuit.getSelected();
			if (parts.length == 1 && parts[0] instanceof Wire) {
				Wire wire = (Wire) parts[0];

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
		public void mousePressed(MouseEvent e) {
			
			e = convertToWorld(e);
			setPoint(e.getX(), e.getY());
			if (currentAction == ACTION_SELECT) {
				selectRect = new Rectangle2D.Double(e.getX(), e.getY(), 0, 0);
			}

			CircuitPart[] parts = circuit.getSelected();
			CircuitPart cp = circuit.findPartAt(e.getX(), e.getY());
			boolean wireNotFinished = (parts.length == 1 && parts[0] instanceof Wire
					&& ((Wire) parts[0]).isNotFinished());
			if (wireNotFinished) {
				// we are drawing a wire: add a new point and change nothing else
				Wire wire = (Wire) parts[0];
				if (cp == null)
					// empty space clicked
					wire.addPoint(CircuitPart.round(e.getX()), CircuitPart.round(e.getY()));
				else if (cp instanceof Pin) {
					Pin pin = ((Pin) cp);
					wire.to = pin;
					pin.connect(wire);
					// check for existing wire, if there is one, delete!
//					if (pin.isConnected()) {
//						for (Wire w : pin.wires) {
//							w.clear();
//						}
//					}
					wire.setTempPoint(null);
					fireCircuitChanged();
					// now we have a finished wire, the wire stays active
				}
				repaint();
				return;
			}
			if (cp == null) {
				// empty space has been clicked
				circuit.deselectAll();
				repaint();
				fireStatusText("");
				return;
			}
			// check if the part is a connector
			if (cp instanceof Pin) {
				Pin pin = ((Pin) cp);

				// we cannot edit a Connector, but we can
				// 1. set an input to inverted or high or low or revert to normal type
				if (pin.isInput()) {
					if (currentAction == Pin.HIGH || currentAction == Pin.LOW || currentAction == Pin.INVERTED
							|| currentAction == Pin.NORMAL) {
						// 1. if we clicked on an input modificator
						pin.setLevelType(currentAction);
						pin.changedLevel(new LSLevelEvent(new Wire(null, null), pin.level, true));
						currentAction = ACTION_NONE;
						fireCircuitChanged();
						return;
					}
				} else {
					// is output
					// 3. start a new wire
					// output is clicked

					if (!e.isAltDown()) {
						Wire newWire = new Wire(pin, null);
						if (circuit.addWire(newWire)) {
							pin.connect(newWire);
						}
						fireStatusText(I18N.tr(Lang.EDITWIRE));
						circuit.deselectAll();
						newWire.select();
						fireCircuitChanged();
						return;
					}
				}
			}
			if (cp instanceof Gate) {
				if (parts.length > 0) {
					// check if we clicked a new gate
					if (!cp.isSelected()) {
						cp.select();
						if (!e.isShiftDown()) {
							circuit.deselectAll();
						}
						parts = circuit.getSelected();
					}
				}
			} else if (cp instanceof Wire) {
				circuit.deselectAll();
				cp.select();
			} else if (cp instanceof WirePoint) {
				circuit.deselectAll();
				cp.select();
			}

			// if (lastClicked != null && lastClicked != cp)
			// lastClicked.deselect();
			// lastClicked = cp;
			cp.mousePressed(new LSMouseEvent(e, currentAction, parts));
			currentAction = ACTION_NONE;
			repaint();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			e = convertToWorld(e);
			int x = e.getPoint().x;
			int y = e.getPoint().y;
			LSPanel.this.requestFocusInWindow();

			if (currentAction == ACTION_SELECT) {
				CircuitPart[] parts = circuit.findParts(selectRect);
				fireStatusText(String.format(I18N.tr(Lang.PARTSSELECTED), String.valueOf(parts.length)));
				currentAction = ACTION_NONE;
				selectRect = null;
				repaint();
				return;
			}
			CircuitPart[] parts = circuit.getSelected();
			for (CircuitPart part : parts) {
				part.mouseReleased(x, y);
			}
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			zoom(e.getX(), e.getY(), -e.getWheelRotation() * zoomingSpeed);
			notifyZoomPos(scaleX, new Point(e.getX(), e.getY()));
		}
	}

	static final int ACTION_ADDPOINT = 16;

	static final int ACTION_CONNECT = 14;

	static final int ACTION_DELPOINT = 17;

	static final int ACTION_DRAWWIRE = 15;

	static final int ACTION_GATE = 1;

	static final int ACTION_MODULE = 18;
	static final int ACTION_NONE = 0;
	static final int ACTION_SELECT = 19;
	static final int ACTION_SIMULATION = 20;
	final static Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10,
			new float[] { 10 }, 0);
	public static final Color gridColor = Color.black;
	private static final long serialVersionUID = -6414072156700139318L;
	CircuitChangedListener changeListener;
	public Circuit circuit = new Circuit();

	// current mode
	private int currentAction;

	private Dimension panelSize = new Dimension(1280, 1024);

	/**
	 * used for track selection, is one endpoint of a rectangle
	 */
	private Rectangle2D selectRect;

	public LSPanel() {
		this.setSize(panelSize);
		this.setPreferredSize(panelSize);
		this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		circuit.setRepaintListener(this);

		// setZoomingSpeed(0.02);
		setPainter(new LogicSimPainterGraphics());

		MouseControl mouseControl = new MouseControl();
		addMouseListener(mouseControl);
		addMouseMotionListener(mouseControl);
		addMouseWheelListener(mouseControl);

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				myKeyPressed(e);
			}
		});
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
	public void changedZoomPos(double zoom, Point pos) {
	}

	public void clear() {
		circuit.deselectAll();
		currentAction = 0;
		circuit.clear();
		repaint();
	}

	private MouseEvent convertToWorld(MouseEvent e) {
		int x = (int) (getTransformer().screenToWorldX(e.getX()));
		int y = (int) (getTransformer().screenToWorldY(e.getY()));
		// int x = (int) Math.round(getTransformer().screenToWorldX(e.getX()));
		// int y = (int) Math.round(getTransformer().screenToWorldY(e.getY()));

		MouseEvent ec = new MouseEvent((Component) e.getSource(), e.getID(), e.getWhen(), e.getModifiersEx(), x, y,
				e.getClickCount(), e.isPopupTrigger(), e.getButton());
		return ec;
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

	public void draw(Graphics2D g2) {
		// Log.getInstance().print("Drawing panel");
		for (CircuitPart gate : circuit.gates) {
			gate.draw(g2);
		}
		for (CircuitPart wire : circuit.wires) {
			wire.draw(g2);
		}
	}

	/**
	 * mirror a part if selected
	 */
	public void mirrorSelected() {
		CircuitPart[] parts = circuit.getSelected();
		for (CircuitPart part : parts) {
			if (part instanceof Gate) {
				((Gate) part).mirror();
			}
		}
		repaint();
	}

	/**
	 * check for escape, delete and space key
	 * 
	 * @param e
	 */
	protected void myKeyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();

		CircuitPart[] parts = circuit.getSelected();
		if (parts.length == 0)
			return;

		if (keyCode == KeyEvent.VK_ESCAPE) {
			if (parts.length == 1 && parts[0] instanceof Wire) {
				Wire w = (Wire) parts[0];
				int pointsOfWire = w.removeLastPoint();
				if (pointsOfWire == 0) {
					w = null;
					parts = null;
					circuit.deselectAll();
					// TODO must the wire be deleted somewhere?
				}
			} else if (parts.length > 1) {
				circuit.deselectAll();
			}
			repaint();
			return;
		}
		if (keyCode == KeyEvent.VK_UP || keyCode == KeyEvent.VK_DOWN || keyCode == KeyEvent.VK_LEFT
				|| keyCode == KeyEvent.VK_RIGHT) {
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
			for (CircuitPart part : parts)
				part.moveBy(dx, dy);
			fireCircuitChanged();
			return;
		}

		if (keyCode == KeyEvent.VK_DELETE || keyCode == KeyEvent.VK_BACK_SPACE) {
			if (circuit.remove(parts)) {
				currentAction = ACTION_NONE;
				fireStatusText(I18N.tr(Lang.PARTSDELETED, String.valueOf(parts.length)));
				fireCircuitChanged();
				repaint();
				return;
			}
			return;
		}

		if (keyCode == KeyEvent.VK_SPACE) {
			CircuitPart[] selected = circuit.getSelected();
			if (selected.length != 1)
				return;
			if (selected[0] instanceof Gate) {
				Gate g = (Gate) selected[0];
				g.interact();
			}
			repaint();
		}

	}

	@Override
	public void needsRepaint(CircuitPart circuitPart) {
		repaint();
	}

	private void fireCircuitChanged() {
		if (changeListener != null) {
			changeListener.changedCircuit();
		}
		repaint();
	}

	private void fireStatusText(String msg) {
		if (changeListener != null) {
			changeListener.changedStatusText(msg);
		}
	}

	private void notifyZoomPos(double zoom, Point mousePos) {
		if (changeListener != null)
			changeListener.changedZoomPos(zoom, mousePos);
	}

	@Override
	public int print(Graphics g, PageFormat pf, int pi) throws PrinterException {
		if (pi >= 1) {
			return Printable.NO_SUCH_PAGE;
		}
		draw((Graphics2D) g);
		return Printable.PAGE_EXISTS;
	}

	/**
	 * rotate a gate if selected
	 */
	public void rotateSelected() {
		CircuitPart[] parts = circuit.getSelected();
		for (CircuitPart part : parts) {
			if (part instanceof Gate) {
				((Gate) part).rotate();
			}
		}
		fireCircuitChanged();
	}

	public void setAction(CircuitPart g) {
		currentAction = ACTION_GATE;
		if (g != null) {
			circuit.deselectAll();
			// place new gate
			int posX = (int) -offsetX / 10 * 10 + 20;
			int posY = (int) -offsetY / 10 * 10 + 20;
			while (circuit.isPartAtCoordinates(posX, posY)) {
				posX += 40;
				posY += 40;
			}
			g.moveTo(posX, posY);
			circuit.addGate((Gate) g);
			g.select();

			fireStatusText("MSG_ADD_NEW_GATE");
			fireCircuitChanged();
			repaint();
		}
	}

	public void setAction(int actionNumber) {
		switch (actionNumber) {
		case ACTION_ADDPOINT:
			fireStatusText(I18N.tr(Lang.ADDPOINT));
			break;
		case ACTION_DELPOINT:
			fireStatusText(I18N.tr(Lang.REMOVEPOINT));
			break;
		case Pin.HIGH:
			fireStatusText(I18N.tr(Lang.INPUTHIGH));
			break;
		case Pin.LOW:
			fireStatusText(I18N.tr(Lang.INPUTLOW));
			break;
		case Pin.NORMAL:
			fireStatusText(I18N.tr(Lang.INPUTNORM));
			break;
		case Pin.INVERTED:
			fireStatusText(I18N.tr(Lang.INPUTINV));
			break;
		}
		currentAction = actionNumber;
	}

	public void setChangeListener(CircuitChangedListener changeListener) {
		this.changeListener = changeListener;
	}

	/**
	 * less zoom
	 */
	public void zoomOut() {
		int x = (int) getTransformer().screenToWorldX(getWidth() / 2);
		int y = (int) getTransformer().screenToWorldY(getHeight() / 2);
		zoom(x, y, -0.5f);
		notifyZoomPos(scaleX, new Point(x, y));
	}

	/**
	 * more zoom
	 */
	public void zoomIn() {
		int x = (int) getTransformer().screenToWorldX(getWidth() / 2);
		int y = (int) getTransformer().screenToWorldY(getHeight() / 2);
		zoom(x, y, 0.5f);
		notifyZoomPos(scaleX, new Point(x, y));
	}

	public void gateSettings() {
		CircuitPart[] parts = circuit.getSelected();
		if (parts.length == 1 && parts[0] instanceof Gate) {
			Gate g = (Gate) parts[0];
			g.showPropertiesUI(this);
			fireCircuitChanged();
		}
	}

}