package logicsim;

import javax.swing.JOptionPane;

public class Dialogs {
	public static final int SAVE = 1;
	public static final int CANCEL = 2;
	public static final int DONT_SAVE = 3;

	public static int confirmSaveDialog() {
		Object[] options1 = { I18N.getString(Lang.BTN_SAVE), I18N.getString(Lang.BTN_CANCEL),
				I18N.getString(Lang.BTN_DONT_SAVE) };

		int result = JOptionPane.showOptionDialog(null, I18N.getString(Lang.MSG_CONFIRMSAVE), "LogicSim",
				JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, options1, null);

		if (result == JOptionPane.YES_OPTION) {
			return SAVE;
		} else if (result == JOptionPane.NO_OPTION) {
			return CANCEL;
		} else {
			return DONT_SAVE;
		}
	}

	public static int confirmDiscardDialog() {
		Object[] options1 = { I18N.getString(Lang.BTN_YES), I18N.getString(Lang.BTN_NO) };
		int result = JOptionPane.showOptionDialog(null, I18N.getString(Lang.MSG_CONFIRMDISCARD), "LogicSim",
				JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options1, options1[1]);

		return result;
	}
}
