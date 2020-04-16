package gates;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import logicsim.Gate;
import logicsim.I18N;
import logicsim.LSLevelEvent;
import logicsim.LSProperties;

/**
 * Buffer Gate for LogicSim
 * 
 * @author Peter Gabriel
 * @version 1.0
 */
public class Buffer extends Gate {
	static final long serialVersionUID = 3351085067064933298L;

	public Buffer() {
		super("basic");
		label = "1";
		type = "buffer";
		createInputs(1);
		createOutputs(1);
		simulate();
	}

	public Buffer(String string) {
		super(string);
	}

	@Override
	public void simulate() {
		super.simulate();
		// boolean oldLevel = getPin(1).getInternalLevel();
		// call pin directly
		getPin(1).changedLevel(new LSLevelEvent(this, getPin(0).getLevel()));
	}

	@Override
	public void changedLevel(LSLevelEvent e) {
		super.changedLevel(e);
		if (busted)
			return;
		simulate();
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
		I18N.addGate(I18N.ALL, type, I18N.TITLE, "Buffer");
		I18N.addGate(I18N.ALL, type, I18N.DESCRIPTION, "Buffer Gate");
		I18N.addGate("de", type, I18N.DESCRIPTION, "Puffer Gatter");
	}
}