package gates;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.Date;

import javax.swing.JOptionPane;

import logicsim.Pin;
import logicsim.Gate;
import logicsim.I18N;
import logicsim.LSMouseEvent;
import logicsim.Log;
import logicsim.WidgetHelper;

/**
 * Clock Generator for LogicSim
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class CLK extends Gate {
	static final long serialVersionUID = 3971572931629721831L;

	static final int PAUSE = 0;
	static final int RUNNING = 1;
	static final int PULSE = 2;

	static final String HT = "hightime";
	static final String LT = "lowtime";

	static final String HT_DEFAULT = "500";
	static final String LT_DEFAULT = "500";

	Rectangle auto = new Rectangle(39, 44, 30, 15);
	Rectangle manual = new Rectangle(11, 44, 15, 15);
	Rectangle oszi = new Rectangle(11, 20, 59, 21);

	boolean[] osz = new boolean[oszi.width + 1];

	int currentMode = PAUSE;
	long lastTime;
	int highTime = 500;
	int lowTime = 500;
	int pos = 0;

	public CLK() {
		super("input");
		type = "clock";
		width = 80;
		height = 70;
		createOutputs(1);
		loadProperties();
	}

	@Override
	protected void loadProperties() {
		highTime = Integer.parseInt(getPropertyWithDefault(HT, HT_DEFAULT));
		lowTime = Integer.parseInt(getPropertyWithDefault(LT, LT_DEFAULT));
	}

	@Override
	public void mousePressedUI(LSMouseEvent e) {
		super.mousePressedUI(e);

		int dx = e.getX() - getX();
		int dy = e.getY() - getY();

		Log.getInstance().print("p " + dx + "/" + dy);

		if (manual.contains(dx, dy) && currentMode != RUNNING) {
			currentMode = PULSE;
			lastTime = 0;
			getPin(0).setLevel(true);
		} else if (auto.contains(dx, dy)) {
			currentMode = 1 - currentMode;
		}
	}

	public void simulate() {
		// advance oszilloscope's position
		pos++;

		// reset data array
		if (pos > 59) {
			pos = 0;
			osz = new boolean[oszi.width + 1];
		}

		Pin cout = getPin(0);
		boolean out = cout.getLevel();
		if (currentMode == RUNNING) {
			if (lastTime == 0)
				lastTime = new Date().getTime();
			if (!out && new Date().getTime() - lastTime > lowTime) {
				cout.setLevel(true);
				lastTime = new Date().getTime();
			} else if (out && new Date().getTime() - lastTime > highTime) {
				cout.setLevel(false);
				lastTime = new Date().getTime();
			}
		} else if (currentMode == PULSE) {
			if (lastTime == 0)
				lastTime = new Date().getTime();
			if (out && new Date().getTime() - lastTime > highTime) {
				cout.setLevel(false);
				currentMode = PAUSE;
			}
		}
		osz[pos] = cout.getLevel();
	}

	@Override
	public void draw(Graphics2D g2) {
		super.draw(g2);
		int x = getX();
		int y = getY();

		g2.setPaint(Color.black);
		g2.setFont(Pin.smallFont);
		String s = "CLK";
		int sw = g2.getFontMetrics().stringWidth(s);
		g2.drawString(s, x + getWidth() / 2 - sw / 2, y + 18);

		g2.setStroke(new BasicStroke(1));
		WidgetHelper.drawPushSwitch(g2, x + manual.x, y + manual.y, manual.width,
				currentMode == PULSE ? Color.red : Color.LIGHT_GRAY, "1");
		Rectangle r = new Rectangle(auto.x + getX(), auto.y + getY(), auto.width, auto.height);
		WidgetHelper.drawSwitchHorizontal(g2, r, currentMode == RUNNING, Color.RED, Color.LIGHT_GRAY);

//		if (currentMode == PAUSE) {
//			g2.setPaint(Color.blue);
//			g2.fillRect(x + auto.x, y + auto.y, 6, auto.height);
//			g2.fillRect(x + auto.x + 8, y + auto.y, 6, auto.height);
//		} else {
//			g2.setPaint(Color.red);
//			Rectangle rect = auto;
//			Polygon p = new Polygon();
//			p.addPoint(x + rect.x + rect.width, y + rect.y + rect.height / 2);
//			p.addPoint(x + rect.x, y + rect.y);
//			p.addPoint(x + rect.x, y + rect.y + rect.height);
//			g2.fill(p);
//		}

		// oszi
		g2.setPaint(Color.DARK_GRAY);
		g2.fillRect(x + oszi.x, y + oszi.y, oszi.width, oszi.height);
		// oszi line
		g2.setPaint(Color.green);
		g2.setStroke(new BasicStroke(1));
		boolean level1 = osz[0];
		boolean level2;
		for (int i = 1; i < pos; i++) {
			level2 = osz[i];
			g2.drawLine(x + oszi.x + i, y + oszi.y + 3 + 12 * (level1 ? 0 : 1), x + oszi.x + i,
					y + oszi.y + 3 + 12 * (level2 ? 0 : 1));
			level1 = level2;
		}
	}

	public boolean hasPropertiesUI() {
		return true;
	}

	public boolean showPropertiesUI(Component frame) {
		String h = (String) JOptionPane.showInputDialog(frame, I18N.getString(type, "ui.entermessagehigh"),
				I18N.getString(type, "ui.title"), JOptionPane.QUESTION_MESSAGE, null, null,
				Integer.toString((int) highTime));
		if (h != null && h.length() > 0) {
			highTime = Integer.parseInt(h);
			setPropertyInt(HT, highTime);
		}
		h = (String) JOptionPane.showInputDialog(frame, I18N.getString(type, "ui.entermessagelow"),
				I18N.getString(type, "ui.title"), JOptionPane.QUESTION_MESSAGE, null, null,
				Integer.toString((int) lowTime));
		if (h != null && h.length() > 0) {
			lowTime = Integer.parseInt(h);
			setPropertyInt(LT, lowTime);
		}
		return true;
	}

}