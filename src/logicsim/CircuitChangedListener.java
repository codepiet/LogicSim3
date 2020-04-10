package logicsim;

public interface CircuitChangedListener {
	public void changedCircuit();

	public void changedStatusText(String text);

	public void changedZoomPos();

	public void setAction(int action);

	public void needsRepaint(CircuitPart circuitPart);
}
