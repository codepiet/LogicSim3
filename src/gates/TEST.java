package gates;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import logicsim.Connector;
import logicsim.Gate;

/**
 * Test Gate for LogicSim
 * 
 * @author Peter Gabriel
 * @version 2.0
 */
public class TEST extends Gate {
	static final long serialVersionUID = 4521959944440523564L;

	public TEST() {
		super("test");
		label = "TEST";
		type = "test";
		width = 80;
		height = 80;
		setNumInputs(4);
		setNumOutputs(4);
		int offset = 30;
		getInput(0).label = "I1";
		getInput(1).label = "I2";
		getInput(2).label = "I3";
		getInput(3).label = "I4";
		getInput(0).setDirection(Connector.RIGHT);
		getInput(1).setDirection(Connector.UP);
		getInput(2).setDirection(Connector.LEFT);
		getInput(3).setDirection(Connector.DOWN);
		getInput(0).moveTo(getX(), getY() + offset);
		getInput(1).moveTo(getX() + offset, getY() + getHeight());
		getInput(2).moveTo(getX() + getWidth(), getY() + getHeight() - offset);
		getInput(3).moveTo(getX() + getWidth() - offset, getY());

		getOutput(0).label = "O1";
		getOutput(1).label = "O2";
		getOutput(2).label = "O3";
		getOutput(3).label = "O4";
		getOutput(0).setDirection(Connector.RIGHT);
		getOutput(1).setDirection(Connector.UP);
		getOutput(2).setDirection(Connector.LEFT);
		getOutput(3).setDirection(Connector.DOWN);
		getOutput(0).setLevelType(Connector.INVERTED);
		getOutput(1).setLevelType(Connector.INVERTED);
		getOutput(2).setLevelType(Connector.INVERTED);
		getOutput(3).setLevelType(Connector.INVERTED);
		getOutput(0).moveTo(getX(), getY() + getHeight() - offset);
		getOutput(1).moveTo(getX() + getWidth() - offset, getY() + getHeight());
		getOutput(2).moveTo(getX() + getWidth(), getY() + offset);
		getOutput(3).moveTo(getX() + offset, getY());

		reset();
	}

	@Override
	protected void drawFrame(Graphics2D g2) {
		Rectangle2D border = new Rectangle2D.Double(getX() + CONN_SIZE, getY() + CONN_SIZE, width - 2 * CONN_SIZE,
				height - 2 * CONN_SIZE);
		g2.setPaint(Color.white);
		g2.fill(border);
		g2.setPaint(Color.black);
		g2.setStroke(new BasicStroke(1));
		g2.draw(border);
		drawLabel(g2, label, bigFont);
	}

	public void simulate() {
		boolean b = true;
		for (int i = 0; i < getNumInputs(); i++) {
			b = getInputLevel(i);
			Connector output = getOutput(i);
			output.setLevel(b);
		}
	}

	@Override
	public void reset() {
		setOutputLevel(0, getInputLevel(0));
		setOutputLevel(1, getInputLevel(1));
		setOutputLevel(2, getInputLevel(2));
		setOutputLevel(3, getInputLevel(3));
	}
}