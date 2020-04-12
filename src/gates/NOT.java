package gates;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import logicsim.Gate;
import logicsim.I18N;
import logicsim.LSProperties;
import logicsim.Pin;

/**
 * NOT Gate for LogicSim
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class NOT extends Gate {
	static final long serialVersionUID = 3351085067064933298L;

	public NOT() {
		super("basic");
		label = "1";
		type = "not";
		createInputs(1);
		createOutputs(1);
		getPin(1).setLevelType(Pin.INVERTED);
	}

	public void simulate() {
		getPin(1).setLevel(getPin(0).getLevel());
	}

	@Override
	protected void drawFrame(Graphics2D g2) {
		String gateType = LSProperties.getInstance().getProperty(LSProperties.GATEDESIGN, LSProperties.GATEDESIGN_IEC);
		if (gateType.equals(LSProperties.GATEDESIGN_IEC))
			super.drawFrame(g2);
		else
			drawANSI(g2);
	}

	private void drawANSI(Graphics2D g2) {
		Path2D p = new Path2D.Double();
		double yu = getY() + CONN_SIZE + 6;
		double xr = getX() + width - CONN_SIZE + 1;
		double yb = getY() + height - CONN_SIZE - 6;

		p.moveTo(xc, yc);
		p.lineTo(xc, yb);
		p.lineTo(xr, yc);
		p.lineTo(xc, yu);
		p.closePath();

		g2.setPaint(Color.WHITE);
		g2.fill(p);
		g2.setPaint(Color.black);
		g2.draw(p);

		g2.setStroke(new BasicStroke(2));
		g2.drawLine(getX() + CONN_SIZE, (int) yc, (int) xc - 1, (int) yc);
	}

	@Override
	public void loadLanguage() {
		I18N.addGate(I18N.ALL, type, I18N.TITLE, "NOT");
		I18N.addGate(I18N.ALL, type, I18N.DESCRIPTION, "NOT Gate (Inverter)");
		I18N.addGate("de", type, I18N.DESCRIPTION, "NOT Gatter (Inverter/Negator)");
		I18N.addGate("es", type, I18N.TITLE, "NOT (Inversor)");
		I18N.addGate("fr", type, I18N.TITLE, "Non (NOT)");
	}
}