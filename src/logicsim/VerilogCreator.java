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
 * For proper output, all pins on gates used should have their label characteristic set as the title of that pin in the appropriate module.
 * Other labels (i.e. the "text" attribute annotations added via properties) are not strictly necessary but will produce a much more comprehensible output.
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
				        
				        buildLookupTables(f, nameCounter, nameLookup);

				        writeInputs(vOutput, vWriter, f, nameCounter, nameLookup);
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
	
	public static void buildLookupTables(VerilogFile f, HashMap<String, Integer> nameCounter, HashMap<CircuitPart, String> nameLookup) {
		if (f.getGates() != null) {
			for (Gate g : f.getGates()) {
				
				String vName = getVName(g);
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
				nameLookup.put(g, vNameNum);
				
				if (g.getInputs() != null) {
					for (Pin i : g.getInputs()) {
						
						boolean isFree = true;
						if (f.getWires() != null) {
							for (Wire w : f.getWires()) {
								
								Pin toPin;
								
								if (w.getTo() instanceof Pin) {
									toPin = (Pin) w.getTo();
									isFree = (isFree && toPin != i);
								};
							}
						}
						if (isFree) {
							String pinVName = getVName(i);
							Integer pinVNameCount;
							if (nameCounter.containsKey(pinVName)) {
								pinVNameCount = nameCounter.get(pinVName) + 1;
							}
							else {
								pinVNameCount = 1;
							}
							nameCounter.put(pinVName, pinVNameCount);
							if (pinVNameCount > 1) {
								nameLookup.put(i, pinVName + Integer.toString(pinVNameCount));
							}
							else {
								nameLookup.put(i, pinVName);
							}
						}
					}
				}
				if (g.getOutputs() != null) {
					for (Pin i : g.getOutputs()) {
						

						String pinVName = getVName(i);
						Integer pinVNameCount;
						if (nameCounter.containsKey(pinVName)) {
							pinVNameCount = nameCounter.get(pinVName) + 1;
						}
						else {
							pinVNameCount = 1;
						}
						nameCounter.put(pinVName, pinVNameCount);
						
						if (pinVNameCount > 1) {
							nameLookup.put(i, pinVName + Integer.toString(pinVNameCount));
						}
						else {
							nameLookup.put(i, pinVName);
						}
						
						if (f.getWires() != null) {
							for (Wire w : f.getWires()) {
								
								Pin fromPin;
								
								if (w.getFrom() instanceof Pin) {
									fromPin = (Pin) w.getFrom();

									if (fromPin == i) {
										nameLookup.put(w, nameLookup.get(i));
									}
								};
							}
						}
						
					}
				}
				
			}
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
		if (p.label != null && p.label != "<Label>") {
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
								
								Pin toPin;
								
								if (w.getTo() instanceof Pin) {
									toPin = (Pin) w.getTo();
									isFree = (isFree && toPin != i);
								};
								
							}
						}
						if (isFree) {
							String vName = nameLookup.get(i);
							Integer vNameCount;
							if (nameCounter.containsKey(vName)) {
								vNameCount = nameCounter.get(vName) + 1;
							}
							else {
								vNameCount = 1;
							}
							nameCounter.put(vName, vNameCount);
							String pinLabel = getPinLabel(i, null);
							
							try {
								if (pinLabel.charAt(0) != '_') {
									if (!isFirst) {
										vWriter.write("," + System.lineSeparator());
									}
									else {
										isFirst = false;
									}
									
									vWriter.write("			input wire " + vName);
									
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
				
				if (g.getOutputs() != null) {
					for (Pin i : g.getOutputs()) {
						
						boolean isFree = true;
						if (f.getWires() != null) {
							for (Wire w : f.getWires()) {
								
								Pin fromPin;
								
								if (w.getFrom() instanceof Pin) {
									fromPin = (Pin) w.getFrom();
									isFree = (isFree && fromPin != i);
								};
							}
						}
						if (isFree || nameLookup.get(i).charAt(0) == 'M') {
							String vName = nameLookup.get(i);
							
							String pinLabel = getPinLabel(i, null);
							
							try {
								if (pinLabel.charAt(0) != '_') {
									if (!isFirst) {
										vWriter.write("," + System.lineSeparator());
									}
									else {
										isFirst = false;
									}
									
									vWriter.write("			output wire " + vName);
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
		if (f.getGates() != null) {
			for (Gate g : f.getGates()) {
				
				if (g.getOutputs() != null) {
					for (Pin i : g.getOutputs()) {
						
						boolean isFree = true;
						
						Pin fromPin;
						
						if (f.getWires() != null) {
							for (Wire w : f.getWires()) {
								
								
								if (w.getFrom() instanceof Pin) {
									fromPin = (Pin) w.getFrom();
									isFree = (isFree && (fromPin != i));
								};
							}
						}
						if (!isFree && nameLookup.get(i).charAt(0) != 'M') {
							try {
								vWriter.write("	wire " + nameLookup.get(i) + ";");
								vWriter.write(System.lineSeparator());
							} catch (IOException e) {
							    System.out.println("Wire printing error");
							}
						}
						
					}
				}
				
			}
		}
		
	}
	
	public static void writeGates(File voutput, FileWriter vWriter, VerilogFile f, HashMap<String, Integer> nameCounter, HashMap<CircuitPart, String> nameLookup) {
		if (f.getGates() != null) {
			for (Gate g : f.getGates()) {
				//separate case for writing elementary logic gates
				if (g.type == "and" || g.type == "or" || g.type == "xor" || g.type == "nand" || g.type == "nor" || g.type == "xnor" || g.type == "not") {
					
					try {
						vWriter.write("	" + g.type + " (");
					}
					catch (IOException e) {
					    System.out.println("Logic gate printing error");
					} 
					
					boolean isFirst = true;
					if (g.getOutputs() != null) {
						for (Pin i : g.getOutputs()) {
							
							String wireName = "error";
							boolean isFree = true;
							Pin fromPin;
							
							if (f.getWires() != null) {
								for (Wire w : f.getWires()) {
									
									if (w.getFrom() instanceof Pin) {
										fromPin = (Pin) w.getFrom();
										if (fromPin == i) {
											isFree = false;
											wireName = nameLookup.get(w);
										}
									};
									

								}
							}
							if (isFree) {
								wireName = nameLookup.get(i);
							}
							try {
								if (isFirst) {
									isFirst = false;
									vWriter.write(wireName);
								}
								else {
									vWriter.write(", " + wireName);
								}	
							} 
							catch (IOException e) {
							    System.out.println("Logic gate input printing error");
							} 
						}
					}
					if (g.getInputs() != null) {
						for (Pin i : g.getInputs()) {
							
							String wireName = "error";
							boolean isFree = true;
							if (f.getWires() != null) {
								for (Wire w : f.getWires()) {
									
									Pin toPin;
									
									if (w.getTo() instanceof Pin) {
										toPin = (Pin) w.getTo();

										if (toPin == i) {
											isFree = false;
											wireName = nameLookup.get(w);
										}
									};
									
								}
							}
							if (isFree) {
								wireName = nameLookup.get(i);
							}
							try {
								if (isFirst) {
									isFirst = false;
									vWriter.write(wireName);
								}
								else {
									vWriter.write(", " + wireName);
								}	
							} 
							catch (IOException e) {
							    System.out.println("logic gate output printing error");
							} 
						}
					}
					
					try {
						vWriter.write(");");
						vWriter.write(System.lineSeparator());
					}
					catch (IOException e) {
					    System.out.println("Logic gate printing error");
					} 
				}
				
				// treats buffer components as assign statements
				else if (g.type == "buffer") {
					
					try {
						vWriter.write("	assign ");
					}
					catch (IOException e) {
					    System.out.println("Logic gate printing error");
					} 
					
					boolean isFirst = true;
					if (g.getOutputs() != null) {
						for (Pin i : g.getOutputs()) {
							
							String wireName = "error";
							boolean isFree = true;
							Pin fromPin;
							
							if (f.getWires() != null) {
								for (Wire w : f.getWires()) {
									
									if (w.getFrom() instanceof Pin) {
										fromPin = (Pin) w.getFrom();
										if (fromPin == i) {
											isFree = false;
											wireName = nameLookup.get(w);
										}
									};
									

								}
							}
							if (isFree) {
								wireName = nameLookup.get(i);
							}
							try {
									vWriter.write(wireName + " = ");
							} 
							catch (IOException e) {
							    System.out.println("Buffer output printing error");
							} 
						}
					}
					if (g.getInputs() != null) {
						for (Pin i : g.getInputs()) {
							
							String wireName = "error";
							boolean isFree = true;
							if (f.getWires() != null) {
								for (Wire w : f.getWires()) {
									
									Pin toPin;
									
									if (w.getTo() instanceof Pin) {
										toPin = (Pin) w.getTo();

										if (toPin == i) {
											isFree = false;
											wireName = nameLookup.get(w);
										}
									};
									
								}
							}
							if (isFree) {
								wireName = nameLookup.get(i);
							}
							try {
								if (isFirst) {
									isFirst = false;
									vWriter.write(wireName);
								}
								else {
									vWriter.write(wireName);
								}	
							} 
							catch (IOException e) {
							    System.out.println("buffer input printing error");
							} 
						}
					}
					
					try {
						vWriter.write(";");
						vWriter.write(System.lineSeparator());
					}
					catch (IOException e) {
					    System.out.println("buffer printing error");
					} 
				}

				//case for all non-trivial gates
				else {
					String vName = nameLookup.get(g);
					String vModule = g.type;
					
					try {
						vWriter.write("	" + vModule);
						vWriter.write(System.lineSeparator());
						vWriter.write("	  " + vName + " (");
						vWriter.write(System.lineSeparator());
					} 
					catch (IOException e) {
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
									
									Pin toPin;
									
									if (w.getTo() instanceof Pin) {
										toPin = (Pin) w.getTo();

										if (toPin == i) {
											isFree = false;
											wireName = nameLookup.get(w);
											pinLabel = getPinLabel(i, w);
										}
									};
								}
							}
							if (isFree) {
								wireName = nameLookup.get(i);
								pinLabel = getPinLabel(i, null);
							}
							try {
								if (pinLabel.charAt(0) != '_') {
									if (isFirst) {
										isFirst = false;
										vWriter.write("	  ." + pinLabel + "(" + wireName + ")");
									}
									else {
										vWriter.write("," + System.lineSeparator());
										vWriter.write("	  ." + pinLabel + "(" + wireName + ")");
									}
								}
							} 
							catch (IOException e) {
							    System.out.println("Input printing error");
							} 
						}
					}
					if (g.getOutputs() != null) {
						for (Pin i : g.getOutputs()) {
							
							String wireName = "error";
							String pinLabel = "error";
							boolean isFree = true;
							Pin fromPin;
							
							
							if (f.getWires() != null) {
								for (Wire w : f.getWires()) {
									
									if (w.getFrom() instanceof Pin) {
										fromPin = (Pin) w.getFrom();
										
										if (fromPin == i) {
											isFree = false;
											wireName = nameLookup.get(w);
											pinLabel = getPinLabel(i, w);
										}
									};
								}
							}
							if (isFree) {
								wireName = nameLookup.get(i);
								pinLabel = getPinLabel(i, null);
							}
							try {
								if (pinLabel.charAt(0) != '_') {
									if (isFirst) {
										isFirst = false;
										vWriter.write("	  ." + pinLabel + "(" + wireName + ")");
									}
									else {
										vWriter.write("," + System.lineSeparator());
										vWriter.write("	  ." + pinLabel + "(" + wireName + ")");
									}
								}
							} 
							catch (IOException e) {
							    System.out.println("Output printing error");
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

}
