package logicsim.gates;

import java.awt.Component;
import java.util.Date;

import javax.swing.JOptionPane;

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
		setNumInputs(1);
		setNumOutputs(1);
		loadProperties();
	}

	@Override
	protected void loadProperties() {
		delayTime = Integer.parseInt(getPropertyWithDefault(DELAY, DELAY_DEFAULT));
	}

	public void simulate() {
		if (lastInputState == false && getInputWire(0) != null && getInputLevel(0)) // positive flanke
			setOutputLevel(0, true);

		if (lastInputState == true && getInputWire(0) != null && getInputLevel(0) == false) // negative flanke
			startTime = new Date().getTime();

		if (new Date().getTime() - startTime > delayTime && getInputWire(0) != null && getInputLevel(0) == false)
			setOutputLevel(0, false);

		if (getInputWire(0) != null)
			lastInputState = getInputLevel(0);
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

}