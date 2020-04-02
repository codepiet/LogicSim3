package logicsim;

import java.lang.reflect.InvocationTargetException;

public class GateInstanciator {

	/**
	 * 
	 * @param g
	 * @param embedded only relevant for modules
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
