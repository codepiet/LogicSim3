package logicsim;

import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JToggleButton;

public class LSToggleButton extends JToggleButton {

	private static final long serialVersionUID = 4992541122998327288L;

	public LSToggleButton(String iconName, String toolTip) {
		this.setDoubleBuffered(true);
		this.setIcon(getIcon(iconName));
		this.setToolTipText(I18N.getString(toolTip));
		// this.setBorderPainted(true);
		// this.setBorder(BorderFactory.createLineBorder(Color.black));
		// this.addMouseListener(this);
	}

	private ImageIcon getIcon(String imgname) {
		String filename = "images/" + imgname + "48.png";
		int is = LSProperties.getInstance().getPropertyInteger("iconsize", 48);
		is = 36;
		// return new ImageIcon(LSFrame.class.getResource(filename));
		return new ImageIcon(new ImageIcon(getClass().getResource(filename)).getImage().getScaledInstance(is, is,
				Image.SCALE_AREA_AVERAGING));
	}

}
