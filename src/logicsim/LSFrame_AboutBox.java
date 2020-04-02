package logicsim;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JWindow;

public class LSFrame_AboutBox extends JWindow {

	private static final long serialVersionUID = -3193728228853983319L;
	Toolkit toolkit = Toolkit.getDefaultToolkit();
	Image imgSplash;
	SplashPanel splashPanel = new SplashPanel();

	public LSFrame_AboutBox(Frame parent) {
		super(parent);
		Dimension scrSize;
		int imgWidth, imgHeight;

		this.imgSplash = new ImageIcon(logicsim.LSFrame.class.getResource("images/about.jpg")).getImage();

		imgWidth = imgSplash.getWidth(this);
		imgHeight = imgSplash.getHeight(this) + 100;
		scrSize = toolkit.getScreenSize();
		setLocation((scrSize.width / 2) - (imgWidth / 2), (scrSize.height / 2) - (imgHeight / 2));
		setSize(imgWidth, imgHeight);
		getContentPane().setLayout(new BorderLayout(0, 0));
		getContentPane().add(splashPanel, "Center");
		this.enableEvents(AWTEvent.MOUSE_EVENT_MASK);

		this.setVisible(true);
	}

	protected void processMouseEvent(MouseEvent e) {
		super.processMouseEvent(e);
		int id = e.getID();
		if (id == MouseEvent.MOUSE_CLICKED) {
			this.setVisible(false);
			this.dispose();
		}
	}

	class SplashPanel extends JPanel {
		private static final long serialVersionUID = 5564588819196489014L;

		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setColor(Color.black);
			g2.fillRect(0, 0, getWidth(), getHeight());
			g2.drawImage(imgSplash, 0, 0, this);

			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			g2.setColor(Color.white);

			FontMetrics fm = g2.getFontMetrics();
			Font of = fm.getFont();
			Font f = new Font(of.getName(), of.getStyle(), 12);
			g2.setFont(f);

			String version = App.class.getPackage().getImplementationVersion();
			g2.drawString("Version " + version + " - This program is free software - Released under the GPL", 10, 240);
			g2.drawString("Programmed by Peter Gabriel since Version 3.0", 10, 260);
			g2.drawString("Based on LogicSim 2.0 by Andreas Tetzl", 10, 280);
			g2.drawString("andreas@tetzl.de         pngabriel@gmail.com        Download on Github", 10, 300);
			// g2.drawString("Artwork by Jens Borsdorf, www.jens-borsdorf.de", 10, 310);

		}
	}

}