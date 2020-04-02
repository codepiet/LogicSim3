package logicsim;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.plaf.basic.BasicInternalFrameUI;

public class App {

	LSFrame lsframe;
	static ArrayList<Category> cats = new ArrayList<Category>();
	public static String CIRCUIT_FILE_SUFFIX = "lsc";
	public static String MODULE_FILE_SUFFIX = "lsm";
	public static String GRAPHICS_FORMAT = "png";

	/** Construct the application */
	public App() {
		new I18N();
		initializeGateCategories();
		JFrame frame = new MyFrame();
		lsframe = new LSFrame(frame);
		((BasicInternalFrameUI) lsframe.getUI()).setNorthPane(null);
		lsframe.setBorder(null);
		frame.getContentPane().add(lsframe);

		lsframe.window = frame;

		//Image si = new ImageIcon(App.class.getResource("images/splash.jpg")).getImage();
		//Splash splash = new Splash(frame, si);
		//splash.setVisible(true);

		lsframe.validate();
		frame.validate();
		// Center the window
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = new Dimension(1024, 768);
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		lsframe.setSize(frameSize);
		frame.setSize(frameSize);
		lsframe.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
		frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
		lsframe.setVisible(true);
		frame.setVisible(true);

	}

	private static void addToCategory(Gate g) {
		String cattitle = g.category;
		if (g.category == null)
			cattitle = "hidden";

		Category cat = null;
		for (Category c : cats) {
			if (c.getTitle().equals(cattitle)) {
				cat = c;
				break;
			}
		}
		if (cat == null) {
			cat = new Category(cattitle);
			cats.add(cat);
		}
		cat.addGate(g);
	}

	private static void initializeGateCategories() {
		cats.add(new Category("basic"));
		cats.add(new Category("input"));
		cats.add(new Category("output"));
		cats.add(new Category("flipflop"));

		List<Class<?>> classes;
		try {
			classes = GateLoaderHelper.getClasses("logicsim.gates");
			for (Class<?> c : classes) {
				Gate gate = (Gate) c.getDeclaredConstructor().newInstance();
				addToCategory(gate);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}

		/*
		 * module part
		 */
		File mods = new File(App.getModulePath());
		// list of filenames in modules dir
		String[] list = mods.list();
		Arrays.sort(list, String.CASE_INSENSITIVE_ORDER);
		// prepare empty list of loaded
		ArrayList<String> loadedModules = new ArrayList<String>();
		// prepare list of modules with sublist of needed modules
		Map<String, ArrayList<String>> modules = new HashMap<String, ArrayList<String>>();

		// now collect all modules with their needed modules
		for (int i = 0; i < list.length; i++) {
			if (list[i].endsWith(App.MODULE_FILE_SUFFIX)) {
				String filename = list[i];
				String type = new File(filename).getName();
				type = type.substring(0, type.lastIndexOf("."));
				modules.put(type, XMLLoader.getModuleListFromFile(App.getModulePath() + "/" + filename));
			}
		}

		int maxtries = modules.keySet().size();
		int tries = 0;
		while (tries < maxtries && maxtries != loadedModules.size()) {
			for (String modname : modules.keySet()) {
				boolean load = true;
				for (String neededModuleName : modules.get(modname)) {
					if (!loadedModules.contains(neededModuleName.toLowerCase())) {
						load = false;
						break;
					}
				}
				if (load) {
					Module mod = new Module(modname);
					mod.category = "module";
					addToCategory(mod);
					loadedModules.add(modname.toLowerCase());
				}
			}
		}
	}

	/** Main method */
	public static void main(String[] args) {
		new App();
	}

	public static String getModulePath() {
		File f = new File("");
		String fname = f.getAbsolutePath() + "/modules/";
		f = new File(fname);
		if (f != null && f.exists() && f.isDirectory()) {
			return new String(f.getAbsolutePath() + "/");
		} else {
			JOptionPane.showMessageDialog(null,
					"Directory modules not found.\nPlease run the program from its directory");
			System.exit(0);
		}

		return "";
	}

	public static String getCircuitPath() {
		File f = new File("");
		String fname = f.getAbsolutePath() + "/circuits/";
		f = new File(fname);
		if (f != null && f.exists() && f.isDirectory()) {
			return new String(f.getAbsolutePath() + "/");
		} else {
			JOptionPane.showMessageDialog(null,
					"Directory circuits not found.\nPlease run the program from its directory");
			System.exit(0);
		}
		return "";
	}

	class MyFrame extends JFrame {
		static final long serialVersionUID = -6532037559895208999L;

		public MyFrame() {
			super();
			enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		}

		/**
		 * Overridden so we can exit when window is closed
		 */
		@Override
		protected void processWindowEvent(WindowEvent e) {
			if (e.getID() == WindowEvent.WINDOW_CLOSING) {
				if (lsframe.showDiscardDialog(I18N.getString(Lang.MNU_EXIT)) == false)
					return;
				System.exit(0);
			}
		}

	}

	public static Gate getGate(String type) {
		for (Category cat : cats) {
			for (Gate g : cat.getGates()) {
				if (g.type.toLowerCase().equals(type)) {
					return g;
				}
			}
		}
		return null;
	}
}