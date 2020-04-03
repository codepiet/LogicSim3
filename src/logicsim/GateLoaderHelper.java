package logicsim;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class GateLoaderHelper {

	/**
	 * Scans all classes accessible from the context class loader which belong to
	 * the given package and subpackages.
	 *
	 * @param packageName The base package
	 * @return The classes
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	static List<Class<?>> getClasses() throws ClassNotFoundException, IOException {
		if (App.Running_From_Jar)
			return getClassesOutsideJar();

		File directory = new File("bin/gates");
		String packageName = "gates";

		List<Class<?>> classes = new ArrayList<Class<?>>();
		if (!directory.exists())
			return classes;

		File[] files = directory.listFiles();
		for (File file : files) {
			if (!file.getName().endsWith(".class"))
				continue;
			classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
		}
		return classes;
	}

	public static List<Class<?>> getClassesOutsideJar() {
		List<String> classNames = new ArrayList<String>();

		File directory = new File("gates");
		File[] files = directory.listFiles();
		for (File file : files) {
			if (!file.getName().endsWith(".class"))
				continue;
			classNames.add(file.getName().substring(0, file.getName().length() - 6));
		}

		ArrayList<Class<?>> classes = new ArrayList<Class<?>>();

		File file = new File(".");
		try {
			// Convert File to a URL
			URL url = file.toURI().toURL();
			URL[] urls = new URL[] { url };

			// Create a new class loader with the directory
			URLClassLoader cl = new URLClassLoader(urls);
			for (String className : classNames) {
				Class<?> cls = cl.loadClass("gates." + className);
				classes.add(cls);
			}
			// cl.close();
		} catch (MalformedURLException e) {
		} catch (ClassNotFoundException e) {
		}
		return classes;
	}

	public static Class<?> getClassOutsideJar(String className) {
		File file = new File(".");
		Class<?> cls = null;
		try {
			// Convert File to a URL
			URL url = file.toURI().toURL();
			URL[] urls = new URL[] { url };

			// Create a new class loader with the directory
			URLClassLoader cl = new URLClassLoader(urls);
			cls = cl.loadClass(className);
			// cl.close();
		} catch (MalformedURLException e) {
		} catch (ClassNotFoundException e) {
		}
		return cls;
	}

	/**
	 * 
	 * @param g
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Gate create(Gate g) {
		Gate gate = null;
		try {
			Class<? extends Gate> c = g.getClass();
			Object obj;
			if (g instanceof Module) {
				Class[] cArg = new Class[] { String.class, Boolean.TYPE };
				obj = c.getDeclaredConstructor(cArg).newInstance(g.type, true);
				gate = (Module) obj;
			} else {
				obj = c.getDeclaredConstructor().newInstance();
				gate = (Gate) obj;
			}
		} catch (InstantiationException e) {
			throw new RuntimeException(e.getMessage());
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e.getMessage());
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e.getMessage());
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e.getMessage());
		} catch (SecurityException e) {
			throw new RuntimeException(e.getMessage());
		}
	
		return gate;
	}

}
