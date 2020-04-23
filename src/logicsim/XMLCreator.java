package logicsim;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.Properties;

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
		Node snode = createSettingsNode(doc, g);
		if (snode != null)
			node.appendChild(snode);

		for (Pin c : g.getInputs()) {
			if ((g instanceof MODIN && c.label != null) || (c.ioType == Pin.INPUT && c.levelType != Pin.NORMAL) || (c.text != "<Label>")) {
				node.appendChild(createInputNode(doc, c));
			}
		}
		for (Pin c : g.getOutputs()) {
			if ((g instanceof MODOUT && c.label != null) || (c.ioType == Pin.INPUT && c.levelType != Pin.NORMAL) || (c.text != "<Label>")) {
				node.appendChild(createOutputNode(doc, c));
			}
		}
		return node;
	}

	private static Node createSettingsNode(Document doc, CircuitPart g) {
		if (g.getProperties().size() > 0) {
			Element node = doc.createElement("properties");
			Properties properties = g.getProperties();
			for (Object key : properties.keySet()) {
				String keyS = (String) key;
				Element n = doc.createElement("property");
				n.setAttribute("key", keyS);
				n.setTextContent(properties.getProperty(keyS));
				node.appendChild(n);
			}
			return node;
		}
		return null;
	}

	private static Node createOutputNode(Document doc, Pin output) {
		Element node = doc.createElement("io");
		node.setAttribute("iotype", "output");
		node.setAttribute("number", String.valueOf(output.number));

		if (output.label != null)
			node.setAttribute("label", output.label);
		
		// settings
		Node snode = createSettingsNode(doc, output);
		if (snode != null)
			node.appendChild(snode);
		
		return node;
	}

	private static Node createInputNode(Document doc, Pin input) {
		Element node = doc.createElement("io");
		node.setAttribute("iotype", "input");
		node.setAttribute("number", String.valueOf(input.number));
		int inputType = input.levelType;
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
		if (input.label != null) {
			node.setAttribute("label", input.label);
		}

		// settings
		Node snode = createSettingsNode(doc, input);
		if (snode != null)
			node.appendChild(snode);
		
		return node;
	}

	private static Node createWireNode(Document doc, Wire w) {
		if (w != null) {
			Element n = doc.createElement("wire");
			if (w.from != null) {
				Element g = doc.createElement("from");
				if (w.from instanceof Pin) {
					Pin p = (Pin) w.from;
					g.setAttribute("id", p.gate.getId());
					g.setAttribute("number", String.valueOf(p.number));
					n.appendChild(g);
				}
			}

			if (w.to != null) {
				Element g = doc.createElement("to");
				if (w.to instanceof Pin) {
					Pin p = (Pin) w.to;
					g.setAttribute("type", "gate");
					g.setAttribute("id", p.gate.getId());
					g.setAttribute("number", String.valueOf(p.number));
				}
				n.appendChild(g);
			}

			for (int i = 0; i < w.points.size(); i++) {
				WirePoint wp = w.points.get(i);
				Element point = doc.createElement("point");
				point.setAttribute("number", String.valueOf(i));
				point.setAttribute("x", String.valueOf(wp.getX()));
				point.setAttribute("y", String.valueOf(wp.getY()));
				point.setAttribute("node", String.valueOf(wp.show));
				n.appendChild(point);
			}	
			Node snode = createSettingsNode(doc, w);
			if (snode != null)
				n.appendChild(snode);
			return n;
		}
		return null;
	}
}
