package test;

import java.awt.BorderLayout;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import gates.AND;
import logicsim.Gate;
import logicsim.Viewer;
import logicsim.Viewer.Painter;
import logicsim.Viewer.Transformer;

/**
 * Simple test and demonstration of the Viewer class java-forum 2012 - Marco13
 * https://www.java-forum.org/thema/zoomen-in-jpanel.139248/
 */
public class ViewerTest {
	/**
	 * The entry point of this test
	 * 
	 * @param args Not used
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				createAndShowGUI();
			}
		});
	}

	/**
	 * Create and show the GUI, to be called on the EDT
	 */
	private static void createAndShowGUI() {
		JFrame f = new JFrame("Viewer");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		f.getContentPane().setLayout(new BorderLayout());

		f.getContentPane().add(new JLabel("Mouse drags to pan, mouse wheel to zoom"), BorderLayout.NORTH);

		final Viewer viewerComponent = new Viewer();
		viewerComponent.setZoomingSpeed(0.02);
		viewerComponent.addPainter(new TestPainterGraphics());
		f.getContentPane().add(viewerComponent, BorderLayout.CENTER);

		final JLabel infoLabel = new JLabel("");
		f.getContentPane().add(infoLabel, BorderLayout.SOUTH);

		// Add a mouse motion listener to the viewer, which
		// will update the info label containing the current
		// mouse position in screen- and world coordinates
		viewerComponent.addMouseMotionListener(new MouseMotionListener() {
			@Override
			public void mouseMoved(MouseEvent e) {
				updateInfo(e.getPoint());
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				updateInfo(e.getPoint());
			}

			/**
			 * Update the info label depending on the given screen point
			 * 
			 * @param screenPoint The screen point
			 */
			private void updateInfo(Point screenPoint) {
				Transformer transformer = viewerComponent.getTransformer();
				Point2D worldPoint = transformer.screenToWorld(screenPoint, null);
				infoLabel.setText("Screen: " + format(screenPoint) + " World: " + format(worldPoint));
			}

			/**
			 * Create a simple string representation of the given point
			 * 
			 * @param p The point
			 * @return The string representation
			 */
			private String format(Point2D p) {
				String xs = String.format(Locale.ENGLISH, "%.2f", p.getX());
				String ys = String.format(Locale.ENGLISH, "%.2f", p.getY());
				return "(" + xs + "," + ys + ")";
			}

		});

		viewerComponent.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				System.out.println(e.getX());
				System.out.println(e.getY());
				System.out.println(viewerComponent.getTransformer().screenToWorldX(e.getX()));
				System.out.println(viewerComponent.getTransformer().screenToWorldY(e.getY()));
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
			}
		});
		
		f.setSize(500, 500);
		f.setVisible(true);
	}

	/**
	 * Implementation of the {@link Painter} interface that shows some painting in a
	 * transformed Graphics2D
	 */
	private static class TestPainterGraphics implements Painter {
		@Override
		public void paint(Graphics2D g, AffineTransform at, int w, int h) {
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.transform(at);

			Gate gate = new AND();
			gate.getInput(0).label = "A";
			gate.getInput(1).label = "B";
			gate.getOutput(0).label = "Q";
			gate.moveTo(50, 50);
			gate.draw(g);
		}
	}
}
