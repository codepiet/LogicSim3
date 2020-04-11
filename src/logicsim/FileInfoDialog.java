package logicsim;

import java.awt.Component;
import java.awt.Rectangle;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class FileInfoDialog {

	public static boolean showFileInfo(Component frame, LogicSimFile lsFile) {
		JPanel panel = new JPanel();

		JLabel lblDescription = new JLabel();
		lblDescription.setText(I18N.tr(Lang.DESCRIPTION));
		lblDescription.setBounds(new Rectangle(15, 56, 100, 23));

		JTextArea txtDescription = new JTextArea(lsFile.getDescription());
		txtDescription.setBounds(new Rectangle(120, 56, 235, 88));

		panel.setLayout(null);
		panel.add(lblDescription, null);
		panel.add(txtDescription, null);

		JOptionPane pane = new JOptionPane(panel);
		pane.setMessageType(JOptionPane.QUESTION_MESSAGE);
		String[] options = new String[] { I18N.tr(Lang.OK), I18N.tr(Lang.CANCEL) };
		pane.setOptions(options);
		JDialog dlg = pane.createDialog(frame, I18N.tr(Lang.PROPERTIES));
		dlg.setResizable(true);
		dlg.setSize(500, 250);
		dlg.setVisible(true);
		if (I18N.tr(Lang.OK) == (String) pane.getValue()) {
			lsFile.setDescription(txtDescription.getText());
			return true;
		}
		return false;
	}
}
