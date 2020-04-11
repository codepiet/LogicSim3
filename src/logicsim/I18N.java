/*
 * I18N.java
 *
 * Created on 29. Dezember 2005, 15:27
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package logicsim;

import java.io.FileInputStream;
import java.util.Properties;

import javax.swing.JOptionPane;

/**
 *
 * @author atetzl
 */
public class I18N {

	public static Properties prop = null;

	/** Creates a new instance of I18N */
	public I18N() {
		if (prop != null)
			return;

		String lang = LSProperties.getInstance().getProperty(LSProperties.LANGUAGE, "en");
		prop = new Properties();
		try {
			prop.load(new FileInputStream("languages/" + lang + ".txt"));
		} catch (Exception ex) {
			ex.printStackTrace();
			try {
				// Default: Englische Sprachdatei laden
				prop.load(new FileInputStream("languages/en.txt"));
			} catch (Exception ex2) {
				JOptionPane.showMessageDialog(null,
						"Language file languages/en.txt not found.\nPlease run the program from its directory.");
				System.exit(5);
			}

		}
	}

	public static String tr(Lang langkey) {
		if (prop == null)
			return "- I18N not initialized -";
		String key = langkey.toString();
		key = key.toLowerCase();
		key = key.replace("_", ".");
		return tr(key);
	}

	public static String tr(String key) {
		if (prop == null)
			return "- I18N not initialized -";
		if (prop.containsKey(key)) {
			String item = prop.getProperty(key);
			if (item != null)
				return item;
		}
		System.err.println("I18N: translation of '" + key + "' is missing");
		return "-" + key + "-";
	}

	public static String getString(String id, String key) {
		return tr("gate." + id + "." + key);
	}

	public static boolean hasString(String key) {
		String item = prop.getProperty(key);
		return (item != null);
	}

	public static boolean hasString(String id, String key) {
		return hasString("gate." + id + "." + key);
	}

	public static String tr(Lang key, String value) {
		String s = tr(key);
		return String.format(s, value);
	}

}
