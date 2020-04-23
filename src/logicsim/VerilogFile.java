package logicsim;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class VerilogFile {
	Circuit circuit = new Circuit();
	Map<String, String> info = new HashMap<String, String>();
	String fileName;
	//file name without extension
	String fileNameNE;

	public VerilogFile(String fileName) {
		this.fileName = fileName;
	}

	public VerilogFile(String fileName, Map<String, String> info, Vector<Gate> gates, Vector<Wire> wires) {
		this(fileName);
		this.info = info;
		this.circuit.setGates(gates);
	}

	/**
	 * extract the pure file name from an absolute path
	 * 
	 * @param fileName
	 * @return
	 */
	public String extractFileName() {
		File f = new File(fileName);
		String name = f.getName();
		// strip extension
		name = name.substring(0, name.lastIndexOf('.'));
		return name;
	}

	public Vector<Gate> getGates() {
		return circuit.getGates();
	}

	public void setGates(Vector<Gate> gates) {
		circuit.setGates(gates);
	}

	public void setWires(Vector<Wire> wires) {
		circuit.setWires(wires);
	}

	public Vector<Wire> getWires() {
		return circuit.getWires();
	}

	private String getKey(String key) {
		return info.containsKey(key) ? info.get(key) : null;
	}

	public void setLabel(String value) {
		info.put("label", value);
	}

	public void setDescription(String value) {
		info.put("description", value);
	}

	public String getLabel() {
		return getKey("label");
	}

	public String getDescription() {
		return getKey("description");
	}
}
