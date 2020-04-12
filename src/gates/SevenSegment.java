package gates;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import logicsim.Gate;
import logicsim.I18N;

/**
 * Seven Segment Display for LogicSim
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class SevenSegment extends Gate {
	static final long serialVersionUID = 8068938713485037151L;

	public SevenSegment() {
		super("output");
		type = "sevenseg";
		height = 80;
		createInputs(7);
		for (int i = 0; i < 7; i++)
			getPin(i).label = String.valueOf((char) (((int) 'a') + i));
		reset();
	}

	@Override
	public void draw(Graphics2D g) {
		super.draw(g);
		int xoffset = getX() + 19;
		int yoffset = getY() + 11;
		g.setStroke(new BasicStroke(1));
		for (int i = 0; i < getNumInputs(); i++) {
			g.setColor(getPin(i).getLevel() ? Color.red : new Color(0xE0, 0xE0, 0xE0));
			switch (i) {
			case 0:
				drawHorizontalSegment(g, xoffset + 1, yoffset + 1);
				break;
			case 1:
				drawVerticalSegment(g, xoffset + 23, yoffset + 2);
				break;
			case 2:
				drawVerticalSegment(g, xoffset + 23, yoffset + 25);
				break;
			case 3:
				drawHorizontalSegment(g, xoffset + 1, yoffset + 47);
				break;
			case 4:
				drawVerticalSegment(g, xoffset, yoffset + 25);
				break;
			case 5:
				drawVerticalSegment(g, xoffset, yoffset + 2);
				break;
			case 6:
				drawHorizontalSegment(g, xoffset + 1, yoffset + 24);
				break;
			}
		}
	}

	private void drawHorizontalSegment(Graphics2D g2, int x, int y) {
		Path2D path = new Path2D.Double();
		path.moveTo(x, y);
		path.lineTo(x + 2, y - 2);
		path.lineTo(x + 19, y - 2);
		path.lineTo(x + 21, y);
		path.lineTo(x + 19, y + 2);
		path.lineTo(x + 2, y + 2);
		g2.fill(path);
	}

	private void drawVerticalSegment(Graphics2D g2, int x, int y) {
		Path2D path = new Path2D.Double();
		path.moveTo(x, y);
		path.lineTo(x + 2, y + 2);
		path.lineTo(x + 2, y + 19);
		path.lineTo(x, y + 21);
		path.lineTo(x - 2, y + 19);
		path.lineTo(x - 2, y + 2);
		g2.fill(path);
	}

	@Override
	public void loadLanguage() {
		I18N.addGate(I18N.ALL, type, I18N.TITLE, "7-Segment-display");
		I18N.addGate(I18N.ALL, type, I18N.DESCRIPTION,
				"Display with 7 segments (leds) to output numbers from 0-9 and from a-f");

		I18N.addGate("de", type, I18N.TITLE, "7-Segment-Anzeige");
		I18N.addGate("de", type, I18N.DESCRIPTION,
				"7 Segment Anzeige zur Darstellung von Ziffern und den Buchstaben a-f");

		I18N.addGate("es", type, I18N.TITLE, "Display 7 seg.");

		I18N.addGate("fr", type, I18N.TITLE, "Afficheur 7 segments");
	}
}