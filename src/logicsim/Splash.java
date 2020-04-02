package logicsim;

/**
 * Splash/About Screen for LogicSim
 * 
 * @author Andreas Tetzl
 * @version 1.0
 */
import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JWindow;

public class Splash extends JWindow implements Runnable {
	private static final long serialVersionUID = -5516536327616468376L;
	Toolkit toolkit = Toolkit.getDefaultToolkit();
	Image imgSplash;
	SplashPanel splashPanel = new SplashPanel();
	Thread thread;
	boolean running = true;

	public Splash(Frame frm, Image imgSplash) {
		super(frm);

		Dimension scrSize;
		int imgWidth, imgHeight;

		this.imgSplash = imgSplash;

		imgWidth = imgSplash.getWidth(this);
		imgHeight = imgSplash.getHeight(this);
		scrSize = toolkit.getScreenSize();
		setLocation((scrSize.width / 2) - (imgWidth / 2), (scrSize.height / 2) - (imgHeight / 2));
		setSize(imgWidth, imgHeight);
		getContentPane().setLayout(new BorderLayout(0, 0));
		getContentPane().add(splashPanel, "Center");
		this.enableEvents(AWTEvent.MOUSE_EVENT_MASK);

		thread = new Thread(this);
		thread.setPriority(Thread.MAX_PRIORITY);
		thread.start();

		setVisible(true);
		toFront();
	}

	protected void processMouseEvent(MouseEvent e) {
		super.processMouseEvent(e);
		int id = e.getID();
		if (id == MouseEvent.MOUSE_CLICKED)
			running = false;
	}

	public void run() {
		for (int i = 0; i < 25 && running; i++) {

			try {
				Thread.sleep(50);
			} catch (Exception e) {
			}

			toFront();
		}
		setVisible(false);
	}

	class SplashPanel extends JPanel {
		private static final long serialVersionUID = -8616776231981337004L;

		public void paint(Graphics g) {
			g.drawImage(imgSplash, 0, 0, this);
		}
	}
}
