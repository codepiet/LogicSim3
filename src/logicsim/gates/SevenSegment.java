package logicsim.gates;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import logicsim.Gate;

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
		setNumInputs(7);
		setNumOutputs(0);

		getInput(0).label = "a";
		getInput(1).label = "b";
		getInput(2).label = "c";
		getInput(3).label = "d";
		getInput(4).label = "e";
		getInput(5).label = "f";
		getInput(6).label = "g";

		reset();
	}

	@Override
	public void draw(Graphics2D g) {
		super.draw(g);
		int xoffset = getX() + 22;
		int yoffset = getY() + 11;
		g.setStroke(new BasicStroke(1));
		for (int i = 0; i < getNumInputs(); i++) {
			g.setColor(getInputLevel(i) ? Color.red : new Color(0xE0, 0xE0, 0xE0));
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

}