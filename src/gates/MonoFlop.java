package gates;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import javax.swing.JOptionPane;

import logicsim.Gate;
import logicsim.I18N;
import logicsim.LSLevelEvent;
import logicsim.Lang;

/**
 * MonoFlop for LogicSim
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class MonoFlop extends Gate implements Runnable {

	static final long serialVersionUID = -6063406618533983926L;

	private Thread thread;

	static final String HT = "hightime";

	static final String HT_DEFAULT = "1000";

	int highTime;

	public MonoFlop() {
		super("flipflops");
		type = "monoflop";
		createInputs(1);
		createOutputs(1);
		reset();
		loadProperties();
	}

	@Override
	protected void loadProperties() {
		highTime = Integer.parseInt(getPropertyWithDefault(HT, HT_DEFAULT));
	}

	@Override
	public void changedLevel(LSLevelEvent e) {
		if (e.source.equals(getPin(0))) {
			if (e.level == HIGH) {
				LSLevelEvent evt = new LSLevelEvent(this, HIGH);
				getPin(1).changedLevel(evt);
				fireRepaint();
				// rising edge detection of input
				thread = new Thread(this);
				thread.setPriority(Thread.MIN_PRIORITY);
				thread.start();
			}
		}
	}

	@Override
	public void run() {
		try {
			Thread.sleep(highTime);
		} catch (InterruptedException e) {
		}
		LSLevelEvent evt = new LSLevelEvent(this, LOW);
		getPin(1).changedLevel(evt);
		fireRepaint();
	}

	@Override
	public void drawRotated(Graphics2D g2) {
		g2.setPaint(Color.black);
		int middleX = getX() + getWidth() / 2;
		int middleY = getY() + getHeight() / 2;
		Path2D path = new Path2D.Double();
		path.moveTo(middleX - 10, middleY + 5);
		path.lineTo(middleX - 3, middleY + 5);
		path.lineTo(middleX - 3, middleY - 5);
		path.lineTo(middleX + 7, middleY - 5);
		path.lineTo(middleX + 7, middleY + 5);
		path.lineTo(middleX + 14, middleY + 5);
		g2.draw(path);
		g2.drawString("1", middleX - 20, middleY + 5);
	}

	@Override
	public boolean showPropertiesUI(Component frame) {
		super.showPropertiesUI(frame);
		String h = (String) JOptionPane.showInputDialog(frame, I18N.getString(type, HT), I18N.tr(Lang.SETTINGS),
				JOptionPane.QUESTION_MESSAGE, null, null, Integer.toString((int) highTime));
		if (h != null && h.length() > 0) {
			highTime = Integer.parseInt(h);
			setPropertyInt(HT, highTime);
		}
		return true;
	}

	@Override
	public void loadLanguage() {
		I18N.addGate(I18N.ALL, type, I18N.TITLE, "Monoflop");
		I18N.addGate(I18N.ALL, type, I18N.DESCRIPTION, "Monoflop");
		I18N.addGate(I18N.ALL, type, HT, "Time High-Level (ms)");
		I18N.addGate("de", type, HT, "Dauer High-Pegel (ms)");
		I18N.addGate("es", type, I18N.TITLE, "Monoestable");
		I18N.addGate("es", type, I18N.DESCRIPTION, "Monoestable");
		I18N.addGate("fr", type, I18N.TITLE, "Monostable");
		I18N.addGate("fr", type, I18N.DESCRIPTION, "Monostable");
	}
}