package logicsim.gates;

import java.awt.Color;

import logicsim.Gate;

/**
 * repeater gate
 * 
 * simple gate which just transfers the input to the output.
 * needed for internal gate construction.
 * 
 * @author Peter Gabriel
 * @version 2.0
 */
public class Repeater extends Gate {
	static final long serialVersionUID = -2338870902247206767L;

	public Repeater() {
		super();
		type = "repeater";
		label = "Rep";
		height = 20;
		backgroundColor = Color.LIGHT_GRAY;
		setNumInputs(1);
		setNumOutputs(1);
	}

	@Override
	public void simulate() {
		super.simulate();
		setOutputLevel(0, getInputLevel(0));
	}

	@Override
	public boolean getOutputLevel(int n) {
		if (getInputWire(n) != null)
			return getInputLevel(n);
		else
			return false;
	}

}