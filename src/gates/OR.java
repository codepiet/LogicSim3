package gates;

import logicsim.Pin;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import logicsim.Gate;
import logicsim.LSProperties;

/**
 * OR Gate for LogicSim
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class OR extends Gate {

	public OR() {
		super("basic");
		label = "\u2265" + "1";
		type = "or";
		createOutputs(1);
		createInputs(2);
		variableInputCountSupported = true;
		// move inputs
	}

	public void simulate() {
		super.simulate();
		boolean b = false;
		for (Pin c : getInputs()) {
			b = b || c.getLevel();
		}
		getPin(0).setLevel(b);
	}

	@Override
	protected void drawFrame(Graphics2D g2) {
		String gateType = LSProperties.getInstance().getProperty(LSProperties.GATEDESIGN, LSProperties.GATEDESIGN_IEC);
		if (gateType.equals(LSProperties.GATEDESIGN_IEC))
			super.drawFrame(g2);
		else
			drawANSI(g2);
	}

	protected void drawANSI(Graphics2D g2) {
		Path2D p = new Path2D.Double();
		double xl = getX() + CONN_SIZE;
		double yu = getY() + CONN_SIZE;
		double xr = getX() + width - CONN_SIZE + 1;
		double yb = getY() + height - CONN_SIZE;
		double xb1 = getX() + 5 * CONN_SIZE;
		double cY = getY() + height / 2;

		p.moveTo(xl, yu);
		p.lineTo(xl + CONN_SIZE, yu);
		p.curveTo(xb1, yu, xr, cY, xr, cY);
		p.curveTo(xr, cY, xb1, yb, xl + CONN_SIZE, yb);
		p.lineTo(xl, yb);
		p.curveTo(xl + 10, yb - 10, xl + 10, yu + 10, xl, yu);
		p.closePath();
		g2.setPaint(Color.WHITE);
		g2.fill(p);
		g2.setPaint(Color.black);
		g2.draw(p);
	}

}