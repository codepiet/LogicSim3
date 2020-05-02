package gates;

import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import logicsim.Gate;
import logicsim.I18N;

/**
 * T-FlipFlop for LogicSim
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class TFlipFlop extends Gate {

	public TFlipFlop() {
		super("flipflops");
		type = "tff";
		createInputs(1);
		createOutputs(2);
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
	public void reset() {
		getPin(1).setLevel(true);
		getPin(2).setLevel(true);
	}

	@Override
	public void loadLanguage() {
		I18N.addGate(I18N.ALL, type, I18N.TITLE, "T Flip-flop");
		I18N.addGate(I18N.ALL, type, I18N.DESCRIPTION, "T Flip-flop");
		I18N.addGate("de", type, I18N.TITLE, "T Flipflop");
		I18N.addGate("de", type, I18N.DESCRIPTION, "T Flipflop");
		I18N.addGate("es", type, I18N.TITLE, "FlipFlop T");
		I18N.addGate("es", type, I18N.DESCRIPTION, "FlipFlop T");
		I18N.addGate("fr", type, I18N.TITLE, "Bascule T");
		I18N.addGate("fr", type, I18N.DESCRIPTION, "Bascule T");
	}
}