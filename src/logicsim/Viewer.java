package logicsim;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import javax.swing.JPanel;

/**
 * A panel that allows panning and zooming. It contains a "Painter" interface.
 * Instances of classes implementing this interface may be added to this viewer
 * and perform painting operations, depending on the current zoom and
 * translation of the Viewer. Additionally, it offers a "Transformer" interface,
 * which offers methods for the conversion between screen- and world
 * coordinates.
 */
public class Viewer extends JPanel {

	public static final double minZoom = 0.1f;
	public static final double maxZoom = 10f;

	/**
	 * Interface for all classes that may perform painting operations. Instances of
	 * classes implementing this interface may be passed to the
	 * {@link Viewer#addPainter(Painter)} method.
	 */
	public interface Painter {
		/**
		 * Perform the painting operations on the given Graphics. The given
		 * AffineTransform will describe the current zooming and translation of this
		 * Viewer.
		 * 
		 * @param g  The Graphics used for painting
		 * @param at The AffineTransform containing the current zooming and translation
		 *           of this Viewer.
		 * @param w  The width of the painting area
		 * @param h  The height of the painting area
		 */
		void paint(Graphics2D g, AffineTransform at, int w, int h);
	}

	/**
	 * Interface for transforming between the screen- and world coordinate system of
	 * this Viewer, depending on its current zooming and translation. An instance of
	 * this class may be obtained from {@link Viewer#getTransformer()}.
	 */
	public interface Transformer {
		/**
		 * Convert the given world coordinate into a screen coordinate
		 * 
		 * @param x The world coordinate
		 * @return The screen screen
		 */
		double worldToScreenX(double x);

		/**
		 * Convert the given world coordinate into a screen coordinate
		 * 
		 * @param y The world coordinate
		 * @return The screen screen
		 */
		double worldToScreenY(double y);

		/**
		 * Convert the given screen coordinate into a world coordinate
		 * 
		 * @param x The screen coordinate
		 * @return The world coordinate
		 */
		double screenToWorldX(double x);

		/**
		 * Convert the given screen coordinate into a world coordinate
		 * 
		 * @param y The screen coordinate
		 * @return The world coordinate
		 */
		double screenToWorldY(double y);

		/**
		 * Convert the given point from screen coordinates to world coordinates. If the
		 * given destination point is <code>null</code>, a new point will be created and
		 * returned.
		 * 
		 * @param pSrc The source point
		 * @param pDst The destination point
		 * @return The destination point
		 */
		Point2D screenToWorld(Point2D pSrc, Point2D pDst);

		/**
		 * Convert the given point from world coordinates to screen coordinates. If the
		 * given destination point is <code>null</code>, a new point will be created and
		 * returned.
		 * 
		 * @param pSrc The source point
		 * @param pDst The destination point
		 * @return The destination point
		 */
		Point2D worldToScreen(Point2D pSrc, Point2D pDst);
	}

	/**
	 * Serial UID
	 */
	private static final long serialVersionUID = -3252732941609348700L;

	/**
	 * The current zooming speed
	 */
	protected double zoomingSpeed = 0.05;

	/**
	 * The current offset (translation) in x-direction, in world units
	 */
	double offsetX = 0;

	/**
	 * The current offset (translation) in y-direction, in world units
	 */
	double offsetY = 0;

	/**
	 * The current scaling in x-direction
	 */
	protected double scaleX = 1;

	/**
	 * The current scaling in y-direction
	 */
	protected double scaleY = 1;

	/**
	 * The previous mouse position
	 */
	protected final Point previousPoint;

	private Painter painter;

	/**
	 * The {@link Transformer} instance of this Viewer
	 */
	private final Transformer transformer;

	/**
	 * Simple implementation of a {@link Transformer}
	 */
	class SimpleTransformer implements Transformer {
		@Override
		public double worldToScreenX(double x) {
			return (x + offsetX) * scaleX;
		}

		@Override
		public double worldToScreenY(double y) {
			return (y + offsetY) * scaleY;
		}

		@Override
		public double screenToWorldX(double x) {
			return x / scaleX - offsetX;
		}

		@Override
		public double screenToWorldY(double y) {
			return y / scaleY - offsetY;
		}

		@Override
		public Point2D screenToWorld(Point2D pSrc, Point2D pDst) {
			double x = screenToWorldX(pSrc.getX());
			double y = screenToWorldY(pSrc.getY());
			if (pDst == null) {
				pDst = new Point2D.Double(x, y);
			} else {
				pDst.setLocation(x, y);
			}
			return pDst;
		}

		@Override
		public Point2D worldToScreen(Point2D pSrc, Point2D pDst) {
			double x = worldToScreenX(pSrc.getX());
			double y = worldToScreenY(pSrc.getY());
			if (pDst == null) {
				pDst = new Point2D.Double(x, y);
			} else {
				pDst.setLocation(x, y);
			}
			return pDst;
		}

	};

	/**
	 * Creates a new Viewer.
	 */
	public Viewer() {
		painter = null;
		previousPoint = new Point();
		transformer = new SimpleTransformer();

		setBackground(Color.WHITE);
	}

	/**
	 * Set the zooming speed of this viewer, which will affect the change of the
	 * scaling depending on the mouse wheel rotation
	 * 
	 * @param zoomingSpeed The zooming speed
	 */
	public void setZoomingSpeed(double zoomingSpeed) {
		this.zoomingSpeed = zoomingSpeed;
	}

	/**
	 * Add the given {@link Painter}, which will perform painting operations in the
	 * {@link #paintComponent(Graphics)} method.
	 * 
	 * @param painter The {@link Painter} to add
	 */
	public void setPainter(Painter painter) {
		this.painter = painter;
		repaint();
	}

	/**
	 * Return the {@link Transformer} that offers coordinate transformations for
	 * this Viewer
	 * 
	 * @return The {@link Transformer} for this Viewer
	 */
	public Transformer getTransformer() {
		return transformer;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;

		AffineTransform at = new AffineTransform();
		at.scale(scaleX, scaleY);
		at.translate(offsetX, offsetY);

		painter.paint(g2, at, getWidth(), getHeight());
	}

	protected void resetZoom() {
		scaleX = 1f;
		scaleY = 1f;
		repaint();
	}

	/**
	 * Zoom about the specified point (in screen coordinates) by the given amount
	 * 
	 * @param x      The x-coordinate of the zooming center
	 * @param y      The y-coordinate of the zooming center
	 * @param amount The zooming amount
	 */
	protected void zoom(int x, int y, double amount) {
		// check if scaleX and Y would be outside bounds
		double testX = scaleX + amount * scaleX;
		double testY = scaleY + amount * scaleY;

		if (testX > maxZoom)
			return;
		if (testX < minZoom)
			return;

		offsetX -= x / scaleX;
		offsetY -= y / scaleY;

		scaleX = testX;
		scaleY = testY;
		offsetX += x / scaleX;
		offsetY += y / scaleY;
		repaint();
	}

	/**
	 * Translate this viewer by the given delta, in screen coordinates
	 * 
	 * @param dx The movement delta in x-direction
	 * @param dy The movement delta in y-direction
	 */
	protected void translate(int dx, int dy) {
		offsetX += dx / scaleX;
		offsetY += dy / scaleY;
//		if (offsetX > 0)
//			offsetX = 0;
//		if (offsetY > 0)
//			offsetY = 0;
		repaint();
	}

}