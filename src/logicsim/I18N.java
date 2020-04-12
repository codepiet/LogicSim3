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

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Properties;

import javax.swing.JOptionPane;

/**
 *
 * @author atetzl
 */
public class I18N {

	public static final String TITLE = "title";
	public static final String DESCRIPTION = "description";
	public static final String ALL = "ALL";

	public static String lang = "en";
	public static Properties prop = null;

	/** Creates a new instance of I18N */
	public I18N() {
		if (prop != null)
			return;

		lang = LSProperties.getInstance().getProperty(LSProperties.LANGUAGE, "en");
		prop = load(lang);
		if (prop.size() == 0 && !"en".equals(lang)) {
			prop = load("en");
			if (prop == null) {
				JOptionPane.showMessageDialog(null,
						"Language file languages/en.txt not found.\nPlease run the program from its directory.");
				System.exit(5);
			}
		}
	}

	public static Properties load(String lang) {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream("languages/" + lang + ".txt"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return properties;
	}

	public static String langToStr(Lang l) {
		String key = l.toString();
		key = key.toLowerCase();
		key = key.replace("_", ".");
		return key;
	}

	public static String tr(Lang langkey) {
		if (prop == null)
			return "- I18N not initialized -";
		return tr(langToStr(langkey));
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

	public static List<String> getLanguages() {
		File dir = new File("languages/");
		String[] files = dir.list();
		java.util.Arrays.sort(files);
		List<String> langs = new ArrayList<String>();
		for (int i = 0; i < files.length; i++) {
			if (files[i].endsWith(".txt")) {
				String name = files[i].substring(0, files[i].length() - 4);
				langs.add(name);
			}
		}
		return langs;
	}

	public static void main(String[] args) {
		List<Lang> langList = new ArrayList<Lang>(EnumSet.allOf(Lang.class));

		List<String> list = new ArrayList<String>();
		for (Lang l : langList) {
			String key = langToStr(l);
			list.add(key);
		}
		// get all languages from folder
		List<String> langs = I18N.getLanguages();
		langs = new ArrayList<String>();
		langs.add("es");
		for (String lang : langs) {
			System.out.println(lang);
			System.out.println("-------------------------");
			Properties ps = load(lang);
			for (String key : list) {
				if (!ps.containsKey(key)) {
					System.err.println(key + " is missing in langfile");
				}
			}
			for (Object obj : ps.keySet()) {
				String key = (String) obj;
				if (key.startsWith("gate."))
					continue;
				// check if the langfile key is in the list
				if (!list.contains(key)) {
					System.err.println("key '" + key + "' is not specified");
				}
			}
		}
	}

	public static void addGate(String langGate, String type, String key, String value) {
		if (!langGate.equals(langGate) && !langGate.equals(ALL))
			return;
		prop.setProperty(type + "." + key, value);
	}
}
