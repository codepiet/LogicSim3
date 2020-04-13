package logicsim;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class LSProperties {

	public static final String GATEDESIGN = "gatedesign";

	public static final String PAINTGRID = "paintgrid";

	public static final String LANGUAGE = "language";

	public static final String GATEDESIGN_IEC = "iec";

	public static final String AUTOWIRE = "autowire";

	private static LSProperties instance = null;

	private Properties properties = new Properties();

	private String filename;

	private LSProperties(String filename) {
		this.filename = filename;
		try {
			properties.load(new FileInputStream(filename));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public String getProperty(String key, String defValue) {
		if (!properties.containsKey(key)) {
			if (defValue != null) {
				setProperty(key, defValue);
			} else
				return null;
		}
		return properties.getProperty(key);
	}

	public boolean getPropertyBoolean(String key, boolean defValue) {
		String v = getProperty(key, String.valueOf(defValue));
		if (v == null)
			return false;
		return Boolean.parseBoolean(v);
	}

	public int getPropertyInteger(String key, int defValue) {
		String v = getProperty(key, String.valueOf(defValue));
		if (v == null)
			return 0;
		return Integer.parseInt(v);
	}

	public void setProperty(String key, String value) {
		properties.setProperty(key, value);
		try {
			properties.store(new FileOutputStream(filename), "LogicSim Configuration");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	public void setPropertyBoolean(String key, boolean value) {
		setProperty(key, String.valueOf(value));
	}

	public void setPropertyInteger(String key, int value) {
		setProperty(key, String.valueOf(value));
	}

	public static LSProperties getInstance() {
		if (instance == null)
			instance = new LSProperties("logicsim.cfg");
		return instance;
	}
}
