package logicsim;

import java.util.ArrayList;
import java.util.Properties;
import java.util.Vector;

/**
 * XML Loader for circuit and module files
 * 
 * ideas taken from https://argonrain.wordpress.com/2009/10/27/000/
 * 
 * @author Peter Gabriel
 * @version 1.0
 */
public class XMLLoader {

	private static final String ROOT_STRING = "logicsim";
	public static String formatVersion = "3";

	public static LogicSimFile loadXmlFile(String fileName) throws RuntimeException {
		LogicSimFile ls = new LogicSimFile(fileName);
		String rootString = ROOT_STRING;
		Xml doc = new Xml(fileName, rootString);
		String version = doc.string("version");
		if (!formatVersion.equals(version))
			throw new RuntimeException(I18N.tr(Lang.READERROR) + ": version does not match");
		Xml node = doc.optChild("info");
		if (node != null) {
			for (Xml n : node.children("item")) {
				String key = n.optString("key");
				ls.info.put(key, n.content());
			}
		}

		// gates part
		node = doc.optChild("gates");
		if (node != null) {
			for (Xml gnode : node.children(XMLCreator.TYPE_GATE)) {
				Gate gate = loadGate(gnode);
				gate.selected = false;
				ls.circuit.addGate(gate);
			}
		}

		node = doc.optChild("wires");
		if (node != null) {
			for (Xml wnode : node.children(XMLCreator.TYPE_WIRE)) {
				// Wire wire = loadWire(wnode);
				CircuitPart from = null;
				CircuitPart to = null;

				// from node
				Xml gnode = wnode.child("from");
				String type = gnode.optString("type");
				if (type == null || XMLCreator.TYPE_GATE.equals(type)) {
					String fromGateId = gnode.string("id");
					int fromNumber = Integer.parseInt(gnode.string("number"));
					// try to get the gate
					Gate fromGate = findGateById(ls.circuit.getGates(), fromGateId);
					if (fromGate == null)
						throw new RuntimeException(I18N.tr(Lang.READERROR) + ": cannot fing gate " + fromGateId);
					from = fromGate.getPin(fromNumber);
				} else if (XMLCreator.TYPE_WIREPOINT.equals(type)) {
					int x = Integer.parseInt(gnode.string("x"));
					int y = Integer.parseInt(gnode.string("y"));
					from = new WirePoint(x, y);
				} else if (XMLCreator.TYPE_WIRE.equals(type)) {
					String id = gnode.string("id");
					// search wire
					for (Wire w : ls.circuit.getWires()) {
						for (WirePoint wp : w.getPoints()) {
							if (id.equals(wp.getId())) {
								from = wp;
								break;
							}
						}
					}
				}
				if (from == null)
					System.out.println("from not found " + type);

				// to node
				gnode = wnode.child("to");
				type = gnode.optString("type");
				if (type == null || XMLCreator.TYPE_GATE.equals(type)) {
					String toGateId = gnode.string("id");
					int toNumber = Integer.parseInt(gnode.string("number"));

					Gate toGate = findGateById(ls.circuit.getGates(), toGateId);
					if (toGate == null)
						throw new RuntimeException(I18N.tr(Lang.READERROR) + ": cannot fing gate " + toGateId);
					to = toGate.getPin(toNumber);
				} else if (XMLCreator.TYPE_WIREPOINT.equals(type)) {
					int x = Integer.parseInt(gnode.string("x"));
					int y = Integer.parseInt(gnode.string("y"));
					to = new WirePoint(x, y);
				} else if (XMLCreator.TYPE_WIRE.equals(type)) {
					String id = gnode.string("id");
					// search wire
					for (Wire w : ls.circuit.getWires()) {
						for (WirePoint wp : w.getPoints()) {
							if (id.equals(wp.getId())) {
								to = wp;
								break;
							}
						}
					}
				}

				if (from == null || to == null) {
					String s = "wire cannot be connected ";
					if (from != null)
						s += " " + from.getId();
					if (to != null)
						s += " " + to.getId();
					// throw new RuntimeException("wire cannot be connected");
					ls.addError(s);
					continue;
				}
				Wire wire = new Wire(from, to);

				// load points
				for (Xml pnode : wnode.children("point")) {
					boolean b = Boolean.parseBoolean(pnode.string("node"));
					int xp = Integer.parseInt(pnode.string("x"));
					int yp = Integer.parseInt(pnode.string("y"));
					WirePoint wp = new WirePoint(xp, yp, b);
					wp.parent = wire;
					wire.addPoint(wp);
				}

				// load properties
				Properties ps = getProperties(wnode);
				for (Object k : ps.keySet()) {
					String key = (String) k;
					wire.setProperty(key, ps.getProperty(key));
				}
				wire.loadProperties();

				wire.selected = false;
				ls.circuit.addWire(wire);
				// connect wire to CircuitPart
				from.connect(wire);
				to.connect(wire);
				// wire.reset();
				// if (from instanceof Pin) {
				// wire.changedLevel(new LSLevelEvent(from, from.getLevel()));
				// }
			}
		}
		return ls;
	}

	private static Gate loadGate(Xml gnode) {
		// acquire basic information
		String type = gnode.string("type").toLowerCase();
		int x = Integer.parseInt(gnode.string("x"));
		int y = Integer.parseInt(gnode.string("y"));
		String optRotate = gnode.optString("rotate");
		String optMirror = gnode.optString("mirror");
		String optInputs = gnode.optString("inputs");

		Gate gate = null;
		try {
			Gate g = App.getGate(type);
			if (g == null)
				throw new RuntimeException("gate type '" + type + "' not there");
			gate = GateLoaderHelper.create(g);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage());
		}

		if (optInputs != null)
			gate.createDynamicInputs(Integer.parseInt(optInputs));
		gate.moveTo(x, y);
		if (optRotate != null) {
			int rot = Integer.parseInt(optRotate) / 90;
			for (int i = 0; i < rot; i++)
				gate.rotate();
		}
		if (optMirror != null) {
			if ("x".equals(optMirror))
				gate.mirror();
			else if ("y".equals(optMirror)) {
				gate.mirror();
				gate.mirror();
			} else if ("xy".equals(optMirror)) {
				gate.mirror();
				gate.mirror();
				gate.mirror();
			}
		}

		// settings
		Properties ps = getProperties(gnode);
		for (Object k : ps.keySet()) {
			String key = (String) k;
			gate.setProperty(key, ps.getProperty(key));
		}
		gate.loadProperties();

		// old
		if (gnode.children("io").size() > 0)
			loadPinsIO(gate, gnode);
		else
			loadPins(gate, gnode);

		Xml node = gnode.optChild("properties");
		if (node != null) {
			for (Xml n : node.children("property")) {
				gate.setProperty(n.name(), n.content());
			}
			gate.loadProperties();
		}
		return gate;
	}

	private static Properties getProperties(Xml gnode) {
		Properties ps = new Properties();
		Xml snode = gnode.optChild("properties");
		if (snode != null) {
			for (Xml n : snode.children("property")) {
				String key = n.string("key");
				ps.setProperty(key, n.content());
			}
		}
		return ps;
	}

	@Deprecated
	private static void loadPinsIO(Gate gate, Xml gnode) {
		// pins: in/output nodes (labels, numbers)
		for (Xml inode : gnode.children("io")) {
			String ioType = inode.string("iotype");

			int number = Integer.parseInt(inode.string("number"));

			String label = inode.optString("label");
			if (label != null)
				gate.getPin(number).setProperty(CircuitPart.TEXT, label);
			gate.getPin(number).loadProperties();

			if ("input".equals(ioType)) {
				String inpType = inode.optString("type");
				if (inpType != null) {
					int inputType = 0;
					if ("high".equals(inpType)) {
						inputType = Pin.HIGH;
					} else if ("low".equals(inpType)) {
						inputType = Pin.LOW;
					} else if ("inv".equals(inpType)) {
						inputType = Pin.INVERTED;
					}
					gate.getPin(number).levelType = inputType;
				}
			}
		}
	}

	private static void loadPins(Gate gate, Xml gnode) {
		// pins: in/output nodes (labels, numbers)
		for (Xml inode : gnode.children("pin")) {
			String ioType = inode.string("iotype");
			int number = Integer.parseInt(inode.string("number"));
			Properties ps = getProperties(inode);
			for (Object k : ps.keySet()) {
				String key = (String) k;
				gate.getPin(number).setProperty(key, ps.getProperty(key));
			}
			gate.getPin(number).loadProperties();

			if (XMLCreator.INPUT.equals(ioType)) {
				String inpType = inode.optString("type");
				if (inpType != null) {
					int inputType = 0;
					if ("high".equals(inpType)) {
						inputType = Pin.HIGH;
					} else if ("low".equals(inpType)) {
						inputType = Pin.LOW;
					} else if ("inv".equals(inpType)) {
						inputType = Pin.INVERTED;
					}
					gate.getPin(number).levelType = inputType;
				}
			}
		}
	}

	private static Gate findGateById(Vector<Gate> gates, String id) {
		for (Gate gate : gates) {
			if (id.equals(gate.getId()))
				return gate;
		}
		return null;
	}

	public static ArrayList<String> getModuleListFromFile(String fileName) {
		ArrayList<String> moduleList = new ArrayList<String>();
		Xml doc = new Xml(fileName, ROOT_STRING);
		String version = doc.string("version");
		if (!formatVersion.equals(version))
			throw new RuntimeException(I18N.tr(Lang.READERROR) + ": version does not match");

		Xml node = doc.optChild("gates");
		if (node != null) {
			for (Xml gnode : node.children("gate")) {
				String type = gnode.string("type").toLowerCase();
				String module = gnode.optString("module");
				if ("true".equals(module) && !moduleList.contains(type.toLowerCase())) {
					moduleList.add(type.toLowerCase());
				}
			}
		}
		return moduleList;
	}
}
