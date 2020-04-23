package logicsim;

import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.StringWriter;
import java.util.HashMap;
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
 * Generates a verilog file corresponding to the circuit
 * Individual components, other than simple logic gates, are expressed as modules,
 * thus corresponding modules with the correct I/O labels need to be present in the file system already
 * to give a full verilog implementation
 * 
 * @author Matthew Lister
 * @version 1.0
 */
public class VerilogCreator {

	public static void createVerilog(VerilogFile f) throws RuntimeException {
		
		HashMap<String, Integer> nameCounter = new HashMap<String, Integer>();
		HashMap<CircuitPart, String> nameLookup = new HashMap<CircuitPart, String>();
		
		    try {
		      File vOutput = new File(f.fileName);
		      if (vOutput.createNewFile()) {
				    
				    try {
				    	FileWriter vWriter = new FileWriter(f.fileName);
				        vWriter.write("module " + f.fileNameNE + "(");
				        vWriter.write(System.lineSeparator());

				        writeInputs(vOutput, vWriter, f, nameCounter, nameLookup);
				        vWriter.write(System.lineSeparator());
				        writeOutputs(vOutput, vWriter, f, nameCounter, nameLookup);
				        vWriter.write(");" + System.lineSeparator());
				        vWriter.write(System.lineSeparator());
				        vWriter.write(System.lineSeparator());
				        vWriter.write(System.lineSeparator());
				        
				        writeWires(vOutput, vWriter, f, nameCounter, nameLookup);
				        vWriter.write(System.lineSeparator());
				        vWriter.write(System.lineSeparator());
				        
				        writeGates(vOutput, vWriter, f, nameCounter, nameLookup);
				        vWriter.write(System.lineSeparator());
				        vWriter.write(System.lineSeparator());

				        vWriter.write("endmodule");
				        
				        
				        vWriter.close();
				    } catch (IOException e) {
				        System.out.println("An error occurred.");
				        e.printStackTrace();
				    }
				    
		        System.out.println("File created: " + vOutput.getName());
		      } else {
		        System.out.println("File already exists.");
		      }
		    } catch (IOException e) {
		      System.out.println("An error occurred.");
		      e.printStackTrace();
		    }
	}
	
	public static String getVName(CircuitPart p) {
		if (p.text == "<Label>") {
			return p.type;
		}
		else {
			return p.text;
		}
	}
	
	public static String getPinLabel(Pin p, Wire w) {
		if (p.label != null) {
			return p.label;
		}
		else if (p.text != "<Label>") {
			return p.text;
		}
		else if (w != null) {
			if (w.text != "<Label>") {
				return w.text;
			}
			else {
				return p.type;
			}
		}
		else {
			return p.type;
		}
	}

	public static void writeInputs(File voutput, FileWriter vWriter, VerilogFile f, HashMap<String, Integer> nameCounter, HashMap<CircuitPart, String> nameLookup) {
		boolean isFirst = true;
		if (f.getGates() != null) {
			for (Gate g : f.getGates()) {
				
				if (g.getInputs() != null) {
					for (Pin i : g.getInputs()) {
						
						boolean isFree = true;
						if (f.getWires() != null) {
							for (Wire w : f.getWires()) {
								isFree = (isFree && (w.to != i));
							}
						}
						if (isFree) {
							String vName = getVName(i);
							Integer vNameCount;
							if (nameCounter.containsKey(vName)) {
								vNameCount = nameCounter.get(vName) + 1;
							}
							else {
								vNameCount = 1;
							}
							nameCounter.put(vName, vNameCount);
							
							try {
								if (!isFirst) {
									vWriter.write("," + System.lineSeparator());
								}
								else {
									isFirst = false;
								}
								if (vNameCount > 1) {
									vWriter.write("			input wire " + vName + Integer.toString(vNameCount));
									nameLookup.put(i, vName + Integer.toString(vNameCount));
								}
								else {
									vWriter.write("			input wire " + vName);
									nameLookup.put(i, vName);
								}
							} 
							catch (IOException e) {
								    System.out.println("Input printing error");
							}
						}
						
					}
				}
				
			}
		}
	}
	
	public static void writeOutputs(File voutput, FileWriter vWriter, VerilogFile f, HashMap<String, Integer> nameCounter, HashMap<CircuitPart, String> nameLookup) {
		boolean isFirst = true;
		if (f.getGates() != null) {
			for (Gate g : f.getGates()) {
				
				if (g.getInputs() != null) {
					isFirst = false;
				}
			}
		}
		if (f.getGates() != null) {
			for (Gate g : f.getGates()) {
				
				if (g.getInputs() != null) {
					for (Pin i : g.getOutputs()) {
						
						boolean isFree = true;
						if (f.getWires() != null) {
							for (Wire w : f.getWires()) {
								isFree = (isFree && (w.from != i));
							}
						}
						if (isFree) {
							String vName = getVName(i);
							Integer vNameCount;
							if (nameCounter.containsKey(vName)) {
								vNameCount = nameCounter.get(vName) + 1;
							}
							else {
								vNameCount = 1;
							}
							nameCounter.put(vName, vNameCount);
							
							try {
								if (!isFirst) {
									vWriter.write("," + System.lineSeparator());
								}
								if (vNameCount > 1) {
									vWriter.write("			output wire " + vName + Integer.toString(vNameCount));
									nameLookup.put(i, vName + Integer.toString(vNameCount));
								}
								else {
									vWriter.write("			output wire " + vName);
									nameLookup.put(i, vName);
								}
							} 
							catch (IOException e) {
								    System.out.println("Input printing error");
							}
						}
						
					}
				}
				
			}
		}
	}
	
	public static void writeWires(File voutput, FileWriter vWriter, VerilogFile f, HashMap<String, Integer> nameCounter, HashMap<CircuitPart, String> nameLookup) {
		if (f.getWires() != null) {
			for (Wire w : f.getWires()) {
				String vName = getVName(w);
				Integer vNameCount;
				if (nameCounter.containsKey(vName)) {
					vNameCount = nameCounter.get(vName) + 1;
				}
				else {
					vNameCount = 1;
				}
				String vNameNum = vName;
				if (vNameCount > 1 || vName == "wire") {
					vNameNum = vName + Integer.toString(vNameCount);
				}
				nameCounter.put(vName, vNameCount);
				nameLookup.put(w, vNameNum);
				
				try {
					vWriter.write("	wire " + vNameNum + ";");
					vWriter.write(System.lineSeparator());
				} 
				catch (IOException e) {
					    System.out.println("Input printing error");
				}
			}
		}
	}
	
	public static void writeGates(File voutput, FileWriter vWriter, VerilogFile f, HashMap<String, Integer> nameCounter, HashMap<CircuitPart, String> nameLookup) {
		if (f.getGates() != null) {
			for (Gate g : f.getGates()) {
				String vName = getVName(g);
				String vModule = g.type;
				Integer vNameCount;
				if (nameCounter.containsKey(vName)) {
					vNameCount = nameCounter.get(vName) + 1;
				}
				else {
					vNameCount = 1;
				}
				String vNameNum = vName;
				if (vNameCount > 1) {
					vNameNum = vName + Integer.toString(vNameCount);
				}
				nameCounter.put(vName, vNameCount);
				
				try {
					vWriter.write("	" + vModule);
					vWriter.write(System.lineSeparator());
					vWriter.write("	  " + vNameNum + " (");
					vWriter.write(System.lineSeparator());
				} 
				catch (IOException e) {
				    System.out.println("Input printing error");
				    System.out.println("Input printing error");
				} 
				boolean isFirst = false;
				if (g.getPins() != null) {
					isFirst = true;
				}
				if (g.getInputs() != null) {
					for (Pin i : g.getInputs()) {
						
						String wireName = "error";
						String pinLabel = "error";
						boolean isFree = true;
						if (f.getWires() != null) {
							for (Wire w : f.getWires()) {
								if (w.to == i) {
									isFree = false;
									wireName = nameLookup.get(w);
									pinLabel = getPinLabel(i, w);
								}
							}
						}
						if (isFree) {
							wireName = nameLookup.get(i);
							pinLabel = getPinLabel(i, null);
						}
						try {
							if (isFirst) {
								isFirst = false;
								vWriter.write("	  ." + pinLabel + "(" + wireName + ")");
							}
							else {
								vWriter.write("," + System.lineSeparator());
								vWriter.write("	  ." + pinLabel + "(" + wireName + ")");
							}	
						} 
						catch (IOException e) {
						    System.out.println("Input printing error");
						    System.out.println("Input printing error");
						} 
					}
				}
				if (g.getOutputs() != null) {
					for (Pin i : g.getOutputs()) {
						
						String wireName = "error";
						String pinLabel = "error";
						boolean isFree = true;
						if (f.getWires() != null) {
							for (Wire w : f.getWires()) {
								if (w.from == i) {
									isFree = false;
									wireName = nameLookup.get(w);
									pinLabel = getPinLabel(i, w);
								}
							}
						}
						if (isFree) {
							wireName = nameLookup.get(i);
							pinLabel = getPinLabel(i, null);
						}
						try {
							if (isFirst) {
								isFirst = false;
								vWriter.write("	  ." + pinLabel + "(" + wireName + ")");
							}
							else {
								vWriter.write("," + System.lineSeparator());
								vWriter.write("	  ." + pinLabel + "(" + wireName + ")");
							}	
						} 
						catch (IOException e) {
						    System.out.println("Input printing error");
						    System.out.println("Input printing error");
						} 
					}
				}
				
				try {
					vWriter.write(System.lineSeparator());
					vWriter.write("	" + ");");
					vWriter.write(System.lineSeparator());
				} 
				catch (IOException e) {
				    System.out.println("Input printing error");
				    System.out.println("Input printing error");
				} 
					
			}
		}
	}

}
