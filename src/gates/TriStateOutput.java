package gates;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import logicsim.Gate;
import logicsim.I18N;
import logicsim.LSLevelEvent;
import logicsim.LSProperties;
import logicsim.Pin;

/**
 * Tri-State-Output
 * 
 * @author Peter Gabriel
 * @version 1.0
 */
public class TriStateOutput extends Gate {
	static final long serialVersionUID = 4521959944440523564L;

	public TriStateOutput() {
		super("output");
		type = "triout";
		createInputs(2);
		createOutputs(1);
		width = 40;
		height = 40;

		getPin(0).moveTo(getX() + 20, getY());
		getPin(0).setDirection(Pin.DOWN);
		getPin(1).moveTo(getX(), getY() + 20);
		getPin(2).moveTo(getX() + 40, getY() + 20);
	}

	@Override
	public void changedLevel(LSLevelEvent e) {
		// pin 0 - switching pin - switches between output and highimp
		// pin 1 - input
		// pin 2 - output
		if (e.source == getPin(0)) {
			if (e.level == HIGH) {
				getPin(2).ioType = Pin.OUTPUT;
				getPin(2).changedLevel(new LSLevelEvent(this, getPin(1).getLevel(), true));
			} else {
				// level of switching pin is low -> send low
				getPin(2).ioType = Pin.HIGHIMP;
				getPin(2).changedLevel(new LSLevelEvent(this, LOW));
			}
		} else if (e.source == getPin(1)) {
			if (getPin(2).isOutput())
				getPin(2).changedLevel(new LSLevelEvent(this, getPin(1).getLevel()));
		}
	}

	@Override
	protected void drawFrame(Graphics2D g2) {
		String gateType = LSProperties.getInstance().getProperty(LSProperties.GATEDESIGN, LSProperties.GATEDESIGN_IEC);
		if (gateType.equals(LSProperties.GATEDESIGN_IEC))
			super.drawFrame(g2);
	}

	@Override
	protected void drawRotated(Graphics2D g2) {
		String gateType = LSProperties.getInstance().getProperty(LSProperties.GATEDESIGN, LSProperties.GATEDESIGN_IEC);
		if (gateType.equals(LSProperties.GATEDESIGN_ANSI)) {
			g2.setStroke(new BasicStroke(3));
			g2.setColor(getPin(0).getLevel() ? Color.red : Color.black);
			g2.drawLine(xc, yc - 15, xc, yc - 10);
			// g2.drawLine(getPin(0).getX(), getPin(0).getY() + 5, getPin(0).getX(),
			// getPin(0).getY() + 10);

			Path2D p = new Path2D.Double();
			double yu = getY() + 3;
			double xr = getX() + width - CONN_SIZE + 1;
			double yb = getY() + height - 3;
			double xl = getX() + CONN_SIZE;

			p.moveTo(xl, yc);
			p.lineTo(xl, yb);
			p.lineTo(xr, yc);
			p.lineTo(xl, yu);
			p.closePath();

			g2.setStroke(new BasicStroke(1));
			g2.setPaint(Color.WHITE);
			g2.fill(p);
			g2.setPaint(Color.black);
			g2.draw(p);
		}
	}

	@Override
	public void loadLanguage() {
		I18N.addGate(I18N.ALL, type, I18N.TITLE, "Tri-State Output");
		I18N.addGate(I18N.ALL, type, I18N.DESCRIPTION, "Tri-State Output");
		I18N.addGate("de", type, I18N.TITLE, "Tri-State Output");
		I18N.addGate("de", type, I18N.DESCRIPTION, "Schaltbarer Ausgang - hochohmig, wenn nicht geschaltet");
	}

}