package gates;

import java.awt.Color;
import java.awt.Graphics2D;

import logicsim.ColorFactory;
import logicsim.Gate;
import logicsim.I18N;
import logicsim.LSLevelEvent;
import logicsim.LSMouseEvent;

/**
 * 8-fold Switch Component for LogicSim
 * 
 * @author Peter Gabriel
 * @version 1.0
 */
public class Switch8 extends Gate {
	static final long serialVersionUID = 2459367526586913840L;

	private static final String COLOR = "color";
	private static final int OVAL_RADIUS = 9;
	private static final String DEFAULT_COLOR = "#ff0000";

	private Color color = null;

	public Switch8() {
		super("input");
		type = "switch8";
		setWidth(20);
		setHeight(90);
		createOutputs(8);
		loadProperties();
	}

	@Override
	public void mousePressedSim(LSMouseEvent e) {
		super.mousePressedSim(e);

		int mx = e.getX();
		int my = e.getY();
		int pinnr = -1;
		if (rotate90 == 0 || rotate90 == 2) {
			if (mx > getX() + CONN_SIZE - 1 && mx < getX() + CONN_SIZE - 1 + OVAL_RADIUS) {
				my = my - getY() - 5;
				my = my / 10;
				if (my >= 0 && my <= 7) {
					pinnr = my;
					if (rotate90 > 1)
						pinnr = 7 - pinnr;
				}
			}
		} else {
			// rotate is 1 or 3
			if (my > getY() + CONN_SIZE - 1 && my < getY() + CONN_SIZE - 1 + OVAL_RADIUS) {
				mx = mx - getX() - 5;
				mx = mx / 10;
				if (mx >= 0 && mx <= 7) {
					pinnr = mx;
					if (rotate90 < 2)
						pinnr = 7 - pinnr;
				}
			}
		}
		if (pinnr > -1) {
			getPin(pinnr).changedLevel(new LSLevelEvent(this, !getPin(pinnr).getLevel()));
		}
	}

	@Override
	public void interact() {
		int value = 0;
		for (int i = 0; i < 8; i++) {
			value = value + ((int) Math.pow(2, i) * (getPin(i).getLevel() ? 1 : 0));
		}
		value++;
		for (int i = 0; i < 8; i++) {
			int pow = (int) Math.pow(2, i);
			LSLevelEvent evt = new LSLevelEvent(this, (value & pow) == pow);
			getPin(i).changedLevel(evt);
		}
	}

	@Override
	public void drawRotated(Graphics2D g2) {
		for (int i = 0; i < 8; i++) {
			Color c = getPin(i).getLevel() ? color : Color.LIGHT_GRAY;
			g2.setPaint(c);
			g2.fillRect(origx + CONN_SIZE - 1, origy + i * 10 + 6, OVAL_RADIUS, OVAL_RADIUS);
			g2.setPaint(Color.BLACK);
			g2.drawRect(origx + CONN_SIZE - 1, origy + i * 10 + 6, OVAL_RADIUS, OVAL_RADIUS);
		}
	}

	@Override
	protected void drawFrame(Graphics2D g2) {
	}

	@Override
	protected void loadProperties() {
		color = ColorFactory.web(getPropertyWithDefault(COLOR, DEFAULT_COLOR));
	}

	@Override
	public void loadLanguage() {
		I18N.addGate(I18N.ALL, type, I18N.TITLE, "Switch (8-fold)");
		I18N.addGate(I18N.ALL, type, I18N.DESCRIPTION, "8 Toggle Switches in one package");

		I18N.addGate("de", type, I18N.TITLE, "Schalter (8-fach)");
		I18N.addGate("de", type, I18N.DESCRIPTION, "8 Umschalter in einer Einheit - ähnlich DIP Schalter");
	}
}