package logicsim;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {

	SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

	private static Log instance;

	public static Log getInstance() {
		if (instance == null)
			instance = new Log();
		return instance;
	}

	private Log() {

	}

	public void print(Object o) {
		Date now = new Date();
		System.out.println(format.format(now) + " - " + o);
	}

}
