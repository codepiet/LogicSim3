
package gates;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Path2D;
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
import logicsim.Pin;
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
	private static final String TOGGLE = "toggle";
	private static final String MOMENTARY = "momentary";
	private static final String DEFAULT_SWITCH_TYPE = TOGGLE;
	private static final String COLOR = "color";
	private static final String DEFAULT_COLOR = "#0000ff";

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
		createOutputs(1);
		getPin(0).setX(getX() + width);
		getPin(0).setY(getY() + 30);
		getPin(0).setLevel(false);
		loadProperties();
	}

	@Override
	protected void loadProperties() {
		color = ColorFactory.web(getPropertyWithDefault(COLOR, DEFAULT_COLOR));
		clickType = getPropertyWithDefault(SWITCH_TYPE, DEFAULT_SWITCH_TYPE).equals(TOGGLE) ? false : true;
	}

	@Override
	public void mousePressedSim(LSMouseEvent e) {
		super.mousePressedSim(e);

		if (clickType) {
			// Click-Button, wird wieder deaktiviert, wenn Maustaste losgelassen wird
			getPin(0).setLevel(true);
			mouseDown = true;
			clickCountDown = 2;
		} else {
			// Toggle-Button
			getPin(0).setLevel(!getPin(0).getLevel());
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
				getPin(0).setLevel(false);
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

		int pos = getPin(0).getLevel() ? 15 : 6;

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

		Pin c = getPin(0);
		if (c.paintDirection == Pin.DOWN) {
			// draw line
			g.setStroke(new BasicStroke(3));
			Path2D path = new Path2D.Double();
			path.moveTo(getX() + 3 + width - CONN_SIZE, getY() + 30);
			path.lineTo(getX() + width, getY() + 30);
			path.lineTo(getX() + width, getY() + CONN_SIZE);
			g.draw(path);
			g.setStroke(new BasicStroke(1));
		}
	}

	@Override
	public void rotate() {
		super.rotate();
		// correction
		Pin c = getPin(0);
		if (c.paintDirection == Pin.RIGHT) {
			c.setX(getX());
			c.setY(getY() + 30);
		} else if (c.paintDirection == Pin.LEFT) {
			c.setX(getX() + width);
			c.setY(getY() + 30);
		} else if (c.paintDirection == Pin.DOWN) {
			c.setX(getX() + width);
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
				I18N.getString(type, SWITCH_TYPE));
		jRadioButton1.setText(I18N.getString(type, TOGGLE));
		jRadioButton2.setText(I18N.getString(type, MOMENTARY));
		jPanel1.setBorder(titledBorder1);
		jPanel1.setBounds(new Rectangle(11, 11, 171, 150));
		jPanel1.setLayout(borderLayout1);
		jPanel1.add(jRadioButton1, BorderLayout.NORTH);
		jPanel1.add(jRadioButton2, BorderLayout.CENTER);

		JOptionPane pane = new JOptionPane(jPanel1);
		pane.setMessageType(JOptionPane.QUESTION_MESSAGE);
		pane.setOptions(new String[] { I18N.tr(Lang.OK), I18N.tr(Lang.CANCEL) });
		JDialog dlg = pane.createDialog(frame, I18N.tr(Lang.SETTINGS));
		dlg.setResizable(true);
		dlg.setSize(290, 180);
		dlg.setVisible(true);
		if (I18N.tr(Lang.OK).equals((String) pane.getValue())) {
			if (jRadioButton1.isSelected()) {
				clickType = false;
			} else if (jRadioButton2.isSelected()) {
				clickType = true;
			}
			setProperty(SWITCH_TYPE, clickType ? MOMENTARY : TOGGLE);
		}

		Color newColor = JColorChooser.showDialog(null, I18N.tr(Lang.SETTINGS), color);
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
		I18N.addGate(I18N.ALL, type, I18N.TITLE, "Switch");
		I18N.addGate(I18N.ALL, type, I18N.DESCRIPTION, "Toggle or Momentary Switch");
		I18N.addGate(I18N.ALL, type, SWITCH_TYPE, "type");
		I18N.addGate(I18N.ALL, type, MOMENTARY, "momentary switch");
		I18N.addGate(I18N.ALL, type, TOGGLE, "toggle switch");

		I18N.addGate("de", type, I18N.TITLE, "Schalter");
		I18N.addGate("de", type, I18N.DESCRIPTION, "Schalter (Taster oder Umschalter)");
		I18N.addGate("de", type, SWITCH_TYPE, "Typ");
		I18N.addGate("de", type, MOMENTARY, "Taster");
		I18N.addGate("de", type, TOGGLE, "Umschalter");

		I18N.addGate("es", type, I18N.TITLE, "Pulsador");
		I18N.addGate("es", type, SWITCH_TYPE, "Tipo de pulsador");
		I18N.addGate("es", type, MOMENTARY, "Momentáneo");
		I18N.addGate("es", type, TOGGLE, "Con enclavamiento");

		I18N.addGate("fr", type, I18N.TITLE, "Bouton");
		I18N.addGate("fr", type, SWITCH_TYPE, "Type de bouton");
		I18N.addGate("fr", type, MOMENTARY, "Bouton poussoir");
		I18N.addGate("fr", type, TOGGLE, "Interrupteur");
	}
}