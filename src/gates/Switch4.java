package gates;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import logicsim.Pin;
import logicsim.Gate;
import logicsim.LSMouseEvent;
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
				getPin(i).setLevel(!getPin(i).getLevel());
				break;
			}
		}
	}

	@Override
	public void draw(Graphics2D g2) {
		super.draw(g2);

		g2.setStroke(new BasicStroke(1));
		for (int i = 0; i < areaRect.length; i++) {
			WidgetHelper.drawSwitchVertical(g2, areaRect[i], getPin(i).getLevel(), Color.red, Color.LIGHT_GRAY);
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
	public void moveTo(int x, int y) {
		int dx = x - getX();
		int dy = y - getY();
		moveBy(dx, dy);
	}

	@Override
	public void rotate() {
		if (rotate90 == 0) {
			rotate90 = 2;
			for (Pin c : getOutputs()) {
				c.setY(getY());
				c.paintDirection = Pin.DOWN;
			}
		} else {
			rotate90 = 0;
			for (Pin c : getOutputs()) {
				c.setY(getY() + height);
				c.paintDirection = Pin.UP;
			}
		}
	}
}