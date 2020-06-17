package logicsim;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * XML Creator for circuit and module files
 * 
 * taken from https://argonrain.wordpress.com/2009/10/27/000/
 * 
 * @author Peter Gabriel
 * @version 1.0
 */
public class XMLCreator {

	static final String INPUT = "input";
	static final String OUTPUT = "output";
	static final String TYPE_WIRE = "wire";
	static final String TYPE_WIREPOINT = "point";
	static final String TYPE_GATE = "gate";

	public static String createXML(LogicSimFile f) throws RuntimeException {
		DocumentBuilderFactory xmlFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder xmlBuilder;
		try {
			xmlBuilder = xmlFactory.newDocumentBuilder();
			Document doc = xmlBuilder.newDocument();
			String rootName = "logicsim";

			if (f.circuit.isModule()) {
				rootName = "logicsim";
			}
			Element mainRootElement = doc.createElement(rootName);
			mainRootElement.setAttribute("version", XMLLoader.formatVersion);
			doc.appendChild(mainRootElement);

			if (f.info != null) {
				Element node = doc.createElement("info");
				for (Iterator<String> iterator = f.info.keySet().iterator(); iterator.hasNext();) {
					String key = iterator.next();
					String value = f.info.get(key);
					Element n = doc.createElement(key);
					n.setTextContent(value);
					node.appendChild(n);
				}
				mainRootElement.appendChild(node);
			}

			if (f.getGates() != null) {
				Element node = doc.createElement("gates");
				for (Gate g : f.getGates()) {
					Node gnode = createGateNode(doc, g);
					node.appendChild(gnode);
				}
				mainRootElement.appendChild(node);
			}

			if (f.getWires() != null) {
				Element node = doc.createElement("wires");
				for (Wire w : f.getWires()) {
					Node wnode = createWireNode(doc, w);
					node.appendChild(wnode);
				}
				mainRootElement.appendChild(node);
			}

			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty(OutputKeys.STANDALONE, "no");
			DOMSource source = new DOMSource(doc);

			if (f.fileName == null) {
				StringWriter writer = new StringWriter();
				transformer.transform(source, new StreamResult(writer));
				String xmlString = writer.getBuffer().toString();
				return xmlString;
			} else {
				FileOutputStream outStream = new FileOutputStream(new File(f.fileName));
				transformer.transform(source, new StreamResult(outStream));
				f.changed = false;
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return null;
	}

	public static Node createGateNode(Document doc, Gate g) {
		Element node = doc.createElement("gate");
		if (g instanceof Module) {
			node.setAttribute("type", ((Module) g).type);
			node.setAttribute("module", "true");
		} else {
			node.setAttribute("type", g.type);
		}
		node.setAttribute("x", String.valueOf(g.getX()));
		node.setAttribute("y", String.valueOf(g.getY()));

		if (g.rotate90 != 0) {
			node.setAttribute("rotate", String.valueOf(g.rotate90 * 90));
		}
		if (g.mirror != 0) {
			String val = null;
			if (g.mirror == Gate.XAXIS)
				val = "x";
			else if (g.mirror == Gate.YAXIS)
				val = "y";
			else if (g.mirror == Gate.BOTH_AXES)
				val = "xy";
			if (val != null) {
				node.setAttribute("mirror", val);
			}
		}

		if (g.supportsVariableInputs() && g.getNumInputs() != 2)
			node.setAttribute("inputs", String.valueOf(g.getNumInputs()));

		// settings
		Node snode = createSettingsNode(doc, g.getProperties());
		if (snode != null)
			node.appendChild(snode);

		for (Pin c : g.getInputs()) {
			if ((g instanceof MODIN && c.getProperty(CircuitPart.TEXT) != null)
					|| (c.getIoType() == Pin.INPUT && c.levelType != Pin.NORMAL) || (c.text != "<Label>")) {
				node.appendChild(createPinNode(doc, c));
			}
		}
		for (Pin c : g.getOutputs()) {
			if ((g instanceof MODOUT && c.getProperty(CircuitPart.TEXT) != null)
					|| (c.getIoType() == Pin.INPUT && c.levelType != Pin.NORMAL) || (c.text != "<Label>")) {
				node.appendChild(createPinNode(doc, c));
			}
		}
		return node;
	}

	private static Node createSettingsNode(Document doc, Properties ps) {
		if (ps.size() > 0) {
			Element node = doc.createElement("properties");
			for (Object key : ps.keySet()) {
				String keyS = (String) key;
				Element n = doc.createElement("property");
				n.setAttribute("key", keyS);
				String value = ps.getProperty(keyS);
				if (value == null || value.length() == 0)
					continue;
				n.setTextContent(value);
				node.appendChild(n);
			}
			// check if node contains properties
			if (node.hasChildNodes())
				return node;
		}
		return null;
	}

	private static Node createPinNode(Document doc, Pin pin) {
		Element node = doc.createElement("pin");
		String ioType = INPUT;
		if (pin.isOutput())
			ioType = OUTPUT;
		node.setAttribute("iotype", ioType);
		node.setAttribute("number", String.valueOf(pin.number));
		if (pin.isInput()) {
			int inputType = pin.levelType;
			if (inputType != Pin.NORMAL) {
				String inpType = "";
				if (inputType == Pin.HIGH)
					inpType = "high";
				else if (inputType == Pin.LOW)
					inpType = "low";
				else if (inputType == Pin.INVERTED)
					inpType = "inv";
				node.setAttribute("type", inpType);
			}
		}
		Node n = createSettingsNode(doc, pin.getProperties());
		if (n != null)
			node.appendChild(n);
		return node;
	}

	private static Node createWireNode(Document doc, Wire w) {
		if (w != null) {
			Element n = doc.createElement("wire");
			if (w.getFrom() != null) {
				Element g = doc.createElement("from");
				if (w.getFrom() instanceof Pin) {
					Pin p = (Pin) w.getFrom();
					g.setAttribute("type", "gate");
					g.setAttribute("id", p.parent.getId());
					g.setAttribute("number", String.valueOf(p.number));
				} else if (w.getFrom() instanceof WirePoint) {
					WirePoint wp = (WirePoint) w.getFrom();
					String type = TYPE_WIRE;
					if (wp.parent == null || w.equals(wp.parent))
						type = TYPE_WIREPOINT;
					g.setAttribute("type", type);
					if (TYPE_WIRE.equals(type)) {
						g.setAttribute("id", wp.getId());
					} else {
						g.setAttribute("x", String.valueOf(wp.getX()));
						g.setAttribute("y", String.valueOf(wp.getY()));
					}
				}
				n.appendChild(g);
			}

			if (w.getTo() != null) {
				Element g = doc.createElement("to");
				if (w.getTo() instanceof Pin) {
					Pin p = (Pin) w.getTo();
					g.setAttribute("type", TYPE_GATE);
					g.setAttribute("id", p.parent.getId());
					g.setAttribute("number", String.valueOf(p.number));
				} else if (w.getTo() instanceof WirePoint) {
					// distinguish between own wirepoint and foreign wirepoint
					WirePoint wp = (WirePoint) w.getTo();
					String type = TYPE_WIRE;
					if (wp.parent == null || w.equals(wp.parent))
						type = TYPE_WIREPOINT;
					g.setAttribute("type", type);
					if (TYPE_WIRE.equals(type)) {
						g.setAttribute("id", wp.getId());
					} else {
						g.setAttribute("x", String.valueOf(wp.getX()));
						g.setAttribute("y", String.valueOf(wp.getY()));
					}
				}
				n.appendChild(g);
			}
			Vector<WirePoint> pts = w.getPoints();
			for (int i = 0; i < pts.size(); i++) {
				WirePoint wp = pts.get(i);
				Element point = doc.createElement("point");
				point.setAttribute("number", String.valueOf(i));
				point.setAttribute("x", String.valueOf(wp.getX()));
				point.setAttribute("y", String.valueOf(wp.getY()));
				point.setAttribute("node", String.valueOf(wp.show));
				n.appendChild(point);
			}

			// label
			Node snode = createSettingsNode(doc, w.getProperties());
			if (snode != null)
				n.appendChild(snode);

			return n;
		}
		return null;
	}
}
