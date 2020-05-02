package gates;

import logicsim.Pin;

import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import logicsim.Gate;
import logicsim.I18N;

/**
 * JKMS-Flipflop for LogicSim
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class JKMSFlipFlop extends Gate {
	static final long serialVersionUID = 6562388223937836948L;

	public JKMSFlipFlop() {
		super("flipflops");
		type = "jkmsff";
		createInputs(3);
		createOutputs(2);

		getPin(0).setProperty(TEXT, "J");
		getPin(1).setProperty(TEXT, Pin.POS_EDGE_TRIG);
		getPin(2).setProperty(TEXT, "K");

		getPin(3).setProperty(TEXT, "Q");
		getPin(4).setProperty(TEXT, "/Q");

		getPin(3).moveBy(0, 10);
		getPin(4).moveBy(0, -10);

		reset();
	}

	@Override
	public void drawRotated(Graphics2D g2) {
		Path2D p = new Path2D.Double();
		int sp = 10;
		p.moveTo(getX() + width - 2 * sp, getY() + height - 2 * sp);
		p.lineTo(getX() + width - sp, getY() + height - 2 * sp);
		p.lineTo(getX() + width - sp, getY() + height - sp);

		p.moveTo(getX() + width - 2 * sp, getY() + sp);
		p.lineTo(getX() + width - sp, getY() + sp);
		p.lineTo(getX() + width - sp, getY() + 2 * sp);
		g2.draw(p);
	}

	@Override
	public void loadLanguage() {
		I18N.addGate(I18N.ALL, type, I18N.TITLE, "JKMS Flip-flop");
		I18N.addGate(I18N.ALL, type, I18N.DESCRIPTION, "JKMS Flip-flop");
		I18N.addGate("de", type, I18N.TITLE, "JKMS Flipflop");
		I18N.addGate("de", type, I18N.DESCRIPTION, "JKMS Flipflop");
		I18N.addGate("es", type, I18N.TITLE, "FlipFlop JKMS");
		I18N.addGate("es", type, I18N.DESCRIPTION, "FlipFlop JKMS");
		I18N.addGate("fr", type, I18N.TITLE, "Bascule JKMS");
		I18N.addGate("fr", type, I18N.DESCRIPTION, "Bascule JKMS");
	}
}