package logicsim;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class LSButton extends JButton implements MouseListener {

	private static final long serialVersionUID = 4465562539140913810L;
	private boolean toggle;
	private boolean selected;

	public LSButton(String iconName, String toolTip) {
		this.setDoubleBuffered(true);
		this.setIcon(getIcon(iconName));
		this.setToolTipText(I18N.getString(toolTip));
		// this.setBorderPainted(true);
		// this.setBorder(BorderFactory.createLineBorder(Color.black));
		this.addMouseListener(this);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

	public void setToggleButton(boolean toggle) {
		this.toggle = toggle;
	}

	public void setSelected(boolean selected) {
		if (!toggle)
			return;

		this.selected = selected;
		repaint();
	}

	private ImageIcon getIcon(String imgname) {
		String filename = "images/" + imgname + "48.png";
		int is = LSProperties.getInstance().getPropertyInteger("iconsize", 48);
		is = 36;
		// return new ImageIcon(LSFrame.class.getResource(filename));
		return new ImageIcon(new ImageIcon(getClass().getResource(filename)).getImage().getScaledInstance(is, is,
				Image.SCALE_AREA_AVERAGING));
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (!toggle)
			return;
		selected = !selected;

		if (selected) {
			setOpaque(true);
			setBackground(Color.green);
		} else {
			setOpaque(false);
		}
		invalidate();

		System.out.println(selected);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

}
