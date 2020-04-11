package gates;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Rectangle;

import javax.swing.ButtonGroup;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import logicsim.Pin;
import logicsim.Gate;
import logicsim.I18N;
import logicsim.LSMouseEvent;
import logicsim.Lang;

/**
 * Binary Input Component for LogicSim
 * 
 * @author Andreas Tetzl
 * @author Peter Gabriel
 * @version 2.0
 */
public class BinIn extends Gate {
	static final long serialVersionUID = 3971572931629721831L;

	private static final String DISPLAY_TYPE = "displaytype";
	private static final String DISPLAY_TYPE_HEX = "hex";
	private static final String DISPLAY_TYPE_DEC = "dec";
	private static final String DISPLAY_TYPE_DEFAULT = "hex";

	String displayType;

	Rectangle rect1 = new Rectangle(12, 32, 15, 15);
	Rectangle rect2 = new Rectangle(32, 32, 15, 15);
	Rectangle rect3 = new Rectangle(12, 67, 15, 15);
	Rectangle rect4 = new Rectangle(32, 67, 15, 15);

	public BinIn() {
		super("input");
		type = "binin";
		height = 90;
		createOutputs(8);
		loadProperties();
	}

	@Override
	protected void loadProperties() {
		displayType = getPropertyWithDefault(DISPLAY_TYPE, DISPLAY_TYPE_DEFAULT);
	}

	@Override
	public void mousePressedSim(LSMouseEvent e) {
		super.mousePressedSim(e);

		int dx = e.getX() - getX();
		int dy = e.getY() - getY();
		int value = 0;
		for (int i = 0; i < 8; i++) {
			if (getPin(i).getLevel())
				value += (1 << i);
		}

		boolean dHex = DISPLAY_TYPE_HEX.equals(displayType);

		if (rect1.contains(dx, dy)) {
			if (dHex)
				value += 16;
			else
				value += 10;
		} else if (rect2.contains(dx, dy)) {
			value += 1;
		} else if (rect3.contains(dx, dy)) {
			if (dHex)
				value -= 16;
			else
				value -= 10;
		} else if (rect4.contains(dx, dy)) {
			value -= 1;
		}

		if (value < 0)
			value = 0xff + value + 1;
		if (value > 0xff)
			value = value - 0xff - 1;

		for (int i = 0; i < 8; i++)
			getPin(i).setLevel((value & (1 << i)) != 0);
	}

	@Override
	public void draw(Graphics2D g) {
		super.draw(g);
		int x = getX();
		int y = getY();
		boolean dHex = DISPLAY_TYPE_HEX.equals(displayType);
		g.setPaint(Color.BLUE);

		int value = 0;
		for (int i = 0; i < 8; i++) {
			if (getOutputs().get(i).getLevel())
				value += (1 << i);
		}

		// draw triangles
		Rectangle rect = rect1.getBounds();
		Polygon p = new Polygon();
		p.addPoint(x + rect.x + rect.width, y + rect.y + rect.height);
		p.addPoint(x + rect.x + rect.width / 2, y + rect.y);
		p.addPoint(x + rect.x, y + rect.y + rect.height);
		g.fill(p);
		rect = rect2;
		p = new Polygon();
		p.addPoint(x + rect.x + rect.width, y + rect.y + rect.height);
		p.addPoint(x + rect.x + rect.width / 2, y + rect.y);
		p.addPoint(x + rect.x, y + rect.y + rect.height);
		g.fill(p);
		rect = rect3;
		p = new Polygon();
		p.addPoint(x + rect.x + rect.width, y + rect.y);
		p.addPoint(x + rect.x + rect.width / 2, y + rect.y + rect.height);
		p.addPoint(x + rect.x, y + rect.y);
		g.fill(p);
		rect = rect4;
		p = new Polygon();
		p.addPoint(x + rect.x + rect.width, y + rect.y);
		p.addPoint(x + rect.x + rect.width / 2, y + rect.y + rect.height);
		p.addPoint(x + rect.x, y + rect.y);
		g.fill(p);
		g.setPaint(Color.BLACK);
		String sval = "";

		if (dHex)
			sval = Integer.toHexString(value);
		else
			sval = Integer.toString(value);
		if (sval.length() == 0)
			sval = "00";
		if (sval.length() == 1)
			sval = "0" + sval;

		sval = sval.toUpperCase();
		g.setFont(bigFont);
		int sw = g.getFontMetrics().stringWidth(sval);
		g.drawString(sval, x + getWidth() / 2 - sw / 2, y + height / 2 + 17);
		g.setFont(Pin.smallFont);
		String s = "INPUT";
		sw = g.getFontMetrics().stringWidth(s);
		g.drawString(s, x + getWidth() / 2 - sw / 2, y + 17);
		s = displayType.toUpperCase();
		sw = g.getFontMetrics().stringWidth(s);
		g.drawString(s, x + getWidth() / 2 - sw / 2, y + 29);
	}

	public boolean hasPropertiesUI() {
		return true;
	}

	public boolean showPropertiesUI(Component frame) {
		JRadioButton jRadioButton1 = new JRadioButton();
		JRadioButton jRadioButton2 = new JRadioButton();

		// Group the radio buttons.
		ButtonGroup group = new ButtonGroup();
		group.add(jRadioButton1);
		group.add(jRadioButton2);

		if (DISPLAY_TYPE_HEX.equals(displayType))
			jRadioButton1.setSelected(true);
		else
			jRadioButton2.setSelected(true);

		JPanel jPanel1 = new JPanel();
		TitledBorder titledBorder1;
		BorderLayout borderLayout1 = new BorderLayout();

		titledBorder1 = new TitledBorder(new EtchedBorder(EtchedBorder.RAISED, Color.white, new Color(142, 142, 142)),
				I18N.getString(type, "ui.displaytype"));
		jRadioButton1.setText(I18N.getString(type, "ui.hex"));
		jRadioButton2.setText(I18N.getString(type, "ui.dec"));
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
				displayType = DISPLAY_TYPE_HEX;
			} else if (jRadioButton2.isSelected()) {
				displayType = DISPLAY_TYPE_DEC;
			}
			displayType = displayType.toUpperCase();
			return true;
		}
		return false;
	}

}