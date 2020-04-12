package gates;

import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Date;

import javax.swing.JOptionPane;

import logicsim.Pin;
import logicsim.Gate;
import logicsim.I18N;

/**
 * Off-Delay component for LogicSim
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class OffDelay extends Gate {
	static final long serialVersionUID = 3185172056863740651L;

	static final String DELAY = "delay";

	static final String DELAY_DEFAULT = "500";

	int delayTime = 1000;

	long startTime;
	boolean lastInputState;

	public OffDelay() {
		super("input");
		type = "offdelay";
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
		if (lastInputState == false && in.isConnected() && in.getLevel()) // positive flanke
			out.setLevel(true);

		if (lastInputState == true && in.isConnected() && !in.getLevel()) // negative flanke
			startTime = new Date().getTime();

		if (new Date().getTime() - startTime > delayTime && in.isConnected() && !in.getLevel())
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
	public void drawFrame(Graphics2D g2) {
		super.drawFrame(g2);
		int cX = width / 2 + getX();
		int cY = height / 2 + getY();
		int cd = 15;
		g2.drawOval(cX - cd / 2, cY - cd / 2 + 5, cd, cd);
		g2.drawLine(getX() + cd, getY() + cd, getX() + width - cd, getY() + cd);
		g2.drawString("1", getX() + cd, getY() + cd + 12);
		g2.drawString("0", getX() + width - cd - 6, getY() + cd + 12);
		Path2D ptr = new Path2D.Double();
		ptr.moveTo(cX, cY);
		ptr.lineTo(cX, cY + 5);
		ptr.lineTo(cX + 3, cY + 5);
		g2.draw(ptr);
	}

	@Override
	public void loadLanguage() {
		I18N.addGate(I18N.ALL, type, I18N.TITLE, "Off Delay");
		I18N.addGate(I18N.ALL, type, I18N.DESCRIPTION, "Delays the signal when turning to LOW");
		I18N.addGate(I18N.ALL, type, DELAY, "Time of delay (ms)");

		I18N.addGate("de", type, I18N.TITLE, "Ausschaltverzögerung");
		I18N.addGate("de", type, I18N.DESCRIPTION, "Verzögert das Low-Signal");
		I18N.addGate("de", type, DELAY, "Verzögerungszeit (ms)");

		I18N.addGate("es", type, I18N.TITLE, "Retardo de 1 a 0");
		I18N.addGate("es", type, DELAY, "Introduce tiempo de retardo (ms)");

		I18N.addGate("fr", type, I18N.TITLE, "Temporisation repos");
		I18N.addGate("fr", type, DELAY, "Décalage (ms)");
	}
}