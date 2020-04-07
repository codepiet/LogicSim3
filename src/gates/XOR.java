package gates;

import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import logicsim.Pin;

/**
 * XOR gate for LogicSim
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class XOR extends OR {

	public XOR() {
		super();
		label = "=1";
		type = "xor";
	}

	@Override
	public void simulate() {
		super.simulate();
		int n = 0;
		for (Pin p : getInputs())
			if (p.getLevel())
				n++;
		// ungerade ??
		getPin(0).setLevel(n % 2 > 0);
	}

	@Override
	protected void drawANSI(Graphics2D g2) {
		Path2D p = new Path2D.Double();
		double xl = getX() + 2 * CONN_SIZE;
		double xl2 = getX() + CONN_SIZE;
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
		p.moveTo(xl2, yb);
		p.curveTo(xl2 + 10, yb - 10, xl2 + 10, yu + 10, xl2, yu);

		g2.draw(p);
	}
}