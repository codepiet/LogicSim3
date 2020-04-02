package logicsim.gates;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import logicsim.Connector;
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

	/*
	 * Ein Klick-Button bleibt nur so lange an, wie der Nutzer den Mausknopf
	 * gedr�ckt h�lt, aber mindestens 2 Simulations-Zyklen. Daf�r wird beim Klick
	 * der Countdown auf 2 gesetzt und in jedem Zyklus heruntergez�hlt. Der Button
	 * geht aus, wenn der Countdown auf 0 ist und die Maustaste losgelassen wurde.
	 */
	long clickCountDown = 0;
	boolean mouseDown = false; // true solange der User die Maus �ber dem Gatter gedr�ckt h�lt

	Rectangle[] areaRect, switchRect;

	public Switch4() {
		super("input");
		type = "switch4";
		width = 80;
		height = 40;
		setNumOutputs(4);

		getOutput(0).setDirection(Connector.UP);
		getOutput(1).setDirection(Connector.UP);
		getOutput(2).setDirection(Connector.UP);
		getOutput(3).setDirection(Connector.UP);
		getOutput(0).moveTo(getX() + 10, getY() + getHeight());
		getOutput(1).moveTo(getX() + 30, getY() + getHeight());
		getOutput(2).moveTo(getX() + 50, getY() + getHeight());
		getOutput(3).moveTo(getX() + 70, getY() + getHeight());

		reset();
		areaRect = new Rectangle[4];
		switchRect = new Rectangle[4];
		Rectangle r;
		for (int i = 0; i < 4; i++) {
			Connector conn = getOutput(i);
			r = new Rectangle(conn.getX() - 6, getY() + 3, 13, 27);
			areaRect[i] = r;
			r = new Rectangle(conn.getX() - 6, getY() + 3, 13, 13);
			switchRect[i] = r;
		}
	}

	@Override
	protected void drawFrame(Graphics2D g2) {
		Rectangle2D border = new Rectangle2D.Double(getX(), getY(), width, height - CONN_SIZE);
		g2.setPaint(Color.white);
		g2.fill(border);
		g2.setPaint(Color.black);
		g2.setStroke(new BasicStroke(1));
		g2.draw(border);
		drawLabel(g2, label, bigFont);
	}

	@Override
	public void mousePressedUI(LSMouseEvent e) {
		super.mousePressedUI(e);

		int mx = e.getX();
		int my = e.getY();
		for (int i = 0; i < areaRect.length; i++) {
			if (areaRect[i].contains(mx, my)) {
				setOutputLevel(i, !getOutputLevel(i));
				switchRect[i].y = getOutputLevel(i) ? getY() + 17 : getY() + 3;
				break;
			}
		}
	}

	@Override
	public void draw(Graphics2D g2) {
		super.draw(g2);

		g2.setStroke(new BasicStroke(1));
		for (int i = 0; i < areaRect.length; i++) {
			WidgetHelper.drawSwitchVertical(g2, areaRect[i], getOutputLevel(i), Color.red, Color.LIGHT_GRAY);
		}
	}

	@Override
	public void moveBy(int dx, int dy) {
		super.moveBy(dx, dy);
		for (int i = 0; i < areaRect.length; i++) {
			areaRect[i].x = areaRect[i].x + dx;
			areaRect[i].y = areaRect[i].y + dy;
			switchRect[i].x = switchRect[i].x + dx;
			switchRect[i].y = switchRect[i].y + dy;
		}
	}

	@Override
	public void moveTo(int x, int y) {
		int dx = x - getX();
		int dy = y - getY();
		moveBy(dx, dy);
	}
}