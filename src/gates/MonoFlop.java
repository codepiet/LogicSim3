package gates;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.Date;

import javax.swing.JOptionPane;

import logicsim.Gate;
import logicsim.I18N;

/**
 * MonoFlop for LogicSim
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class MonoFlop extends Gate {
	static final long serialVersionUID = -6063406618533983926L;

	long startTime;
	boolean lastState;

	static final String HT = "hightime";

	static final String HT_DEFAULT = "500";

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
	public void draw(Graphics2D g2) {
		super.draw(g2);
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
	public void simulate() {
		boolean in = getPin(0).getLevel();
		// detect rising edge
		if (lastState == false && in) {
			getPin(1).setLevel(true);
			startTime = new Date().getTime();
		}

		if (new Date().getTime() - startTime > highTime)
			getPin(1).setLevel(false);

		lastState = in;
	}

	@Override
	public boolean hasPropertiesUI() {
		return true;
	}

	@Override
	public boolean showPropertiesUI(Component frame) {
		String h = (String) JOptionPane.showInputDialog(frame, I18N.getString(type, "ui.entermessagehigh"),
				I18N.getString(type, "ui.title"), JOptionPane.QUESTION_MESSAGE, null, null,
				Integer.toString((int) highTime));
		if (h != null && h.length() > 0) {
			highTime = Integer.parseInt(h);
			setPropertyInt(HT, highTime);
		}
		return true;
	}

	@Override
	public void loadLanguage() {
		I18N.addGate(I18N.ALL, type, I18N.TITLE, "Monoflop");
		I18N.addGate(I18N.ALL, type, HT, "Time High-Level (ms)");
		I18N.addGate("de", type, HT, "Dauer High-Pegel (ms)");
		I18N.addGate("es", type, I18N.TITLE, "Monoestable");
		I18N.addGate("fr", type, I18N.TITLE, "Monostable");
	}
}