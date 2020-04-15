package gates;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;

import javax.swing.JColorChooser;

import logicsim.ColorFactory;
import logicsim.Gate;
import logicsim.I18N;
import logicsim.LSLevelEvent;
import logicsim.Lang;

/**
 * LED for LogicSim
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class LED extends Gate {

	static final long serialVersionUID = 6576677427368074734L;

	private static final String COLOR = "color";
	private static final String DEFAULT_COLOR = "#ff0000";

	private Color color = null;

	private boolean level;

	public LED() {
		super("output");
		type = "led";
		width = 40;
		height = 40;
		createInputs(1);
		variableInputCountSupported = false;
		loadProperties();
		reset();
	}

	@Override
	public void changedLevel(LSLevelEvent e) {
		super.changedLevel(e);
		if (level != e.level) {
			level = e.level;
			fireRepaint();
		}
	}

	@Override
	protected void loadProperties() {
		color = ColorFactory.web(getPropertyWithDefault(COLOR, DEFAULT_COLOR));
	}

	@Override
	public void draw(Graphics2D g) {
		super.draw(g);
		int x = getX();
		int y = getY();

		Color c = Color.LIGHT_GRAY;

		int ovalCenterY = y + getHeight() / 2;
		int ovalRadius = 14;

		int y1 = ovalCenterY - ovalRadius;

		if (getPin(0).getLevel()) {
			c = color;
		}
		g.setPaint(c);
		g.fillOval(x + CONN_SIZE - 1, y1, ovalRadius * 2, ovalRadius * 2);
		g.setPaint(Color.BLACK);
		g.drawOval(x + CONN_SIZE - 1, y1, ovalRadius * 2, ovalRadius * 2);
	}

	@Override
	public boolean hasPropertiesUI() {
		return true;
	}

	@Override
	public boolean showPropertiesUI(Component frame) {
		Color newColor = JColorChooser.showDialog(null, I18N.getString(type, I18N.TITLE) + " " + I18N.tr(Lang.SETTINGS),
				color);
		if (newColor != null)
			color = newColor;
		setProperty(COLOR, "#" + Integer.toHexString(color.getRGB()).substring(2));
		return true;
	}

	@Override
	protected void drawFrame(Graphics2D g2) {
	}

	@Override
	public void loadLanguage() {
		I18N.addGate(I18N.ALL, type, I18N.TITLE, "LED");
	}
}