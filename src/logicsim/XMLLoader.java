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
			throw new RuntimeException(I18N.getString(Lang.ERR_READ) + ": version does not match");
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
				String optInputs = gnode.optString("inputs");

				Gate gate = null;
				try {
					Gate g = App.getGate(type);
					if (g == null)
						throw new RuntimeException(
								I18N.getString(Lang.ERR_READ) + ": gate type '" + type + "' not there");
					gate = GateInstanciator.create(g);
				} catch (Exception e) {
					throw new RuntimeException(I18N.getString(Lang.ERR_READ) + ": " + e.getMessage());
				}

				if (optInputs != null)
					gate.setNumInputs(Integer.parseInt(optInputs));
				gate.moveTo(x, y);

				// settings
				Xml snode = gnode.optChild("properties");
				if (snode != null) {
					for (Xml n : snode.children("property")) {
						String key = n.string("key");
						gate.setProperty(key, n.content());
					}
					gate.loadProperties();
				}

				// output nodes (labels, numbers)
				for (Xml inode : gnode.children("io")) {
					String ioType = inode.string("iotype");
					if ("input".equals(ioType)) {
						int inputNumber = Integer.parseInt(inode.string("number"));

						String label = inode.optString("label");
						gate.getInputs().get(inputNumber).label = label;

						String inpType = inode.optString("type");
						if (inpType != null) {
							int inputType = 0;
							if ("high".equals(inpType)) {
								inputType = Connector.HIGH;
							} else if ("low".equals(inpType)) {
								inputType = Connector.LOW;
							} else if ("inv".equals(inpType)) {
								inputType = Connector.INVERTED;
							}
							gate.getInputs().get(inputNumber).levelType = inputType;
						}
					} else {
						int outputNumber = Integer.parseInt(inode.string("number"));
						String label = inode.optString("label");
						gate.getOutputs().get(outputNumber).label = label;
					}
				}
				node = doc.optChild("properties");
				if (node != null) {
					for (Xml n : node.children("property")) {
						gate.setProperty(n.name(), n.content());
					}
					gate.loadProperties();
				}
				gate.active = false;
				gates.add(gate);
			}
		}
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
					throw new RuntimeException(I18N.getString(Lang.ERR_READ) + ": from gate is null");
				if (toGate == null)
					throw new RuntimeException(I18N.getString(Lang.ERR_READ) + ": to gate is null");

				Wire wire = new Wire(fromGate.getOutput(fromNumber), toGate.getInput(toNumber));
				for (Xml pnode : wnode.children("point")) {
					boolean b = Boolean.parseBoolean(pnode.string("node"));
					int xp = Integer.parseInt(pnode.string("x"));
					int yp = Integer.parseInt(pnode.string("y"));
					WirePoint wp = new WirePoint(xp, yp, b);
					wire.points.add(wp);
				}
				wire.active = false;
				// connect wire to gates
				fromGate.setOutputWire(fromNumber, wire);
				toGate.setInputWire(toNumber, wire);
			}
		}
		ls.setGates(gates);
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
			throw new RuntimeException(I18N.getString(Lang.ERR_READ) + ": version does not match");

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
