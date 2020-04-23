package gates;

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

	/*String TEXT_DEFAULT = "<Text>";*/

	public TextLabel() {
		super("output");
		type = "label";
		width = 60;
		height = 20;
		loadProperties();
	}

	/*@Override
	protected void loadProperties() {
		text = getPropertyWithDefault(TEXT, TEXT_DEFAULT);
	}*/
	
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
	protected void drawFrame(Graphics2D g2) {
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

	/*public boolean hasPropertiesUI() {
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
	}*/

	@Override
	public void loadLanguage() {
		I18N.addGate(I18N.ALL, type, I18N.TITLE, "Textfield");
		I18N.addGate(I18N.ALL, type, I18N.DESCRIPTION,
				"Textfield just for displaying text on the drawing surface - for documentation purposes");
		I18N.addGate(I18N.ALL, type, TEXT, "input Text");

		I18N.addGate("de", type, I18N.TITLE, "Textfeld");
		I18N.addGate("de", type, I18N.DESCRIPTION, "Textfeld für Dokumentationszwecke oder Hilfetexte");
		I18N.addGate("de", type, TEXT, "Text eingeben");

		I18N.addGate("es", type, I18N.TITLE, "Etiqueta de texto");

		I18N.addGate("fr", type, I18N.TITLE, "Étiquette");
		I18N.addGate("fr", type, TEXT, "Entrer le texte");

	}
}