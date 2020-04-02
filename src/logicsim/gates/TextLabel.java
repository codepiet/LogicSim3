package logicsim.gates;

import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

import javax.swing.JOptionPane;

import logicsim.Gate;
import logicsim.I18N;

/**
 * Text Label component for LogicSim
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class TextLabel extends Gate {
	static final long serialVersionUID = 6576677427368074734L;

	static final String TEXT = "text";

	static final String TEXT_DEFAULT = "Text";

	String text;

	public TextLabel() {
		super("output");
		type = "label";
		width = 60;
		height = 20;
		setNumInputs(0);
		setNumOutputs(0);
		drawFrame = false;
		loadProperties();
	}

	@Override
	protected void loadProperties() {
		text = getPropertyWithDefault(TEXT, TEXT_DEFAULT);
	}

	@Override
	public boolean insideFrame(int mx, int my) {
		return getBoundingBox().contains(mx, my);
	}

	@Override
	protected void drawActiveFrame(Graphics2D g2) {
		g2.setFont(bigFont);
		FontMetrics fm = g2.getFontMetrics();
		int stringWidth = fm.stringWidth(text);
		int stringHeight = fm.getHeight();

		width = stringWidth;
		height = stringHeight;

		super.drawActiveFrame(g2);
	}

	@Override
	public void draw(Graphics2D g2) {
		super.draw(g2);
		g2.setFont(bigFont);
		g2.setColor(Color.black);

		FontMetrics fm = g2.getFontMetrics();
		int stringWidth = fm.stringWidth(text);
		int stringHeight = fm.getHeight();

		width = stringWidth;
		height = stringHeight;

		g2.drawString(text, getX() + getWidth() / 2 - width / 2, getY() + getHeight() / 2 + 4);

	}

	public boolean hasPropertiesUI() {
		return true;
	}

	public boolean showPropertiesUI(Component frame) {
		String h = (String) JOptionPane.showInputDialog(frame, I18N.getString(type, "ui.text"),
				I18N.getString(type, "ui.title"), JOptionPane.QUESTION_MESSAGE, null, null, text);
		if (h != null && h.length() > 0) {
			text = h;
			setProperty(TEXT, text);
		}
		return true;
	}
}