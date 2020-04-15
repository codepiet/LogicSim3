package logicsim;

import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics2D;
import java.io.File;

import javax.swing.JOptionPane;

/**
 * Module implementation
 * 
 * taken from Orginal Module by Andreas Tetzl
 * 
 * @author Peter Gabriel
 *
 */
public class Module extends Gate {
	static final long serialVersionUID = 3938879095465005332L;

	private MODIN moduleIn = null;
	private MODOUT moduleOut = null;
	transient LogicSimFile lsFile = new LogicSimFile(null);

	private boolean embedded = true;

	public Module(String type) {
		super();
		this.type = type;
		loadModule();
	}

	public Module(String type, boolean embedded) {
		super();
		this.type = type;
		this.embedded = embedded;
		loadModule();
	}

	/**
	 * loads module from file
	 */
	public void loadModule() {
		String filename = App.getModulePath() + type + "." + App.MODULE_FILE_SUFFIX;
		File f = new File(filename);
		if (!f.exists()) {
			String s = I18N.tr(Lang.MODULENOTFOUND).replaceFirst("%s", type);
			JOptionPane.showMessageDialog(null, s);
			return;
		}
		try {
			lsFile = XMLLoader.loadXmlFile(filename);
		} catch (RuntimeException x) {
			JOptionPane.showMessageDialog(null, I18N.tr(Lang.READERROR) + ": " + x.getMessage());
			return;
		}

		if (lsFile == null)
			return;
		if (lsFile.circuit == null)
			return;

		label = lsFile.getLabel();

		// in der geladenen GateList nach MODIN und MODOUT Gattern suchen
		for (CircuitPart g : lsFile.circuit.gates) {
			if (g instanceof MODIN) {
				moduleIn = (MODIN) g;
				for (Pin c : moduleIn.getOutputs()) {
					// add MODIN's input-connectors to module:
					// check if MODIN's outputs are connected
					if (c.isConnected()) {
						Pin newIn = new Pin(getX(), getY() + 10 + (c.number - 16) * 10, this, c.number - 16);
						newIn.ioType = Pin.INPUT;
						newIn.levelType = Pin.NORMAL;
						Pin in = moduleIn.getPin(c.number - 16);
						newIn.label = in.label;
						pins.add(newIn);
					}
				}
			}
		}
		for (CircuitPart g : lsFile.circuit.gates) {
			if (g instanceof MODOUT) {
				moduleOut = (MODOUT) g;
				// add MODOUT's output-connectors to module:
				// check if MODOUT's inputs have a wire
				for (Pin c : moduleOut.getInputs()) {
					if (c.isConnected()) {
						Pin newOut = new Pin(getX() + getWidth(), getY() + 10 + c.number * 10, this, c.number + 16);
						newOut.ioType = Pin.OUTPUT;
						newOut.paintDirection = Pin.LEFT;
						newOut.levelType = Pin.NORMAL;
						Pin out = moduleOut.getPin(c.number + 16);
						newOut.label = out.label;
						pins.add(newOut);
					}
				}
			}
			// initialize height and reposition connectors
			int numIn = getNumInputs();
			int numOut = getNumOutputs();
			int max = (numIn > numOut) ? numIn : numOut;
			if (max > 5)
				height = 10 * max * 10;
			for (Pin c : getInputs()) {
				c.setY(getY() + getConnectorPosition(c.number, numIn, Gate.VERTICAL));
			}
			for (Pin c : getOutputs()) {
				c.setY(getY() + getConnectorPosition(c.number - 16, numOut, Gate.VERTICAL));
			}
		}

		if (moduleIn == null || moduleOut == null) {
			JOptionPane.showMessageDialog(null, I18N.tr(Lang.NOMODULE));
			throw new RuntimeException("no module - does not contain both moduleio components: " + type);
		}

		if (embedded) {
			// TODO remove all wires which are connected to MODIN-Inputs
			// and remove all wires which are connected to MODOUT-Outputs
//			for (Pin c : moduleIn.getInputs()) {
//				c.deleteWires();
//			}
//			for (Pin c : moduleOut.getOutputs()) {
//				c.deleteWires();
//			}
		}
	}

	@Override
	public void simulate() {
		super.simulate();

		for (Pin c : getInputs()) {
			moduleIn.getPin(c.number).setLevel(c.getLevel());
		}

		if (lsFile.circuit != null) {
			lsFile.circuit.simulate();
		}

		for (Pin c : getOutputs()) {
			c.setLevel(moduleOut.getPin(c.number).getLevel());
		}
	}

	@Override
	protected void drawLabel(Graphics2D g2, String lbl, Font font) {
		super.drawLabel(g2, type, font);
	}

	@Override
	public boolean hasPropertiesUI() {
		return true;
	}

	@Override
	public boolean showPropertiesUI(Component frame) {
		if (moduleIn != null) {
			String content = I18N.tr(Lang.DESCRIPTION) + ":\n";
			content += lsFile.getDescription();
			JOptionPane.showMessageDialog(frame, content);
		}
		return true;
	}

}