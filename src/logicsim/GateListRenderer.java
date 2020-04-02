package logicsim;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.SwingConstants;

public class GateListRenderer extends JLabel implements ListCellRenderer<Object> {
	private static final long serialVersionUID = -361281475843085219L;

	@Override
	public Component getListCellRendererComponent(JList<? extends Object> list, Object value, int index,
			boolean isSelected, boolean cellHasFocus) {

		setFont(list.getFont());
		setOpaque(true);
		if (value instanceof Gate) {
			if (isSelected) {
				setBackground(new Color(0xaa, 0xaa, 0xFF));
				setForeground(Color.white);
			} else {
				setForeground(list.getForeground());
				setBackground(list.getBackground());
			}
			if (value instanceof Module) {
				setText(((Gate) value).type);
			} else {
				String s = ((Gate) value).type;
				if (!s.contains("test"))
					s = I18N.getString(s, "title");
				setText(s);
			}
			setHorizontalAlignment(SwingConstants.LEFT);
			return this;
		} else {
			setText((String) value);
			setBackground(Color.LIGHT_GRAY);
			setForeground(Color.WHITE);
			setHorizontalAlignment(SwingConstants.CENTER);
			return this;
		}
	}

}
