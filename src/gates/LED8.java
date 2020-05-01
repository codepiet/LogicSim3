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
 * 8-fold LED
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class LED8 extends Gate {

	static final long serialVersionUID = 6576677427368074734L;

	private static final String COLOR = "color";
	private static final String DEFAULT_COLOR = "#ff0000";
	private static final int OVAL_RADIUS = 9;

	private Color color = null;

	public LED8() {
		super("output");
		type = "led8";
		setWidth(20);
		setHeight(90);
		createInputs(8);
		loadProperties();
	}

	protected void setHeight(int i) {
		height = i;
		if (yc == -1)
			yc = getY() + height / 2;
	}

	protected void setWidth(int i) {
		width = i;
		if (xc == -1) {
			xc = getX() + width / 2;
		}
	}

	@Override
	public void changedLevel(LSLevelEvent e) {
		super.changedLevel(e);
		fireRepaint();
	}

	@Override
	protected void loadProperties() {
		color = ColorFactory.web(getPropertyWithDefault(COLOR, DEFAULT_COLOR));
	}

	@Override
	public void drawRotated(Graphics2D g2) {
		for (int i = 0; i < 8; i++) {
			Color c = getPin(i).getLevel() ? color : Color.LIGHT_GRAY;
			g2.setPaint(c);
			g2.fillOval(origx + CONN_SIZE - 1, origy + i * 10 + 6, OVAL_RADIUS, OVAL_RADIUS);
			g2.setPaint(Color.BLACK);
			g2.drawOval(origx + CONN_SIZE - 1, origy + i * 10 + 6, OVAL_RADIUS, OVAL_RADIUS);
		}
	}

	@Override
	public boolean showPropertiesUI(Component frame) {
		super.showPropertiesUI(frame);
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
		I18N.addGate(I18N.ALL, type, I18N.TITLE, "Oct LED");
		I18N.addGate(I18N.ALL, type, I18N.DESCRIPTION, "eight LEDs in one package");
	}
}