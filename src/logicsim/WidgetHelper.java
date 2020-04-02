package logicsim;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

public class WidgetHelper {

	private static Stroke oldStroke;
	private static Font oldFont;
	private static Color oldColor;

	public static void drawSwitchHorizontal(Graphics2D g2, Rectangle rect, boolean on, Color onColor, Color offColor) {
		backup(g2);

		g2.setStroke(new BasicStroke(1));
		g2.setPaint(on ? onColor : offColor);
		g2.fill(rect);
		g2.setPaint(Color.BLACK);
		g2.draw(rect);
		g2.setPaint(Color.WHITE);
		Rectangle switchRect = new Rectangle(rect.x, rect.y, rect.width / 2, rect.height);
		if (on)
			switchRect.x += rect.width / 2;
		g2.fill(switchRect);
		g2.setPaint(Color.BLACK);
		g2.draw(switchRect);
		revert(g2);
	}

	public static void drawSwitchVertical(Graphics2D g2, Rectangle rect, boolean on, Color onColor, Color offColor) {
		backup(g2);

		g2.setStroke(new BasicStroke(1));
		g2.setPaint(on ? onColor : offColor);
		g2.fill(rect);
		g2.setPaint(Color.BLACK);
		g2.draw(rect);
		g2.setPaint(Color.WHITE);
		Rectangle switchRect = new Rectangle(rect.x, rect.y, rect.width, rect.height / 2);
		if (on)
			switchRect.y += rect.height / 2;
		g2.fill(switchRect);
		g2.setPaint(Color.BLACK);
		g2.draw(switchRect);

		revert(g2);
	}

	public static void drawPushSwitch(Graphics2D g2, int x, int y, int diameter, Color fill, String label) {
		backup(g2);
		g2.setStroke(new BasicStroke(1));
		g2.setPaint(fill);
		g2.fillOval(x, y, diameter, diameter);
		g2.setPaint(Color.BLACK);
		g2.drawOval(x, y, diameter, diameter);
		if (label != null) {
			int textHeight = diameter * 7 / 10;
			g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, textHeight));
			drawStringCentered(g2, label, x + diameter / 2, y + textHeight / 2);
		}
		revert(g2);
	}

	private static void revert(Graphics2D g2) {
		g2.setStroke(oldStroke);
		g2.setFont(oldFont);
		g2.setColor(oldColor);
	}

	private static void backup(Graphics2D g2) {
		oldStroke = g2.getStroke();
		oldFont = g2.getFont();
		oldColor = g2.getColor();
	}

	public static void drawStringCentered(Graphics2D g2, String text, int cX, int cY) {
		FontMetrics fm = g2.getFontMetrics();
		Rectangle2D rect = fm.getStringBounds(text, g2);
		g2.drawString(text, cX - (int) (rect.getWidth() / 2), (int) (cY + rect.getHeight() / 2 + 1));
	}

}
