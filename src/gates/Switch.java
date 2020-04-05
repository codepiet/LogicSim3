package gates;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

import javax.swing.ButtonGroup;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import logicsim.ColorFactory;
import logicsim.Connector;
import logicsim.Gate;
import logicsim.I18N;
import logicsim.LSMouseEvent;
import logicsim.Lang;

/**
 * Switch Component for LogicSim
 * 
 * Ein Klick-Button bleibt nur so lange an, wie der Nutzer den Mausknopf
 * gedr�ckt h�lt, aber mindestens 2 Simulations-Zyklen. Daf�r wird beim Klick
 * der Countdown auf 2 gesetzt und in jedem Zyklus heruntergez�hlt. Der Button
 * geht aus, wenn der Countdown auf 0 ist und die Maustaste losgelassen wurde.
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class Switch extends Gate {
	static final long serialVersionUID = 2459367526586913840L;

	private static final String SWITCH_TYPE = "type";
	private static final String COLOR = "color";

	private static final String DEFAULT_COLOR = "#0000ff";
	private static final String DEFAULT_SWITCH_TYPE = "toggle";

	/**
	 * type of switch: true=Click, false=Toggle
	 */
	boolean clickType = false;

	long clickCountDown = 0;
	boolean mouseDown = false; // true solange der User die Maus �ber dem Gatter gedr�ckt h�lt

	private Color color = null;

	public Switch() {
		super("input");
		type = "switch";
		width = 40;
		height = 40;
		setNumInputs(0);
		setNumOutputs(1);
		setOutputLevel(0, false);
		drawFrame = false;
		getOutput(0).setX(getX() + width);
		getOutput(0).setY(getY() + 30);

		loadProperties();
	}

	@Override
	protected void loadProperties() {
		color = ColorFactory.web(getPropertyWithDefault(COLOR, DEFAULT_COLOR));
		clickType = getPropertyWithDefault(SWITCH_TYPE, DEFAULT_SWITCH_TYPE).equals("toggle") ? false : true;
	}

	@Override
	public void mousePressedUI(LSMouseEvent e) {
		super.mousePressedUI(e);

		if (clickType) {
			// Click-Button, wird wieder deaktiviert, wenn Maustaste losgelassen wird
			setOutputLevel(0, true);
			mouseDown = true;
			clickCountDown = 2;
		} else {
			// Toggle-Button
			setOutputLevel(0, !getOutputLevel(0));
		}
	}

	@Override
	public void mouseReleased(int mx, int my) {
		mouseDown = false;
	}

	public void simulate() {
		if (clickType) {
			if (clickCountDown > 0)
				clickCountDown--;
			if (clickCountDown == 0 && !mouseDown)
				setOutputLevel(0, false);
		}
	}

	@Override
	public void draw(Graphics2D g) {
		super.draw(g);
		int x = getX();
		int y = getY();

		Rectangle2D rect;
		// rect = new Rectangle2D.Float(x + width - CONN_SIZE - 12, y + 5, 13, 30);
		rect = new Rectangle2D.Float(x + 5, y + height - CONN_SIZE - 12, 30, 13);

		g.setStroke(new BasicStroke(1));
		g.setPaint(Color.LIGHT_GRAY);
		g.fill(rect);
		g.setPaint(Color.BLACK);
		g.draw(rect);

		int pos = getOutputLevel(0) ? 15 : 6;

//		Polygon poly = new Polygon();
//		poly.addPoint(x + width - 12 - CONN_SIZE, y + 8);
//		poly.addPoint(x + pos + 3, y + 8);
//		poly.addPoint(x + pos, y + 12);
//		poly.addPoint(x + pos, y + 28);
//		poly.addPoint(x + pos + 3, y + 32);
//		poly.addPoint(x + width - 12 - CONN_SIZE, y + 32);

		Polygon poly = new Polygon();
		// begin in the lower left corner and go counterclockwise
		poly.addPoint(x + 8, y + height - 12 - CONN_SIZE);
		poly.addPoint(x + 8, y + pos + 3);
		poly.addPoint(x + 12, y + pos);
		poly.addPoint(x + 28, y + pos);
		poly.addPoint(x + 32, y + pos + 3);
		poly.addPoint(x + 32, y + height - 12 - CONN_SIZE);

		g.setPaint(color);
		g.fillPolygon(poly);
		g.setPaint(Color.BLACK);
		g.drawPolygon(poly);
	}

	@Override
	public void rotate() {
		super.rotate();
		// correction
		Connector c = getOutput(0);
		if (c.paintDirection == Connector.RIGHT) {
			c.setX(getX());
			c.setY(getY() + 30);
		} else if (c.paintDirection == Connector.LEFT) {
			c.setX(getX() + width);
			c.setY(getY() + 30);
		} else if (c.paintDirection == Connector.DOWN) {
			c.setX(getX() + width / 2);
			c.setY(getY());
		} else {
			c.setX(getX() + width / 2);
			c.setY(getY() + height);
		}

	}

	@Override
	public boolean hasPropertiesUI() {
		return true;
	}

	@Override
	public boolean showPropertiesUI(Component frame) {
		JRadioButton jRadioButton1 = new JRadioButton();
		JRadioButton jRadioButton2 = new JRadioButton();

		// Group the radio buttons.
		ButtonGroup group = new ButtonGroup();
		group.add(jRadioButton1);
		group.add(jRadioButton2);

		if (clickType)
			jRadioButton2.setSelected(true);
		else
			jRadioButton1.setSelected(true);

		JPanel jPanel1 = new JPanel();
		TitledBorder titledBorder1;
		BorderLayout borderLayout1 = new BorderLayout();

		// Border border1 = new EtchedBorder(EtchedBorder.RAISED, Color.white, new
		// Color(142, 142, 142));
		titledBorder1 = new TitledBorder(new EtchedBorder(EtchedBorder.RAISED, Color.white, new Color(142, 142, 142)),
				I18N.getString(type, "ui.grouptitle"));
		jRadioButton1.setText(I18N.getString(type, "ui.toggle"));
		jRadioButton2.setText(I18N.getString(type, "ui.click"));
		jPanel1.setBorder(titledBorder1);
		jPanel1.setBounds(new Rectangle(11, 11, 171, 150));
		jPanel1.setLayout(borderLayout1);
		jPanel1.add(jRadioButton1, BorderLayout.NORTH);
		jPanel1.add(jRadioButton2, BorderLayout.CENTER);

		JOptionPane pane = new JOptionPane(jPanel1);
		pane.setMessageType(JOptionPane.QUESTION_MESSAGE);
		pane.setOptions(new String[] { I18N.getString(Lang.BTN_OK), I18N.getString(Lang.BTN_CANCEL) });
		JDialog dlg = pane.createDialog(frame, I18N.getString(type, "ui.title"));
		dlg.setResizable(true);
		dlg.setSize(290, 180);
		dlg.setVisible(true);
		if (I18N.getString(Lang.BTN_OK).equals((String) pane.getValue())) {
			if (jRadioButton1.isSelected()) {
				clickType = false;
			} else if (jRadioButton2.isSelected()) {
				clickType = true;
			}
			setProperty(SWITCH_TYPE, clickType ? "momentary" : "toggle");
		}

		Color newColor = JColorChooser.showDialog(null, I18N.getString(type, "ui.title"), color);
		if (newColor != null)
			color = newColor;
		setProperty(COLOR, "#" + Integer.toHexString(color.getRGB()).substring(2));
		return true;
	}

}