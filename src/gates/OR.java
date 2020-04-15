package gates;

import logicsim.Pin;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import logicsim.Gate;
import logicsim.I18N;
import logicsim.LSLevelEvent;
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
	}

	@Override
	public void simulate() {
		super.simulate();
		boolean oldLevel = getPin(0).getInternalLevel();
		boolean newLevel = false;
		for (Pin c : getInputs()) {
			newLevel = newLevel || c.getLevel();
			if (newLevel)
				break;
		}
		if (newLevel != oldLevel)
			getPin(0).changedLevel(new LSLevelEvent(this, newLevel));
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

	@Override
	public void loadLanguage() {
		I18N.addGate(I18N.ALL, type, I18N.TITLE, "OR");
		I18N.addGate(I18N.ALL, type, I18N.DESCRIPTION, "OR Gate");
		I18N.addGate("de", type, I18N.DESCRIPTION, "OR Gatter (einstellbare Eingangsanzahl)");
		I18N.addGate("es", type, I18N.TITLE, "OR (O)");
		I18N.addGate("fr", type, I18N.TITLE, "Ou (OR)");
	}
}