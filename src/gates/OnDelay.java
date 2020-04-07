package gates;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Date;

import javax.swing.JOptionPane;

import logicsim.Gate;
import logicsim.I18N;
import logicsim.Pin;

/**
 * ON-Delay component for LogicSim
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class OnDelay extends Gate {
	static final long serialVersionUID = -2350098633141393951L;

	static final String DELAY = "delay";

	static final String DELAY_DEFAULT = "500";

	transient long startTime;
	transient boolean lastInputState;
	int delayTime = 1000;

	public OnDelay() {
		super("input");
		type = "ondelay";
		createInputs(1);
		createOutputs(1);
		loadProperties();
	}

	@Override
	protected void loadProperties() {
		delayTime = Integer.parseInt(getPropertyWithDefault(DELAY, DELAY_DEFAULT));
	}

	public void simulate() {
		Pin in = getPin(1);
		Pin out = getPin(2);

		if (lastInputState == false && in.isConnected() && in.getLevel()) { // positive flanke
			startTime = new Date().getTime();
		}

		if (new Date().getTime() - startTime > delayTime && in.isConnected() && in.getLevel())
			out.setLevel(true);

		if (in.isConnected() || !in.getLevel())
			out.setLevel(false);

		if (in.isConnected())
			lastInputState = in.getLevel();
	}

	public boolean hasPropertiesUI() {
		return true;
	}

	public boolean showPropertiesUI(Component frame) {
		String h = (String) JOptionPane.showInputDialog(frame, I18N.getString(type, "ui.time"),
				I18N.getString(type, "ui.title"), JOptionPane.QUESTION_MESSAGE, null, null,
				Integer.toString((int) delayTime));
		if (h != null && h.length() > 0) {
			delayTime = Integer.parseInt(h);
			setPropertyInt(DELAY, delayTime);
		}
		return true;
	}

	@Override
	protected void drawFrame(Graphics2D g2) {
		super.drawFrame(g2);
		int cX = width / 2 + getX();
		int cY = height / 2 + getY();
		int cd = 15;
		g2.drawOval(cX - cd / 2, cY - cd / 2 + 5, cd, cd);
		g2.drawLine(getX() + cd, getY() + cd, getX() + width - cd, getY() + cd);
		g2.drawString("0", getX() + cd, getY() + cd + 12);
		g2.drawString("1", getX() + width - cd - 6, getY() + cd + 12);
		Path2D ptr = new Path2D.Double();
		ptr.moveTo(cX, cY);
		ptr.lineTo(cX, cY + 5);
		ptr.lineTo(cX + 3, cY + 5);
		g2.draw(ptr);
	}

}