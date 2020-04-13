package logicsim;

import java.awt.Point;

public interface CircuitChangedListener {
	public void changedCircuit();

	public void changedStatusText(String text);

	public void changedZoomPos(double zoom, Point pos);

	public void setAction(int action);

	public void needsRepaint(CircuitPart circuitPart);
}
