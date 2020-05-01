package gates;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;

import logicsim.Gate;
import logicsim.I18N;
import logicsim.LSLevelEvent;
import logicsim.LSMouseEvent;
import logicsim.Pin;
import logicsim.WidgetHelper;

/**
 * 4-fold Switch Component for LogicSim
 * 
 * @author Peter Gabriel
 * @version 2.0
 */
public class Switch4 extends Gate {
	static final long serialVersionUID = 2459367526586913840L;

	long clickCountDown = 0;
	boolean mouseDown = false;

	Rectangle[] areaRect;

	public Switch4() {
		super("input");
		type = "switch4";
		width = 80;
		height = 40;
		createOutputs(4);

		for (int i = 0; i < 4; i++) {
			getPin(i).setDirection(Pin.UP);
			getPin(i).moveTo(getX() + i * 20 + 10, getY() + getHeight());
		}

		reset();
		areaRect = new Rectangle[4];
		Rectangle r;
		for (int i = 0; i < 4; i++) {
			Pin conn = getPin(i);
			r = new Rectangle(conn.getX() - 6, getY() + 6, 13, 28);
			areaRect[i] = r;
		}
	}

	@Override
	public void mousePressedSim(LSMouseEvent e) {
		super.mousePressedSim(e);

		int mx = e.getX();
		int my = e.getY();
		for (int i = 0; i < areaRect.length; i++) {
			if (areaRect[i].contains(mx, my)) {
				getPin(i).changedLevel(new LSLevelEvent(this, !getPin(i).getLevel()));
				break;
			}
		}
	}

	@Override
	public void draw(Graphics2D g2) {
		super.draw(g2);
		g2.setStroke(new BasicStroke(1));
		for (int i = 0; i < areaRect.length; i++) {
			if (areaRect[i].width < areaRect[i].height)
				WidgetHelper.drawSwitchVertical(g2, areaRect[i], getPin(i).getLevel(), Color.red, Color.LIGHT_GRAY);
			else
				WidgetHelper.drawSwitchHorizontal(g2, areaRect[i], getPin(i).getLevel(), Color.red, Color.LIGHT_GRAY);
		}
	}

	@Override
	public void moveBy(int dx, int dy) {
		super.moveBy(dx, dy);
		for (int i = 0; i < areaRect.length; i++) {
			areaRect[i].x = areaRect[i].x + dx;
			areaRect[i].y = areaRect[i].y + dy;
		}
	}

	@Override
	public void rotate() {
		super.rotate();
		Point ctr = new Point(xc, yc);
		for (int i = 0; i < 4; i++) {
			Rectangle r = areaRect[i];
			r = WidgetHelper.rotateRectangle(r, ctr);
			areaRect[i] = r;
		}
	}

	@Override
	public void loadLanguage() {
		I18N.addGate(I18N.ALL, type, I18N.TITLE, "DIP-Switch (4-fold)");
		I18N.addGate(I18N.ALL, type, I18N.DESCRIPTION, "4 Toggle Switches in one package");

		I18N.addGate("de", type, I18N.TITLE, "DIP-Schalter (4-fach)");
		I18N.addGate("de", type, I18N.DESCRIPTION, "4 Umschalter in einer Einheit - ähnlich DIP Schalter");
	}
}