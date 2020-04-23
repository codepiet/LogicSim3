package logicsim;

import java.util.ArrayList;
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
		Vector<Gate> gates = new Vector<Gate>();
		node = doc.optChild("gates");
		if (node != null) {
			for (Xml gnode : node.children("gate")) {
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
					if (gate.height != gate.width) {
						gate.rotate();
					} else {
						for (int i = 0; i < rot; i++)
							gate.rotate();
					}
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
				Xml snode = gnode.optChild("properties");
				if (snode != null) {
					for (Xml n : snode.children("property")) {
						String key = n.string("key");
						gate.setProperty(key, n.content());
					}
					gate.loadProperties();
				}

				// in/output nodes (labels, numbers)
				for (Xml inode : gnode.children("io")) {
					String ioType = inode.string("iotype");
					int pinNumber = Integer.parseInt(inode.string("number"));
					Pin iPin = gate.getPin(pinNumber);
					if ("input".equals(ioType)) {

						String label = inode.optString("label");
						iPin.label = label;

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
							iPin.levelType = inputType;
						}
						
					} else {
						String label = inode.optString("label");
						iPin.label = label;
					}
					Xml isnode = inode.optChild("properties");
					if (isnode != null) {
						for (Xml n : isnode.children("property")) {
							String key = n.string("key");
							iPin.setProperty(key, n.content());
						}
						iPin.loadProperties();
					}
				}
				node = doc.optChild("properties");
				if (node != null) {
					for (Xml n : node.children("property")) {
						gate.setProperty(n.name(), n.content());
					}
					gate.loadProperties();
				}
				gate.selected = false;
				gates.add(gate);
			}
		}

		Vector<Wire> wires = new Vector<Wire>();
		node = doc.optChild("wires");
		if (node != null) {
			for (Xml wnode : node.children("wire")) {
				Xml gnode = wnode.child("from");
				String fromGateId = gnode.string("id");
				int fromNumber = Integer.parseInt(gnode.string("number"));
				gnode = wnode.child("to");
				String toGateId = gnode.string("id");
				int toNumber = Integer.parseInt(gnode.string("number"));

				// try to get the gates
				Gate fromGate = findGateById(gates, fromGateId);
				Gate toGate = findGateById(gates, toGateId);
				if (fromGate == null)
					throw new RuntimeException(I18N.tr(Lang.READERROR) + ": from gate is null");
				if (toGate == null)
					throw new RuntimeException(I18N.tr(Lang.READERROR) + ": to gate is null");

				Pin from = fromGate.getPin(fromNumber);
				Pin to = toGate.getPin(toNumber);
				Wire wire = new Wire(from, to);
				for (Xml pnode : wnode.children("point")) {
					boolean b = Boolean.parseBoolean(pnode.string("node"));
					int xp = Integer.parseInt(pnode.string("x"));
					int yp = Integer.parseInt(pnode.string("y"));
					WirePoint wp = new WirePoint(xp, yp, b);
					wire.points.add(wp);
				}

				// settings
				Xml snode = wnode.optChild("properties");
				if (snode != null) {
					for (Xml n : snode.children("property")) {
						String key = n.string("key");
						wire.setProperty(key, n.content());
					}
					wire.loadProperties();
				}
				
				wire.selected = false;
				// connect wire to gates
				wires.add(wire);
				from.connect(wire);
				to.connect(wire);
			}
		}
		ls.setGates(gates);
		ls.setWires(wires);
		return ls;
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
