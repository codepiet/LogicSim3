package logicsim;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JButton;

public class LSButton extends JButton {

	private static final long serialVersionUID = 4465562539140913810L;

	public LSButton(String iconName, Lang toolTip) {
		this.setDoubleBuffered(true);
		this.setIcon(getIcon(iconName));
		this.setToolTipText(I18N.tr(toolTip));
		this.setName(toolTip.toString());
		// this.setBorderPainted(true);
		// this.setBorder(BorderFactory.createLineBorder(Color.black));
		// this.addMouseListener(this);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

	private ImageIcon getIcon(String imgname) {
		String filename = "images/" + imgname + ".png";
		int is = LSProperties.getInstance().getPropertyInteger("iconsize", 48);
		is = 36;
		// return new ImageIcon(LSFrame.class.getResource(filename));
		return new ImageIcon(new ImageIcon(getClass().getResource(filename)).getImage().getScaledInstance(is, is,
				Image.SCALE_AREA_AVERAGING));
	}
}
