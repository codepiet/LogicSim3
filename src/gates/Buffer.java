package gates;

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
	}

	public Buffer(String string) {
		super(string);
	}

	@Override
	public void simulate() {
		super.simulate();
		// call pin directly
		getPin(1).changedLevel(new LSLevelEvent(this, getPin(0).getLevel(), true));
	}

	@Override
	public void changedLevel(LSLevelEvent e) {
		super.changedLevel(e);
		simulate();
	}

	@Override
	protected void drawRotated(Graphics2D g2) {
		String gateType = LSProperties.getInstance().getProperty(LSProperties.GATEDESIGN, LSProperties.GATEDESIGN_IEC);
		if (gateType.equals(LSProperties.GATEDESIGN_ANSI))
			drawANSI(g2);
	}

	@Override
	protected void drawFrame(Graphics2D g2) {
		String gateType = LSProperties.getInstance().getProperty(LSProperties.GATEDESIGN, LSProperties.GATEDESIGN_IEC);
		if (gateType.equals(LSProperties.GATEDESIGN_IEC)) {
			if (getPin(0).getX() == getX() + 10) {
				getPin(0).setX(getPin(0).getX() - 10);
				getPin(1).setX(getPin(1).getX() + 10);
			}
			super.drawFrame(g2);
		}
	}

	private void drawANSI(Graphics2D g2) {
		Path2D p = new Path2D.Double();
		double yu = getY() + CONN_SIZE + 6;
		double xr = getX() + width - 10 - CONN_SIZE + 1;
		double yb = getY() + height - CONN_SIZE - 6;
		double xl = getX() + 10 + CONN_SIZE;
		// check coordinates of pins, x coordinates shoud be a little more inwards
		if (getPin(0).getX() == getX()) {
			getPin(0).setX(getPin(0).getX() + 10);
			getPin(1).setX(getPin(1).getX() - 10);
		}

		p.moveTo(xl, yc);
		p.lineTo(xl, yb);
		p.lineTo(xr, yc);
		p.lineTo(xl, yu);
		p.closePath();

		g2.setPaint(Color.WHITE);
		g2.fill(p);
		g2.setPaint(Color.black);
		g2.draw(p);
	}

	@Override
	public void loadLanguage() {
		I18N.addGate(I18N.ALL, type, I18N.TITLE, "Buffer");
		I18N.addGate(I18N.ALL, type, I18N.DESCRIPTION, "Buffer Gate");
		I18N.addGate("de", type, I18N.DESCRIPTION, "Puffer Gatter");
	}
}